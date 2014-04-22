
package com.holding.smile.myview;

import java.util.Hashtable;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.holding.smile.R;
import com.holding.smile.activity.MyApplication;
import com.holding.smile.cache.ImageLoader;
import com.holding.smile.entity.JColor;
import com.holding.smile.entity.JSort;
import com.holding.smile.entity.SortType;
import com.holding.smile.tools.StrUtils;

/**
 * 
 * 类说明：自定义动态按扭布局
 * 
 * @author robin 2014-4-11下午1:11:21
 * 
 */
public class MyLinearLayout extends LinearLayout {
	int					mLeft, mRight, mTop, mBottom;
	private Hashtable	map				= new Hashtable();
	private ImageLoader	mImageLoader	= MyApplication.getImageLoader();
	private Context		context;

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

	public void setColorList(Context mcontext, List<JColor> colorList) {
		this.context = mcontext;
		if (colorList != null && !colorList.isEmpty()) {
			LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			int size = colorList.size();
			for (int i = 0; i < size; i++) {
				View v = mInflater.inflate(R.layout.color_item, null);
				JColor color = colorList.get(i);
				ImageView tv = (ImageView) v.findViewById(R.id.good_color);
				if (StrUtils.isNotEmpty(color.getColorimg())) {
					String url = MyApplication.jgoods_img_url + color.getColorimg();
					tv.setTag(url);
					mImageLoader.DisplayImage(url, tv, false);
					v.setTag(color.getColorimg());
				} else {
					tv.setImageResource(R.drawable.empty_photo);
				}
				v.setId(i);
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

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		int mWidth = MeasureSpec.getSize(widthMeasureSpec);
		int mCount = getChildCount();
		int mX = 0;
		int mY = 0;
		mLeft = 4;
		mRight = 4;
		mTop = 5;
		mBottom = 4;

		int j = 0;

		View lastview = null;
		for (int i = 0; i < mCount; i++) {
			final View child = getChildAt(i);

			child.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
			// 此处增加onlayout中的换行判断，用于计算所需的高度
			int childw = child.getMeasuredWidth();
			int childh = child.getMeasuredHeight();
			mX += childw; // 将每次子控件宽度进行统计叠加，如果大于设定的高度则需要换行，高度即Top坐标也需重新设置

			Position position = new Position();
			mLeft = getPosition(i - j, i);
			mRight = mLeft + child.getMeasuredWidth();
			if (mX >= mWidth) {
				mX = childw;
				mY += childh;
				j = i;
				mLeft = 4;
				mRight = mLeft + child.getMeasuredWidth();
				mTop = mY + 5;
				// PS：如果发现高度还是有问题就得自己再细调了
			}
			mBottom = mTop + child.getMeasuredHeight();
			mY = mTop; // 每次的高度必须记录 否则控件会叠加到一起
			position.left = mLeft;
			position.top = mTop + 3;
			position.right = mRight;
			position.bottom = mBottom;
			map.put(child, position);
		}
		setMeasuredDimension(mWidth, mBottom);
	}

	@Override
	protected LayoutParams generateDefaultLayoutParams() {
		return new LayoutParams(1, 1); // default of 1px spacing
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub

		int count = getChildCount();
		for (int i = 0; i < count; i++) {
			View child = getChildAt(i);
			Position pos = (Position) map.get(child);
			if (pos != null) {
				child.layout(pos.left, pos.top, pos.right, pos.bottom);
			} else {
				Log.i("MyLayout", "error");
			}
		}
	}

	private class Position {
		int	left, top, right, bottom;
	}

	public int getPosition(int IndexInRow, int childIndex) {
		if (IndexInRow > 0) {
			return getPosition(IndexInRow - 1, childIndex - 1)
					+ getChildAt(childIndex - 1).getMeasuredWidth() + 8;
		}
		return getPaddingLeft();
	}
}
