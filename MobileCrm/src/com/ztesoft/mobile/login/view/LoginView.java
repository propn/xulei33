package com.ztesoft.mobile.login.view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.ztesoft.mobile.R;

public class LoginView extends FrameLayout {

	protected Button btnLogin;
	protected Button btnCancel;

	protected EditText edtUsername;
	protected EditText edtPassword;

	protected View progress;

	private OnLoginListener onLoginListener;

	private LoginThread loginThread;

	protected Boolean isLoginCanceled;

	protected long timeout = 0;

	public LoginView(Context context) {
		super(context);
		initViews();
	}

	public LoginView(Context context, Long timeout) {
		super(context);
		this.timeout = timeout;
		initViews();
	}

	private void initViews() {

		inflate(getContext(), R.layout.login_view, this);

		btnLogin = (Button) findViewById(R.id.login_view_login);
		btnCancel = (Button) findViewById(R.id.login_view_cancel);

		edtUsername = (EditText) findViewById(R.id.login_view_username);
		edtPassword = (EditText) findViewById(R.id.login_view_password);

		btnLogin.setOnClickListener(new LoginButtonListener());

		progress = findViewById(R.id.login_view_progress);

		isLoginCanceled = false;
	}

	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}

	public long getTimeout() {
		return timeout;
	}

	public void setOnLoginListener(OnLoginListener l) {
		onLoginListener = l;
	}

	public void setCancelButtonOnClickListener(OnClickListener l) {
		btnCancel.setOnClickListener(l);
	}

	public void setLoginButtonOnClickListener(OnClickListener l) {
		btnLogin.setOnClickListener(l);
	}

	private void setEnable(boolean flag) {
		// set TextView Enable as flag
		edtUsername.setEnabled(flag);
		edtPassword.setEnabled(flag);
		// set Button Enable as flag
		btnLogin.setEnabled(flag);
		btnCancel.setEnabled(flag);
	}

	private void setOnLoging(boolean flag) {
		setEnable(!flag);
		if (flag) {
			// while loging, progress's visible
			progress.setVisibility(VISIBLE);
		} else {
			progress.setVisibility(GONE);
		}
	}

	private class LoginButtonListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			if (onLoginListener != null) {
				isLoginCanceled = false;
				if (timeout != 0) {
					handler.sendEmptyMessageDelayed(LOGIN_TIMEOUT, timeout);
				}

				loginThread = new LoginThread();
				loginThread.start();
			}
		}
	}

	protected class LoginThread extends Thread {
		@Override
		public void run() {
			
			handler.sendEmptyMessage(SET_ONLOGIN_TRUE);

			boolean flag = onLoginListener.onLogin(LoginView.this, edtUsername
					.getText().toString(), edtPassword.getText().toString());

			if (isLoginCanceled) {
				return;
			}

			if (flag) {
				handler.sendEmptyMessage(LOGIN_SUCCESS);
			} else {
				handler.sendEmptyMessage(LOGIN_FAILED);
			}

			handler.sendEmptyMessage(SET_ONLOGIN_FALSE);
		}
	};

	public void loginCancel() {
		isLoginCanceled = true;
	}

	private final int SET_ONLOGIN_FALSE = 1000;
	private final int SET_ONLOGIN_TRUE = 1001;
	private final int LOGIN_TIMEOUT = 1002;
	private final int LOGIN_SUCCESS = 1003;
	private final int LOGIN_FAILED = 1004;

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SET_ONLOGIN_TRUE:
				setOnLoging(true);
				break;
			case SET_ONLOGIN_FALSE:
				setOnLoging(false);
				break;
			case LOGIN_TIMEOUT:
				onLoginListener.onLoginTimeout(LoginView.this);
				loginCancel();
				sendEmptyMessage(SET_ONLOGIN_FALSE);
				break;
			case LOGIN_SUCCESS:
				onLoginListener.onLoginSuccess(LoginView.this);
				break;
			case LOGIN_FAILED:
				onLoginListener.onLoginFailed(LoginView.this);
				break;
			}
			super.handleMessage(msg);
		}
	};

}
