
package com.holding.smile.activity;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.holding.smile.R;
import com.holding.smile.adapter.HistorySearchAdapter;
import com.holding.smile.adapter.MyJGoodsAdapter;
import com.holding.smile.dto.RtnValueDto;
import com.holding.smile.entity.JGoods;
import com.holding.smile.myview.PullToRefreshView;
import com.holding.smile.myview.PullToRefreshView.OnFooterRefreshListener;
import com.holding.smile.myview.PullToRefreshView.OnHeaderRefreshListener;
import com.holding.smile.tools.ToastUtils;

/**
 * 
 * 类说明：搜索物品
 * 
 * @author robin 2014-2-25下午12:25:48
 * 
 */
public class SearchGoodsActivity extends BaseActivity implements OnClickListener,
		OnHeaderRefreshListener, OnFooterRefreshListener {

	private static final int	WHAT_DID_LOAD_DATA	= 0;
	private static final int	WHAT_DID_REFRESH	= 1;
	private static final int	WHAT_DID_MORE		= 2;
	private static final int	LIMIT				= 20;
	private EditText			editText;
	private ImageView			resetView;

	private ListView			historyListView;
	private List<String>		historySearchList	= new ArrayList<String>();
	private boolean				smartFlag			= false;

	private MyJGoodsAdapter		adapter;
	private List<JGoods>		mStrings			= new ArrayList<JGoods>();

	private PullToRefreshView	mPullToRefreshView;
	private ListView			mListView;

	private String				keywords;
	private int					optNum				= 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentLayout(R.layout.search_goods_view);
		ImageView backBtn = (ImageView) findViewById(R.id.back_normal);
		backBtn.setOnClickListener(this);

		displayFooterMain(R.id.mainfooter_three);

		mPullToRefreshView = (PullToRefreshView) findViewById(R.id.search_pull_refresh_view);
		mListView = (ListView) findViewById(R.id.searchlistview);
		mListView.setAdapter(adapter);
		mPullToRefreshView.setOnHeaderRefreshListener(this);
		mPullToRefreshView.setOnFooterRefreshListener(this);

		editText = (EditText) findViewById(R.id.search_content);
		editText.setOnClickListener(this);

		resetView = (ImageView) findViewById(R.id.search_pic_content_del);
		resetView.setOnClickListener(this);

		historyListView = (ListView) findViewById(R.id.smart_options_list);
		showHistoryListView();

		editText.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				String inputContent = s.toString();
				int len = inputContent.length();
				if (len > 100) {
					ToastUtils.showShort(context, "最多只能输入100个字符！");
					editText.setText(inputContent.substring(0, 100));
					return;
				} else {
					String inputText = "";// 输入内容
					inputText = inputContent.substring(start, start + count);
					if (inputText.equals("\n")) {// 按回车键时，退出编辑状态,并隐藏键盘
						editText.clearFocus();
						InputMethodManager imm = (InputMethodManager) editText.getContext()
																				.getSystemService(
																						Context.INPUT_METHOD_SERVICE);
						imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
						String startStr = inputContent.substring(0, start);
						String endStr = inputContent.substring(start + 1);
						String keywords = startStr + endStr;
						keywords = keywords.trim();
						editText.setText(keywords);
						smartFlag = true;
						historyListView.setVisibility(ViewGroup.GONE);

						if (keywords != null && !keywords.equals(""))
							MyApplication.getInstance().getSqliteService().addSearch(keywords);
						searchGoodsList(keywords);
					} else {
						if (!smartFlag) {
							showHistoryListView();
						} else {
							smartFlag = false;
						}
					}

				}

			}

		});

		editText.setOnEditorActionListener(new EditText.OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH
						|| actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
					System.out.println("====================================search");

					editText.clearFocus();
					InputMethodManager imm = (InputMethodManager) editText.getContext()
																			.getSystemService(
																					Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
					String keywords = editText.getText().toString().trim();
					smartFlag = true;
					historyListView.setVisibility(ViewGroup.GONE);

					if (keywords != null && !keywords.equals(""))
						MyApplication.getInstance().getSqliteService().addSearch(keywords);
					searchGoodsList(keywords);
					return true;
				}
				return false;
			}
		});
	}

	/**
	 * 展示历史搜索列表
	 */
	public void showHistoryListView() {

		historySearchList.clear();
		List<String> historySearchWordsList = MyApplication.getInstance()
															.getSqliteService()
															.getSearchWords(
																	editText.getText().toString());
		if (historySearchWordsList != null && !historySearchWordsList.isEmpty()) {
			historySearchList = historySearchWordsList;
		} else {
			return;
		}

		HistorySearchAdapter smartOptionAdapter = new HistorySearchAdapter(historySearchList);
		historyListView.setAdapter(smartOptionAdapter);
		historyListView.setVisibility(ViewGroup.VISIBLE);

		historyListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				historyListView.setVisibility(ViewGroup.GONE);
				smartFlag = true;
				editText.setText(historySearchList.get(position));
				editText.setSelection(editText.getText().length());

				InputMethodManager imm = (InputMethodManager) editText.getContext()
																		.getSystemService(
																				Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
				historyListView.clearChoices();
				searchGoodsList(historySearchList.get(position));
			}

		});

	}

	/**
	 * 搜索物品
	 */
	public void searchGoodsList(String keywords) {

		mStrings.clear();
		mListView.setVisibility(ViewGroup.VISIBLE);
		if (adapter == null) {
			adapter = new MyJGoodsAdapter(context, mStrings);
			mListView.setAdapter(adapter);
			mListView.setOnScrollListener(mScrollListener);
		}
		this.keywords = keywords;
		startTask();
	}

	OnScrollListener	mScrollListener	= new OnScrollListener() {

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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.search_content:
				InputMethodManager imm = (InputMethodManager) editText.getContext()
																		.getSystemService(
																				Context.INPUT_METHOD_SERVICE);
				imm.showSoftInput(editText, 0);
				if (editText.getText() != null && !"".equals(editText.getText().toString())
						&& !"".equals(editText.getText().toString().trim())) {
					showHistoryListView();
				}
				break;
			case R.id.back_normal:
				setResult(RESULT_CANCELED, null);
				finish();
				break;
			case R.id.search_pic_content_del:
				editText.setText("");
				historyListView.setVisibility(ViewGroup.GONE);
				smartFlag = true;
				break;
			default:
				break;

		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == SEARCH_CODE || resultCode == RESULT_CANCELED) {

		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (returnDesktop(keyCode, event)) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			if (getCurrentFocus() != null && getCurrentFocus().getWindowToken() != null) {
				InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);
			}
		}
		return super.onTouchEvent(event);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		editText = null;
		historyListView = null;
		historySearchList.clear();
		historySearchList = null;

		mStrings.clear();
		mStrings = null;
		if (adapter != null) {
			adapter.notifyDataSetChanged();
			adapter.notifyDataSetInvalidated();
			adapter = null;
			mListView.destroyDrawingCache();
			mListView = null;
		}
	}

	@Override
	public void onFooterRefresh(PullToRefreshView view) {
		mPullToRefreshView.postDelayed(new Runnable() {
			@Override
			public void run() {
				optNum = 2;
				loadData();
			}
		}, 1000);
	}

	@Override
	public void onHeaderRefresh(PullToRefreshView view) {
		mPullToRefreshView.postDelayed(new Runnable() {
			@Override
			public void run() {
				optNum = 1;
				loadData();
			}
		}, 1000);
	}

	@Override
	public synchronized void loadData() {
		RtnValueDto rGoods = null;
		int mseeageWhat = WHAT_DID_LOAD_DATA;
		if (optNum == 0) {
			rGoods = MyApplication.getInstance().getDataService().getJGoodsSearchListInit(keywords);
			mseeageWhat = WHAT_DID_LOAD_DATA;
		} else if (optNum == 1) {
			rGoods = MyApplication.getInstance().getDataService()
									.getJGoodsSearchListRefresh(keywords, null);
			mseeageWhat = WHAT_DID_REFRESH;
		} else if (optNum == 2) {
			rGoods = MyApplication.getInstance().getDataService()
									.getJGoodsSearchListMore(keywords, null, mStrings.size());
			mseeageWhat = WHAT_DID_MORE;
		}
		if (rGoods == null) {
			rGoods = new RtnValueDto();
			rGoods.setData(null);
		}
		Message msg = mUIHandler.obtainMessage(mseeageWhat);
		msg.obj = rGoods;
		msg.sendToTarget();
	}

	@SuppressLint("HandlerLeak")
	private final Handler	mUIHandler	= new Handler() {

											@Override
											public void handleMessage(Message msg) {
												switch (msg.what) {
													case WHAT_DID_LOAD_DATA: {
														if (msg.obj != null) {
															RtnValueDto obj = (RtnValueDto) msg.obj;
															if (obj != null
																	&& obj.getData() != null
																	&& obj.getData().size() > 0) {
																List<JGoods> strings = obj.getData();
																if (strings != null) {
																	mStrings.clear();
																	mStrings.addAll(strings);
																	adapter.notifyDataSetChanged();
																}
															} else {
																ToastUtils.showShort(context,
																		"暂无数据！");
															}
														}
														mPullToRefreshView.onHeaderRefreshComplete();
														break;
													}
													case WHAT_DID_REFRESH: {
														if (msg.obj != null) {
															RtnValueDto obj = (RtnValueDto) msg.obj;
															if (obj != null
																	&& obj.getData() != null
																	&& obj.getData().size() > 0) {
																List<JGoods> strings = obj.getData();
																if (strings != null
																		&& !strings.isEmpty()) {
																	for (JGoods each : strings) {
																		mStrings.add(0, each);
																	}
																	if (mStrings.size() > LIMIT) {
																		for (int i = LIMIT; i < mStrings.size(); i++) {
																			mStrings.remove(i);
																		}
																	}
																	adapter.notifyDataSetChanged();
																}
															} else {
																ToastUtils.showShort(context,
																		"暂无数据！");
															}
														}
														mPullToRefreshView.onHeaderRefreshComplete();
														break;
													}

													case WHAT_DID_MORE: {
														if (msg.obj != null) {
															RtnValueDto obj = (RtnValueDto) msg.obj;
															if (obj != null
																	&& obj.getData() != null
																	&& obj.getData().size() > 0) {
																List<JGoods> strings = obj.getData();
																if (strings != null
																		&& !strings.isEmpty()) {
																	mStrings.addAll(strings);
																	adapter.notifyDataSetChanged();
																}
															} else {
																ToastUtils.showShort(context,
																		"最后一个了！");
															}
														}
														mPullToRefreshView.onFooterRefreshComplete();
														break;
													}
												}

												waitCloseProgressBar();

											}
										};

}
