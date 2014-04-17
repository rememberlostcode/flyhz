
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
import com.holding.smile.activity.AddressEditActivity;
import com.holding.smile.activity.BaseActivity;
import com.holding.smile.activity.MyApplication;
import com.holding.smile.entity.Consignee;
import com.holding.smile.tools.JSONUtil;

public class MyAddressAdapter extends BaseAdapter {
	private Context			context;
	private List<Consignee>	consigneeList;

	// 自己定义的构造函数
	public MyAddressAdapter(Context context, List<Consignee> consigneeList) {
		this.consigneeList = consigneeList;
		this.context = context;
	}

	@Override
	public int getCount() {
		return consigneeList.size();
	}

	@Override
	public Object getItem(int position) {
		return consigneeList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	static class ViewHolder {
		TextView	name;
		TextView	city;
		TextView	address;
	}

	@Override
	public View getView(final int position, View convertView, final ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.address_list, null);
			holder = new ViewHolder();
			holder.name = (TextView) convertView.findViewById(R.id.address_list_name);
			holder.city = (TextView) convertView.findViewById(R.id.address_list_city);
			holder.address = (TextView) convertView.findViewById(R.id.address_list_address);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		Consignee cons = (Consignee) getItem(position);
		holder.name.setText(cons.getName().trim());

		String pcd = MyApplication.getInstance()
									.getSqliteService()
									.getProvinceCityDistrict(cons.getProvinceId(),
											cons.getCityId(), cons.getDistrictId());
		holder.city.setText(pcd);
		holder.address.setText(cons.getAddress());

		convertView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, AddressEditActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.putExtra("consignee", JSONUtil.getEntity2Json(getItem(position)));
				((Activity) parent.getContext()).startActivityForResult(intent,
						BaseActivity.ADDRESS_EDIT_CODE);
			}
		});
		return convertView;
	}
}
