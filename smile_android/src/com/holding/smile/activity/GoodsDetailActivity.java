
package com.holding.smile.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.holding.smile.R;
import com.holding.smile.adapter.MyPagerAdapter;
import com.holding.smile.dto.RtnValueDto;
import com.holding.smile.entity.JColor;
import com.holding.smile.entity.JGoods;
import com.holding.smile.myview.MyLinearLayout;
import com.holding.smile.myview.MyViewPager;
import com.holding.smile.tools.Constants;
import com.holding.smile.tools.StrUtils;

/**
 * 
 * 类说明：物品详情页
 * 
 * @author robin 2014-2-25下午1:16:56
 * 
 */
public class GoodsDetailActivity extends BaseActivity implements OnClickListener {

	private MyViewPager		mViewPager;
	private List<View>		viewList	= new ArrayList<View>();
	private MyPagerAdapter	pagerAdapter;
	// 装点点的ImageView数组
	private ImageView[]		tips;
	private JGoods			jGoods		= null;
	private List<String>	picList		= new ArrayList<String>();
	private List<JColor>	colorList	= new ArrayList<JColor>();	// 相同款号的商品颜色列表
	private Integer			gid			= null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ImageView backBtn = displayHeaderBack();
		backBtn.setOnClickListener(this);

		TextView headerDesc = displayHeaderDescription();
		headerDesc.setText(R.string.good_detail);

		Intent intent = getIntent();
		gid = (Integer) intent.getExtras().getSerializable("gid");
		if (gid != null) {
			initGoods();
			// 加载颜色
			loadColors(jGoods);
		} else {
			Toast.makeText(context, Constants.MESSAGE_NET, Toast.LENGTH_SHORT).show();
		}

	}

	/**
	 * 初始化产品
	 * 
	 * @param gid
	 */
	private void initGoods() {
		picList.clear();
		RtnValueDto rtnValue = MyApplication.getInstance().getDataService().getGoodsDetail(gid);
		if (rtnValue != null) {
			jGoods = rtnValue.getGoodDetail();
			if (jGoods == null) {
				if (rtnValue.getValidate() != null
						&& StrUtils.isNotEmpty(rtnValue.getValidate().getMessage())) {
					Toast.makeText(context, rtnValue.getValidate().getMessage(), Toast.LENGTH_SHORT)
							.show();
				} else {
					Toast.makeText(context, Constants.MESSAGE_NET, Toast.LENGTH_SHORT).show();
				}
			}
		}

		if (jGoods != null) {
			setContentLayout(R.layout.goods_detail_view);
			// 底部布局
			View view = displayFooterMainBuyBtn();
			View nowBuy = view.findViewById(R.id.nowbuy);
			View addCart = view.findViewById(R.id.addcart);
			nowBuy.setOnClickListener(this);
			addCart.setOnClickListener(this);
			TextView b = (TextView) findViewById(R.id.b);
			TextView n = (TextView) findViewById(R.id.n);
			TextView pp = (TextView) findViewById(R.id.pp);
			TextView d = (TextView) findViewById(R.id.d);

			// colorView = (MyGridView) findViewById(R.id.grid_color);

			if (jGoods.getBe() != null && !"".equals(jGoods.getBe().trim())) {
				b.setText(jGoods.getBe().trim());
			}
			if (jGoods.getN() != null && !"".equals(jGoods.getN().trim())) {
				n.setText(jGoods.getN().trim());
			}
			if (jGoods.getPp() != null) {
				pp.setText("￥" + jGoods.getPp());
			}
			// if (jGoods.getLp() != null) {
			// po.setText("￥" + jGoods.getLp());
			// po.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);// 中间横线
			// }
			// if (jGoods.getSp() != null) {
			// save.setText("省￥" + jGoods.getSp());
			// }
			if (jGoods.getD() != null && !"".equals(jGoods.getD().trim())) {
				d.setText(jGoods.getD().trim());
			}
			if (jGoods.getP() != null) {
				for (int i = 0; i < jGoods.getP().length; i++) {
					picList.add(jGoods.getP()[i]);
				}
			}

			if (mViewPager == null)
				mViewPager = (MyViewPager) findViewById(R.id.mypicpager);

			addViewPager();// 添加页卡
			if (pagerAdapter == null) {
				// 实例化适配器
				pagerAdapter = new MyPagerAdapter(viewList);
				mViewPager.setAdapter(pagerAdapter);
			} else {
				pagerAdapter.setListViews(viewList);
			}
			mViewPager.setCurrentItem(0); // 设置默认当前页
		}
	}

	/**
	 * 设置颜色标签布局
	 */
	private void loadColors(JGoods jGoods) {
		if (jGoods == null)
			return;

		colorList.clear();
		// 处理同款颜色
		if (jGoods.getAg() != null) {
			Integer[] ag = jGoods.getAg();
			int size = ag.length;
			for (int i = 0; i < size; i++) {
				JColor jcolor = new JColor();
				jcolor.setId(ag[i]);
				jcolor.setColor("颜色" + i);
				colorList.add(jcolor);
			}
		}

		final MyLinearLayout colorLayout = (MyLinearLayout) findViewById(R.id.color_list);
		colorLayout.setColorList(context, colorList);
		int childCount = colorLayout.getChildCount();
		for (int i = 0; i < childCount; i++) {
			View v = colorLayout.getChildAt(i);
			v.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					JColor color = colorList.get(v.getId());
					if (color != null) {
						gid = color.getId();
						initGoods();
						Toast.makeText(context, "您选择了" + color.getColor() + "色", Toast.LENGTH_SHORT)
								.show();
					}
				}
			});
		}

		// if (colorAdapter == null)
		// colorAdapter = new ColorAdapter(context, colorList);
		// colorAdapter.notifyDataSetChanged();

		// if (colorView != null) {
		// int ii = colorAdapter.getCount();
		// colorView.setNumColumns(ii);
		// colorView.setAdapter(colorAdapter);
		// colorView.setOnItemClickListener(new OnItemClickListener() {
		//
		// @Override
		// public void onItemClick(AdapterView<?> parent, View view, int
		// position, long id) {
		// // TODO Auto-generated method stub
		// JColor color = colorList.get(position);
		// if (color != null) {
		// gid = color.getId();
		// initGoods();
		// Toast.makeText(context, "您选择了" + color.getColor() + "色",
		// Toast.LENGTH_SHORT)
		// .show();
		// }
		// }
		// });
		// }
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_back: {
				finish();
				break;
			}
			case R.id.nowbuy: {
				Toast.makeText(context, "您点了立即购买", Toast.LENGTH_SHORT).show();
				// Intent intent = new Intent(this, SearchGoodsActivity.class);
				// intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				// startActivity(intent);
				// finish();
				break;
			}
			case R.id.addcart: {
				Toast.makeText(context, "您点了加入购物车", Toast.LENGTH_SHORT).show();
				// Intent intent = new Intent(this, SearchGoodsActivity.class);
				// intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				// startActivity(intent);
				break;
			}
		}
		super.onClick(v);
	}

	/**
	 * 添加页卡
	 */
	private void addViewPager() {
		viewList.clear();
		LayoutInflater inflater = getLayoutInflater();
		// 添加页卡数据
		if (picList != null && !picList.isEmpty()) {
			float density = MyApplication.getInstance().getDensity();
			int cWidth = (int) (MyApplication.getInstance().getScreenWidth() * density);
			int size = picList.size();
			for (int i = 0; i < size; i++) {
				View view = inflater.inflate(R.layout.good_pic_item, null);
				view.setLayoutParams(new LayoutParams(cWidth, cWidth));
				ImageView imageView = (ImageView) view.findViewById(R.id.good_pic);
				imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
				imageView.setMaxHeight(cWidth);
				imageView.setMinimumHeight(cWidth);
				imageView.setTag(MyApplication.jgoods_img_url + picList.get(i));
				viewList.add(view);
			}

			ViewGroup group = (ViewGroup) findViewById(R.id.view_group);
			group.removeAllViews();
			// 将点点加入到ViewGroup中
			tips = new ImageView[size];
			for (int i = 0; i < tips.length; i++) {
				ImageView imageView = new ImageView(this);
				imageView.setLayoutParams(new LayoutParams(10, 10));
				tips[i] = imageView;
				if (i == 0) {
					tips[i].setBackgroundResource(R.drawable.page_indicator_focused);
				} else {
					tips[i].setBackgroundResource(R.drawable.page_indicator_unfocused);
				}

				LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
						new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT,
								LayoutParams.WRAP_CONTENT));
				layoutParams.leftMargin = 5;
				layoutParams.rightMargin = 5;
				group.addView(imageView, layoutParams);
			}
		}

		// 设置监听，主要是设置点点的背景
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				setImageBackground(arg0 % picList.size());
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}

			/**
			 * 设置选中的tip的背景
			 * 
			 * @param selectItems
			 */
			private void setImageBackground(int selectItems) {
				for (int i = 0; i < tips.length; i++) {
					if (i == selectItems) {
						tips[i].setBackgroundResource(R.drawable.page_indicator_focused);
					} else {
						tips[i].setBackgroundResource(R.drawable.page_indicator_unfocused);
					}
				}
			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mViewPager != null) {
			mViewPager.invalidate();
			mViewPager = null;
		}
		if (pagerAdapter != null) {
			pagerAdapter.notifyDataSetChanged();
			pagerAdapter = null;
		}
		picList.clear();
		picList = null;
		tips = null;
		setResult(RESULT_CANCELED, null);
	}
}
