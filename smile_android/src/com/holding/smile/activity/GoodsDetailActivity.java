
package com.holding.smile.activity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.holding.smile.R;
import com.holding.smile.adapter.MyPagerAdapter;
import com.holding.smile.dto.DiscountDto;
import com.holding.smile.dto.RtnValueDto;
import com.holding.smile.entity.JColor;
import com.holding.smile.entity.JGoods;
import com.holding.smile.entity.SUser;
import com.holding.smile.myview.MyTableLayout;
import com.holding.smile.myview.MyViewPager;
import com.holding.smile.tools.CodeValidator;
import com.holding.smile.tools.StrUtils;
import com.holding.smile.tools.ToastUtils;

/**
 * 
 * 类说明：物品详情页
 * 
 * @author robin 2014-2-25下午1:16:56
 * 
 */
public class GoodsDetailActivity extends BaseActivity implements OnClickListener {

	private MyViewPager			mViewPager;
	private List<View>			viewList		= new ArrayList<View>();
	private MyPagerAdapter		pagerAdapter;
	// 装点点的ImageView数组
	private ImageView[]			tips;
	private TextView			b;												// 品牌名
	private TextView			p_num;											// 商品编号
	private TextView			n;												// 商品名
	private TextView			pp;											// 价格
	private TextView			lp;											// 原价格
	private TextView			discountPrice;									// 折扣价格
	private TextView			sn;											// 销售量
	private TextView			d;												// 描述
	private TextView			scolor;										// 选择的颜色
	private List<JGoods>		details			= new ArrayList<JGoods>();
	private JGoods				jGoods			= null;
	private List<String>		picList			= new ArrayList<String>();
	private ArrayList<String>		imgList			= new ArrayList<String>();
	private List<DiscountDto>	discountList	= new ArrayList<DiscountDto>(); // 相同款号的商品颜色列表
	private List<JColor>		colorList		= new ArrayList<JColor>();		// 相同款号的商品颜色列表
	private Integer				gid				= null;						// 商品ID
	private String				bs				= null;						// 款号
	private DiscountDto			selectDiscount	= null;						// 所选择的折扣

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ImageView backBtn = displayHeaderBack();
		backBtn.setOnClickListener(this);

		TextView headerDesc = displayHeaderDescription();
		headerDesc.setText(R.string.good_detail);

		Intent intent = getIntent();
		try {
			bs = intent.getExtras().getString("bs");
			gid = (Integer) intent.getExtras().getSerializable("gid");
		} catch (Exception e) {
			Log.e("参数转换异常：", e.getMessage());
		}
		if (StrUtils.isNotEmpty(bs)) {
			startTask();
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
			p_num = (TextView) findViewById(R.id.p_num);
			n = (TextView) findViewById(R.id.n);
			pp = (TextView) findViewById(R.id.pp);
			lp = (TextView) findViewById(R.id.lp);
			discountPrice = (TextView) findViewById(R.id.discount_p);
			sn = (TextView) findViewById(R.id.sn);
			d = (TextView) findViewById(R.id.d);
			scolor = (TextView) findViewById(R.id.select_color);
			initGoods();
			// 加载折扣
			loadDiscounts();
			// 加载颜色
			loadColors();
		}
	}

	@Override
	public synchronized void loadData() {
		RtnValueDto rtnValue = MyApplication.getInstance().getDataService().getGoodsDetail(bs);

		Message msg = mUIHandler.obtainMessage(0);
		msg.obj = rtnValue;
		msg.sendToTarget();
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

			if (jGoods == null)
				return;

			p_num.setText(bs);
			if (jGoods.getBe() != null && !"".equals(jGoods.getBe().trim())) {
				b.setText(jGoods.getBe().trim());
			}
			if (jGoods.getN() != null && !"".equals(jGoods.getN().trim())) {
				n.setText(jGoods.getN().trim());
			}
			if (jGoods.getPp() != null) {
				pp.setText("￥" + jGoods.getPp());
			}
			if (jGoods.getLp() != null) {
				lp.setText("￥" + jGoods.getLp());
				lp.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);// 中间横线
			}
			if (jGoods.getSn() != null) {
				sn.setText("已售 " + jGoods.getSn() + " 件");
			} else {
				sn.setText("已售 0 件");
			}
			if (jGoods.getD() != null && !"".equals(jGoods.getD().trim())) {
				d.setText(jGoods.getD().trim());
			}
			if (jGoods.getP() != null) {
				for (int i = 0; i < jGoods.getP().length; i++) {
					picList.add(jGoods.getP()[i]);
					imgList.add(jGoods.getBp()[i]);
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
				pagerAdapter = new MyPagerAdapter(viewList, false);
				mViewPager.setAdapter(pagerAdapter);
			} else {
				pagerAdapter.setListViews(viewList);
				int size = picList.size();
				for (int i = 0; i < size; i++) {
					View view = mViewPager.findViewWithTag("imRecord" + i);
					if (view != null) {
						pagerAdapter.disposeItem(view,
								MyApplication.jgoods_img_url + picList.get(i));
					}
				}

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
			JColor jcolor = new JColor();
			jcolor.setId(g.getId());
			if (g.getC() != null) {
				jcolor.setC(g.getC());
				jcolor.setCi(g.getCi());
			}
			colorList.add(jcolor);
		}

		// final MyLinearLayout colorLayout = (MyLinearLayout)
		// findViewById(R.id.color_list);
		// colorLayout.setColorList(context, colorList);
		// int childCount = colorLayout.getChildCount();
		// for (int i = 0; i < childCount; i++) {
		// View v = colorLayout.getChildAt(i);
		//
		// v.setOnClickListener(new View.OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// JColor color = colorList.get(v.getId());
		// if (color != null) {
		// gid = color.getId();
		// scolor.setText(color.getC());
		// initGoods();
		// }
		// }
		// });
		// }

		final MyTableLayout tableLayout = (MyTableLayout) findViewById(R.id.color_list);
		tableLayout.setColorList(context, colorList);
		final int rowCount = tableLayout.getChildCount();
		for (int i = 0; i < rowCount; i++) {
			TableRow row1 = (TableRow) tableLayout.getChildAt(i);
			int rowChildCount = row1.getChildCount();
			for (int j = 0; j < rowChildCount; j++) {
				View v = row1.getChildAt(j);
				v.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						for (int i = 0; i < rowCount; i++) {
							TableRow row1 = (TableRow) tableLayout.getChildAt(i);
							int rowChildCount = row1.getChildCount();
							for (int j = 0; j < rowChildCount; j++) {
								row1.getChildAt(j).setSelected(false);
							}
						}
						JColor color = colorList.get(v.getId());
						if (color != null) {
							gid = color.getId();
							scolor.setText(color.getC());
							initGoods();
						}
						v.setSelected(true);
					}
				});
			}
		}
	}

	/**
	 * 加载折扣布局
	 */
	private void loadDiscounts() {
		discountList.clear();
		if (jGoods == null)
			return;

		if (jGoods.getDiscounts() == null || jGoods.getDiscounts().length == 0) {
			return;
		}
		for (int i = 0; i < jGoods.getDiscounts().length; i++) {
			discountList.add(jGoods.getDiscounts()[i]);
		}

		final MyTableLayout tableLayout = (MyTableLayout) findViewById(R.id.discount_table);
		tableLayout.setDiscountList(context, discountList);
		final int rowCount = tableLayout.getChildCount();
		for (int i = 0; i < rowCount; i++) {
			TableRow row1 = (TableRow) tableLayout.getChildAt(i);
			int rowChildCount = row1.getChildCount();
			for (int j = 0; j < rowChildCount; j++) {
				View v = row1.getChildAt(j);
				v.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						for (int i = 0; i < rowCount; i++) {
							TableRow row1 = (TableRow) tableLayout.getChildAt(i);
							int rowChildCount = row1.getChildCount();
							for (int j = 0; j < rowChildCount; j++) {
								row1.getChildAt(j).setSelected(false);
							}
						}
						DiscountDto discount = discountList.get(v.getId());
						if (selectDiscount == null
								|| !discount.getId().equals(selectDiscount.getId())) {
							selectDiscount = discount;
							if (selectDiscount != null) {
								double val = StrUtils.div(selectDiscount.getDiscount(), 10, 2);
								BigDecimal dp = jGoods.getPp().multiply(BigDecimal.valueOf(val));
								discountPrice.setText("￥" + dp.intValue() + "");
							}
							v.setSelected(true);
						} else {
							selectDiscount = null;
							discountPrice.setText("");
						}
					}
				});
			}
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
				SUser user = MyApplication.getInstance().getCurrentUser();
				Intent intent = new Intent();
				if (user == null || MyApplication.getInstance().getSessionId() == null) {
					intent.putExtra("class", OrderInformActivity.class);
					intent.setClass(context, LoginActivity.class);
				} else {
					intent.setClass(context, OrderInformActivity.class);
				}
				intent.putExtra("gid", gid);
				intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				startActivity(intent);
				overridePendingTransition(0, 0);
				break;
			}
			case R.id.addcart: {
				SUser user = MyApplication.getInstance().getCurrentUser();
				Intent intent = new Intent();
				if (user == null || MyApplication.getInstance().getSessionId() == null) {
					intent.setClass(context, LoginActivity.class);
					intent.putExtra("isClose", true);
					intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
					startActivity(intent);
					overridePendingTransition(0, 0);
				} else {
					Integer discountId = null;
					if (selectDiscount != null)
						discountId = selectDiscount.getId();
					RtnValueDto rtnValue = MyApplication.getInstance().getSubmitService()
														.addCart(gid, (short) 1, discountId);
					if (CodeValidator.dealCode(this, rtnValue)) {
						ToastUtils.showShort(this, "已加入购物车！");
					}
				}
				break;
			}
		}
		super.onClick(v);
	}

	/**
	 * 添加页卡
	 */
	@SuppressLint("InflateParams")
	private void addViewPager() {
		viewList.clear();
		LayoutInflater inflater = getLayoutInflater();
		// 添加页卡数据
		if (picList != null && !picList.isEmpty()) {
			int size = picList.size();
			for (int i = 0; i < size; i++) {
				View view = inflater.inflate(R.layout.pager_pic_item, null);
				final int position = i;
				ImageView imageView = (ImageView) view.findViewById(R.id.good_pic);
				imageView.setTag(MyApplication.jgoods_img_url + picList.get(i));
				imageView.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View arg0) {
						Intent intent = new Intent(context, ImageViewActivity.class);
						// Intent intent = new Intent(context,
						// GoodsBigImgActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
						intent.putStringArrayListExtra("picList", imgList);
						intent.putExtra("position", position);
						startActivity(intent);
					}
				});
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
												switch (msg.what) {
													case 0: {
														if (msg.obj != null) {
															RtnValueDto obj = (RtnValueDto) msg.obj;
															if (CodeValidator.dealCode(context, obj)) {
																details = obj.getData();
																if (details == null) {
																	if (obj.getValidate() != null
																			&& StrUtils.isNotEmpty(obj.getValidate()
																										.getMessage())) {
																		ToastUtils.showShort(
																				context,
																				obj.getValidate()
																					.getMessage());
																	}
																} else {
																	initView();
																}
															}
														}
														break;
													}
												}
												closeLoading();
											}
										};

}
