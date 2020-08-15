package com.dong.expense.adapter;

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
import android.widget.BaseExpandableListAdapter;
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

public class ExpenseListAdapter extends BaseExpandableListAdapter {
	private static final String TAG = ExpenseListAdapter.class.getSimpleName();
	private List<ExpenseInfo> expenseList;
	private LayoutInflater mInflater;
	private Context mContext;

	public ExpenseListAdapter(Context context) {
		mContext = context;
		mInflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		expenseList = new ArrayList<ExpenseInfo>();
	}

	public void setExpenseList(List<ExpenseInfo> list) {
		expenseList.clear();
		if (null != list) {
			expenseList.addAll(list);
		}
		this.notifyDataSetChanged();
	}

	@Override
	public int getGroupCount() {
		// TODO Auto-generated method stub
		return expenseList.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		List<ExpenseInfo> list = expenseList.get(groupPosition)
				.getExpenseList();
		return null == list ? 0 : list.size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		// TODO Auto-generated method stub
		return expenseList.get(groupPosition);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		List<ExpenseInfo> list = expenseList.get(groupPosition)
				.getExpenseList();
		return null == list ? null : list.get(childPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		// TODO Auto-generated method stub
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return childPosition;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		final HolderView holder;
		if (null == convertView) {
			holder = new HolderView();
			convertView = mInflater
					.inflate(R.layout.adapter_expense_list, null);
			holder.dateText = (TextView) convertView
					.findViewById(R.id.text_date);
			holder.moneyText = (TextView) convertView
					.findViewById(R.id.text_expense);
			convertView.setTag(holder);
		} else {
			holder = (HolderView) convertView.getTag();
		}
		holder.position = groupPosition;
		holder.expenseInfo = expenseList.get(holder.position);
		holder.dateText.setText(holder.expenseInfo.getExpenseDate());
		holder.moneyText.setText(String.valueOf(holder.expenseInfo
				.getExpenseTotal()));
		return convertView;
	}

	class HolderView {
		TextView dateText;
		TextView moneyText;
		int position;
		ExpenseInfo expenseInfo;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		final HolderChildView holder;
		if (null == convertView) {
			holder = new HolderChildView();
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
			holder = (HolderChildView) convertView.getTag();
		}
		holder.groupPosition = groupPosition;
		holder.position = childPosition;
		holder.list = expenseList.get(holder.groupPosition).getExpenseList();
		if (null != holder.list && holder.list.size() > 0) {
			holder.expenseInfo = holder.list.get(holder.position);
			String type = holder.expenseInfo.getExpenseType();
			if (StringUtil.isNotNull(type)) {
				holder.typeText.setText(type);
			} else {
				holder.typeText.setText("其他");
			}
			holder.moneyText.setText(String.valueOf(holder.expenseInfo
					.getExpense()));
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
					Toast.makeText(mContext, "暂未实现，可以点击修改查看", Toast.LENGTH_SHORT).show();
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
		}
		return convertView;
	}

	private void showPromptDialog(final HolderChildView holder) {
		new AlertDialog.Builder(mContext)
				.setTitle("温馨提示")
				.setMessage("确定要删除记录")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						ExpenseDao dao = ExpenseDao.getInstance(mContext);
						dao.deteleItem(holder.expenseInfo.getId());
						holder.list.remove(holder.position);
						ExpenseListAdapter.this.notifyDataSetChanged();
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

	class HolderChildView {
		RelativeLayout expenseLayout;
		LinearLayout updateLayout;
		TextView typeText;
		TextView moneyText;
		Button updateBtn;
		Button deleteBtn;
		Button detailBtn;
		int position;
		int groupPosition;
		List<ExpenseInfo> list;
		ExpenseInfo expenseInfo;
	}

}
