package com.dong.expense.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.os.Environment;

public class FileUtil {
	private static final String TAG = FileUtil.class.getSimpleName();
	public static final String EXPENSE_FILE_PATH = "/DailyExpense/expense/";
	public static final String EXPENSE_FILE_NAME = "expense.txt";

	/**
	 * 保存
	 * @param str
	 * @return
	 */
	public static boolean saveExpenseMsg(String str) {
		if (!isSDCardAvaliable()) {
			return false;
		}
		String filepath = Environment.getExternalStorageDirectory()
				+ EXPENSE_FILE_PATH;
		if (!new File(filepath).exists()) {
			LogUtils.LOGW("getApkFile", "mkdirs");
			new File(filepath).mkdirs();
		}
		File file = new File(filepath + EXPENSE_FILE_NAME);
		FileOutputStream outStream = null;
		try {
			file.createNewFile();
			outStream = new FileOutputStream(file);
			outStream.write(str.getBytes());
			return true;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (null != outStream) {
				try {
					outStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return false;
	}

	/**
	 * 读取以保存的
	 * @return
	 */
	public static String readExpenseFile() {
		if (!isSDCardAvaliable()) {
			return "";
		}
		String filepath = Environment.getExternalStorageDirectory()
				+ EXPENSE_FILE_PATH;
		File file = new File(filepath + EXPENSE_FILE_NAME);
		if (!file.exists()) {
			return "";
		}
		String str = "";
		FileInputStream inStream = null;
		ByteArrayOutputStream stream = null;
		try {
			inStream = new FileInputStream(file);
			stream = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int length = -1;
			while ((length = inStream.read(buffer)) != -1) {
				stream.write(buffer, 0, length);
			}
			str = stream.toString();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (null != stream) {
					stream.close();
				}
				if (null != inStream) {
					inStream.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return str;
	}

	/**
	 * 再次保存前先删除之前的
	 */
	public static void deleteExpense() {
		if (!isSDCardAvaliable()) {
			return;
		}
		String filepath = Environment.getExternalStorageDirectory()
				+ EXPENSE_FILE_PATH;
		File file = new File(filepath + EXPENSE_FILE_NAME);
		if (!file.exists()) {
			return;
		}
		file.delete();
	}

	public static boolean isExpenseExist() {
		if (!isSDCardAvaliable()) {
			return false;
		}
		String filepath = Environment.getExternalStorageDirectory()
				+ EXPENSE_FILE_PATH;
		File file = new File(filepath + EXPENSE_FILE_NAME);
		return file.exists();
	}

	public static boolean isSDCardAvaliable() {
		LogUtils.LOGE(TAG, "===isSDCardAvaliable====");
		return Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED);
	}
}
