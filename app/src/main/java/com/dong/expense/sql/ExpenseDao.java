package com.dong.expense.sql;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.dong.expense.infos.ExpenseInfo;

public class ExpenseDao extends BaseDBDao {
	private static final String TAG = ExpenseDao.class.getSimpleName();

	private static ExpenseDao instance = null;

	public static ExpenseDao getInstance(Context context) {
		if (instance == null) {
			instance = new ExpenseDao(context);
		}
		return instance;
	}

	private ExpenseDao(Context context) {
		super(context);
	}

	/**
	 * 单条插入
	 * 
	 * @param info
	 */
	public void itemInsert(ExpenseInfo info) {
		ContentValues values = new ContentValues();
		values.put(ExpenseTable.EXPENSE, info.getExpense());
		values.put(ExpenseTable.TYPE, info.getType());
		values.put(ExpenseTable.EXPENSETYPE, info.getExpenseType());
		values.put(ExpenseTable.EXPENSETIME, info.getExpenseTime());
		values.put(ExpenseTable.EXPENSEDATE, info.getExpenseDate());
		values.put(ExpenseTable.EXPLAIN, info.getExplain());
		values.put(ExpenseTable.UPDATETIME, info.getUpdateTime());
		values.put(ExpenseTable.CREATETIME, info.getCreateTime());
		int id = info.getId();
		if (id > 0 && isExist(id)) {
			update(ExpenseTable.TABLE_NAME, values, ExpenseTable.ID + " = ? ",
					new String[] { String.valueOf(id) });
		} else {
			insert(ExpenseTable.TABLE_NAME, null, values);
		}
	}

	/**
	 * 修改
	 * 
	 * @param info
	 * @param time
	 */
	public void itemUpdate(ExpenseInfo info, long time) {
		ContentValues values = new ContentValues();
		values.put(ExpenseTable.EXPENSE, info.getExpense());
		values.put(ExpenseTable.TYPE, info.getType());
		values.put(ExpenseTable.EXPENSETYPE, info.getExpenseType());
		values.put(ExpenseTable.EXPENSETIME, info.getExpenseTime());
		values.put(ExpenseTable.EXPENSEDATE, info.getExpenseDate());
		values.put(ExpenseTable.EXPLAIN, info.getExplain());
		int id = info.getId();
		if (id > 0) {
			values.put(ExpenseTable.UPDATETIME, time);
			update(ExpenseTable.TABLE_NAME, values, ExpenseTable.ID + " = ? ",
					new String[] { String.valueOf(id) });
		} else {
			values.put(ExpenseTable.CREATETIME, time);
			insert(ExpenseTable.TABLE_NAME, null, values);
		}
	}

	private boolean isExist(int id) {
		Log.i(TAG, "==isExist==");
		boolean isExist = false;
		Cursor cursor = query(ExpenseTable.TABLE_NAME, null, ExpenseTable.ID
				+ " = ? ", new String[] { String.valueOf(id) }, null, null,
				null, null);
		if (null == cursor || cursor.getCount() == 0) {
			return isExist;
		}
		int count = cursor.getCount();
		cursor.close();
		Log.i(TAG, "==isExistMac==count==" + count);
		if (count == 1) {
			isExist = true;
		} else {
			deteleItem(id);
		}
		return isExist;
	}

	/**
	 * 删除
	 */
	public void deteleItem(int id) {
		Log.i(TAG, "==deteleItem==");
		delete(ExpenseTable.TABLE_NAME, ExpenseTable.ID + " = ?",
				new String[] { String.valueOf(id) });
	}

	/**
	 * 删除
	 */
	public void deteleAll() {
		Log.i(TAG, "==deteleItem==");
		delete(ExpenseTable.TABLE_NAME, null, null);
	}

	/**
	 * 查询 某个时间段的 按条查询
	 * 
	 * @param startTime
	 *            开始时间
	 * @param endTime
	 *            结束时间
	 * @return
	 */
	public List<ExpenseInfo> queryTime(long startTime, long endTime) {
		Log.i(TAG, "==queryTime==");
		Cursor cursor = query(ExpenseTable.TABLE_NAME, null,
				ExpenseTable.EXPENSETIME + " >= " + startTime + " AND "
						+ ExpenseTable.EXPENSETIME + " < " + endTime, null,
				null, null, null, null);
		List<ExpenseInfo> expenseList = new ArrayList<ExpenseInfo>();
		while (cursor != null && cursor.moveToNext()) {
			ExpenseInfo item = ergodicQuery(cursor);
			expenseList.add(item);
		}
		if (cursor != null) {
			Log.e(TAG, "==queryAll==count===" + cursor.getCount());
			cursor.close();
		}
		return expenseList;
	}

	/**
	 * 查询某个时间段 按日查询
	 * 
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public List<ExpenseInfo> queryTime2(long startTime, long endTime) {
		Log.i(TAG, "==queryTime2==");
		Cursor cursor = query(ExpenseTable.TABLE_NAME, new String[] {
				ExpenseTable.EXPENSEDATE,
				"SUM(" + ExpenseTable.EXPENSE + ") AS expenseTotal" },
				ExpenseTable.EXPENSETIME + " >= " + startTime + " AND "
						+ ExpenseTable.EXPENSETIME + " < " + endTime, null,
				ExpenseTable.EXPENSEDATE, null, ExpenseTable.EXPENSEDATE
						+ " DESC", null);
		List<ExpenseInfo> expenseList = new ArrayList<ExpenseInfo>();
		while (cursor != null && cursor.moveToNext()) {
			ExpenseInfo item = new ExpenseInfo();
			item.setExpenseTotal(cursor.getFloat(cursor
					.getColumnIndex("expenseTotal")));
			String date = cursor.getString(cursor
					.getColumnIndex(ExpenseTable.EXPENSEDATE));
			item.setExpenseDate(date);
			List<ExpenseInfo> list = queryOne(date);
			item.setExpenseList(list);
			expenseList.add(item);
		}
		if (cursor != null) {
			Log.e(TAG, "==queryTime2==count===" + cursor.getCount());
			cursor.close();
		}
		return expenseList;
	}

	public List<ExpenseInfo> queryTermTime2(long starTime, long endTime,
			String selectItem) {
		Log.i(TAG, "==queryTermTime2==");
		Cursor cursor = query(ExpenseTable.TABLE_NAME, new String[] {
				ExpenseTable.EXPENSEDATE,
				"SUM(" + ExpenseTable.EXPENSE + ") AS expenseTotal" },
				ExpenseTable.EXPENSETIME + " >= " + starTime + " AND "
						+ ExpenseTable.EXPENSETIME + " < " + endTime + " AND "
						+ ExpenseTable.EXPENSETYPE + " = '" + selectItem + "'",
				null, ExpenseTable.EXPENSEDATE, null, ExpenseTable.EXPENSEDATE
						+ " DESC", null);
		List<ExpenseInfo> expenseList = new ArrayList<ExpenseInfo>();
		while (cursor != null && cursor.moveToNext()) {
			ExpenseInfo item = new ExpenseInfo();
			item.setExpenseTotal(cursor.getFloat(cursor
					.getColumnIndex("expenseTotal")));
			String date = cursor.getString(cursor
					.getColumnIndex(ExpenseTable.EXPENSEDATE));
			item.setExpenseDate(date);
			List<ExpenseInfo> list = queryTermOne(date, selectItem);
			item.setExpenseList(list);
			expenseList.add(item);
		}
		if (cursor != null) {
			Log.e(TAG, "==queryTermTime2==count===" + cursor.getCount());
			cursor.close();
		}
		return expenseList;
	}

	/**
	 * 查询某一天 按条查询
	 * 
	 * @param date
	 * @return
	 */
	public List<ExpenseInfo> queryOne(String date) {
		Log.i(TAG, "==queryOne==");
		Cursor cursor = query(ExpenseTable.TABLE_NAME, null,
				ExpenseTable.EXPENSEDATE + " = ?", new String[] { date }, null,
				null, null, null);
		List<ExpenseInfo> expenseList = new ArrayList<ExpenseInfo>();
		while (cursor != null && cursor.moveToNext()) {
			ExpenseInfo item = ergodicQuery(cursor);
			expenseList.add(item);
		}
		if (cursor != null) {
			cursor.close();
		}
		return expenseList;
	}

	/**
	 * 查询某一天 按条查询
	 * 
	 * @param date
	 * @return
	 */
	public List<ExpenseInfo> queryTermOne(String date, String selectItem) {
		Log.i(TAG, "==queryOne==");
		Cursor cursor = query(ExpenseTable.TABLE_NAME, null,
				ExpenseTable.EXPENSEDATE + " = '" + date + "' AND "
						+ ExpenseTable.EXPENSETYPE + " = '" + selectItem + "'",
				null, null, null, null, null);
		List<ExpenseInfo> expenseList = new ArrayList<ExpenseInfo>();
		while (cursor != null && cursor.moveToNext()) {
			ExpenseInfo item = ergodicQuery(cursor);
			expenseList.add(item);
		}
		if (cursor != null) {
			cursor.close();
		}
		return expenseList;
	}

	/**
	 * 查询总共 消费金额
	 * 
	 */
	public ExpenseInfo queryMoneyAll() {
		Log.i(TAG, "==queryMoneyDay==");
		Cursor cursor = query(ExpenseTable.TABLE_NAME, new String[] { "SUM("
				+ ExpenseTable.EXPENSE + ") AS expenseTotal" }, null, null,
				null, null, null, null);
		ExpenseInfo item = null;
		while (cursor != null && cursor.moveToNext()) {
			item = new ExpenseInfo();
			item.setExpenseTotal(cursor.getFloat(cursor
					.getColumnIndex("expenseTotal")));
		}
		if (cursor != null) {
			cursor.close();
		}
		return item;
	}

	/**
	 * 查询 某天 消费金额
	 * 
	 * @param startTime
	 *            开始时间
	 * @param endTime
	 *            结束时间
	 * @return
	 */
	public ExpenseInfo queryMoneyDay(String date) {
		Log.i(TAG, "==queryMoneyDay==");
		Cursor cursor = query(ExpenseTable.TABLE_NAME, new String[] { "SUM("
				+ ExpenseTable.EXPENSE + ") AS expenseTotal" },
				ExpenseTable.EXPENSEDATE + " = ?", new String[] { date }, null,
				null, null, null);
		ExpenseInfo item = null;
		while (cursor != null && cursor.moveToNext()) {
			item = new ExpenseInfo();
			item.setExpenseTotal(cursor.getFloat(cursor
					.getColumnIndex("expenseTotal")));
		}
		if (cursor != null) {
			cursor.close();
		}
		return item;
	}

	/**
	 * 查询 某个时间段的 消费金额
	 * 
	 * @param startTime
	 *            开始时间
	 * @param endTime
	 *            结束时间
	 * @return
	 */
	public ExpenseInfo queryMoney(long startTime, long endTime) {
		Log.i(TAG, "==queryMoney==");
		Cursor cursor = query(ExpenseTable.TABLE_NAME, new String[] { "SUM("
				+ ExpenseTable.EXPENSE + ") AS expenseTotal" },
				ExpenseTable.EXPENSETIME + " >= " + startTime + " AND "
						+ ExpenseTable.EXPENSETIME + " < " + endTime, null,
				null, null, null, null);
		ExpenseInfo item = null;
		while (cursor != null && cursor.moveToNext()) {
			item = new ExpenseInfo();
			item.setExpenseTotal(cursor.getFloat(cursor
					.getColumnIndex("expenseTotal")));
			item.setExpenseTime(startTime);
		}
		if (cursor != null) {
			cursor.close();
		}
		return item;
	}

	public ExpenseInfo queryTermMoney(long starTime, long endTime,
			String selectItem) {
		Log.i(TAG, "==queryTermMoney==");
		Cursor cursor = query(ExpenseTable.TABLE_NAME, new String[] { "SUM("
				+ ExpenseTable.EXPENSE + ") AS expenseTotal" },
				ExpenseTable.EXPENSETIME + " >= " + starTime + " AND "
						+ ExpenseTable.EXPENSETIME + " < " + endTime + " AND "
						+ ExpenseTable.EXPENSETYPE + " = '" + selectItem + "'",
				null, null, null, null, null);
		ExpenseInfo item = null;
		while (cursor != null && cursor.moveToNext()) {
			item = new ExpenseInfo();
			item.setExpenseTotal(cursor.getFloat(cursor
					.getColumnIndex("expenseTotal")));
			item.setExpenseTime(starTime);
		}
		if (cursor != null) {
			cursor.close();
		}
		return item;
	}

	/**
	 * 查询所有按条查询
	 * 
	 * @return
	 */
	public List<ExpenseInfo> queryAll() {
		Log.i(TAG, "==queryAll==");
		Cursor cursor = query(ExpenseTable.TABLE_NAME, null, null, null, null,
				null, null, null);
		List<ExpenseInfo> expenseList = new ArrayList<ExpenseInfo>();
		while (cursor != null && cursor.moveToNext()) {
			ExpenseInfo item = ergodicQuery(cursor);
			expenseList.add(item);
		}
		if (cursor != null) {
			cursor.close();
		}
		return expenseList;
	}

	/**
	 * 查询所有，按日期查询
	 * 
	 * @return
	 */
	public List<ExpenseInfo> queryAll2() {
		Log.i(TAG, "==queryTime2==");
		Cursor cursor = query(ExpenseTable.TABLE_NAME, new String[] {
				ExpenseTable.EXPENSEDATE,
				"SUM(" + ExpenseTable.EXPENSE + ") AS expenseTotal" }, null,
				null, ExpenseTable.EXPENSEDATE, null, ExpenseTable.EXPENSEDATE
						+ " DESC", null);
		List<ExpenseInfo> expenseList = new ArrayList<ExpenseInfo>();
		while (cursor != null && cursor.moveToNext()) {
			ExpenseInfo item = new ExpenseInfo();
			item.setExpenseTotal(cursor.getFloat(cursor
					.getColumnIndex("expenseTotal")));
			String date = cursor.getString(cursor
					.getColumnIndex(ExpenseTable.EXPENSEDATE));
			item.setExpenseDate(date);
			List<ExpenseInfo> list = queryOne(date);
			item.setExpenseList(list);
			expenseList.add(item);
		}
		if (cursor != null) {
			Log.e(TAG, "==queryAll2==count===" + cursor.getCount());
			cursor.close();
		}
		return expenseList;
	}

	/**
	 * 遍历查询
	 * 
	 * @param cursor
	 * @return
	 */
	private ExpenseInfo ergodicQuery(Cursor cursor) {
		ExpenseInfo item = new ExpenseInfo();
		item.setId(cursor.getInt(cursor.getColumnIndex(ExpenseTable.ID)));
		item.setExpense(cursor.getFloat(cursor
				.getColumnIndex(ExpenseTable.EXPENSE)));
		item.setType(cursor.getInt(cursor.getColumnIndex(ExpenseTable.TYPE)));
		item.setExpenseType(cursor.getString(cursor
				.getColumnIndex(ExpenseTable.EXPENSETYPE)));
		item.setExpenseTime(cursor.getLong(cursor
				.getColumnIndex(ExpenseTable.EXPENSETIME)));
		item.setExpenseDate(cursor.getString(cursor
				.getColumnIndex(ExpenseTable.EXPENSEDATE)));
		item.setExplain(cursor.getString(cursor
				.getColumnIndex(ExpenseTable.EXPLAIN)));
		item.setCreateTime(cursor.getLong(cursor
				.getColumnIndex(ExpenseTable.CREATETIME)));
		item.setUpdateTime(cursor.getLong(cursor
				.getColumnIndex(ExpenseTable.UPDATETIME)));
		return item;
	}

	class ExpenseTable {
		public static final String TABLE_NAME = "daily_expense_table";

		public static final String ID = "id";
		public static final String EXPENSE = "expense";// 金额
		public static final String TYPE = "type";// 1支出 2收入
		public static final String EXPENSETYPE = "expenseType";// 消费类型
		public static final String EXPENSETIME = "expenseTime";// 消费时间
		public static final String EXPENSEDATE = "expenseDate";// 消费日期
		public static final String EXPLAIN = "explain";// 说明
		public static final String CREATETIME = "createTime";// 当前时间
		public static final String UPDATETIME = "updateTime";// 修改时间
		public static final String EXP1 = "exp1";
		public static final String EXP2 = "exp2";
		public static final String EXP3 = "exp3";
	}

}
