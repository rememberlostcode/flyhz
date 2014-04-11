
package com.holding.smile.activity;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.holding.smile.R;
import com.holding.smile.adapter.CategoryAdapter;
import com.holding.smile.dto.RtnValueDto;
import com.holding.smile.entity.Category;
import com.holding.smile.tools.SetExpandableListViewListViewHeight;

/**
 * 
 * 类说明：分类
 * 
 * @author robin 2014-4-10下午3:01:45
 * 
 */
public class CategoryActivity extends BaseActivity implements OnClickListener {
	private static final int	WHAT_DID_LOAD_DATA	= 0;
	private CategoryAdapter		cateAdapter;
	private ListView			listView;
	private List<Category>		cateList			= new ArrayList<Category>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentLayout(R.layout.cate_view);
		ImageView backBtn = displayHeaderBack();
		backBtn.setOnClickListener(this);

		TextView headerDesc = displayHeaderDescription();
		headerDesc.setText(R.string.category);

		listView = (ListView) findViewById(R.id.cate_list_view);
		cateAdapter = new CategoryAdapter(context, cateList);
		listView.setAdapter(cateAdapter);
		loadData();
		SetExpandableListViewListViewHeight.setListViewHeightOnChildren(listView);

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Category cate = cateList.get(position);
				Intent intent = new Intent();
				intent.putExtra("cate", cate);
				setResult(RESULT_OK, intent);
				finish();
			}
		});
	}

	@Override
	public void loadData() {
		RtnValueDto cates = MyApplication.getInstance().getDataService().getCategorys();
		if (cates != null) {
			Message msg = mUIHandler.obtainMessage(WHAT_DID_LOAD_DATA);
			msg.obj = cates;
			msg.sendToTarget();
		} else {
			Toast.makeText(context, "暂无数据", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_search: {
				Intent intent = new Intent(this, SearchGoodsActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				finish();
				break;
			}
			case R.id.btn_back: {
				setResult(RESULT_CANCELED, null);
				finish();
				break;
			}
		}
		super.onClick(v);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		cateList.clear();
		cateList = null;
		if (listView != null) {
			listView.invalidate();
			listView = null;
		}
		if (cateAdapter != null) {
			cateAdapter.notifyDataSetChanged();
			cateAdapter.notifyDataSetInvalidated();
			cateAdapter = null;
		}
	}

	@SuppressLint("HandlerLeak")
	private Handler	mUIHandler	= new Handler() {

									@Override
									public void handleMessage(Message msg) {
										progressBar.setVisibility(View.GONE);
										switch (msg.what) {
											case WHAT_DID_LOAD_DATA: {
												if (msg.obj != null) {
													RtnValueDto obj = (RtnValueDto) msg.obj;
													List<Category> strings = obj.getCateData();
													if (strings != null && !strings.isEmpty()) {
														for (int i = 0; i < strings.size(); i++) {
															Category each = strings.get(i);
															cateList.add(each);
														}
														cateAdapter.notifyDataSetChanged();
													}
												}
												break;
											}
										}
									}
								};

}
