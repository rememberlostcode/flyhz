
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

	public HorizontalGridViewAdapter(Context context, List<JGoods> jGoodsList) {
		this.context = context;
		this.mInflater = LayoutInflater.from(context);
		this.jGoodsList = jGoodsList;
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
		private TextView	n;
		private TextView	pn;
		private TextView	po;
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
			holder.n = (TextView) convertView.findViewById(R.id.tv_name);
			holder.pn = (TextView) convertView.findViewById(R.id.tv_pn);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.im.setImageResource(R.drawable.empty_photo);

		if (jGoodsList != null && !jGoodsList.isEmpty()) {
			final JGoods jGoods = jGoodsList.get(position);
			if (jGoods != null) {
				holder.n.setText(jGoods.getN());
				holder.pn.setText(jGoods.getPn());
				if (jGoods.getP() != null && jGoods.getP().length > 0) {
					holder.im.setTag(jGoods.getP()[0]);
				}
			}
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
