package com.dong.expense.adapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dong.expense.R;
import com.dong.expense.infos.ExpenseInfo;
import com.dong.expense.sql.ExpenseDao;
import com.dong.expense.ui.activity.ExpenseWriteActivity;
import com.dong.expense.utils.ConstantPool;
import com.dong.expense.utils.StringUtil;

public class ExpenseDayAdapter extends BaseAdapter {
	private static final String TAG = ExpenseDayAdapter.class.getSimpleName();
	private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	private List<ExpenseInfo> expenseList;
	private LayoutInflater mInflater;
	private Context mContext;

	public ExpenseDayAdapter(Context context) {
		mContext = context;
		mInflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		expenseList = new ArrayList<ExpenseInfo>();
	}

	public void setExpenseList(List<ExpenseInfo> list) {
		if (null == list) {
			return;
		}
		expenseList.clear();
		expenseList.addAll(list);
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return expenseList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return expenseList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (null == convertView) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(
					R.layout.adapter_expense_list_child, null);
			holder.expenseLayout = (RelativeLayout) convertView
					.findViewById(R.id.expense_layout);
			holder.typeText = (TextView) convertView
					.findViewById(R.id.text_type);
			holder.moneyText = (TextView) convertView
					.findViewById(R.id.text_expense);
			holder.updateLayout = (LinearLayout) convertView
					.findViewById(R.id.update_layout);
			holder.updateBtn = (Button) convertView
					.findViewById(R.id.update_btn);
			holder.deleteBtn = (Button) convertView
					.findViewById(R.id.delete_btn);
			holder.detailBtn = (Button) convertView
					.findViewById(R.id.details_btn);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.position = position;
		holder.expenseInfo = expenseList.get(holder.position);
		String type = holder.expenseInfo.getExpenseType();
		if (StringUtil.isNotNull(type)) {
			holder.typeText.setText(type);
		} else {
			holder.typeText.setText("其他");
		}
		holder.moneyText
				.setText(String.valueOf(holder.expenseInfo.getExpense()));
		holder.updateLayout.setVisibility(View.GONE);
		holder.expenseLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (holder.updateLayout.getVisibility() == View.VISIBLE) {
					holder.updateLayout.setVisibility(View.GONE);
				} else {
					holder.updateLayout.setVisibility(View.VISIBLE);
				}
			}
		});
		holder.detailBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Toast.makeText(mContext, "暂未实现，可以点击修改查看", Toast.LENGTH_SHORT)
						.show();
			}
		});
		holder.updateBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.putExtra(ConstantPool.UPDATE_EXPENSE_MSG,
						holder.expenseInfo);
				intent.setClass(mContext, ExpenseWriteActivity.class);
				mContext.startActivity(intent);
			}
		});
		holder.deleteBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showPromptDialog(holder);
			}
		});
		return convertView;
	}

	private void showPromptDialog(final ViewHolder holder) {
		new AlertDialog.Builder(mContext)
				.setTitle("温馨提示")
				.setMessage("确定要删除记录")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						ExpenseDao dao = ExpenseDao.getInstance(mContext);
						dao.deteleItem(holder.expenseInfo.getId());
						expenseList.remove(holder.position);
						ExpenseDayAdapter.this.notifyDataSetChanged();
						dialog.dismiss();
						Toast.makeText(mContext, "删除成功", Toast.LENGTH_SHORT)
								.show();
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).show();

	}

	class ViewHolder {
		RelativeLayout expenseLayout;
		LinearLayout updateLayout;
		TextView typeText;
		TextView moneyText;
		Button updateBtn;
		Button deleteBtn;
		Button detailBtn;
		int position;
		ExpenseInfo expenseInfo;
	}

}
