
package com.holding.smile.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import cn.jpush.android.api.InstrumentedActivity;
import cn.jpush.android.api.JPushInterface;

import com.holding.smile.R;
import com.holding.smile.tools.ToastUtils;
import com.holding.smile.tools.jpush.ExampleUtil;

public class JPushActivity extends InstrumentedActivity implements OnClickListener {

	private EditText		msgText;
	private TextView		stausText;
	private Button			startButton;
	private Button			endButton;
	private Button			resumeButton;

	public static boolean	isForeground	= false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.jpush);
		initView();
		registerMessageReceiver(); // used for receive msg
	}

	private void initView() {
		msgText = (EditText) findViewById(R.id.jpush_msg_rec);
		stausText = (TextView) findViewById(R.id.jpush_status);
		startButton = (Button) findViewById(R.id.jpush_start);
		endButton = (Button) findViewById(R.id.jpush_end);
		resumeButton = (Button) findViewById(R.id.jpush_resume);

		startButton.setOnClickListener(this);
		endButton.setOnClickListener(this);
		resumeButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.jpush_start: {
				init();
				stausText.setText("已启动消息接收");
				break;
			}
			case R.id.jpush_end: {
				stop();
				stausText.setText("未启动消息接收");
				break;
			}
			case R.id.jpush_resume: {
				resume();
				stausText.setText("已重新启动消息接收");
				break;
			}
		}
	}

	// 初始化 JPush。如果已经初始化，但没有登录成功，则执行重新登录。
	private void init() {
		JPushInterface.init(this);
		ToastUtils.showShort(this, "已经初始化！");
	}

	private void stop() {
		JPushInterface.stopPush(this);
		ToastUtils.showShort(this, "已经停止！");
	}
	
	private void resume() {
		JPushInterface.resumePush(this);
		ToastUtils.showShort(this, "已经重启！");
	}

	@Override
	protected void onResume() {
		isForeground = true;
		super.onResume();
	}

	@Override
	protected void onPause() {
		isForeground = false;
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(mMessageReceiver);
		super.onDestroy();
	}

	// for receive customer msg from jpush server
	private MessageReceiver		mMessageReceiver;
	public static final String	MESSAGE_RECEIVED_ACTION	= "com.example.jpushdemo.MESSAGE_RECEIVED_ACTION";
	public static final String	KEY_TITLE				= "title";
	public static final String	KEY_MESSAGE				= "message";
	public static final String	KEY_EXTRAS				= "extras";

	public void registerMessageReceiver() {
		mMessageReceiver = new MessageReceiver();
		IntentFilter filter = new IntentFilter();
		filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
		filter.addAction(MESSAGE_RECEIVED_ACTION);
		registerReceiver(mMessageReceiver, filter);
	}

	public class MessageReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
				String messge = intent.getStringExtra(KEY_MESSAGE);
				String extras = intent.getStringExtra(KEY_EXTRAS);
				StringBuilder showMsg = new StringBuilder();
				showMsg.append(KEY_MESSAGE + " : " + messge + "\n");
				if (!ExampleUtil.isEmpty(extras)) {
					showMsg.append(KEY_EXTRAS + " : " + extras + "\n");
				}
				setCostomMsg(showMsg.toString());
			}
		}
	}

	private void setCostomMsg(String msg) {
		if (null != msgText) {
			msgText.setText(msg);
			msgText.setVisibility(android.view.View.VISIBLE);
		}
	}

}