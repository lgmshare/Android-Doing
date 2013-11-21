package com.example.base.update;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.os.AsyncTask;

import com.example.base.utils.NetworkUtil;

/**
 * 下载更新包
 */
public class UpdateAsyncTask extends AsyncTask<String, Integer, String> {

	private static final String MSG_NET_FAIL = "NET_FAIL";
	private static final String MSG_DOWNLOAD_SUCCESS = "DOWNLOAD_SUCCESS";
	private static final String MSG_DOWNLOAD_FAIL = "DOWNLOAD_FAIL";
	
	private UpdateCallback downCallBack;
	private HttpURLConnection urlConn;
	
	public UpdateAsyncTask(UpdateCallback downloadCallback){
		this.downCallBack = downloadCallback;
	}
	
	@Override
	protected void onPreExecute() {
		downCallBack.onPreare();
		super.onPreExecute();
	}
	
	@Override
	protected String doInBackground(String... args) {
		String apkDownloadUrl = args[0]; //apk下载地址
		String apkSavePath = args[1]; //apk在sd卡中的安装位置
		String result = "";
		if(!NetworkUtil.checkURL(apkDownloadUrl)){
			result = MSG_NET_FAIL;
		}else{
			InputStream is = null;
			FileOutputStream fos = null;
			try {
				URL url = new URL(apkDownloadUrl);
				urlConn = (HttpURLConnection)url.openConnection();
				is = urlConn.getInputStream();
				int totalSize = urlConn.getContentLength();   //文件大小
				fos = new FileOutputStream(apkSavePath);
				
				byte buf[] = new byte[1024];
				float total = 0;
				int len = -1;
				//已下载进度
				double cm = 0.00;
				long last = 0;
				while(!downCallBack.onCancel() && (len = is.read(buf))!=-1){
					fos.write(buf, 0, len);
					total += len;
					
					double progressCount =(int)((total / totalSize) * 100);
					if (progressCount >= cm && Math.abs(System.currentTimeMillis() - last) >= 1000) {
						last = System.currentTimeMillis();
						cm += 0.01;
						int load = (int) (total * 100 / totalSize);
						publishProgress(load);
					}
				}
				fos.flush();
				result = MSG_DOWNLOAD_SUCCESS;
			} catch (Exception e) {
				e.printStackTrace();
				result = MSG_DOWNLOAD_FAIL;
			}finally{
				try {
					if(fos!=null)
						fos.close();
					if(is!=null)
						is.close();
				} catch (IOException e) {
					e.printStackTrace();
					result = MSG_DOWNLOAD_FAIL;
				}
			}
		}
		return result;
	}
	
	@Override
	protected void onProgressUpdate(Integer... values) {
		downCallBack.onProgress(values[0]);
		super.onProgressUpdate(values);
	}
	
	@Override
	protected void onPostExecute(String result) {
		if(downCallBack.onCancel()){
			downCallBack.onCompleted(false, "版本更新下载已取消。");
		}else if(MSG_DOWNLOAD_SUCCESS.equals(result)){
			downCallBack.onCompleted(true, null);
		}else if(MSG_NET_FAIL.equals(result)){
			downCallBack.onCompleted(false, "连接服务器失败，请稍后重试。");
		}else{
			downCallBack.onCompleted(false, "版本更新失败，请稍后重试。");
		}
		super.onPostExecute(result);
	}
	
	@Override
	protected void onCancelled() {
		if(urlConn!=null){
			urlConn.disconnect();
		}
		super.onCancelled();
	}
	
}
