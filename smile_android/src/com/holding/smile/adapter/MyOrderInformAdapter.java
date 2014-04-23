
package com.holding.smile.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.holding.smile.R;
import com.holding.smile.activity.BaseActivity;
import com.holding.smile.activity.GoodsDetailActivity;
import com.holding.smile.activity.MyApplication;
import com.holding.smile.cache.ImageLoader;
import com.holding.smile.dto.OrderDetailDto;
import com.holding.smile.dto.ProductDto;
import com.holding.smile.dto.RtnValueDto;
import com.holding.smile.tools.StrUtils;

public class MyOrderInformAdapter extends BaseAdapter {
	private Context					context;
	private TextView				total;
	private List<OrderDetailDto>	orderDetails;
	private ImageLoader				mImageLoader	= MyApplication.getImageLoader();
	private boolean					mBusy			= false;
	private Integer					sWidth			= MyApplication.getInstance().getScreenWidth();
	private Handler					mUIHandler;

	public void setFlagBusy(boolean busy) {
		this.mBusy = busy;
	}

	// 自己定义的构造函数
	public MyOrderInformAdapter(Context context, List<OrderDetailDto> contacts, TextView total,
			Handler mUIHandler) {
		this.orderDetails = contacts;
		this.context = context;
		this.total = total;
		this.mUIHandler = mUIHandler;
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
		ImageView	p;
		TextView	color;
		TextView	brandstyle;
		TextView	buyQtyText;
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
			holder.pp = (TextView) convertView.findViewById(R.id.pp);
			holder.p = (ImageView) convertView.findViewById(R.id.p);
			holder.brandstyle = (TextView) convertView.findViewById(R.id.brand_style);
			holder.color = (TextView) convertView.findViewById(R.id.color_cate);
			holder.buyQtyText = (TextView) convertView.findViewById(R.id.buyqtytext);
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
			holder.qty.setText(orderDetail.getQty() + "");
			total.setText("共计" + orderDetail.getQty() + "件商品，￥" + orderDetail.getTotal() + "元");
			if (jGoods.getImgs() != null && jGoods.getImgs().length > 0) {
				String url = MyApplication.jgoods_img_url + jGoods.getImgs()[0];
				holder.p.setTag(url);
				if (!mBusy) {
					mImageLoader.DisplayImage(url, holder.p, false);
				} else {
					mImageLoader.DisplayImage(url, holder.p, false);
				}
			}

			holder.p.setOnClickListener(new OnClickListener() {
				@SuppressWarnings("unused")
				@Override
				public void onClick(View v) {
					if (jGoods != null) {
						Intent intent = new Intent(context, GoodsDetailActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						intent.putExtra("gid", jGoods.getId());
						((Activity) parent.getContext()).startActivityForResult(intent,
								BaseActivity.SEARCH_CODE);
					} else {
						notifyDataSetChanged();
					}
				}
			});
			holder.subBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					if (orderDetail.getQty() > 1) {
						orderDetail.setQty((short) (orderDetail.getQty() - 1));
						RtnValueDto rtnValue = MyApplication.getInstance()
															.getDataService()
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
					orderDetail.setQty((short) (orderDetail.getQty() + 1));
					RtnValueDto rtnValue = MyApplication.getInstance()
														.getDataService()
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
}
