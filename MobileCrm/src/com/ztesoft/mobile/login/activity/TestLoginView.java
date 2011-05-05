package com.ztesoft.mobile.login.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.ztesoft.mobile.R;
import com.ztesoft.mobile.core.BaseActivity;
import com.ztesoft.mobile.login.view.LoginView;
import com.ztesoft.mobile.login.view.OnLoginListenerImpl;

public class TestLoginView extends BaseActivity {

	public static Button btnActivity;
	public static Button btnDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState, R.layout.login_test);

		btnActivity = (Button) findViewById(R.id.test_activity);
		btnActivity.setOnClickListener(new BtnActivityOnclikListener());

		btnDialog = (Button) findViewById(R.id.test_dialog);
		btnDialog.setOnClickListener(new BtnDialogOnClickListener());

	}

	/**
	 * 转向登录界面
	 * 
	 * @author Thunder.xu
	 */
	private class BtnActivityOnclikListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			intent.setClass(TestLoginView.this, LoginViewOnActivity.class);
			startActivity(intent);
		}
	}

	/**
	 * 弹出登录框
	 * 
	 * @author Thunder.xu
	 * 
	 */
	private class BtnDialogOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {

			final LoginView lv = new LoginView(TestLoginView.this);
			final Dialog dialog = new Dialog(TestLoginView.this);

			dialog.setContentView(lv);
			lv.setOnLoginListener(new OnLoginListenerImpl());
			lv.setCancelButtonOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});

			dialog.setTitle(getString(R.string.login_address));
			dialog.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					lv.loginCancel();
				}
			});
			dialog.show();
		}

	}
}