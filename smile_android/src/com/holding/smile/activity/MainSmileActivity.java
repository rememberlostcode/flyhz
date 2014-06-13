
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
import com.holding.smile.adapter.BrandAdapter;
import com.holding.smile.adapter.MyPagerAdapter;
import com.holding.smile.dto.RtnValueDto;
import com.holding.smile.entity.Brand;
import com.holding.smile.entity.Category;
import com.holding.smile.entity.IndexBrands;
import com.holding.smile.entity.JActivity;
import com.holding.smile.entity.SUser;
import com.holding.smile.myview.MyListView;
import com.holding.smile.myview.PullToRefreshView;
import com.holding.smile.myview.PullToRefreshView.OnHeaderRefreshListener;
import com.holding.smile.service.LoginService;
import com.holding.smile.tools.StrUtils;

public class MainSmileActivity extends BaseActivity implements OnClickListener, OnHeaderRefreshListener {

	private static final int	WHAT_DID_LOAD_DATA	= 0;
	private static final int	WHAT_DID_REFRESH	= 1;
	private static final int	AUTO_LOGIN			= 2;

	private PullToRefreshView	mPullToRefreshView;
	private ViewPager			mViewPager;
	private List<View>			viewList;
	private MyPagerAdapter		pagerAdapter;
	// 装点点的ImageView数组
	private ImageView[]			tips;

	private BrandAdapter		brandAdapter;
	private MyListView			listView;
	private List<JActivity>		recActList			= new ArrayList<JActivity>();	// 活动商品
	private List<Brand>			brandList			= new ArrayList<Brand>();
	private Integer				cid					= null;
	private TextView			headerDescription;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentLayout(R.layout.smile_main);

		TextView cateBtn = displayHeaderRight();
		cateBtn.setText(R.string.category);
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
		brandAdapter = new BrandAdapter(brandList, cid);
		listView.setAdapter(brandAdapter);

		mPullToRefreshView.setOnHeaderRefreshListener(this);
		mPullToRefreshView.setMoreFlag(false);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.header_right: {
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
					brandAdapter.setCid(cid);
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
				loadData();
			}
		}, 1000);
	}

	@Override
	public void loadData() {
		RtnValueDto rtnValue = MyApplication.getInstance().getDataService().getIndexBrands(cid);
		if (rtnValue != null) {
			Message msg = mUIHandler.obtainMessage(WHAT_DID_LOAD_DATA);
			msg.obj = rtnValue.getIndexBrandsData();
			msg.sendToTarget();
		} else {
			Toast.makeText(context, "暂无数据", Toast.LENGTH_SHORT).show();
		}

		/* 自动登录另起一个线程 */
		mUIHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				SUser user = MyApplication.getInstance().getSqliteService().getScurrentUser();
				if (user != null) {
					MyApplication.getInstance().setCurrentUser(user);
				}
				if (user != null && user.getUsername() != null && user.getToken() != null) {
					Message msg = mUIHandler.obtainMessage(AUTO_LOGIN);
					msg.obj = user;
					msg.sendToTarget();
				}
			}
		}, 200);
	}

	public void onRefresh() {
		RtnValueDto rtnValue = MyApplication.getInstance().getDataService().getIndexBrands(cid);
		if (rtnValue != null) {
			Message msg = mUIHandler.obtainMessage(WHAT_DID_REFRESH);
			msg.obj = rtnValue;
			msg.sendToTarget();
		} else {
			Toast.makeText(context, "暂无数据", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		brandList.clear();
		brandList = null;
		if (brandAdapter != null) {
			brandAdapter.notifyDataSetChanged();
			brandAdapter.notifyDataSetInvalidated();
			brandAdapter = null;
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
						// Toast.makeText(context, "您点击了活动区域" + jAct.getId() +
						// "!", Toast.LENGTH_SHORT)
						// .show();

						Intent intent = new Intent(context, HtmlUIActivity.class);
						if (StrUtils.isNotEmpty(jAct.getUrl())) {
							intent.putExtra("url", jAct.getUrl());
						} else {
							if (jAct.getId().equals(2)) {
								intent.putExtra("url", MyApplication.jgoods_img_url
										+ "/activity/index2.html");
							} else {
								intent.putExtra("url", MyApplication.jgoods_img_url
										+ "/activity/index.html");
							}
						}
						startActivity(intent);

					}
				});
				viewList.add(view);
			}

			ViewGroup group = (ViewGroup) findViewById(R.id.viewGroup);
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
															brandList.clear();
															IndexBrands obj = (IndexBrands) msg.obj;

															// 活动区商品
															List<JActivity> jActList = obj.getActivitys();
															if (jActList != null
																	&& !jActList.isEmpty()) {
																int jSize = jActList.size();
																for (int i = 0; i < jSize; i++) {
																	JActivity each = jActList.get(i);
																	each.setUrl("");
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
															// 品牌区
															List<Brand> brands = obj.getBrands();
															if (brands != null && !brands.isEmpty()) {
																int bSize = brands.size();
																for (int i = 0; i < bSize; i++) {
																	Brand each = brands.get(i);
																	brandList.add(each);
																}
															}
														} else {
															Toast.makeText(context, "暂无数据",
																	Toast.LENGTH_SHORT).show();
														}
														brandAdapter.notifyDataSetChanged();
														mPullToRefreshView.onHeaderRefreshComplete();
														break;
													}
													case WHAT_DID_REFRESH: {
														brandList.clear();
														if (msg.obj != null) {
															RtnValueDto obj = (RtnValueDto) msg.obj;
															if (obj.getIndexBrandsData() != null) {
																// 品牌区
																List<Brand> brands = obj.getIndexBrandsData()
																						.getBrands();
																if (brands != null
																		&& !brands.isEmpty()) {
																	int bSize = brands.size();
																	for (int i = 0; i < bSize; i++) {
																		Brand each = brands.get(i);
																		brandList.add(each);
																	}
																}
															}
														} else {
															Toast.makeText(context, "暂无数据",
																	Toast.LENGTH_SHORT).show();
														}
														brandAdapter.notifyDataSetChanged();
														mPullToRefreshView.onHeaderRefreshComplete();
														break;
													}
													case AUTO_LOGIN: {
														if (msg.obj != null) {
															SUser user = (SUser) msg.obj;
															LoginService loginService = MyApplication.getInstance()
																										.getLoginService();
															RtnValueDto rvd = loginService.autoLogin(user);
															if (rvd != null
																	&& rvd.getUserData() != null) {
																Toast.makeText(
																		context,
																		"自动登录成功！欢迎您,"
																				+ user.getUsername(),
																		Toast.LENGTH_SHORT).show();
															}
														}
														break;
													}
												}
											}
										};

}