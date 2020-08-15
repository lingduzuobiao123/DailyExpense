package com.dong.expense.ui.activity;

import java.text.SimpleDateFormat;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.dong.expense.R;
import com.dong.expense.adapter.ExpenseDayAdapter;
import com.dong.expense.infos.ExpenseInfo;
import com.dong.expense.logic.ApplicationPool;
import com.dong.expense.sql.ExpenseDao;
import com.dong.expense.utils.ConstantPool;
import com.dong.expense.utils.StringUtil;
import com.umeng.analytics.MobclickAgent;

public class ExpenseDayActivity extends Activity implements OnClickListener {
	private static final String TAG = ExpenseDayActivity.class.getSimpleName();
	protected static final int EXPENSE_TOTAL = 0x00300;
	protected static final int EXPENSE_LIST_QUERY = 0x00310;
	private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	private TextView mTextTitle;
	private TextView mTextRight;
	private ImageView mImageBack;
	private TextView mTextTotal;
	private ListView mListExpense;
	private ExpenseDayAdapter expenseAdapter;

	private String queryDay;
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case EXPENSE_TOTAL:
				ExpenseInfo info = (ExpenseInfo) msg.obj;
				mTextTotal.setText(String.valueOf(info.getExpenseTotal()));
				break;
			case EXPENSE_LIST_QUERY:
				List<ExpenseInfo> expenseList = (List<ExpenseInfo>) msg.obj;
				expenseAdapter.setExpenseList(expenseList);
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
		setContentView(R.layout.activity_expense_day);
		queryDay = getIntent().getStringExtra(ConstantPool.EXPENSE_DATE_QUERY);
		initView();
		setListener();
//		initData();
	}


	private void initView() {
		mTextTitle = (TextView) findViewById(R.id.text_title);
		mTextTitle.setText(R.string.expense_list_text);
		mTextRight = (TextView) findViewById(R.id.text_right);
		mImageBack = (ImageView) findViewById(R.id.image_back);
		mTextTotal = (TextView) findViewById(R.id.expense_total_text);
		mListExpense = (ListView) findViewById(R.id.expense_day_listview);
		expenseAdapter = new ExpenseDayAdapter(this);
		mListExpense.setAdapter(expenseAdapter);
	}

	private void initData() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				ExpenseDao dao = ExpenseDao
						.getInstance(ExpenseDayActivity.this);

				if (StringUtil.isNotNull(queryDay)) {
					ExpenseInfo info = dao.queryMoneyDay(queryDay);
					mHandler.sendMessage(mHandler.obtainMessage(
							EXPENSE_TOTAL, info));
					List<ExpenseInfo> expenseList = dao.queryOne(queryDay);
					mHandler.sendMessage(mHandler.obtainMessage(
							EXPENSE_LIST_QUERY, expenseList));
				} else {
					ExpenseInfo info = dao.queryMoneyAll();
					mHandler.sendMessage(mHandler.obtainMessage(
							EXPENSE_TOTAL, info));
					List<ExpenseInfo> expenseList = dao.queryAll();
					mHandler.sendMessage(mHandler.obtainMessage(
							EXPENSE_LIST_QUERY, expenseList));
				}
			}
		}).start();
	}

	private void setListener() {
		mImageBack.setOnClickListener(this);
		mListExpense.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub

			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.image_back:
			this.finish();
			break;
		// case R.id.btn_verify_lockpattern:
		// startVerifyLockPattern();
		// break;
		default:
			break;
		}
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
