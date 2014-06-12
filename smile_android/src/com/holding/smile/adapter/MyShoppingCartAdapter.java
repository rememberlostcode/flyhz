
package com.holding.smile.adapter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import com.holding.smile.activity.BaseActivity;
import com.holding.smile.activity.GoodsDetailActivity;
import com.holding.smile.activity.MyApplication;
import com.holding.smile.cache.ImageLoader;
import com.holding.smile.dto.ProductDto;
import com.holding.smile.dto.RtnValueDto;
import com.holding.smile.entity.CartItem;
import com.holding.smile.tools.StrUtils;

public class MyShoppingCartAdapter extends BaseAdapter {
	private Context			context;
	private List<CartItem>	cartItemList;
	private ImageLoader		mImageLoader	= MyApplication.getImageLoader();
	private boolean			mBusy			= false;
	private Integer			sWidth			= MyApplication.getInstance().getScreenWidth();
	private ProgressDialog	pDialog;
	private Handler			mUIHandler;

	private Set<Integer>	sIds			= new HashSet<Integer>();
	private Boolean			editFlag		= false;
	private Boolean			selectAll		= false;

	public void setFlagBusy(boolean busy) {
		this.mBusy = busy;
	}

	public void setEditFlag(boolean editFlag) {
		this.editFlag = editFlag;
		notifyDataSetChanged();
	}

	public void setSelectAll(Boolean selectAll) {
		this.selectAll = selectAll;
		if (selectAll) {
			if (cartItemList != null && !cartItemList.isEmpty()) {
				for (CartItem item : cartItemList) {
					if (item.getId() != null)
						sIds.add(item.getId());
				}
			}
		} else {
			sIds.clear();
		}
		mUIHandler.sendEmptyMessage(3);
	}

	public Boolean getSelectAll() {
		return selectAll;
	}

	// 自己定义的构造函数
	public MyShoppingCartAdapter(Context context, List<CartItem> contacts, ProgressDialog pDialog,
			Handler mUIHandler) {
		this.cartItemList = contacts;
		this.context = context;
		this.pDialog = pDialog;
		this.mUIHandler = mUIHandler;
	}

	public void setData(List<CartItem> contacts) {
		this.cartItemList = contacts;
		notifyDataSetChanged();
	}

	public void removeItem(CartItem item) {
		cartItemList.remove(item);
		notifyDataSetChanged();
	}

	/**
	 * 获取已选中的itemId
	 * 
	 * @return
	 */
	public Set<Integer> getSelectIds() {
		return sIds;
	}

	@Override
	public int getCount() {
		return cartItemList.size();
	}

	@Override
	public Object getItem(int position) {
		return cartItemList.get(position);
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
		TextView	qty;
		ImageView	subBtn;
		ImageView	addBtn;
		ImageView	checkBox;
		ImageView	delBtn;
	}

	@Override
	public View getView(final int position, View convertView, final ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.shopping_cart_item, null);
			holder = new ViewHolder();
			holder.n = (TextView) convertView.findViewById(R.id.n);
			holder.n.setWidth((int) (sWidth - 190 * MyApplication.getInstance().getDensity()));
			holder.pp = (TextView) convertView.findViewById(R.id.pp);
			holder.p = (ImageView) convertView.findViewById(R.id.p);
			holder.brandstyle = (TextView) convertView.findViewById(R.id.brand_style);
			holder.color = (TextView) convertView.findViewById(R.id.color_cate);
			holder.qty = (TextView) convertView.findViewById(R.id.qty);
			holder.subBtn = (ImageView) convertView.findViewById(R.id.sub_qty);
			holder.addBtn = (ImageView) convertView.findViewById(R.id.add_qty);
			holder.checkBox = (ImageView) convertView.findViewById(R.id.check_box);
			holder.delBtn = (ImageView) convertView.findViewById(R.id.del_btn);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.p.setImageResource(R.drawable.empty_photo);

		if (editFlag) {
			holder.delBtn.setVisibility(View.VISIBLE);
		} else {
			holder.delBtn.setVisibility(View.GONE);
		}

		final CartItem cartItem = (CartItem) getItem(position);
		if (cartItem != null && cartItem.getProduct() != null) {

			if (selectAll) {
				holder.checkBox.setSelected(true);
			} else {
				holder.checkBox.setSelected(false);
			}
			if (sIds.contains(cartItem.getId())) {
				holder.checkBox.setSelected(true);
			} else {
				holder.checkBox.setSelected(false);
			}

			final ProductDto jGoods = cartItem.getProduct();
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
				holder.pp.setText("￥" + cartItem.getTotal());
			}
			holder.qty.setText(cartItem.getQty() + "");
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
						((Activity) parent.getContext()).startActivityForResult(intent,
								BaseActivity.SEARCH_CODE);
					} else {
						notifyDataSetChanged();
					}
				}
			});
			holder.n.setOnClickListener(new OnClickListener() {
				@SuppressWarnings("unused")
				@Override
				public void onClick(View v) {
					if (jGoods != null) {
						Intent intent = new Intent(context, GoodsDetailActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						intent.putExtra("bs", jGoods.getBrandstyle());
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
					if (cartItem.getQty() > 1) {

						// 先发送空信息显示进度条
						showPDialog();

						cartItem.setQty((short) (cartItem.getQty() - 1));
						RtnValueDto rtnValue = MyApplication.getInstance()
															.getSubmitService()
															.updateCartQty(cartItem.getId(),
																	cartItem.getQty());
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

					cartItem.setQty((short) (cartItem.getQty() + 1));
					RtnValueDto rtnValue = MyApplication.getInstance()
														.getSubmitService()
														.updateCartQty(cartItem.getId(),
																cartItem.getQty());
					Message msg = mUIHandler.obtainMessage(1);
					msg.obj = rtnValue;
					msg.sendToTarget();
				}
			});

			holder.checkBox.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (!sIds.contains(cartItem.getId())) {
						sIds.add(cartItem.getId());
						v.setSelected(true);
					} else {
						sIds.remove(cartItem.getId());
						v.setSelected(false);
					}
					if (sIds.isEmpty()) {
						selectAll = false;
					}
					mUIHandler.sendEmptyMessage(3);
				}
			});

			holder.delBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					Message msg = mUIHandler.obtainMessage(4);
					msg.obj = cartItem.getId();
					msg.sendToTarget();
				}
			});
		}
		return convertView;
	}

	// 显示进度条
	private void showPDialog() {
		pDialog.show();
		mUIHandler.sendEmptyMessage(2);
	}

}
