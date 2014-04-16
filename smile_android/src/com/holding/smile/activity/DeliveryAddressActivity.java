
package com.holding.smile.activity;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.holding.smile.R;
import com.holding.smile.dto.RtnValueDto;
import com.holding.smile.entity.Consignee;

/**
 * 收货地址管理
 * 
 * @author zhangb 2014年4月15日 下午3:22:11
 * 
 */
public class DeliveryAddressActivity extends BaseActivity implements OnClickListener {

	private LinearLayout	settingAddressListLayout;
	private Button			addressAdd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentLayout(R.layout.delivery_address);
		ImageView backBtn = displayHeaderBack();
		backBtn.setOnClickListener(this);

		settingAddressListLayout = (LinearLayout) findViewById(R.id.setting_address_list);
		addressAdd = (Button) findViewById(R.id.setting_address_add);
		addressAdd.setOnClickListener(this);

		startTask();

		// System.out.println(MyApplication.getInstance().getSqliteService().getProvinces());
	}

	@Override
	public void loadData() {
		RtnValueDto consignees = MyApplication.getInstance().getDataService().getConsigneeList();
		if (consignees != null && consignees.getConsigneeData() != null) {
			List<Consignee> list = consignees.getConsigneeData();
			for (int i = 0; i < list.size(); i++) {
				TextView text = new TextView(context);
				text.setText(list.get(i).getAddress());
				settingAddressListLayout.addView(text);
			}
		} else {
			Toast.makeText(context, "暂无数据", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_back: {
				setResult(RESULT_CANCELED, null);
				finish();
				break;
			}
			case R.id.setting_address_add: {
				Intent intent = new Intent();
				intent.setClass(context, AddressEditActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				startActivity(intent);
				break;
			}
		}
		super.onClick(v);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}
