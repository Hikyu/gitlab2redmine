/**
 * @Title: HttpUtils.java
 * @Prject: gitlabEventCenter
 * @Package: com.oscar.gitlabEventCenter.utils
 * @author: Yukai  
 * @date: 2018年5月15日 下午4:00:48
 */
package com.oscar.gitlabEventCenter.common.utils;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import com.oscar.gitlabEventCenter.common.exception.HttpRequestFailed;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @ClassName: HttpUtils
 * @Description: TODO
 * @author Yukai
 * @data 2018年5月15日 下午4:00:48
 */
public class HttpUtils {
    private static OkHttpClient client;
    static {
        client = new OkHttpClient();
    }

    public static String getFromHttpHeader(HttpServletRequest request, String headerName) {
        return request.getHeader(headerName);
    }

    public static String getReqeustHost(HttpServletRequest request) {
        return request.getRemoteHost();
    }
    
    public static Response sendRequestSync(Request request) {
        Response response = null;
        try {
            response = client.newCall(request).execute();
            return response;
        } catch (IOException e) {
            throw new HttpRequestFailed(String.format("Http request to %s failed", request.url().toString()), e);
        }
    }
    
    public static String getResponseBody(Response response) throws IOException {
        String res = response.body().string();
        if (!response.isSuccessful()) {
            Request request = response.request();
            throw new HttpRequestFailed(String.format("Http request to %s failed, code: %s, response: body %s",
                    request.url().toString(), response.code(), res));
        }
        return res;
    }
    
}
