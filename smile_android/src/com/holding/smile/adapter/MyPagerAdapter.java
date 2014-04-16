
package com.holding.smile.adapter;

import java.util.List;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.holding.smile.R;
import com.holding.smile.activity.MyApplication;
import com.holding.smile.cache.ImageLoader;

/**
 * 
 * 类说明：页卡适配器
 * 
 * @author robin 2014-4-15下午4:05:44
 * 
 */
public class MyPagerAdapter extends PagerAdapter {
	private ImageLoader	mImageLoader	= MyApplication.getImageLoader();
	private List<View>	mListViews;

	public MyPagerAdapter(List<View> mListViews) {
		this.mListViews = mListViews;// 构造方法，参数是我们的页卡，这样比较方便。
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView(mListViews.get(position));// 删除页卡
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) { // 这个方法用来实例化页卡
		View view = null;
		if (mListViews != null && !mListViews.isEmpty()) {
			view = mListViews.get(position);
			ImageView im = (ImageView) view.findViewById(R.id.good_pic);
			if (im != null) {
				String url = im.getTag().toString();
				mImageLoader.DisplayImage(url, im, false);
			}
			container.addView(mListViews.get(position), 0);// 添加页卡
		}
		return view;
	}

	@Override
	public int getCount() {
		return mListViews.size();// 返回页卡的数量
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;// 官方提示这样写
	}

}