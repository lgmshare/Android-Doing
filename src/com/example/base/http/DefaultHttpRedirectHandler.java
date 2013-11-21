package com.example.base.http;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;

/**
 * 默认重定向handler
 * 
 * @author lim
 *
 */
public class DefaultHttpRedirectHandler implements HttpRedirectHandler {
	
    @Override
    public HttpRequestBase getDirectRequest(HttpResponse response) {
        if (response.containsHeader("Location")) {
            String location = response.getFirstHeader("Location").getValue();
            HttpGet request = new HttpGet(location);
            if (response.containsHeader("Set-Cookie")) {
                String cookie = response.getFirstHeader("Set-Cookie").getValue();
                request.addHeader("Cookie", cookie);
            }
            return request;
        }
        return null;
    }
}
