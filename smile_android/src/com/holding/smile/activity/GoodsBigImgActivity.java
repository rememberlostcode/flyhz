
package com.holding.smile.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.holding.smile.R;
import com.holding.smile.adapter.BigImgPagerAdapter;
import com.holding.smile.myview.TouchImageView;

/**
 * 
 * 类说明：物品大图
 * 
 * @author robin 2014-2-25下午1:16:56
 * 
 */
public class GoodsBigImgActivity extends BaseActivity implements OnClickListener {

	private ViewPager			mViewPager;
	private List<View>			viewList	= new ArrayList<View>();
	private BigImgPagerAdapter	pagerAdapter;
	// 装点点的ImageView数组
	private ImageView[]			tips;
	private List<String>		picList		= new ArrayList<String>();
	private int					position;								// 当前页

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		try {
			picList = intent.getExtras().getStringArrayList("picList");
			position = intent.getExtras().getInt("position");
			initView();
		} catch (Exception e) {
			Log.e("参数转换异常：", e.getMessage());
		}

	}

	/**
	 * 初始化View
	 */
	private void initView() {
		if (picList != null && !picList.isEmpty()) {
			setContentLayout(R.layout.big_pic_pager);
			if (mViewPager == null)
				mViewPager = (ViewPager) findViewById(R.id.mypicpager);

			addViewPager();// 添加页卡
			if (pagerAdapter == null) {
				// 实例化适配器
				pagerAdapter = new BigImgPagerAdapter(viewList);
				mViewPager.setAdapter(pagerAdapter);
			} else {
				pagerAdapter.setListViews(viewList);
			}
			mViewPager.setCurrentItem(position); // 设置默认当前页
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_back: {
				finish();
				break;
			}
		}
		super.onClick(v);
	}

	/**
	 * 添加页卡
	 */
	private void addViewPager() {
		viewList.clear();
		// LayoutInflater inflater = getLayoutInflater();
		// 添加页卡数据
		if (picList != null && !picList.isEmpty()) {
			int size = picList.size();
			for (int i = 0; i < size; i++) {
				// View view = inflater.inflate(R.layout.pager_pic_item, null);
				String imagePath = null;
				if(picList.get(i).indexOf("/idcard") > -1){
					imagePath = MyApplication.jgoods_img_url + picList.get(i);
				} else {
					imagePath = picList.get(i);
				}
				TouchImageView imageView = new TouchImageView(this, imagePath);
				// ImageView imageView = (ImageView)
				// view.findViewById(R.id.good_pic);
				imageView.setTag(imagePath);
				viewList.add(imageView);
			}

			ViewGroup group = (ViewGroup) findViewById(R.id.view_group);
			group.removeAllViews();
			// 将点点加入到ViewGroup中
			tips = new ImageView[size];
			for (int i = 0; i < tips.length; i++) {
				ImageView imageView = new ImageView(this);
				imageView.setLayoutParams(new LayoutParams(10, 10));
				tips[i] = imageView;
				if (i == 0) {
					tips[i].setBackgroundResource(R.drawable.page_indicator_focused);
				} else {
					tips[i].setBackgroundResource(R.drawable.page_indicator_unfocused);
				}

				LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
						new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT,
								LayoutParams.WRAP_CONTENT));
				layoutParams.leftMargin = 5;
				layoutParams.rightMargin = 5;
				group.addView(imageView, layoutParams);
			}
		}

		// 设置监听，主要是设置点点的背景
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				setImageBackground(arg0 % picList.size());
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}

			/**
			 * 设置选中的tip的背景
			 * 
			 * @param selectItems
			 */
			private void setImageBackground(int selectItems) {
				for (int i = 0; i < tips.length; i++) {
					if (i == selectItems) {
						tips[i].setBackgroundResource(R.drawable.page_indicator_focused);
					} else {
						tips[i].setBackgroundResource(R.drawable.page_indicator_unfocused);
						TouchImageView imageView = (TouchImageView) viewList.get(i);
						if (imageView != null)
							imageView.initImageView();
					}
				}
			}
		});

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mViewPager != null) {
			mViewPager.invalidate();
			mViewPager = null;
		}
		if (pagerAdapter != null) {
			pagerAdapter.notifyDataSetChanged();
			pagerAdapter = null;
		}
		picList.clear();
		picList = null;
		tips = null;
	}

}
