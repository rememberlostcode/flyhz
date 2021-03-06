
package com.holding.smile.adapter;

import java.util.List;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.holding.smile.R;
import com.holding.smile.activity.MainTwoActivity;
import com.holding.smile.activity.MyApplication;
import com.holding.smile.entity.Brand;
import com.holding.smile.tools.Constants;
import com.holding.smile.tools.StrUtils;
import com.holding.smile.tools.ToastUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 
 * 类说明：水平列表适配器
 * 
 * @author robin 2014-3-23下午1:45:08
 * 
 */
public class BrandAdapter extends BaseAdapter {

	private LayoutInflater	mInflater;
	private List<Brand>		brandList;
	private Integer			cid;
	private LayoutParams	para;

	public BrandAdapter(List<Brand> brandList, Integer cid) {
		this.brandList = brandList;
		this.cid = cid;
	}

	// 设置分类ID
	public void setCid(Integer cid) {
		this.cid = cid;
	}

	@Override
	public int getCount() {
		if (brandList != null)
			return brandList.size();
		return 0;
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	private static class ViewHolder {
		private TextView	brand;
		// private TextView moreText;
		// private RelativeLayout relativeLayout;
		private ImageView	p;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final Context context = parent.getContext();
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			mInflater = LayoutInflater.from(context);
			convertView = mInflater.inflate(R.layout.index_item, null);
			holder.brand = (TextView) convertView.findViewById(R.id.list_brand);
			// holder.moreText = (TextView)
			// convertView.findViewById(R.id.list_more);
			// holder.relativeLayout = (RelativeLayout)
			// convertView.findViewById(R.id.brand_head);
			holder.p = (ImageView) convertView.findViewById(R.id.branc_pic);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.p.setImageResource(R.drawable.empty_photo);

		if (para == null) {
			para = holder.p.getLayoutParams();
			para.width = MyApplication.getInstance().getScreenWidth();
			para.height = (int) (para.width / 2.5);
		}
		holder.p.setLayoutParams(para);

		if (brandList != null && !brandList.isEmpty()) {
			final Brand brand = brandList.get(position);
			if (brand != null) {
				holder.brand.setText(brand.getName());
				if (StrUtils.isNotEmpty(brand.getImg_url())) {
					String url = MyApplication.jgoods_img_url + brand.getImg_url();
					holder.p.setTag(url);
					ImageLoader.getInstance().displayImage(url, holder.p,MyApplication.options);
//					if (!mBusy) {
//						mImageLoader.DisplayImage(url, holder.p, false);
//					} else {
//						mImageLoader.DisplayImage(url, holder.p, false);
//					}
				}
			}

			convertView.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					if (brand != null) {
						Intent intent = new Intent(context, MainTwoActivity.class);
						intent.putExtra("bid", brand.getId());
						intent.putExtra("cid", cid);
						intent.putExtra("bn", brand.getName());
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						Activity activity = (Activity) context;
						activity.startActivity(intent);
						activity.overridePendingTransition(0, 0);
					} else {
						ToastUtils.showShort(context, Constants.MESSAGE_EXCEPTION);
						notifyDataSetChanged();// 更新一下显示的数据
					}
				}
			});
		}
		return convertView;
	}
}
