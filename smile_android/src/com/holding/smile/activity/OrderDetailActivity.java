
package com.holding.smile.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.holding.smile.R;
import com.holding.smile.adapter.OrderDetailAdapter;
import com.holding.smile.dto.OrderDto;
import com.holding.smile.myview.MyListView;
import com.holding.smile.tools.ClickUtil;
import com.holding.smile.tools.Constants;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 订单详情
 * 
 * @author zhangb 2014年4月28日 下午3:04:37
 * 
 */
public class OrderDetailActivity extends BaseActivity implements OnClickListener {
	private OrderDetailAdapter	adapter;
	private MyListView			listView;
	private OrderDto			order;
	private TextView			total;
	private TextView			orderNumberView;
	private TextView			orderTimeView;
	private TextView			consigneeAddressView;
	private TextView			consigneePhoneView;
	private TextView			consigneeNameView;

	private TextView			idcardView;
	private ImageView			idcardImageView;

	private Button				contactUsButton;
	private Button				statusButton;

	private MyListView			logisticsListView;		// 物流

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentLayout(R.layout.order_detail);
		ImageView backBtn = displayHeaderBack();
		backBtn.setOnClickListener(this);

		TextView textView = displayHeaderDescription();
		textView.setText("订单详情");
		listView = (MyListView) findViewById(R.id.order_detail_list_view);
		orderNumberView = (TextView) findViewById(R.id.order_detail_number);
		orderTimeView = (TextView) findViewById(R.id.order_detail_time);

		contactUsButton = (Button) findViewById(R.id.order_detail_contact_us);
		contactUsButton.setOnClickListener(this);
	}

	@Override
	protected void onStart() {
		super.onStart();
		try {
			Intent intent = getIntent();
			if (intent.getExtras() != null && intent.getExtras().getSerializable("order") != null) {
				order = (OrderDto) (intent.getExtras().getSerializable("order"));
				if (order != null) {
					adapter = new OrderDetailAdapter(OrderDetailActivity.this, order);
					adapter.notNeedClick();
					listView.setAdapter(adapter);

					orderNumberView.setText(order.getNumber());
					orderTimeView.setText(order.getTime());

					if (order.getConsignee() != null) {
						consigneeNameView = (TextView) findViewById(R.id.order_detail_consignee_name);
						consigneeAddressView = (TextView) findViewById(R.id.order_detail_consignee_address);
						consigneePhoneView = (TextView) findViewById(R.id.order_detail_consignee_phone);
						consigneeNameView.setText(order.getConsignee().getName());
						consigneeAddressView.setText(order.getConsignee().getAddress());
						consigneePhoneView.setText(order.getConsignee().getPhone());

						if (order.getConsignee().getIdentitycard() != null) {
							idcardView = (TextView) findViewById(R.id.order_detail_idcard);
							idcardImageView = (ImageView) findViewById(R.id.order_detail_idcard_url);

							idcardView.setText(order.getConsignee().getIdentitycard().getNumber());

							String url = MyApplication.jgoods_img_url
									+ order.getConsignee().getIdentitycard().getUrl();
							idcardImageView.setTag(url);
							
							ImageLoader.getInstance().displayImage(url, idcardImageView);
//							MyApplication.getImageLoader()
//											.DisplayImage(url, idcardImageView, false);
						}
					}

					total = (TextView) findViewById(R.id.order_detail_total);
					total.setText("共计" + order.getQty() + "件商品，￥" + order.getTotal() + "元");

					if (order.getStatus() != null && Integer.parseInt(order.getStatus()) >= 20
							&& !order.getStatus().equals("50") && order.getLogisticsDto() != null
							&& order.getLogisticsDto().getTransitStepInfoList() != null) {
						TextView textViewAddres = (TextView) findViewById(R.id.order_detail_address);
						textViewAddres.setText(textViewAddres.getText()
								+ (order.getLogisticsDto().getAddress() != null ? order.getLogisticsDto()
																						.getAddress()
										: ""));

						logisticsListView = (MyListView) findViewById(R.id.order_detail_logistics_list);
						logisticsListView.setAdapter(new ArrayAdapter<String>(this,
								R.layout.simple_list_text, order.getLogisticsDto()
																.getTransitStepInfoList()));
					} else {
						findViewById(R.id.order_detail_wuliu).setVisibility(View.GONE);
					}

					statusButton = (Button) findViewById(R.id.order_detail_status);
					statusButton.setText(ClickUtil.getTextByStatus(order.getStatus()));
					statusButton.setBackgroundColor(ClickUtil.getBackgroundColorByStatus(order.getStatus()));
					statusButton.setOnClickListener(this);
				}
			}
		} catch (Exception e) {
			Log.e(MyApplication.LOG_TAG, e.getMessage());
		}
		closeLoading();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_back: {
				setResult(RESULT_CANCELED, null);
				finish();
				break;
			}
			case R.id.order_detail_status: {
				if (order != null) {
					if (Constants.OrderStateCode.FOR_PAYMENT.code.equals(order.getStatus())) {
						Intent intent = new Intent(this, WebViewActivity.class);
						intent.putExtra("number", order.getNumber());
						intent.putExtra("amount", order.getTotal());
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(intent);
					} else if (Constants.OrderStateCode.THE_LACK_OF_IDENTITY_CARD.code.equals(order.getStatus())) {
						Intent intent = new Intent(this, IdcardManagerActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(intent);
					}
				}
				break;
			}
			case R.id.order_detail_contact_us: {
				ClickUtil.sendEmail(this);
				break;
			}
		}
	}

}
