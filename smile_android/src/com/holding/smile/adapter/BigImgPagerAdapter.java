
package com.holding.smile.adapter;

import java.util.List;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.holding.smile.activity.MyApplication;
import com.holding.smile.cache.ImageLoader;

/**
 * 
 * 类说明：页卡适配器
 * 
 * @author robin 2014-06-26下午4:05:44
 * 
 */
public class BigImgPagerAdapter extends PagerAdapter {
	private ImageLoader		mImageLoader	= MyApplication.getImageLoader();
	private List<View>		mListViews;
	private ImageView		currentImgView;
	private int			width			= MyApplication.getInstance().getScreenWidth();

	public BigImgPagerAdapter(List<View> mListViews) {
		this.mListViews = mListViews;// 构造方法，参数是我们的页卡，这样比较方便。
	}

	public void setListViews(List<View> mListViews) {
		this.mListViews = mListViews;
		notifyDataSetChanged();
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
			// ImageView im = (ImageView) view;
			// im.setImageResource(R.drawable.empty_photo);
			// String url = im.getTag().toString();
			// mImageLoader.DisplayImage(url, im, false);
			container.addView(view, 0);// 添加页卡
		}
		return view;
	}

	@Override
	public int getCount() {
		if (mListViews != null)
			return mListViews.size();// 返回页卡的数量
		return 0;
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;// 官方提示这样写
	}

}
