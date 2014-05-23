
package com.holding.smile.activity;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.holding.smile.R;
import com.holding.smile.adapter.MyOrdersAdapter;
import com.holding.smile.dto.OrderDto;
import com.holding.smile.dto.RtnValueDto;
import com.holding.smile.myview.MyListView;

/**
 * 我的订单
 * 
 * @author zhangb 2014年4月24日 上午10:58:56
 * 
 */
public class MyOrdersActivity extends BaseActivity implements OnClickListener {
	private List<OrderDto>	list;
	private MyOrdersAdapter	adapter;
	private MyListView		listView;

	private String			status;

	private TextView		editView;
	private Button			allButton;
	private Button			finshButton;
	private Button			unfinshButton;
	
	private CheckBox				allChecked;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentLayout(R.layout.order_manager);
		ImageView backBtn = displayHeaderBack();
		backBtn.setOnClickListener(this);

		TextView textView = displayHeaderDescription();
		textView.setText("订单管理");
		
		displayFooterMainOrder();

		editView = displayHeaderRight();
		editView.setText("编辑");
		editView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if ("编辑".equals(editView.getText().toString())) {
					editView.setText("取消");
					adapter.showEdit(true);
				} else {
					editView.setText("编辑");
					adapter.showEdit(false);
				}
			}
		});

		allChecked = (CheckBox) findViewById(R.id.footer_my_orders_all_checked);
		allChecked.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					adapter.setSelectAll(true);
				} else {
					adapter.setSelectAll(false);
				}

			}
		});
		allButton = (Button) findViewById(R.id.list_orders_button_all);
		finshButton = (Button) findViewById(R.id.list_orders_button_finsh);
		unfinshButton = (Button) findViewById(R.id.list_orders_button_unfinsh);

		allButton.setOnClickListener(this);
		finshButton.setOnClickListener(this);
		unfinshButton.setOnClickListener(this);

		listView = (MyListView) findViewById(R.id.list_orders_list);
		startTask();
	}

	@Override
	public void loadData() {
		RtnValueDto orders = MyApplication.getInstance().getDataService().getOrdersList(status);
		Message msg = mUIHandler.obtainMessage(1);
		msg.obj = orders;
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
			case R.id.list_orders_button_all: {
				status = null;
				startTask();
				break;
			}
			case R.id.list_orders_button_finsh: {
				status = "finsh";
				startTask();
				break;
			}
			case R.id.list_orders_button_unfinsh: {
				status = "unfinsh";
				startTask();
				break;
			}
		}
		super.onClick(v);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == IDCARD_EDIT_CODE && resultCode == RESULT_OK) {
			startTask();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@SuppressLint("HandlerLeak")
	private final Handler	mUIHandler	= new Handler() {

											@Override
											public void handleMessage(Message msg) {
												progressBar.setVisibility(View.GONE);
												switch (msg.what) {
													case 1: {
														RtnValueDto rvd = (RtnValueDto) (msg.obj);
														list = rvd.getOrderListData();

														if (list == null || list.size() == 0) {
															Toast.makeText(context, "暂无数据",
																	Toast.LENGTH_SHORT).show();
														}

														if (adapter != null) {
															adapter.setData(list);
														} else {
															adapter = new MyOrdersAdapter(
																	MyOrdersActivity.this, list,mUIHandler);
														}

														listView.setAdapter(adapter);
														if (status == null) {
															allButton.setBackgroundResource(R.color.lightgreen);
															finshButton.setBackgroundResource(R.color.orange);
															unfinshButton.setBackgroundResource(R.color.orange);
														} else if ("finsh".equals(status)) {
															allButton.setBackgroundResource(R.color.orange);
															finshButton.setBackgroundResource(R.color.lightgreen);
															unfinshButton.setBackgroundResource(R.color.orange);
														} else {
															allButton.setBackgroundResource(R.color.orange);
															finshButton.setBackgroundResource(R.color.orange);
															unfinshButton.setBackgroundResource(R.color.lightgreen);
														}
														break;
													}
													case 3: {
														if (!adapter.getSelectAll()) {
															allChecked.setChecked(false);
														}
														break;
													}
													
												}
											}
										};
}
