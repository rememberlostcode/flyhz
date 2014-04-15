
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

public class MyJGoodsAdapter extends BaseAdapter {
	private Context			context;
	private List<JGoods>	jGoodsList;
	private ImageLoader		mImageLoader	= MyApplication.getImageLoader();
	private boolean			mBusy			= false;

	public void setFlagBusy(boolean busy) {
		this.mBusy = busy;
	}

	// 自己定义的构造函数
	public MyJGoodsAdapter(Context context, List<JGoods> contacts) {
		this.jGoodsList = contacts;
		this.context = context;
	}

	@Override
	public int getCount() {
		return jGoodsList.size();
	}

	@Override
	public Object getItem(int position) {
		return jGoodsList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	static class ViewHolder {
		TextView	n;
		TextView	pp;
		ImageView	p;
		TextView	sn;
	}

	@Override
	public View getView(final int position, View convertView, final ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.home_list, null);
			holder = new ViewHolder();
			holder.n = (TextView) convertView.findViewById(R.id.n);
			// holder.n.setWidth((int) (sWidth - 190 *
			// MyApplication.getInstance().getDensity()));
			holder.pp = (TextView) convertView.findViewById(R.id.pp);
			holder.p = (ImageView) convertView.findViewById(R.id.p);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.p.setImageResource(R.drawable.empty_photo);

		final JGoods jGoods = (JGoods) getItem(position);
		if (jGoods.getN() != null && !"".equals(jGoods.getN().trim())) {
			holder.n.setText(jGoods.getN().trim());
		}
		if (jGoods.getPp() != null) {
			holder.pp.setText("￥" + jGoods.getPp());
		}
		if (jGoods.getP() != null && jGoods.getP().length > 0) {
			String url = MyApplication.jgoods_img_url + jGoods.getP()[0];
			holder.p.setTag(url);
			if (!mBusy) {
				mImageLoader.DisplayImage(url, holder.p, false);
			} else {
				mImageLoader.DisplayImage(url, holder.p, false);
			}
		}

		convertView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, GoodsDetailActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.putExtra("gid", jGoods.getId());
				((Activity) parent.getContext()).startActivityForResult(intent,
						BaseActivity.SEARCH_CODE);
			}
		});
		return convertView;
	}
}
