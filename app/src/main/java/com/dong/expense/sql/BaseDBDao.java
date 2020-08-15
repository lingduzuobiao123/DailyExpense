package com.dong.expense.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * @author dongxl
 * 
 */
public abstract class BaseDBDao {

	protected Context mContext;
	private ExpenseDBHelper dbHelper;

	protected BaseDBDao(Context context) {
		mContext = context;
		dbHelper = new ExpenseDBHelper(mContext);
	}

	/**
	 * 插入
	 * 
	 * @param table
	 * @param columns
	 * @param values
	 * @return
	 */
	protected long insert(String table, String columns, ContentValues values) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		long index = db.insert(table, columns, values);
		db.close();
		return index;
	}

	/**
	 * 修改
	 * 
	 * @param table
	 * @param values
	 * @param where
	 * @param whereArgs
	 * @return
	 */
	protected int update(String table, ContentValues values, String where,
			String[] whereArgs) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		int index = db.update(table, values, where, whereArgs);
		db.close();
		return index;
	}

	/**
	 * 删除
	 * 
	 * @param table
	 * @param where
	 * @param whereArgs
	 * @return
	 */
	protected int delete(String table, String where, String[] whereArgs) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		int index = db.delete(table, where, whereArgs);
		db.close();
		return index;
	}

	/**
	 * 查询
	 * 
	 * @param table
	 * @param columns
	 * @param selection
	 * @param selectionArgs
	 * @param groupBy
	 * @param having
	 * @param orderBy
	 * @param limit
	 * @return
	 */
	protected Cursor query(String table, String[] columns, String selection,
			String[] selectionArgs, String groupBy, String having,
			String orderBy, String limit) {
		final SQLiteDatabase db = dbHelper.getReadableDatabase();
		return db.query(table, columns, selection, selectionArgs, groupBy,
				having, orderBy, limit);
	}

	/**
	 * @param sql 查询sql语句
	 * @return
	 */
	protected Cursor rawQuery(String sql) {
		final SQLiteDatabase db = dbHelper.getReadableDatabase();
		return db.rawQuery(sql, null);
	}

	/**
	 * 关闭数据库
	 */
	protected void closeDb() {
		if (null != dbHelper) {
			dbHelper.close();
		}
	}

}
