
package com.holding.smile.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.holding.smile.R;
import com.holding.smile.dto.OrderDto;

public class MyOrdersAdapter extends BaseAdapter {
	private Context			context;
	private List<OrderDto>	orderList;

	// 自己定义的构造函数
	public MyOrdersAdapter(Context context, List<OrderDto> orderList) {
		this.orderList = orderList;
		this.context = context;
	}

	@Override
	public int getCount() {
		return orderList.size();
	}

	@Override
	public Object getItem(int position) {
		return orderList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	static class ViewHolder {
		TextView	number;
		TextView	time;
		TextView	price;
		TextView	totalnum;
		ImageView	cover;
		Button		moreButton;
	}

	@Override
	public View getView(final int position, View convertView, final ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.order_list, null);
			holder = new ViewHolder();

			holder.number = (TextView) convertView.findViewById(R.id.order_list_number);
			holder.time = (TextView) convertView.findViewById(R.id.order_list_time);
			holder.price = (TextView) convertView.findViewById(R.id.order_list_price_value);
			holder.totalnum = (TextView) convertView.findViewById(R.id.order_list_totalnum_value);
			holder.moreButton = (Button) convertView.findViewById(R.id.order_list_button_more);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final OrderDto order = (OrderDto) getItem(position);
		holder.number.setText(order.getNumber());
		holder.time.setText(order.getTime());
		holder.price.setText(String.valueOf(order.getTotal()));
		holder.totalnum.setText(String.valueOf(order.getQty()));

		convertView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(context, "点击了某一个订单", Toast.LENGTH_SHORT).show();
				// Intent intent = new Intent(context,
				// IdcardEditActivity.class);
				// intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				// intent.putExtra("order", order);
				// ((Activity)
				// parent.getContext()).startActivityForResult(intent,
				// BaseActivity.IDCARD_EDIT_CODE);
			}
		});
		return convertView;
	}
}