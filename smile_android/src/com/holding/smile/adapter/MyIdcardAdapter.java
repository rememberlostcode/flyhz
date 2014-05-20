
package com.holding.smile.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.holding.smile.R;
import com.holding.smile.activity.BaseActivity;
import com.holding.smile.activity.IdcardEditActivity;
import com.holding.smile.entity.Idcard;

public class MyIdcardAdapter extends BaseAdapter {
	private Context			context;
	private List<Idcard>	idcardList;

	// 自己定义的构造函数
	public MyIdcardAdapter(Context context, List<Idcard> idcardList) {
		this.idcardList = idcardList;
		this.context = context;
	}

	@Override
	public int getCount() {
		return idcardList.size();
	}

	@Override
	public Object getItem(int position) {
		return idcardList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	static class ViewHolder {
		TextView	name;
		TextView	idcard;
	}

	@Override
	public View getView(final int position, View convertView, final ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.idcard_list, null);
			holder = new ViewHolder();
			holder.name = (TextView) convertView.findViewById(R.id.idcard_list_name);
			holder.idcard = (TextView) convertView.findViewById(R.id.idcard_list_idcard);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final Idcard idcard = (Idcard) getItem(position);
		holder.name.setText(idcard.getName().trim());

		holder.idcard.setText(idcard.getNumber());

		convertView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, IdcardEditActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.putExtra("idcard", idcard);
				((Activity) parent.getContext()).startActivityForResult(intent,
						BaseActivity.IDCARD_EDIT_CODE);
			}
		});
		return convertView;
	}
}
