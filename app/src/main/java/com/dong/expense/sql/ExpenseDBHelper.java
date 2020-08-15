package com.dong.expense.sql;

import com.dong.expense.sql.ExpenseDao.ExpenseTable;
import com.dong.expense.sql.ExpenseTypeDao.ExpenseTypeTable;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * @author dongxl
 * 
 */
public class ExpenseDBHelper extends SQLiteOpenHelper {
	private static final String TAG = ExpenseDBHelper.class.getSimpleName();
	private static final String DATABASE_NAME = "daily_expense.db";// 数据库名称
	private static final int DATABASE_VERSION = 1;// 数据库版本号
	private Context mContext;

	public ExpenseDBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		mContext = context;
	}

	public ExpenseDBHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		mContext = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// createCity(db);
		createExpenseType(db);
		createExpense(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

	private void createExpense(SQLiteDatabase db) {
		StringBuilder builder = new StringBuilder();
		builder.append(" CREATE TABLE IF NOT EXISTS ");
		builder.append(ExpenseTable.TABLE_NAME);
		builder.append(" ( ");
		builder.append(ExpenseTable.ID);
		builder.append(" INTEGER PRIMARY KEY AUTOINCREMENT, ");
		builder.append(ExpenseTable.EXPENSE);
		builder.append(" FLOAT, ");
		builder.append(ExpenseTable.TYPE);
		builder.append(" INTEGER, ");
		builder.append(ExpenseTable.EXPENSETYPE);
		builder.append(" TEXT, ");
		builder.append(ExpenseTable.EXPENSETIME);
		builder.append(" LONG, ");
		builder.append(ExpenseTable.EXPENSEDATE);
		builder.append(" TEXT, ");
		builder.append(ExpenseTable.EXPLAIN);
		builder.append(" TEXT, ");
		builder.append(ExpenseTable.CREATETIME);
		builder.append(" LONG, ");
		builder.append(ExpenseTable.UPDATETIME);
		builder.append(" LONG, ");
		builder.append(ExpenseTable.EXP1);
		builder.append(" TEXT, ");
		builder.append(ExpenseTable.EXP2);
		builder.append(" TEXT, ");
		builder.append(ExpenseTable.EXP3);
		builder.append(" TEXT );");

		db.execSQL(builder.toString());
		Log.i(TAG, " db execSQL create createExpense");
	}

	/**
	 * 创建类型表
	 * 
	 * @param db
	 */
	private void createExpenseType(SQLiteDatabase db) {
		StringBuilder builder = new StringBuilder();
		builder.append(" CREATE TABLE IF NOT EXISTS ");
		builder.append(ExpenseTypeTable.TABLE_NAME);
		builder.append(" ( ");
		builder.append(ExpenseTypeTable.ID);
		builder.append(" INTEGER PRIMARY KEY AUTOINCREMENT, ");
		builder.append(ExpenseTypeTable.NAME);
		builder.append(" TEXT, ");
		builder.append(ExpenseTypeTable.SORT);
		builder.append(" INTEGER, ");
		builder.append(ExpenseTypeTable.EXP1);
		builder.append(" TEXT, ");
		builder.append(ExpenseTypeTable.EXP2);
		builder.append(" TEXT, ");
		builder.append(ExpenseTypeTable.EXP3);
		builder.append(" TEXT );");

		db.execSQL(builder.toString());
		Log.i(TAG, " db execSQL create createExpenseType");
		ExpenseTypeDao.createTypeList(mContext,db);
	}

	// /**
	// * 创建流量统计表
	// *
	// * @param db
	// */
	// private void createCounty(SQLiteDatabase db) {
	// StringBuilder builder = new StringBuilder();
	// builder.append(" CREATE TABLE IF NOT EXISTS ");
	// builder.append(CountyTable.TABLE_NAME);
	// builder.append(" ( ");
	// builder.append(CountyTable.ID);
	// builder.append(" INTEGER PRIMARY KEY AUTOINCREMENT, ");
	// builder.append(CountyTable.PROVINCE);
	// builder.append(" TEXT, ");
	// builder.append(CountyTable.CITY);
	// builder.append(" TEXT, ");
	// builder.append(CountyTable.COUNTY);
	// builder.append(" TEXT, ");
	// builder.append(CountyTable.CITYID);
	// builder.append(" TEXT, ");
	// builder.append(CountyTable.PINYIN);
	// builder.append(" TEXT, ");
	// builder.append(CountyTable.INITIAL);
	// builder.append(" TEXT, ");
	// builder.append(CountyTable.EXP1);
	// builder.append(" TEXT, ");
	// builder.append(CountyTable.EXP2);
	// builder.append(" TEXT, ");
	// builder.append(CountyTable.EXP3);
	// builder.append(" TEXT );");
	//
	// db.execSQL(builder.toString());
	// Log.i(TAG, " db execSQL create createCounty");
	// }

}
