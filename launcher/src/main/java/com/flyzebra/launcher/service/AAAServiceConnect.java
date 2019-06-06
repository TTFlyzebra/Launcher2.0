package com.flyzebra.launcher.service;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.widget.Toast;

import com.flyzebra.aaaservice.service.IAAAInterface;
import com.flyzebra.aaaservice.service.IPropertyListener;
import com.flyzebra.launcher.constant.Constants;
import com.flyzebra.launcher.utils.FlyLog;
import com.flyzebra.launcher.utils.ServiceUtils;

/**
 * 鉴权服务封装
 * Created by FlyZebra on 2016/9/1.
 */
public class AAAServiceConnect {
    private Activity activity;
    private IAAAInterface iAAAServiceConn;
    private ServiceConnection AAAServiceConn;
    private static AAAServiceConnect mInstance;
    private static AAAClient mAAAClient;
    private static Object lock = new Object();

    public AAAServiceConnect(final Activity context) {
        activity = context;
        AAAServiceConn = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                iAAAServiceConn = IAAAInterface.Stub.asInterface(service);

                mAAAClient = new AAAClient();
                try {
                    service.linkToDeath(mAAAClient, 0);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                int check = getDeviceCheck();
                switch (check) {
                    /**设备鉴权,
                     * DEVICE_UNAUTHORIZED = 0x01;         //设备未鉴权
                     * DEVICE_ILLEGAL = 0x02;              //非法设备
                     * USER_UNAVAILABLE = 0x03;            //用户不可用
                     * USER_AVAILABLE = 0x04;              //用户可用
                     * USER_ARREARS = 0x05;                //存在欠费的业务，但是用户可用
                     */
                    case 0x0001:
                        //无需提示用户
                        //Toast.makeText(activity, "设备未认证", Toast.LENGTH_SHORT).show();
                        break;
                    case 0x0002:
                        //测试不弹
                        Toast.makeText(activity, "未认证设备", Toast.LENGTH_SHORT).show();
                        break;
                    case 0x0003:
                        Toast.makeText(activity, "设备认证失败", Toast.LENGTH_SHORT).show();
                        break;
                    case 0x0004:
                        //无需提示用户
                        //Toast.makeText(activity, "设备认证通过", Toast.LENGTH_SHORT).show();
                        break;
                    case 0x0005:
                        Toast.makeText(activity, "存在欠费的业务", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        try {
                            Toast.makeText(activity, "未知错误(错误码：" + String.format("%02x", check) + ")", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                }

            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                ServiceUtils.bindServiceCls(activity, Constants.AAA_PACKAGE_NAME, Constants.AAA_SERVICE, AAAServiceConn);
//                service.linkToDeath(mAAAClient, 0);
            }
        };
        ServiceUtils.bindServiceCls(activity, Constants.AAA_PACKAGE_NAME, Constants.AAA_SERVICE, AAAServiceConn);
        FlyLog.d();
    }

    /**
     * 获取设备鉴权状态
     *
     * @return 设备未激活： 0x0000--CA卡未插入，
     * 0x0001网络连接失败
     * 0x0002非法设备，
     * 设备激活中  0x0100，
     * 设备激活成功 0x0200
     */
    public int getDeviceCheck() {
        int temp = -1;
        try {
            temp = iAAAServiceConn.getAuthStatus();
        } catch (RemoteException e) {
            e.printStackTrace();
            FlyLog.d(e.toString());
        }
        return temp;
    }

    /**
     * 获取业务鉴权状态
     *
     * @return
     */
    public boolean getApplyCheck(String string) {
        boolean temp = true;//默认不通过授权
        try {
            if (iAAAServiceConn != null) {
                temp = iAAAServiceConn.isAllowed(string);//参数为业务的包名
                FlyLog.d(string + " pass aaa is " + temp);
            } else {
                FlyLog.d("no bind aaaservice!");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            FlyLog.d(e.toString());
        }
        return temp;
    }

    public void unbindService(Context context) {
        context.unbindService(AAAServiceConn);
    }

    public void registerPropertyListener(String pProp, final PropertyListener pListener) {

        if (iAAAServiceConn != null) {
            try {
                iAAAServiceConn.registerPropertyListener(pProp, new IPropertyListener.Stub() {

                    @Override
                    public void onPropertyChanged(String pNew, String pOld) throws RemoteException {
                        if (pListener != null) {
                            pListener.onPropertyChanged(pNew, pOld);
                        }
                    }
                });
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

    }

    public String query(String pKey) {
        if (iAAAServiceConn != null) {
            try {
                return iAAAServiceConn.query(pKey);
            } catch (RemoteException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }


    /*
    * 取消属性监听
    */
    public void unregisterPropertyListener(String pProp) {
        if (iAAAServiceConn != null) {
            try {
                iAAAServiceConn.unregisterPropertyListener(pProp);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }


    public interface PropertyListener {
        void onPropertyChanged(String pNew, String pOld);
    }


    public static class TokenBean {
        public String result;
        public int code;
    }


    private final class AAAClient implements IBinder.DeathRecipient {

        @Override
        public void binderDied() {
            FlyLog.e("AAAServiceConnect is died ,try to re bind");
            ServiceUtils.bindServiceCls(activity, Constants.AAA_PACKAGE_NAME, Constants.AAA_SERVICE, AAAServiceConn);
        }
    }


}
