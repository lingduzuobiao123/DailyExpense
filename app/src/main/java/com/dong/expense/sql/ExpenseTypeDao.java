package com.dong.expense.sql;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.dong.expense.R;
import com.dong.expense.infos.TypeInfo;

public class ExpenseTypeDao extends BaseDBDao {
	private static final String TAG = ExpenseTypeDao.class.getSimpleName();

	private static ExpenseTypeDao instance = null;

	public static ExpenseTypeDao getInstance(Context context) {
		if (instance == null) {
			instance = new ExpenseTypeDao(context);
		}
		return instance;
	}

	private ExpenseTypeDao(Context context) {
		super(context);
	}

	public static void createTypeList(Context context, SQLiteDatabase db) {
		String[] types = context.getResources().getStringArray(
				R.array.expense_type_list);
		for (int i = 0; i < types.length; i++) {
			ContentValues values = new ContentValues();
			values.put(ExpenseTypeTable.NAME, types[i]);
			values.put(ExpenseTypeTable.SORT, i);
			db.insert(ExpenseTypeTable.TABLE_NAME, null, values);
		}
	}

	/**
	 * 单条插入
	 * 
	 * @param info
	 */
	public void itemInsert(TypeInfo info) {
		ContentValues values = new ContentValues();
		values.put(ExpenseTypeTable.NAME, info.getName());
		values.put(ExpenseTypeTable.SORT, info.getSort());
		int id = info.getId();
		if (id > 0 && isExist(id)) {
			update(ExpenseTypeTable.TABLE_NAME, values, ExpenseTypeTable.ID
					+ " = ? ", new String[] { String.valueOf(id) });
		} else {
			insert(ExpenseTypeTable.TABLE_NAME, null, values);
		}
	}

	private boolean isExist(int id) {
		Log.i(TAG, "==isExist==");
		boolean isExist = false;
		Cursor cursor = query(ExpenseTypeTable.TABLE_NAME, null,
				ExpenseTypeTable.ID + " = ? ",
				new String[] { String.valueOf(id) }, null, null, null, null);
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
		delete(ExpenseTypeTable.TABLE_NAME, ExpenseTypeTable.ID + " = ?",
				new String[] { String.valueOf(id) });
	}

	/**
	 * 删除
	 */
	public void deteleAll() {
		Log.i(TAG, "==deteleItem==");
		delete(ExpenseTypeTable.TABLE_NAME, null, null);
	}

	public int totalCount() {
		Log.i(TAG, "==queryAll==");
		Cursor cursor = query(ExpenseTypeTable.TABLE_NAME, null, null, null,
				null, null, null, null);
		int count = 0;
		if (cursor != null) {
			count = cursor.getCount();
			cursor.close();
		}
		return count;
	}

	public List<TypeInfo> queryAll() {
		Log.i(TAG, "==queryAll==");
		Cursor cursor = query(ExpenseTypeTable.TABLE_NAME, null, null, null,
				null, null, ExpenseTypeTable.SORT + " ASC", null);// ExpenseTypeTable.SORT
																	// + " ASC"
		List<TypeInfo> typeList = new ArrayList<TypeInfo>();
		while (cursor != null && cursor.moveToNext()) {
			TypeInfo item = ergodicQuery(cursor);
			typeList.add(item);
		}
		if (cursor != null) {
			cursor.close();
		}
		return typeList;
	}

	/**
	 * 遍历查询
	 * 
	 * @param cursor
	 * @return
	 */
	private TypeInfo ergodicQuery(Cursor cursor) {
		TypeInfo item = new TypeInfo();
		item.setId(cursor.getInt(cursor.getColumnIndex(ExpenseTypeTable.ID)));
		item.setName(cursor.getString(cursor
				.getColumnIndex(ExpenseTypeTable.NAME)));
		item.setSort(cursor.getInt(cursor.getColumnIndex(ExpenseTypeTable.SORT)));
		return item;
	}

	class ExpenseTypeTable {
		public static final String TABLE_NAME = "expense_type_table";

		public static final String ID = "id";
		public static final String NAME = "name";// 名字
		public static final String SORT = "sort";// 排序
		public static final String EXP1 = "exp1";
		public static final String EXP2 = "exp2";
		public static final String EXP3 = "exp3";
	}
}
