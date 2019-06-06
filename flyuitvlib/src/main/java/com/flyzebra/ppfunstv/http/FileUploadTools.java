package com.flyzebra.ppfunstv.http;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Map;

public class FileUploadTools {
    /**
     * 函数功能：发送HTML表单,例如下面表单提交功能:
     * <FORM METHOD=POST ACTION="http://www.flyzebr.com:8181/upfile.jsb" enctype="multipart/form-data">	 			*
     * <br>	<INPUT TYPE="text" NAME="name"><br>
     * <INPUT TYPE="text" NAME="id"><br>
     * <INPUT TYPE="file" name="file0"/><br>
     * <INPUT TYPE="file" name="file1"/><br>
     * </FORM>
     *
     * @param url_path http://www.flyzebr.com:8181/upfile.jsb
     */
    public static boolean SendForm(String url_path, Map<String, String> value, File[] ArrayFile) {
        boolean flag = false;
        final String BOUNDARY = "---------------------------7df34b3b1d066a";
        final String endline = "--" + BOUNDARY + "--\r\n";
        int fileDataLength = 0;
        //计算各文件数据总长度
        for (int i = 0; i < ArrayFile.length; i++) {
            String MimeType = FileMimeTools.getMIMEType(ArrayFile[i]);
            String formName = "file" + i;
            StringBuffer fileExplain = new StringBuffer();
            fileExplain.append("--");
            fileExplain.append(BOUNDARY);
            fileExplain.append("\r\n");
            fileExplain.append("Content-Disposition: form-data;name=\""
                    + formName + "\";" + "filename=\""
                    + ArrayFile[i].getName() + "\"\r\n");
            fileExplain.append("Content-Type: " + MimeType + "\r\n\r\n");
            fileExplain.append("\r\n");
            fileDataLength += fileExplain.toString().getBytes().length;
            fileDataLength += ArrayFile[i].length();
        }
        // 构造文本类型参数头，计算长度
        StringBuffer textEntity = new StringBuffer();
        for (Map.Entry<String, String> entry : value.entrySet()) {
            textEntity.append("--");
            textEntity.append(BOUNDARY);
            textEntity.append("\r\n");
            textEntity.append("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"\r\n\r\n");
            textEntity.append(entry.getValue());
            textEntity.append("\r\n");
        }
        // 计算传输给服务器的实体数据总长度(文本总长度+数据总长度+分隔符)
        int dataLength = textEntity.toString().getBytes().length
                + fileDataLength + endline.getBytes().length;

        OutputStream out = null;
        Socket socket = null;
        BufferedReader reader = null;
        try {
            URL url = new URL(url_path);
            int port = url.getPort() == -1 ? 80 : url.getPort();// 默认端口号其实可以不写
            socket = new Socket(InetAddress.getByName(url.getHost()), port);
            out = socket.getOutputStream();
            //构造HTTP文件头并发送
            StringBuffer httpHeaders = new StringBuffer();
            httpHeaders.append("POST " + url.getPath() + " HTTP/1.1\r\n");
            httpHeaders.append("Accept: text/html, application/xhtml+xml, */*\r\n");
            httpHeaders.append("Accept-Encoding: gzip, deflate\r\n");
            httpHeaders.append("Accept-Language: zh-CN\r\n");
            httpHeaders.append("Cache-Control: no-cache\r\n");
            httpHeaders.append("Connection: Keep-Alive\r\n");
            httpHeaders.append("Content-Length: " + dataLength + "\r\n");
            httpHeaders.append("Content-Type: multipart/form-data; boundary=" + BOUNDARY + "\r\n");
            httpHeaders.append("Host: " + url.getHost() + ":" + port + "\r\n");
            httpHeaders.append("Referer:" + url_path + "\r\n\r\n");
            out.write(httpHeaders.toString().getBytes());
            // 发送文件
            for (int i = 0; i < ArrayFile.length; i++) {
                String MimeType = FileMimeTools.getMIMEType(ArrayFile[i]);
                String formName = "file" + i;
                StringBuffer fileInfo = new StringBuffer();
                fileInfo.append("--");
                fileInfo.append(BOUNDARY);
                fileInfo.append("\r\n");
                fileInfo.append("Content-Disposition: form-data;name=\""
                        + formName + "\";" + "filename=\""
                        + ArrayFile[i].getName() + "\"\r\n");
                fileInfo.append("Content-Type: " + MimeType + "\r\n\r\n");
                out.write(fileInfo.toString().getBytes());

//				Log.i(G.TAG,fileEntity.toString());
                // 边读边写
                InputStream ins = new FileInputStream(ArrayFile[i]);
                byte[] buffer = new byte[1024];
                int len = 0;
                while ((len = ins.read(buffer, 0, 1024)) != -1) {
                    out.write(buffer, 0, len);
                }
                ins.close();
                out.write("\r\n".getBytes());
            }
            //发送文本信息
            out.write(textEntity.toString().getBytes());
            // 发送数据结束标志
            out.write(endline.getBytes());
            //读取返回信息
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            // 读取web服务器返回的数据，判断请求码是否为200，如果不是200，代表请求失败
            String ret = reader.readLine();
            if (ret.indexOf("200") != -1) {
                flag = true;
            }
            out.flush();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return flag;
    }

}
