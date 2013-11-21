package com.example.base.http;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.HttpVersion;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultRedirectHandler;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;

import android.text.TextUtils;
import android.util.Log;

import com.example.base.exception.HttpException;
import com.example.base.parser.IParser;
import com.example.base.type.IType;

/**
 * 网络连接api
 * @author lim
 *
 */
public class AppHttpClient {

	public static final String LOG_TAG = "AsyncHttpClient";
	public static final String VERSION = "1.0.0";
	public static final int DEFAULT_MAX_CONNECTIONS = 10;//最大连接数
	public static final int DEFAULT_SOCKET_TIMEOUT = 10 * 1000;//超时时间
	public static final int DEFAULT_MAX_RETRIES = 5;//最大重试次数
	public static final int DEFAULT_RETRY_SLEEP_TIME_MILLIS = 1500;//请求间隔时间
	public static final int DEFAULT_SOCKET_BUFFER_SIZE = 8192;//缓存大小
	public static final String HEADER_ACCEPT_ENCODING = "Accept-Encoding";
	public static final String ENCODING_GZIP = "gzip";

	private int maxConnections = DEFAULT_MAX_CONNECTIONS;
	private int timeout = DEFAULT_SOCKET_TIMEOUT;
	private int threadPoolSize = 3;
	private long currentRequestExpiry = AppHttpGetCache.getDefaultExpiryTime();
	private String defaultResponseTextCharset = HTTP.UTF_8;

	private final DefaultHttpClient httpClient;
	private final HttpContext httpContext;
	private final Map<String, String> clientHeaderMap;
	
	public static AppHttpGetCache mHttpGetCache;
	private static ExecutorService threadPool;
	private HttpRedirectHandler httpRedirectHandler;

	/**
	 * Creates a new HttpClient with default constructor arguments values
	 */
	public AppHttpClient() {
		this(false, 80, 443);
	}

	/**
	 * Creates a new HttpClient.
	 * 
	 * @param httpPort
	 *            non-standard HTTP-only port
	 */
	public AppHttpClient(int httpPort) {
		this(false, httpPort, 443);
	}

	/**
	 * Creates a new HttpClient.
	 * 
	 * @param httpPort
	 *            non-standard HTTP-only port
	 * @param httpsPort
	 *            non-standard HTTPS-only port
	 */
	public AppHttpClient(int httpPort, int httpsPort) {
		this(false, httpPort, httpsPort);
	}

	/**
	 * Creates new HttpClient using given params
	 * 
	 * @param fixNoHttpResponseException
	 *            Whether to fix or not issue, by ommiting SSL verification
	 * @param httpPort
	 *            HTTP port to be used, must be greater than 0
	 * @param httpsPort
	 *            HTTPS port to be used, must be greater than 0
	 */
	public AppHttpClient(boolean fixNoHttpResponseException, int httpPort, int httpsPort) {
		this(getDefaultSchemeRegistry(fixNoHttpResponseException, httpPort, httpsPort));
	}

	/**
	 * Returns default instance of SchemeRegistry
	 * 
	 * @param fixNoHttpResponseException
	 *            Whether to fix or not issue, by ommiting SSL verification
	 * @param httpPort
	 *            HTTP port to be used, must be greater than 0
	 * @param httpsPort
	 *            HTTPS port to be used, must be greater than 0
	 */
	private static SchemeRegistry getDefaultSchemeRegistry(boolean fixNoHttpResponseException, int httpPort, int httpsPort) {
		if (fixNoHttpResponseException) {
			Log.d(LOG_TAG, "Beware! Using the fix is insecure, as it doesn't verify SSL certificates.");
		}

		if (httpPort < 1) {
			httpPort = 80;
			Log.d(LOG_TAG, "Invalid HTTP port number specified, defaulting to 80");
		}

		if (httpsPort < 1) {
			httpsPort = 443;
			Log.d(LOG_TAG, "Invalid HTTPS port number specified, defaulting to 443");
		}

		// Fix to SSL flaw in API < ICS
		// See https://code.google.com/p/android/issues/detail?id=13117
		SSLSocketFactory sslSocketFactory;
		if (fixNoHttpResponseException)
			sslSocketFactory = MySSLSocketFactory.getFixedSocketFactory();
		else
			sslSocketFactory = SSLSocketFactory.getSocketFactory();

		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), httpPort));
		schemeRegistry.register(new Scheme("https", sslSocketFactory, httpsPort));

		return schemeRegistry;
	}

	public AppHttpClient(SchemeRegistry schemeRegistry) {

		BasicHttpParams httpParams = new BasicHttpParams();

		///////////////////////网络设置/////////////////////////////////
		//设置连接最大等待时间
		ConnManagerParams.setTimeout(httpParams, timeout);
		//设置并发数
		ConnManagerParams.setMaxConnectionsPerRoute(httpParams, new ConnPerRouteBean(maxConnections));
		//最大连接数
		ConnManagerParams.setMaxTotalConnections(httpParams, DEFAULT_MAX_CONNECTIONS);
		//设置连接超时时间
		HttpConnectionParams.setConnectionTimeout(httpParams, timeout);
		//设置读取超时时间
		HttpConnectionParams.setSoTimeout(httpParams, timeout);
		HttpConnectionParams.setTcpNoDelay(httpParams, true);
		HttpConnectionParams.setSocketBufferSize(httpParams, DEFAULT_SOCKET_BUFFER_SIZE);

		HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setUserAgent(httpParams, String.format("android-async-http/%s (http://loopj.com/android-async-http)", VERSION));

		ThreadSafeClientConnManager cm = new ThreadSafeClientConnManager(httpParams, schemeRegistry);
		mHttpGetCache = new AppHttpGetCache();
		threadPool = Executors.newFixedThreadPool(DEFAULT_MAX_CONNECTIONS, threadFactory);
		clientHeaderMap = new HashMap<String, String>();

		httpContext = new BasicHttpContext(new BasicHttpContext());
		
		httpClient = new DefaultHttpClient(cm, httpParams);
		httpClient.addRequestInterceptor(new HttpRequestInterceptor() {
			@Override
			public void process(HttpRequest request, HttpContext context) {
				if (!request.containsHeader(HEADER_ACCEPT_ENCODING)) {
					request.addHeader(HEADER_ACCEPT_ENCODING, ENCODING_GZIP);
				}
				for (String header : clientHeaderMap.keySet()) {
					request.addHeader(header, clientHeaderMap.get(header));
				}
			}
		});

		httpClient.addResponseInterceptor(new HttpResponseInterceptor() {
			@Override
			public void process(HttpResponse response, HttpContext context) {
				final HttpEntity entity = response.getEntity();
				if (entity == null) {
					return;
				}
				final Header encoding = entity.getContentEncoding();
				if (encoding != null) {
					for (HeaderElement element : encoding.getElements()) {
						if (element.getName().equalsIgnoreCase(ENCODING_GZIP)) {
							response.setEntity(new GZipDecompressingEntity(
									response.getEntity()));
							break;
						}
					}
				}
			}
		});

		httpClient.setHttpRequestRetryHandler(new RetryHandler(DEFAULT_MAX_RETRIES, DEFAULT_RETRY_SLEEP_TIME_MILLIS));

	}

	private static final ThreadFactory threadFactory = new ThreadFactory() {
		
		private final AtomicInteger mCount = new AtomicInteger(1);

		@Override
		public Thread newThread(Runnable r) {
			Thread thread = new Thread(r, "HttpClient #" + mCount.getAndIncrement());
			thread.setPriority(Thread.NORM_PRIORITY - 1);
			return thread;
		}
	};

	/**
     * Get the underlying HttpClient instance. This is useful for setting additional fine-grained
     * settings for requests by accessing the client's ConnectionManager, HttpParams and
     * SchemeRegistry.
     *
     * @return underlying HttpClient instance
     */
    public HttpClient getHttpClient() {
        return this.httpClient;
    }

    /**
     * Get the underlying HttpContext instance. This is useful for getting and setting fine-grained
     * settings for requests by accessing the context's attributes such as the CookieStore.
     *
     * @return underlying HttpContext instance
     */
    public HttpContext getHttpContext() {
        return this.httpContext;
    }


	/**
	 * 设置默认字符格式
	 * @param charSet
	 * @return
	 */
	public void setDefaultResponseTextCharset(String charSet) {
		if (!TextUtils.isEmpty(charSet)) {
			this.defaultResponseTextCharset = charSet;
		}
	}

	public void setHttpGetCacheSize(int httpGetCacheSize) {
		mHttpGetCache.setCacheSize(httpGetCacheSize);
	}

	public void setHttpRedirectHandler(HttpRedirectHandler httpRedirectHandler) {
		this.httpRedirectHandler = httpRedirectHandler;
	}

	public void setDefaultHttpGetCacheExpiry(long defaultExpiry) {
		AppHttpGetCache.setDefaultExpiryTime(defaultExpiry);
		currentRequestExpiry = AppHttpGetCache.getDefaultExpiryTime();
	}

	public void setCurrentHttpGetCacheExpiry(long currRequestExpiry) {
		this.currentRequestExpiry = currRequestExpiry;
	}

	public void setRegisterScheme(Scheme scheme) {
		this.httpClient.getConnectionManager().getSchemeRegistry().register(scheme);
	}

	public void setRequestThreadPoolSize(int threadPoolSize) {
		if (threadPoolSize > 0 && threadPoolSize != this.threadPoolSize) {
			this.threadPoolSize = threadPoolSize;
			AppHttpClient.threadPool = Executors.newFixedThreadPool(threadPoolSize, threadFactory);
		}
	}
	/** Sets an optional CookieStore to use when making requests
     *
     * @param cookieStore The CookieStore implementation to use, usually an instance of {@link
     *                    PersistentCookieStore}
     */
    public void setCookieStore(CookieStore cookieStore) {
        httpContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
    }

    /**
     * Overrides the threadpool implementation used when queuing/pooling requests. By default,
     * Executors.newFixedThreadPool() is used.
     *
     * @param threadPool an instance of {@link ThreadPoolExecutor} to use for queuing/pooling
     *                   requests.
     */
    public void setThreadPool(ThreadPoolExecutor threadPool) {
        AppHttpClient.threadPool = threadPool;
    }

    /**
     * Simple interface method, to enable or disable redirects. If you set manually RedirectHandler
     * on underlying HttpClient, effects of this method will be canceled.
     *
     * @param enableRedirects boolean
     */
    public void setEnableRedirects(final boolean enableRedirects) {
        httpClient.setRedirectHandler(new DefaultRedirectHandler() {
            @Override
            public boolean isRedirectRequested(HttpResponse response, HttpContext context) {
                return enableRedirects;
            }
        });
    }

    /**
     * Sets the User-Agent header to be sent with each request. By default, "Android Asynchronous
     * Http Client/VERSION (http://loopj.com/android-async-http/)" is used.
     *
     * @param userAgent the string to use in the User-Agent header.
     */
    public void setUserAgent(String userAgent) {
        HttpProtocolParams.setUserAgent(this.httpClient.getParams(), userAgent);
    }


    /**
     * Returns current limit of parallel connections
     *
     * @return maximum limit of parallel connections, default is 10
     */
    public int getMaxConnections() {
        return maxConnections;
    }

    /**
     * Sets maximum limit of parallel connections
     *
     * @param maxConnections maximum parallel connections, must be at least 1
     */
    public void setMaxConnections(int maxConnections) {
        if (maxConnections < 1)
            maxConnections = DEFAULT_MAX_CONNECTIONS;
        this.maxConnections = maxConnections;
        final HttpParams httpParams = this.httpClient.getParams();
        ConnManagerParams.setMaxConnectionsPerRoute(httpParams, new ConnPerRouteBean(this.maxConnections));
    }

    /**
     * Returns current socket timeout limit (milliseconds), default is 10000 (10sec)
     *
     * @return Socket Timeout limit in milliseconds
     */
    public int getTimeout() {
        return timeout;
    }

    /**
     * Set the connection and socket timeout. By default, 10 seconds.
     *
     * @param timeout the connect/socket timeout in milliseconds, at least 1 second
     */
    public void setTimeout(int timeout) {
        if (timeout < 1000)
            timeout = DEFAULT_SOCKET_TIMEOUT;
        this.timeout = timeout;
        final HttpParams httpParams = this.httpClient.getParams();
        ConnManagerParams.setTimeout(httpParams, this.timeout);
        HttpConnectionParams.setSoTimeout(httpParams, this.timeout);
        HttpConnectionParams.setConnectionTimeout(httpParams, this.timeout);
    }

    /**
     * Sets the Proxy by it's hostname and port
     *
     * @param hostname the hostname (IP or DNS name)
     * @param port     the port number. -1 indicates the scheme default port.
     */
    public void setProxy(String hostname, int port) {
        final HttpHost proxy = new HttpHost(hostname, port);
        final HttpParams httpParams = this.httpClient.getParams();
        httpParams.setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
    }

    /**
     * Sets the Proxy by it's hostname,port,username and password
     *
     * @param hostname the hostname (IP or DNS name)
     * @param port     the port number. -1 indicates the scheme default port.
     * @param username the username
     * @param password the password
     */
    public void setProxy(String hostname, int port, String username, String password) {
        httpClient.getCredentialsProvider().setCredentials(
                new AuthScope(hostname, port),
                new UsernamePasswordCredentials(username, password));
        final HttpHost proxy = new HttpHost(hostname, port);
        final HttpParams httpParams = this.httpClient.getParams();
        httpParams.setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
    }


    /**
     * Sets the SSLSocketFactory to user when making requests. By default, a new, default
     * SSLSocketFactory is used.
     *
     * @param sslSocketFactory the socket factory to use for https requests.
     */
    public void setSSLSocketFactory(SSLSocketFactory sslSocketFactory) {
        this.httpClient.getConnectionManager().getSchemeRegistry().register(new Scheme("https", sslSocketFactory, 443));
    }

    /**
     * Sets the maximum number of retries and timeout for a particular Request.
     *
     * @param retries maximum number of retries per request
     * @param timeout sleep between retries in milliseconds
     */
    public void setMaxRetriesAndTimeout(int retries, int timeout) {
        this.httpClient.setHttpRequestRetryHandler(new RetryHandler(retries, timeout));
    }

    /**
     * Sets headers that will be added to all requests this client makes (before sending).
     *
     * @param header the name of the header
     * @param value  the contents of the header
     */
    public void addHeader(String header, String value) {
        clientHeaderMap.put(header, value);
    }

    /**
     * Remove header from all requests this client makes (before sending).
     *
     * @param header the name of the header
     */
    public void removeHeader(String header) {
        clientHeaderMap.remove(header);
    }

    /**
     * Sets basic authentication for the request. Uses AuthScope.ANY. This is the same as
     * setBasicAuth('username','password',AuthScope.ANY)
     *
     * @param username Basic Auth username
     * @param password Basic Auth password
     */
    public void setBasicAuth(String username, String password) {
        AuthScope scope = AuthScope.ANY;
        setBasicAuth(username, password, scope);
    }

    /**
     * Sets basic authentication for the request. You should pass in your AuthScope for security. It
     * should be like this setBasicAuth("username","password", new AuthScope("host",port,AuthScope.ANY_REALM))
     *
     * @param username Basic Auth username
     * @param password Basic Auth password
     * @param scope    - an AuthScope object
     */
    public void setBasicAuth(String username, String password, AuthScope scope) {
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(username, password);
        this.httpClient.getCredentialsProvider().setCredentials(scope, credentials);
    }

    /**
     * Removes set basic auth credentials
     */
    public void clearBasicAuth() {
        this.httpClient.getCredentialsProvider().clear();
    }
	
    /**
     * 发起异步请求
     * @param <T>
     * @param method
     * @param url
     * @param callBack
     * @return
     */
    public <T> AsyncHttpTask<T> send(AppHttpRequest.HttpMethod method, String url, RequestCallBack<T> callBack) {
    	return send(method, url, null, null, callBack);
    }
    /**
     * 发起异步请求
     * @param <T>
     * @param method
     * @param url
     * @param callBack
     * @return
     */
	public <T> AsyncHttpTask<T> send(AppHttpRequest.HttpMethod method, String url, IParser<IType> parser, RequestCallBack<T> callBack) {
		return send(method, url, null, null, callBack);
	}

	/**
	 * 发起异步请求
	 * @param <T>
	 * @param method
	 * @param url
	 * @param params
	 * @param callBack
	 * @return
	 */
	public <T> AsyncHttpTask<T> send(AppHttpRequest.HttpMethod method, String url, RequestParams params, RequestCallBack<T> callBack) {
		return send(method, url, params, null, callBack);
	}
	
	/**
	 * 发起异步请求
	 * @param <T>
	 * @param method
	 * @param url
	 * @param params
	 * @param parser
	 * @param callBack
	 * @return
	 */
	public <T> AsyncHttpTask<T> send(AppHttpRequest.HttpMethod method, String url,RequestParams params, IParser<IType> parser, RequestCallBack<T> callBack) {
		return send(method, url, params, null, parser, callBack);
	}

	/**
	 * 发起异步请求
	 * @param <T>
	 * @param method
	 * @param url
	 * @param params
	 * @param contentType
	 * @param callBack
	 * @return
	 */
	public <T> AsyncHttpTask<T> send(AppHttpRequest.HttpMethod method, String url, RequestParams params, String contentType, IParser<IType> parser, RequestCallBack<T> callBack) {
		if (url == null)
			throw new IllegalArgumentException("url may not be null");

		AppHttpRequest request = new AppHttpRequest(method, url);
		return sendRequest(request, params, contentType, parser, callBack);
	}

	/**
	 * 发起同步请求
	 * @param method
	 * @param url
	 * @return
	 * @throws HttpException
	 */
	public ResponseStream sendSync(AppHttpRequest.HttpMethod method, String url) throws HttpException {
		return sendSync(method, url, null);
	}

	/**
	 * 发起同步请求
	 * @param method
	 * @param url
	 * @param params
	 * @return
	 * @throws HttpException
	 */
	public ResponseStream sendSync(AppHttpRequest.HttpMethod method, String url, RequestParams params) throws HttpException {
		return sendSync(method, url, params, null);
	}

	/**
	 * 发起同步请求
	 * @param method
	 * @param url
	 * @param params
	 * @param contentType
	 * @return
	 * @throws HttpException
	 */
	public ResponseStream sendSync(AppHttpRequest.HttpMethod method, String url, RequestParams params, String contentType) throws HttpException {
		if (url == null)
			throw new IllegalArgumentException("url may not be null");

		AppHttpRequest request = new AppHttpRequest(method, url);
		return sendSyncRequest(request, params, contentType);
	}

	/**
	 * 文件下载
	 * @param url 下载地址
	 * @param target 存储地址
	 * @param callback
	 * @return
	 */
	public AsyncHttpTask<File> download(String url, String target, RequestCallBack<File> callback) {
		return download(AppHttpRequest.HttpMethod.GET, url, target, null, false, false, callback);
	}

	/**
	 * 文件下载
	 * @param url 下载地址
	 * @param target 存储地址
	 * @param autoResume 是否自动重新获取
	 * @param callback
	 * @return
	 */
	public AsyncHttpTask<File> download(String url, String target, boolean autoResume, RequestCallBack<File> callback) {
		return download(AppHttpRequest.HttpMethod.GET, url, target, null, autoResume, false, callback);
	}

	/**
	 * 文件下载
	 * @param url 下载地址
	 * @param target 存储地址
	 * @param autoResume 是否自动重新获取
	 * @param autoRename 是否自动重命名
	 * @param callback
	 * @return
	 */
	public AsyncHttpTask<File> download(String url, String target, boolean autoResume, boolean autoRename, RequestCallBack<File> callback) {
		return download(AppHttpRequest.HttpMethod.GET, url, target, null, autoResume, autoRename, callback);
	}

	/**
	 * 文件下载
	 * @param url 下载地址
	 * @param target 存储地址
	 * @param params 
	 * @param callback
	 * @return
	 */
	public AsyncHttpTask<File> download(String url, String target, RequestParams params, RequestCallBack<File> callback) {
		return download(AppHttpRequest.HttpMethod.GET, url, target, params, false, false, callback);
	}

	/**
	 * 文件下载
	 * @param url 下载地址
	 * @param target 存储地址
	 * @param params
	 * @param autoResume
	 * @param callback
	 * @return
	 */
	
	public AsyncHttpTask<File> download(String url, String target, RequestParams params, boolean autoResume, RequestCallBack<File> callback) {
		return download(AppHttpRequest.HttpMethod.GET, url, target, params, autoResume, false, callback);
	}

	/**
	 * 文件下载
	 * @param url 下载地址
	 * @param target 存储地址
	 * @param params
	 * @param autoResume
	 * @param autoRename
	 * @param callback
	 * @return
	 */
	public AsyncHttpTask<File> download(String url, String target, RequestParams params, boolean autoResume, boolean autoRename, RequestCallBack<File> callback) {
		return download(AppHttpRequest.HttpMethod.GET, url, target, params, autoResume, autoRename, callback);
	}

	/**
	 * 文件下载
	 * @param method
	 * @param url 下载地址
	 * @param target 存储地址
	 * @param callback
	 * @return
	 */
	public AsyncHttpTask<File> download(AppHttpRequest.HttpMethod method, String url, String target, RequestCallBack<File> callback) {
		return download(method, url, target, null, false, false, callback);
	}

	/**
	 * 文件下载
	 * @param method
	 * @param url 下载地址
	 * @param target 存储地址
	 * @param autoResume
	 * @param callback
	 * @return
	 */
	public AsyncHttpTask<File> download(AppHttpRequest.HttpMethod method, String url, String target, boolean autoResume, RequestCallBack<File> callback) {
		return download(method, url, target, null, autoResume, false, callback);
	}

	/**
	 * 文件下载
	 * @param method
	 * @param url 下载地址
	 * @param target 存储地址
	 * @param autoResume
	 * @param autoRename
	 * @param callback
	 * @return
	 */
	public AsyncHttpTask<File> download(AppHttpRequest.HttpMethod method, String url, String target, boolean autoResume, boolean autoRename, RequestCallBack<File> callback) {
		return download(method, url, target, null, autoResume, autoRename, callback);
	}

	/**
	 * 文件下载
	 * @param method
	 * @param url 下载地址
	 * @param target 存储地址
	 * @param params
	 * @param callback
	 * @return
	 */
	public AsyncHttpTask<File> download(AppHttpRequest.HttpMethod method, String url, String target, RequestParams params, RequestCallBack<File> callback) {
		return download(method, url, target, params, false, false, callback);
	}

	/**
	 * 文件下载
	 * @param method
	 * @param url 下载地址
	 * @param target 存储地址
	 * @param params
	 * @param autoResume
	 * @param callback
	 * @return
	 */
	public AsyncHttpTask<File> download(AppHttpRequest.HttpMethod method, String url, String target, RequestParams params, boolean autoResume, RequestCallBack<File> callback) {
		return download(method, url, target, params, autoResume, false, callback);
	}

	/**
	 * 文件下载
	 * @param method
	 * @param url 下载地址
	 * @param target 存储地址
	 * @param params
	 * @param autoResume
	 * @param autoRename
	 * @param callback
	 * @return
	 */
	public AsyncHttpTask<File> download(AppHttpRequest.HttpMethod method, String url, String target, RequestParams params, boolean autoResume, boolean autoRename, RequestCallBack<File> callback) {
		if (url == null)
			throw new IllegalArgumentException("url may not be null");
		if (target == null)
			throw new IllegalArgumentException("target may not be null");

		AppHttpRequest request = new AppHttpRequest(method, url);
		
		AsyncHttpTask<File> handler = new AsyncHttpTask<File>(httpClient, httpContext, defaultResponseTextCharset, null, callback);
		handler.setExpiry(currentRequestExpiry);
		handler.setHttpRedirectHandler(httpRedirectHandler);
		
		request.setRequestParams(params, handler);

		handler.executeOnExecutor(threadPool, request, target, autoResume, autoRename);
		return handler;
	}

	/**
	 * 异步请求
	 * @param <T>
	 * @param request
	 * @param params
	 * @param contentType
	 * @param callBack
	 * @return
	 */
	private <T> AsyncHttpTask<T> sendRequest(AppHttpRequest request, RequestParams params, String contentType, IParser<IType> parser, RequestCallBack<T> callBack) {
		if (contentType != null) {
			request.setHeader("Content-Type", contentType);
		}

		AsyncHttpTask<T> task = new AsyncHttpTask<T>(httpClient, httpContext, defaultResponseTextCharset, parser, callBack);

		task.setExpiry(currentRequestExpiry);
		task.setHttpRedirectHandler(httpRedirectHandler);
		request.setRequestParams(params, task);

		task.executeOnExecutor(threadPool, request);
		return task;
	}

	/**
	 * 同步请求
	 * @param request
	 * @param params
	 * @param contentType
	 * @return
	 * @throws HttpException
	 */
	private ResponseStream sendSyncRequest(AppHttpRequest request, RequestParams params, String contentType) throws HttpException {
        if (contentType != null) {
            request.setHeader("Content-Type", contentType);
        }

        SyncHttpTask handler = new SyncHttpTask(httpClient, httpContext, defaultResponseTextCharset);

        handler.setExpiry(currentRequestExpiry);
        handler.setHttpRedirectHandler(httpRedirectHandler);
        request.setRequestParams(params);

        return handler.sendRequest(request);
    }
}
