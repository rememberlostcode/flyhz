
package com.holding.smile.activity;

import java.util.ArrayList;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.holding.smile.R;
import com.holding.smile.adapter.MyAddressAdapter;
import com.holding.smile.dto.RtnValueDto;
import com.holding.smile.entity.Consignee;

/**
 * 收货地址管理
 * 
 * @author zhangb 2014年4月15日 下午3:22:11
 * 
 */
public class AddressManagerActivity extends BaseActivity implements OnClickListener {

	private Button				addressAdd;
	private List<Consignee>		list;
	private MyAddressAdapter	adapter;
	private ListView			listView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentLayout(R.layout.address_manager);
		ImageView backBtn = displayHeaderBack();
		backBtn.setOnClickListener(this);

		TextView textView = displayHeaderDescription();
		textView.setText("收货地址管理");

		listView = (ListView) findViewById(R.id.list_address_list);
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
				Intent intent = new Intent(this, AddressEditActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivityForResult(intent, ADDRESS_EDIT_CODE);
				break;
			}
		}
		super.onClick(v);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == ADDRESS_EDIT_CODE && resultCode == RESULT_OK) {
			if (data.getExtras() != null && data.getExtras().getSerializable("consignee") != null) {
				Consignee consignee = (Consignee) (data.getExtras().getSerializable("consignee"));
				if (list == null) {
					list = new ArrayList<Consignee>();
				}
				boolean isAdd = true;
				for (int i = 0; i < list.size(); i++) {
					if (list.get(i).getId() == consignee.getId()) {
						if (consignee.getIsDel()) {// 是否是删除
							list.remove(i);
						} else {
							list.set(i, consignee);// 修改只需要重新设置
						}
						isAdd = false;
						break;
					}
				}
				if (isAdd && !consignee.getIsDel()) {// 不会删除也不是修改需添加
					list.add(consignee);
				}
				RtnValueDto consignees = new RtnValueDto();
				consignees.setConsigneeData(list);
				Message msg = mUIHandler.obtainMessage(1);
				msg.obj = consignees;
				msg.sendToTarget();
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@SuppressLint("HandlerLeak")
	private final Handler	mUIHandler	= new Handler() {

											@Override
											public void handleMessage(Message msg) {
												progressBar.setVisibility(View.GONE);
												switch (msg.what) {
													case 1: {
														RtnValueDto rvd = (RtnValueDto) (msg.obj);
														list = rvd.getConsigneeData();
														if (list == null) {
															Toast.makeText(context, "暂无数据",
																	Toast.LENGTH_SHORT).show();
															break;
														}

														adapter = new MyAddressAdapter(context,
																list);
														listView.setAdapter(adapter);
														break;
													}
												}
											}
										};
}
