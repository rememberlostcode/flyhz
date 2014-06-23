
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
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.holding.smile.R;
import com.holding.smile.adapter.MyJGoodsAdapter;
import com.holding.smile.dto.RtnValueDto;
import com.holding.smile.entity.JGoods;
import com.holding.smile.entity.JSort;
import com.holding.smile.myview.MyLinearLayout;
import com.holding.smile.tools.ToastUtils;

/**
 * 
 * 类说明：排行榜
 * 
 * @author robin 2014-4-16上午9:47:40
 * 
 */
public class SortActivity extends BaseActivity implements OnClickListener {

	private static final int	WHAT_DID_LOAD_DATA	= 0;

	private MyJGoodsAdapter		adapter;
	private List<JGoods>		mStrings			= new ArrayList<JGoods>();

	private ListView			mListView;
	private String				sortUrl				= null;					// 选中的排序URL
	private List<JSort>			jSortList			= new ArrayList<JSort>();	// 排序类型列表

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentLayout(R.layout.goods_sort);
		ImageView backBtn = displayHeaderBack();
		backBtn.setOnClickListener(this);

		TextView headerDescription = displayHeaderDescription();
		headerDescription.setText(R.string.the_charts);
		displayFooterMain(R.id.mainfooter_two);

		setSortLayout();// 设置排序标签

		adapter = new MyJGoodsAdapter(context, mStrings);
		mListView = (ListView) findViewById(R.id.goods_list_view);
		mListView.setAdapter(adapter);
		mListView.setOnScrollListener(mScrollListener);

		startTask();

	}

	/**
	 * 设置排序标签布局
	 */
	private void setSortLayout() {
		final MyLinearLayout jSortLayout = (MyLinearLayout) findViewById(R.id.sort_type);
		RtnValueDto rtnValue = MyApplication.getInstance().getDataService().getSortList();
		if (rtnValue != null && rtnValue.getSortData() != null) {
			jSortList = rtnValue.getSortData();
		}
		jSortLayout.setJSortList(context, jSortList);
		jSortLayout.setBackgroundBtn(0);
		int childCount = jSortLayout.getChildCount();
		// 排行不为空，则设置默认每一个排行
		if (childCount != 0) {
			sortUrl = jSortList.get(0).getU();
		}
		for (int i = 0; i < childCount; i++) {
			View v = jSortLayout.getChildAt(i);
			v.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					jSortLayout.setBackgroundBtn(v.getId());
					sortUrl = v.getTag().toString();
					startTask();
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
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == SEARCH_CODE || resultCode == RESULT_CANCELED) {

		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public synchronized void loadData() {
		RtnValueDto rGoods = MyApplication.getInstance().getDataService()
											.getJGoodsSortList(sortUrl);

		if (rGoods != null) {
			Message msg = mUIHandler.obtainMessage(WHAT_DID_LOAD_DATA);
			msg.obj = rGoods;
			msg.sendToTarget();
		} else {
			ToastUtils.showShort(context, "暂无数据！");
			waitCloseProgressBar();
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		startTask();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		jSortList.clear();
		jSortList = null;
		mStrings.clear();
		mStrings = null;
		mListView.destroyDrawingCache();
		mListView = null;
		if (adapter != null) {
			adapter.notifyDataSetChanged();
			adapter.notifyDataSetInvalidated();
			adapter = null;
		}
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
													switch (msg.what) {
														case WHAT_DID_LOAD_DATA: {
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
															break;
														}
													}
													waitCloseProgressBar();
												}
											};

}
