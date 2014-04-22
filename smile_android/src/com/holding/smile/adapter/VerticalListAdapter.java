
package com.holding.smile.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.holding.smile.R;
import com.holding.smile.activity.BaseActivity;
import com.holding.smile.activity.MainTwoActivity;
import com.holding.smile.activity.MyApplication;
import com.holding.smile.dto.BrandJGoods;
import com.holding.smile.myview.MyGridView;
import com.holding.smile.tools.Constants;

/**
 * 
 * 类说明：水平列表适配器
 * 
 * @author robin 2014-3-23下午1:45:08
 * 
 */
public class VerticalListAdapter extends BaseAdapter {

	private float				density		= MyApplication.getInstance().getDensity();
	// 列宽
	private int					cWidth		= (int) (80 * density);
	// 水平间距
	private int					hSpacing	= (int) (10 * density);
	private LayoutInflater		mInflater;
	private List<BrandJGoods>	brandJGoodsList;
	private Integer				cid;

	public VerticalListAdapter(List<BrandJGoods> brandJGoodsList, Integer cid) {
		this.brandJGoodsList = brandJGoodsList;
		this.cid = cid;
	}

	// 设置分类ID
	public void setCid(Integer cid) {
		this.cid = cid;
	}

	@Override
	public int getCount() {
		return brandJGoodsList.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	private static class ViewHolder {
		private TextView		brand;
		private TextView		moreText;
		private RelativeLayout	relativeLayout;
		private MyGridView		gridView;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final Context context = parent.getContext();
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			mInflater = LayoutInflater.from(context);
			convertView = mInflater.inflate(R.layout.horizontallistview, null);
			holder.brand = (TextView) convertView.findViewById(R.id.list_brand);
			holder.moreText = (TextView) convertView.findViewById(R.id.list_more);
			holder.relativeLayout = (RelativeLayout) convertView.findViewById(R.id.brand_head);
			holder.gridView = (MyGridView) convertView.findViewById(R.id.gridview);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if (brandJGoodsList != null && !brandJGoodsList.isEmpty()) {
			final BrandJGoods brandJGoods = brandJGoodsList.get(position);
			if (brandJGoods != null) {

				final HorizontalGridViewAdapter hlAdapter = new HorizontalGridViewAdapter(context,
						brandJGoods.getGs());
				int ii = hlAdapter.getCount();
				LayoutParams params = new LayoutParams(ii * cWidth, LayoutParams.WRAP_CONTENT);
				holder.gridView.setLayoutParams(params);
				holder.gridView.setColumnWidth(cWidth);
				holder.gridView.setHorizontalSpacing(hSpacing);
				holder.gridView.setStretchMode(GridView.NO_STRETCH);
				holder.gridView.setNumColumns(ii);
				holder.gridView.setAdapter(hlAdapter);
				holder.gridView.setOnScrollListener(new OnScrollListener() {

					@Override
					public void onScrollStateChanged(AbsListView view, int scrollState) {
						switch (scrollState) {
							case OnScrollListener.SCROLL_STATE_FLING:
								hlAdapter.setFlagBusy(true);
								break;
							case OnScrollListener.SCROLL_STATE_IDLE:
								hlAdapter.setFlagBusy(false);
								break;
							case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
								hlAdapter.setFlagBusy(false);
								break;
							default:
								break;
						}
						hlAdapter.notifyDataSetChanged();
					}

					@Override
					public void onScroll(AbsListView view, int firstVisibleItem,
							int visibleItemCount, int totalItemCount) {

					}
				});
				hlAdapter.notifyDataSetChanged();

				holder.brand.setText(brandJGoods.getN());
				holder.relativeLayout.setOnClickListener(new View.OnClickListener() {

					@SuppressWarnings("unused")
					@Override
					public void onClick(View arg0) {
						if (brandJGoods != null) {
							Intent intent = new Intent(context, MainTwoActivity.class);
							intent.putExtra("bid", brandJGoods.getId());
							intent.putExtra("bn", brandJGoods.getN());
							intent.putExtra("cid", cid);
							intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							Activity activity = (Activity) context;
							activity.startActivityForResult(intent, BaseActivity.MORE_CODE);
							activity.overridePendingTransition(0, 0);
						} else {
							Toast.makeText(context, Constants.MESSAGE_NET, Toast.LENGTH_SHORT)
									.show();
							hlAdapter.notifyDataSetChanged();// 更新一下显示的数据
						}
					}
				});
			}
		}
		return convertView;
	}
}
