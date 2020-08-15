package com.dong.expense.ui.activity;

import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dong.expense.R;
import com.dong.expense.infos.ExpenseInfo;
import com.dong.expense.logic.ApplicationPool;
import com.dong.expense.sql.ExpenseDao;
import com.dong.expense.ui.view.GestureContentView;
import com.dong.expense.ui.view.GestureDrawline.GestureCallBack;
import com.dong.expense.ui.view.LockIndicator;
import com.dong.expense.utils.ConstantPool;
import com.dong.expense.utils.ExpenseUtil;
import com.dong.expense.utils.FileUtil;
import com.dong.expense.utils.MD5;
import com.dong.expense.utils.SharedUtil;
import com.dong.expense.utils.StringUtil;
import com.umeng.analytics.MobclickAgent;

/**
 * 
 * 手势密码设置界面
 * 
 */
public class GestureSetActivity extends Activity implements OnClickListener {
	private static final String TAG = GestureSetActivity.class.getSimpleName();
	protected static final int EXPENSE_FIRST_RESTORE_SUC = 0x00600;
	private TextView mTextTitle;
	private ImageView mImageBack;
	private TextView mTextRight;
	private LockIndicator mLockIndicator;
	private TextView mTextTip;
	private FrameLayout mGestureContainer;
	private GestureContentView mGestureContentView;
	private TextView mTextReset;
	private boolean mIsFirstInput = true;
	private String mFirstPassword = null;
	private int settingInto;// 是否设置进入，0:第一次打开设置，1:修改手势进入 2:设置手势进入3:忘记手势密码进入
	private ProgressDialog pDialog;
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case EXPENSE_FIRST_RESTORE_SUC:
				hideDiolog();
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		ApplicationPool.getInstance().addActivity(this);
		setContentView(R.layout.activity_gesture_set);
		settingInto = getIntent().getIntExtra(ConstantPool.GESTURE_INTO_SET, 0);
		setUpViews();
		setUpListeners();
	}

	private void setUpViews() {
		mTextTitle = (TextView) findViewById(R.id.text_title);
		mTextTitle.setText(R.string.setup_gesture_code);
		mTextRight = (TextView) findViewById(R.id.text_right);
		mImageBack = (ImageView) findViewById(R.id.image_back);
		if (settingInto == 0) {
			mImageBack.setVisibility(View.GONE);
			mTextRight.setVisibility(View.VISIBLE);
			restoreData();
		} else {
			mImageBack.setVisibility(View.VISIBLE);
			mTextRight.setVisibility(View.GONE);
		}
		mTextReset = (TextView) findViewById(R.id.text_reset);
		mTextReset.setClickable(false);
		mLockIndicator = (LockIndicator) findViewById(R.id.lock_indicator);
		mTextTip = (TextView) findViewById(R.id.text_tip);
		mGestureContainer = (FrameLayout) findViewById(R.id.gesture_container);
		// 初始化一个显示各个点的viewGroup
		mGestureContentView = new GestureContentView(this, false, "",
				new GestureCallBack() {
					@Override
					public void onGestureCodeInput(String inputCode) {
						if (mIsFirstInput) {
							if (!isInputPassValidate(inputCode)) {
								mTextTip.setText(Html
										.fromHtml("<font color='#c70c1e'>最少链接4个点, 请重新输入</font>"));
								mGestureContentView.clearDrawlineState(0L);
								return;
							}
							mFirstPassword = MD5.encodeByMD5(inputCode);
							updateCodeList(inputCode);
							mGestureContentView.clearDrawlineState(0L);
							mTextReset.setClickable(true);
							mTextReset
									.setText(getString(R.string.reset_gesture_code));
							mTextTip.setText(getString(R.string.setup_gesture_pattern_again));
						} else {
							if (MD5.validatePassword(mFirstPassword, inputCode)) {
								Toast.makeText(GestureSetActivity.this, "设置成功",
										Toast.LENGTH_SHORT).show();
								mGestureContentView.clearDrawlineState(0L);
								SharedUtil
										.setGesturePasswork(
												GestureSetActivity.this,
												mFirstPassword);
								if (settingInto == 0) {
									Intent intent = new Intent();
									intent.setClass(GestureSetActivity.this,
											MainActivity.class);
									startActivity(intent);
								} else if (settingInto == 3) {
									// ExpenseDao dao =
									// ExpenseDao.getInstance(GestureSetActivity.this);
									// dao.deteleAll();
									Intent intent = new Intent();
									intent.setClass(GestureSetActivity.this,
											MainActivity.class);
									startActivity(intent);
									GestureForgetActivity.getIntanse().finish();
									GestureLoginActivity.getIntanse().finish();
								}
								GestureSetActivity.this.finish();
							} else {
								mTextTip.setText(Html
										.fromHtml("<font color='#c70c1e'>与上一次绘制不一致，请重新绘制</font>"));
								// 左右移动动画
								Animation shakeAnimation = AnimationUtils
										.loadAnimation(GestureSetActivity.this,
												R.anim.shake);
								mTextTip.startAnimation(shakeAnimation);
								// 保持绘制的线，1.5秒后清除
								mGestureContentView.clearDrawlineState(1300L);
							}
						}
						mIsFirstInput = false;
					}

					@Override
					public void checkedSuccess() {

					}

					@Override
					public void checkedFail() {

					}
				});
		// 设置手势解锁显示到哪个布局里面
		mGestureContentView.setParentView(mGestureContainer);
		updateCodeList("");
	}

	/**
	 * 第一次打开应用时，如果之前备份的有数据先恢复
	 */
	private void restoreData() {
		showDiolog();
		new Thread(new Runnable() {
			@Override
			public void run() {
				String json = FileUtil.readExpenseFile();
				List<ExpenseInfo> list1 = ExpenseUtil.jsonToList(json);
				if (list1 != null && list1.size() > 0) {
					ExpenseDao dao = ExpenseDao
							.getInstance(GestureSetActivity.this);
					for (int i = 0; i < list1.size(); i++) {
						ExpenseInfo info = list1.get(i);
						dao.itemInsert(info);
					}
					List<ExpenseInfo> list2 = dao.queryAll();
					if (StringUtil.isNotNull(ExpenseUtil.listToJson(list2))) {
						FileUtil.deleteExpense();// 保存新数据前，先删除旧的
						boolean isSuc = FileUtil.saveExpenseMsg(json);
					}
				}
				mHandler.sendEmptyMessage(EXPENSE_FIRST_RESTORE_SUC);
			}
		}).start();
	}

	private void showDiolog() {
		pDialog = ProgressDialog.show(this, "温馨提示", "加载中,请稍等...", true, true);
	}

	private void hideDiolog() {
		if (null != pDialog) {
			pDialog.dismiss();
		}
	}

	private void setUpListeners() {
		mImageBack.setOnClickListener(this);
		mTextRight.setOnClickListener(this);
		mTextReset.setOnClickListener(this);
	}

	private void updateCodeList(String inputCode) {
		// 更新选择的图案
		mLockIndicator.setPath(inputCode);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.image_back:
			this.finish();
			break;
		case R.id.text_right:
			SharedUtil.setGesturePasswork(GestureSetActivity.this, "");
			Intent intent = new Intent();
			intent.setClass(GestureSetActivity.this, MainActivity.class);
			startActivity(intent);
			GestureSetActivity.this.finish();
			break;
		case R.id.text_reset:
			mIsFirstInput = true;
			updateCodeList("");
			mTextTip.setText(getString(R.string.setup_gesture_pattern));
			mTextReset.setText(R.string.set_gesture_pattern_reason);
			break;
		default:
			break;
		}
	}

	private boolean isInputPassValidate(String inputPassword) {
		if (TextUtils.isEmpty(inputPassword) || inputPassword.length() < 4) {
			return false;
		}
		return true;
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

	@Override
	protected void onDestroy() {
		hideDiolog();
		super.onDestroy();
	}
}
