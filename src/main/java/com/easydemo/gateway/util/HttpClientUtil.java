package com.easydemo.gateway.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.mortbay.util.UrlEncoded;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;

/**
 * @Author: mitsui
 * @Date: 2021/6/29
 * @Description:
 */
public class HttpClientUtil {
    private static final Logger log = LoggerFactory.getLogger(HttpClientUtil.class);

    public static String sendGet(String url_, Object object) {
        StringBuffer sb = new StringBuffer();
        HttpURLConnection conn = null;
        try {
            if (!url_.endsWith("?")) {
                url_ += "?";
            }
            url_ += parseParams(object);
            // 创建url资源
            URL url = new URL(url_);
            // 建立http连接
            conn = (HttpURLConnection)url.openConnection();
            // 设置允许输出
            conn.setDoOutput(false);
            // 设置允许输入
            conn.setDoInput(true);
            // 设置不用缓存
            conn.setUseCaches(false);
            // 设置传递方式
            conn.setRequestMethod("GET");
            // 设置维持长连接
            conn.setRequestProperty("Connection", "Keep-Alive");
            // 设置文件字符集:
            conn.setRequestProperty("Charset", "UTF-8");
            // 设置文件类型:
            conn.setRequestProperty("contentType", "application/json");
            conn.setRequestProperty("Content-Type", "application/json;");
            // 设置超时时间 设置10s —>由于大数据查询较慢 设置为30s
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(30000);
            // 开始连接请求
            conn.connect();
            // 请求返回的状态
            if (HttpURLConnection.HTTP_OK == conn.getResponseCode()) {
                // 请求返回的数据
                InputStream in1 = conn.getInputStream();
                try {
                    String readLine;
                    BufferedReader responseReader = new BufferedReader(new InputStreamReader(in1, "UTF-8"));
                    while ((readLine = responseReader.readLine()) != null) {
                        sb.append(readLine).append("\n");
                    }
                    responseReader.close();
                    log.debug(sb.toString());
                } catch (Exception e1) {
                    log.error(e1.getMessage());
                }
            } else {
                log.error("http请求失败，请求路径为：{},状态码为：{},错误信息为:{}",url,conn.getResponseCode(),conn.getResponseMessage());
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return sb.toString();
    }

    public static String parseParams(Object object) {
        String s = JSON.toJSONString(object);
        JSONObject jsonObject = JSON.parseObject(s);
        StringBuffer buffer = new StringBuffer();
        if (jsonObject != null && !jsonObject.isEmpty()) {
            for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
                if (entry.getValue() == null) {
                    continue;
                }
                buffer.append(entry.getKey());
                buffer.append("=");
                buffer.append(UrlEncoded.encodeString(entry.getValue().toString(),"UTF-8"));
                buffer.append("&");
            }
        }
        return buffer.toString();
    }

    public static String sendGet(String url_) {
        return sendGet(url_, null);
    }

    public static String sendGet(String url_, Map<String, String> headerParams) {
        log.debug("HttpUtil sendGet URL:" + url_);
        StringBuffer sb = new StringBuffer();
        HttpURLConnection conn = null;
        try {
            // 创建url资源
            URL url = new URL(url_);
            // 建立http连接
            conn = (HttpURLConnection)url.openConnection();
            // 设置允许输出
            conn.setDoOutput(false);
            // 设置允许输入
            conn.setDoInput(true);
            // 设置不用缓存
            conn.setUseCaches(false);
            // 设置传递方式
            conn.setRequestMethod("GET");
            // 设置维持长连接
            conn.setRequestProperty("Connection", "Keep-Alive");
            // 设置文件字符集:
            conn.setRequestProperty("Charset", "UTF-8");
            // 设置文件类型:
            conn.setRequestProperty("contentType", "application/json");
            conn.setRequestProperty("Content-Type", "application/json;");
            // 头部参数
            if (headerParams != null && headerParams.size() > 0) {
                Iterator<Map.Entry<String, String>> iterator = headerParams.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, String> node = iterator.next();
                    conn.setRequestProperty(node.getKey(), node.getValue());
                }
            }
            // 开始连接请求
            conn.connect();
            // 请求返回的状态
            if (HttpURLConnection.HTTP_OK == conn.getResponseCode()) {
                // 请求返回的数据
                InputStream in1 = conn.getInputStream();
                try {
                    String readLine = new String();
                    BufferedReader responseReader = new BufferedReader(new InputStreamReader(in1, "UTF-8"));
                    while ((readLine = responseReader.readLine()) != null) {
                        sb.append(readLine).append("\n");
                    }
                    responseReader.close();
                    log.debug(sb.toString());
                } catch (Exception e1) {
                    log.error(e1.getMessage());
                }
            } else {
                log.error("http请求失败，请求路径为：{},状态码为：{},错误信息为:{}",url,conn.getResponseCode(),conn.getResponseMessage());
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return sb.toString();
    }

    public static String sendPost(String url_, Object object) {
        if (!(object instanceof JSON)) {
            object = JSON.toJSON(object);
        }
        StringBuffer sb = new StringBuffer();
        HttpURLConnection conn = null;
        try {
            // 创建url资源
            URL url = new URL(url_);
            // 建立http连接
             conn = (HttpURLConnection)url.openConnection();
            // 设置允许输出
            conn.setDoOutput(true);
            // 设置允许输入
            conn.setDoInput(true);
            // 设置不用缓存
            conn.setUseCaches(false);
            // 设置传递方式
            conn.setRequestMethod("POST");
            // 设置维持长连接
            conn.setRequestProperty("Connection", "Keep-Alive");
            // 设置文件字符集:
            conn.setRequestProperty("Charset", "UTF-8");
            // 转换为字节数组
            byte[] data = (object.toString()).getBytes();
            // 设置文件长度
            conn.setRequestProperty("Content-Length", String.valueOf(data.length));
            // 设置文件类型:
            conn.setRequestProperty("contentType", "application/json");
            conn.setRequestProperty("Content-Type", "application/json;");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            // 开始连接请求
            conn.connect();
            OutputStream out = new DataOutputStream(conn.getOutputStream());
            // 写入请求的字符串
            out.write((object.toString()).getBytes());
            out.flush();
            out.close();
            // 请求返回的状态
            if (HttpURLConnection.HTTP_OK == conn.getResponseCode()) {
                // 请求返回的数据
                InputStream in1 = conn.getInputStream();
                try {
                    String readLine = new String();
                    BufferedReader responseReader = new BufferedReader(new InputStreamReader(in1, "UTF-8"));
                    while ((readLine = responseReader.readLine()) != null) {
                        sb.append(readLine).append("\n");
                    }
                    responseReader.close();
                    log.debug("HttpUtil sendGet URL:" + sb.toString());
                } catch (Exception e1) {
                    log.error(e1.getMessage());
                }
            } else {
                log.error("error++,{}",conn.getResponseMessage());
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return sb.toString();
    }
}
