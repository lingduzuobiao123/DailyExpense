package com.dong.expense.utils;

import android.text.TextUtils;

public class StringUtil {

	/**
	 * 判断字符串是否为空
	 * 
	 * @param str
	 * @return true 不为空，false 为空
	 */
	public static boolean isNotNull(String str) {
		if (str != null && !TextUtils.isEmpty(str) && !"".equals(str.trim())) {
			return true;
		}
		return false;
	}

	/**
	 * 判断是否为null或空值
	 * 
	 * @param str
	 *            String
	 * @return true or false
	 */
	public static boolean isNullOrEmpty(String str) {
		return str == null || str.trim().length() == 0;
	}

	/**
	 * 判断字符串是否为空
	 * 
	 * @param str
	 */
	public static String toString(String str) {
		if (str != null && !TextUtils.isEmpty(str) && !"".equals(str.trim())) {
			return str;
		}
		return "";
	}

	public static String timeToStr(int time) {
		String timeStr = String.valueOf(time);
		if (timeStr.length() == 1) {
			timeStr = "0" + timeStr;
		}
		return timeStr;
	}

}
