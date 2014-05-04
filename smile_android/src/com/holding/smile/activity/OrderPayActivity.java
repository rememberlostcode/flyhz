
package com.holding.smile.activity;

import java.math.BigDecimal;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.holding.smile.R;
import com.holding.smile.dto.OrderDto;
import com.holding.smile.dto.RtnValueDto;
import com.holding.smile.tools.ClickUtil;
import com.holding.smile.tools.Constants;
import com.holding.smile.tools.StrUtils;

/**
 * 
 * 类说明：订单支付页
 * 
 * @author robin 2014-4-22下午1:52:45
 * 
 */
public class OrderPayActivity extends BaseActivity implements OnClickListener {

	private static final int	WHAT_DID_LOAD_DATA	= 0;
	private static final int	WHAT_PROGRESS_STATE	= 2;
	private ProgressDialog		pDialog;
	private int					mProgress;
	private Button				payBtn;
	private TextView			payMsgText;
	private TextView			numberText;
	private TextView			amountText;
	private TextView			timeText;
	private String				number;					// 订单号
	private BigDecimal			amount;					// 总额

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ImageView backBtn = displayHeaderBack();
		backBtn.setOnClickListener(this);

		TextView headerDesc = displayHeaderDescription();
		headerDesc.setText(R.string.order_pay);

		Intent intent = getIntent();
		try {
			initPDialog();// 初始化进度条
			number = intent.getExtras().getString("number");// 订单号
			String time = intent.getExtras().getString("time");// 订单生成时间
			amount = (BigDecimal) intent.getExtras().getSerializable("amount");// 总金额
			if (StrUtils.isNotEmpty(number) && StrUtils.isNotEmpty(time) && amount != null) {
				setContentLayout(R.layout.order_pay_view);
				payBtn = (Button) findViewById(R.id.taobao_pay_btn);
				payBtn.setOnClickListener(this);
				payMsgText = (TextView) findViewById(R.id.pay_message);
				numberText = (TextView) findViewById(R.id.number);
				timeText = (TextView) findViewById(R.id.time);
				amountText = (TextView) findViewById(R.id.amount);
				numberText.setText(number);
				timeText.setText(time);
				amountText.setText("￥" + amount + "元");
				mUIHandler.sendEmptyMessage(WHAT_DID_LOAD_DATA);
			} else {
				Toast.makeText(context, Constants.MESSAGE_EXCEPTION, Toast.LENGTH_SHORT).show();
			}
		} catch (Exception e) {
			Toast.makeText(context, Constants.MESSAGE_EXCEPTION, Toast.LENGTH_SHORT).show();
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
		pDialog.setMessage("正在刷新...");
		pDialog.setIndeterminate(false);
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
		pDialog.show();
		mUIHandler.sendEmptyMessage(WHAT_PROGRESS_STATE);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_back: {
				finish();
				break;
			}
			case R.id.taobao_pay_btn: {
				Intent intent = new Intent(this, WebViewActivity.class);
				intent.putExtra("number", number);
				intent.putExtra("amount", amount);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivityForResult(intent, ORDER_CODE);
				// finish();
				break;
			}
		}
		super.onClick(v);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == ORDER_CODE && RESULT_CANCELED == resultCode) {
			RtnValueDto rtnValue = MyApplication.getInstance().getDataService()
												.getOrderStatus(number);
			if (rtnValue != null) {
				OrderDto order = rtnValue.getOrderData();
				if (order != null) {
					if (!"10".equals(order.getStatus())) {
						payBtn.setVisibility(View.GONE);
						String text = ClickUtil.getTextByStatus(order.getStatus());
						payMsgText.setText(text);
						payMsgText.setVisibility(View.VISIBLE);
					}
				}
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		payBtn = null;
		numberText = null;
		amountText = null;
		timeText = null;
	}

	@SuppressLint("HandlerLeak")
	private final Handler	mUIHandler	= new Handler() {

											@Override
											public void handleMessage(Message msg) {
												// progressBar.setVisibility(View.GONE);
												switch (msg.what) {
													case WHAT_DID_LOAD_DATA: {
														if (msg.obj != null) {

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
