
package com.holding.smile.adapter;

import java.util.List;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

import com.holding.smile.R;
import com.holding.smile.activity.MyApplication;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 
 * 类说明：页卡适配器
 * 
 * @author robin 2014-4-15下午4:05:44
 * 
 */
public class MyPagerAdapter extends PagerAdapter {
	private List<View>		mListViews;
	private LayoutParams	para;
	private boolean			imgFlag			= false;							// true为按长宽比1.9:1显示图片

	public MyPagerAdapter(List<View> mListViews, boolean imgFlag) {
		this.mListViews = mListViews;// 构造方法，参数是我们的页卡，这样比较方便。
		this.imgFlag = imgFlag;
	}

	public void setListViews(List<View> mListViews) {
		this.mListViews = mListViews;
		notifyDataSetChanged();
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		if (getCount() > 0) {
			container.removeView(mListViews.get(position));// 删除页卡
		}
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) { // 这个方法用来实例化页卡
		View view = null;
		if (mListViews != null && !mListViews.isEmpty()) {
			view = mListViews.get(position);
			String key = "imRecord" + position;
			view.setTag(key);
			ImageView im = (ImageView) view.findViewById(R.id.good_pic);
			if (im != null) {
				if (para == null) {
					para = im.getLayoutParams();
					para.width = MyApplication.getInstance().getScreenWidth();
					if (imgFlag) {
						para.height = (int) (para.width / 1.9);
					} else {
						para.height = para.width;
					}
				}
				im.setLayoutParams(para);
				im.setImageResource(R.drawable.empty_photo);
				String url = im.getTag().toString();
				ImageLoader.getInstance().displayImage(url, im,MyApplication.options);
			}
			container.addView(view, 0);// 添加页卡
		}
		return view;
	}

	/**
	 * 处理页卡Item
	 * 
	 * @param view
	 */
	public void disposeItem(View view, String imgPath) {
		ImageView im = (ImageView) view.findViewById(R.id.good_pic);
		if (im != null) {
			im.setTag(imgPath);
			im.setImageResource(R.drawable.empty_photo);
			String url = im.getTag().toString();
			ImageLoader.getInstance().displayImage(url, im,MyApplication.options);
		}
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

	@Override
	public void startUpdate(ViewGroup container) {
		// TODO Auto-generated method stub
		super.startUpdate(container);
	}

}
