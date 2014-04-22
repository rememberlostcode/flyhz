
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
import com.holding.smile.activity.BaseActivity;
import com.holding.smile.activity.GoodsDetailActivity;
import com.holding.smile.activity.MyApplication;
import com.holding.smile.cache.ImageLoader;
import com.holding.smile.dto.OrderDetailDto;
import com.holding.smile.dto.ProductDto;

public class MyOrderInformAdapter extends BaseAdapter {
	private Context					context;
	private List<OrderDetailDto>	orderDetails;
	private ImageLoader				mImageLoader	= MyApplication.getImageLoader();
	private boolean					mBusy			= false;

	public void setFlagBusy(boolean busy) {
		this.mBusy = busy;
	}

	// 自己定义的构造函数
	public MyOrderInformAdapter(Context context, List<OrderDetailDto> contacts) {
		this.orderDetails = contacts;
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
		TextView	b;
		TextView	n;
		TextView	pp;
		ImageView	p;
		TextView	qty;
	}

	@Override
	public View getView(final int position, View convertView, final ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.order_item, null);
			holder = new ViewHolder();
			holder.n = (TextView) convertView.findViewById(R.id.n);
			// holder.n.setWidth((int) (sWidth - 190 *
			// MyApplication.getInstance().getDensity()));
			holder.pp = (TextView) convertView.findViewById(R.id.pp);
			holder.p = (ImageView) convertView.findViewById(R.id.p);
			holder.qty = (TextView) convertView.findViewById(R.id.qty);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.p.setImageResource(R.drawable.empty_photo);
		final OrderDetailDto orderDetail = (OrderDetailDto) getItem(position);
		if (orderDetail != null) {
			final ProductDto jGoods = orderDetail.getProduct();
			if (jGoods.getName() != null && !"".equals(jGoods.getName().trim())) {
				holder.n.setText(jGoods.getName().trim());
			}
			if (orderDetail.getTotal() != null) {
				holder.pp.setText("￥" + orderDetail.getTotal());
			}
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
		}
		return convertView;
	}
}
