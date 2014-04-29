
package com.holding.smile.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.holding.smile.R;
import com.holding.smile.adapter.OrderDetailAdapter;
import com.holding.smile.dto.OrderDto;
import com.holding.smile.myview.MyListView;

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

		try {
			Intent intent = getIntent();
			if (intent.getExtras() != null && intent.getExtras().getSerializable("order") != null) {
				order = (OrderDto) (intent.getExtras().getSerializable("order"));
				if (order != null) {
					adapter = new OrderDetailAdapter(OrderDetailActivity.this, order);
					listView.setAdapter(adapter);
					total = (TextView) findViewById(R.id.order_detail_total);
					total.setText("共计" + order.getQty() + "件商品，￥" + order.getTotal() + "元");
					System.out.println("status=" + order.getStatus());
					System.out.println("consignee=" + order.getConsignee());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
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
		}
	}

}
