package com.ztesoft.mobile.login.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.ztesoft.mobile.core.BaseActivity;
import com.ztesoft.mobile.login.view.LoginView;
import com.ztesoft.mobile.login.view.OnLoginListenerImpl;

public class LoginViewOnActivity extends BaseActivity {

	private LoginView logV;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		logV = new LoginView(this, 2000L);
		logV.setOnLoginListener(new OnLoginListenerImpl());

		logV.setCancelButtonOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		super.onCreate(savedInstanceState, logV);
	}

	@Override
	protected void onStop() {
		logV.loginCancel();
		super.onStop();
	}

}
