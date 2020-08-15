package com.dong.expense.utils;

public class ConstantPool {

	public static final String GESTURE_INTO_LOGIN = "gesture_into_login"; // 进入方法
																			// 0:登录进入，1:修改手势进入
																			// 2:关闭手势进入
	public static final String GESTURE_INTO_SET = "gesture_into_set"; // 进入方法
																		// 0:第一次打开设置，1:修改手势进入
																		// 2:设置手势进入3:忘记手势密码进入
	public static final String EXPENSE_DATE_QUERY = "expense_date_query"; // 消费日期查询
	public static final String EXPENSE_QUERY_START = "expense_query_start"; // 查询开始时间
	public static final String EXPENSE_QUERY_END = "expense_query_end"; // 查询结束时间
	public static final String EXPENSE_TERM_SPINNER = "expense_term_spinner"; //
	public static final String UPDATE_EXPENSE_MSG = "update_expense_msg"; // 消费信息

	public static final long AUTO_BACKUP_TIME = 3 * 24 * 60 * 60 * 1000; // 设置自动备份间隔时间

	// */ 手势密码点的状态
	public static final int POINT_STATE_NORMAL = 0; // 正常状态

	public static final int POINT_STATE_SELECTED = 1; // 按下状态

	public static final int POINT_STATE_WRONG = 2; // 错误状态
	// */
}
