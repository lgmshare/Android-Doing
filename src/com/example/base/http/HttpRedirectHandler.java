package com.example.base.http;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpRequestBase;

/**
 * 重定向handler
 * 
 * @author lim
 *
 */
public interface HttpRedirectHandler {
    HttpRequestBase getDirectRequest(HttpResponse response);
}
