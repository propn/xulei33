package com.ztesoft.mobile.widget.Button;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.widget.Button;

public class ImageTextButton extends Button {

	private final static int WIDTH_PADDING = 2;
	private final static int HEIGHT_PADDING = 4;
	private final static int SPACE = 10;
	private final String label;
	private final int imageResId;
	private final Bitmap image;
	private int fontWidth;
	private int fontHeight;

	public ImageTextButton(final Context c, int rid, String text) {
		super(c);
		this.label = text;
		this.imageResId = rid;
		this.image = BitmapFactory.decodeResource(c.getResources(), imageResId);
		setFocusable(true);
		setClickable(true);
		getFontWidthAndHeight();
	}

	private void getFontWidthAndHeight() {
		Paint pFont = new Paint();
		Rect rect = new Rect();
		pFont.getTextBounds("中", 0, 1, rect);
		this.fontHeight = rect.height();
		this.fontWidth = rect.width();
	}

	private int getTextWidth(String text) {
		return text.length() * this.fontWidth;
	}

	@Override
	protected void onFocusChanged(boolean gainFocus, int direction,
			Rect previouslyFocusedRect) {
		if (gainFocus == true) {
			this.setBackgroundColor(Color.rgb(255, 165, 0));
		} else {
			this.setBackgroundColor(Color.alpha(0));
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		Paint textPaint = new Paint();
		textPaint.setColor(Color.WHITE);

		canvas.drawBitmap(image, WIDTH_PADDING / 2, HEIGHT_PADDING / 2, null);
		canvas.drawText(label, (image.getWidth() / 2) / 2, (HEIGHT_PADDING / 2)
				+ image.getHeight() + 8 + SPACE, textPaint);
		this.setBackgroundColor(Color.alpha(0));
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		int action = event.getAction();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
		case MotionEvent.ACTION_MOVE:
		case MotionEvent.ACTION_POINTER_DOWN:
		case MotionEvent.ACTION_POINTER_2_DOWN:

			this.setBackgroundColor(Color.rgb(255, 165, 0));

			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_POINTER_1_UP:
		case MotionEvent.ACTION_POINTER_2_UP:

			this.setBackgroundColor(Color.alpha(0));
			break;
		}
		return super.onTouchEvent(event);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(measureWidth(widthMeasureSpec),
				measureHeight(heightMeasureSpec));
	}

	private int measureWidth(int measureSpec) {
		int preferred = image.getWidth() + 20;
		return getMeasurement(measureSpec, preferred);
	}

	private int measureHeight(int measureSpec) {
		int preferred = image.getHeight() + this.fontHeight + SPACE * 2;
		return getMeasurement(measureSpec, preferred);
	}

	private int getMeasurement(int measureSpec, int preferred) {
		int specSize = MeasureSpec.getSize(measureSpec);
		int measurement = 0;
		switch (MeasureSpec.getMode(measureSpec)) {
		case MeasureSpec.EXACTLY:
			measurement = specSize;
			break;
		case MeasureSpec.AT_MOST:
			measurement = Math.min(preferred, specSize);
			break;
		default:
			measurement = preferred;
			break;
		}
		return measurement;
	}

	public String getLabel() {
		return label;
	}

	public int getImageResId() {
		return imageResId;
	}

}
