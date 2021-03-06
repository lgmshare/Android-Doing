package com.example.base.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.os.Environment;

public class FileUtil {

	public static final String ENCODE_TYPE = "UTF-8";
	private int FILESIZE = 4 * 1024;
	
	public static String getSDPATH() {
		// 得到当前外部存储设备的目录( /SDCARD )
		return Environment.getExternalStorageDirectory() + "/";
	}
	
	/**
	 * 检查文件是否存在
	 * 
	 * @param fileName
	 * @param isCreate
	 * @return
	 * @throws IOException
	 */
	public static boolean isExist(String fileName, boolean isCreate)
			throws IOException {
		File file = new File(fileName);
		boolean ret = file.exists();
		if (!ret && isCreate) {
			file.createNewFile();
		}
		return ret;
	}

	/**
	 * 删除文件
	 * 
	 * @param fileName
	 */
	public static void deteleFile(String fileName) {
		File file = new File(fileName);
		if (file.exists()) {
			file.delete();
		}
	}

	/**
	 * 递归删除文件和文件夹
	 * 
	 * @param file
	 *            要删除的根目录
	 */
	public static void deleteFile(File file) {
		if (!file.exists()) {
			return;
		}

		if (file.isFile()) {
			file.delete();
			return;
		}
		if (file.isDirectory()) {
			File[] childFile = file.listFiles();
			if (childFile == null || childFile.length == 0) {
				file.delete();
				return;
			}
			for (File f : childFile) {
				deleteFile(f);
			}
			file.delete();
		}
	}

	/**
	 * 创建文件
	 * 
	 * @param fileName
	 * @throws IOException
	 */
	public static void createFile(String fileName) throws IOException {
		File file = new File(fileName);
		if (file.exists()) {
			file.delete();
		}
		file.createNewFile();
	}

	/**
	 * 读数据
	 * 
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	public static byte[] read(String fileName) throws IOException {
		File file = new File(fileName);
		if (!file.exists()) {
			return null;
		}
		FileInputStream is = null;
		try {
			is = new FileInputStream(fileName);
			int size = is.available();
			byte[] data = new byte[size];
			is.read(data);
			return data;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != is) {
				is.close();
			}
		}
		return null;
	}

	/**
	 * 写入数据
	 * 
	 * @param fileName
	 * @param data
	 * @throws IOException
	 */
	public static void write(String fileName, byte[] data) throws IOException {
		FileOutputStream os = null;
		try {
			File file = new File(fileName);
			if (file.exists()) {
				file.delete();
			}
			file.createNewFile();
			os = new FileOutputStream(fileName);
			os.write(data);
			os.flush();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != os) {
				os.close();
			}
		}
	}

	/**
	 * 在SD卡上创建文件
	 * 
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	public static File createSDFile(String fileName) throws IOException {
		File file = new File(getSDPATH() + fileName);
		file.createNewFile();
		return file;
	}

	/**
	 * 在SD卡上创建目录
	 * 
	 * @param dirName
	 * @return
	 */
	public File createSDDir(String dirName) {
		File dir = new File(getSDPATH() + dirName);
		dir.mkdir();
		return dir;
	}

	/**
	 * 判断SD卡上的文件夹是否存在
	 * 
	 * @param fileName
	 * @return
	 */
	public boolean isFileExist(String fileName) {
		File file = new File(getSDPATH() + fileName);
		return file.exists();
	}

	/**
	 * 将一个InputStream里面的数据写入到SD卡中
	 * 
	 * @param path
	 * @param fileName
	 * @param input
	 * @return
	 */
	public File write2SDFromInput(String path, String fileName,
			InputStream input) {
		File file = null;
		OutputStream output = null;
		try {
			createSDDir(path);
			file = createSDFile(path + fileName);
			output = new FileOutputStream(file);
			byte[] buffer = new byte[FILESIZE];
			while ((input.read(buffer)) != -1) {
				output.write(buffer);
			}
			output.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				output.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return file;
	}

	/**
	 * 将一个字符串写入到SD卡中
	 * 
	 * @param path
	 * @param fileName
	 * @param input
	 * @return
	 */
	public File write2SDFromInput(String path, String fileName, String input) {
		File file = null;
		OutputStream output = null;
		try {
			createSDDir(path);
			file = createSDFile(path + fileName);
			output = new FileOutputStream(file);
			output.write(input.getBytes());
			output.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				output.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return file;
	}
}
