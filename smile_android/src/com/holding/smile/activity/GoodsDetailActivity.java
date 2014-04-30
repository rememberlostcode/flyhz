
package com.holding.smile.activity;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
	private TextView		b;										// 品牌名
	private TextView		n;										// 商品名
	private TextView		pp;									// 价格
	private TextView		d;										// 描述
	private TextView		scolor;								// 选择的颜色
	private List<JGoods>	details		= new ArrayList<JGoods>();
	private JGoods			jGoods		= null;
	private List<String>	picList		= new ArrayList<String>();
	private List<JColor>	colorList	= new ArrayList<JColor>();	// 相同款号的商品颜色列表
	private Integer			gid			= null;					// 商品ID
	private String			bs			= null;					// 款号

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ImageView backBtn = displayHeaderBack();
		backBtn.setOnClickListener(this);

		TextView headerDesc = displayHeaderDescription();
		headerDesc.setText(R.string.good_detail);

		Intent intent = getIntent();
		bs = intent.getExtras().getString("bs");
		gid = (Integer) intent.getExtras().getSerializable("gid");
		if (StrUtils.isNotEmpty(bs)) {
			startTask();
		} else {
			Toast.makeText(context, Constants.MESSAGE_NET, Toast.LENGTH_SHORT).show();
		}

	}

	private void initView() {
		if (details != null && !details.isEmpty()) {
			setContentLayout(R.layout.goods_detail_view);
			// 底部布局
			View view = displayFooterMainBuyBtn();
			View nowBuy = view.findViewById(R.id.nowbuy);
			View addCart = view.findViewById(R.id.addcart);
			nowBuy.setOnClickListener(this);
			addCart.setOnClickListener(this);

			b = (TextView) findViewById(R.id.b);
			n = (TextView) findViewById(R.id.n);
			pp = (TextView) findViewById(R.id.pp);
			d = (TextView) findViewById(R.id.d);
			scolor = (TextView) findViewById(R.id.select_color);
			initGoods();
			// 加载颜色
			loadColors();
		}
	}

	@Override
	public void loadData() {
		RtnValueDto rtnValue = MyApplication.getInstance().getDataService().getGoodsDetail(bs);
		if (rtnValue != null) {
			Message msg = mUIHandler.obtainMessage(0);
			msg.obj = rtnValue;
			msg.sendToTarget();
		} else {
			Toast.makeText(context, "暂无数据", Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 初始化产品
	 * 
	 */
	private void initGoods() {
		picList.clear();
		if (details != null && !details.isEmpty()) {
			if (gid != null) {
				for (JGoods g : details) {
					if (gid.equals(g.getId())) {
						jGoods = g;
						break;
					}
				}
			} else {
				jGoods = details.get(0);
			}

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

			if (jGoods.getC() != null) {
				scolor.setText(jGoods.getC());
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
	private void loadColors() {
		if (jGoods == null)
			return;

		// 处理同款颜色
		for (JGoods g : details) {
			if (g.getC() != null) {
				JColor jcolor = new JColor();
				jcolor.setId(g.getId());
				jcolor.setC(g.getC());
				jcolor.setCi(g.getCi());
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
						scolor.setText(color.getC());
						initGoods();
					}
				}
			});
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_back: {
				finish();
				break;
			}
			case R.id.nowbuy: {
				Intent intent = new Intent(this, OrderInformActivity.class);
				intent.putExtra("gid", gid);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				break;
			}
			case R.id.addcart: {
				RtnValueDto rtnValue = MyApplication.getInstance().getSubmitService()
													.addCart(gid, (short) 1);
				if (rtnValue != null) {
					Toast.makeText(context, "已加入购物车", Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(context, Constants.MESSAGE_NET, Toast.LENGTH_SHORT).show();
				}
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
				imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
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

	@SuppressLint("HandlerLeak")
	private final Handler	mUIHandler	= new Handler() {

											@Override
											public void handleMessage(Message msg) {
												progressBar.setVisibility(View.GONE);
												switch (msg.what) {
													case 0: {
														if (msg.obj != null) {
															RtnValueDto obj = (RtnValueDto) msg.obj;
															details = obj.getData();
															if (details == null) {
																if (obj.getValidate() != null
																		&& StrUtils.isNotEmpty(obj.getValidate()
																									.getMessage())) {
																	Toast.makeText(
																			context,
																			obj.getValidate()
																				.getMessage(),
																			Toast.LENGTH_SHORT)
																			.show();
																} else {
																	Toast.makeText(context,
																			Constants.MESSAGE_NET,
																			Toast.LENGTH_SHORT)
																			.show();
																}
															} else {
																initView();
															}
														} else {
															Toast.makeText(context, "暂无数据",
																	Toast.LENGTH_SHORT).show();
														}
														break;
													}
												}
											}
										};

}
