/**
 * Copyright(c)2012 Beijing PeaceMap Co. Ltd.
 * All right reserved. 
 */
package com.example.base.update;

/**
 * 下载数据接口
 * @author： aokunsang
 * @date： 2012-12-17
 */
public interface UpdateCallback {

	/**
	 * 下载前准备
	 */
	public void onPreare();
	/**
	 * 下载进度更新
	 * @param progress 进度值
	 */
	public void onProgress(int progress);
	/**
	 * 下载完成
	 * @param success  下载成功标示
	 * @param errorMsg 下载失败显示内容
	 */
	public void onCompleted(boolean success,String errorMsg);
	/**
	 * 取消下载
	 */
	public boolean onCancel();
}
