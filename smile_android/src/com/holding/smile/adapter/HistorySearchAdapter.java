
package com.holding.smile.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.holding.smile.R;

/**
 * 
 * 类说明：历史搜索适配器
 * 
 * @author robin 2014-2-25下午12:28:21
 * 
 */
public class HistorySearchAdapter extends BaseAdapter {

	private List<String>	historySearchList;

	public HistorySearchAdapter(List<String> historySearchList) {
		this.historySearchList = historySearchList;
	}

	@Override
	public int getCount() {
		if (historySearchList != null)
			return historySearchList.size();
		return 0;
	}

	@Override
	public Object getItem(int position) {
		if (historySearchList != null)
			return historySearchList.get(position);
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final Context context = parent.getContext();
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.history_search_item, null);
		}
		if (historySearchList == null || historySearchList.isEmpty()) {
			//
		} else {
			TextView smartOption = (TextView) convertView.findViewById(R.id.history_search_name);
			smartOption.setText(historySearchList.get(position));
		}
		return convertView;
	}
}
