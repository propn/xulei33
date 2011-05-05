package com.ztesoft.mobile.widget.Button;

import android.content.Context;
import android.widget.RadioButton;

public class CmRadioButton extends RadioButton {

	private String mQuestionId = "";//
	private String mNextQuestionId = "";//

	public CmRadioButton(Context context, String questionId,
			String nextQuestionId) {
		super(context);
		this.mQuestionId = questionId;
		this.mNextQuestionId = nextQuestionId;
	}

	public String getQuestionId() {
		return mQuestionId;
	}

	public void setQuestionId(String mQuestionId) {
		this.mQuestionId = mQuestionId;
	}

	public String getNextQuestionId() {
		return mNextQuestionId;
	}

	public void setNextQuestionId(String mNextQuestionId) {
		this.mNextQuestionId = mNextQuestionId;
	}

}
