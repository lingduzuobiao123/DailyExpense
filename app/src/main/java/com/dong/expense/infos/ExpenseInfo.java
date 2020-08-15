package com.dong.expense.infos;

import java.io.Serializable;
import java.util.List;

public class ExpenseInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2389668342272436799L;

	private int id;
	private float expense;// 金额
	private int type;// 1支出 2收入
	private String expenseType; // 消费类型
	private long expenseTime; // 消费时间
	private String expenseDate; // 消费日期
	private String explain;// 说明
	private long createTime;// 当前时间
	private long updateTime;// 修改时间

	private float expenseTotal;// 总金额
	private List<ExpenseInfo> expenseList;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public float getExpense() {
		return expense;
	}

	public void setExpense(float expense) {
		this.expense = expense;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getExpenseType() {
		return expenseType;
	}

	public void setExpenseType(String expenseType) {
		this.expenseType = expenseType;
	}

	public long getExpenseTime() {
		return expenseTime;
	}

	public void setExpenseTime(long expenseTime) {
		this.expenseTime = expenseTime;
	}

	public String getExplain() {
		return explain;
	}

	public void setExplain(String explain) {
		this.explain = explain;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public long getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(long updateTime) {
		this.updateTime = updateTime;
	}

	public String getExpenseDate() {
		return expenseDate;
	}

	public void setExpenseDate(String expenseDate) {
		this.expenseDate = expenseDate;
	}

	public float getExpenseTotal() {
		return expenseTotal;
	}

	public void setExpenseTotal(float expenseTotal) {
		this.expenseTotal = expenseTotal;
	}

	public List<ExpenseInfo> getExpenseList() {
		return expenseList;
	}

	public void setExpenseList(List<ExpenseInfo> expenseList) {
		this.expenseList = expenseList;
	}

	@Override
	public String toString() {
		return "ExpenseInfo [expense=" + expense + ", type=" + type
				+ ", expenseType=" + expenseType + ", expenseTime="
				+ expenseTime + ", expenseDate=" + expenseDate + ", explain="
				+ explain + ", createTime=" + createTime + ", updateTime="
				+ updateTime + ", expenseTotal=" + expenseTotal
				+ ", expenseList=" + expenseList + "]";
	}

}
