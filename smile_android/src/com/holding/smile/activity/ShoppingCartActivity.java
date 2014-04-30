
package com.holding.smile.activity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
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
	private static final int		WHAT_DID_SELECT_DATA	= 3;
	private static final int		WHAT_DID_DEL_DATA		= 4;
	private ProgressDialog			pDialog;
	private int						mProgress;
	private TextView				editBtn;
	private MyListView				listView;
	private MyShoppingCartAdapter	cartAdapter;
	private ImageButton				payoffBtn;
	private TextView				total;
	private CheckBox				allChecked;
	private List<CartItem>			cartItemList			= new ArrayList<CartItem>();
	private Integer					allQty					= 0;							// 结算总数量，默认是0
	private BigDecimal				allTotal				= new BigDecimal(0);			// 结算总金额,默认是0

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ImageView backBtn = displayHeaderBack();
		backBtn.setOnClickListener(this);
		TextView headerDesc = displayHeaderDescription();
		headerDesc.setText(R.string.my_shopping_cart);
		editBtn = displayHeaderRight();
		editBtn.setText(R.string.edit);
		editBtn.setTag("edit");
		editBtn.setOnClickListener(this);

		startTask();

	}

	/**
	 * 初始化View
	 */
	private void initView() {
		displayFooterMain(R.id.mainfooter_four);
		displayFooterMainTotal();

		setContentLayout(R.layout.shopping_cart_view);
		payoffBtn = (ImageButton) findViewById(R.id.payoff_btn);
		payoffBtn.setOnClickListener(this);
		total = (TextView) findViewById(R.id.total);
		allChecked = (CheckBox) findViewById(R.id.all_checked);
		allChecked.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					cartAdapter.setSelectAll(true);
				} else {
					cartAdapter.setSelectAll(false);
				}

			}
		});

		allQty = 0;
		allTotal = new BigDecimal(0);
		total.setText("已选商品" + allQty + "件,合计:￥" + allTotal.doubleValue() + "元");

		initPDialog();// 初始化进度条
		listView = (MyListView) findViewById(R.id.cart_list);
		cartAdapter = new MyShoppingCartAdapter(context, cartItemList, pDialog, mUIHandler);
		listView.setAdapter(cartAdapter);
	}

	public void loadData() {
		RtnValueDto rtnValue = MyApplication.getInstance().getDataService().getCartItemList();
		Message msg = mUIHandler.obtainMessage(WHAT_DID_LOAD_DATA);
		msg.obj = rtnValue;
		msg.sendToTarget();
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
			case R.id.header_right: {
				String tag = editBtn.getTag().toString();
				if (tag != null && tag.equals("edit")) {
					editBtn.setText(R.string.cancel);
					editBtn.setTag("cancel");
					cartAdapter.setEditFlag(true);
				} else {
					editBtn.setText(R.string.edit);
					editBtn.setTag("edit");
					cartAdapter.setEditFlag(false);
				}
				break;
			}
			case R.id.payoff_btn: {
				Set<Integer> sIds = cartAdapter.getSelectIds();
				if (sIds != null && !sIds.isEmpty()) {
					List<String> cartIds = new ArrayList<String>();
					for (Integer cartId : sIds) {
						cartIds.add(String.valueOf(cartId));
					}
					Intent intent = new Intent(this, OrderInformActivity.class);
					intent.putExtra("cartIds", (Serializable) cartIds);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivityForResult(intent, ORDER_CODE);
				}
				break;
			}
		}
		super.onClick(v);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == ORDER_CODE) {
			if (resultCode == RESULT_OK) {
				cartItemList.clear();
				cartAdapter.getSelectIds().clear();
				cartAdapter.notifyDataSetChanged();
				loadData();
			}
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
		allTotal = null;
		setResult(RESULT_CANCELED, null);
	}

	/**
	 * 计算总额
	 */
	private void calculateTotal() {
		if (!cartAdapter.getSelectAll()) {
			allChecked.setChecked(false);
		}

		if (cartItemList == null || cartItemList.isEmpty()) {
			payoffBtn.setVisibility(View.GONE);
		} else {
			payoffBtn.setVisibility(View.VISIBLE);
		}
		Set<Integer> sIds = cartAdapter.getSelectIds();
		allQty = 0;
		allTotal = new BigDecimal(0);
		if (sIds != null && !sIds.isEmpty()) {
			if (cartItemList != null && !cartItemList.isEmpty()) {
				for (CartItem item : cartItemList) {
					if (sIds.contains(item.getId())) {
						allQty += (int) item.getQty();
						allTotal = allTotal.add(item.getTotal());
					}
				}
			}
		}
		total.setText("已选商品" + allQty + "件,合计:￥" + allTotal.doubleValue() + "元");
		cartAdapter.notifyDataSetChanged();
	}

	@SuppressLint("HandlerLeak")
	private final Handler	mUIHandler	= new Handler() {

											@Override
											public void handleMessage(Message msg) {
												progressBar.setVisibility(View.GONE);
												switch (msg.what) {
													case WHAT_DID_LOAD_DATA: {
														cartItemList.clear();
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
																		for (CartItem cart : cartItems) {
																			if (cart.getProduct() != null) {
																				cartItemList.add(cart);
																			}
																		}
																	}
																}
															}
														} else {
															Toast.makeText(context, "暂无数据",
																	Toast.LENGTH_SHORT).show();
														}

														initView();
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
																	if (cartItemList != null
																			&& !cartItemList.isEmpty()) {
																		for (CartItem item : cartItemList) {
																			if (item.getId()
																					.equals(itemData.getId())) {
																				item.setProduct(itemData.getProduct());
																				item.setTotal(itemData.getTotal());
																			}
																		}
																		// 计算总金额
																		calculateTotal();
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
													case WHAT_DID_DEL_DATA: {
														if (msg.obj != null) {
															Integer itemId = (Integer) msg.obj;
															if (itemId != null) {
																alert(itemId);
															}
														} else {
															Toast.makeText(context,
																	Constants.MESSAGE_NET,
																	Toast.LENGTH_SHORT).show();
														}
														pDialog.dismiss();
														break;
													}
													case WHAT_DID_SELECT_DATA: {
														// 计算总金额
														calculateTotal();
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

	private void alert(final Integer itemId) {
		new AlertDialog.Builder(this).setTitle("提示")
										.setMessage("您确定要删除此商品吗？")
										.setPositiveButton("确定",
												new DialogInterface.OnClickListener() {
													public void onClick(DialogInterface dialog,
															int which) {

														RtnValueDto rtnValue = MyApplication.getInstance()
																							.getSubmitService()
																							.removeCart(
																									itemId);
														if (rtnValue != null
																&& rtnValue.getCode()
																			.equals(200000)) {
															cartAdapter.getSelectIds().remove(
																	itemId);
															if (cartItemList != null
																	&& !cartItemList.isEmpty()) {
																for (CartItem item : cartItemList) {
																	if (item.getId().equals(itemId)) {
																		cartItemList.remove(item);
																		break;
																	}
																}
																// 计算总金额
																calculateTotal();
															}
															Toast.makeText(context, "删除成功！",
																	Toast.LENGTH_SHORT).show();
														} else {
															Toast.makeText(context,
																	Constants.MESSAGE_NET,
																	Toast.LENGTH_SHORT).show();
														}

													}
												}).setNegativeButton("取消", null).show();
	}
}
