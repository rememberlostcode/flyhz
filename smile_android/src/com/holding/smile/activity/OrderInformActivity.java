
package com.holding.smile.activity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
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
import com.holding.smile.dto.ProductDto;
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

	private static final int		WHAT_DID_LOAD_DATA			= 0;
	private static final int		WHAT_DID_UPDATE_DATA		= 1;
	private static final int		WHAT_PROGRESS_STATE			= 2;
	private static final int		WHAT_DID_CONFIRMORDER_DATA	= 3;
	private ProgressDialog			pDialog;
	private int						mProgress;
	private MyListView				listView;
	private MyOrderInformAdapter	orderAdapter;
	private TextView				confirmBtn;
	private TextView				totalNumber;
	private TextView				totalMoney;
	private OrderDto				order						= null;
	private List<OrderDetailDto>	orderDetails				= new ArrayList<OrderDetailDto>();
	private Integer					gid							= null;							// 商品ID
	private Integer					addressId					= 1;								// 地址id
	private Integer					qty							= 1;								// 购买数量，默认是1
	private List<String>			cartIds						= null;							// 从购物车结算时用，保存选中的购物车ID
	private Integer					allQty						= 0;								// 结算总数量，默认是0
	private BigDecimal				allTotal					= null;							// 结算总金额
	private boolean					cartFlag					= false;							// 为true是指从购物车中结算的

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ImageView backBtn = displayHeaderBack();
		backBtn.setOnClickListener(this);

		TextView headerDesc = displayHeaderDescription();
		headerDesc.setText(R.string.order_inform);

		Intent intent = getIntent();
		try {
			if (intent.getExtras().getSerializable("gid") != null) {
				gid = (Integer) intent.getExtras().getSerializable("gid");
			} else if (intent.getExtras().getSerializable("cartIds") != null) {
				cartIds = (List<String>) intent.getExtras().getSerializable("cartIds");
				cartFlag = true;
			}
			startTask();
		} catch (Exception e) {
			Toast.makeText(context, Constants.MESSAGE_NET, Toast.LENGTH_SHORT).show();
		}

	}

	/**
	 * 初始化View
	 */
	private void initView() {
		if (order == null)
			return;
		setContentLayout(R.layout.order_inform_view);
		confirmBtn = (TextView) findViewById(R.id.confirm_btn);
		confirmBtn.setOnClickListener(this);
		totalNumber = (TextView) findViewById(R.id.order_inform_total_number);
		totalMoney = (TextView) findViewById(R.id.order_inform_total_money);

		initPDialog();// 初始化进度条
		listView = (MyListView) findViewById(R.id.order_list);
		orderAdapter = new MyOrderInformAdapter(context, orderDetails, pDialog, mUIHandler,
				cartFlag);
		listView.setAdapter(orderAdapter);

	}

	public synchronized void loadData() {
		if (gid != null || (cartIds != null && !cartIds.isEmpty())) {
			String pidQty = "";
			if (gid != null) {
				pidQty = gid + "_" + qty;
			}
			RtnValueDto rtnValue = MyApplication.getInstance().getSubmitService()
												.getOrderInform(pidQty, cartIds, addressId);
			if (rtnValue != null) {
				Message msg = mUIHandler.obtainMessage(WHAT_DID_LOAD_DATA);
				msg.obj = rtnValue;
				msg.sendToTarget();
			} else {
				Toast.makeText(context, "暂无数据", Toast.LENGTH_SHORT).show();
				finish();
			}
		} else {
			Toast.makeText(context, Constants.MESSAGE_NET, Toast.LENGTH_SHORT).show();
			finish();
		}
	}

	/**
	 * 进度条初始化
	 */
	private void initPDialog() {
		pDialog = new ProgressDialog(this);
		// pDialog.setTitle("");
		// pDialog.closeOptionsMenu();
		pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		// pDialog.setMax(100);
		pDialog.setMessage("正在加载...");
		pDialog.setIndeterminate(false);
		pDialog.setCanceledOnTouchOutside(true);
		// pDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "右键",
		// new DialogInterface.OnClickListener() {
		// @Override
		// public void onClick(DialogInterface dialog, int which) {
		// // TODO Auto-generated method stub
		//
		// }
		// });
		pDialog.setProgress(0);
		mProgress = 0;
		// 当要显示进度效果时，再给handler发送一个空消息
		// mUIHandler.sendEmptyMessage(WHAT_PROGRESS_STATE);
	}

	/**
	 * 确认订单并生成订单信息
	 */
	private void confirmOrder() {
		if (gid != null || (cartIds != null && !cartIds.isEmpty())) {
			pDialog.show();
			mUIHandler.sendEmptyMessage(WHAT_PROGRESS_STATE);

			String pidQty = "";
			if (gid != null) {
				pidQty = gid + "_" + qty;
			}
			RtnValueDto rtnValue = MyApplication.getInstance().getSubmitService()
												.confirmOrder(pidQty, cartIds, addressId);
			if (rtnValue != null) {
				Message msg = mUIHandler.obtainMessage(WHAT_DID_CONFIRMORDER_DATA);
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
				setResult(RESULT_CANCELED, null);
				finish();
				break;
			}
			case R.id.confirm_btn: {
				confirmOrder();
				break;
			}
		}
		super.onClick(v);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == ORDER_CODE || resultCode == RESULT_CANCELED) {

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
	}

	/**
	 * 计算总额
	 */
	private void calculateTotal() {
		allQty = 0;
		allTotal = new BigDecimal(0);
		if (orderDetails != null && !orderDetails.isEmpty()) {
			for (OrderDetailDto orderDetail : orderDetails) {
				if (orderDetail != null) {
					allQty += (int) orderDetail.getQty();
					allTotal = allTotal.add(orderDetail.getTotal());
				}
			}
		}
		totalNumber.setText(allQty + "");
		totalMoney.setText(allTotal.doubleValue() + "");
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
																		initView();// 初始化view
																		calculateTotal();// 计算总额
																	}
																}
															}
														}
														break;
													}
													case WHAT_DID_UPDATE_DATA: {
														if (msg.obj != null) {
															RtnValueDto obj = (RtnValueDto) msg.obj;
															if (obj != null) {
																ProductDto product = obj.getProductData();
																if (product == null) {
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
																	if (orderDetails != null
																			&& !orderDetails.isEmpty()) {
																		for (OrderDetailDto orderDetail : orderDetails) {
																			ProductDto p = orderDetail.getProduct();
																			if (p.getId()
																					.equals(product.getId())) {
																				orderDetail.setQty(product.getQty());
																				p.setPurchasingPrice(product.getPurchasingPrice()
																											.divide(BigDecimal.valueOf(orderDetail.getQty())));
																				orderDetail.setTotal(product.getPurchasingPrice());
																				order.setTotal(orderDetail.getTotal());
																				qty = (int) orderDetail.getQty();
																				break;
																			}
																		}
																		// 计算总额
																		calculateTotal();
																		orderAdapter.notifyDataSetChanged();
																	}
																}
															}
														} else {
															Toast.makeText(context,
																	Constants.MESSAGE_NET,
																	Toast.LENGTH_SHORT).show();
														}
														if (pDialog != null)
															pDialog.dismiss();
														break;
													}
													case WHAT_DID_CONFIRMORDER_DATA: {
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
																	if (order.getNumber() != null
																			&& order.getTime() != null
																			&& order.getTotal() != null) {
																		Intent intent = new Intent(
																				context,
																				OrderPayActivity.class);
																		intent.putExtra("number",
																				order.getNumber());
																		intent.putExtra("time",
																				order.getTime());
																		intent.putExtra("amount",
																				order.getTotal());
																		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
																		startActivity(intent);

																		if (cartIds != null
																				&& !cartIds.isEmpty()) {
																			for (String cartId : cartIds) {
																				MyApplication.getInstance()
																								.getSubmitService()
																								.removeCart(
																										Integer.valueOf(cartId));
																			}

																			if (cartFlag) {// 从购物车结算时要返回刷新
																				setResult(
																						RESULT_OK,
																						null);
																			}
																		}
																		finish();
																	} else {
																		Toast.makeText(
																				context,
																				Constants.MESSAGE_NET,
																				Toast.LENGTH_SHORT)
																				.show();
																	}
																}
															}
														}
														if (pDialog != null)
															pDialog.dismiss();
														break;
													}
													case WHAT_PROGRESS_STATE: {
														if (mProgress >= 100) {
															// pDialog.dismiss();
															mProgress = 0;
														} else {
															mProgress++;
															// ProgressBar进度值加1
															pDialog.incrementProgressBy(1);
															// 延迟100毫秒后发送空消息
															mUIHandler.sendEmptyMessageDelayed(
																	WHAT_PROGRESS_STATE, 100);
														}
														break;
													}
												}
											}

										};
}
