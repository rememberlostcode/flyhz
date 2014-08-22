
package com.holding.smile.activity;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
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
import com.holding.smile.tools.CodeValidator;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 
 * 类说明：排行榜
 * 
 * @author robin 2014-4-16上午9:47:40
 * 
 */
public class SortActivity extends BaseActivity implements OnClickListener {

	private static final int	WHAT_DID_LOAD_DATA	= 0;
	// private ImageLoader mImageLoader = MyApplication.getImageLoader();
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

		mListView = (ListView) findViewById(R.id.goods_list_view);
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
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (returnDesktop(keyCode, event)) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		jSortList.clear();
		jSortList = null;
		mStrings.clear();
		mStrings = null;
		if (mListView != null) {
			mListView.destroyDrawingCache();
			mListView = null;
		}
		if (adapter != null) {
			adapter.notifyDataSetChanged();
			adapter.notifyDataSetInvalidated();
			adapter = null;
		}
	};

	OnScrollListener	mScrollListener	= new OnScrollListener() {
											private int	_start_index;
											private int	_end_index;

											@Override
											public void onScrollStateChanged(AbsListView view,
													int scrollState) {
												switch (scrollState) {
													case OnScrollListener.SCROLL_STATE_FLING:
														// adapter.setFlagBusy(true);
														break;
													case OnScrollListener.SCROLL_STATE_IDLE:
														// adapter.setFlagBusy(false);
														pageImgLoad(_start_index, _end_index);
														break;
													case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
														// adapter.setFlagBusy(true);
														break;
													default:
														break;
												}
											}

											@Override
											public void onScroll(AbsListView view,
													int firstVisibleItem, int visibleItemCount,
													int totalItemCount) {
												// 设置当前屏幕显示的起始index和结束index
												_start_index = firstVisibleItem;
												_end_index = firstVisibleItem + visibleItemCount;
												if (_end_index >= totalItemCount) {
													_end_index = totalItemCount - 1;
												}
											}
										};

	private void pageImgLoad(int start_index, int end_index) {
		for (int i = 0; start_index < end_index; start_index++, i++) {
			JGoods curr_item = (JGoods) adapter.getItem(start_index);
			if (curr_item != null && curr_item.getP() != null && curr_item.getP().length > 0) {
				View itemView = mListView.getChildAt(i);
				if (itemView != null) {
					ImageView imageView = (ImageView) itemView.findViewById(R.id.p);
					if (imageView != null) {
						ImageLoader.getInstance().displayImage(
								MyApplication.jgoods_img_url + curr_item.getP()[0], imageView,MyApplication.options);
					}
				}
			}
		}
//		adapter.notifyDataSetChanged();
	}

	@SuppressLint("HandlerLeak")
	private final Handler	mUIHandler	= new Handler() {

											@Override
											public void handleMessage(Message msg) {
												switch (msg.what) {
													case WHAT_DID_LOAD_DATA: {
														mStrings.clear();
														adapter = new MyJGoodsAdapter(context,
																mStrings);
														mListView.setAdapter(adapter);
														if (msg.obj != null) {
															RtnValueDto obj = (RtnValueDto) msg.obj;
															if (obj != null
																	&& CodeValidator.dealCode(
																			context, obj)) {
																List<JGoods> strings = obj.getData();
																if (strings != null
																		&& !strings.isEmpty()) {
																	for (int i = 0; i < strings.size(); i++) {
																		JGoods each = strings.get(i);
																		mStrings.add(each);
																	}
																}
															}
														}
														if (adapter != null) {
															adapter.notifyDataSetChanged();
														}
														break;
													}
												}
												closeLoading();
											}
										};

}
