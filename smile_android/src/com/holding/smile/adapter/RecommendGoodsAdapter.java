
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
import com.holding.smile.entity.JGoods;

public class RecommendGoodsAdapter extends BaseAdapter {

	private List<JGoods>	jGoodsList;

	// 自己定义的构造函数
	public RecommendGoodsAdapter(List<JGoods> contacts) {
		this.jGoodsList = contacts;
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
		ImageView	p;
	}

	@Override
	public View getView(final int position, View convertView, final ViewGroup parent) {
		final Context context = parent.getContext();
		ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.recommend_good_item, null);
			holder = new ViewHolder();
			holder.n = (TextView) convertView.findViewById(R.id.good_n);
			holder.p = (ImageView) convertView.findViewById(R.id.good_pic);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final JGoods jGoods = (JGoods) getItem(position);
		if (jGoods != null) {
			holder.n.setText(jGoods.getN());
			if (jGoods.getP() != null && jGoods.getP().length > 0) {
				holder.p.setTag(jGoods.getP()[0]);
			}
			holder.p.setImageResource(R.drawable.empty_photo);

			convertView.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Intent intent = new Intent(context, GoodsDetailActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					intent.putExtra("jGoods", jGoods);
					((Activity) context).startActivityForResult(intent, BaseActivity.SEARCH_CODE);
				}
			});
		}
		return convertView;
	}
}
