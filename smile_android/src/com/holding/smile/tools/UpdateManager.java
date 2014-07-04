
package com.holding.smile.tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.holding.smile.R;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

public class UpdateManager {

	private Context				mContext;

	// 提示语
	private String				updateMsg		= "有最新的软件包哦，亲快下载吧~";

	// 返回的安装包url
	private String				apkUrl			= "";
	private String				version			= "1.0.1";

	private Dialog				noticeDialog;

	private Dialog				downloadDialog;
	/* 下载包安装路径 */
	private static final String	savePath		= "/sdcard/updateApk/";

	private static String		saveFileName	= "";

	/* 进度条与通知ui刷新的handler和msg常量 */
	private ProgressBar			mProgress;

	private static final int	DOWN_UPDATE		= 1;

	private static final int	DOWN_OVER		= 2;

	private int					progress;

	private Thread				downLoadThread;

	private boolean				interceptFlag	= false;

	@SuppressLint("HandlerLeak")
	private Handler				mHandler		= new Handler() {
													public void handleMessage(Message msg) {
														switch (msg.what) {
															case DOWN_UPDATE:
																mProgress.setProgress(progress);
																break;
															case DOWN_OVER:

																installApk();
																break;
															default:
																break;
														}
													};
												};

	public UpdateManager(Context context) {
		this.mContext = context;
	}

	// 外部接口让主Activity调用
	public void checkUpdateInfo() {
		if (apkUrl == null || "".equals(apkUrl)) {
			return;
		}
		showNoticeDialog();
	}

	private void showNoticeDialog() {
		AlertDialog.Builder builder = new Builder(mContext);
		builder.setTitle("软件版本更新");
		builder.setMessage(updateMsg);
		builder.setPositiveButton("下载", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				showDownloadDialog();
			}
		});
		builder.setNegativeButton("以后再说", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		noticeDialog = builder.create();
		noticeDialog.show();
	}

	private void showDownloadDialog() {
		AlertDialog.Builder builder = new Builder(mContext);
		builder.setTitle("软件版本更新");

		final LayoutInflater inflater = LayoutInflater.from(mContext);
		View v = inflater.inflate(R.layout.progress_version, null);
		mProgress = (ProgressBar) v.findViewById(R.id.progress_bar);

		builder.setView(v);
		builder.setNegativeButton("取消", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				interceptFlag = true;
			}
		});
		downloadDialog = builder.create();
		downloadDialog.show();

		downloadApk();
	}

	private Runnable	mdownApkRunnable	= new Runnable() {
												@Override
												public void run() {
													try {
														URL url = new URL(apkUrl);

														HttpURLConnection conn = (HttpURLConnection) url.openConnection();
														conn.connect();
														int length = conn.getContentLength();
														InputStream is = conn.getInputStream();

														File file = new File(savePath);
														if (!file.exists()) {
															file.mkdir();
														}

														int index = apkUrl.lastIndexOf("/") + 1;
														if (index < apkUrl.length()) {
															saveFileName = savePath
																	+ apkUrl.substring(index);
															String apkFile = saveFileName;
															File ApkFile = new File(apkFile);
															FileOutputStream fos = new FileOutputStream(
																	ApkFile);

															int count = 0;
															byte buf[] = new byte[1024];

															do {
																int numread = is.read(buf);
																count += numread;
																progress = (int) (((float) count / length) * 100);
																// 更新进度
																mHandler.sendEmptyMessage(DOWN_UPDATE);
																if (numread <= 0) {
																	// 下载完成通知安装
																	mHandler.sendEmptyMessage(DOWN_OVER);
																	downloadDialog.dismiss();
																	break;
																}
																fos.write(buf, 0, numread);
															} while (!interceptFlag);// 点击取消就停止下载.
															fos.close();
															is.close();
														}
													} catch (MalformedURLException e) {
														e.printStackTrace();
													} catch (IOException e) {
														e.printStackTrace();
													}

												}
											};

	/**
	 * 下载apk
	 * 
	 * @param url
	 */

	private void downloadApk() {
		downLoadThread = new Thread(mdownApkRunnable);
		downLoadThread.start();
	}

	public void setApkUrl(String apkUrl) {
		this.apkUrl = apkUrl;
	}

	/**
	 * 安装apk
	 * 
	 * @param url
	 */
	private void installApk() {
		File apkfile = new File(saveFileName);
		if (!apkfile.exists()) {
			return;
		}
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setDataAndType(Uri.parse("file://" + apkfile.toString()),
				"application/vnd.android.package-archive");
		mContext.startActivity(i);

	}
}
