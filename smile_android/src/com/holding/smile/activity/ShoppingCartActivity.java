
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.holding.smile.R;
import com.holding.smile.adapter.MyShoppingCartAdapter;
import com.holding.smile.dto.RtnValueDto;
import com.holding.smile.entity.CartItem;
import com.holding.smile.myview.MyListView;
import com.holding.smile.tools.Constants;
import com.holding.smile.tools.StrUtils;

/**
 * 
 * 类说明：购物车
 * 
 * @author robin 2014-4-22下午1:52:45
 * 
 */
public class ShoppingCartActivity extends BaseActivity implements OnClickListener {

	private static final int		WHAT_DID_LOAD_DATA		= 0;
	private static final int		WHAT_DID_UPDATE_DATA	= 1;
	private static final int		WHAT_PROGRESS_STATE		= 2;
	private ProgressDialog			pDialog;
	private int						mProgress;
	private MyListView				listView;
	private MyShoppingCartAdapter	cartAdapter;
	private ImageButton				confirmBtn;
	private TextView				total;
	private List<CartItem>			cartItemList			= new ArrayList<CartItem>();
	private Integer					gid						= null;						// 商品ID
	private String					bs						= null;						// 款号
	private Integer					qty						= 1;							// 购买数量，默认是1
	private List<Integer>			cartIds					= null;						// 从购物车结算时用，保存选中的购物车ID
	private BigDecimal				allTotal;												// 总金额

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ImageView backBtn = displayHeaderBack();
		backBtn.setOnClickListener(this);

		TextView headerDesc = displayHeaderDescription();
		headerDesc.setText(R.string.my_shopping_cart);

		startTask();

	}

	/**
	 * 初始化View
	 */
	private void initView() {
		setContentLayout(R.layout.shoping_cart_view);
		confirmBtn = (ImageButton) findViewById(R.id.confirm_btn);
		confirmBtn.setOnClickListener(this);
		total = (TextView) findViewById(R.id.total);

		initPDialog();// 初始化进度条
		listView = (MyListView) findViewById(R.id.cart_list);
		cartAdapter = new MyShoppingCartAdapter(context, cartItemList, total, pDialog, mUIHandler);
		listView.setAdapter(cartAdapter);
	}

	public void loadData() {
		RtnValueDto rtnValue = MyApplication.getInstance().getDataService().getCartItemList();
		if (rtnValue != null) {
			Message msg = mUIHandler.obtainMessage(WHAT_DID_LOAD_DATA);
			msg.obj = rtnValue;
			msg.sendToTarget();
		} else {
			Toast.makeText(context, "暂无数据", Toast.LENGTH_SHORT).show();
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_back: {
				finish();
				break;
			}
			case R.id.confirm_btn: {
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
		if (cartAdapter != null) {
			cartAdapter.notifyDataSetChanged();
			cartAdapter = null;
		}
		cartItemList.clear();
		cartItemList = null;
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
																List<CartItem> cartItems = obj.getCartListData();
																if (cartItems == null) {
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
																	if (cartItems != null
																			&& !cartItems.isEmpty()) {
																		qty = 0;
																		allTotal = new BigDecimal(0);
																		for (CartItem cart : cartItems) {
																			if (cart.getProduct() != null) {
																				cartItemList.add(cart);
																				qty += (int) cart.getQty();
																				allTotal = allTotal.add(cart.getTotal());
																			}
																		}
																		initView();
																		total.setText("共计" + qty
																				+ "件商品，￥"
																				+ allTotal + "元");
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
																CartItem itemData = obj.getCartData();
																if (itemData == null) {
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
																	qty = 0;
																	allTotal = new BigDecimal(0);
																	if (cartItemList != null
																			&& !cartItemList.isEmpty()) {
																		for (CartItem item : cartItemList) {
																			if (item.getId()
																					.equals(itemData.getId())) {
																				item.setProduct(itemData.getProduct());
																				item.setTotal(itemData.getTotal());
																			}
																			qty += (int) item.getQty();
																			allTotal = allTotal.add(item.getTotal());
																		}
																		total.setText("共计" + qty
																				+ "件商品，￥"
																				+ allTotal + "元");
																		cartAdapter.notifyDataSetChanged();
																	}
																}
															}
														} else {
															Toast.makeText(context,
																	Constants.MESSAGE_NET,
																	Toast.LENGTH_SHORT).show();
														}
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
