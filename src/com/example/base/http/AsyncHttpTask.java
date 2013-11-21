package com.example.base.http;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.protocol.HttpContext;

import android.os.SystemClock;
import android.text.TextUtils;

import com.example.base.exception.HttpException;
import com.example.base.exception.ParseException;
import com.example.base.parser.IParser;
import com.example.base.type.IType;
import com.example.base.utils.HttpUtils;
import com.example.base.utils.LogUtil;

/**
 * 执行异步请求
 * @author lim
 *
 * @param <T>
 */
public class AsyncHttpTask<T> extends CompatibleAsyncTask<Object, Object, Void> implements RequestCallBackHandler {

	private final static int UPDATE_START = 0x01;
    private final static int UPDATE_LOADING = 0x02;
    private final static int UPDATE_FAILURE = 0x03;
    private final static int UPDATE_SUCCESS = 0x04;
    private final static int UPDATE_FINISH = 0x05;
	
    private final HttpContext context;
    private final AbstractHttpClient client;
    private final IParser<IType> parser;
    private final RequestCallBack<T> callback;

    private String charset; // The default charset of response header info.
    private String requestUrl;
    private String fileSavePath = null;
    
    private HttpRequestBase request;
    private HttpRedirectHandler httpRedirectHandler;

    private int retriedTimes = 0;
    private boolean isUploading = true;
    private boolean autoResume = false; // Whether the downloading could continue from the point of interruption.
    private boolean autoRename = false; // Whether rename the file by response header info when the download completely.
    private boolean mStopped = false;
    
    private long lastUpdateTime;
    private long expiry = AppHttpGetCache.getDefaultExpiryTime();
    private State state = State.WAITING;
    
    public AsyncHttpTask(AbstractHttpClient client, HttpContext context, String charset, IParser<IType> parser, RequestCallBack<T> callback) {
        this.client = client;
        this.context = context;
        this.parser = parser;
        this.callback = callback;
        this.charset = charset;
    }

    public State getState() {
        return state;
    }

    public void setExpiry(long expiry) {
        this.expiry = expiry;
    }
    
    public void setHttpRedirectHandler(HttpRedirectHandler httpRedirectHandler) {
        if (httpRedirectHandler != null) {
            this.httpRedirectHandler = httpRedirectHandler;
        }
    }

    public RequestCallBack<T> getRequestCallBack() {
        return this.callback;
    }
    
    // 执行请求
    @SuppressWarnings("unchecked")
	private ResponseInfo<T> sendRequest(HttpRequestBase request) throws HttpException, ParseException{
    	//是否是执行文件下载
        if (autoResume && !TextUtils.isEmpty(fileSavePath)) {
            File downloadFile = new File(fileSavePath);
            long fileLen = 0;
            if (downloadFile.isFile() && downloadFile.exists()) {
                fileLen = downloadFile.length();
            }
            if (fileLen > 0) {
                request.setHeader("RANGE", "bytes=" + fileLen + "-");
            }
        }

        boolean retry = true;
        HttpRequestRetryHandler retryHandler = client.getHttpRequestRetryHandler();
        while (retry) {
            IOException exception = null;
            try {
                if (request.getMethod().equals(AppHttpRequest.HttpMethod.GET.toString())) {
                    String result = AppHttpClient.mHttpGetCache.get(requestUrl);
                    if (result != null) {
                        return new ResponseInfo<T>(null, (T) result, true);
                    }
                }

                ResponseInfo<T> responseInfo = null;
                if (!isCancelled()) {
                    HttpResponse response = client.execute(request, context);
                    responseInfo = handleResponse(response);
                }
                return responseInfo;
            } catch (UnknownHostException e) {
                exception = e;
                retry = retryHandler.retryRequest(exception, ++retriedTimes, context);
            } catch (SocketException e) {
                retry = false;
                exception = e;
                retry = retryHandler.retryRequest(exception, ++retriedTimes, context);
            } catch (SocketTimeoutException e) {
            	exception = e;
                retry = retryHandler.retryRequest(exception, ++retriedTimes, context);
            } catch (IOException e) {
                exception = e;
                retry = retryHandler.retryRequest(exception, ++retriedTimes, context);
            } catch (NullPointerException e) {
                exception = new IOException(e.getMessage());
                exception.initCause(e);
                retry = retryHandler.retryRequest(exception, ++retriedTimes, context);
            } catch (HttpException e) {
                throw e;
            } catch (ParseException e) {
                throw e;
            } catch (Throwable e) {
                exception = new IOException(e.getMessage());
                exception.initCause(e);
                retry = retryHandler.retryRequest(exception, ++retriedTimes, context);
            }
            
            if (!retry && exception != null) {
                throw new HttpException(exception);
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
	 * @throws ParseException 
	 */
	@SuppressWarnings("unchecked")
	private ResponseInfo<T> handleResponse(HttpResponse response) throws HttpException, IOException, ParseException {
        if (response == null) {
            throw new HttpException("response is null");
        }
        if (isCancelled()) return null;

        StatusLine status = response.getStatusLine();
        int statusCode = status.getStatusCode();
        if (statusCode < 300) {
            Object result = null;
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                isUploading = false;
                if (!TextUtils.isEmpty(fileSavePath)) {
                    autoResume = autoResume && HttpUtils.isSupportRange(response);
                    String responseFileName = autoRename ? HttpUtils.getFileNameFromHttpResponse(response) : null;
                    result = downloadFile(entity, fileSavePath, autoResume, responseFileName);
                } else {
                    // Set charset from response header info if it's exist.
                    String responseCharset = HttpUtils.getCharsetFromHttpResponse(response);
                    charset = TextUtils.isEmpty(responseCharset) ? charset : responseCharset;

                    result = downloadString(entity);
                    AppHttpClient.mHttpGetCache.put(requestUrl, (String) result, expiry);
                    
                    if (parser != null) {
                    	result = parser.parse((String)result);
					}
                }
            }
            return new ResponseInfo<T>(response, (T) result, false);
        } else if (statusCode == 301 || statusCode == 302) {
        	//请求重定向
            if (httpRedirectHandler == null) {
                httpRedirectHandler = new DefaultHttpRedirectHandler();
            }
            HttpRequestBase request = httpRedirectHandler.getDirectRequest(response);
            if (request != null) {
                return this.sendRequest(request);
            }
        } else if (statusCode == 416) {
            throw new HttpException(statusCode, "maybe the file has downloaded completely");
        } else {
            throw new HttpException(statusCode, status.getReasonPhrase());
        }
        return null;
    }
    
	/**
	 * 文本数据下载
	 * @param entity
	 * @return
	 * @throws IOException
	 */
	private String downloadString(HttpEntity entity) throws IOException{
		if (entity == null) return null;

        long current = 0;
        long total = entity.getContentLength();

        InputStream inputStream = null;
        StringBuilder sb = new StringBuilder();
        try {
            inputStream = entity.getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, charset));
            String line = "";
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                current += HttpUtils.sizeOfString(line, charset);
                if (!this.updateProgress(total, current, false)) {
                    return sb.toString();
                }
            }
        	this.updateProgress(total, current, true);
        } finally {
        	 if (inputStream != null) {
                 try {
                	 inputStream.close();
                 } catch (Throwable e) {
                	 e.printStackTrace();
                 }
             }
        }
        LogUtil.i(sb.toString());
        return sb.toString();
	}
	
	/**
	 * 文件数据下载
	 * @param entity
	 * @param target
	 * @param isResume
	 * @param responseFileName
	 * @return
	 * @throws IOException
	 */
	private File downloadFile(HttpEntity entity, String target, boolean isResume, String responseFileName) throws IOException {
		if (entity == null || TextUtils.isEmpty(target)) {
			return null;
		}

		File targetFile = new File(target);
		if (!targetFile.exists()) {
			targetFile.createNewFile();
		}

		long current = 0;
		InputStream inputStream = null;
		FileOutputStream fileOutputStream = null;
		try {
			if (isResume) {
				current = targetFile.length();
				fileOutputStream = new FileOutputStream(target, true);
			} else {
				fileOutputStream = new FileOutputStream(target);
			}
			long total = entity.getContentLength() + current;
			if (!this.updateProgress(total, current, true)) {
				return targetFile;
			}
			inputStream = entity.getContent();
			BufferedInputStream bis = new BufferedInputStream(inputStream);
			byte[] tmp = new byte[4096];
			int len;
			while ((len = bis.read(tmp)) != -1) {
				fileOutputStream.write(tmp, 0, len);
				current += len;
				if (!this.updateProgress(total, current, false)) {
					return targetFile;
				}
			}
			fileOutputStream.flush();
			this.updateProgress(total, current, true);
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (Throwable e) {
				}
			}
			if (fileOutputStream != null) {
				try {
					fileOutputStream.close();
				} catch (Throwable e) {
				}
			}
		}

		if (targetFile.exists() && !TextUtils.isEmpty(responseFileName)) {
			File newFile = new File(targetFile.getParent(), responseFileName);
			while (newFile.exists()) {
				newFile = new File(targetFile.getParent(), System.currentTimeMillis() + responseFileName);
			}
			return targetFile.renameTo(newFile) ? newFile : targetFile;
		} else {
			return targetFile;
		}
	}
	
    @Override
    protected void onPreExecute() {
    	super.onPreExecute();
        this.publishProgress(UPDATE_START);
    }
    
    @Override
    protected Void doInBackground(Object... params) {
        if (params == null || params.length < 1) return null;

        if (params.length > 3) {
            fileSavePath = String.valueOf(params[1]);
            autoResume = (Boolean) params[2];
            autoRename = (Boolean) params[3];
        }
        try {
            request = (HttpRequestBase) params[0];
            requestUrl = request.getURI().toString();
            if (callback != null) {
                callback.setRequestUrl(requestUrl);
            }
            lastUpdateTime = SystemClock.uptimeMillis();

            ResponseInfo<T> responseInfo = sendRequest(request);
            if (responseInfo != null) {
                this.publishProgress(UPDATE_SUCCESS, responseInfo);
                return null;
            }
        } catch (HttpException e) {
            this.publishProgress(UPDATE_FAILURE, e, e.getExceptionCode(), e.getMessage());
        } catch (ParseException e) {
        	this.publishProgress(UPDATE_FAILURE, e, e.getExceptionCode(), e.getMessage());
		}

        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
    	super.onPostExecute(result);
    	 this.publishProgress(UPDATE_FINISH);
    }
    
    @Override
    @SuppressWarnings("unchecked")
    protected void onProgressUpdate(Object... values) {
        if (mStopped || values == null || values.length < 1 || callback == null) return;
        switch ((Integer) values[0]) {
            case UPDATE_START:
                this.state = State.STARTED;
                callback.onStart();
                break;
            case UPDATE_LOADING:
                if (values.length != 3) return;
                this.state = State.LOADING;
                callback.onLoading( Long.valueOf(String.valueOf(values[1])), Long.valueOf(String.valueOf(values[2])), isUploading);
                break;
            case UPDATE_FAILURE:
                if (values.length != 4) return;
                this.state = State.FAILURE;
                callback.onFailure((Throwable) values[1], String.valueOf(values[1]),(String) values[3]);
                break;
            case UPDATE_SUCCESS:
                if (values.length != 2) return;
                this.state = State.SUCCESS;
                callback.onSuccess((ResponseInfo<T>) values[1]);
                break;
            case UPDATE_FINISH:
                this.state = State.FINNSH;
                callback.onFinish();
                break;
            default:
                break;
        }
    }

    @Override
    public boolean updateProgress(long total, long current, boolean forceUpdateUI) {
        if (callback != null && !mStopped) {
            if (forceUpdateUI) {
                this.publishProgress(UPDATE_LOADING, total, current);
            } else {
                long currTime = SystemClock.uptimeMillis();
                if (currTime - lastUpdateTime >= callback.getRate()) {
                    lastUpdateTime = currTime;
                    this.publishProgress(UPDATE_LOADING, total, current);
                }
            }
        }
        return !mStopped;
    }

    /**
     * stop request task.
     */
    @Override
    public void stop() {
        this.mStopped = true;
        if (!request.isAborted()) {
            try {
                request.abort();
            } catch (Throwable e) {
            }
        }
        if (!this.isCancelled()) {
            try {
                this.cancel(true);
            } catch (Throwable e) {
            	e.printStackTrace();
            }
        }

        this.state = State.FINNSH;
        if (callback != null) {
            callback.onFinish();
        }
    }

    @Override
    public boolean isStopped() {
        return mStopped;
    }

    public enum State {
        WAITING(0), STARTED(1), LOADING(2), SUCCESS(3), FINNSH(4), FAILURE(-1);
        private int value = 0;

        State(int value) {
            this.value = value;
        }

        public static State valueOf(int value) {
            switch (value) {
                case 0:
                    return WAITING;
                case 1:
                    return STARTED;
                case 2:
                    return LOADING;
                case 3:
                	return SUCCESS;
                case 4:
                    return FINNSH;
                case -1:
                    return FAILURE;
                default:
                    return null;
            }
        }

        public int value() {
            return this.value;
        }
    }
}
