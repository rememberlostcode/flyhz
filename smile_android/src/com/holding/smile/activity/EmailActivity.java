
package com.holding.smile.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.holding.smile.R;
import com.holding.smile.dto.RtnValueDto;

/**
 * 邮箱设置
 * 
 * @author zhangb 2014年4月18日 上午8:32:02
 * 
 */
public class EmailActivity extends BaseActivity implements OnClickListener {

	private EditText	emailEditText;
	private Button		saveButton;
	private Button		canleButton;
	private String		email	= "";

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
				// 设置邮箱
				email = emailEditText.getText().toString();
				RtnValueDto rvd = MyApplication.getInstance().getSubmitService()
												.setUserInfo("email", email);
				if (200000 == rvd.getCode()) {
					Toast.makeText(context, "保存成功！", Toast.LENGTH_SHORT).show();
					Intent intent = new Intent();
					intent.putExtra("email", email);
					setResult(RESULT_OK, intent);
					finish();
				} else {
					Toast.makeText(context, "保存失败，请重试！", Toast.LENGTH_SHORT).show();
				}
				break;
			}
			case R.id.email_canle: {
				// 解除绑定邮箱
				RtnValueDto rvd = MyApplication.getInstance().getSubmitService()
												.setUserInfo("email", "");
				if (200000 == rvd.getCode()) {
					Toast.makeText(context, "保存成功！", Toast.LENGTH_SHORT).show();
					Intent intent = new Intent();
					intent.putExtra("email", "");
					setResult(RESULT_OK, intent);
					finish();
				} else {
					Toast.makeText(context, "保存失败，请重试！", Toast.LENGTH_SHORT).show();
				}
				break;
			}
		}
		super.onClick(v);
	}

}
