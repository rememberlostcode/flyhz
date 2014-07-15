
package com.holding.smile.activity;

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
 * 修改密码
 * 
 * @author zhangb 2014年4月16日 下午4:48:45
 * 
 */
public class ResetPwdActivity extends BaseActivity {

	private EditText	pwdOld;
	private EditText	pwdNew;
	private EditText	pwdNewConfirm;

	private Button		pwdSave;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentLayout(R.layout.password);
		ImageView backBtn = displayHeaderBack();
		backBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setResult(RESULT_CANCELED, null);
				finish();
			}
		});

		TextView textView = displayHeaderDescription();
		textView.setText("修改密码");

		pwdOld = (EditText) findViewById(R.id.password_old);
		pwdNew = (EditText) findViewById(R.id.password_new);
		pwdNewConfirm = (EditText) findViewById(R.id.password_old_confirm);

		pwdSave = (Button) findViewById(R.id.password_save);
		pwdSave.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String oldPwd = pwdOld.getText().toString();
				String newPwd = pwdNew.getText().toString();
				String conPwd = pwdNewConfirm.getText().toString();

				if (newPwd == null || "".equals(newPwd.trim())) {
					ToastUtils.showShort(context, "密码不能为空！");
					return;
				}
				if (newPwd.length() < 6 || newPwd.length() > 12) {
					ToastUtils.showShort(context, "密码只能6到12个字符！");
					return;
				}
				if (!newPwd.equals(conPwd.trim())) {
					ToastUtils.showShort(context, "两个密码不一致！");
					return;
				}
				if (!StrUtils.chaeckPassword(newPwd)) {
					ToastUtils.showShort(context, "密码太简单了，至少需要字母加数字！");
					return;
				}

				showLoading();
				RtnValueDto rvd = MyApplication.getInstance().getSubmitService()
												.passwordReset(oldPwd, newPwd);
				closeImmediatelyLoading();
				if (CodeValidator.dealCode(context, rvd)) {
					ToastUtils.showShort(context, "修改成功！");
					setResult(RESULT_CANCELED, null);
					finish();
				}
			}
		});
	}

}
