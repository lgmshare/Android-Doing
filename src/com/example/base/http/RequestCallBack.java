package com.example.base.http;


public abstract class RequestCallBack<T> {

	//默认请求速度
	private static final int DEFAULT_RATE = 1000;
	//最小请求速度
	private static final int MIN_RATE = 200;

	private String requestUrl;
	private Object userTag;

	public RequestCallBack() {
		this.rate = DEFAULT_RATE;
	}

	public RequestCallBack(int rate) {
		this.rate = rate;
	}

	public RequestCallBack(Object userTag) {
		this.rate = DEFAULT_RATE;
		this.userTag = userTag;
	}

	public RequestCallBack(int rate, Object userTag) {
		this.rate = rate;
		this.userTag = userTag;
	}

	private int rate;

	public final int getRate() {
		if (rate < MIN_RATE) {
			return MIN_RATE;
		}
		return rate;
	}

	public final void setRate(int rate) {
		this.rate = rate;
	}

	public Object getUserTag() {
		return userTag;
	}

	public void setUserTag(Object userTag) {
		this.userTag = userTag;
	}

	public final String getRequestUrl() {
		return requestUrl;
	}

	public final void setRequestUrl(String requestUrl) {
		this.requestUrl = requestUrl;
	}

	/**
	 * 请求开始
	 */
	public void onStart() {
	}

	/**
	 * 加载进度
	 * 
	 * @param total
	 * @param current
	 * @param isUploading
	 */
	public void onLoading(long total, long current, boolean isUploading) {
	}

	/**
	 * 请求结束
	 */
	public void onFinish() {
	}

	/**
	 * 请求成功
	 * 
	 * @param responseInfo
	 */
	public abstract void onSuccess(ResponseInfo<T> responseInfo);

	/**
	 * 请求失败
	 * 
	 * @param error
	 * @param msg
	 */
	public abstract void onFailure(Throwable error, String errorCode, String msg);
}
