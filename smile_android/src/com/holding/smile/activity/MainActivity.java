
package com.holding.smile.activity;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.GridView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.holding.smile.R;
import com.holding.smile.adapter.RecommendGoodsAdapter;
import com.holding.smile.adapter.VerticalListAdapter;
import com.holding.smile.dto.BrandJGoods;
import com.holding.smile.dto.RtnValueDto;
import com.holding.smile.entity.Category;
import com.holding.smile.entity.JActivity;
import com.holding.smile.entity.JIndexJGoods;
import com.holding.smile.myview.MyGridView;
import com.holding.smile.tools.NullHostNameVerifier;
import com.holding.smile.tools.NullX509TrustManager;

public class MainActivity extends BaseActivity implements OnClickListener {

	private static final int		WHAT_DID_LOAD_DATA	= 0;
	private static final int		WHAT_DID_REFRESH	= 1;

	private MyGridView				reGridView;
	private RecommendGoodsAdapter	reGoodsAdapter;
	private VerticalListAdapter		vlAdapter;
	private ListView				listView;

    private List<JGoods>          recGoodsList            = new ArrayList<JGoods>();     // 活动商品
	private List<BrandJGoods>		brandJGoodsList			= new ArrayList<BrandJGoods>();
	private Integer					cid						= null;
	private TextView				headerDescription;	
	
	private List<JActivity>			recActList			= new ArrayList<JActivity>();	// 活动商品
	private List<BrandJGoods>		brandJGoodsList		= new ArrayList<BrandJGoods>();
	private Integer					cid					= null;
	private TextView				headerDescription;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentLayout(R.layout.smile_main);

		TextView cateBtn = displayHeaderCate();
		cateBtn.setOnClickListener(this);

		headerDescription = displayHeaderDescription();
		headerDescription.setText(R.string.recommend_goods);
		displayFooterMain(R.id.mainfooter_one);

		reGridView = (MyGridView) findViewById(R.id.recommend_goods);
		reGoodsAdapter = new RecommendGoodsAdapter(context, recActList);
		reGridView.setAdapter(reGoodsAdapter);
		reGridView.setOnScrollListener(mScrollListener);

		listView = (ListView) findViewById(R.id.brand_list);
		vlAdapter = new VerticalListAdapter(brandJGoodsList, cid);
		listView.setAdapter(vlAdapter);

		startTask();

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
		if (reGoodsAdapter != null) {
			reGoodsAdapter.notifyDataSetChanged();
			reGoodsAdapter.notifyDataSetInvalidated();
			reGoodsAdapter = null;
		}
		if (reGridView != null) {
			reGridView.invalidate();
			reGridView = null;
		}
	};

	OnScrollListener		mScrollListener	= new OnScrollListener() {

												@Override
												public void onScrollStateChanged(AbsListView view,
														int scrollState) {
													switch (scrollState) {
														case OnScrollListener.SCROLL_STATE_FLING:
															reGoodsAdapter.setFlagBusy(true);
															break;
														case OnScrollListener.SCROLL_STATE_IDLE:
															reGoodsAdapter.setFlagBusy(false);
															break;
														case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
															reGoodsAdapter.setFlagBusy(false);
															break;
														default:
															break;
													}
													reGoodsAdapter.notifyDataSetChanged();
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
																	int ii = reGoodsAdapter.getCount();
																	int cWidth = MyApplication.getInstance()
																								.getScreenWidth();
																	LayoutParams params = new LayoutParams(
																			ii * cWidth,
																			LayoutParams.WRAP_CONTENT);
																	reGridView.setLayoutParams(params);
																	reGridView.setColumnWidth(cWidth);
																	reGridView.setStretchMode(GridView.NO_STRETCH);
																	reGridView.setNumColumns(ii);
																	reGoodsAdapter.notifyDataSetChanged();
																}

																// 品牌区
																List<BrandJGoods> brands = obj.getBrand();
																if (brands != null
																		&& !brands.isEmpty()) {
																	int bSize = brands.size();
																	for (int i = 0; i < bSize; i++) {
																		BrandJGoods each = brands.get(i);
																		brandJGoodsList.add(each);
																	}
																	vlAdapter.notifyDataSetChanged();
																}
															} else {
																Toast.makeText(context, "暂无数据",
																		Toast.LENGTH_SHORT).show();
															}
															break;
														}
														case WHAT_DID_REFRESH: {
															if (msg.obj != null) {
																brandJGoodsList.clear();
																RtnValueDto obj = (RtnValueDto) msg.obj;
																// 品牌区
																List<BrandJGoods> brands = obj.getBrandData();
																if (brands != null
																		&& !brands.isEmpty()) {
																	int bSize = brands.size();
																	for (int i = 0; i < bSize; i++) {
																		BrandJGoods each = brands.get(i);
																		brandJGoodsList.add(each);
																	}
																	vlAdapter.notifyDataSetChanged();
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
