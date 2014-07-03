
package com.holding.smile.myview;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

import com.holding.smile.activity.MyApplication;

public class MyViewPager extends ViewPager {

	private int		cWidth	= (int) MyApplication.getInstance().getScreenWidth();
	private boolean	imgFlag	= false;												// true为按长宽比1.9:1显示图片

	public MyViewPager(Context context) {
		super(context);
	}

	public MyViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		int height = 0;
		for (int i = 0; i < getChildCount(); i++) {
			View child = getChildAt(i);
			child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
			int h = child.getMeasuredHeight();
			if (imgFlag) {
				height = (int) (cWidth / 1.9);
			} else {
				if (h > cWidth) {
					height = cWidth;
				} else {
					height = cWidth;
				}
			}
		}

		heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);

		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	public boolean isImgFlag() {
		return imgFlag;
	}

	/**
	 * true为按长宽比1.9:1显示图片
	 * 
	 * @param imgFlag
	 */
	public void setImgFlag(boolean imgFlag) {
		this.imgFlag = imgFlag;
	}

}
