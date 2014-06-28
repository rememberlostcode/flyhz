
package com.holding.smile.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
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
		if (jGoodsList != null)
			return jGoodsList.size();
		return 0;
	}

	@Override
	public Object getItem(int position) {
		if (jGoodsList != null && !jGoodsList.isEmpty())
			return jGoodsList.get(position);
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	static class ViewHolder {
		TextView	n;
		TextView	lp;
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
			holder.lp = (TextView) convertView.findViewById(R.id.lp);
			holder.pp = (TextView) convertView.findViewById(R.id.pp);
			holder.p = (ImageView) convertView.findViewById(R.id.p);
			holder.sn = (TextView) convertView.findViewById(R.id.sn);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.p.setImageResource(R.drawable.empty_photo);

		final JGoods jGoods = (JGoods) getItem(position);
		if (jGoods != null) {
			if (jGoods.getN() != null && !"".equals(jGoods.getN().trim())) {
				holder.n.setText(jGoods.getN().trim());
			}
			if (jGoods.getPp() != null) {
				holder.pp.setText("￥" + jGoods.getPp());
			}
			if (jGoods.getLp() != null) {
				holder.lp.setText("￥" + jGoods.getLp());
				holder.lp.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
			}
			if (jGoods.getSn() != null) {
				holder.sn.setText("已售 " + jGoods.getSn() + " 件");
				// holder.sn.setVisibility(ViewGroup.VISIBLE);
			} else {
				holder.sn.setText("已售 0 件");
				// holder.sn.setVisibility(ViewGroup.GONE);
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
		}

		convertView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (jGoods != null) {
					Intent intent = new Intent(context, GoodsDetailActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					intent.putExtra("gid", jGoods.getId());
					intent.putExtra("bs", jGoods.getBs());
					((Activity) parent.getContext()).startActivityForResult(intent,
							BaseActivity.SEARCH_CODE);
				} else {
					notifyDataSetChanged();// 从新更新一下
				}
			}
		});

		return convertView;
	}
}
