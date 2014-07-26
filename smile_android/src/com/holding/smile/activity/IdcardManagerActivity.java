
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
import android.widget.ListView;
import android.widget.TextView;

import com.holding.smile.R;
import com.holding.smile.adapter.MyIdcardAdapter;
import com.holding.smile.dto.RtnValueDto;
import com.holding.smile.entity.Idcard;
import com.holding.smile.tools.CodeValidator;
import com.holding.smile.tools.ToastUtils;

/**
 * 收货地址管理
 * 
 * @author zhangb 2014年4月15日 下午3:22:11
 * 
 */
public class IdcardManagerActivity extends BaseActivity implements OnClickListener {

	private Button			idcardsAdd;
	private List<Idcard>	list;
	private MyIdcardAdapter	adapter;
	private ListView		listView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentLayout(R.layout.idcard_manager);
		ImageView backBtn = displayHeaderBack();
		backBtn.setOnClickListener(this);

		TextView textView = displayHeaderDescription();
		textView.setText("身份证管理");

		listView = (ListView) findViewById(R.id.list_idcards_list);
		idcardsAdd = (Button) findViewById(R.id.list_idcards_add);
		idcardsAdd.setOnClickListener(this);

		startTask();
	}

	@Override
	public synchronized void loadData() {
		RtnValueDto idcards = MyApplication.getInstance().getDataService().getIdcardsList();
		if (CodeValidator.dealCode(context, idcards)) {
			Message msg = mUIHandler.obtainMessage(1);
			msg.obj = idcards;
			msg.sendToTarget();
		} else {
			closeLoading();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_back: {
				finish();
				break;
			}
			case R.id.list_idcards_add: {
				Intent intent = new Intent(this, IdcardEditActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivityForResult(intent, IDCARD_EDIT_CODE);
				break;
			}
		}
		super.onClick(v);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == IDCARD_EDIT_CODE && resultCode == RESULT_OK) {
			startTask();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@SuppressLint("HandlerLeak")
	private final Handler	mUIHandler	= new Handler() {

											@Override
											public void handleMessage(Message msg) {
												switch (msg.what) {
													case 1: {
														RtnValueDto rvd = (RtnValueDto) (msg.obj);
														list = rvd.getIdcardsData();
														if (list == null) {
															ToastUtils.showShort(context, "暂无数据！");
															break;
														}

														adapter = new MyIdcardAdapter(context, list);
														listView.setAdapter(adapter);
														break;
													}
												}
												closeLoading();
											}
										};
}
