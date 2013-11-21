package com.example.base.http;

import com.example.base.exception.HttpException;
import com.example.base.parser.IParser;
import com.example.base.type.IType;



public class HttpClientApi {

	public static final String BASE_URL = "http://211.149.152.140:9080/";

	/* 检查是否升级url */
	public static final String REQ_CHECK_UPDATE = "update/getUpdateInfo.do";
	public static final String REQ_USER_LOGIN = "yuanjuweb/api/member/memberLoginApi";
	public static final String REQ_USER_REGISTE = "";
	
	
	public static <T> AsyncHttpTask<T> post(String url, RequestCallBack<T> callBack){
		AppHttpClient http = new AppHttpClient();
		return http.send(AppHttpRequest.HttpMethod.POST, getAbsoluteUrl(url), callBack);
	}
	
	public static <T> AsyncHttpTask<T> post(String url, IParser<IType> parser, RequestCallBack<T> callBack){
		AppHttpClient http = new AppHttpClient();
		return http.send(AppHttpRequest.HttpMethod.POST, getAbsoluteUrl(url), parser, callBack);
	}
	
	public static <T> AsyncHttpTask<T> post(String url, RequestParams params, IParser<IType> parser, RequestCallBack<T> callBack){
		AppHttpClient http = new AppHttpClient();
		return http.send(AppHttpRequest.HttpMethod.POST, getAbsoluteUrl(url), params, parser, callBack);
	}
	
	public static <T> AsyncHttpTask<T> get(String url, RequestCallBack<T> callBack){
		AppHttpClient http = new AppHttpClient();
		return http.send(AppHttpRequest.HttpMethod.GET, getAbsoluteUrl(url), callBack);
	}
	
	public static <T> AsyncHttpTask<T> get(String url, IParser<IType> parser, RequestCallBack<T> callBack){
		AppHttpClient http = new AppHttpClient();
		return http.send(AppHttpRequest.HttpMethod.GET, getAbsoluteUrl(url), parser, callBack);
	}
	
	public static <T> AsyncHttpTask<T> get(String url, RequestParams params, IParser<IType> parser, RequestCallBack<T> callBack){
		AppHttpClient http = new AppHttpClient();
		return http.send(AppHttpRequest.HttpMethod.GET, getAbsoluteUrl(url), params, parser, callBack);
	}
	
	/**
	 * 发起同步请求
	 * @param method
	 * @param url
	 * @return
	 * @throws HttpException
	 */
	public static ResponseStream postSync(String url) throws HttpException {
		AppHttpClient http = new AppHttpClient();
		return http.sendSync(AppHttpRequest.HttpMethod.POST, getAbsoluteUrl(url));
	}

	/**
	 * 发起同步请求
	 * @param method
	 * @param url
	 * @param params
	 * @return
	 * @throws HttpException
	 */
	public static ResponseStream postSync(String url, RequestParams params) throws HttpException {
		AppHttpClient http = new AppHttpClient();
		return http.sendSync(AppHttpRequest.HttpMethod.POST, getAbsoluteUrl(url), params, null);
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
	public static ResponseStream postSync(String url, RequestParams params, String contentType) throws HttpException {
		AppHttpClient http = new AppHttpClient();
		return http.sendSync(AppHttpRequest.HttpMethod.POST, getAbsoluteUrl(url), params, contentType);
	}
	
	private static String getAbsoluteUrl(String relativeUrl) {
		return BASE_URL + relativeUrl;
	}
	
}
