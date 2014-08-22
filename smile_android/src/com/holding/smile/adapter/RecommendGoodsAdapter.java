
package com.holding.smile.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.holding.smile.R;
import com.holding.smile.activity.MyApplication;
import com.holding.smile.entity.JActivity;
import com.holding.smile.tools.StrUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

public class RecommendGoodsAdapter extends BaseAdapter {

	private List<JActivity>	jActivityList;
	private Context			context;
	
	// 自己定义的构造函数
	public RecommendGoodsAdapter(Context context, List<JActivity> contacts) {
		this.context = context;
		this.jActivityList = contacts;
	}

	@Override
	public int getCount() {
		return jActivityList.size();
	}

	@Override
	public Object getItem(int position) {
		return jActivityList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	static class ViewHolder {
		ImageView	p;
	}

	@Override
	public View getView(final int position, View convertView, final ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.recommend_good_item, null);
			holder = new ViewHolder();
			holder.p = (ImageView) convertView.findViewById(R.id.good_pic);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.p.setImageResource(R.drawable.empty_photo);

		final JActivity jActivity = (JActivity) getItem(position);
		if (jActivity != null) {
			if (StrUtils.isNotEmpty(jActivity.getP())) {
				String url = MyApplication.jgoods_img_url + jActivity.getP();
				holder.p.setTag(url);
				ImageLoader.getInstance().displayImage(url, holder.p,MyApplication.options);
			}

			convertView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// Intent intent = new Intent(context,
					// GoodsDetailActivity.class);
					// intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					// intent.putExtra("jActivity", jActivity);
					// ((Activity)
					// parent.getContext()).startActivityForResult(intent,
					// BaseActivity.SEARCH_CODE);
				}
			});
		}
		return convertView;
	}
}
