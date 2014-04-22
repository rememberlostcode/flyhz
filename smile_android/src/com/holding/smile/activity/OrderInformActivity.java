
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.holding.smile.R;
import com.holding.smile.adapter.MyOrderInformAdapter;
import com.holding.smile.dto.OrderDetailDto;
import com.holding.smile.dto.OrderDto;
import com.holding.smile.dto.RtnValueDto;
import com.holding.smile.myview.MyListView;
import com.holding.smile.tools.Constants;
import com.holding.smile.tools.StrUtils;

/**
 * 
 * 类说明：订单确认信息页
 * 
 * @author robin 2014-4-22下午1:52:45
 * 
 */
public class OrderInformActivity extends BaseActivity implements OnClickListener {

	private static final int		WHAT_DID_LOAD_DATA	= 0;
	private MyListView				listView;
	private MyOrderInformAdapter	orderAdapter;
	private OrderDto				order				= null;
	private List<OrderDetailDto>	orderDetails		= new ArrayList<OrderDetailDto>();
	private Integer					gid					= null;							// 商品ID
	private String					bs					= null;							// 款号
	private Integer					addressId			= 1;								// 地址id
	private Integer					qty					= 1;								// 购买数量，默认是1
	private List<Integer>			cartIds				= null;							// 从购物车结算时用，保存选中的购物车ID

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentLayout(R.layout.order_inform_view);
		ImageView backBtn = displayHeaderBack();
		backBtn.setOnClickListener(this);

		TextView headerDesc = displayHeaderDescription();
		headerDesc.setText(R.string.order_inform);

		Intent intent = getIntent();
		try {
			gid = (Integer) intent.getExtras().getSerializable("gid");

			listView = (MyListView) findViewById(R.id.order_list);
			orderAdapter = new MyOrderInformAdapter(context, orderDetails);
			listView.setAdapter(orderAdapter);

			startTask();
		} catch (Exception e) {
			Toast.makeText(context, Constants.MESSAGE_NET, Toast.LENGTH_SHORT).show();
		}

	}

	public void loadData() {
		if (gid != null) {
			String pidQty = gid + "_" + qty;
			RtnValueDto rtnValue = MyApplication.getInstance().getDataService()
												.getOrderInform(pidQty, cartIds, addressId);
			if (rtnValue != null) {
				Message msg = mUIHandler.obtainMessage(WHAT_DID_LOAD_DATA);
				msg.obj = rtnValue;
				msg.sendToTarget();
			} else {
				Toast.makeText(context, "暂无数据", Toast.LENGTH_SHORT).show();
			}
		} else {
			Toast.makeText(context, Constants.MESSAGE_NET, Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_back: {
				finish();
				break;
			}
			case R.id.nowbuy: {
				Toast.makeText(context, "您点了立即购买", Toast.LENGTH_SHORT).show();
				// Intent intent = new Intent(this, SearchGoodsActivity.class);
				// intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				// startActivity(intent);
				// finish();
				break;
			}
			case R.id.addcart: {
				Toast.makeText(context, "您点了加入购物车", Toast.LENGTH_SHORT).show();
				// Intent intent = new Intent(this, SearchGoodsActivity.class);
				// intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				// startActivity(intent);
				break;
			}
		}
		super.onClick(v);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == SEARCH_CODE || resultCode == RESULT_CANCELED) {

		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (listView != null) {
			listView.invalidate();
			listView = null;
		}
		if (orderAdapter != null) {
			orderAdapter.notifyDataSetChanged();
			orderAdapter = null;
		}
		orderDetails.clear();
		orderDetails = null;
		setResult(RESULT_CANCELED, null);
	}

	@SuppressLint("HandlerLeak")
	private final Handler	mUIHandler	= new Handler() {

											@Override
											public void handleMessage(Message msg) {
												progressBar.setVisibility(View.GONE);
												switch (msg.what) {
													case WHAT_DID_LOAD_DATA: {
														if (msg.obj != null) {
															RtnValueDto obj = (RtnValueDto) msg.obj;
															if (obj != null) {
																order = obj.getOrderData();
																if (order == null) {
																	if (obj.getValidate() != null
																			&& StrUtils.isNotEmpty(obj.getValidate()
																										.getMessage())) {
																		Toast.makeText(
																				context,
																				obj.getValidate()
																					.getMessage(),
																				Toast.LENGTH_SHORT)
																				.show();
																	} else {
																		Toast.makeText(
																				context,
																				Constants.MESSAGE_NET,
																				Toast.LENGTH_SHORT)
																				.show();
																	}
																} else {
																	orderDetails = order.getDetails();
																	if (orderDetails != null
																			&& !orderDetails.isEmpty()) {
																		orderAdapter.notifyDataSetChanged();
																	}
																}
															}
														}
														break;
													}
												}
											}
										};
}
