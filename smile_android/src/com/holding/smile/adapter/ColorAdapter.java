
package com.holding.smile.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.holding.smile.R;
import com.holding.smile.activity.MyApplication;
import com.holding.smile.entity.JColor;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 
 * 类说明:颜色图片选择适配器
 * 
 * @author robin
 * @version 创建时间：2013-12-3 下午6:23:23
 * 
 */
public class ColorAdapter extends BaseAdapter {

	private List<JColor>	colorList;
	private Context			context;

	public ColorAdapter(Context context, List<JColor> colorList) {
		this.context = context;
		this.colorList = colorList;
	}

	public int getCount() {
		return colorList.size();
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	static class ViewHolder {
		TextView	id;
		TextView	color;
		ImageView	p;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.color_item, parent, false);
			holder = new ViewHolder();
			holder.p = (ImageView) convertView.findViewById(R.id.good_color);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.p.setImageResource(R.drawable.empty_photo);

		if (colorList != null && !colorList.isEmpty()) {
			String url = MyApplication.jgoods_img_url + colorList.get(position);
			holder.p.setTag(url);
			ImageLoader.getInstance().displayImage(url, holder.p,MyApplication.options);
		}
		return convertView;
	}

}
