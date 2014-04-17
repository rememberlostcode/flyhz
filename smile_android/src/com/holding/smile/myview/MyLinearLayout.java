
package com.holding.smile.myview;

import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.holding.smile.R;
import com.holding.smile.entity.JSort;
import com.holding.smile.entity.SortType;

/**
 * 
 * 类说明：自定义动态按扭布局
 * 
 * @author robin 2014-4-11下午1:11:21
 * 
 */
public class MyLinearLayout extends LinearLayout {
	private Context	context;

	public MyLinearLayout(Context context) {
		super(context);
		this.context = context;
	}

	public MyLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setSortTypeList(Context mcontext, List<SortType> sortTypeList) {
		this.context = mcontext;
		if (sortTypeList != null && !sortTypeList.isEmpty()) {
			LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			int size = sortTypeList.size();
			for (int i = 0; i < size; i++) {
				View v = mInflater.inflate(R.layout.myself_layout, null);
				SortType sorttype = sortTypeList.get(i);
				TextView tv = (TextView) v.findViewById(R.id.my_text);
				tv.setText(sorttype.getN());
				v.setId(i);
				v.setTag(sorttype.getV());
				if (size == 1) {
					tv.setBackgroundResource(R.drawable.btn_green_pressed);
					v.setBackgroundResource(R.drawable.corners_bg);
				} else {
					if (i == 0) {
						tv.setBackgroundResource(R.drawable.btn_green_pressed);
						v.setBackgroundResource(R.drawable.left_corners_bg);
					} else if (i == (size - 1)) {
						v.setBackgroundResource(R.drawable.right_corners_bg);
					}
				}
				this.addView(v);
			}
		}
	}

	public void setJSortList(Context context, List<JSort> jSortList) {
		this.context = context;
		if (jSortList != null && !jSortList.isEmpty()) {
			LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			int size = jSortList.size();
			for (int i = 0; i < size; i++) {
				View v = mInflater.inflate(R.layout.myself_layout, null);
				JSort sort = jSortList.get(i);
				TextView tv = (TextView) v.findViewById(R.id.my_text);
				tv.setText(sort.getN());
				v.setId(i);
				v.setTag(sort.getU());
				if (size == 1) {
					tv.setBackgroundResource(R.drawable.btn_green_pressed);
					v.setBackgroundResource(R.drawable.corners_bg);
				} else {
					if (i == 0) {
						tv.setBackgroundResource(R.drawable.btn_green_pressed);
						v.setBackgroundResource(R.drawable.left_corners_bg);
					} else if (i == (size - 1)) {
						v.setBackgroundResource(R.drawable.right_corners_bg);
					}
				}
				this.addView(v);
			}
		}
	}

	/**
	 * 设置按下，其它不按下
	 * 
	 * @param postion
	 */
	public void setBackgroundBtn(int postion) {
		int childCount = getChildCount();
		for (int i = 0; i < childCount; i++) {
			RelativeLayout layout = (RelativeLayout) getChildAt(i);
			View v = layout.getChildAt(0);
			if (i == postion) {
				v.setBackgroundResource(R.drawable.btn_green_pressed);
			} else {
				v.setBackgroundResource(R.drawable.btn_white_normal);
			}
		}
	}
}
