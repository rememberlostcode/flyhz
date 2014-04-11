
package com.holding.smile.myview;

import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.holding.smile.R;
import com.holding.smile.entity.SortType;

/**
 * 
 * 类说明：自定义动态布局
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
			for (SortType sorttype : sortTypeList) {
				View v = mInflater.inflate(R.layout.myself_layout, null);
				TextView tv = (TextView) v.findViewById(R.id.my_text);
				tv.setText(sorttype.getN());
				v.setTag(sorttype.getV());
				this.addView(v);
			}
		}
	}

}
