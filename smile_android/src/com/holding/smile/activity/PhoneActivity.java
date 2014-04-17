
package com.holding.smile.activity;

import com.holding.smile.R;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 手机设置
 */
public class PhoneActivity extends BaseActivity implements OnClickListener {
	private EditText				phoneEditText;
	private Button					saveButton;
	private String phone = "";

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
				setResult(RESULT_CANCELED, null);
				finish();
				break;
			}
			case R.id.phone_save: {

				//设置手机
				break;
			}
		}
		super.onClick(v);
	}

}
