package com.ztesoft.mobile.core;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.ztesoft.mobile.R;
import com.ztesoft.mobile.menu.MenuActivity;
import com.ztesoft.mobile.update.AutoUpdate;
import com.ztesoft.mobile.widget.Button.CmImageButton;

public class MobileCrm extends BaseActivity {

	private LinearLayout mainLayout01;

	private int mWidth, mHeight;

	private final static int IMG_WIDTH = 80;

	private final int WC = LinearLayout.LayoutParams.WRAP_CONTENT;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState, R.layout.mobile_crm);
		mainLayout01 = (LinearLayout) this.findViewById(R.id.menus);

		initMenus();
		// CheckNetwork();

	}

	private void initMenus() {
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);

		mWidth = dm.widthPixels;
		mHeight = dm.heightPixels;

		LinearLayout l1 = new LinearLayout(this);
		LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(WC, WC);

		int n = mWidth / IMG_WIDTH;

		boolean firstFlag = false;
		for (int i = 0; i < 11; i++) {
			if (i % n == 0) {
				l1 = createLayout();
				mainLayout01.addView(l1);
				firstFlag = true;
			}
			CmImageButton btnImg = createImageButton(i + 1, firstFlag);
			l1.addView(btnImg, param);
			firstFlag = false;
		}

	}

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
	 * @param firstFlag
	 * @return
	 */
	private CmImageButton createImageButton(int id, boolean firstFlag) {
		CmImageButton ib01 = new CmImageButton(this, "");
		ib01.setId(id);
		Drawable dw2 = getDrawableById(id);

		ib01.setImageDrawable(dw2);
		LinearLayout.LayoutParams imbutton01 = new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

		if (firstFlag && id > 1) {

			ImageButton bfImb = (ImageButton) this.findViewById(id - 1);
			LinearLayout.LayoutParams tmpLp = (LinearLayout.LayoutParams) bfImb
					.getLayoutParams();
			tmpLp.weight = 1f;
		} else {
			imbutton01.weight = 0.3f;
		}

		ib01.setLayoutParams(imbutton01);
		ib01.setOnClickListener(listener);
		ib01.setOnTouchListener(touchLight);
		ib01.setBackgroundColor(Color.alpha(1));

		return ib01;

	}

	private Drawable getDrawableById(int id) {
		Drawable dw2 = null;
		switch (id) {
		case 1:
			dw2 = this.getResources().getDrawable(R.drawable.menu_xl); // 前置
			break;
		case 2:
			dw2 = this.getResources().getDrawable(R.drawable.menu_fashion); // 业务受理
			break;
		case 3:
			dw2 = this.getResources().getDrawable(R.drawable.menu_fee_yo); // 客户查询
			break;
		case 4:
			dw2 = this.getResources().getDrawable(R.drawable.menu_my_unorder); // 费用查询
			break;
		case 5:
			dw2 = this.getResources().getDrawable(R.drawable.menu_ninfo_ct); // 积分查询
			break;
		case 6:
			dw2 = this.getResources().getDrawable(R.drawable.menu_fashion); // 缴费
			break;
		case 7:
			dw2 = this.getResources().getDrawable(R.drawable.menu_fee_yo); // 宽带续约
			break;
		case 8:
			dw2 = this.getResources().getDrawable(R.drawable.menu_my_unorder); // 外线工单处理
			break;
		case 9:
			dw2 = this.getResources().getDrawable(R.drawable.menu_ninfo_ct); // 常用信息查询
			break;
		case 10:
			dw2 = this.getResources().getDrawable(R.drawable.menu_ql); // 我的信息
			break;
		case 11:
			dw2 = this.getResources().getDrawable(R.drawable.menu_return); // 退出
			break;
		}

		return dw2;
	}

	private int REQUEST_CODE = 2;
	// 设置按钮事件
	final OnClickListener listener = new OnClickListener() {

		public void onClick(View v) {

			if (v.getId() != 11) {
				String sId = v.getId() + "";
				Intent intent = new Intent();
				intent.setClass(MobileCrm.this, MenuActivity.class);// 指定了跳转前的Activity和跳转后的Activity
				intent.setData(Uri.parse(sId));// 向下一个Activity传递了string类型参数"one
				startActivityForResult(intent, REQUEST_CODE);// 以传递参数的方式跳转到下一个Activity
				// finish();// 如果不结束，那么这个Activity将被压在下一个Activity之下
			} else if (v.getId() == 11) {
				// finish();// 如果不结束，那么这个Activity将被压在下一个Activity之下
			}
		}
	};

	/**
	 * mouse touch事件
	 */
	final OnTouchListener touchLight = new OnTouchListener() {

		public float[] BT_SELECTED = new float[] { 1, 0, 0, 0, 90, 0, 1, 67, 0,
				50, 0, 0, 1, 0, 90, 0, 0, 80, 1, 0 };
		public float[] BT_NOT_SELECTED = new float[] { 1, 0, 0, 0, 0, 0, 1, 0,
				0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0 };

		public boolean onTouch(View v, MotionEvent event) {
			if (event.getAction() == MotionEvent.ACTION_DOWN
					|| event.getAction() == MotionEvent.ACTION_POINTER_DOWN
					|| event.getAction() == MotionEvent.ACTION_POINTER_1_DOWN
					|| event.getAction() == MotionEvent.ACTION_POINTER_2_DOWN
					|| event.getAction() == MotionEvent.ACTION_MOVE) {
				v.getBackground().setColorFilter(
						new ColorMatrixColorFilter(BT_SELECTED));
				v.setBackgroundDrawable(v.getBackground());
				v.setBackgroundColor(Color.rgb(255, 165, 0));
			} else if (event.getAction() == MotionEvent.ACTION_UP
					|| event.getAction() == MotionEvent.ACTION_POINTER_1_UP
					|| event.getAction() == MotionEvent.ACTION_POINTER_2_UP) {
				v.getBackground().setColorFilter(
						new ColorMatrixColorFilter(BT_NOT_SELECTED));
				v.setBackgroundDrawable(v.getBackground());
				v.setBackgroundColor(Color.alpha(0));
			}
			return false;
		}
	};

	/**
	 * focus change
	 */
	final OnFocusChangeListener focusListener = new OnFocusChangeListener() {

		public final float[] BT_SELECTED = new float[] { 1, 0, 0, 0, 50, 0, 1,
				0, 0, 50, 0, 0, 1, 0, 50, 0, 0, 0, 1, 0 };
		public final float[] BT_NOT_SELECTED = new float[] { 1, 0, 0, 0, 0, 0,
				1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0 };

		public void onFocusChange(View v, boolean hasFocus) {
			if (hasFocus) {
				v.getBackground().setColorFilter(
						new ColorMatrixColorFilter(BT_SELECTED));
				v.setBackgroundDrawable(v.getBackground());
			} else {
				v.getBackground().setColorFilter(
						new ColorMatrixColorFilter(BT_NOT_SELECTED));
				v.setBackgroundDrawable(v.getBackground());
			}
		}

	};

	/**
	 * 检查版本更新
	 */
	public void checkUpdate() {
		if (!CheckNetwork()) {
			return;
		}
		int remoteVersion = 0;
		if (App.versionCode != remoteVersion) {// 更新
			Intent intent = new Intent();
			intent.setClass(MobileCrm.this, AutoUpdate.class);
			startActivity(intent);
		}
	}
}