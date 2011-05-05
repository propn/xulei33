package com.ztesoft.mobile.login.view;

import android.view.View;

public interface OnLoginListener {

	public boolean onLogin(View v, String username, String password);

	public void onLoginSuccess(View v);

	public void onLoginFailed(View v);

	public void onLoginTimeout(View v);
}
