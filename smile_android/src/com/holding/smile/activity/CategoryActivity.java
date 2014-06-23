
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

import com.holding.smile.R;
import com.holding.smile.adapter.CategoryAdapter;
import com.holding.smile.dto.RtnValueDto;
import com.holding.smile.entity.Category;
import com.holding.smile.tools.CodeValidator;

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
		startTask();

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent();
				Category cate = null;
				if (id == -1) {// id为－1，是指全部
					cate = new Category();
					cate.setName(context.getString(R.string.all_cate));
				} else {
					cate = cateList.get(position);
				}
				intent.putExtra("cate", cate);
				setResult(RESULT_OK, intent);
				finish();
			}
		});
	}

	@Override
	public synchronized void loadData() {
		// 默认全部
		Category cate = new Category();
		cate.setName(context.getString(R.string.all_cate));
		cateList.add(cate);
		RtnValueDto cates = MyApplication.getInstance().getDataService().getCategorys();

		Message msg = mUIHandler.obtainMessage(WHAT_DID_LOAD_DATA);
		msg.obj = cates;
		msg.sendToTarget();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
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
		if (listView != null) {
			listView.invalidate();
			listView = null;
		}
		if (cateAdapter != null) {
			cateAdapter.notifyDataSetChanged();
			cateAdapter.notifyDataSetInvalidated();
			cateAdapter = null;
		}
		cateList.clear();
		cateList = null;
	}

	@SuppressLint("HandlerLeak")
	private Handler	mUIHandler	= new Handler() {

									@Override
									public void handleMessage(Message msg) {
										switch (msg.what) {
											case WHAT_DID_LOAD_DATA: {
												if (msg.obj != null) {
													RtnValueDto obj = (RtnValueDto) msg.obj;
													if (CodeValidator.dealCode(context, obj)) {
														List<Category> strings = obj.getCateData();
														if (strings != null && !strings.isEmpty()) {
															for (int i = 0; i < strings.size(); i++) {
																Category each = strings.get(i);
																cateList.add(each);
															}
															cateAdapter.notifyDataSetChanged();
														}
													}
												}
												break;
											}
										}
										waitCloseProgressBar();
									}

								};

}
