
package com.holding.smile.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.holding.smile.R;
import com.holding.smile.dto.RtnValueDto;
import com.holding.smile.entity.Idcard;
import com.holding.smile.tools.BitmapUtils;
import com.holding.smile.tools.IdcardValidator;

/**
 * 身份证信息编辑
 * 
 * @author zhangb 2014年4月21日 上午11:19:41
 * 
 */
public class IdcardEditActivity extends BaseActivity implements OnClickListener {
	private Idcard		idcard;
	private String		picturePath;

	private EditText	idcardName;
	private EditText	idcardNumber;
	private TextView	idcardPhotoButton;
	private Button		idcardSave;
	private Button		idcardDelete;
	private ImageView	imageView;

	private int			optNum	= 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentLayout(R.layout.idcard_edit);
		ImageView backBtn = displayHeaderBack();
		backBtn.setOnClickListener(this);

		TextView textView = displayHeaderDescription();
		textView.setText("身份证编辑");

		idcardName = (EditText) findViewById(R.id.idcard_name);
		idcardNumber = (EditText) findViewById(R.id.idcard_number);
		idcardPhotoButton = (TextView) findViewById(R.id.idcard_photo_button);
		idcardSave = (Button) findViewById(R.id.idcard_add_save);
		idcardDelete = (Button) findViewById(R.id.idcard_delete);
		imageView = (ImageView) findViewById(R.id.idcard_idcard_img);

		idcardName.setOnClickListener(this);
		idcardNumber.setOnClickListener(this);
		idcardPhotoButton.setOnClickListener(this);
		idcardSave.setOnClickListener(this);
		idcardDelete.setOnClickListener(this);

		try {
			Intent intent = getIntent();
			if (intent.getExtras() != null && intent.getExtras().getSerializable("idcard") != null) {
				idcard = (Idcard) (intent.getExtras().getSerializable("idcard"));
				if (idcard != null) {
					idcardName.setText(idcard.getName());
					idcardNumber.setText(idcard.getNumber());
					if (idcard.getUrl() != null && !"".equals(idcard.getUrl())) {
						MyApplication.getImageLoader().DisplayImage(
								MyApplication.jgoods_img_url + idcard.getUrl(), imageView, false);
						imageView.setVisibility(View.VISIBLE);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (idcard == null) {
			idcard = new Idcard();
			idcardDelete.setVisibility(View.GONE);
		}
	}

	@Override
	public void loadData() {
		RtnValueDto rvd = null;
		try {
			switch (optNum) {
				case OPT_CODE_SAVE:
					rvd = MyApplication.getInstance().getSubmitService()
										.idcardSave(idcard, picturePath);
					break;
				case OPT_CODE_REMOVE:
					if (idcard != null && idcard.getId() != null) {
						rvd = MyApplication.getInstance().getSubmitService()
											.idcardRemove(idcard.getId());
					}
					break;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		Message msg = mUIHandler.obtainMessage(optNum);
		msg.obj = rvd;
		msg.sendToTarget();
	}

	private final int	OPT_CODE_SAVE	= 1;
	private final int	OPT_CODE_REMOVE	= 2;

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_back: {
				setResult(RESULT_CANCELED, null);
				finish();
				break;
			}
			case R.id.idcard_add_save: {
				String xm = idcardName.getText().toString();
				if ("".equals(xm) || "".equals(xm.trim())) {
					Toast.makeText(context, "姓名不能为空", Toast.LENGTH_SHORT).show();
					break;
				}
				idcard.setName(xm.trim());

				String sfzh = idcardNumber.getText().toString();
				if ("".equals(sfzh) || "".equals(sfzh.trim())) {
					Toast.makeText(context, "身份证号不能为空", Toast.LENGTH_SHORT).show();
					break;
				}
				idcard.setNumber(sfzh.trim());
				IdcardValidator iv = new IdcardValidator();
				if (!iv.isValidatedAllIdcard(idcard.getNumber())) {
					Toast.makeText(context, "身份证号非法，请确认后再提交！", Toast.LENGTH_SHORT).show();
					break;
				}

				if (idcard.getId() == null
						&& (picturePath == null || "".equals(picturePath.trim()))) {
					Toast.makeText(context, "必须上传身份证", Toast.LENGTH_SHORT).show();
					break;
				}

				optNum = OPT_CODE_SAVE;
				startTask();
				break;
			}

			case R.id.idcard_delete: {
				new AlertDialog.Builder(this).setTitle("提示框")
												.setMessage("确定删除该记录吗？")
												.setPositiveButton("确定",
														new DialogInterface.OnClickListener() {
															public void onClick(
																	DialogInterface dialog,
																	int which) {
																optNum = OPT_CODE_REMOVE;
																startTask();
															}
														}).setNegativeButton("取消", null).show();
				break;
			}
			case R.id.idcard_photo_button: {
				Intent i = new Intent(Intent.ACTION_PICK,
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(i, SELECT_PHOTO_IMAGE);
				break;
			}
		}
		super.onClick(v);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == SELECT_PHOTO_IMAGE && resultCode == RESULT_OK) {
			Uri selectedImage = data.getData();
			String[] filePathColumn = { MediaStore.Images.Media.DATA };

			Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null,
					null);
			cursor.moveToFirst();

			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			picturePath = cursor.getString(columnIndex);
			cursor.close();
			// imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
			imageView.setImageBitmap(BitmapUtils.decodeFile(picturePath, 500, 500));
			imageView.setVisibility(View.VISIBLE);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@SuppressLint("HandlerLeak")
	private final Handler	mUIHandler	= new Handler() {

											@Override
											public void handleMessage(Message msg) {
												progressBar.setVisibility(View.GONE);
												// if (msg.obj != null) {
												switch (msg.what) {
													case OPT_CODE_SAVE: {
														RtnValueDto rvd = (RtnValueDto) msg.obj;
														if (rvd != null && 200000 == rvd.getCode()) {
															Toast.makeText(context, "保存成功！",
																	Toast.LENGTH_SHORT).show();
															Intent intent = new Intent();
															intent.putExtra("idcard", idcard);
															setResult(RESULT_OK, intent);
															finish();
														} else {
															Toast.makeText(context, "保存失败，请重试！",
																	Toast.LENGTH_SHORT).show();
														}
														break;
													}
													case OPT_CODE_REMOVE: {
														RtnValueDto rvd = (RtnValueDto) msg.obj;
														if (rvd != null && 200000 == rvd.getCode()) {
															Toast.makeText(context, "删除成功！",
																	Toast.LENGTH_SHORT).show();
															Intent intent = new Intent();
															intent.putExtra("idcard", idcard);
															setResult(RESULT_OK, intent);
															finish();
														} else {
															if (idcard != null
																	&& idcard.getId() != null) {
																Toast.makeText(context,
																		"删除失败，请重试！",
																		Toast.LENGTH_SHORT).show();
															}
															finish();
														}
														break;
													}
												}
												// }
											}
										};

}
