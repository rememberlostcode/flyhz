
package com.holding.smile.activity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.holding.smile.R;
import com.holding.smile.adapter.MyOrdersAdapter;
import com.holding.smile.dto.OrderDto;
import com.holding.smile.dto.RtnValueDto;
import com.holding.smile.myview.MyListView;
import com.holding.smile.tools.CodeValidator;
import com.holding.smile.tools.ToastUtils;

/**
 * 我的订单
 * 
 * @author zhangb 2014年4月24日 上午10:58:56
 * 
 */
public class MyOrdersActivity extends BaseActivity implements OnClickListener {
	private List<OrderDto>		list;
	private MyOrdersAdapter		adapter;
	private MyListView			listView;

	private String				status;

	private TextView			editView;

	private LinearLayout		statusLayout;
	private TextView			allButton;
	private TextView			finshButton;
	private TextView			unfinshButton;

	private RelativeLayout		footerMyOrders;
	private ImageView			allChecked;
	private TextView			allPayButton;

	private View				footerView;

	public static final String	NEED_RECEIVE	= "finsh";
	public static final String	NEED_PAY		= "unfinsh";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentLayout(R.layout.order_manager);

		try {
			Intent intent = getIntent();
			if (intent.getExtras() != null && intent.getExtras().getSerializable("status") != null) {
				status = intent.getExtras().getString("status");
			} else {
				status = "";
			}
		} catch (Exception e) {
			Log.e(MyApplication.getClassName(this.getClass().getName()), e.getMessage());
		}

	}

	@Override
	public void onStart() {
		super.onStart();
		displayHeaderBack().setOnClickListener(this);

		displayHeaderDescription().setText("订单管理");

		if (footerView == null) {
			footerView = displayFooterMainOrder();
		}
		footerView.setVisibility(View.VISIBLE);

		editView = displayHeaderRight();
		editView.setText(R.string.edit);
		editView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (statusLayout == null) {
					statusLayout = (LinearLayout) findViewById(R.id.list_orders_status_layout);
				}

				if ("编辑".equals(editView.getText().toString())) {
					editView.setText(R.string.finish);
					adapter.showEdit(true);
					footerView.setVisibility(View.GONE);
					statusLayout.setVisibility(View.GONE);
				} else {
					editView.setText(R.string.edit);
					adapter.showEdit(false);
					footerView.setVisibility(View.VISIBLE);
					statusLayout.setVisibility(View.VISIBLE);
					
					if(adapter.isEdited()){
						adapter.setEdited(false);
						startTask();
					}
				}
			}
		});

		footerMyOrders = (RelativeLayout) findViewById(R.id.footer_my_orders);
		footerMyOrders.setVisibility(View.GONE);

		allPayButton = (TextView) findViewById(R.id.footer_my_orders_all_pay);
		allChecked = (ImageView) findViewById(R.id.footer_my_orders_all_checked);

		allChecked.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				boolean selected = allChecked.isSelected();
				if (selected) {
					allChecked.setSelected(false);
					adapter.setSelectAll(false);
				} else {
					allChecked.setSelected(true);
					adapter.setSelectAll(true);
				}
			}
		});

		allButton = (TextView) findViewById(R.id.list_orders_button_all);
		finshButton = (TextView) findViewById(R.id.list_orders_button_receive);
		unfinshButton = (TextView) findViewById(R.id.list_orders_button_unsend);

		allButton.setOnClickListener(this);
		finshButton.setOnClickListener(this);
		unfinshButton.setOnClickListener(this);
		allPayButton.setOnClickListener(this);

		listView = (MyListView) findViewById(R.id.list_orders_list);

		if (NEED_PAY.equals(status)) {
			unfinshButton.setSelected(true);
		} else if (NEED_RECEIVE.equals(status)) {
			finshButton.setSelected(true);
		} else {
			allButton.setSelected(true);
		}
		startTask();
	}

	@Override
	public synchronized void loadData() {
		RtnValueDto orders = MyApplication.getInstance().getDataService().getOrdersList(status);
		if (CodeValidator.dealCode(context, orders)) {
			Message msg = mUIHandler.obtainMessage(1);
			msg.obj = orders;
			msg.sendToTarget();
		}
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
				allButton.setSelected(true);
				finshButton.setSelected(false);
				unfinshButton.setSelected(false);
				status = null;
				startTask();
				break;
			}
			case R.id.list_orders_button_receive: {
				allButton.setSelected(false);
				finshButton.setSelected(true);
				unfinshButton.setSelected(false);
				status = NEED_RECEIVE;
				startTask();
				break;
			}
			case R.id.list_orders_button_unsend: {
				allButton.setSelected(false);
				finshButton.setSelected(false);
				unfinshButton.setSelected(true);
				status = NEED_PAY;
				startTask();
				break;
			}
			case R.id.footer_my_orders_all_pay: {
				Set<String> numbers = adapter.getSelectNumbers();
				BigDecimal total = adapter.getTotal();

				if (numbers.isEmpty()) {
					ToastUtils.showShort(context, "请选择至少一个订单！");
					break;
				}
				Intent intent = new Intent(context, WebViewActivity.class);
				Object[] ss = numbers.toArray();
				String numberString = "";
				for (int i = 0; i < ss.length; i++) {
					if (i != 0) {
						numberString += ",";
					}
					numberString += ss[i].toString();
				}
				intent.putExtra("number", numberString);
				intent.putExtra("amount", total);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
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
												switch (msg.what) {
													case 1: {
														RtnValueDto rvd = (RtnValueDto) (msg.obj);

														if (rvd.getOrderListData() == null
																|| rvd.getOrderListData().size() == 0) {
															ToastUtils.showShort(context, "暂无数据！");
														}

														list = rvd.getOrderListData();
														if (adapter == null) {
															adapter = new MyOrdersAdapter(
																	MyOrdersActivity.this, list,
																	mUIHandler, editView);
														}
														adapter.setData(list, footerMyOrders,
																allChecked);

														listView.setAdapter(adapter);
														break;
													}
													case 3: {
														adapter.setNumZero();
														adapter.notifyDataSetChanged();
														break;
													}

												}
												closeLoading();
											}
										};
}
