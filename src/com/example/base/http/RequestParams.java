/*
 * Copyright (c) 2013. lim (lim@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.base.http;

import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;


/**
 * 请求参数
 * @author lim
 *
 */
public class RequestParams {

    private String charset = HTTP.UTF_8;

    private List<HeaderItem> headers;
    private HttpEntity bodyEntity;
    private LinkedHashMap<String, NameValuePair> params;
    private HashMap<String, ContentBody> fileParams;

    public RequestParams() {
    }

    public RequestParams(String charset) {
        this.charset = charset;
    }

    /**
     * Adds a header to this message. The header will be appended to the end of the list.
     *
     * @param header
     */
    public void addHeader(Header header) {
        if (this.headers == null) {
            this.headers = new ArrayList<HeaderItem>();
        }
        this.headers.add(new HeaderItem(header));
    }

    /**
     * Adds a header to this message. The header will be appended to the end of the list.
     *
     * @param name
     * @param value
     */
    public void addHeader(String name, String value) {
        if (this.headers == null) {
            this.headers = new ArrayList<HeaderItem>();
        }
        this.headers.add(new HeaderItem(name, value));
    }

    /**
     * Adds all the headers to this message.
     *
     * @param headers
     */
    public void addHeaders(List<Header> headers) {
        if (this.headers == null) {
            this.headers = new ArrayList<HeaderItem>();
        }
        for (Header header : headers) {
            this.headers.add(new HeaderItem(header));
        }
    }

    /**
     * Overwrites the first header with the same name.
     * The new header will be appended to the end of the list, if no header with the given name can be found.
     *
     * @param header
     */
    public void setHeader(Header header) {
        if (this.headers == null) {
            this.headers = new ArrayList<HeaderItem>();
        }
        this.headers.add(new HeaderItem(header, true));
    }

    /**
     * Overwrites the first header with the same name.
     * The new header will be appended to the end of the list, if no header with the given name can be found.
     *
     * @param name
     * @param value
     */
    public void setHeader(String name, String value) {
        if (this.headers == null) {
            this.headers = new ArrayList<HeaderItem>();
        }
        this.headers.add(new HeaderItem(name, value, true));
    }

    /**
     * Overwrites all the headers in the message.
     *
     * @param headers
     */
    public void setHeaders(List<Header> headers) {
        if (this.headers == null) {
            this.headers = new ArrayList<HeaderItem>();
        }
        for (Header header : headers) {
            this.headers.add(new HeaderItem(header, true));
        }
    }

    public void put(String name, String value) {
        if (params == null) {
            params = new LinkedHashMap<String, NameValuePair>();
        }
        params.put(name, new BasicNameValuePair(name, value));
    }

    public void put(NameValuePair nameValuePair) {
        if (params == null) {
            params = new LinkedHashMap<String, NameValuePair>();
        }
        params.put(nameValuePair.getName(), nameValuePair);
    }

    public void put(List<NameValuePair> nameValuePairs) {
        if (params == null) {
            params = new LinkedHashMap<String, NameValuePair>();
        }
        if (nameValuePairs != null && nameValuePairs.size() > 0) {
            for (NameValuePair pair : nameValuePairs) {
                params.put(pair.getName(), pair);
            }
        }
    }

    public void put(String key, File file) {
        if (fileParams == null) {
            fileParams = new HashMap<String, ContentBody>();
        }
        fileParams.put(key, new FileBody(file));
    }

    public void put(String key, File file, String mimeType) {
        if (fileParams == null) {
            fileParams = new HashMap<String, ContentBody>();
        }
        fileParams.put(key, new FileBody(file, mimeType));
    }

    public void put(String key, File file, String mimeType, String charset) {
        if (fileParams == null) {
            fileParams = new HashMap<String, ContentBody>();
        }
        fileParams.put(key, new FileBody(file, mimeType, charset));
    }

    public void put(String key, InputStream stream, long length, String fileName) {
        if (fileParams == null) {
            fileParams = new HashMap<String, ContentBody>();
        }
        fileParams.put(key, new InputStreamBody(stream, length, fileName));
    }

    public void put(String key, InputStream stream, long length, String mimeType, String fileName) {
        if (fileParams == null) {
            fileParams = new HashMap<String, ContentBody>();
        }
        fileParams.put(key, new InputStreamBody(stream, length, mimeType, fileName));
    }

    public void setEntity(HttpEntity bodyEntity) {
        this.bodyEntity = bodyEntity;
        if (params != null) {
            params.clear();
            params = null;
        }
        if (fileParams != null) {
            fileParams.clear();
            fileParams = null;
        }
    }

    /**
     * Returns an HttpEntity containing all request parameters
     */
    public HttpEntity getEntity() {
        if (bodyEntity != null) {
            return bodyEntity;
        }
        HttpEntity result = null;
        if (fileParams != null && !fileParams.isEmpty()) {
            MultipartEntity multipartEntity = new MultipartEntity(HttpMultipartMode.STRICT, null, Charset.forName(charset));
            if (params != null && !params.isEmpty()) {
                for (NameValuePair param : params.values()) {
                    try {
                        multipartEntity.addPart(param.getName(), new StringBody(param.getValue()));
                    } catch (UnsupportedEncodingException e) {
                    	e.printStackTrace();
                    }
                }
            }

            for (ConcurrentHashMap.Entry<String, ContentBody> entry : fileParams.entrySet()) {
                multipartEntity.addPart(entry.getKey(), entry.getValue());
            }

            result = multipartEntity;
        } else if (params != null && !params.isEmpty()) {
            result = new BodyParamsEntity(new ArrayList<NameValuePair>(params.values()), charset);
        }

        return result;
    }

    /**
     * 获取请求参数
     * @return
     */
    public List<NameValuePair> getParams() {
        if (params != null && !params.isEmpty()) {
            return new ArrayList<NameValuePair>(params.values());
        }
        return null;
    }

    public List<HeaderItem> getHeaders() {
        return headers;
    }

    public class HeaderItem {
        public final boolean overwrite;
        public final Header header;

        public HeaderItem(Header header) {
            this.overwrite = false;
            this.header = header;
        }

        public HeaderItem(Header header, boolean overwrite) {
            this.overwrite = overwrite;
            this.header = header;
        }

        public HeaderItem(String name, String value) {
            this.overwrite = false;
            this.header = new BasicHeader(name, value);
        }

        public HeaderItem(String name, String value, boolean overwrite) {
            this.overwrite = overwrite;
            this.header = new BasicHeader(name, value);
        }
    }
}