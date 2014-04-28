
package com.holding.smile.adapter;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.holding.smile.R;
import com.holding.smile.activity.MyApplication;
import com.holding.smile.activity.OrderDetailActivity;
import com.holding.smile.dto.OrderDto;
import com.holding.smile.dto.RtnValueDto;
import com.holding.smile.myview.MyListView;

public class MyOrdersAdapter extends BaseAdapter {
	private Context					context;
	private List<OrderDto>			orderList;
	private MyListView				listView;
	private MyOrderInformAdapter	orderAdapter;

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
		Button		statusButton;
	}

	@Override
	public View getView(final int position, View convertView, final ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.order_list, null);
			holder = new ViewHolder();

			holder.number = (TextView) convertView.findViewById(R.id.order_list_number);
			holder.time = (TextView) convertView.findViewById(R.id.order_list_time);
			holder.price = (TextView) convertView.findViewById(R.id.order_list_price_value);
			holder.totalnum = (TextView) convertView.findViewById(R.id.order_list_totalnum_value);
			holder.statusButton = (Button) convertView.findViewById(R.id.order_list_status);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final OrderDto order = (OrderDto) getItem(position);
		holder.number.setText(order.getNumber());
		holder.time.setText(order.getTime());
		holder.price.setText(String.valueOf(order.getTotal()));
		holder.totalnum.setText(String.valueOf(order.getQty()));

		holder.statusButton.setText(getTextByStatus(order.getStatus()));
		holder.statusButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(context, "点击了status", Toast.LENGTH_SHORT).show();
			}
		});
		convertView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, OrderDetailActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.putExtra("order", order);
				((Activity) parent.getContext()).startActivity(intent);
			}
		});
		convertView.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				final String[] mItems = { "详情", "关闭" };
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				// builder.setTitle("订单操作");
				builder.setItems(mItems, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// 点击后弹出窗口选择了第几项
						switch (which) {
							case 0:
								Intent intent = new Intent(context, OrderDetailActivity.class);
								intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								intent.putExtra("order", order);
								((Activity) parent.getContext()).startActivity(intent);
								break;
							case 1:
								RtnValueDto rvd = MyApplication.getInstance().getSubmitService()
																.closeOrder(order.getId());
								if (200000 == rvd.getCode()) {
									Toast.makeText(context, "订单已关闭", Toast.LENGTH_SHORT).show();
									holder.statusButton.setText(getTextByStatus("50"));
								}
								break;
						}
					}
				});
				builder.create().show();
				return false;
			}
		});

		listView = (MyListView) convertView.findViewById(R.id.order_list_content);
		orderAdapter = new MyOrderInformAdapter(context, order.getDetails(), new TextView(context),
				null, null);
		orderAdapter.setIsShowOrder(true);
		orderAdapter.setActivityParent(parent);
		listView.setAdapter(orderAdapter);
		return convertView;
	}

	private String getTextByStatus(String status) {
		// 10待支付；11支付中；12已支付；13缺少身份证；14已有身份证；15发货中；20已发货；21国外清关；30国内清关；40国内物流；50已关闭；60已完成；70已删除；
		String text = "待支付";
		if (status == null) {
			return text;
		}
		int ints = Integer.parseInt(status);
		switch (ints) {
			case 10:
				text = "待支付";
				break;
			case 11:
				text = "支付中";
				break;
			case 12:
				text = "已支付";
				break;
			case 13:
				text = "缺少身份证";
				break;
			case 14:
				text = "已有身份证";
				break;
			case 15:
				text = "发货中";
				break;
			case 20:
				text = "已发货";
				break;
			case 21:
				text = "国外清关";
				break;
			case 30:
				text = "国内清关";
				break;
			case 40:
				text = "国内物流";
				break;
			case 50:
				text = "已关闭";
				break;
			case 60:
				text = "已完成";
				break;
			case 70:
				text = "已删除";
				break;
			default:
				break;
		}
		return text;
	}

}
