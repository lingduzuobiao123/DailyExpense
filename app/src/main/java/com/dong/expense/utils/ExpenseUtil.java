package com.dong.expense.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.dong.expense.infos.ExpenseInfo;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

public class ExpenseUtil {
	private static final String TAG = ExpenseUtil.class.getSimpleName();
	public static float screenDensity = -1;
	private static int displayWidth = 0;
	private static int displayHeight = 0;
	private static int statusBarHeight = 0;

	// 获取屏幕宽度
	public static int getDisplayWidth(Context context) {
		if (displayWidth <= 0) {
			WindowManager wm = (WindowManager) context.getApplicationContext()
					.getSystemService(Context.WINDOW_SERVICE);
			DisplayMetrics dm = new DisplayMetrics();
			wm.getDefaultDisplay().getMetrics(dm);
			displayWidth = dm.widthPixels;
		}
		return displayWidth;
	}

	// 获取屏幕高度
	public static int getDisplayHeight(Context context) {
		if (displayHeight <= 0) {
			WindowManager wm = (WindowManager) context.getApplicationContext()
					.getSystemService(Context.WINDOW_SERVICE);
			DisplayMetrics dm = new DisplayMetrics();
			wm.getDefaultDisplay().getMetrics(dm);
			displayHeight = dm.heightPixels;
		}
		return displayHeight;
	}

	/**
	 * 获得状态栏的高
	 * 
	 * @param context
	 * @return
	 */
	public static int getStatusBarHeight(Context context) {
		if (statusBarHeight <= 0) {
			try {
				Class<?> c = Class.forName("com.android.internal.R$dimen");
				Object obj = c.newInstance();
				Field field = c.getField("status_bar_height");
				int x = Integer.parseInt(field.get(obj).toString());
				statusBarHeight = context.getResources().getDimensionPixelSize(
						x);
			} catch (Exception e) {
				LogUtils.LOGE(TAG, "get status bar height fail");
			}
		}
		return statusBarHeight;
	}

	/**
	 * 获得屏幕密度
	 * 
	 * @param context
	 * @return
	 */
	public static float getScreenDensity(Context context) {
		if (screenDensity <= 0) {
			WindowManager wm = (WindowManager) context.getApplicationContext()
					.getSystemService(Context.WINDOW_SERVICE);
			DisplayMetrics dm = new DisplayMetrics();
			wm.getDefaultDisplay().getMetrics(dm);
			screenDensity = dm.density;
		}
		SharedUtil.storeScreenDensity(context, screenDensity);
		return screenDensity;
	}

	/**
	 * 获取屏幕分辨率
	 * 
	 * @param context
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static int[] getScreenDispaly(Context context) {
		WindowManager windowManager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		int width = windowManager.getDefaultDisplay().getWidth();// 手机屏幕的宽度
		int height = windowManager.getDefaultDisplay().getHeight();// 手机屏幕的高度
		int result[] = { width, height };
		return result;
	}

	/*
	 * 将当前日期加减n天数。 如传入整型-5 意为将当前日期减去5天的日期 如传入整型5 意为将当前日期加上5天后的日期 返回字串
	 * 例(19990203)
	 */
	public static Date dateAdd(int days) {
		// 日期处理模块 (将日期加上某些天或减去天数)返回字符串
		Calendar canlendar = Calendar.getInstance(); // java.util包
		canlendar.add(Calendar.DATE, days); // 日期减 如果不够减会将月变动
		return canlendar.getTime();
	}
	
	public static String listToJson(List<ExpenseInfo> list) {
		if (null == list || list.size() == 0) {
			return "";
		}
		JSONObject json = new JSONObject();
		try {
			JSONArray ja = new JSONArray();
			for (ExpenseInfo info : list) {
				JSONObject jo = new JSONObject();
				jo.put("id", info.getId());
				jo.put("expense", info.getExpense());
				jo.put("type", info.getType());
				jo.put("expenseType", info.getExpenseType());
				jo.put("expenseTime", info.getExpenseTime());
				jo.put("expenseDate", info.getExpenseDate());
				jo.put("explain", info.getExplain());
				jo.put("createTime", info.getCreateTime());
				jo.put("updateTime", info.getUpdateTime());
				ja.put(jo);
			}
			json.put("expenseInfo", ja);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json.toString();
	}

	public static List<ExpenseInfo> jsonToList(String json) {
		List<ExpenseInfo> list = new ArrayList<ExpenseInfo>();
		if (!StringUtil.isNotNull(json)) {
			return list;
		}
		try {
			JSONArray ja = new JSONObject(json).optJSONArray("expenseInfo");
			for (int i = 0; i < ja.length(); i++) {
				JSONObject jo = ja.optJSONObject(i);
				ExpenseInfo info = new ExpenseInfo();
				info.setId(jo.optInt("id"));
				info.setExpense((float) jo.optDouble("expense"));
				info.setType(jo.optInt("type"));
				info.setExpenseTime(jo.optLong("expenseTime"));
				info.setExpenseDate(jo.optString("expenseDate"));
				info.setExpenseType(jo.optString("expenseType"));
				info.setExplain(jo.optString("explain"));
				info.setCreateTime(jo.optLong("createTime"));
				info.setUpdateTime(jo.optLong("updateTime"));
				list.add(info);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}
}
