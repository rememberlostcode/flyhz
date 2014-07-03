
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
import com.holding.smile.tools.CodeValidator;
import com.holding.smile.tools.StrUtils;
import com.holding.smile.tools.ToastUtils;

/**
 * 
 * 类说明：找回密码
 * 
 * @author robin 2014-7-3上午11:03:50
 * 
 */
public class FindBackPwdActivity extends BaseActivity implements OnClickListener {
	private EditText	editText;
	private Button		confirmBtn;
	private String		username	= "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentLayout(R.layout.findbackpwd);
		ImageView backBtn = displayHeaderBack();
		backBtn.setOnClickListener(this);

		TextView textView = displayHeaderDescription();
		textView.setText("找回密码");

		editText = (EditText) findViewById(R.id.input_username);
		confirmBtn = (Button) findViewById(R.id.btn_confirm);

		editText.setOnClickListener(this);
		confirmBtn.setOnClickListener(this);

		try {
			Intent intent = getIntent();
			if (intent.getExtras() != null) {
				username = intent.getExtras().getString("username");
				if (username != null) {
					editText.setText(username);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public synchronized void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_back: {
				InputMethodManager imm = (InputMethodManager) editText.getContext()
																		.getSystemService(
																				Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
				setResult(RESULT_CANCELED, null);
				finish();
				break;
			}
			case R.id.btn_confirm: {
				// 设置用户名
				username = editText.getText().toString();
				if (!StrUtils.isNotEmpty(username)) {
					ToastUtils.showShort(context, "用户名不能为空！");
					return;
				}
				progressBar.setVisibility(View.VISIBLE);

				RtnValueDto rvd = MyApplication.getInstance().getDataService()
												.findBackPwd(username);
				waitCloseProgressBar();
				if (CodeValidator.dealCode(context, rvd)) {
					ToastUtils.showShort(context, "新密码已发送至邮箱：" + rvd.getAtData());
					finish();
				}
				break;
			}
		}
		super.onClick(v);
	}

}
