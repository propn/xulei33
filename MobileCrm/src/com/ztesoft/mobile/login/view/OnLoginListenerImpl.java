package com.ztesoft.mobile.login.view;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import com.ztesoft.mobile.R;
import com.ztesoft.mobile.login.activity.LoginSuccessActivity;

public class OnLoginListenerImpl implements OnLoginListener {

	protected Object session;

	public boolean onLogin(View v, String username, String password) {
		// for (int i = 0; i < 10000000; i++)
		// ;
		if (username.equals("timeout")) {
				while (true) {
			}
		}
		if (username.equals("androidin")) {
			return true;
		}

		return false;
	}

	public void onLoginFailed(final View v) {
		Toast.makeText(v.getContext(),
				v.getContext().getText(R.string.login_failed),
				Toast.LENGTH_LONG).show();

	}

	public void onLoginSuccess(View v) {
		Context context = v.getContext();
		context.startActivity(new Intent(context, LoginSuccessActivity.class));
	}

	public void onLoginTimeout(View v) {
		Toast.makeText(v.getContext(),
				v.getContext().getText(R.string.login_timeout),
				Toast.LENGTH_LONG).show();
	}
}
