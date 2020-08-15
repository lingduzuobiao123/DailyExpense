package com.dong.expense.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.widget.Toast;

public class SharedUtil {
	private static final String TAG = SharedUtil.class.getSimpleName();
	private final static String SP_NAME_MAIN = "sp_name_dailyexpense";

	private static final String MAIN_IS_GESTURE_PASSWORK = "is_gesture_passwork";
	private static final String MAIN_SET_GESTURE_PASSWORK = "set_gesture_passwork";

	public final static String SP_SCREENDENSITY_KEY = "SP_SCREENDENSITY_KEY";

	// 应用设置
	private static final String SETTING_BACKUP_TIME = "apply_setting_backup";
	private static final String APPLY_SETTING_NOTIFI = "apply_setting_notifi";
	private static final String APPLY_SETTING_UPDATE = "apply_setting_update";
	private static final String APPLY_SETTING_EXIT = "apply_setting_exit";
	private static final String APPLY_SETTING_MSG = "apply_setting_msg";
	private static final String APPLY_SETTING_WIFI = "apply_setting_wifi";

	private static final String CREATE_INSTALL_SHORTCUT = "create_install_shortcut";

	private static final String PC_RECOMMEND = "pc_recommend";// pc推手机

	private static final String SHOWGUIDE = "show_guide_6";// 是否显示引导

	/**
	 * 保存屏幕密度到share，以便widget里面使用
	 */
	public static void storeScreenDensity(Context context, float density) {
		SharedPreferences sp = context.getSharedPreferences(SP_NAME_MAIN,
				Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putFloat(SP_SCREENDENSITY_KEY, density);
		editor.commit();

	}

	/**
	 * 从share获取屏幕密度
	 */
	public static float fetchScreenDensity(Context context) {
		SharedPreferences sp = context.getSharedPreferences(SP_NAME_MAIN,
				Context.MODE_PRIVATE);
		return sp != null ? sp.getFloat(SP_SCREENDENSITY_KEY, 1) : 1;
	}

	/**
	 * 设置手机密码
	 * 
	 * @param context
	 * @param isPassworkSet
	 *            true 有手机密码，false没有
	 */
	public static void setGesturePasswork(Context context, String passwork) {
		try {
			SharedPreferences sp = context
					.getSharedPreferences(SP_NAME_MAIN, 0);
			Editor editor = sp.edit();
			editor.putBoolean(MAIN_IS_GESTURE_PASSWORK, true);
			editor.putString(MAIN_SET_GESTURE_PASSWORK, passwork);
			editor.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 是否设置手势密码
	 * 
	 * @param context
	 * @return true 有手机密码，false没有
	 */
	public static boolean isGesturePasswork(Context context) {
		boolean isPassworkSet = false;
		SharedPreferences sp = context.getSharedPreferences(SP_NAME_MAIN, 0);
		isPassworkSet = sp.getBoolean(MAIN_IS_GESTURE_PASSWORK, false);
		return isPassworkSet;
	}

	/**
	 * 得到设置手机密码
	 * 
	 * @param context
	 * @return
	 */
	public static String getGesturePasswork(Context context) {
		SharedPreferences sp = context.getSharedPreferences(SP_NAME_MAIN, 0);
		return sp.getString(MAIN_SET_GESTURE_PASSWORK, "");
	}

	/**
	 * 设置本次自动备份的时间
	 * 
	 * @param context
	 * @param time
	 */
	public static void setBackupTime(Context context, long time) {
		try {
			SharedPreferences sp = context
					.getSharedPreferences(SP_NAME_MAIN, 0);
			Editor editor = sp.edit();
			editor.putLong(SETTING_BACKUP_TIME, time);
			editor.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取上传自动备份时间
	 * 
	 * @param context
	 * @return
	 */
	public static boolean getBackupTime(Context context) {
		SharedPreferences sp = context.getSharedPreferences(SP_NAME_MAIN, 0);
		long lastTime = sp.getLong(SETTING_BACKUP_TIME, 0);
//		Toast.makeText(context, ""+(System.currentTimeMillis() - lastTime)+"===="+ConstantPool.AUTO_BACKUP_TIME, Toast.LENGTH_SHORT).show();
		if ((System.currentTimeMillis() - lastTime) > ConstantPool.AUTO_BACKUP_TIME) {
			return true;
		} else {
			return false;
		}

	}

	/**
	 * 显示状态栏通知
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isApplyNotifi(Context context) {
		boolean isChecked = false;
		SharedPreferences sp = context.getSharedPreferences(SP_NAME_MAIN, 0);
		isChecked = sp.getBoolean(APPLY_SETTING_NOTIFI, true);
		return isChecked;
	}

	/**
	 * 自动检测更新
	 * 
	 * @param context
	 * @param isChecked
	 */
	public static void setApplyUpdate(Context context, boolean isChecked) {
		try {
			SharedPreferences sp = context
					.getSharedPreferences(SP_NAME_MAIN, 0);
			Editor editor = sp.edit();
			editor.putBoolean(APPLY_SETTING_UPDATE, isChecked);
			editor.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 自动检测更新
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isApplyUpdate(Context context) {
		boolean isChecked = false;
		SharedPreferences sp = context.getSharedPreferences(SP_NAME_MAIN, 0);
		isChecked = sp.getBoolean(APPLY_SETTING_UPDATE, true);
		return isChecked;
	}

	/**
	 * 退出提醒
	 * 
	 * @param context
	 * @param isChecked
	 */
	public static void setApplyExit(Context context, boolean isChecked) {
		try {
			SharedPreferences sp = context
					.getSharedPreferences(SP_NAME_MAIN, 0);
			Editor editor = sp.edit();
			editor.putBoolean(APPLY_SETTING_EXIT, isChecked);
			editor.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 退出提醒
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isApplyExit(Context context) {
		boolean isChecked = false;
		SharedPreferences sp = context.getSharedPreferences(SP_NAME_MAIN, 0);
		isChecked = sp.getBoolean(APPLY_SETTING_EXIT, true);
		return isChecked;
	}

	/**
	 * 接收推送消息
	 * 
	 * @param context
	 * @param isChecked
	 */
	public static void setApplyMsg(Context context, boolean isChecked) {
		try {
			SharedPreferences sp = context
					.getSharedPreferences(SP_NAME_MAIN, 0);
			Editor editor = sp.edit();
			editor.putBoolean(APPLY_SETTING_MSG, isChecked);
			editor.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 接受推送消息
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isApplyMsg(Context context) {
		boolean isChecked = false;
		SharedPreferences sp = context.getSharedPreferences(SP_NAME_MAIN, 0);
		isChecked = sp.getBoolean(APPLY_SETTING_MSG, true);
		return isChecked;
	}

	/**
	 * 设为默认wifi工具
	 * 
	 * @param context
	 * @param isChecked
	 */
	public static void setApplyWIFI(Context context, boolean isChecked) {
		try {
			SharedPreferences sp = context
					.getSharedPreferences(SP_NAME_MAIN, 0);
			Editor editor = sp.edit();
			editor.putBoolean(APPLY_SETTING_WIFI, isChecked);
			editor.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 设为默认wifi工具
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isApplyWIFI(Context context) {
		boolean isChecked = false;
		SharedPreferences sp = context.getSharedPreferences(SP_NAME_MAIN, 0);
		isChecked = sp.getBoolean(APPLY_SETTING_WIFI, true);
		return isChecked;
	}

	/**
	 * 设为创建快捷方式
	 * 
	 * @param context
	 * @param isChecked
	 */
	public static void setCreateShortcut(Context context) {
		try {
			SharedPreferences sp = context
					.getSharedPreferences(SP_NAME_MAIN, 0);
			Editor editor = sp.edit();
			editor.putBoolean(CREATE_INSTALL_SHORTCUT, true);
			editor.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 是否创建快捷方式
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isCreateShortcut(Context context) {
		boolean isChecked = false;
		SharedPreferences sp = context.getSharedPreferences(SP_NAME_MAIN, 0);
		isChecked = sp.getBoolean(CREATE_INSTALL_SHORTCUT, false);
		return isChecked;
	}

	/**
	 * 是否显示引导
	 * 
	 * @param context
	 * @return true显示，false不显示
	 */
	public static boolean isShowGuide(Context context) {
		SharedPreferences sp = context.getSharedPreferences(SP_NAME_MAIN, 0);
		return sp.getBoolean(SHOWGUIDE, true);
	}

	/**
	 * 设置是否显示引导
	 * 
	 * @param context
	 * @param isChecked
	 */
	public static void setShowGuide(Context context) {
		try {
			SharedPreferences sp = context
					.getSharedPreferences(SP_NAME_MAIN, 0);
			Editor editor = sp.edit();
			editor.putBoolean(SHOWGUIDE, false);
			editor.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 是否PC推过手机
	 * 
	 * @param context
	 * @return true推过
	 */
	public static boolean isRecommend(Context context) {
		SharedPreferences sp = context.getSharedPreferences(SP_NAME_MAIN, 0);
		return sp.getBoolean(PC_RECOMMEND, false);
	}

	/**
	 * 设置PC推过手机
	 * 
	 * @param context
	 */
	public static void setRecommend(Context context) {
		try {
			SharedPreferences sp = context
					.getSharedPreferences(SP_NAME_MAIN, 0);
			Editor editor = sp.edit();
			editor.putBoolean(PC_RECOMMEND, true);
			editor.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
