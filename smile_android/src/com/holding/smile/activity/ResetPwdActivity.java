
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

				if (!newPwd.equals(conPwd)) {
					Toast.makeText(context, "输入的两次新密码不一致", Toast.LENGTH_SHORT).show();
				} else {
					RtnValueDto rvd = MyApplication.getInstance().getSubmitService()
													.passwordReset(oldPwd, newPwd);
					if (rvd != null && rvd.getCode() != null && rvd.getCode() == 200000) {
						Toast.makeText(context, "修改成功", Toast.LENGTH_SHORT).show();
						setResult(RESULT_CANCELED, null);
						finish();
					} else {
						Toast.makeText(context, "修改失败", Toast.LENGTH_SHORT).show();
					}
				}
			}
		});
	}
}
