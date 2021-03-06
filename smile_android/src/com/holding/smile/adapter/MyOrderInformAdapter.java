
package com.holding.smile.adapter;

import java.math.BigDecimal;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.holding.smile.R;
import com.holding.smile.activity.GoodsDetailActivity;
import com.holding.smile.activity.MyApplication;
import com.holding.smile.dto.DiscountDto;
import com.holding.smile.dto.OrderDetailDto;
import com.holding.smile.dto.ProductDto;
import com.holding.smile.dto.RtnValueDto;
import com.holding.smile.tools.StrUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

public class MyOrderInformAdapter extends BaseAdapter {
	private Context					context;
	private List<OrderDetailDto>	orderDetails;
	private Integer					sWidth		= MyApplication.getInstance().getScreenWidth();
	private ProgressBar				progressBar;
	private Handler					mUIHandler;
	private boolean					cartFlag	= false;										// 为true时，说明是从购物车结算的，这时不能修改购买数量

	// 自己定义的构造函数
	public MyOrderInformAdapter(Context context, List<OrderDetailDto> contacts,
			ProgressBar progressBar, Handler mUIHandler, boolean cartFlag) {
		this.orderDetails = contacts;
		this.context = context;
		this.progressBar = progressBar;
		this.mUIHandler = mUIHandler;
		this.cartFlag = cartFlag;
	}

	public void setData(List<OrderDetailDto> contacts) {
		this.orderDetails = contacts;
		notifyDataSetChanged();
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
		TextView	huise_pp;
		TextView	spq;
		ImageView	p;
		TextView	color;
		TextView	brandstyle;
		TextView	qty;
		ImageView	subBtn;
		ImageView	addBtn;
	}

	@Override
	public View getView(final int position, View convertView, final ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.order_item, null);
			holder = new ViewHolder();
			holder.n = (TextView) convertView.findViewById(R.id.n);
			holder.n.setWidth((int) (sWidth - 190 * MyApplication.getInstance().getDensity()));
			holder.spq = (TextView) convertView.findViewById(R.id.show_product_qty);
			holder.pp = (TextView) convertView.findViewById(R.id.pp);
			holder.huise_pp = (TextView) convertView.findViewById(R.id.huise_pp);
			holder.p = (ImageView) convertView.findViewById(R.id.p);
			holder.brandstyle = (TextView) convertView.findViewById(R.id.brand_style);
			holder.color = (TextView) convertView.findViewById(R.id.color_cate);
			holder.qty = (TextView) convertView.findViewById(R.id.qty);
			holder.subBtn = (ImageView) convertView.findViewById(R.id.sub_qty);
			holder.addBtn = (ImageView) convertView.findViewById(R.id.add_qty);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.p.setImageResource(R.drawable.empty_photo);
		final OrderDetailDto orderDetail = (OrderDetailDto) getItem(position);
		if (orderDetail != null && orderDetail.getProduct() != null) {
			if (cartFlag) {
				holder.subBtn.setVisibility(View.GONE);
				holder.addBtn.setVisibility(View.GONE);
			}

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
				DiscountDto discount = orderDetail.getDiscount();
				if (discount != null && discount.getDp() != null) {
					holder.huise_pp.setVisibility(View.VISIBLE);
					holder.huise_pp.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
					holder.pp.setText("￥" + discount.getDp().setScale(0, BigDecimal.ROUND_HALF_UP));
					holder.huise_pp.setText("￥"
							+ orderDetail.getTotal().setScale(0, BigDecimal.ROUND_HALF_UP));
				} else {
					holder.huise_pp.setVisibility(View.GONE);
					holder.pp.setText("￥"
							+ orderDetail.getTotal().setScale(0, BigDecimal.ROUND_HALF_UP));
				}
			}
			holder.qty.setText(orderDetail.getQty() + "");
			if (jGoods.getImgs() != null && jGoods.getImgs().length > 0) {
				String url = MyApplication.jgoods_img_url + jGoods.getImgs()[0];
				holder.p.setTag(url);
				ImageLoader.getInstance().displayImage(url, holder.p,MyApplication.options);
			}

			holder.p.setOnClickListener(new OnClickListener() {
				@SuppressWarnings("unused")
				@Override
				public void onClick(View v) {
					if (jGoods != null) {
						Intent intent = new Intent(context, GoodsDetailActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						intent.putExtra("gid", jGoods.getId());
						intent.putExtra("bs", jGoods.getBrandstyle());
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						((Activity) parent.getContext()).startActivity(intent);
					} else {
						notifyDataSetChanged();
					}
				}
			});
			holder.subBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					if (orderDetail.getQty() > 1) {

						// 先发送空信息显示进度条
						showPDialog();

						orderDetail.setQty((short) (orderDetail.getQty() - 1));
						RtnValueDto rtnValue = MyApplication.getInstance()
															.getSubmitService()
															.updateOrderQty(jGoods.getId(),
																	orderDetail.getQty());
						Message msg = mUIHandler.obtainMessage(1);
						msg.obj = rtnValue;
						msg.sendToTarget();
					}
				}
			});
			holder.addBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// 先发送空信息显示进度条
					showPDialog();

					orderDetail.setQty((short) (orderDetail.getQty() + 1));
					RtnValueDto rtnValue = MyApplication.getInstance()
														.getSubmitService()
														.updateOrderQty(jGoods.getId(),
																orderDetail.getQty());
					Message msg = mUIHandler.obtainMessage(1);
					msg.obj = rtnValue;
					msg.sendToTarget();
				}
			});
		}
		return convertView;
	}

	// 显示进度条
	private void showPDialog() {
		if (progressBar != null)
			progressBar.setVisibility(View.VISIBLE);
		if (mUIHandler != null) {
			mUIHandler.sendEmptyMessage(2);
		}
	}
}
