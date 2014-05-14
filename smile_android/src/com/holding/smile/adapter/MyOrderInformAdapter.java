
package com.holding.smile.adapter;

import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
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
import com.holding.smile.activity.GoodsDetailActivity;
import com.holding.smile.activity.MyApplication;
import com.holding.smile.cache.ImageLoader;
import com.holding.smile.dto.OrderDetailDto;
import com.holding.smile.dto.ProductDto;
import com.holding.smile.dto.RtnValueDto;
import com.holding.smile.tools.StrUtils;

public class MyOrderInformAdapter extends BaseAdapter {
	private Context					context;
	private List<OrderDetailDto>	orderDetails;
	private ImageLoader				mImageLoader	= MyApplication.getImageLoader();
	private boolean					mBusy			= false;
	private Integer					sWidth			= MyApplication.getInstance().getScreenWidth();
	private ProgressDialog			pDialog;
	private Handler					mUIHandler;
	private boolean					cartFlag		= false;										// 为true时，说明是从购物车结算的，这时不能修改购买数量

	public void setFlagBusy(boolean busy) {
		this.mBusy = busy;
	}

	// 自己定义的构造函数
	public MyOrderInformAdapter(Context context, List<OrderDetailDto> contacts,
			ProgressDialog pDialog, Handler mUIHandler, boolean cartFlag) {
		this.orderDetails = contacts;
		this.context = context;
		this.pDialog = pDialog;
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
		TextView	spq;
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
			holder.spq = (TextView) convertView.findViewById(R.id.show_product_qty);
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
				holder.pp.setText("￥" + jGoods.getPurchasingPrice());
			}
			holder.qty.setText(orderDetail.getQty() + "");
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
						intent.putExtra("bs", jGoods.getBrandstyle());
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
		if (pDialog != null) {
			pDialog.show();
		}
		if (mUIHandler != null) {
			mUIHandler.sendEmptyMessage(2);
		}
	}
}
