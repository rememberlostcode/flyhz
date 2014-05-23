
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
import com.holding.smile.activity.IdcardManagerActivity;
import com.holding.smile.activity.MyApplication;
import com.holding.smile.activity.WebViewActivity;
import com.holding.smile.dto.OrderDto;
import com.holding.smile.dto.RtnValueDto;
import com.holding.smile.myview.MyListView;
import com.holding.smile.tools.ClickUtil;
import com.holding.smile.tools.Constants;

public class MyOrdersAdapter extends BaseAdapter {
	private Context				context;
	private List<OrderDto>		orderList;
	private MyListView			listView;
	private OrderDetailAdapter	orderAdapter;
	private boolean				showDelete	= true;
	
	private Set<String>	numbers			= new HashSet<String>();
	private BigDecimal	total = new BigDecimal(0);
	private Boolean			selectAll		= false;
	private Handler mUIHandler;

	// 自己定义的构造函数
	public MyOrdersAdapter(Context context, List<OrderDto> orderList,Handler mUIHandler) {
		this.orderList = orderList;
		this.context = context;
		this.mUIHandler = mUIHandler;
	}

	public void setData(List<OrderDto> contacts) {
		if (contacts == null) {
			this.orderList = new ArrayList<OrderDto>();
		} else {
			this.orderList = contacts;
		}
		notifyDataSetChanged();
	}

	public void showEdit(boolean showDelete) {
		this.showDelete = showDelete;
		notifyDataSetChanged();
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
		TextView	total;
		Button		deleteButton;
		ImageView	checkBoxImage;
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
			holder.deleteButton = (Button) convertView.findViewById(R.id.order_list_delete);
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
		
		if (selectAll) {
			holder.checkBoxImage.setBackgroundResource(R.drawable.icon_choice);
			total = total.add(order.getTotal());
		} else {
			holder.checkBoxImage.setBackgroundResource(R.drawable.icon_no_choice);
			total = total.subtract(order.getTotal());
		}
		if (numbers.contains(order.getNumber())) {
			holder.checkBoxImage.setBackgroundResource(R.drawable.icon_choice);
			total = total.add(order.getTotal());
		} else {
			holder.checkBoxImage.setBackgroundResource(R.drawable.icon_no_choice);
			total = total.subtract(order.getTotal());
		}

		holder.statusButton.setText(ClickUtil.getTextByStatus(order.getStatus()));
		holder.statusButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (Constants.OrderStateCode.FOR_PAYMENT.code.equals(order.getStatus())) {
					Intent intent = new Intent(context, WebViewActivity.class);
					
					if(numbers.isEmpty()){
						intent.putExtra("number", order.getNumber());
						intent.putExtra("amount", order.getTotal());
					} else {
						Object[] ss = numbers.toArray();
						String numberString = "";
						for(int i=0;i<ss.length;i++){
							if(i!=0){
								numberString += ",";
							}
							numberString += ss[i].toString();
						}
						intent.putExtra("number", numberString);
						intent.putExtra("amount", order.getTotal());
					}
					
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					context.startActivity(intent);
				} else if (Constants.OrderStateCode.THE_LACK_OF_IDENTITY_CARD.code.equals(order.getStatus())) {
					Intent intent = new Intent();
					intent.setClass(context, IdcardManagerActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
					context.startActivity(intent);
				}
			}
		});
		
		holder.checkBoxImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!numbers.contains(order.getNumber())) {
					numbers.add(order.getNumber());
					v.setBackgroundResource(R.drawable.icon_choice);
					total = total.add(order.getTotal());
				} else {
					numbers.remove(order.getNumber());
					v.setBackgroundResource(R.drawable.icon_no_choice);
					total = total.subtract(order.getTotal());
				}
				if (numbers.isEmpty()) {
					selectAll = false;
				}
				mUIHandler.sendEmptyMessage(3);

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
																		Toast.makeText(context,
																				"订单已关闭",
																				Toast.LENGTH_SHORT)
																				.show();
																		holder.statusButton.setText(ClickUtil.getTextByStatus(Constants.OrderStateCode.HAVE_BEEN_CLOSED.code));
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
		if (selectAll) {
			numbers.clear();
			total = new BigDecimal(0);
			if (orderList != null && !orderList.isEmpty()) {
				for (OrderDto item : orderList) {
					if (item.getNumber() != null){
						numbers.add(item.getNumber());
						total = total.add(item.getTotal());
					}
				}
			}
		} else {
			numbers.clear();
			total = new BigDecimal(0);
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

	public Boolean getSelectAll() {
		return selectAll;
	}
	
}
