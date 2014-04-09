
package com.holding.smile.activity;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.holding.smile.R;
import com.holding.smile.adapter.MyJGoodsAdapter;
import com.holding.smile.dto.RtnValueDto;
import com.holding.smile.entity.JGoods;
import com.holding.smile.myview.PullToRefreshView;
import com.holding.smile.myview.PullToRefreshView.OnFooterRefreshListener;
import com.holding.smile.myview.PullToRefreshView.OnHeaderRefreshListener;

public class MainTwoActivity extends BaseActivity implements OnClickListener,
		OnHeaderRefreshListener, OnFooterRefreshListener {

	private static final int	WHAT_DID_LOAD_DATA	= 0;
	private static final int	WHAT_DID_REFRESH	= 1;
	private static final int	WHAT_DID_MORE		= 2;

	private int					SEARCH_CODE			= 5;
	private MyJGoodsAdapter		adapter;
	private List<JGoods>		mStrings			= new ArrayList<JGoods>();
	private static final int	LIMIT				= 20;

	private PullToRefreshView	mPullToRefreshView;
	private GridView			mGridView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentLayout(R.layout.activity_main);
		ImageView backBtn = displayHeaderBack();
		backBtn.setOnClickListener(this);

		ImageView button = (ImageView) findViewById(R.id.btn_search);
		button.setOnClickListener(this);

		displayHeaderSearch();
		TextView headerDescription = displayHeaderDescription();
		headerDescription.setText(R.string.app_name);
		displayFooterMain(R.id.mainfooter_two);

		adapter = new MyJGoodsAdapter(mStrings);
		mPullToRefreshView = (PullToRefreshView) findViewById(R.id.main_pull_refresh_view);
		mGridView = (GridView) findViewById(R.id.gridview);
		mGridView.setAdapter(adapter);
		mPullToRefreshView.setOnHeaderRefreshListener(this);
		mPullToRefreshView.setOnFooterRefreshListener(this);

		MyApplication.getInstance().setmImgList(mGridView);
		startTask();

		if (MyApplication.getInstance().getScreenWidth() < 800) {
			mGridView.setNumColumns(1);
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.btn_back: {
				finish();
				break;
			}
			case R.id.btn_search: {
				Intent intent = new Intent(this, SearchGoodsActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivityForResult(intent, SEARCH_CODE);
				break;
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == SEARCH_CODE || resultCode == RESULT_CANCELED) {
			MyApplication.getInstance().setmImgList(mGridView);
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
		RtnValueDto rGoods = MyApplication.getInstance().getDataService().getJGoodsTwoListInit();
		if (rGoods != null) {
			Message msg = mUIHandler.obtainMessage(WHAT_DID_LOAD_DATA);
			msg.obj = rGoods;
			msg.sendToTarget();
		} else {
			Toast.makeText(context, "暂无数据", Toast.LENGTH_SHORT).show();
		}
	}

	public void onRefresh() {
		JGoods jGoodsFirst = null;
		Object obj = mGridView.getItemAtPosition(0);
		if (obj != null) {
			jGoodsFirst = (JGoods) obj;
		}
		RtnValueDto rGoods = MyApplication.getInstance().getDataService()
											.getJGoodsTwoListRefresh(jGoodsFirst);
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
		Integer i = mGridView.getLastVisiblePosition();
		if (i >= 0) {
			obj = mGridView.getItemAtPosition(i);
		}
		if (obj != null) {
			jGoodsLast = (JGoods) obj;
		}
		RtnValueDto rGoods = MyApplication.getInstance().getDataService()
											.getJGoodsTwoListMore(jGoodsLast);
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
		mStrings.clear();
		mStrings = null;
		if (adapter != null) {
			adapter.notifyDataSetChanged();
			adapter.notifyDataSetInvalidated();
			adapter = null;
			mGridView.destroyDrawingCache();
			mGridView = null;
		}
		setResult(MORE_CODE, null);
	};

	@SuppressLint("HandlerLeak")
	private Handler	mUIHandler	= new Handler() {

									@Override
									public void handleMessage(Message msg) {
										progressBar.setVisibility(View.GONE);
										switch (msg.what) {
											case WHAT_DID_LOAD_DATA: {
												if (msg.obj != null) {
													RtnValueDto obj = (RtnValueDto) msg.obj;
													List<JGoods> strings = obj.getData();
													if (strings != null && !strings.isEmpty()) {
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
													RtnValueDto obj = (RtnValueDto) msg.obj;
													if (obj.getValidate() != null) {
														String option = obj.getValidate()
																			.getOption();
														if (option != null && "3".equals(option)) {
															mStrings.removeAll(mStrings);
														}
														String message = obj.getValidate()
																			.getMessage();
														if (message != null
																&& !"".equals(message.trim())) {
															Toast.makeText(context, message,
																	Toast.LENGTH_SHORT).show();
														}
													}
													List<JGoods> strings = obj.getData();
													if (strings != null && !strings.isEmpty()) {
														for (int i = 0; i < strings.size(); i++) {
															JGoods each = strings.get(i);
															mStrings.add(0, each);
														}
														if (mStrings.size() > LIMIT) {
															for (int i = LIMIT; i < mStrings.size(); i++) {
																mStrings.remove(i);
															}
														}
														adapter.notifyDataSetChanged();
													}
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
														if (option != null && "3".equals(option)) {
															mStrings.removeAll(mStrings);
														}
														String message = obj.getValidate()
																			.getMessage();
														if (message != null
																&& !"".equals(message.trim())) {
															Toast.makeText(context, message,
																	Toast.LENGTH_SHORT).show();
														}
													}
													List<JGoods> strings = obj.getData();
													if (strings != null && !strings.isEmpty()) {
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
