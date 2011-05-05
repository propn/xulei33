/**
 * 带label的editor TextView
 */
package com.ztesoft.mobile.widget.labeledit;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ztesoft.mobile.R;

/**
 * @author Thunder.xu
 * 
 */
public class LabelEditText extends LinearLayout {

	private final String namespace = "http://org.leixu.android";

	private final String lableText = "lableText";
	private final String lableFontSize = "lableFontSize";
	private final String lablePosition = "lablePosition";
	private final String hint = "hint";

	private TextView textView;
	private EditText edittext;

	private String mLableText;
	private int mLableFontSize;
	private String mLablePosition;
	private String mHint;
	private String mInputType;

	public LabelEditText(Context context, AttributeSet attrs) {
		super(context, attrs);

		// 获取lableText
		int resourceId = attrs.getAttributeResourceValue(namespace, lableText,
				0);
		if (resourceId == 0) {// 是字符串
			mLableText = attrs.getAttributeValue(namespace, lableText);
		} else {// 是资源
			mLableText = getResources().getString(resourceId);
		}

		if (null == mLableText) {
			throw new RuntimeException("必须设置lableText属性值");
		}

		// 获取lableFontSize
		resourceId = attrs.getAttributeResourceValue(namespace, lableFontSize,
				0);
		if (resourceId == 0) {// 是字符串
			mLableFontSize = attrs.getAttributeIntValue(namespace,
					lableFontSize, 14);
		} else {// 是资源
			mLableFontSize = getResources().getInteger(resourceId);
		}

		// 获取lablePosition
		resourceId = attrs.getAttributeResourceValue(namespace, lablePosition,
				0);
		if (resourceId == 0) {// 是字符串
			mLablePosition = attrs.getAttributeValue(namespace, lablePosition);
		} else {// 是资源
			mLablePosition = getResources().getString(resourceId);
		}
		if (null == mLablePosition) {
			mLablePosition = "left";
		}

		// 加载layout定义
		String infService = Context.LAYOUT_INFLATER_SERVICE;
		LayoutInflater li;
		li = (LayoutInflater) context.getSystemService(infService);
		// 根据lablePosition装载不同的布局文件
		if ("left".equals(mLablePosition)) {
			li.inflate(R.layout.labeledittext_horizontal, this);
		} else if ("top".equals(mLablePosition)) {
			li.inflate(R.layout.labeledittext_vertical, this);
		} else {
			throw new RuntimeException("lablePosition只能是 top 或left");
		}

		textView = (TextView) findViewById(R.id.textview);

		textView.setTextSize((float) mLableFontSize);
		textView.setTextSize(mLableFontSize);
		textView.setText(mLableText);

		edittext = (EditText) findViewById(R.id.edittext);
		// 获取hint
		resourceId = attrs.getAttributeResourceValue(namespace, hint, 0);
		if (resourceId == 0) {// 是字符串
			mHint = attrs.getAttributeValue(namespace, hint);
		} else {// 是资源
			mHint = getResources().getString(resourceId);
		}
		if (null != mHint) {
			edittext.setHint(mHint);
		}

	}
}
