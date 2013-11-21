package com.example.base.http;

import org.apache.http.*;

import java.util.Locale;

/**
 * 返回结果
 * @author lim
 *
 * @param <T>
 */
public final class ResponseInfo<T> {
	
    public final Header[] allHeaders;
    public final Locale locale;

    // status line
    public final int statusCode;
    public final ProtocolVersion protocolVersion;
    public final String reasonPhrase;

    // entity
    public final long contentLength;
    public final Header contentType;
    public final Header contentEncoding;

    public final boolean resultFormCache;
    public T result;
    
    public ResponseInfo(final HttpResponse response, T result, boolean resultFormCache) {
        this.result = result;
        this.resultFormCache = resultFormCache;

        if (response != null) {
            allHeaders = response.getAllHeaders();
            locale = response.getLocale();

            // status line
            StatusLine statusLine = response.getStatusLine();
            if (statusLine != null) {
                statusCode = statusLine.getStatusCode();
                protocolVersion = statusLine.getProtocolVersion();
                reasonPhrase = statusLine.getReasonPhrase();
            } else {
                statusCode = 0;
                protocolVersion = null;
                reasonPhrase = null;
            }

            // entity
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                contentLength = entity.getContentLength();
                contentType = entity.getContentType();
                contentEncoding = entity.getContentEncoding();
            } else {
                contentLength = 0;
                contentType = null;
                contentEncoding = null;
            }
        } else {
            allHeaders = null;
            locale = null;

            // status line
            statusCode = 0;
            protocolVersion = null;
            reasonPhrase = null;

            // entity
            contentLength = 0;
            contentType = null;
            contentEncoding = null;
        }
    }
}
