
package com.holding.smile.myview;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.holding.smile.R;
import com.holding.smile.activity.MyApplication;
import com.holding.smile.dto.DiscountDto;
import com.holding.smile.entity.JColor;
import com.holding.smile.tools.StrUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 
 * 类说明：自定义表格布局
 * 
 * @author robin 2014-4-22下午12:22:22
 * 
 */
public class MyTableLayout extends TableLayout {

	private int							cWidth		= (int) (MyApplication.getInstance()
																			.getScreenWidth() - 55) / 3;

	@SuppressWarnings("deprecation")
	private TableLayout.LayoutParams	params		= new TableLayout.LayoutParams(
															TableLayout.LayoutParams.FILL_PARENT,
															TableLayout.LayoutParams.WRAP_CONTENT);

	private TableRow.LayoutParams		paramsRow	= new TableRow.LayoutParams(cWidth,
															TableLayout.LayoutParams.WRAP_CONTENT,
															1);
	private TableRow.LayoutParams		paramsRow2	= new TableRow.LayoutParams(cWidth,
															TableLayout.LayoutParams.WRAP_CONTENT);

	public MyTableLayout(Context context) {
		super(context);
	}

	public MyTableLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// 注意这里,主要是把高度值改动了
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}

	public void setDiscountList(Context context, List<DiscountDto> discountList) {
		if (discountList != null && !discountList.isEmpty()) {
			TableRow row = null;
			int count = 0;
			int size = discountList.size();
			double rows = ((double) size) / 3;// 计算行数，每行3列
			for (int i = 0; i < size; i++) {
				final DiscountDto discount = discountList.get(i);
				if (count == 0 || count == 3) {
					if (count == 3) {
						if (rows > 1)
							rows -= 1;
						this.addView(row);// 添加行
						count = 0;
					}
					row = new TableRow(context);
					row.setLayoutParams(params);
					row.setPadding(4, 4, 4, 4);
					row.setGravity(Gravity.LEFT);
				}
				// 添加列
				TextView col = new TextView(context);
				if (rows > 1) {
					col.setLayoutParams(paramsRow);
				} else {
					col.setLayoutParams(paramsRow2);
				}
				col.setId(i);
				col.setText(discount.getDiscount() + "折");
				col.setTextSize(15);
				col.setGravity(Gravity.CENTER);
				col.setBackgroundResource(R.drawable.my_discount_bg);
				row.addView(col);// 添加列
				count += 1;
			}
			if (count != 3)
				this.addView(row);// 添加行
		}
	}

	@SuppressLint("InflateParams")
	public void setColorList(Context context, List<JColor> colorList) {
		if (colorList != null && !colorList.isEmpty()) {
			LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			TableRow row = null;
			int count = 0;
			int size = colorList.size();
			double rows = ((double) size) / 3;// 计算行数，每行3列
			for (int i = 0; i < size; i++) {
				if (count == 0 || count == 3) {
					if (count == 3) {
						if (rows > 1)
							rows -= 1;
						this.addView(row);// 添加行
						count = 0;
					}
					row = new TableRow(context);
					row.setLayoutParams(params);
					row.setPadding(4, 4, 4, 4);
					row.setGravity(Gravity.LEFT);
				}

				// 添加列
				View v = mInflater.inflate(R.layout.color_item, null);
				JColor color = colorList.get(i);
				ImageView tv = (ImageView) v.findViewById(R.id.good_color);
				if (StrUtils.isNotEmpty(color.getCi())) {
					String url = MyApplication.jgoods_img_url + color.getCi();
					tv.setTag(url);
					// Bitmap bitmap =
					// MyApplication.getImageLoader().getBitmapFromCache(url);
					tv.setImageBitmap(ImageLoader.getInstance().loadImageSync(url));
					v.setTag(color.getCi());
				} else {
					tv.setImageResource(R.drawable.empty_photo);
				}
				v.setId(i);
				if (rows > 1) {
					v.setLayoutParams(paramsRow);
				} else {
					v.setLayoutParams(paramsRow2);
				}
				row.addView(v);// 添加列
				count += 1;
			}
			if (count != 3)
				this.addView(row);// 添加行
		}
	}

}
