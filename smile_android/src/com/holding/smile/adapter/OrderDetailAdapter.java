
package com.holding.smile.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.holding.smile.R;
import com.holding.smile.activity.MyApplication;
import com.holding.smile.cache.ImageLoader;
import com.holding.smile.dto.OrderDetailDto;
import com.holding.smile.dto.ProductDto;

public class OrderDetailAdapter extends BaseAdapter {
	private Context					context;
	private List<OrderDetailDto>	list;
	private ImageLoader				mImageLoader	= MyApplication.getImageLoader();
	private boolean					mBusy			= false;

	public void setFlagBusy(boolean busy) {
		this.mBusy = busy;
	}

	// 自己定义的构造函数
	public OrderDetailAdapter(Context context, List<OrderDetailDto> contacts) {
		this.list = contacts;
		this.context = context;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
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
		TextView	number;
	}

	@Override
	public View getView(final int position, View convertView, final ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.order_detail_list, null);
			holder = new ViewHolder();
			holder.n = (TextView) convertView.findViewById(R.id.detail_n);
			holder.pp = (TextView) convertView.findViewById(R.id.detail_pp);
			holder.p = (ImageView) convertView.findViewById(R.id.detail_p);
			holder.color = (TextView) convertView.findViewById(R.id.detail_color);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.p.setImageResource(R.drawable.empty_photo);

		final OrderDetailDto orderDetailDto = (OrderDetailDto) getItem(position);
		ProductDto product = orderDetailDto.getProduct();
		if (product.getName() != null && !"".equals(product.getName().trim())) {
			holder.n.setText(product.getName().trim());
		}
		if (product.getPurchasingPrice() != null) {
			holder.pp.setText("￥" + product.getPurchasingPrice());
		}
		holder.color.setText(product.getColor());
		if (product.getImgs() != null && product.getImgs().length > 0) {
			String url = MyApplication.jgoods_img_url + product.getImgs()[0];
			holder.p.setTag(url);
			if (!mBusy) {
				mImageLoader.DisplayImage(url, holder.p, false);
			} else {
				mImageLoader.DisplayImage(url, holder.p, false);
			}
		}
		return convertView;
	}
}
