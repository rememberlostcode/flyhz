
package com.holding.smile.myview;

import java.math.BigDecimal;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
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
import com.holding.smile.entity.JColor;
import com.holding.smile.entity.JDiscount;
import com.holding.smile.tools.StrUtils;

/**
 * 
 * 类说明：自定义表格布局
 * 
 * @author robin 2014-4-22下午12:22:22
 * 
 */
public class MyTableLayout extends TableLayout {

	private Context						context;

	private int							cWidth		= (int) (MyApplication.getInstance()
																			.getScreenWidth() - 55) / 3;

	private JDiscount					selectDiscount;

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

	public void setDiscountList(Context context, final TextView discountText,
			List<JDiscount> discountList, final BigDecimal pp) {
		this.context = context;
		if (discountList != null && !discountList.isEmpty()) {
			TableRow row = null;
			int count = 0;
			int size = discountList.size();
			double rows = ((double) size) / 3;// 计算行数，每行3列
			for (int i = 0; i < size; i++) {
				final JDiscount discount = discountList.get(i);
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
				col.setId(discount.getId());
				col.setText(discount.getDiscount() + "折");
				col.setTextSize(15);
				col.setGravity(Gravity.CENTER);
				col.setBackgroundResource(R.drawable.my_discount_bg);
				row.addView(col);// 添加列
				col.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						int rowCount = getChildCount();
						for (int i = 0; i < rowCount; i++) {
							TableRow row1 = (TableRow) getChildAt(i);
							int rowChildCount = row1.getChildCount();
							for (int j = 0; j < rowChildCount; j++) {
								row1.getChildAt(j).setSelected(false);
							}
						}
						if (selectDiscount != null
								&& !selectDiscount.getId().equals(discount.getId())) {
							selectDiscount = discount;
							if (selectDiscount != null) {
								double disconut = StrUtils.div(selectDiscount.getDiscount(), 10, 2);
								BigDecimal dp = pp.multiply(BigDecimal.valueOf(disconut));
								discountText.setText("￥" + dp.intValue() + "");
							}
							v.setSelected(true);
						}
					}
				});
				count += 1;
			}
			if (count != 3)
				this.addView(row);// 添加行
		}
	}

	public void setColorList(Context context, List<JColor> colorList) {
		this.context = context;
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
					Bitmap bitmap = MyApplication.getImageLoader().getBitmapFromCache(url);
					tv.setImageBitmap(bitmap);
					v.setTag(color.getCi());
				} else {
					tv.setImageResource(R.drawable.empty_photo);
				}
				v.setId(i);
				if (rows > 1) {
					v.setLayoutParams(paramsRow);
				} else {
					// v.setLayoutParams(paramsRow2);
				}
				row.addView(v);// 添加列
				count += 1;
			}
			if (count != 3)
				this.addView(row);// 添加行
		}
	}

	public JDiscount getSelectDiscount() {
		return selectDiscount;
	}

	public void setSelectDiscount(JDiscount selectDiscount) {
		this.selectDiscount = selectDiscount;
	}

}
