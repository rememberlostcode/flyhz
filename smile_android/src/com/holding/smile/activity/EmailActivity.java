
package com.holding.smile.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.holding.smile.R;
import com.holding.smile.dto.RtnValueDto;
import com.holding.smile.tools.CodeValidator;
import com.holding.smile.tools.StrUtils;
import com.holding.smile.tools.ToastUtils;

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
		if (email == null || "".equals(email)) {
			saveButton.setVisibility(View.VISIBLE);
			canleButton.setVisibility(View.GONE);
		} else {
			canleButton.setVisibility(View.VISIBLE);
			saveButton.setVisibility(View.GONE);
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
				if(StrUtils.checkEmail(email)){
					progressBar.setVisibility(View.VISIBLE);
					RtnValueDto rvd = MyApplication.getInstance().getSubmitService()
													.setUserInfo("email", email);
					if (CodeValidator.dealCode(context, rvd)) {
						ToastUtils.showShort(this, "保存成功！");
						Intent intent = new Intent();
						intent.putExtra("email", email);
						setResult(RESULT_OK, intent);
						finish();
					}
					waitCloseProgressBar();
				} else {
					ToastUtils.showShort(this, "邮箱格式不正确！");
					return;
				}
				break;
			}
			case R.id.email_canle: {
				progressBar.setVisibility(View.VISIBLE);
				// 解除绑定邮箱
				RtnValueDto rvd = MyApplication.getInstance().getSubmitService()
												.setUserInfo("email", "");
				if (CodeValidator.dealCode(context, rvd)) {
					ToastUtils.showShort(this, "取消成功！");
					Intent intent = new Intent();
					intent.putExtra("email", "");
					setResult(RESULT_OK, intent);
					finish();
				}
				waitCloseProgressBar();
				break;
			}
		}
		super.onClick(v);
	}

}
