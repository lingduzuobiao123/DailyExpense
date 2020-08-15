package com.dong.expense.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dong.expense.R;
import com.dong.expense.logic.ApplicationPool;
import com.dong.expense.ui.view.GestureContentView;
import com.dong.expense.ui.view.GestureDrawline.GestureCallBack;
import com.dong.expense.utils.ConstantPool;
import com.dong.expense.utils.SharedUtil;
import com.umeng.analytics.MobclickAgent;

/**
 * 
 * 手势绘制/校验界面
 * 
 */
public class GestureLoginActivity extends Activity implements
		android.view.View.OnClickListener {
	private static final String TAG = GestureLoginActivity.class
			.getSimpleName();
	private RelativeLayout mTopLayout;
	private TextView mTextTitle;
	private TextView mTextRight;
	private ImageView mImageBack;
	private TextView mTextUnlocked;
	private TextView mTextTip;
	private FrameLayout mGestureContainer;
	private GestureContentView mGestureContentView;
	private TextView mTextForget;
	private int errorNum;// 错误次数
	private int settingInto;// 0:登录进入，1:修改手势进入 2:关闭手势进入

	private static GestureLoginActivity intanse;

	public static GestureLoginActivity getIntanse() {
		return intanse;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		ApplicationPool.getInstance().addActivity(this);
		setContentView(R.layout.activity_gesture_login);
		intanse = this;
		errorNum = 0;
		settingInto = getIntent().getIntExtra(ConstantPool.GESTURE_INTO_LOGIN,
				0);
		setUpViews();
		setUpListeners();
	}

	private void setUpViews() {
		mTopLayout = (RelativeLayout) findViewById(R.id.top_layout);
		mTextTitle = (TextView) findViewById(R.id.text_title);
		mTextTitle.setText(R.string.login_gesture_code);
		mTextRight = (TextView) findViewById(R.id.text_right);
		mImageBack = (ImageView) findViewById(R.id.image_back);
		mTextUnlocked = (TextView) findViewById(R.id.text_unlocked);
		mTextTip = (TextView) findViewById(R.id.text_tip);
		mGestureContainer = (FrameLayout) findViewById(R.id.gesture_container);
		mTextForget = (TextView) findViewById(R.id.text_forget_gesture);
		mTextForget.setVisibility(View.GONE);
		if (settingInto == 0) {
			mTopLayout.setVisibility(View.GONE);
		} else {
			mTopLayout.setVisibility(View.VISIBLE);
		}

		String passwork = SharedUtil.getGesturePasswork(this);
		// 初始化一个显示各个点的viewGroup
		mGestureContentView = new GestureContentView(this, true, passwork,
				new GestureCallBack() {

					@Override
					public void onGestureCodeInput(String inputCode) {

					}

					@Override
					public void checkedSuccess() {
						mGestureContentView.clearDrawlineState(0L);
						Intent intent = new Intent();
						if (settingInto == 1) {
							intent.putExtra(ConstantPool.GESTURE_INTO_SET, 1);
							intent.setClass(GestureLoginActivity.this,
									GestureSetActivity.class);
							startActivity(intent);
						} else if (settingInto == 0) {
							intent.setClass(GestureLoginActivity.this,
									MainActivity.class);
							startActivity(intent);
						} else if (settingInto == 2) {
							SharedUtil.setGesturePasswork(
									GestureLoginActivity.this, "");
							Toast.makeText(GestureLoginActivity.this, "关闭成功",
									Toast.LENGTH_SHORT).show();
						}
						GestureLoginActivity.this.finish();
					}

					@Override
					public void checkedFail() {
						errorNum++;
						mGestureContentView.clearDrawlineState(1300L);
						mTextTip.setVisibility(View.VISIBLE);
						mTextTip.setText(Html
								.fromHtml("<font color='#c70c1e'>手势密码错误</font>"));
						// 左右移动动画
						Animation shakeAnimation = AnimationUtils
								.loadAnimation(GestureLoginActivity.this,
										R.anim.shake);
						mTextTip.startAnimation(shakeAnimation);
					}
				});
		// 设置手势解锁显示到哪个布局里面
		mGestureContentView.setParentView(mGestureContainer);
	}

	private void setUpListeners() {
		mImageBack.setOnClickListener(this);
		mTextForget.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.image_back:
			this.finish();
			break;
		case R.id.text_forget_gesture:
			Intent intent = new Intent(GestureLoginActivity.this,
					GestureForgetActivity.class);
			startActivity(intent);
			break;
		default:
			break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		/*
		 * 
		 * add()方法的四个参数，依次是：
		 * 
		 * 1、组别，如果不分组的话就写Menu.NONE,
		 * 
		 * 2、Id，这个很重要，Android根据这个Id来确定不同的菜单
		 * 
		 * 3、顺序，那个菜单现在在前面由这个参数的大小决定
		 * 
		 * 4、文本，菜单的显示文本
		 */
		menu.add(Menu.NONE, Menu.FIRST + 1, 5, "忘记密码");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case Menu.FIRST + 1:
			Intent intent = new Intent(GestureLoginActivity.this,
					GestureForgetActivity.class);
			startActivity(intent);
			break;
		}
		return super.onOptionsItemSelected(item);
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
