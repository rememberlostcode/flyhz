
package com.holding.smile.activity;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

		TextView textView = displayHeaderDescription();
		textView.setText("收货地址管理");

		settingAddressListLayout = (LinearLayout) findViewById(R.id.setting_address_list);
		addressAdd = (Button) findViewById(R.id.setting_address_add);
		addressAdd.setOnClickListener(this);

		startTask();
	}

	@Override
	public void loadData() {
		RtnValueDto consignees = MyApplication.getInstance().getDataService().getConsigneeList();
		if (consignees != null) {

			Message msg = mUIHandler.obtainMessage(1);
			msg.obj = consignees;
			msg.sendToTarget();

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

	@SuppressLint("HandlerLeak")
	private final Handler	mUIHandler	= new Handler() {

											@Override
											public void handleMessage(Message msg) {
												progressBar.setVisibility(View.GONE);
												switch (msg.what) {
													case 1: {
														if (msg.obj != null) {
															RtnValueDto rvd = (RtnValueDto) (msg.obj);
															List<Consignee> list = rvd.getConsigneeData();
															if (list != null) {
																for (int i = 0; i < list.size(); i++) {
																	TextView text = new TextView(
																			context);
																	text.setText(list.get(i)
																						.getAddress());
																	settingAddressListLayout.addView(text);
																}
															}
														} else {
															Toast.makeText(context, "暂无数据",
																	Toast.LENGTH_SHORT).show();
														}
														break;
													}
												}
											}
										};
}