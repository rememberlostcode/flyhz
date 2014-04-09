
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.holding.smile.R;
import com.holding.smile.adapter.VerticalListAdapter;
import com.holding.smile.dto.BrandJGoods;
import com.holding.smile.dto.RtnValueDto;
import com.holding.smile.myview.PullToRefreshView;
import com.holding.smile.myview.PullToRefreshView.OnHeaderRefreshListener;

public class MainActivity extends BaseActivity implements OnClickListener, OnHeaderRefreshListener {

	private static final int	WHAT_DID_LOAD_DATA	= 0;
	private static final int	WHAT_DID_REFRESH	= 1;

	private VerticalListAdapter	vlAdapter;
	private ListView			listView;
	private List<BrandJGoods>	brandJGoodsList		= new ArrayList<BrandJGoods>();

	private PullToRefreshView	mPullToRefreshView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentLayout(R.layout.smile_main);

		ImageView backBtn = displayHeaderBack();
		backBtn.setOnClickListener(this);

		ImageView button = (ImageView) findViewById(R.id.btn_search);
		button.setOnClickListener(this);

		displayHeaderSearch();
		TextView headerDescription = displayHeaderDescription();
		headerDescription.setText(R.string.app_name);
		displayFooterMain(R.id.mainfooter_one);

		mPullToRefreshView = (PullToRefreshView) findViewById(R.id.main_pull_refresh_view);
		listView = (ListView) findViewById(R.id.brand_list);
		vlAdapter = new VerticalListAdapter(context, brandJGoodsList);
		listView.setAdapter(vlAdapter);
		vlAdapter.notifyDataSetChanged();

		startTask();
		MyApplication.getInstance().setmImgList(listView);

	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
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
			MyApplication.getInstance().setmImgList(listView);
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
		RtnValueDto rGoods = MyApplication.getInstance().getDataService().getBrandJGoodsListInit();
		if (rGoods != null) {
			Message msg = mUIHandler.obtainMessage(WHAT_DID_LOAD_DATA);
			msg.obj = rGoods;
			msg.sendToTarget();
		} else {
			Toast.makeText(context, "暂无数据", Toast.LENGTH_SHORT).show();
		}
	}

	public void onRefresh() {
		RtnValueDto rGoods = MyApplication.getInstance().getDataService().getBrandJGoodsListInit();
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
													List<BrandJGoods> strings = obj.getBrandData();
													if (strings != null && !strings.isEmpty()) {
														for (int i = 0; i < strings.size(); i++) {
															BrandJGoods each = strings.get(i);
															brandJGoodsList.add(each);
														}
														vlAdapter.notifyDataSetChanged();
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
															brandJGoodsList.removeAll(brandJGoodsList);
														}
														String message = obj.getValidate()
																			.getMessage();
														if (message != null
																&& !"".equals(message.trim())) {
															Toast.makeText(context, message,
																	Toast.LENGTH_SHORT).show();
														}
													}
													List<BrandJGoods> strings = obj.getBrandData();
													if (strings != null && !strings.isEmpty()) {
														for (int i = 0; i < strings.size(); i++) {
															BrandJGoods each = strings.get(i);
															brandJGoodsList.add(0, each);
														}
														vlAdapter.notifyDataSetChanged();
													}
												}
												mPullToRefreshView.onHeaderRefreshComplete();
												break;
											}
										}
									}
								};

}
