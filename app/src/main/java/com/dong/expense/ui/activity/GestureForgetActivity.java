package com.dong.expense.ui.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.dong.expense.R;
import com.dong.expense.logic.ApplicationPool;
import com.dong.expense.sql.ExpenseDao;
import com.dong.expense.utils.ConstantPool;
import com.dong.expense.utils.SharedUtil;
import com.umeng.analytics.MobclickAgent;

public class GestureForgetActivity extends Activity implements OnClickListener {
	private static final String TAG = GestureForgetActivity.class
			.getSimpleName();
	private TextView mTextTitle;
	private TextView mTextRight;
	private ImageView mImageBack;
	private Button mBtnForget;
	private Button mBtnVerifyLock;

	private static GestureForgetActivity intanse;
	
	public static GestureForgetActivity getIntanse() {
		return intanse;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		ApplicationPool.getInstance().addActivity(this);
		setContentView(R.layout.activity_gesture_forget);
		intanse = this;
		initView();
		initListener();
	}

	private void initView() {
		mTextTitle = (TextView) findViewById(R.id.text_title);
		mTextTitle.setText(R.string.forget_gesture);
		mTextRight = (TextView) findViewById(R.id.text_right);
		mImageBack = (ImageView) findViewById(R.id.image_back);
		mBtnForget = (Button) findViewById(R.id.btn_forget_gesture);
	}

	private void initListener() {
		mImageBack.setOnClickListener(this);
		mBtnForget.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.image_back:
			this.finish();
			break;
		case R.id.btn_forget_gesture:
			showPromptDialog();
			break;
		default:
			break;
		}
	}

	private void showPromptDialog() {
		new AlertDialog.Builder(this).setTitle("温馨提示")
				.setMessage("忘记手势会删除所有记录，确定要忘记？")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						startSetLockPattern();
						dialog.dismiss();
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).show();

	}

	private void startSetLockPattern() {
		Intent intent = new Intent(GestureForgetActivity.this,
				GestureSetActivity.class);
		intent.putExtra(ConstantPool.GESTURE_INTO_SET, 3);
		startActivity(intent);
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
