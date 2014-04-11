
package com.holding.smile.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.holding.smile.R;
import com.holding.smile.entity.Category;

/**
 * 
 * 类说明:
 * 
 * @author robin
 * @version 创建时间：2013-12-3 下午6:23:23
 * 
 */
public class CategoryAdapter extends BaseAdapter {

	private List<Category>	cateList;
	private Context			context;

	public CategoryAdapter(Context context, List<Category> cateList) {
		this.context = context;
		this.cateList = cateList;
	}

	public int getCount() {
		return cateList.size();
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	static class ViewHolder {
		TextView	name;
		TextView	gt;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.cate_item, parent, false);
			holder = new ViewHolder();
			holder.name = (TextView) convertView.findViewById(R.id.cate_id);
			holder.gt = (TextView) convertView.findViewById(R.id.gt_id);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (cateList != null && !cateList.isEmpty()) {
			Category cate = cateList.get(position);
			if (cate != null)
				holder.name.setText(cate.getName());
		}
		return convertView;
	}

}
