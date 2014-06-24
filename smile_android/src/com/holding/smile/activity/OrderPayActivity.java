
package com.holding.smile.activity;

import java.math.BigDecimal;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.holding.smile.R;
import com.holding.smile.dto.OrderDto;
import com.holding.smile.dto.RtnValueDto;
import com.holding.smile.tools.ClickUtil;
import com.holding.smile.tools.Constants;
import com.holding.smile.tools.StrUtils;
import com.holding.smile.tools.ToastUtils;

/**
 * 
 * 类说明：订单支付页
 * 
 * @author robin 2014-4-22下午1:52:45
 * 
 */
public class OrderPayActivity extends BaseActivity implements OnClickListener {

	private Button				payBtn;
	private TextView			payMsgText;
	private TextView			numberText;
	private TextView			amountText;
	private TextView			timeText;
	private String		number;	// 订单号
	private BigDecimal	amount;	// 总额

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ImageView backBtn = displayHeaderBack();
		backBtn.setOnClickListener(this);

		TextView headerDesc = displayHeaderDescription();
		headerDesc.setText(R.string.order_pay);

		progressBar.setVisibility(View.VISIBLE);
		Intent intent = getIntent();
		try {
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
			} else {
				ToastUtils.showShort(context, Constants.MESSAGE_EXCEPTION);
			}
		} catch (Exception e) {
			ToastUtils.showShort(context, Constants.MESSAGE_EXCEPTION);
		}
		waitCloseProgressBar();
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
			progressBar.setVisibility(View.VISIBLE);
			RtnValueDto rtnValue = MyApplication.getInstance().getDataService()
												.getOrderStatus(number);
			if (rtnValue != null) {
				OrderDto order = rtnValue.getOrderData();
				if (order != null && order.getStatus() != null) {
					if (!"10".equals(order.getStatus())) {
						payBtn.setVisibility(View.GONE);
						String text = ClickUtil.getTextByStatus(order.getStatus());
						payMsgText.setText(text);
						payMsgText.setVisibility(View.VISIBLE);
					}
				}
			}
			waitCloseProgressBar();
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

}
