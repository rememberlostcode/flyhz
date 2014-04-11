
package com.holding.smile.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.holding.smile.R;
import com.holding.smile.adapter.ImageAdapter;
import com.holding.smile.entity.JGoods;
import com.holding.smile.tools.SetExpandableListViewListViewHeight;

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
	private List<String>	picList	= new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentLayout(R.layout.goods_detail_view);
		ImageView backBtn = displayHeaderBack();
		backBtn.setOnClickListener(this);

		// ImageView searchBtn = displayHeaderSearch();
		// searchBtn.setOnClickListener(this);

		Intent intent = getIntent();
		JGoods jGoods = (JGoods) intent.getExtras().getSerializable("jGoods");
		if (jGoods == null) {
			finish();
		}

		TextView headerDesc = displayHeaderDescription();
		headerDesc.setText(jGoods.getN());

		TextView b = (TextView) findViewById(R.id.b);
		TextView n = (TextView) findViewById(R.id.n);
		TextView pn = (TextView) findViewById(R.id.pn);
		TextView po = (TextView) findViewById(R.id.po);
		TextView save = (TextView) findViewById(R.id.save);
		TextView d = (TextView) findViewById(R.id.d);
		TextView desc = (TextView) findViewById(R.id.desc);
		listView = (ListView) findViewById(R.id.goods_pic_list);

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

		imageAdapter = new ImageAdapter(context, picList);
		listView.setAdapter(imageAdapter);
		// MyApplication.getInstance().setmImgList(listView);
		SetExpandableListViewListViewHeight.setListViewHeightOnChildren(listView);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_search: {
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
		// new RecycleBitmapUtil(true).recycle(listView);
		setResult(RESULT_CANCELED, null);
	}
}
