package com.ztesoft.mobile.menu;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ztesoft.mobile.R;
import com.ztesoft.mobile.business.OnLineJob;
import com.ztesoft.mobile.core.BaseActivity;
import com.ztesoft.mobile.widget.Button.ImageTextButton;

public class MenuActivity extends BaseActivity {

	private MenuActivity macivity;
	private Intent intent;
	private LinearLayout mainLayout01;

	private int mWidth, mHeight;

	private final static int IMG_WIDTH = 80;
	private int mainMenuId;

	private final int WC = LinearLayout.LayoutParams.WRAP_CONTENT;

	public MenuActivity() {
		macivity = this;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState, R.layout.menu);
		
		try {
			intent = MenuActivity.this.getIntent();
			String sId = intent.getDataString();
			mainMenuId = Integer.parseInt(sId);
		} catch (Exception ex) {
		}
		mainLayout01 = (LinearLayout) this.findViewById(R.id.menuLayout01);
		createMain();
	}

	private void createMain() {
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);

		mWidth = dm.widthPixels;
		mHeight = dm.heightPixels;

		LinearLayout l1 = new LinearLayout(this);
		LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(WC, WC);

		int n = mWidth / IMG_WIDTH;

		int menuNums = MenuHelper.getSecondMenus(mainMenuId);
		menuNums = menuNums + 1;
		try {
			TextView tTitle = (TextView) this.findViewById(R.id.menuTitle);
			tTitle.setText(MenuHelper.getTitle(mainMenuId));
			tTitle.setTextColor(Color.WHITE);
			tTitle.setTextAppearance(this, 10);
		} catch (Exception ex) {

		}

		for (int i = 0; i < menuNums; i++) {
			if (i % n == 0) {
				l1 = createLayout();
				mainLayout01.addView(l1);
			}
			int tmpId = mainMenuId * 10 + i;
			ImageTextButton btnImg = createImageButton(tmpId, mainMenuId);
			l1.addView(btnImg, param);
		}
	}

	/**
	 * 
	 * @return
	 */
	private LinearLayout createLayout() {
		LinearLayout layout = new LinearLayout(this);

		layout.setOrientation(LinearLayout.HORIZONTAL); // 控件对其方式为垂直排列
		LinearLayout.LayoutParams rr01 = new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

		layout.setLayoutParams(rr01);

		return layout;
	}

	/**
	 * 
	 * @param id
	 * @return
	 */
	private ImageTextButton createImageButton(int id, int parentId) {

		Object[] mObj = MenuHelper
				.getSecondMenuNameById(macivity, id, parentId);

		int reI = (Integer) mObj[0];
		String nI = (String) mObj[1];
		ImageTextButton ib01 = new ImageTextButton(this, reI, nI);
		ib01.setId(id);

		LinearLayout.LayoutParams imbutton01 = new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		imbutton01.weight = 1f;
		ib01.setBackgroundColor(Color.alpha(0));
		ib01.setLayoutParams(imbutton01);
		ib01.setOnClickListener(button_listener);
		ib01.setOnTouchListener(touchLight);

		return ib01;

	}

	// 定义button的点击响应方法
	private Button.OnClickListener button_listener = new Button.OnClickListener() {

		public void onClick(View v) {
			int id = v.getId();
			switch (id) {
			case 104:
				break;
			default:
				invokeEvent(id, mainMenuId);
				break;
			}

		}
	};

	/**
	 * mouse touch事件
	 */
	private OnTouchListener touchLight = new OnTouchListener() {

		public boolean onTouch(View v, MotionEvent event) {
			if (event.getAction() == MotionEvent.ACTION_DOWN
					|| event.getAction() == MotionEvent.ACTION_POINTER_DOWN
					|| event.getAction() == MotionEvent.ACTION_POINTER_1_DOWN
					|| event.getAction() == MotionEvent.ACTION_POINTER_2_DOWN
					|| event.getAction() == MotionEvent.ACTION_MOVE) {

				v.setBackgroundColor(Color.rgb(255, 165, 0));
			} else if (event.getAction() == MotionEvent.ACTION_UP
					|| event.getAction() == MotionEvent.ACTION_POINTER_1_UP
					|| event.getAction() == MotionEvent.ACTION_POINTER_2_UP) {

				v.setBackgroundColor(Color.alpha(0));
			}
			return false;
		}
	};

	/**
	 * 事件
	 * 
	 * @param macivity
	 * @param id
	 * @param parentId
	 * @return
	 */
	private int REQUEST_CODE = 2;

	public void invokeEvent(int id, int parentId) {

		switch (parentId) {
		case 1:
			switch (id) {
			case 10: // E家套餐
			case 11: // 天翼套餐
				finish();
				break;
			case 12: // 问问
				finish();
				break;
			}

			break;
		case 2:
			switch (id) {
			case 20: // 天翼套餐
				finish();
				break;
			case 21: // E家办理
				break;
			case 22: // 增值业务

				break;
			case 23: // 程控功能

				break;
			case 24: // 优惠促销

				break;
			}

			break;
		case 3:
			switch (id) {
			case 30: // 基本信息

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

				break;
			case 51: // 积分消费历史
				break;
			}
			break;
		case 6:
			switch (id) {
			case 60: // 电子钱包缴费

				break;
			case 61: // 为客户缴费
				break;
			}
			break;
		case 7:
			switch (id) {
			case 70: // 待续约宽带查询

				break;
			case 71: // 宽带续约
				break;
			}
			break;
		case 8:
			switch (id) {
			case 80: // 外线工单处理

				break;
			}
			break;
		case 9:
			switch (id) {
			case 90: // 号码归属地查询

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
				intent = new Intent();
				intent.setClass(macivity, OnLineJob.class);
				intent.setData(Uri.parse(id + "," + parentId));
				startActivity(intent);
				finish();
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
	}
}
