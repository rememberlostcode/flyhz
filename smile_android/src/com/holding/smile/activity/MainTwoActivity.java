
package com.holding.smile.activity;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.holding.smile.R;
import com.holding.smile.adapter.MyJGoodsAdapter;
import com.holding.smile.dto.RtnValueDto;
import com.holding.smile.entity.Category;
import com.holding.smile.entity.JGoods;
import com.holding.smile.entity.SortType;
import com.holding.smile.myview.MyLinearLayout;
import com.holding.smile.myview.PullToRefreshView;
import com.holding.smile.myview.PullToRefreshView.OnFooterRefreshListener;
import com.holding.smile.myview.PullToRefreshView.OnHeaderRefreshListener;

public class MainTwoActivity extends BaseActivity implements OnClickListener,
		OnHeaderRefreshListener, OnFooterRefreshListener {

	private static final int	WHAT_DID_LOAD_DATA	= 0;
	private static final int	WHAT_DID_REFRESH	= 1;
	private static final int	WHAT_DID_MORE		= 2;

	private MyJGoodsAdapter		adapter;
	private List<JGoods>		mStrings			= new ArrayList<JGoods>();
	private static final int	LIMIT				= 20;

	private PullToRefreshView	mPullToRefreshView;
	private ListView			mListView;
	private Integer				bid					= null;						// 品牌ID
	private Integer				cid					= null;						// 分类ID
	private String				seqorderType		= null;						// 选中的排序类型
	private List<SortType>		sorttypeList		= new ArrayList<SortType>();	// 排序类型列表

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ImageView backBtn = displayHeaderBack();
		backBtn.setOnClickListener(this);
		TextView cateBtn = displayHeaderRight();
		cateBtn.setText(R.string.category);
		cateBtn.setOnClickListener(this);

		try {
			Intent intent = this.getIntent();
			if (intent.getExtras().getSerializable("bid") != null)
				bid = (Integer) intent.getExtras().getSerializable("bid");

			if (intent.getExtras().getSerializable("cid") != null)
				cid = (Integer) intent.getExtras().getSerializable("cid");

			String bn = intent.getExtras().getString("bn");
			TextView headerDescription = displayHeaderDescription();
			if (bn != null)
				headerDescription.setText(bn);
		} catch (Exception e) {
			Log.e(MyApplication.LOG_TAG, "查看全部时报错：" + e.getMessage());
		}

		startTask();

	}

	/**
	 * 初始化View
	 */
	private void initView() {
		setContentLayout(R.layout.activity_main);
		displayFooterMain(0);
		setSortTypeLayout();// 设置排序标签

		adapter = new MyJGoodsAdapter(context, mStrings);
		mPullToRefreshView = (PullToRefreshView) findViewById(R.id.main_pull_refresh_view);
		mListView = (ListView) findViewById(R.id.goods_list_view);
		mListView.setAdapter(adapter);
		mListView.setOnScrollListener(mScrollListener);
		mPullToRefreshView.setOnHeaderRefreshListener(this);
		mPullToRefreshView.setOnFooterRefreshListener(this);
	}

	/**
	 * 设置排序标签布局
	 */
	private void setSortTypeLayout() {
		final MyLinearLayout sortTypeLayout = (MyLinearLayout) findViewById(R.id.sort_type);
		sorttypeList = MyApplication.getInstance().getDataService().getSortTypeList();
		sortTypeLayout.setSortTypeList(context, sorttypeList);
		int childCount = sortTypeLayout.getChildCount();
		for (int i = 0; i < childCount; i++) {
			View v = sortTypeLayout.getChildAt(i);
			v.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					sortTypeLayout.setBackgroundBtn(v.getId());
					seqorderType = v.getTag().toString();
					onRefresh();
				}
			});
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.btn_back: {
				finish();
				break;
			}
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
	public void onFooterRefresh(PullToRefreshView view) {
		mPullToRefreshView.postDelayed(new Runnable() {
			@Override
			public void run() {
				onLoadMore();
			}
		}, 1000);
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
		RtnValueDto rGoods = MyApplication.getInstance().getDataService()
											.getBrandJGoodsListInit(bid, cid, seqorderType);
		if (rGoods != null) {
			Message msg = mUIHandler.obtainMessage(WHAT_DID_LOAD_DATA);
			msg.obj = rGoods;
			msg.sendToTarget();
		} else {
			Toast.makeText(context, "暂无数据", Toast.LENGTH_SHORT).show();
		}
	}

	public void onRefresh() {
		RtnValueDto rGoods = MyApplication.getInstance().getDataService()
											.getBrandJGoodsListInit(bid, cid, seqorderType);
		if (rGoods != null) {
			Message msg = mUIHandler.obtainMessage(WHAT_DID_REFRESH);
			msg.obj = rGoods;
			msg.sendToTarget();
		} else {
			Toast.makeText(context, "暂无数据", Toast.LENGTH_SHORT).show();
		}
	}

	public void onLoadMore() {
		JGoods jGoodsLast = null;
		Object obj = null;
		Integer i = mListView.getLastVisiblePosition();
		if (i >= 0) {
			obj = mListView.getItemAtPosition(i);
		}
		if (obj != null) {
			jGoodsLast = (JGoods) obj;
		}
		RtnValueDto rGoods = MyApplication.getInstance()
											.getDataService()
											.getBrandJGoodsListMore(bid, cid, seqorderType,
													jGoodsLast.getSeq());
		if (rGoods != null) {
			Message msg = mUIHandler.obtainMessage(WHAT_DID_MORE);
			msg.obj = rGoods;
			msg.sendToTarget();
		} else {
			Toast.makeText(context, "暂无数据", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		sorttypeList.clear();
		sorttypeList = null;
		mStrings.clear();
		mStrings = null;
		mPullToRefreshView.destroyDrawingCache();
		mPullToRefreshView = null;
		mListView.destroyDrawingCache();
		mListView = null;
		if (adapter != null) {
			adapter.notifyDataSetChanged();
			adapter.notifyDataSetInvalidated();
			adapter = null;
		}
		setResult(MORE_CODE, null);
	};

	OnScrollListener		mScrollListener	= new OnScrollListener() {

												@Override
												public void onScrollStateChanged(AbsListView view,
														int scrollState) {
													switch (scrollState) {
														case OnScrollListener.SCROLL_STATE_FLING:
															adapter.setFlagBusy(true);
															break;
														case OnScrollListener.SCROLL_STATE_IDLE:
															adapter.setFlagBusy(false);
															break;
														case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
															adapter.setFlagBusy(false);
															break;
														default:
															break;
													}
													adapter.notifyDataSetChanged();
												}

												@Override
												public void onScroll(AbsListView view,
														int firstVisibleItem, int visibleItemCount,
														int totalItemCount) {

												}
											};

	@SuppressLint("HandlerLeak")
	private final Handler	mUIHandler		= new Handler() {

												@Override
												public void handleMessage(Message msg) {
													progressBar.setVisibility(View.GONE);
													switch (msg.what) {
														case WHAT_DID_LOAD_DATA: {
															initView();// 初始化View
															if (msg.obj != null) {
																mStrings.clear();
																RtnValueDto obj = (RtnValueDto) msg.obj;
																List<JGoods> strings = obj.getData();
																if (strings != null
																		&& !strings.isEmpty()) {
																	for (int i = 0; i < strings.size(); i++) {
																		JGoods each = strings.get(i);
																		mStrings.add(each);
																	}
																	adapter.notifyDataSetChanged();
																}
															}
															mPullToRefreshView.onHeaderRefreshComplete();
															break;
														}
														case WHAT_DID_REFRESH: {
															if (msg.obj != null) {
																mStrings.clear();
																RtnValueDto obj = (RtnValueDto) msg.obj;
																if (obj.getValidate() != null) {
																	String option = obj.getValidate()
																						.getOption();
																	if (option != null
																			&& "3".equals(option)) {
																		mStrings.removeAll(mStrings);
																	}
																	String message = obj.getValidate()
																						.getMessage();
																	if (message != null
																			&& !"".equals(message.trim())) {
																		Toast.makeText(context,
																				message,
																				Toast.LENGTH_SHORT)
																				.show();
																	}
																}
																List<JGoods> strings = obj.getData();
																if (strings != null
																		&& !strings.isEmpty()) {
																	for (int i = 0; i < strings.size(); i++) {
																		JGoods each = strings.get(i);
																		mStrings.add(0, each);
																	}
																	if (mStrings.size() > LIMIT) {
																		for (int i = LIMIT; i < mStrings.size(); i++) {
																			mStrings.remove(i);
																		}
																	}
																}
																adapter.notifyDataSetChanged();
															}
															mPullToRefreshView.onHeaderRefreshComplete();
															break;
														}

														case WHAT_DID_MORE: {
															if (msg.obj != null) {
																RtnValueDto obj = (RtnValueDto) msg.obj;
																if (obj.getValidate() != null) {
																	String option = obj.getValidate()
																						.getOption();
																	if (option != null
																			&& "3".equals(option)) {
																		mStrings.removeAll(mStrings);
																	}
																	String message = obj.getValidate()
																						.getMessage();
																	if (message != null
																			&& !"".equals(message.trim())) {
																		Toast.makeText(context,
																				message,
																				Toast.LENGTH_SHORT)
																				.show();
																	}
																}
																List<JGoods> strings = obj.getData();
																if (strings != null
																		&& !strings.isEmpty()) {
																	for (int i = 0; i < strings.size(); i++) {
																		JGoods each = strings.get(i);
																		mStrings.add(each);
																	}
																	adapter.notifyDataSetChanged();
																}
															}
															mPullToRefreshView.onFooterRefreshComplete();
															break;
														}
													}
												}
											};
}
