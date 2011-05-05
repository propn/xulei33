package com.ztesoft.mobile.menu;

import android.graphics.drawable.Drawable;

import com.ztesoft.mobile.R;

public class MenuHelper {

	public static int getSecondMenus(int id) {

		int menusNumber = 0;
		switch (id) {
		case 1:
			menusNumber = 3; // 前置
			break;
		case 2:
			menusNumber = 5; // 业务受理
			break;
		case 3:
			menusNumber = 4; // 客户查询
			break;
		case 4:
			menusNumber = 4; // 费用查询
			break;
		case 5:
			menusNumber = 2; // 积分查询
			break;
		case 6:
			menusNumber = 2; // 缴费
			break;
		case 7:
			menusNumber = 2; // 宽带续约
			break;
		case 8:
			menusNumber = 1; // 外线工单处理
			break;
		case 9:
			menusNumber = 3; // 常用信息查询
			break;
		case 10:
			menusNumber = 4; // 我的信息
			break;
		}
		return menusNumber;
	}

	/**
	 * 
	 */
	public static String getTitle(int id) {
		// TextView tSecondTitle = (TextView)
		// macivity.findViewById(R.id.tSecondTitle);
		String tTitle = "";
		switch (id) {
		case 1:
			// menusNumber = 3; // 前置
			tTitle = "营销前置推荐：";
			break;
		case 2:
			// menusNumber = 5; // 业务受理
			tTitle = "业务受理：";
			break;
		case 3:
			// menusNumber = 4; // 客户查询
			tTitle = "客户查询：";
			break;
		case 4:
			// menusNumber = 4; // 费用查询
			tTitle = "费用查询：";
			break;
		case 5:
			// menusNumber = 2; // 积分查询
			tTitle = "积分查询：";
			break;
		case 6:
			// menusNumber = 2; // 缴费
			tTitle = "缴费：";
			break;
		case 7:
			// menusNumber = 2; // 宽带续约
			tTitle = "宽带续约：";
			break;
		case 8:
			// menusNumber = 1; // 外线工单处理
			tTitle = "外线工单处理：";
			break;
		case 9:
			// menusNumber = 3; // 常用信息查询
			tTitle = "常用信息查询：";
			break;
		case 10:
			// menusNumber = 4; // 我的信息
			tTitle = "我的信息：";
			break;
		}

		return tTitle;

	}

	/**
	 * 
	 * @param macivity
	 * @param id
	 * @param parentId
	 * @return
	 */
	public static Drawable getSecondDrawableById(MenuActivity macivity, int id,
			int parentId) {
		Drawable dw2 = null;
		switch (parentId) {
		case 1:
			switch (id) {
			case 10: // E家套餐
				dw2 = macivity.getResources().getDrawable(
						R.drawable.menu_fee_yo);
				break;
			case 11: // 天翼套餐
				dw2 = macivity.getResources().getDrawable(
						R.drawable.menu_fee_yo);
				break;
			case 12: // 问问
				dw2 = macivity.getResources().getDrawable(
						R.drawable.menu_fee_yo);
				break;
			}

			break;
		case 2:
			switch (id) {
			case 20: // 天翼套餐
				dw2 = macivity.getResources().getDrawable(
						R.drawable.menu_ninfo_ct);
				break;
			case 21: // E家办理
				dw2 = macivity.getResources().getDrawable(
						R.drawable.menu_ninfo_ct);
				break;
			case 22: // 增值业务
				dw2 = macivity.getResources().getDrawable(
						R.drawable.menu_ninfo_ct);
				break;
			case 23: // 程控功能
				dw2 = macivity.getResources().getDrawable(
						R.drawable.menu_ninfo_ct);
				break;
			case 24: // 优惠促销
				dw2 = macivity.getResources().getDrawable(
						R.drawable.menu_ninfo_ct);
				break;
			}

			break;
		case 3:
			switch (id) {
			case 30: // 基本信息
				dw2 = macivity.getResources().getDrawable(
						R.drawable.menu_my_unorder); // E家套餐
				break;
			case 31: // 套餐信息
				break;
			case 32: // 产品信息
				break;
			case 23: // 业务办理历史
				break;
			}
			break;
		case 4:
			switch (id) {
			case 40: // 账单查询
				dw2 = macivity.getResources().getDrawable(
						R.drawable.menu_my_unorder); // E家套餐
				break;
			case 41: // 余额查询
				break;
			case 42: // 套餐剩余流量
				break;
			case 43: // 缴费记录
				break;
			}
			break;
		case 5:

			switch (id) {
			case 50: // 积分余额
				dw2 = macivity.getResources().getDrawable(
						R.drawable.menu_my_unorder); // E家套餐
				break;
			case 51: // 积分消费历史
				break;
			}
			break;
		case 6:
			switch (id) {
			case 60: // 电子钱包缴费
				dw2 = macivity.getResources().getDrawable(
						R.drawable.menu_my_unorder); // E家套餐
				break;
			case 61: // 为客户缴费
				break;
			}
			break;
		case 7:
			switch (id) {
			case 70: // 待续约宽带查询
				dw2 = macivity.getResources().getDrawable(
						R.drawable.menu_my_unorder); // E家套餐
				break;
			case 71: // 宽带续约
				break;
			}
			break;
		case 8:
			switch (id) {
			case 80: // 外线工单处理
				dw2 = macivity.getResources().getDrawable(
						R.drawable.menu_my_unorder); // E家套餐
				break;
			}
			break;
		case 9:
			switch (id) {
			case 90: // 号码归属地查询
				dw2 = macivity.getResources().getDrawable(
						R.drawable.menu_my_unorder); // E家套餐
				break;
			case 91: // 长途区号查询
				break;
			case 92: // 营业厅导航
				break;
			}
			break;
		case 10:
			switch (id) {
			case 100: // 我的未转化营销单
				dw2 = macivity.getResources().getDrawable(
						R.drawable.menu_my_unorder); // E家套餐
				break;
			case 101: // 我的外线工单
				break;
			case 102: // 我的电子钱包
				break;
			case 103: // 我的佣金
				break;
			}
			break;
		}

		return dw2;
	}

	/**
	 * 
	 * @param macivity
	 * @param id
	 * @param parentId
	 * @return
	 */
	public static Object[] getSecondMenuNameById(MenuActivity macivity, int id,
			int parentId) {
		Object[] menus = new Object[2];
		switch (parentId) {
		case 1:
			switch (id) {
			case 10: // E家套餐
				menus[0] = R.drawable.menu_my_unorder;
				menus[1] = "E家套餐";
				break;
			case 11: // 天翼套餐
				menus[0] = R.drawable.menu_my_unorder;
				menus[1] = "天翼套餐";
				break;
			case 12: // 问问
				menus[0] = R.drawable.menu_my_unorder;
				menus[1] = "问问";
				// dw2 =
				// macivity.getResources().getDrawable(R.drawable.btn_prequest);
				break;
			case 13: //
				menus[0] = R.drawable.menu_return;
				menus[1] = "返回";

				break;
			}

			break;
		case 2:
			switch (id) {
			case 20: // 天翼套餐
				menus[0] = R.drawable.menu_my_unorder;
				menus[1] = "天翼套餐";
				// dw2 =
				// macivity.getResources().getDrawable(R.drawable.btn_acp_ty);
				break;
			case 21: // E家办理
				menus[0] = R.drawable.menu_my_unorder;
				menus[1] = "E家办理";
				// dw2 =
				// macivity.getResources().getDrawable(R.drawable.btn_acp_ehome);
				break;
			case 22: // 增值业务
				menus[0] = R.drawable.menu_my_unorder;
				menus[1] = "增值业务";
				// dw2 =
				// macivity.getResources().getDrawable(R.drawable.btn_acp_va);
				break;
			case 23: // 程控功能
				menus[0] = R.drawable.menu_my_unorder;
				menus[1] = "程控功能";
				// dw2 =
				// macivity.getResources().getDrawable(R.drawable.btn_acp_bz);
				break;
			case 24: // 优惠促销
				menus[0] = R.drawable.menu_my_unorder;
				menus[1] = "优惠促销";
				// dw2 =
				// macivity.getResources().getDrawable(R.drawable.btn_acp_pt);
				break;
			case 25: // 返回
				menus[0] = R.drawable.menu_return;
				menus[1] = "返回";
				break;
			}

			break;
		case 3:
			switch (id) {
			case 30: // 基本信息
				menus[0] = R.drawable.menu_my_unorder;
				menus[1] = "基本信息";
				// dw2 =
				// macivity.getResources().getDrawable(R.drawable.btn_act_beforepng);
				// // E家套餐
				break;
			case 31: // 套餐信息
				menus[0] = R.drawable.menu_my_unorder;
				menus[1] = "套餐信息";
				break;
			case 32: // 产品信息
				menus[0] = R.drawable.menu_my_unorder;
				menus[1] = "产品信息";
				break;
			case 33: // 业务办理历史
				menus[0] = R.drawable.menu_my_unorder;
				menus[1] = "办理历史";
				break;
			case 34: // 返回
				menus[0] = R.drawable.menu_return;
				menus[1] = "返回";
				break;
			}
			break;
		case 4:
			switch (id) {
			case 40: // 账单查询
				menus[0] = R.drawable.menu_my_unorder;
				menus[1] = "账单查询";
				break;
			case 41: // 余额查询
				menus[0] = R.drawable.menu_my_unorder;
				menus[1] = "余额查询";
				break;
			case 42: // 套餐剩余流量
				menus[0] = R.drawable.menu_my_unorder;
				menus[1] = "剩余流量";
				break;
			case 43: // 缴费记录
				menus[0] = R.drawable.menu_my_unorder;
				menus[1] = "缴费记录";
				break;
			case 44: // 返回
				menus[0] = R.drawable.menu_return;
				menus[1] = "返回";
				break;
			}
			break;
		case 5:

			switch (id) {
			case 50: // 积分余额
				menus[0] = R.drawable.menu_my_unorder;
				menus[1] = "积分余额";

				break;
			case 51: // 积分消费历史
				menus[0] = R.drawable.menu_my_unorder;
				menus[1] = "积分余额";
				break;
			case 52: // 返回
				menus[0] = R.drawable.menu_return;
				menus[1] = "返回";
				break;
			}
			break;
		case 6:
			switch (id) {
			case 60: // 电子钱包缴费
				menus[0] = R.drawable.menu_ninfo_ct;
				menus[1] = "电子钱包";
				break;
			case 61: // 为客户缴费
				menus[0] = R.drawable.menu_ninfo_ct;
				menus[1] = "客户缴费";
				break;
			case 62: // 返回
				menus[0] = R.drawable.menu_return;
				menus[1] = "返回";
				break;
			}
			break;
		case 7:
			switch (id) {
			case 70: // 待续约宽带查询
				menus[0] = R.drawable.menu_ninfo_ct;
				menus[1] = "待续约宽带";
				break;
			case 71: // 宽带续约
				menus[0] = R.drawable.menu_fee_yo;
				menus[1] = "宽带续约";
				break;
			case 72: // 返回
				menus[0] = R.drawable.menu_return;
				menus[1] = "返回";
				break;
			}
			break;
		case 8:
			switch (id) {
			case 80: // 外线工单处理
				menus[0] = R.drawable.menu_my_unorder;
				menus[1] = "外线工单";
				break;
			case 81: // 返回
				menus[0] = R.drawable.menu_return;
				menus[1] = "返回";
				break;
			}

			break;
		case 9:
			switch (id) {
			case 90: // 号码归属地查询
				menus[0] = R.drawable.menu_return;
				menus[1] = "号码归属";
				break;
			case 91: // 长途区号查询
				menus[0] = R.drawable.menu_my_unorder;
				menus[1] = "长途区号";
				break;
			case 92: // 营业厅导航
				menus[0] = R.drawable.menu_fee_yo;
				menus[1] = "营业厅导航";
				break;
			case 93: // 返回
				menus[0] = R.drawable.menu_return;
				menus[1] = "返回";
				break;
			}
			break;
		case 10:
			switch (id) {
			case 100: // 我的未转化营销单
				menus[0] = R.drawable.menu_ninfo_ct;
				menus[1] = "未转化单";
				break;
			case 101: // 我的外线工单
				menus[0] = R.drawable.menu_fashion;
				menus[1] = "外线工单";
				break;
			case 102: // 我的电子钱包
				menus[0] = R.drawable.menu_ql;
				menus[1] = "电子钱包";
				break;
			case 103: // 我的佣金
				menus[0] = R.drawable.menu_xl;
				menus[1] = "我的佣金";
				break;
			case 104: // 返回
				menus[0] = R.drawable.menu_return;
				menus[1] = "返回";
				break;
			}
			break;
		}

		return menus;
	}
}
