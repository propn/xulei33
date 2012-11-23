package com.propn.golf.mvc;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Test;

public class ReqTest {

    @Test
    public void getT() {

    }

    @Test
    public void postFromT() {

    }

    @Test
    public void postMultipartT() throws Exception {
        // 发起post请求
        String urlString = "http://localhost:8080/golf";
        String filePath = "D:\\company\\voice\\十渡.wav";
        URL connectURL = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) connectURL.openConnection();
        conn.setReadTimeout(100000);
        conn.setConnectTimeout(100000);
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setUseCaches(false);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Connection", "Keep-Alive");

        conn.setRequestProperty("Content-Type", "multipart/form-data;");
        DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
        StringBuffer sb = new StringBuffer("Content-Disposition: form-data;");
        sb.append("&loginName=test&pwd=test1");
        dos.writeBytes(sb.toString());

        FileInputStream fileInputStream = new FileInputStream(filePath);
        int bytesAvailable = fileInputStream.available();
        int maxBufferSize = 1024;
        int bufferSize = Math.min(bytesAvailable, maxBufferSize);
        byte[] buffer = new byte[bufferSize];

        // read file and write it into form...
        int bytesRead = fileInputStream.read(buffer, 0, bufferSize);

        while (bytesRead > 0) {
            dos.write(buffer, 0, bufferSize);
            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
        }

        dos.flush();
        dos.close();

        // 接收发起请求后由服务端返回的结果
        int read;
        StringBuffer inputb = new StringBuffer();
        InputStream is = conn.getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(is, "UTF-8");
        while ((read = inputStreamReader.read()) >= 0) {
            inputb.append((char) read);
        }
        System.out.println(inputb.toString());
    }

}
