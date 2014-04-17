
package com.holding.smile.activity;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.Menu;
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
import com.holding.smile.adapter.VerticalListAdapter;
import com.holding.smile.dto.BrandJGoods;
import com.holding.smile.dto.RtnValueDto;
import com.holding.smile.entity.Category;
import com.holding.smile.entity.JActivity;
import com.holding.smile.entity.JIndexJGoods;
import com.holding.smile.myview.MyListView;
import com.holding.smile.myview.PullToRefreshView;
import com.holding.smile.myview.PullToRefreshView.OnHeaderRefreshListener;

public class MainActivity extends BaseActivity implements OnClickListener, OnHeaderRefreshListener {

	private static final int	WHAT_DID_LOAD_DATA	= 0;
	private static final int	WHAT_DID_REFRESH	= 1;

	private PullToRefreshView	mPullToRefreshView;
	private ViewPager			mViewPager;
	private List<View>			viewList;
	private MyPagerAdapter		pagerAdapter;
	// 装点点的ImageView数组
	private ImageView[]			tips;

	private VerticalListAdapter	vlAdapter;
	private MyListView			listView;
	private List<JActivity>		recActList			= new ArrayList<JActivity>();	// 活动商品
	private List<BrandJGoods>	brandJGoodsList		= new ArrayList<BrandJGoods>();
	private Integer				cid					= null;
	private TextView			headerDescription;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentLayout(R.layout.smile_main);

		TextView cateBtn = displayHeaderCate();
		cateBtn.setOnClickListener(this);

		headerDescription = displayHeaderDescription();
		headerDescription.setText(R.string.recommend_goods);
		displayFooterMain(R.id.mainfooter_one);

		initView();
		startTask();

	}

	private void initView() {
		LinearLayout indexViewLayout = (LinearLayout) LayoutInflater.from(context).inflate(
				R.layout.index_recommend, null);
		mPullToRefreshView = (PullToRefreshView) findViewById(R.id.main_pull_refresh_view);
		listView = (MyListView) findViewById(R.id.list_view);
		listView.addHeaderView(indexViewLayout);// 添加子View

		mViewPager = (ViewPager) indexViewLayout.findViewById(R.id.viewpager);
		vlAdapter = new VerticalListAdapter(brandJGoodsList, cid);
		listView.setAdapter(vlAdapter);

		mPullToRefreshView.setOnHeaderRefreshListener(this);
		mPullToRefreshView.setMoreFlag(false);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.mainfooter_search: {
				Intent intent = new Intent(this, SearchGoodsActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivityForResult(intent, SEARCH_CODE);
				break;
			}
			case R.id.btn_cate: {
				// Toast.makeText(context, "您点了类别！", Toast.LENGTH_LONG).show();
				Intent intent = new Intent(this, CategoryActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivityForResult(intent, CATE_CODE);
				break;
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CATE_CODE && resultCode == RESULT_OK) {
			if (data != null) {
				Category cate = (Category) data.getExtras().getSerializable("cate");
				if (cate != null) {
					cid = cate.getId();
					headerDescription.setText(cate.getName());
					onRefresh();
				}
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onHeaderRefresh(PullToRefreshView view) {
		mPullToRefreshView.postDelayed(new Runnable() {
			@Override
			public void run() {
				onRefresh();
			}
		}, 1000);
	}

	@Override
	public void loadData() {
		RtnValueDto rGoods = MyApplication.getInstance().getDataService().getIndexJGoods();
		if (rGoods != null) {
			Message msg = mUIHandler.obtainMessage(WHAT_DID_LOAD_DATA);
			msg.obj = rGoods.getIndexData();
			msg.sendToTarget();
		} else {
			Toast.makeText(context, "暂无数据", Toast.LENGTH_SHORT).show();
		}
	}

	public void onRefresh() {
		RtnValueDto rGoods = MyApplication.getInstance().getDataService()
											.getRecommendBrandsListInit(cid);
		if (rGoods != null) {
			Message msg = mUIHandler.obtainMessage(WHAT_DID_REFRESH);
			msg.obj = rGoods;
			msg.sendToTarget();
		} else {
			Toast.makeText(context, "暂无数据", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		brandJGoodsList.clear();
		brandJGoodsList = null;
		if (vlAdapter != null) {
			vlAdapter.notifyDataSetChanged();
			vlAdapter.notifyDataSetInvalidated();
			vlAdapter = null;
		}
		if (listView != null) {
			listView.invalidate();
			listView = null;
		}

		recActList.clear();
		recActList = null;
	};

	/**
	 * 添加页卡
	 */
	private void addViewPager() {
		viewList = new ArrayList<View>();
		LayoutInflater inflater = getLayoutInflater();
		// 添加页卡数据
		if (recActList != null && !recActList.isEmpty()) {
			int size = recActList.size();
			for (int i = 0; i < size; i++) {
				final JActivity jAct = recActList.get(i);
				View view = inflater.inflate(R.layout.good_pic_item, null);
				ImageView imageView = (ImageView) view.findViewById(R.id.good_pic);
				imageView.setContentDescription(jAct.getId() + "");
				imageView.setTag(MyApplication.jgoods_img_url + jAct.getP());
				imageView.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View arg0) {
						Toast.makeText(context, "您点击了活动区域" + jAct.getId() + "!", Toast.LENGTH_SHORT)
								.show();
					}
				});
				viewList.add(view);
			}

			ViewGroup group = (ViewGroup) findViewById(R.id.viewGroup);
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
				setImageBackground(arg0 % recActList.size());
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

	@SuppressLint("HandlerLeak")
	private final Handler	mUIHandler	= new Handler() {

											@Override
											public void handleMessage(Message msg) {
												progressBar.setVisibility(View.GONE);
												switch (msg.what) {
													case WHAT_DID_LOAD_DATA: {
														if (msg.obj != null) {
															recActList.clear();
															brandJGoodsList.clear();
															JIndexJGoods obj = (JIndexJGoods) msg.obj;

															// 活动区商品
															List<JActivity> strings = obj.getActivity();
															if (strings != null
																	&& !strings.isEmpty()) {
																int jSize = strings.size();
																for (int i = 0; i < jSize; i++) {
																	JActivity each = strings.get(i);
																	recActList.add(each);
																}

																addViewPager();// 添加页卡
																// 实例化适配器
																pagerAdapter = new MyPagerAdapter(
																		viewList);
																mViewPager.setAdapter(pagerAdapter);
																mViewPager.setCurrentItem(0); // 设置默认当前页

															}

															// 品牌区
															List<BrandJGoods> brands = obj.getBrand();
															if (brands != null && !brands.isEmpty()) {
																int bSize = brands.size();
																for (int i = 0; i < bSize; i++) {
																	BrandJGoods each = brands.get(i);
																	brandJGoodsList.add(each);
																}
															}
														} else {
															Toast.makeText(context, "暂无数据",
																	Toast.LENGTH_SHORT).show();
														}
														vlAdapter.notifyDataSetChanged();
														mPullToRefreshView.onHeaderRefreshComplete();
														break;
													}
													case WHAT_DID_REFRESH: {
														brandJGoodsList.clear();
														if (msg.obj != null) {
															RtnValueDto obj = (RtnValueDto) msg.obj;
															// 品牌区
															List<BrandJGoods> brands = obj.getBrandData();
															if (brands != null && !brands.isEmpty()) {
																int bSize = brands.size();
																for (int i = 0; i < bSize; i++) {
																	BrandJGoods each = brands.get(i);
																	brandJGoodsList.add(each);
																}
															}
														} else {
															Toast.makeText(context, "暂无数据",
																	Toast.LENGTH_SHORT).show();
														}
														vlAdapter.notifyDataSetChanged();
														mPullToRefreshView.onHeaderRefreshComplete();
														break;
													}
												}
											}
										};

}
