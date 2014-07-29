
package com.holding.smile.activity;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.holding.smile.R;
import com.holding.smile.dto.RtnValueDto;
import com.holding.smile.entity.Idcard;
import com.holding.smile.tools.BitmapUtils;
import com.holding.smile.tools.CodeValidator;
import com.holding.smile.tools.IdcardValidator;
import com.holding.smile.tools.ToastUtils;

/**
 * 身份证信息编辑
 * 
 * @author zhangb 2014年4月21日 上午11:19:41
 * 
 */
public class IdcardEditActivity extends BaseActivity implements OnClickListener {
	private Idcard		idcard;
	private String		picturePath;
	private String		backPicturePath;

	private EditText	idcardName;
	private EditText	idcardNumber;
	private TextView		idcardSave;
	private Button		idcardDelete;
	private ImageView	imageView;
	private ImageView	imageBackView;
	private ImageView	imageViewButton;
	private ImageView	imageBackViewButton;

	private int			optNum	= 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentLayout(R.layout.idcard_edit);
		ImageView backBtn = displayHeaderBack();
		backBtn.setOnClickListener(this);

		idcardSave = displayHeaderRight();
		idcardSave.setText("保存");
		
		TextView textView = displayHeaderDescription();
		textView.setText("身份证编辑");

		idcardName = (EditText) findViewById(R.id.idcard_name);
		idcardNumber = (EditText) findViewById(R.id.idcard_number);
		idcardDelete = (Button) findViewById(R.id.idcard_delete);
		imageView = (ImageView) findViewById(R.id.idcard_idcard_img);
		imageBackView = (ImageView) findViewById(R.id.idcard_idcard_back_img);
		imageViewButton = (ImageView) findViewById(R.id.idcard_idcard_img_upload);
		imageBackViewButton = (ImageView) findViewById(R.id.idcard_idcard_back_img_upload);

		idcardName.setOnClickListener(this);
		idcardNumber.setOnClickListener(this);
		idcardSave.setOnClickListener(this);
		idcardDelete.setOnClickListener(this);
		imageView.setOnClickListener(this);
		imageBackView.setOnClickListener(this);
		imageViewButton.setOnClickListener(this);
		imageBackViewButton.setOnClickListener(this);
		
		try {
			Intent intent = getIntent();
			if (intent.getExtras() != null && intent.getExtras().getSerializable("idcard") != null) {
				idcard = (Idcard) (intent.getExtras().getSerializable("idcard"));
				if (idcard != null) {
					idcardName.setText(idcard.getName());
					idcardNumber.setText(idcard.getNumber());
					if (idcard.getUrl() != null && !"".equals(idcard.getUrl())) {
						imageView.setVisibility(View.VISIBLE);
						LayoutParams para = imageView.getLayoutParams();
						para.height = MyApplication.getInstance().getScreenHeight() / 4;
						imageView.setLayoutParams(para);
						MyApplication.getImageLoader().DisplayImage(
								MyApplication.jgoods_img_url + idcard.getUrl(), imageView, false);
					}
					
					if (idcard.getBack_url() != null && !"".equals(idcard.getBack_url())) {
						imageBackView.setVisibility(View.VISIBLE);
						LayoutParams paraBak = imageBackView.getLayoutParams();
						paraBak.height = MyApplication.getInstance().getScreenHeight()/4;
						imageBackView.setLayoutParams(paraBak);
						MyApplication.getImageLoader().DisplayImage(
								MyApplication.jgoods_img_url + idcard.getBack_url(), imageBackView, false);
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
		
		idcardNumber.setOnEditorActionListener(new EditText.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE
						|| actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
					InputMethodManager imm = (InputMethodManager) idcardNumber.getContext()
																			.getSystemService(
																					Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(idcardNumber.getWindowToken(), 0);
					return true;
				}
				return false;
			}
		});
	}

	@Override
	public synchronized void loadData() {
		RtnValueDto rvd = null;
		try {
			switch (optNum) {
				case OPT_CODE_SAVE: {
					rvd = MyApplication.getInstance().getSubmitService()
										.idcardSave(idcard, picturePath, backPicturePath);
					break;
				}
				case OPT_CODE_REMOVE: {
					if (idcard != null && idcard.getId() != null) {
						rvd = MyApplication.getInstance().getSubmitService()
											.idcardRemove(idcard.getId());
					}
					break;
				}
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
				InputMethodManager imm = (InputMethodManager) idcardName.getContext().getSystemService(
								Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(idcardName.getWindowToken(), 0);
				setResult(RESULT_CANCELED, null);
				finish();
				break;
			}
			case R.id.header_right: {
				String xm = idcardName.getText().toString();
				if ("".equals(xm) || "".equals(xm.trim())) {
					ToastUtils.showShort(this, "姓名不能为空");
					break;
				}
				idcard.setName(xm.trim());

				String sfzh = idcardNumber.getText().toString();
				if ("".equals(sfzh) || "".equals(sfzh.trim())) {
					ToastUtils.showShort(this, "身份证号不能为空!");
					break;
				}
				idcard.setNumber(sfzh.trim());
				IdcardValidator iv = new IdcardValidator();
				if (!iv.isValidatedAllIdcard(idcard.getNumber())) {
					ToastUtils.showShort(this, "身份证号非法，请确认后再提交！");
					break;
				}

				if (idcard.getId() == null
						&& (picturePath == null || "".equals(picturePath.trim()))
						&& (backPicturePath == null || "".equals(backPicturePath.trim()))) {
					ToastUtils.showShort(this, "必须上传身份证,正反两面都需要!");
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
			case R.id.idcard_idcard_img_upload: {
				Intent i = new Intent(Intent.ACTION_PICK,
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(i, SELECT_PHOTO_IMAGE);
				break;
			}
			case R.id.idcard_idcard_back_img_upload: {
				Intent i = new Intent(Intent.ACTION_PICK,
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(i, SELECT_PHOTO_IMAGE_BACK);
				break;
			}
			case R.id.idcard_idcard_img: {
				if (picturePath == null && idcard.getUrl() == null) {
					ToastUtils.showShort(this, "您还未上传身份证!");
				} else {
					Intent intent = new Intent(this, GoodsBigImgActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					List<String> picList = new ArrayList<String>();
					if(picturePath == null){
						picList.add(idcard.getUrl());
					} else {
						picList.add(picturePath);
					}
					int position = 0;
					intent.putStringArrayListExtra("picList", (ArrayList<String>) picList);
					intent.putExtra("position", position);
					startActivity(intent);
				}
				break;
			}
			case R.id.idcard_idcard_back_img: {
				if (backPicturePath == null && idcard.getBack_url() == null) {
					ToastUtils.showShort(this, "您还未上传身份证!");
				} else {
					Intent intent = new Intent(this, GoodsBigImgActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					List<String> picList = new ArrayList<String>();
					if(backPicturePath == null){
						picList.add(idcard.getBack_url());
					} else {
						picList.add(backPicturePath);
					}
					
					int position = 0;
					intent.putStringArrayListExtra("picList", (ArrayList<String>) picList);
					intent.putExtra("position", position);
					startActivity(intent);
				}
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
			
			LayoutParams para = imageView.getLayoutParams();
			para.height = MyApplication.getInstance().getScreenHeight()/4;
			
			imageView.setLayoutParams(para);
			imageView.setImageBitmap(BitmapUtils.decodeFile(picturePath, 500, 500));
			imageView.setVisibility(View.VISIBLE);
		}
		if (requestCode == SELECT_PHOTO_IMAGE_BACK && resultCode == RESULT_OK) {
			Uri selectedImage = data.getData();
			String[] filePathColumn = { MediaStore.Images.Media.DATA };

			Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null,
					null);
			cursor.moveToFirst();

			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			backPicturePath = cursor.getString(columnIndex);
			cursor.close();
			
			LayoutParams paraBak = imageBackView.getLayoutParams();
			paraBak.height = MyApplication.getInstance().getScreenHeight()/4;
			
			imageBackView.setLayoutParams(paraBak);
			imageBackView.setImageBitmap(BitmapUtils.decodeFile(backPicturePath, 500, 500));
			imageBackView.setVisibility(View.VISIBLE);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@SuppressLint("HandlerLeak")
	private final Handler	mUIHandler	= new Handler() {

											@Override
											public void handleMessage(Message msg) {
												// if (msg.obj != null) {
												closeImmediatelyLoading();
												switch (msg.what) {
													case OPT_CODE_SAVE: {
														if (msg.obj != null) {
															RtnValueDto rvd = (RtnValueDto) msg.obj;
															if (rvd != null
																	&& CodeValidator.dealCode(
																			context, rvd)
																	&& 200000 == rvd.getCode()) {
																ToastUtils.showShort(context,
																		"保存成功!");
																Intent intent = new Intent();
																intent.putExtra("idcard", idcard);
																setResult(RESULT_OK, intent);
																finish();
															} else {
																ToastUtils.showShort(context,
																		"保存失败，请重试！");
															}
														} else {
															ToastUtils.showShort(context,
																	"保存失败，请重试！");
														}
														break;
													}
													case OPT_CODE_REMOVE: {
														if (msg.obj != null) {
															RtnValueDto rvd = (RtnValueDto) msg.obj;
															if (rvd != null
																	&& CodeValidator.dealCode(
																			context, rvd)
																	&& 200000 == rvd.getCode()) {
																ToastUtils.showShort(context,
																		"删除成功!");
																Intent intent = new Intent();
																intent.putExtra("idcard", idcard);
																setResult(RESULT_OK, intent);
																finish();
															} else {
																if (idcard != null
																		&& idcard.getId() != null) {
																	ToastUtils.showShort(context,
																			"删除失败，请重试!");
																}
															}
														} else {
															if (idcard != null
																	&& idcard.getId() != null) {
																ToastUtils.showShort(context,
																		"删除失败，请重试!");
															}
														}
														break;
													}
												}
											}
										};

}
