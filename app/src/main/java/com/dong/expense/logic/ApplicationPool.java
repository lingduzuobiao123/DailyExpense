package com.dong.expense.logic;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;

import com.dong.expense.utils.LogUtils;
import com.umeng.analytics.MobclickAgent;

public class ApplicationPool extends Application {
	private final static String TAG = ApplicationPool.class.getSimpleName();
	private static ApplicationPool mInstance = null;
	private List<Activity> activityList = new ArrayList<Activity>();
	private String channelID;// 渠道ID

	@Override
	public void onCreate() {
		super.onCreate();
		mInstance = this;

		// // MobclickAgent.setDebugMode(true);
		// MobclickAgent.openActivityDurationTrack(false);//
		// 禁止默认的页面统计方式，这样将不会再自动统计Activity。
		// MobclickAgent.updateOnlineConfig(this);
		// MobclickAgent.setCatchUncaughtExceptions(true);
	}

	public static ApplicationPool getInstance() {
		return mInstance;
	}

	/**
	 * 获取渠道ID
	 * 
	 * @return
	 */
	public String getChannelId() {
		if (channelID == null || channelID.equals("")
				|| channelID.equals("null")) {
			try {
				ApplicationInfo appInfo = this.getPackageManager()
						.getApplicationInfo(getPackageName(),
								PackageManager.GET_META_DATA);
				Object channel = appInfo.metaData.get("UMENG_CHANNEL");
				channelID = String.valueOf(channel);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		LogUtils.LOGI(TAG, "==getChannelId==" + channelID);
		return channelID;
	}

	public void clearActivity() {
		LogUtils.LOGV(TAG, "clearActivity...");
		for (Activity activity : activityList) {
			if (null != activity) {
				activity.finish();
				activity = null;
			}
		}
		activityList.clear();
	}

	public void addActivity(Activity activity) {
		LogUtils.LOGV(TAG, "addActivity...");
		if (activity != null) {
			activityList.add(activity);
		}
	}

	/**
	 * @return the main context of the Application
	 */
	public static Context getAppContext() {
		return mInstance;
	}

	/**
	 * @return the main resources from the Application
	 */
	public static Resources getAppResources() {
		if (mInstance == null)
			return null;
		return mInstance.getResources();
	}

}
