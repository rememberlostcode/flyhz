
package com.holding.smile.activity;

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
import com.holding.smile.tools.StrUtils;

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
					Toast.makeText(context, "密码不能为空！", Toast.LENGTH_SHORT).show();
					return;
				}
				if (newPwd.length() < 6 || newPwd.length() > 12) {
					Toast.makeText(context, "密码只能6到12个字符！", Toast.LENGTH_SHORT).show();
					return;
				}
				if (!newPwd.equals(conPwd.trim())) {
					Toast.makeText(context, "两个密码不一致！", Toast.LENGTH_SHORT).show();
					return;
				}
				if (!StrUtils.chaeckPassword(newPwd)) {
					Toast.makeText(context, "密码太简单了，至少需要字母加数字！", Toast.LENGTH_SHORT).show();
					return;
				}

				RtnValueDto rvd = MyApplication.getInstance().getSubmitService()
												.passwordReset(oldPwd, newPwd);
				if (rvd != null) {
					if( rvd.getCode() != null && rvd.getCode() == 200000){
						Toast.makeText(context, "修改成功", Toast.LENGTH_SHORT).show();
						setResult(RESULT_CANCELED, null);
						finish();
					} else if (rvd.getCode() != null && rvd.getCode() == 101026) {
						Toast.makeText(context, "修改失败，原密码错误！", Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(context, "修改失败，请检查网络！", Toast.LENGTH_SHORT).show();
					}
				} else {
					Toast.makeText(context, "修改失败，请检查网络！", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}
}
