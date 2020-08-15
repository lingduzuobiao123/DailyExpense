package com.dong.expense.ui.activity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.dong.expense.R;
import com.dong.expense.adapter.ExpenseSpinnerAdapter;
import com.dong.expense.infos.ExpenseInfo;
import com.dong.expense.infos.TypeInfo;
import com.dong.expense.logic.ApplicationPool;
import com.dong.expense.sql.ExpenseDao;
import com.dong.expense.sql.ExpenseTypeDao;
import com.dong.expense.utils.ConstantPool;
import com.dong.expense.utils.ExpenseUtil;
import com.dong.expense.utils.FileUtil;
import com.dong.expense.utils.SharedUtil;
import com.dong.expense.utils.StringUtil;
import com.umeng.analytics.MobclickAgent;

/**
 * 还有个保存12月以前的数据，并添加备份和恢复功能
 * 
 * @author TXWL
 * 
 */
public class MainActivity extends Activity implements OnClickListener {
	private static final String TAG = MainActivity.class.getSimpleName();
	protected static final int EXPENSE_TOTAL_TODAY = 0x00100;
	protected static final int EXPENSE_TOTAL_YESTERDAY = 0x00110;
	protected static final int EXPENSE_TOTAL_WEEK = 0x00120;
	protected static final int EXPENSE_TOTAL_MONTH = 0x00130;
	protected static final int EXPENSE_TOTAL_TIME = 0x00140;
	protected static final int EXPENSE_TERM_SELECT = 0x00150;
	private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	private Button mBtnWrite;
	private Button mBtnLook;
	private Button mBtnSetting;
	private TextView todayText;
	private TextView yesterdayText;
	private TextView weekText;
	private TextView monthText;
	private Button todayBtn, yesterdayBtn, weekBtn, monthBtn, startBtn, endBtn,
			moneyBtn, listBtn;
	private TextView moneyText;
	private Spinner termSpinner;
	private String todayDate, yesterDate;
	private long weekStart, weekEnd;
	private long monthStart, monthEnd;
	private String selectItem;
	private int selected = 0;

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case EXPENSE_TOTAL_TODAY:
				ExpenseInfo info1 = (ExpenseInfo) msg.obj;
				todayText.setText(String.valueOf(info1.getExpenseTotal()));
				break;
			case EXPENSE_TOTAL_YESTERDAY:
				ExpenseInfo info2 = (ExpenseInfo) msg.obj;
				yesterdayText.setText(String.valueOf(info2.getExpenseTotal()));
				break;
			case EXPENSE_TOTAL_WEEK:
				ExpenseInfo info3 = (ExpenseInfo) msg.obj;
				weekText.setText(String.valueOf(info3.getExpenseTotal()));
				break;
			case EXPENSE_TOTAL_MONTH:
				ExpenseInfo info4 = (ExpenseInfo) msg.obj;
				monthText.setText(String.valueOf(info4.getExpenseTotal()));
				break;
			case EXPENSE_TOTAL_TIME:
				ExpenseInfo info5 = (ExpenseInfo) msg.obj;
				timeMoneyShow(info5);
				break;
			case EXPENSE_TERM_SELECT:
				List<TypeInfo> typeList = (List<TypeInfo>) msg.obj;
				if (null == typeList) {
					typeList = new ArrayList<TypeInfo>();
				}
				TypeInfo startType = new TypeInfo();
				startType.setName("所有");
				typeList.add(0, startType);
				// TypeInfo endType = new TypeInfo();
				// endType.setName("其他");
				// typeList.add(endType);
				ExpenseSpinnerAdapter spinnerAdapter = new ExpenseSpinnerAdapter(
						MainActivity.this, typeList);
				termSpinner.setAdapter(spinnerAdapter);
				termSpinner.setSelection(selected);
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// ApplicationPool.getInstance().addActivity(this);
		setContentView(R.layout.activity_main);
		initView();
		setListener();
		// initData();
		Date date = new Date();
		String dateStr = df.format(date);
		startBtn.setText(dateStr);
		endBtn.setText(dateStr);
		checkBackup();
	}

	private void initView() {
		todayText = (TextView) findViewById(R.id.today_text);
		yesterdayText = (TextView) findViewById(R.id.yesterday_text);
		weekText = (TextView) findViewById(R.id.week_text);
		monthText = (TextView) findViewById(R.id.month_text);
		mBtnWrite = (Button) findViewById(R.id.btn_expense_write);
		mBtnLook = (Button) findViewById(R.id.btn_expense_look);
		mBtnSetting = (Button) findViewById(R.id.btn_setting);

		todayBtn = (Button) findViewById(R.id.btn_today);
		yesterdayBtn = (Button) findViewById(R.id.btn_yesterday);
		weekBtn = (Button) findViewById(R.id.btn_week);
		monthBtn = (Button) findViewById(R.id.btn_month);
		startBtn = (Button) findViewById(R.id.btn_start_date);
		endBtn = (Button) findViewById(R.id.btn_end_date);
		moneyBtn = (Button) findViewById(R.id.btn_money_time);
		listBtn = (Button) findViewById(R.id.btn_list_time);
		moneyText = (TextView) findViewById(R.id.time_money_text);
		termSpinner = (Spinner) findViewById(R.id.term_spinner);
	}

	private void initData() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				ExpenseDao dao = ExpenseDao.getInstance(MainActivity.this);
				Date date1 = new Date();
				long time = date1.getTime();
				todayDate = df.format(date1);
				ExpenseInfo info1 = dao.queryMoneyDay(todayDate);
				mHandler.sendMessage(mHandler.obtainMessage(
						EXPENSE_TOTAL_TODAY, info1));
				long time2 = time - (24 * 60 * 60 * 1000);
				Date date2 = new Date(time2);
				yesterDate = df.format(date2);
				ExpenseInfo info2 = dao.queryMoneyDay(yesterDate);
				mHandler.sendMessage(mHandler.obtainMessage(
						EXPENSE_TOTAL_YESTERDAY, info2));

				Date date3 = ExpenseUtil.dateAdd(-7);
				String dateStr3 = df.format(date3);
				Date date31 = date3;
				try {
					date31 = df.parse(dateStr3);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				weekStart = date31.getTime();
				weekEnd = time;
				ExpenseInfo info3 = dao.queryMoney(weekStart, weekEnd);// 1427624356041
				// time2
				mHandler.sendMessage(mHandler.obtainMessage(EXPENSE_TOTAL_WEEK,
						info3));
				Date date4 = ExpenseUtil.dateAdd(-30);
				String dateStr = df.format(date4);
				Date date5 = date4;
				try {
					date5 = df.parse(dateStr);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				monthStart = date5.getTime();
				monthEnd = time;
				ExpenseInfo info4 = dao.queryMoney(monthStart, monthEnd);
				mHandler.sendMessage(mHandler.obtainMessage(
						EXPENSE_TOTAL_MONTH, info4));

				ExpenseTypeDao typeDao = ExpenseTypeDao
						.getInstance(MainActivity.this);
				List<TypeInfo> typeList = typeDao.queryAll();
				mHandler.sendMessage(mHandler.obtainMessage(
						EXPENSE_TERM_SELECT, typeList));
			}
		}).start();
	}

	private void setListener() {
		mBtnWrite.setOnClickListener(this);
		mBtnLook.setOnClickListener(this);
		mBtnSetting.setOnClickListener(this);
		todayBtn.setOnClickListener(this);
		yesterdayBtn.setOnClickListener(this);
		weekBtn.setOnClickListener(this);
		monthBtn.setOnClickListener(this);
		startBtn.setOnClickListener(this);
		endBtn.setOnClickListener(this);
		moneyBtn.setOnClickListener(this);
		listBtn.setOnClickListener(this);
		termSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				selected = termSpinner.getSelectedItemPosition();
				TypeInfo typeInfo = (TypeInfo) termSpinner.getSelectedItem();
				selectItem = typeInfo.getName();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_expense_write:
			startExpenseWrite();
			break;
		case R.id.btn_expense_look:
			startExpenseLook();
			break;
		case R.id.btn_setting:
			startSetting();
			break;
		case R.id.btn_today:
			startDayLook(todayDate);
			break;
		case R.id.btn_yesterday:
			startDayLook(yesterDate);
			break;
		case R.id.btn_week:
			startTimeLook(weekStart, weekEnd, false);
			break;
		case R.id.btn_month:
			startTimeLook(monthStart, monthEnd, false);
			break;
		case R.id.btn_start_date:
			setDateDialog(startBtn);
			break;
		case R.id.btn_end_date:
			setDateDialog(endBtn);
			break;
		case R.id.btn_money_time:
			queryTimeMoney();
			break;
		case R.id.btn_list_time:
			long starTime = getStartTime(startBtn);
			long endTime = getEndTime(endBtn);
			startTimeLook(starTime, endTime, true);
			break;
		default:
			break;
		}
	}

	private long getEndTime(Button button) {
		return getStartTime(button) + 24 * 60 * 60 * 1000;
	}

	private long getStartTime(Button button) {
		String timeStr = button.getText().toString();
		Date date = null;
		try {
			date = df.parse(timeStr);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return date.getTime();
	}

	private void startSetting() {
		Intent intent = new Intent(MainActivity.this, SettingActivity.class);
		startActivity(intent);
	}

	private void startExpenseWrite() {
		Intent intent = new Intent(MainActivity.this,
				ExpenseWriteActivity.class);
		startActivity(intent);
	}

	private void startExpenseLook() {
		Intent intent = new Intent(MainActivity.this, ExpenseListActivity.class);
		startActivity(intent);
	}

	private void startDayLook(String date) {
		Intent intent = new Intent(MainActivity.this, ExpenseDayActivity.class);
		intent.putExtra(ConstantPool.EXPENSE_DATE_QUERY, date);
		startActivity(intent);
	}

	private void startTimeLook(long starTime, long endTime, boolean isTerm) {
		if (starTime > 0 && endTime > starTime) {
			Intent intent = new Intent(MainActivity.this,
					ExpenseListActivity.class);
			intent.putExtra(ConstantPool.EXPENSE_QUERY_START, starTime);
			intent.putExtra(ConstantPool.EXPENSE_QUERY_END, endTime);
			if (isTerm) {
				intent.putExtra(ConstantPool.EXPENSE_TERM_SPINNER, selectItem);
			}
			startActivity(intent);
		} else {
			Toast.makeText(this, "结束时间必须大于开始时间", Toast.LENGTH_SHORT).show();
		}
	}

	private void setDateDialog(final Button button) {
		String dateStr = button.getText().toString().toString();
		String[] dates = dateStr.split("-");
		new DatePickerDialog(this, new OnDateSetListener() {

			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				button.setText(year + "-"
						+ StringUtil.timeToStr(monthOfYear + 1) + "-"
						+ StringUtil.timeToStr(dayOfMonth));
			}
		}, Integer.parseInt(dates[0]), (Integer.parseInt(dates[1]) - 1),
				Integer.parseInt(dates[2])).show();
	}

	private void queryTimeMoney() {
		long starTime = getStartTime(startBtn);
		long endTime = getEndTime(endBtn);
		if (starTime > 0 && endTime > starTime) {
			ExpenseDao dao = ExpenseDao.getInstance(MainActivity.this);
			ExpenseInfo info = null;
			if (StringUtil.isNotNull(selectItem) && !selectItem.equals("所有")) {
				if (selectItem.equals("其他")) {
					selectItem = null;
				}
				info = dao.queryTermMoney(starTime, endTime, selectItem);
			} else {
				info = dao.queryMoney(starTime, endTime);
			}
			// mHandler.sendMessage(mHandler.obtainMessage(EXPENSE_TOTAL_TIME,
			// info));
			timeMoneyShow(info);
		} else {
			Toast.makeText(this, "结束时间必须大于开始时间", Toast.LENGTH_SHORT).show();
		}
	}

	private void timeMoneyShow(ExpenseInfo info) {
		String startStr = startBtn.getText().toString();
		String endStr = endBtn.getText().toString();
		moneyText.setText(startStr + "—" + endStr + "总共花费："
				+ String.valueOf(info.getExpenseTotal()));
	}

	/**
	 * 检测是否需要自动备份数据
	 */
	private void checkBackup() {
		if (!SharedUtil.getBackupTime(this)) {
			return;
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				ExpenseDao dao = ExpenseDao.getInstance(MainActivity.this);
				List<ExpenseInfo> list = dao.queryAll();
				String json = ExpenseUtil.listToJson(list);
				if (StringUtil.isNotNull(json)) {
					FileUtil.deleteExpense();// 保存新数据前，先删除旧的
					boolean isSuc = FileUtil.saveExpenseMsg(json);
					if (isSuc) {
						SharedUtil.setBackupTime(MainActivity.this,
								System.currentTimeMillis());
					}
				}else{
					SharedUtil.setBackupTime(MainActivity.this,
							System.currentTimeMillis());
				}
			}
		}).start();
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(TAG);
		MobclickAgent.onResume(this);
		initData();
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(TAG);
		MobclickAgent.onPause(this);
	}
}
