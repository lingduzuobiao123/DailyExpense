package com.dong.expense.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dong.expense.R;
import com.dong.expense.infos.TypeInfo;

public class ExpenseTypeAdapter extends BaseAdapter {
	private static final String TAG = ExpenseTypeAdapter.class.getSimpleName();
	private List<TypeInfo> typeList;
	private LayoutInflater mInflater;
	private Context mContext;
	private int selected = -1;
	private int selectColor;

	public ExpenseTypeAdapter(Context context, List<TypeInfo> list) {
		mContext = context;
		mInflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		typeList = list;
		selectColor = mContext.getResources().getColor(
				R.color.gridview_expense_type_select);
	}

	public void setSelected(int position) {
		selected = position;
		this.notifyDataSetChanged();
	}

	public int getSelected() {
		return selected;
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
		return null == typeList ? 0 : typeList.size();
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
		ViewHolder holder;
		if (null == convertView) {
			holder = new ViewHolder();
			convertView = mInflater
					.inflate(R.layout.adapter_expense_type, null);
			holder.nameText = (TextView) convertView
					.findViewById(R.id.text_type);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.position = position;
		holder.typeInfo = typeList.get(holder.position);
		holder.nameText.setText(String.valueOf(holder.typeInfo.getName()));
		if (holder.position == selected) {
			convertView.setBackgroundColor(selectColor);
		} else {
			convertView.setBackgroundColor(Color.TRANSPARENT);
		}
		return convertView;
	}

	class ViewHolder {
		TextView nameText;
		int position;
		TypeInfo typeInfo;
	}

}
