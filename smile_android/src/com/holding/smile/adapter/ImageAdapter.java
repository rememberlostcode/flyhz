
package com.holding.smile.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.holding.smile.R;
import com.holding.smile.activity.MyApplication;
import com.holding.smile.cache.ImageLoader;

/**
 * 
 * 类说明:
 * 
 * @author robin
 * @version 创建时间：2013-12-3 下午6:23:23
 * 
 */
public class ImageAdapter extends BaseAdapter {

	private int				maxWidth		= MyApplication.getInstance().getScreenWidth();
	private int				maxHeight		= MyApplication.getInstance().getScreenHeight();
	private ImageLoader		mImageLoader	= MyApplication.getImageLoader();
	private List<String>	picList;
	private Context			context;

	public ImageAdapter(Context context, List<String> picList) {
		this.context = context;
		this.picList = picList;
	}

	public int getCount() {
		return picList.size();
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	static class ViewHolder {
		ImageView	p;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.good_pic_item, parent,
					false);
			holder = new ViewHolder();
			holder.p = (ImageView) convertView.findViewById(R.id.good_pic);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.p.setImageResource(R.drawable.empty_photo);

		if (picList != null && !picList.isEmpty()) {
			String url = MyApplication.jgoods_img_url + picList.get(position);
			holder.p.setTag(url);
			mImageLoader.DisplayImage(url, holder.p, false);
		}
		return convertView;
	}

}
