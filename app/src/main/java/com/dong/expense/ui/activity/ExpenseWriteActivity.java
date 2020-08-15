package com.dong.expense.ui.activity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.dong.expense.R;
import com.dong.expense.adapter.ExpenseTypeAdapter;
import com.dong.expense.infos.ExpenseInfo;
import com.dong.expense.infos.TypeInfo;
import com.dong.expense.logic.ApplicationPool;
import com.dong.expense.sql.ExpenseDao;
import com.dong.expense.sql.ExpenseTypeDao;
import com.dong.expense.utils.ConstantPool;
import com.dong.expense.utils.StringUtil;
import com.umeng.analytics.MobclickAgent;

public class ExpenseWriteActivity extends Activity implements OnClickListener {
	private static final String TAG = ExpenseWriteActivity.class
			.getSimpleName();
	protected static final int EXPENSE_WRITE_INIT = 0x00400;
	protected static final int EXPENSE_WRITE_SAVE = 0x00410;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-ddHH:mm");
	private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat tf = new SimpleDateFormat("HH:mm");
	private TextView mTextTitle;
	private TextView mTextRight;
	private ImageView mImageBack;

	private EditText mEditMoney;
	private EditText mEditExplain;
	private GridView typeGrid;
	private Button mBtnDate;
	private Button mBtnTime;
	private ExpenseInfo expenseInfo;
	private boolean isUpdate;// 是否修改，ture修改
	private List<TypeInfo> typeList;
	private ExpenseTypeAdapter typeAdapter;
	private DatePickerDialog dateDialog;
	private TimePickerDialog timeDialog;
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case EXPENSE_WRITE_INIT:
				initData();
				break;
			case EXPENSE_WRITE_SAVE:
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
//		ApplicationPool.getInstance().addActivity(this);
		setContentView(R.layout.activity_expense_write);
		expenseInfo = (ExpenseInfo) getIntent().getSerializableExtra(
				ConstantPool.UPDATE_EXPENSE_MSG);
		initView();
		setListener();
		new Thread(new Runnable() {
			@Override
			public void run() {
				ExpenseTypeDao dao = ExpenseTypeDao
						.getInstance(ExpenseWriteActivity.this);
				typeList = dao.queryAll();
				mHandler.sendEmptyMessage(EXPENSE_WRITE_INIT);
			}
		}).start();
	}

	private void initView() {
		mTextTitle = (TextView) findViewById(R.id.text_title);
		mTextRight = (TextView) findViewById(R.id.text_right);
		mTextRight.setText(R.string.expense_save_text);
		mTextRight.setVisibility(View.VISIBLE);
		mImageBack = (ImageView) findViewById(R.id.image_back);
		mEditMoney = (EditText) findViewById(R.id.edit_expense_money);
		mEditExplain = (EditText) findViewById(R.id.edit_explain);
		typeGrid = (GridView) findViewById(R.id.expense_type_view);
		mBtnDate = (Button) findViewById(R.id.btn_set_date);
		mBtnTime = (Button) findViewById(R.id.btn_set_time);
	}

	private void initData() {
		List<TypeInfo> list = new ArrayList<TypeInfo>();
		typeGrid.setNumColumns(5);
		int lenght = 10;
		if (typeList.size() > lenght) {
			list.addAll(typeList.subList(0, (lenght - 1)));
			TypeInfo info = new TypeInfo();
			info.setMore(true);
			info.setName("更多");
			list.add(info);
		} else {
			list.addAll(typeList);
		}
		typeAdapter = new ExpenseTypeAdapter(this, list);
		typeGrid.setAdapter(typeAdapter);

		if (null != expenseInfo && expenseInfo.getId() > 0) {
			isUpdate = true;
			mTextTitle.setText(R.string.expense_update_text);
			mEditMoney.setText(String.valueOf(expenseInfo.getExpense()));
			mEditExplain.setText(expenseInfo.getExplain());
			Date date = new Date(expenseInfo.getExpenseTime());
			mBtnDate.setText(df.format(date));
			mBtnTime.setText(tf.format(date));
			String type = expenseInfo.getExpenseType();
			for (int i = 0; i < list.size(); i++) {
				TypeInfo info = list.get(i);
				if (info.getName().equals(type)) {
					typeAdapter.setSelected(i);
					break;
				}
			}
		} else {
			isUpdate = false;
			expenseInfo = new ExpenseInfo();
			mTextTitle.setText(R.string.expense_add_text);
			Date date = new Date();
			mBtnDate.setText(df.format(date));
			mBtnTime.setText(tf.format(date));
		}
	}

	private void setListener() {
		mImageBack.setOnClickListener(this);
		mTextRight.setOnClickListener(this);
		mBtnDate.setOnClickListener(this);
		mBtnTime.setOnClickListener(this);
		typeGrid.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				TypeInfo info = (TypeInfo) typeAdapter.getItem(position);
				if (info.isMore()) {
					typeAdapter.setSelected(-1);
				} else {
					typeAdapter.setSelected(position);
				}
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.image_back:
			this.finish();
			break;
		case R.id.text_right:
			saveExpense();
			break;
		case R.id.btn_set_date:
			setDateDialog();
			break;
		case R.id.btn_set_time:
			setTimeDialog();
			break;
		default:
			break;
		}
	}

	private void setDateDialog() {
		String dateStr = mBtnDate.getText().toString().toString();
		String[] dates = dateStr.split("-");
		new DatePickerDialog(this, new OnDateSetListener() {

			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				mBtnDate.setText(year + "-"
						+ StringUtil.timeToStr(monthOfYear + 1) + "-"
						+ StringUtil.timeToStr(dayOfMonth));
			}
		}, Integer.parseInt(dates[0]), (Integer.parseInt(dates[1]) - 1),
				Integer.parseInt(dates[2])).show();

	}

	private void setTimeDialog() {
		String timeStr = mBtnTime.getText().toString().toString();
		String[] times = timeStr.split(":");
		new TimePickerDialog(this, new OnTimeSetListener() {

			@Override
			public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
				mBtnTime.setText(StringUtil.timeToStr(hourOfDay) + ":"
						+ StringUtil.timeToStr(minute));
			}
		}, Integer.parseInt(times[0]), Integer.parseInt(times[1]), true).show();

	}

	/**
	 * 保存
	 */
	private void saveExpense() {
		System.out.println("==saveExpense==");
		long time = System.currentTimeMillis();
		String moneyStr = mEditMoney.getText().toString();
		if (!StringUtil.isNotNull(moneyStr)) {
			// 提示输入金额
			Toast.makeText(this, "请输入金额", Toast.LENGTH_SHORT).show();
			return;
		}
		float money = 0;
		try {
			money = Float.parseFloat(moneyStr);
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (money == 0) {
			// 提示输入金额
			Toast.makeText(this, "请输入正确金额", Toast.LENGTH_SHORT).show();
			return;
		}
		String dateStr = mBtnDate.getText().toString().trim();
		String timeStr = dateStr + mBtnTime.getText().toString();
		long expenseTime = 0;
		try {
			Date date = sdf.parse(timeStr);
			expenseTime = date.getTime();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (expenseTime == 0
				|| (expenseTime / 1000 / 60) < (time / 1000 / 60 - 365 * 24 * 60)) {
			Toast.makeText(this, "日期必须在12月以内", Toast.LENGTH_SHORT).show();
			return;
		}

		if ((expenseTime / 1000 / 60) > (time / 1000 / 60)) {
			Toast.makeText(this, "时间不能大于当前时间", Toast.LENGTH_SHORT).show();
			return;
		}
		expenseInfo.setExpenseTime(expenseTime);
		expenseInfo.setExpenseDate(dateStr);
		int position = typeAdapter.getSelected();
		if (position > -1) {
			TypeInfo info = (TypeInfo) typeAdapter.getItem(position);
			expenseInfo.setExpenseType(info.getName());
		}
		expenseInfo.setExpense(money);
		expenseInfo.setType(1);
		String explain = mEditExplain.getText().toString();
		if (StringUtil.isNotNull(explain)) {
			expenseInfo.setExplain(explain);
		}

		ExpenseDao dao = ExpenseDao.getInstance(this);
		dao.itemUpdate(expenseInfo, time);
		Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
		this.finish();
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(TAG);
		MobclickAgent.onResume(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(TAG);
		MobclickAgent.onPause(this);
	}
}
