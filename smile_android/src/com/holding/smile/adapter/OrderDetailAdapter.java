
package com.holding.smile.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.holding.smile.R;
import com.holding.smile.activity.GoodsDetailActivity;
import com.holding.smile.activity.MyApplication;
import com.holding.smile.activity.OrderDetailActivity;
import com.holding.smile.dto.OrderDetailDto;
import com.holding.smile.dto.OrderDto;
import com.holding.smile.dto.ProductDto;
import com.holding.smile.tools.StrUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

public class OrderDetailAdapter extends BaseAdapter {
	private Context					context;
	private OrderDto				orderDto;
	private List<OrderDetailDto>	orderDetails;
	private boolean					isNeedClick		= true;
	private Integer					sWidth			= MyApplication.getInstance().getScreenWidth();
	private ViewGroup				activityParent;

	/**
	 * 不需要点击跳到订单详情页面，已经在订单详情页面调用
	 * 
	 * @param isNeedClick
	 */
	public void notNeedClick() {
		this.isNeedClick = false;
	}

	public void setActivityParent(ViewGroup activityParent) {
		this.activityParent = activityParent;
	}

	// 自己定义的构造函数
	public OrderDetailAdapter(Context context, OrderDto orderDto) {
		this.orderDto = orderDto;
		this.orderDetails = orderDto.getDetails();
		this.context = context;
	}

	@Override
	public int getCount() {
		return orderDetails.size();
	}

	@Override
	public Object getItem(int position) {
		return orderDetails.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	static class ViewHolder {
		TextView	n;
		TextView	pp;
		TextView	spq;
		ImageView	p;
		TextView	color;
		TextView	brandstyle;
	}

	@Override
	public View getView(final int position, View convertView, final ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.order_detail_product_list,
					null);
			holder = new ViewHolder();
			holder.n = (TextView) convertView.findViewById(R.id.n);
			holder.n.setWidth((int) (sWidth - 190 * MyApplication.getInstance().getDensity()));
			holder.spq = (TextView) convertView.findViewById(R.id.show_product_qty);
			holder.pp = (TextView) convertView.findViewById(R.id.pp);
			holder.p = (ImageView) convertView.findViewById(R.id.p);
			holder.brandstyle = (TextView) convertView.findViewById(R.id.brand_style);
			holder.color = (TextView) convertView.findViewById(R.id.color_cate);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if (isNeedClick) {
			convertView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(context, OrderDetailActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					intent.putExtra("order", orderDto);
					((Activity) parent.getContext()).startActivity(intent);
				}
			});
		}

		holder.p.setImageResource(R.drawable.empty_photo);
		final OrderDetailDto orderDetail = (OrderDetailDto) getItem(position);
		if (orderDetail != null && orderDetail.getProduct() != null) {
			final ProductDto jGoods = orderDetail.getProduct();
			if (StrUtils.isNotEmpty(jGoods.getName())) {
				holder.n.setText(jGoods.getName().trim());
			}
			if (StrUtils.isNotEmpty(jGoods.getBrandstyle())) {
				holder.brandstyle.setText("款号：" + jGoods.getBrandstyle());
			}
			if (StrUtils.isNotEmpty(jGoods.getColor())) {
				holder.color.setText("颜色分类：" + jGoods.getColor());
			}
			if (jGoods.getPurchasingPrice() != null) {
				holder.pp.setText("￥" + jGoods.getPurchasingPrice());
			}
			if (jGoods.getImgs() != null && jGoods.getImgs().length > 0) {
				String url = MyApplication.jgoods_img_url + jGoods.getImgs()[0];
				holder.p.setTag(url);
				ImageLoader.getInstance().displayImage(url, holder.p);
			}

			holder.p.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (jGoods != null) {
						if (activityParent != null) {
							Intent intent = new Intent(context, GoodsDetailActivity.class);
							intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							intent.putExtra("gid", jGoods.getId());
							intent.putExtra("bs", jGoods.getBrandstyle());
							((Activity) activityParent.getContext()).startActivity(intent);
						}
					}
				}
			});
			holder.spq.setText("x" + orderDetail.getQty());
		}
		return convertView;
	}
}
