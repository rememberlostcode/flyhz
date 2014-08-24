
package com.holding.smile.activity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.holding.smile.R;
import com.holding.smile.adapter.MyOrderInformAdapter;
import com.holding.smile.dto.DiscountDto;
import com.holding.smile.dto.OrderDetailDto;
import com.holding.smile.dto.OrderDto;
import com.holding.smile.dto.ProductDto;
import com.holding.smile.dto.RtnValueDto;
import com.holding.smile.myview.MyListView;
import com.holding.smile.tools.CodeValidator;
import com.holding.smile.tools.Constants;
import com.holding.smile.tools.StrUtils;
import com.holding.smile.tools.ToastUtils;

/**
 * 
 * 类说明：订单确认信息页
 * 
 * @author robin 2014-4-22下午1:52:45
 * 
 */
public class OrderInformActivity extends BaseActivity implements OnClickListener {

	private static final String		TAG							= "OrderInformActivity";

	private static final int		WHAT_DID_LOAD_DATA			= 0;
	private static final int		WHAT_DID_UPDATE_DATA		= 1;
	private static final int		WHAT_DID_CONFIRMORDER_DATA	= 3;
	private MyListView				listView;
	private MyOrderInformAdapter	orderAdapter;
	private TextView				logisticEvery;													// 单个物流费
	private TextView				logisticsTotal;												// 总物流费
	private TextView				pTotalNumber;													// 产品总数
	private TextView				pTotalMoney;													// 产品合计总价格
	private TextView				confirmBtn;
	private TextView				totalNumber;
	private TextView				totalMoney;
	private OrderDto				order						= null;
	private List<OrderDetailDto>	orderDetails				= new ArrayList<OrderDetailDto>();
	private Integer					gid							= null;							// 商品ID
	private Integer					addressId					= null;							// 地址id
	private Integer					qty							= 1;								// 购买数量，默认是1
	private List<String>			cartIds						= null;							// 从购物车结算时用，保存选中的购物车ID
	private Integer					allQty						= 0;								// 结算总数量，默认是0
	private BigDecimal				allTotal					= null;							// 结算总金额
	private BigDecimal				logisticFee					= null;							// 单个物流费
	private BigDecimal				logisticsFeeTotal			= new BigDecimal(0);				// 总物流费
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
			ToastUtils.showShort(context, Constants.MESSAGE_EXCEPTION);
		}

	}

	/**
	 * 初始化View
	 */
	private void initView() {
		if (order == null)
			return;
		displayFooterMainOrderInform();
		setContentLayout(R.layout.order_inform_view);
		logisticEvery = (TextView) findViewById(R.id.logistic_every);
		logisticsTotal = (TextView) findViewById(R.id.total_logistics);
		pTotalNumber = (TextView) findViewById(R.id.product_total_number);
		pTotalMoney = (TextView) findViewById(R.id.product_total_money);

		confirmBtn = (TextView) findViewById(R.id.confirm_btn);
		confirmBtn.setOnClickListener(this);
		totalNumber = (TextView) findViewById(R.id.order_inform_total_number);
		totalMoney = (TextView) findViewById(R.id.order_inform_total_money);
		totalNumber = (TextView) findViewById(R.id.order_inform_total_number);

		listView = (MyListView) findViewById(R.id.order_list);
		orderAdapter = new MyOrderInformAdapter(context, orderDetails, null, mUIHandler, cartFlag);
		listView.setAdapter(orderAdapter);

	}

	@Override
	public synchronized void loadData() {
		if (gid != null || (cartIds != null && !cartIds.isEmpty())) {
			String pidQty = "";
			if (gid != null) {
				pidQty = gid + "_" + qty;
			}
			RtnValueDto rtnValue = MyApplication.getInstance().getSubmitService()
												.getOrderInform(pidQty, cartIds, addressId);

			Message msg = mUIHandler.obtainMessage(WHAT_DID_LOAD_DATA);
			msg.obj = rtnValue;
			msg.sendToTarget();
		} else {
			ToastUtils.showShort(context, Constants.MESSAGE_EXCEPTION);
			finish();
		}
	}

	/**
	 * 确认订单并生成订单信息
	 */
	private void confirmOrder() {
		if (gid != null || (cartIds != null && !cartIds.isEmpty())) {
			String pidQty = "";
			if (gid != null) {
				pidQty = gid + "_" + qty;
			}
			showLoading();
			RtnValueDto rtnValue = MyApplication.getInstance().getSubmitService()
												.confirmOrder(pidQty, cartIds, addressId);
			closeLoading();
			Message msg = mUIHandler.obtainMessage(WHAT_DID_CONFIRMORDER_DATA);
			msg.obj = rtnValue;
			msg.sendToTarget();
		} else {
			ToastUtils.showShort(context, Constants.MESSAGE_EXCEPTION);
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
					if (logisticFee == null)
						logisticFee = orderDetail.getLogisticsPriceEvery();
					allQty += (int) orderDetail.getQty();
					DiscountDto discount = orderDetail.getDiscount();
					if (discount != null && discount.getDp() != null) {
						allTotal = allTotal.add(discount.getDp());
					} else {
						allTotal = allTotal.add(orderDetail.getTotal());
					}
				}
			}
		}

		if (logisticFee != null) {
			logisticEvery.setText(logisticFee.setScale(0, BigDecimal.ROUND_HALF_UP) + "");
			logisticsFeeTotal = logisticFee.multiply(BigDecimal.valueOf(allQty));
			logisticsTotal.setText(logisticsFeeTotal.setScale(0, BigDecimal.ROUND_HALF_UP) + "");
		}
		pTotalNumber.setText(allQty + "");
		pTotalMoney.setText(allTotal.setScale(0, BigDecimal.ROUND_HALF_UP) + "");
		totalNumber.setText(allQty + "");
		totalMoney.setText(allTotal.add(logisticsFeeTotal).setScale(0, BigDecimal.ROUND_HALF_UP)
				+ "");
	}

	@SuppressLint("HandlerLeak")
	private final Handler	mUIHandler	= new Handler() {

											@Override
											public void handleMessage(Message msg) {
												try {
													switch (msg.what) {
														case WHAT_DID_LOAD_DATA: {
															if (msg.obj != null) {
																RtnValueDto obj = (RtnValueDto) msg.obj;
																if (CodeValidator.dealCode(context,
																		obj)) {
																	order = obj.getOrderData();
																	if (order == null) {
																		if (obj.getValidate() != null
																				&& StrUtils.isNotEmpty(obj.getValidate()
																											.getMessage())) {
																			ToastUtils.showShort(
																					context,
																					obj.getValidate()
																						.getMessage());
																		} else {
																			ToastUtils.showShort(
																					context,
																					Constants.MESSAGE_EXCEPTION);
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
																if (CodeValidator.dealCode(context,
																		obj)) {
																	ProductDto product = obj.getProductData();
																	if (product == null) {
																		if (obj.getValidate() != null
																				&& StrUtils.isNotEmpty(obj.getValidate()
																											.getMessage())) {
																			ToastUtils.showShort(
																					context,
																					obj.getValidate()
																						.getMessage());
																		} else {
																			ToastUtils.showShort(
																					context,
																					Constants.MESSAGE_EXCEPTION);
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
																ToastUtils.showShort(context,
																		Constants.MESSAGE_EXCEPTION);
															}
															break;
														}
														case WHAT_DID_CONFIRMORDER_DATA: {
															if (msg.obj != null) {
																RtnValueDto obj = (RtnValueDto) msg.obj;
																if (CodeValidator.dealCode(context,
																		obj)) {
																	order = obj.getOrderData();
																	if (order == null) {
																		if (obj.getValidate() != null
																				&& StrUtils.isNotEmpty(obj.getValidate()
																											.getMessage())) {
																			ToastUtils.showShort(
																					context,
																					obj.getValidate()
																						.getMessage());
																		} else {
																			ToastUtils.showShort(
																					context,
																					Constants.MESSAGE_EXCEPTION);
																		}
																	} else {
																		if (order.getNumber() != null
																				&& order.getTime() != null
																				&& order.getTotal() != null) {

																			Intent intent = new Intent(
																					context,
																					OrderPayActivity.class);
																			intent.putExtra(
																					"number",
																					order.getNumber());
																			intent.putExtra("time",
																					order.getTime());
																			intent.putExtra(
																					"amount",
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

																				// 更新购物车中的商品数量信息
																				if (allQty != null) {
																					MyApplication.getInstance()
																									.getSqliteService()
																									.addUserShoppingCount(
																											0 - allQty);
																				}

																				if (cartFlag) {// 从购物车结算时要返回刷新
																					setResult(
																							RESULT_OK,
																							null);
																				}
																			}
																			finish();
																		} else {
																			ToastUtils.showShort(
																					context,
																					Constants.MESSAGE_EXCEPTION);
																		}
																	}
																}
															}
															break;
														}
													}
													closeLoading();
												} catch (Exception e) {
													Log.i(TAG, e.getMessage());
													ToastUtils.showShort(context,
															Constants.MESSAGE_EXCEPTION);

													if (cartFlag) {// 从购物车结算时要返回刷新
														setResult(RESULT_OK, null);
													}
													finish();
												}
											}

										};
}
