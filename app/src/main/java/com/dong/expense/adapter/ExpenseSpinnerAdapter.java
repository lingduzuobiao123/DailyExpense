package com.dong.expense.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dong.expense.R;
import com.dong.expense.infos.TypeInfo;

public class ExpenseSpinnerAdapter extends BaseAdapter {
	private static final String TAG = ExpenseSpinnerAdapter.class
			.getSimpleName();
	private List<TypeInfo> typeList;
	private LayoutInflater mInflater;
	private Context mContext;

	public ExpenseSpinnerAdapter(Context context, List<TypeInfo> list) {
		mContext = context;
		mInflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		typeList = list;
	}

	public void setExpenseList(List<TypeInfo> list) {
		if (null == list) {
			return;
		}
		typeList.clear();
		typeList.addAll(list);
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return typeList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return typeList.get(position);
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
			convertView = mInflater.inflate(R.layout.adapter_expense_spinner,
					null);
			holder.typeText = (TextView) convertView
					.findViewById(R.id.text_type);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.position = position;
		holder.typeInfo = typeList.get(holder.position);
		String type = holder.typeInfo.getName();
		holder.typeText.setText(type);
		return convertView;
	}

	class ViewHolder {
		TextView typeText;
		int position;
		TypeInfo typeInfo;
	}

}
