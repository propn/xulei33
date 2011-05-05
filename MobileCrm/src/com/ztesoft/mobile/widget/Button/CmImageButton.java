package com.ztesoft.mobile.widget.Button;

import android.content.Context;

import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CmImageButton extends ImageButton {

	private TextView txtView;

	private final int WC = LinearLayout.LayoutParams.WRAP_CONTENT;

	public CmImageButton(Context context, String text) {
		super(context);
		// TODO Auto-generated constructor stub
		int bottom = super.getBottom();
		// int height = super.getHeight();
		int left = super.getLeft();
		int right = super.getRight();
		int top = super.getTop();

		txtView = new TextView(context);
		top = top + txtView.getHeight();
		bottom = bottom + txtView.getHeight();

		LayoutParams params = new LayoutParams(WC, WC);

		txtView.setLayoutParams(params);

		txtView.setCompoundDrawablesWithIntrinsicBounds(left, top, right,
				bottom);
		txtView.setText(text);
	}

	public TextView getTextView() {
		return this.txtView;
	}
}
