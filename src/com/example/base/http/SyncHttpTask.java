package com.example.base.http;

import java.io.IOException;
import java.net.UnknownHostException;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.protocol.HttpContext;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import com.example.base.exception.HttpException;
import com.example.base.utils.HttpUtils;

/**
 * 同步请求
 * @author lim
 * @TODO 在一个线程中连续请求多个接口时使用
 */
public class SyncHttpTask {

    private final AbstractHttpClient client;
    private final HttpContext context;
    
    private HttpRedirectHandler httpRedirectHandler;//重试请求handler

    private String charset; // The default charset of response header info.
    private String requestUrl;
    private int retriedTimes = 0;//重试次数
    private long expiry = AppHttpGetCache.getDefaultExpiryTime();
    
    public SyncHttpTask(AbstractHttpClient client, HttpContext context, String charset) {
        this.client = client;
        this.context = context;
        this.charset = charset;
    }

    /**
     * 设置过期时间
     * @param expiry
     */
    public void setExpiry(long expiry) {
        this.expiry = expiry;
    }

    public void setHttpRedirectHandler(HttpRedirectHandler httpRedirectHandler) {
        this.httpRedirectHandler = httpRedirectHandler;
    }
    
    @SuppressLint("NewApi")
	public ResponseStream sendRequest(HttpRequestBase request) throws HttpException {

        boolean retry = true;
        HttpRequestRetryHandler retryHandler = client.getHttpRequestRetryHandler();
        while (retry) {
            IOException exception = null;
            try {
                requestUrl = request.getURI().toString();
                if (request.getMethod().equals(AppHttpRequest.HttpMethod.GET.toString())) {
                    String result = AppHttpClient.mHttpGetCache.get(requestUrl);
                    if (result != null) {
                        return new ResponseStream(result);
                    }
                }

                HttpResponse response = client.execute(request, context);
                return handleResponse(response);
            } catch (UnknownHostException e) {
                exception = e;
                retry = retryHandler.retryRequest(exception, ++retriedTimes, context);
            } catch (IOException e) {
                exception = e;
                retry = retryHandler.retryRequest(exception, ++retriedTimes, context);
            } catch (NullPointerException e) {
                exception = new IOException(e.getMessage());
                exception.initCause(e);
                retry = retryHandler.retryRequest(exception, ++retriedTimes, context);
            } catch (Throwable e) {
                exception = new IOException(e.getMessage());
                exception.initCause(e);
                retry = retryHandler.retryRequest(exception, ++retriedTimes, context);
            }
            if (!retry && exception != null) {
                throw new HttpException();
            }
        }
        return null;
    }

    /**
     * 处理结果
     * @param response
     * @return
     * @throws HttpException
     * @throws IOException
     */
    private ResponseStream handleResponse(HttpResponse response) throws HttpException, IOException {
        if (response == null) {
            throw new IOException("response is null");
        }
        StatusLine status = response.getStatusLine();
        int statusCode = status.getStatusCode();
        if (statusCode < 300) {

            // Set charset from response header if it's exist.
            String responseCharset = HttpUtils.getCharsetFromHttpResponse(response);
            charset = TextUtils.isEmpty(responseCharset) ? charset : responseCharset;

            return new ResponseStream(response, charset, requestUrl, expiry);
        } else if (statusCode == 301 || statusCode == 302) {
            if (httpRedirectHandler == null) {
                httpRedirectHandler = new DefaultHttpRedirectHandler();
            }
            HttpRequestBase request = httpRedirectHandler.getDirectRequest(response);
            if (request != null) {
                return this.sendRequest(request);
            }
        } else if (statusCode == 416) {
            throw new IOException("maybe the file has downloaded completely");
        } else {
            throw new IOException(status.getReasonPhrase());
        }
        return null;
    }
}
