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

package com.example.base.utils;

import android.text.TextUtils;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.protocol.HTTP;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.cert.X509Certificate;

/**
 * Created by lim on 13-8-30.
 */
public class HttpUtils {
	

	private static final int STRING_BUFFER_LENGTH = 100;
	
	private static String defaultEncodingCharset = HTTP.DEFAULT_CONTENT_CHARSET;
    private static TrustManager[] trustAllCerts;
    
    public static String[] supportCharset = new String[]{
	    	"UTF-8",

            "GB2312",
            "GBK",
            "GB18030",

            "US-ASCII",
            "ASCII",

            "ISO-2022-KR",
            "ISO-8859-1",
            "ISO-8859-2",
            "ISO-2022-JP",
            "ISO-2022-JP-2"
    };
    
	private HttpUtils() {
	}

	public static boolean isSupportRange(final HttpResponse response) {
		if (response == null)
			return false;
		Header header = response.getFirstHeader("Accept-Ranges");
		if (header != null) {
			return "bytes".equals(header.getValue());
		}
		header = response.getFirstHeader("Content-Range");
		if (header != null) {
			String value = header.getValue();
			return value != null && value.startsWith("bytes");
		}
		return false;
	}

	public static String getFileNameFromHttpResponse(final HttpResponse response) {
		if (response == null)
			return null;
		String result = null;
		Header header = response.getFirstHeader("Content-Disposition");
		if (header != null) {
			for (HeaderElement element : header.getElements()) {
				NameValuePair fileNamePair = element.getParameterByName("filename");
				if (fileNamePair != null) {
					result = fileNamePair.getValue();
					// try to get correct encoding str
					result = toCharset(result, HTTP.UTF_8, result.length());
					break;
				}
			}
		}
		return result;
	}

	public static String getCharsetFromHttpResponse(final HttpResponse response) {
		if (response == null)
			return null;
		String result = null;
		Header header = response.getEntity().getContentType();
		if (header != null) {
			for (HeaderElement element : header.getElements()) {
				NameValuePair charsetPair = element.getParameterByName("charset");
				if (charsetPair != null) {
					result = charsetPair.getValue();
					break;
				}
			}
		}

		boolean isSupportedCharset = false;
		if (!TextUtils.isEmpty(result)) {
			try {
				isSupportedCharset = Charset.isSupported(result);
			} catch (Throwable e) {
			}
		}

		return isSupportedCharset ? result : null;
	}

	public static long sizeOfString(final String str, String charset) throws UnsupportedEncodingException {
		if (TextUtils.isEmpty(str)) {
			return 0;
		}
		int len = str.length();
		if (len < STRING_BUFFER_LENGTH) {
			return str.getBytes(charset).length;
		}
		long size = 0;
		for (int i = 0; i < len; i += STRING_BUFFER_LENGTH) {
			int end = i + STRING_BUFFER_LENGTH;
			end = end < len ? end : len;
			String temp = getSubString(str, i, end);
			size += temp.getBytes(charset).length;
		}
		return size;
	}

	//字符串截取
	public static String getSubString(final String str, int start, int end) {
		return new String(str.substring(start, end));
	}

	/**
	 * 受信的连接
	 */
	public static void trustAllSSLForHttpsURLConnection() {
		// Create a trust manager that does not validate certificate chains
		if (trustAllCerts == null) {
			trustAllCerts = new TrustManager[] { new X509TrustManager() {
				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
					return null;
				}

				public void checkClientTrusted(X509Certificate[] certs,
						String authType) {
				}

				public void checkServerTrusted(X509Certificate[] certs,
						String authType) {
				}
			} };
		}
		// Install the all-trusting trust manager
		final SSLContext sslContext;
		try {
			sslContext = SSLContext.getInstance("TLS");
			sslContext.init(null, trustAllCerts, null);
			HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
		} catch (Throwable e) {
			e.printStackTrace();
		}
		HttpsURLConnection.setDefaultHostnameVerifier(org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
	}

	/**
	 * 字符编码
	 * @param str
	 * @param charset
	 * @param judgeCharsetLength
	 * @return
	 */
	public static String toCharset(final String str, final String charset, int judgeCharsetLength) {
		try {
			String oldCharset = getEncoding(str, judgeCharsetLength);
			return new String(str.getBytes(oldCharset), charset);
		} catch (Throwable e) {
			e.printStackTrace();
			return str;
		}
	}

	/**
	 * 获取字符集
	 * @param str
	 * @param judgeCharsetLength
	 * @return
	 */
	public static String getEncoding(final String str, int judgeCharsetLength) {
		String encode = defaultEncodingCharset;
		for (String charset : supportCharset) {
			if (isCharset(str, charset, judgeCharsetLength)) {
				encode = charset;
				break;
			}
		}
		return encode;
	}

	public static boolean isCharset(final String str, final String charset, int judgeCharsetLength) {
		try {
			String temp = str.length() > judgeCharsetLength ? str.substring(0, judgeCharsetLength) : str;
			return temp.equals(new String(temp.getBytes(charset), charset));
		} catch (Throwable e) {
			return false;
		}
	}
}
