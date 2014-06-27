
package com.holding.smile.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.holding.smile.R;
import com.holding.smile.dto.RtnValueDto;
import com.holding.smile.tools.StrUtils;
import com.holding.smile.tools.ToastUtils;

/**
 * 手机设置
 * 
 * @author zhangb 2014年4月18日 上午8:32:57
 * 
 */
public class PhoneActivity extends BaseActivity implements OnClickListener {
	private EditText	phoneEditText;
	private Button		saveButton;
	private String		phone	= "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentLayout(R.layout.phone);
		ImageView backBtn = displayHeaderBack();
		backBtn.setOnClickListener(this);

		TextView textView = displayHeaderDescription();
		textView.setText("手机设置");

		phoneEditText = (EditText) findViewById(R.id.phone_address);
		saveButton = (Button) findViewById(R.id.phone_save);

		phoneEditText.setOnClickListener(this);
		saveButton.setOnClickListener(this);

		try {
			Intent intent = getIntent();
			if (intent.getExtras() != null) {
				phone = intent.getExtras().getString("phone");
				if (phone != null) {
					phoneEditText.setText(phone);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_back: {
				InputMethodManager imm = (InputMethodManager) phoneEditText.getContext()
						.getSystemService(
								Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(phoneEditText.getWindowToken(), 0);
				setResult(RESULT_CANCELED, null);
				finish();
				break;
			}
			case R.id.phone_save: {
				progressBar.setVisibility(View.VISIBLE);
				// 设置手机
				phone = phoneEditText.getText().toString();
				
				if(!StrUtils.checkPhoneNumber(phone)){
					ToastUtils.showShort(context, "手机号格式不对，请修改！");
					return;
				}
				
				RtnValueDto rvd = MyApplication.getInstance().getSubmitService()
												.setUserInfo("mphone", phone);
				waitCloseProgressBar();
				if (rvd != null && 200000 == rvd.getCode()) {
					ToastUtils.showShort(context, "保存成功！");
					Intent intent = new Intent();
					intent.putExtra("phone", phone);
					setResult(RESULT_OK, intent);
					finish();
				}
				break;
			}
		}
		super.onClick(v);
	}

}
