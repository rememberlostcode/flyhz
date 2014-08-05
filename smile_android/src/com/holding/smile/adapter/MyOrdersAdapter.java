
package com.holding.smile.adapter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.holding.smile.R;
import com.holding.smile.activity.IdcardManagerActivity;
import com.holding.smile.activity.MyApplication;
import com.holding.smile.activity.WebViewActivity;
import com.holding.smile.dto.OrderDto;
import com.holding.smile.dto.RtnValueDto;
import com.holding.smile.myview.MyListView;
import com.holding.smile.tools.ClickUtil;
import com.holding.smile.tools.Constants;
import com.holding.smile.tools.ToastUtils;

public class MyOrdersAdapter extends BaseAdapter {
	private Context				context;
	private List<OrderDto>		orderList;
	private MyListView			listView;
	private OrderDetailAdapter	orderAdapter;
	private boolean				showDelete	= false;

	private Set<String>			numbers		= new HashSet<String>();
	private BigDecimal			total		= new BigDecimal(0);
	private Boolean				selectAll	= false;
	private Handler				mUIHandler;

	private int					numTotal	= 0;
	private int					selectTotal	= 0;
	private RelativeLayout		footerMyOrders;
	private ImageView			allChecked;

	public void setNumZero() {
		numTotal = 0;
		selectTotal = 0;
	}

	// 自己定义的构造函数
	public MyOrdersAdapter(Context context, List<OrderDto> orderList, Handler mUIHandler) {
		this.orderList = orderList;
		this.context = context;
		this.mUIHandler = mUIHandler;
	}

	public void setData(List<OrderDto> contacts, RelativeLayout footerMyOrders, ImageView allChecked) {
		if (contacts == null) {
			this.orderList = new ArrayList<OrderDto>();
		} else {
			this.orderList = contacts;
		}

		this.footerMyOrders = footerMyOrders;
		this.allChecked = allChecked;

		setNumZero();
		// notifyDataSetChanged();
	}

	public void showEdit(boolean showDelete) {
		this.showDelete = showDelete;
		setNumZero();
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		if (orderList == null) {
			orderList = new ArrayList<OrderDto>();
		}
		return orderList.size();
	}

	@Override
	public Object getItem(int position) {
		if (orderList == null) {
			return null;
		}
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
		TextView	statusButton;
		TextView	total;
		ImageView	deleteButton;
		ImageView	checkBoxImage;
	}

	@Override
	public View getView(final int position, View convertView, final ViewGroup parent) {
		final ViewHolder holder;
		Log.i("order", "getView selectTotal/numTotal====" + selectTotal + "/" + numTotal);
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.order_list, null);
			holder = new ViewHolder();

			holder.number = (TextView) convertView.findViewById(R.id.order_list_number);
			holder.time = (TextView) convertView.findViewById(R.id.order_list_time);
			holder.price = (TextView) convertView.findViewById(R.id.order_list_price_value);
			holder.totalnum = (TextView) convertView.findViewById(R.id.order_list_totalnum_value);
			holder.statusButton = (TextView) convertView.findViewById(R.id.order_list_status);
			holder.deleteButton = (ImageView) convertView.findViewById(R.id.order_list_delete);
			holder.checkBoxImage = (ImageView) convertView.findViewById(R.id.order_list_checkBox);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final OrderDto order = (OrderDto) getItem(position);
		holder.number.setText(order.getNumber());
		holder.time.setText(order.getTime());
		holder.price.setText(String.valueOf(order.getTotal()));
		holder.totalnum.setText(String.valueOf(order.getQty()));

		if (order.getStatus().equals(Constants.OrderStateCode.FOR_PAYMENT.code + "")) {
			if (numbers.contains(order.getNumber())) {
				holder.checkBoxImage.setBackgroundResource(R.drawable.new_cb_checked);
				selectTotal++;
			} else {
				holder.checkBoxImage.setBackgroundResource(R.drawable.new_cb_normal);
			}
			holder.checkBoxImage.setVisibility(View.VISIBLE);
			numTotal++;
		} else {
			holder.checkBoxImage.setVisibility(View.GONE);
		}
		String status = order.getStatus();
		holder.statusButton.setText(ClickUtil.getTextByStatus(status));
		holder.statusButton.setBackgroundColor(ClickUtil.getBackgroundColorByStatus(status));
		holder.statusButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (Constants.OrderStateCode.FOR_PAYMENT.code.equals(order.getStatus())) {
					Intent intent = new Intent(context, WebViewActivity.class);
					intent.putExtra("number", order.getNumber());
					intent.putExtra("amount", order.getTotal());
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					context.startActivity(intent);
				} else if (Constants.OrderStateCode.THE_LACK_OF_IDENTITY_CARD.code.equals(order.getStatus())) {
					Intent intent = new Intent();
					intent.setClass(context, IdcardManagerActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					context.startActivity(intent);
				}
			}
		});

		holder.checkBoxImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (numbers.contains(order.getNumber())) {
					numbers.remove(order.getNumber());
					v.setBackgroundResource(R.drawable.new_cb_normal);
					total = total.subtract(order.getTotal());

					selectTotal--;
				} else {
					numbers.add(order.getNumber());
					v.setBackgroundResource(R.drawable.new_cb_checked);
					total = total.add(order.getTotal());

					selectTotal++;
				}
				if (numbers.isEmpty()) {
					selectAll = false;
				}

				Log.i("order", "selectTotal/numTotal====" + selectTotal + "/" + numTotal);
				// if(numTotal == selectTotal){
				// allChecked.setBackgroundResource(R.drawable.new_cb_checked);
				// } else {
				// allChecked.setBackgroundResource(R.drawable.new_cb_normal);
				// }

				if (selectTotal == 0) {
					footerMyOrders.setVisibility(View.GONE);
				} else {
					footerMyOrders.setVisibility(View.VISIBLE);
				}
				// mUIHandler.sendEmptyMessage(3);
			}
		});

		if (showDelete
				&& (Constants.OrderStateCode.FOR_PAYMENT.code + "").equals(order.getStatus())) {
			holder.deleteButton.setVisibility(View.VISIBLE);
			holder.deleteButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					new AlertDialog.Builder(context).setTitle("取消提示框")
													.setMessage("确定取消该订单吗？")
													.setPositiveButton("确定",
															new DialogInterface.OnClickListener() {
																public void onClick(
																		DialogInterface dialog,
																		int which) {
																	// 点击弹出层的关闭
																	RtnValueDto rvd = MyApplication.getInstance()
																									.getSubmitService()
																									.closeOrder(
																											order.getId());
																	if (200000 == rvd.getCode()) {
																		ToastUtils.showShort(
																				context, "订单已关闭！");
																		String status = Constants.OrderStateCode.HAVE_BEEN_CLOSED.code;
																		holder.statusButton.setText(ClickUtil.getTextByStatus(status));
																		holder.statusButton.setBackgroundColor(ClickUtil.getBackgroundColorByStatus(status));
																	}
																}
															}).setNegativeButton("取消", null).show();

				}
			});
		} else {
			holder.deleteButton.setVisibility(View.GONE);
		}

		listView = (MyListView) convertView.findViewById(R.id.order_list_content);
		orderAdapter = new OrderDetailAdapter(context, order);
		orderAdapter.setActivityParent(parent);
		listView.setAdapter(orderAdapter);

		return convertView;
	}

	public void setSelectAll(Boolean selectAll) {
		this.selectAll = selectAll;
		numbers.clear();
		total = new BigDecimal(0);
		if (selectAll) {
			if (orderList != null && !orderList.isEmpty()) {
				for (OrderDto item : orderList) {
					if (item.getNumber() != null && item.getStatus().equals("10")) {
						numbers.add(item.getNumber());
						total = total.add(item.getTotal());
					}
				}
			}
		}
		mUIHandler.sendEmptyMessage(3);
	}

	/**
	 * 获取已选中的itemId
	 * 
	 * @return
	 */
	public Set<String> getSelectNumbers() {
		return numbers;
	}

	public BigDecimal getTotal() {
		return total;
	}

	public Boolean getSelectAll() {
		return selectAll;
	}

}
