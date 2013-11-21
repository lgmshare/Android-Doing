package com.example.base.config;

import android.os.Environment;

/**
 * 参数配置
 * @author lim
 *
 */
public class BaseConfig {

	// 客户端的DES 加密的key
    public static final String MD5_KEY = "yj123456";
	/*-----------------preference存储key配置-------------*/
    
    public static final String FILE_ROOT_URL = "/base/";
    
	/*-----------------XMPP连接信息配置------------------*/
	public static String APIKEY = "123456";
	public static String XMPPHOST = "211.149.152.140";
	public static String XMPPPORT = "5222";
	
	// 上传头像临时路径
    public static String PHOTOPATH = "/yuanju/user/";
    // 上传头像名字
    public static String UPLOAD_PHOTO_NAME = "avater.png";
    /* 升级包保存路径 */
	public final static String apkSavepath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/";
	/* 在多少天内不检查升级 */
	public final static int defaultMinUpdateDay = 50;
}
