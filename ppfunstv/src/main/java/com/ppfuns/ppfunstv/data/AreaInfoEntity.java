package com.ppfuns.ppfunstv.data;

/**
 * Created by 李宗源 on 2016/8/1.
 * E-mail:lizy@ppfuns.com
 * 根据IP地址获取区域信息
 */
public class AreaInfoEntity {

    /**
     * errNum : 0
     * errMsg : success
     * retData : {"ip":"117.89.35.58","country":"中国","province":"江苏","city":"南京","district":"鼓楼","carrier":"中国电信"}
     */

    private int errNum;
    private String errMsg;
    /**
     * ip : 117.89.35.58
     * country : 中国
     * province : 江苏
     * city : 南京
     * district : 鼓楼
     * carrier : 中国电信
     */

    private RetDataEntity retData;

    public int getErrNum() {
        return errNum;
    }

    public void setErrNum(int errNum) {
        this.errNum = errNum;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public RetDataEntity getRetData() {
        return retData;
    }

    public void setRetData(RetDataEntity retData) {
        this.retData = retData;
    }

    public static class RetDataEntity {
        private String ip;
        private String country;
        private String province;
        private String city;
        private String district;
        private String carrier;

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getProvince() {
            return province;
        }

        public void setProvince(String province) {
            this.province = province;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getDistrict() {
            return district;
        }

        public void setDistrict(String district) {
            this.district = district;
        }

        public String getCarrier() {
            return carrier;
        }

        public void setCarrier(String carrier) {
            this.carrier = carrier;
        }
    }
}
