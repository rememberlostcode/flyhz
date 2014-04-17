
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
 * 邮箱设置
 * @author silvermoon
 *
 */
public class EmailActivity extends BaseActivity implements OnClickListener {

	private EditText				emailEditText;
	private Button					saveButton;
	private Button					canleButton;
	private String email = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentLayout(R.layout.email);
		ImageView backBtn = displayHeaderBack();
		backBtn.setOnClickListener(this);

		TextView textView = displayHeaderDescription();
		textView.setText("邮箱设置");

		emailEditText = (EditText) findViewById(R.id.email_address);
		saveButton = (Button) findViewById(R.id.email_save);
		canleButton = (Button) findViewById(R.id.email_canle);

		emailEditText.setOnClickListener(this);
		saveButton.setOnClickListener(this);
		canleButton.setOnClickListener(this);

		try {
			Intent intent = getIntent();
			if (intent.getExtras() != null) {
				email = intent.getExtras().getString("email");
				if (email != null) {
					emailEditText.setText(email);
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
			case R.id.email_save: {

				//设置邮箱
				break;
			}
			case R.id.email_canle: {

				//解除绑定邮箱
				break;
			}
		}
		super.onClick(v);
	}

}
