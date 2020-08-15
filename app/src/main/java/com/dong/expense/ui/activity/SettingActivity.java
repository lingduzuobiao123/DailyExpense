package com.dong.expense.ui.activity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dong.expense.R;
import com.dong.expense.infos.ExpenseInfo;
import com.dong.expense.logic.ApplicationPool;
import com.dong.expense.sql.ExpenseDao;
import com.dong.expense.utils.ConstantPool;
import com.dong.expense.utils.ExpenseUtil;
import com.dong.expense.utils.FileUtil;
import com.dong.expense.utils.SharedUtil;
import com.dong.expense.utils.StringUtil;
import com.umeng.analytics.MobclickAgent;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class SettingActivity extends Activity implements OnClickListener {
    private static final String TAG = SettingActivity.class.getSimpleName();

    protected static final int EXPENSE_BACKUP_SUC = 0x00500;
    protected static final int EXPENSE_BACKUP_FAIL = 0x00510;
    protected static final int EXPENSE_BACKUP_EMPTY = 0x00520;
    protected static final int EXPENSE_RESTORE_EMPTY = 0x00530;
    protected static final int EXPENSE_RESTORE_SUC = 0x00540;
    private TextView mTextTitle;
    private TextView mTextRight;
    private ImageView mImageBack;
    private Button mBtnSwitch;
    private Button mBtnUpdate;
    private Button mBtnBackup;
    private Button mBtnRestore;
    private boolean isCloseGesture;// 是否关闭手势
    private ProgressDialog pDialog;
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case EXPENSE_BACKUP_SUC:
                    Toast.makeText(SettingActivity.this, "备份成功", Toast.LENGTH_SHORT)
                            .show();
                    hideDiolog();
                    break;
                case EXPENSE_BACKUP_FAIL:
                    hideDiolog();
                    Toast.makeText(SettingActivity.this, "备份失败", Toast.LENGTH_SHORT)
                            .show();
                    break;
                case EXPENSE_BACKUP_EMPTY:
                    hideDiolog();
                    Toast.makeText(SettingActivity.this, "备份数据为空",
                            Toast.LENGTH_SHORT).show();
                    break;
                case EXPENSE_RESTORE_EMPTY:
                    hideDiolog();
                    Toast.makeText(SettingActivity.this, "没有备份数据",
                            Toast.LENGTH_SHORT).show();
                    break;
                case EXPENSE_RESTORE_SUC:
                    hideDiolog();
                    Toast.makeText(SettingActivity.this, "数据恢复成功,并删除备份数据",
                            Toast.LENGTH_SHORT).show();
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
        setContentView(R.layout.activity_setting);
        initView();
        setListener();
        initData();
    }

    private void initView() {
        mTextTitle = (TextView) findViewById(R.id.text_title);
        mTextTitle.setText(R.string.setting_title_text);
        mTextRight = (TextView) findViewById(R.id.text_right);
        mImageBack = (ImageView) findViewById(R.id.image_back);
        mBtnSwitch = (Button) findViewById(R.id.btn_gesture_switch);
        mBtnUpdate = (Button) findViewById(R.id.btn_gesture_update);
        mBtnBackup = (Button) findViewById(R.id.btn_data_backup);
        mBtnRestore = (Button) findViewById(R.id.btn_data_restore);
    }

    private void initData() {
        if (StringUtil.isNotNull(SharedUtil.getGesturePasswork(this))) {
            mBtnSwitch.setText("关闭手势");
            isCloseGesture = true;
            mBtnUpdate.setVisibility(View.VISIBLE);
        } else {
            mBtnSwitch.setText("设置手势");
            isCloseGesture = false;
            mBtnUpdate.setVisibility(View.GONE);
        }
    }

    private void setListener() {
        mImageBack.setOnClickListener(this);
        mBtnSwitch.setOnClickListener(this);
        mBtnUpdate.setOnClickListener(this);
        mBtnBackup.setOnClickListener(this);
        mBtnRestore.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_back:
                this.finish();
                break;
            case R.id.btn_gesture_switch:
                startSwitch();
                break;
            case R.id.btn_gesture_update:
                startGesture();
                break;
            case R.id.btn_data_backup:
                dataBackup();
                break;
            case R.id.btn_data_restore:
                dataRestore();
                break;
            default:
                break;
        }
    }

    private void showDiolog() {
        pDialog = ProgressDialog.show(this, "温馨提示", "加载中,请稍等...", true, true);
    }

    private void hideDiolog() {
        if (null != pDialog && pDialog.isShowing()) {
            pDialog.dismiss();
        }
    }

    /**
     * 备份数据
     */
    private void dataBackup() {
        //Schedulers.io(): 适合I/O类型的操作，比如网络请求，磁盘操作。
        //Schedulers.computation(): 适合计算任务，比如事件循环或者回调处理。
        //AndroidSchedulers.mainThread() : 回调主线程，比如UI操作。
        showDiolog();
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                ExpenseDao dao = ExpenseDao.getInstance(SettingActivity.this);
                List<ExpenseInfo> list = dao.queryAll();
                String json = ExpenseUtil.listToJson(list);
                int result;
                if (StringUtil.isNotNull(json)) {
                    FileUtil.deleteExpense();// 保存新数据前，先删除旧的
                    boolean isSuc = FileUtil.saveExpenseMsg(json);
                    if (isSuc) {
                        SharedUtil.setBackupTime(SettingActivity.this,
                                System.currentTimeMillis());
                        result = EXPENSE_BACKUP_SUC;
                    } else {
                        result = EXPENSE_BACKUP_FAIL;
                    }
                } else {
                    result = EXPENSE_BACKUP_EMPTY;
                }
                emitter.onNext(result);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Integer result) {
                        mHandler.sendEmptyMessage(result);
                        hideDiolog();
                    }

                    @Override
                    public void onError(Throwable e) {
                        hideDiolog();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


//    /**
//     * 备份数据
//     */
//    private void dataBackup() {
//        showDiolog();
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                ExpenseDao dao = ExpenseDao.getInstance(SettingActivity.this);
//                List<ExpenseInfo> list = dao.queryAll();
//                String json = ExpenseUtil.listToJson(list);
//                if (StringUtil.isNotNull(json)) {
//                    FileUtil.deleteExpense();// 保存新数据前，先删除旧的
//                    boolean isSuc = FileUtil.saveExpenseMsg(json);
//                    if (isSuc) {
//                        mHandler.sendEmptyMessage(EXPENSE_BACKUP_SUC);
//                        SharedUtil.setBackupTime(SettingActivity.this,
//                                System.currentTimeMillis());
//                    } else {
//                        mHandler.sendEmptyMessage(EXPENSE_BACKUP_FAIL);
//                    }
//                } else {
//                    mHandler.sendEmptyMessage(EXPENSE_BACKUP_EMPTY);
//                }
//            }
//        }).start();
//    }

//    /**
//     * 恢复数据
//     */
//    private void dataRestore() {
//        showDiolog();
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                String json = FileUtil.readExpenseFile();
//                List<ExpenseInfo> list = ExpenseUtil.jsonToList(json);
//                if (list == null || list.size() == 0) {
//                    mHandler.sendEmptyMessage(EXPENSE_RESTORE_EMPTY);
//                } else {
//                    ExpenseDao dao = ExpenseDao
//                            .getInstance(SettingActivity.this);
//                    for (int i = 0; i < list.size(); i++) {
//                        ExpenseInfo info = list.get(i);
//                        dao.itemInsert(info);
//                    }
//                    FileUtil.deleteExpense();// 恢复成功后，删除sd卡中的
//                    mHandler.sendEmptyMessage(EXPENSE_RESTORE_SUC);
//                }
//            }
//        }).start();
//    }

    /**
     * 恢复数据
     */
    private void dataRestore() {
        showDiolog();
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                String json = FileUtil.readExpenseFile();
                List<ExpenseInfo> list = ExpenseUtil.jsonToList(json);
                int result;
                if (list == null || list.size() == 0) {
                    result = EXPENSE_RESTORE_EMPTY;
                } else {
                    ExpenseDao dao = ExpenseDao
                            .getInstance(SettingActivity.this);
                    for (int i = 0; i < list.size(); i++) {
                        ExpenseInfo info = list.get(i);
                        dao.itemInsert(info);
                    }
                    FileUtil.deleteExpense();// 恢复成功后，删除sd卡中的
                    result = EXPENSE_RESTORE_SUC;
                }
                emitter.onNext(result);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Integer result) {
                        mHandler.sendEmptyMessage(result);
                        hideDiolog();
                    }

                    @Override
                    public void onError(Throwable e) {
                        hideDiolog();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void startSwitch() {
        if (isCloseGesture) {
            Intent intent = new Intent(SettingActivity.this,
                    GestureLoginActivity.class);
            intent.putExtra(ConstantPool.GESTURE_INTO_LOGIN, 2);
            startActivity(intent);
        } else {
            Intent intent = new Intent(SettingActivity.this,
                    GestureSetActivity.class);
            intent.putExtra(ConstantPool.GESTURE_INTO_SET, 2);
            startActivity(intent);
        }
    }

    private void startGesture() {
        Intent intent = new Intent(SettingActivity.this,
                GestureLoginActivity.class);
        intent.putExtra(ConstantPool.GESTURE_INTO_LOGIN, 1);
        startActivity(intent);
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

    @Override
    protected void onDestroy() {
        hideDiolog();
        super.onDestroy();
    }

}
