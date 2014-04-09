
package com.holding.smile.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.holding.smile.R;
import com.holding.smile.activity.MyApplication;
import com.holding.smile.tools.BitmapUtils;
import com.holding.smile.tools.ImgUtil;

/**
 * 
 * 类说明:
 * 
 * @author robin
 * @version 创建时间：2013-12-3 下午6:23:23
 * 
 */
public class ImageAdapter extends BaseAdapter {

	private int				maxWidth	= MyApplication.getInstance().getScreenWidth();
	private int				maxHeight	= MyApplication.getInstance().getScreenHeight();
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

	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.good_pic_item, parent,
					false);
		}
		if (picList != null && !picList.isEmpty()) {
			ImageView imageView = (ImageView) convertView.findViewById(R.id.good_pic);
			// Bitmap bm = BitmapUtils.decodeFile(picList.get(position),
			// MyApplication.getInstance().getScreenWidth(), 0);
			// if (bm != null) {
			// imageView.setImageBitmap(bm);
			// } else {
			// imageView.setImageResource(R.drawable.empty_photo);
			// }
			Bitmap bitmap = MyApplication.getInstance().getBitmapFromMemoryCache(
					picList.get(position));
			if (bitmap != null) {
				// Bitmap 縮放
				bitmap = BitmapUtils.resizeBitmap(bitmap, maxWidth, maxHeight);
				imageView.setImageBitmap(bitmap);
			} else {
				// 在后台开始下载图片
				bitmap = ImgUtil.getInstance().getBitmap(picList.get(position));
				if (bitmap != null) {
					// 图片下载完成后缓存到LrcCache中
					MyApplication.getInstance().addBitmapToMemoryCache(picList.get(position),
							bitmap);
					// Bitmap 縮放
					bitmap = BitmapUtils.resizeBitmap(bitmap, maxWidth, maxHeight);
					imageView.setImageBitmap(bitmap);
				} else {
					imageView.setImageResource(R.drawable.empty_photo);
				}
			}
			// imageView.setTag(picList.get(position));
			convertView.setTag(imageView);
		}
		return convertView;
	}

}
