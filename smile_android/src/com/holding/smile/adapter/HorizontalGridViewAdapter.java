
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
import com.holding.smile.entity.JGoods;

/**
 * 
 * 类说明：水平列表适配器
 * 
 * @author robin 2014-3-23下午1:45:08
 * 
 */
public class HorizontalGridViewAdapter extends BaseAdapter {

	private LayoutInflater	mInflater;
	private List<JGoods>	jGoodsList;
	private Context			context;
	private ImageLoader		mImageLoader	= MyApplication.getImageLoader();
	private boolean			mBusy			= false;

	public HorizontalGridViewAdapter(Context context, List<JGoods> jGoodsList) {
		this.context = context;
		this.mInflater = LayoutInflater.from(context);
		this.jGoodsList = jGoodsList;
	}

	public void setFlagBusy(boolean busy) {
		this.mBusy = busy;
	}

	@Override
	public int getCount() {
		return jGoodsList.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	private static class ViewHolder {
		private TextView	pn;
		private ImageView	im;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.horizontallistview_item, null);
			holder.im = (ImageView) convertView.findViewById(R.id.iv_pic);
			holder.pn = (TextView) convertView.findViewById(R.id.tv_pn);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.im.setImageResource(R.drawable.empty_photo);

		if (jGoodsList != null && !jGoodsList.isEmpty()) {
			final JGoods jGoods = jGoodsList.get(position);
			if (jGoods != null) {
				holder.pn.setText("￥" + jGoods.getPp());
				if (jGoods.getP() != null && jGoods.getP().length > 0) {
					String url = MyApplication.jgoods_img_url + jGoods.getP()[0];
					holder.im.setTag(url);
					if (!mBusy) {
						mImageLoader.DisplayImage(url, holder.im, false);
					} else {
						mImageLoader.DisplayImage(url, holder.im, false);
					}
				}
			}
			convertView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(context, GoodsDetailActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					intent.putExtra("gid", jGoods.getId());
					((Activity) context).startActivityForResult(intent, BaseActivity.SEARCH_CODE);
				}
			});
		}
		return convertView;
	}
}
