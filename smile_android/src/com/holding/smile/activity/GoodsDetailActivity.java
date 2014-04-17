
package com.holding.smile.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.holding.smile.R;
import com.holding.smile.adapter.ImageAdapter;
import com.holding.smile.adapter.MyPagerAdapter;
import com.holding.smile.dto.RtnValueDto;
import com.holding.smile.entity.JGoods;
import com.holding.smile.tools.Constants;
import com.holding.smile.tools.SetExpandableListViewListViewHeight;
import com.holding.smile.tools.StrUtils;

/**
 * 
 * 类说明：物品详情页
 * 
 * @author robin 2014-2-25下午1:16:56
 * 
 */
public class GoodsDetailActivity extends BaseActivity implements OnClickListener {

	private ImageAdapter	imageAdapter;
	private ListView		listView;
	private ViewPager		mViewPager;
	private List<View>		viewList;
	private MyPagerAdapter	pagerAdapter;
	// 装点点的ImageView数组
	private ImageView[]		tips;
	private List<String>	picList	= new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentLayout(R.layout.goods_detail_view);
		ImageView backBtn = displayHeaderBack();
		backBtn.setOnClickListener(this);

		Intent intent = getIntent();
		Integer gid = (Integer) intent.getExtras().getSerializable("gid");
		if (gid == null) {
			Toast.makeText(context, Constants.MESSAGE_NET, Toast.LENGTH_SHORT).show();
		}
		JGoods jGoods = null;
		RtnValueDto rtnValue = MyApplication.getInstance().getDataService().getGoodsDetail(gid);
		if (rtnValue != null)
			jGoods = rtnValue.getGoodDetail();
		if (jGoods == null) {
			if (rtnValue.getValidate() != null
					&& StrUtils.isNotEmpty(rtnValue.getValidate().getMessage())) {
				Toast.makeText(context, rtnValue.getValidate().getMessage(), Toast.LENGTH_SHORT)
						.show();
			} else {
				Toast.makeText(context, Constants.MESSAGE_NET, Toast.LENGTH_SHORT).show();
			}
		}

		TextView headerDesc = displayHeaderDescription();
		headerDesc.setText(R.string.good_detail);

		TextView b = (TextView) findViewById(R.id.b);
		TextView n = (TextView) findViewById(R.id.n);
		TextView pn = (TextView) findViewById(R.id.pn);
		TextView po = (TextView) findViewById(R.id.po);
		TextView save = (TextView) findViewById(R.id.save);
		TextView d = (TextView) findViewById(R.id.d);
		TextView desc = (TextView) findViewById(R.id.desc);

		mViewPager = (ViewPager) findViewById(R.id.viewpager);

		if (jGoods.getBe() != null && !"".equals(jGoods.getBe().trim())) {
			b.setText(jGoods.getBe().trim());
		}
		if (jGoods.getN() != null && !"".equals(jGoods.getN().trim())) {
			n.setText(jGoods.getN().trim());
		}
		if (jGoods.getPp() != null) {
			pn.setText("￥" + jGoods.getPp());
		}
		if (jGoods.getLp() != null) {
			po.setText("￥" + jGoods.getLp());
			po.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);// 中间横线
		}
		if (jGoods.getSp() != null) {
			save.setText("省￥" + jGoods.getSp());
		}
		if (jGoods.getD() != null && !"".equals(jGoods.getD().trim())) {
			d.setText(jGoods.getD().trim());
		}
		if (jGoods.getD() != null && !"".equals(jGoods.getD().trim())) {
			desc.setText("物品描述：" + jGoods.getD().trim());
		}
		for (int i = 0; i < jGoods.getP().length; i++) {
			picList.add(jGoods.getP()[i]);
		}

		addViewPager();// 添加页卡
		// 实例化适配器
		pagerAdapter = new MyPagerAdapter(viewList);
		mViewPager.setAdapter(pagerAdapter);
		mViewPager.setCurrentItem(0); // 设置默认当前页

		imageAdapter = new ImageAdapter(context, picList);
		listView.setAdapter(imageAdapter);
		SetExpandableListViewListViewHeight.setListViewHeightOnChildren(listView);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.mainfooter_search: {
				Intent intent = new Intent(this, SearchGoodsActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				finish();
				break;
			}
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
		viewList = new ArrayList<View>();
		LayoutInflater inflater = getLayoutInflater();
		// 添加页卡数据
		if (picList != null && !picList.isEmpty()) {
			int size = picList.size();
			for (int i = 0; i < size; i++) {
				View view = inflater.inflate(R.layout.good_pic_item, null);
				ImageView imageView = (ImageView) view.findViewById(R.id.good_pic);
				imageView.setTag(MyApplication.jgoods_img_url + picList.get(i));
				viewList.add(view);
			}

			ViewGroup group = (ViewGroup) findViewById(R.id.viewGroup);
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
					}
				}
			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		picList.clear();
		picList = null;
		if (listView != null) {
			listView.invalidate();
			listView = null;
		}
		if (imageAdapter != null) {
			imageAdapter.notifyDataSetChanged();
			imageAdapter.notifyDataSetInvalidated();
			imageAdapter = null;
		}
		setResult(RESULT_CANCELED, null);
	}
}
