
package com.holding.smile.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.holding.smile.R;
import com.holding.smile.dto.BrandJGoods;
import com.holding.smile.myview.MyGridView;

/**
 * 
 * 类说明：水平列表适配器
 * 
 * @author robin 2014-3-23下午1:45:08
 * 
 */
public class VerticalListAdapter extends BaseAdapter {

	// 列宽
	private int					cWidth		= 160;
	// 水平间距
	private int					hSpacing	= 2;
	private LayoutInflater		mInflater;
	private List<BrandJGoods>	brandJGoodsList;

	public VerticalListAdapter(Context context, List<BrandJGoods> brandJGoodsList) {
		mInflater = LayoutInflater.from(context);
		this.brandJGoodsList = brandJGoodsList;
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
			convertView = mInflater.inflate(R.layout.horizontallistview, null);
			holder.brand = (TextView) convertView.findViewById(R.id.list_brand);
			holder.moreText = (TextView) convertView.findViewById(R.id.list_more);
			// holder.hlv = (HorizontalListView)
			// convertView.findViewById(R.id.horizontallistview1);
			holder.relativeLayout = (RelativeLayout) convertView.findViewById(R.id.brand_head);
			holder.gridView = (MyGridView) convertView.findViewById(R.id.gridview);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if (brandJGoodsList != null && !brandJGoodsList.isEmpty()) {
			final BrandJGoods brandJGoods = brandJGoodsList.get(position);
			if (brandJGoods != null) {

				HorizontalGridViewAdapter hlAdapter = new HorizontalGridViewAdapter(context,
						brandJGoods.getGoodData());
				// holder.hlv.setAdapter(hlAdapter);
				int ii = hlAdapter.getCount();
				LayoutParams params = new LayoutParams(ii * cWidth, LayoutParams.WRAP_CONTENT);
				holder.gridView.setLayoutParams(params);
				holder.gridView.setColumnWidth(cWidth);
				holder.gridView.setHorizontalSpacing(hSpacing);
				holder.gridView.setStretchMode(GridView.NO_STRETCH);
				holder.gridView.setNumColumns(ii);
				holder.gridView.setAdapter(hlAdapter);
				hlAdapter.notifyDataSetChanged();

				holder.brand.setText(brandJGoods.getBrandName());
				holder.relativeLayout.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View arg0) {
						Toast.makeText(context, "您点了查看更多……", Toast.LENGTH_LONG);
					}
				});
			}
		}
		return convertView;
	}
}
