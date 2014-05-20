
package com.holding.smile.myview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TableLayout;

/**
 * 
 * 类说明：自定义表格布局
 * 
 * @author robin 2014-4-22下午12:22:22
 * 
 */
public class MyTableLayout extends TableLayout {
	private Context	context;

	public MyTableLayout(Context context) {
		super(context);
		this.context = context;
	}

	public MyTableLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// 注意这里,主要是把高度值改动了
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}

}
