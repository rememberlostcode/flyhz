
package com.holding.smile.activity;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.holding.smile.R;
import com.holding.smile.adapter.MyOrderInformAdapter;
import com.holding.smile.dto.OrderDetailDto;
import com.holding.smile.dto.OrderDto;
import com.holding.smile.myview.MyListView;

/**
 * 订单详情
 * 
 * @author zhangb 2014年4月28日 下午3:04:37
 * 
 */
public class OrderDetailActivity extends BaseActivity implements OnClickListener {
	private List<OrderDetailDto>	list;
	private MyOrderInformAdapter	adapter;
	private MyListView				listView;
	private OrderDto				order;
	private TextView				total;

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
					total = (TextView) findViewById(R.id.order_detail_total);
					total.setText("共计" + order.getQty() + "件商品，￥" + order.getTotal() + "元");
					list = order.getDetails();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		adapter = new MyOrderInformAdapter(OrderDetailActivity.this, list, null, null, null);
		adapter.setIsShowOrder(true);
		// adapter.setActivityParent(context);
		listView.setAdapter(adapter);
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
