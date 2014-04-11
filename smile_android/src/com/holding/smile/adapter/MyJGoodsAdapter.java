
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
import com.holding.smile.entity.JGoods;

public class MyJGoodsAdapter extends BaseAdapter {

	private List<JGoods>	jGoodsList;

	// 自己定义的构造函数
	public MyJGoodsAdapter(List<JGoods> contacts) {
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
		TextView	b;
		TextView	n;
		TextView	pn;
		TextView	po;
		TextView	save;
		TextView	d;
		ImageView	p;
	}

	@Override
	public View getView(final int position, View convertView, final ViewGroup parent) {
		final Context context = parent.getContext();
		ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.home_list, null);
			holder = new ViewHolder();
			holder.b = (TextView) convertView.findViewById(R.id.b);
			holder.n = (TextView) convertView.findViewById(R.id.n);
			holder.pn = (TextView) convertView.findViewById(R.id.pn);
			holder.po = (TextView) convertView.findViewById(R.id.po);
			holder.save = (TextView) convertView.findViewById(R.id.save);
			holder.d = (TextView) convertView.findViewById(R.id.d);
			holder.p = (ImageView) convertView.findViewById(R.id.p);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final JGoods jGoods = (JGoods) getItem(position);

		if (jGoods.getBe() != null && !"".equals(jGoods.getBe().trim())) {
			holder.b.setText(jGoods.getBe().trim());
		}
		if (jGoods.getN() != null && !"".equals(jGoods.getN().trim())) {
			holder.n.setText(jGoods.getN().trim());
		}
		if (jGoods.getPp() != null) {
			holder.pn.setText("￥" + jGoods.getPp());
		}
		if (jGoods.getLp() != null) {
			holder.po.setText("￥" + jGoods.getLp());
			holder.po.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);// 中间横线
		}
		if (jGoods.getSp() != null) {
			holder.save.setText("省￥" + jGoods.getSp());
		}
		if (jGoods.getD() != null && !"".equals(jGoods.getD().trim())) {
			holder.d.setText(jGoods.getD().trim());
		}
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
		return convertView;
	}
}
