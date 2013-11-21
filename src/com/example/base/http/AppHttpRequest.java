package com.example.base.http;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.CloneUtils;
import org.apache.http.protocol.HTTP;

import android.util.Log;

public class AppHttpRequest extends HttpRequestBase implements HttpEntityEnclosingRequest {

    private HttpEntity entity;
    private HttpMethod method;
    private URIBuilder uriBuilder;

    public AppHttpRequest(HttpMethod method) {
        super();
        this.method = method;
    }

    public AppHttpRequest(HttpMethod method, String uri) {
        super();
        this.method = method;
        setURI(URI.create(uri));
    }

    public AppHttpRequest(HttpMethod method, URI uri) {
        super();
        this.method = method;
        setURI(uri);
    }

    public AppHttpRequest addQueryStringParameter(String name, String value) {
        uriBuilder.addParameter(name, value);
        return this;
    }

    public AppHttpRequest addQueryStringParameter(NameValuePair nameValuePair) {
        uriBuilder.addParameter(nameValuePair.getName(), nameValuePair.getValue());
        return this;
    }

    public AppHttpRequest addQueryStringParams(List<NameValuePair> nameValuePairs) {
        if (nameValuePairs != null) {
            for (NameValuePair nameValuePair : nameValuePairs) {
                uriBuilder.addParameter(nameValuePair.getName(), nameValuePair.getValue());
            }
        }
        return this;
    }

    public void setRequestParams(RequestParams param) {
        if (param != null) {
            List<RequestParams.HeaderItem> headerItems = param.getHeaders();
            if (headerItems != null) {
                for (RequestParams.HeaderItem headerItem : headerItems) {
                    if (headerItem.overwrite) {
                        this.setHeader(headerItem.header);
                    } else {
                        this.addHeader(headerItem.header);
                    }
                }
            }
            
            //请求参数
            if (method.toString().equals(AppHttpRequest.HttpMethod.GET.toString())) {
            	this.addQueryStringParams(param.getParams());
			}else{
				this.setEntity(param.getEntity());
			}
        }
    }

    public void setRequestParams(RequestParams param, RequestCallBackHandler callBackHandler) {
        if (param != null) {
            List<RequestParams.HeaderItem> headerItems = param.getHeaders();
            if (headerItems != null) {
                for (RequestParams.HeaderItem headerItem : headerItems) {
                    if (headerItem.overwrite) {
                        this.setHeader(headerItem.header);
                    } else {
                        this.addHeader(headerItem.header);
                    }
                }
            }
            //请求参数
            if (method.toString().equals(AppHttpRequest.HttpMethod.GET.toString())) {
            	this.addQueryStringParams(param.getParams());
            }else{
            	HttpEntity entity = param.getEntity();
            	if (entity != null) {
            		if (entity instanceof UploadEntity) {
            			((UploadEntity) entity).setCallBackHandler(callBackHandler);
            		}
            		this.setEntity(entity);
            	}
            }
        }
    }

    @Override
    public URI getURI() {
        try {
            return uriBuilder.build();
        } catch (URISyntaxException e) {
        	e.printStackTrace();
            Log.i("uriBuilder",e.getMessage());
            return null;
        }
    }

    @Override
    public void setURI(URI uri) {
        this.uriBuilder = new URIBuilder(uri);
    }

    @Override
    public String getMethod() {
        return this.method.toString();
    }

    @Override
    public HttpEntity getEntity() {
        return this.entity;
    }

    @Override
    public void setEntity(final HttpEntity entity) {
        this.entity = entity;
    }

    @Override
    public boolean expectContinue() {
        Header expect = getFirstHeader(HTTP.EXPECT_DIRECTIVE);
        return expect != null && HTTP.EXPECT_CONTINUE.equalsIgnoreCase(expect.getValue());
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        AppHttpRequest clone = (AppHttpRequest) super.clone();
        if (this.entity != null) {
            clone.entity = (HttpEntity) CloneUtils.clone(this.entity);
        }
        return clone;
    }

    public static enum HttpMethod {
        GET("GET"), POST("POST"), PUT("PUT"), HEAD("HEAD"), MOVE("MOVE"), COPY("COPY"), DELETE("DELETE");

        private final String value;

        HttpMethod(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return this.value;
        }
    }
}
