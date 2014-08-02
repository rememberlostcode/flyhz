
package com.holding.smile.tools;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.util.EncodingUtils;
import org.apache.http.util.EntityUtils;

import android.util.Log;

import com.holding.smile.activity.MyApplication;
import com.holding.smile.dto.RtnValueDto;

/**
 * 上传文件到服务器
 * 
 * @author robin 2013-12-3 下午1:34:04
 * 
 */
public class FileUtils {
	/**
	 * 上传代码， 第一个参数，为要使用的URL， 第二个参数，为表单内容， 第三个参数为要上传的文件，可以上传多个文件，这根据需要页定
	 */
	// private static final String TAG = "uploadFile";
	private static final int	TIME_OUT	= 10 * 1000;	// 超时时间
	private static final String	CHARSET		= "UTF-8";		// 设置编码

	/**
	 * android上传文件到服务器
	 * 
	 * @param file
	 *            需要上传的文件
	 * @param RequestURL
	 *            请求的rul
	 * @return 返回响应的内容
	 */
	public static String uploadFile(File file, String RequestURL) {
		if (!MyApplication.isHasNetwork()) {
			return CodeValidator.getNoNetworkCodeResult();
		}
		String result = null;
		String BOUNDARY = UUID.randomUUID().toString(); // 边界标识 随机生成
		String PREFIX = "--", LINE_END = "\r\n";
		String CONTENT_TYPE = "multipart/form-data"; // 内容类型

		try {
			URL url = new URL(RequestURL);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(TIME_OUT);
			conn.setConnectTimeout(TIME_OUT);
			conn.setDoInput(true); // 允许输入流
			conn.setDoOutput(true); // 允许输出流
			conn.setUseCaches(false); // 不允许使用缓存
			conn.setRequestMethod("POST"); // 请求方式
			conn.setRequestProperty("Charset", CHARSET); // 设置编码
			conn.setRequestProperty("connection", "keep-alive");
			conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);

			if (file != null) {
				/**
				 * 当文件不为空，把文件包装并且上传
				 */
				DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
				StringBuffer sb = new StringBuffer();
				sb.append(PREFIX);
				sb.append(BOUNDARY);
				sb.append(LINE_END);
				/**
				 * 这里重点注意： name里面的值为服务器端需要key 只有这个key 才可以得到对应的文件
				 * filename是文件的名字，包含后缀名的 比如:abc.png
				 */
				sb.append("Content-Disposition: form-data; name=\"fup\"; filename=\""
						+ file.getName() + "\"" + LINE_END);
				sb.append("Content-Type: image/pjpeg; charset=" + CHARSET + LINE_END);
				sb.append(LINE_END);
				dos.write(sb.toString().getBytes());
				InputStream is = new FileInputStream(file);
				byte[] bytes = new byte[1024];
				int len = 0;
				while ((len = is.read(bytes)) != -1) {
					dos.write(bytes, 0, len);
				}
				is.close();
				dos.write(LINE_END.getBytes());
				byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END).getBytes();
				dos.write(end_data);
				dos.flush();
				/**
				 * 获取响应码 200=成功 当响应成功，获取响应的流
				 */
				int res = conn.getResponseCode();
				Log.i(MyApplication.LOG_TAG, "uploadFile response code:" + res);
				if (res == 200) {
					Log.e(MyApplication.LOG_TAG, "uploadFile request success");
					InputStream input = conn.getInputStream();
					StringBuffer sb1 = new StringBuffer();
					int ss;
					while ((ss = input.read()) != -1) {
						sb1.append((char) ss);
					}
					result = sb1.toString();
					Log.i(MyApplication.LOG_TAG, "uploadFile result:" + result);
				} else {
					Log.i(MyApplication.LOG_TAG, "uploadFile request error");
				}
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 批量上传功能 ：直接通过HTTP协议提交数据到服务器,实现表单提交功能
	 * 
	 * @param actionUrl
	 *            上传路径
	 * @param params
	 *            请求参数 key为参数名,value为参数值
	 * @param files
	 *            上传文件
	 */
	public static String postBatchUploadFile(String actionUrl, Map<String, String> params,
			FormFile[] files) {
		if (!MyApplication.isHasNetwork()) {
			return CodeValidator.getNoNetworkCodeResult();
		}
		String BOUNDARY = UUID.randomUUID().toString(); // 边界标识 随机生成
		String PREFIX = "--", LINE_END = "\r\n";
		String CONTENT_TYPE = "multipart/form-data"; // 内容类型
		try {

			URL url = new URL(actionUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(TIME_OUT);
			conn.setConnectTimeout(TIME_OUT);
			conn.setDoInput(true); // 允许输入流
			conn.setDoOutput(true); // 允许输出流
			conn.setUseCaches(false); // 不允许使用缓存
			conn.setRequestMethod("POST"); // 请求方式
			conn.setRequestProperty("Charset", CHARSET); // 设置编码
			conn.setRequestProperty("connection", "keep-alive");
			conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);

			StringBuilder sb = new StringBuilder();

			// 上传的表单参数部分，格式请参考文章
			for (Map.Entry<String, String> entry : params.entrySet()) {// 构建表单字段内容
				sb.append(PREFIX);
				sb.append(BOUNDARY);
				sb.append(LINE_END);
				sb.append("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"\r\n\r\n");
				sb.append(entry.getValue());
				sb.append(LINE_END);
			}
			DataOutputStream outStream = new DataOutputStream(conn.getOutputStream());
			outStream.write(sb.toString().getBytes());// 发送表单字段数据

			// 上传的文件部分，格式请参考文章
			if (files != null)
				for (FormFile file : files) {
					StringBuilder split = new StringBuilder();
					split.append(PREFIX);
					split.append(BOUNDARY);
					split.append(LINE_END);
					split.append("Content-Disposition: form-data;name=\"" + file.getParameterName()
							+ "\";filename=\"" + file.getFilename() + "\"" + LINE_END);
					// split.append("Content-Type:" + file.getContentType() +
					// "\r\n\r\n");
					split.append("Content-Type: image/pjpeg; charset=" + CHARSET + LINE_END);
					outStream.write(split.toString().getBytes());
					outStream.write(file.getData(), 0, file.getData().length);
					outStream.write(LINE_END.getBytes());
				}
			byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END).getBytes();// 数据结束标志
			outStream.write(end_data);
			outStream.flush();
			int cah = conn.getResponseCode();
			if (cah != 200)
				throw new RuntimeException("请求url失败");
			InputStream is = conn.getInputStream();
			int ch;
			StringBuilder b = new StringBuilder();
			while ((ch = is.read()) != -1) {
				b.append((char) ch);
			}
			outStream.close();
			conn.disconnect();
			return b.toString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 批量文件上传
	 * 
	 * @param requestUrl
	 * @param fileNameList
	 * @throws UnsupportedEncodingException
	 * @throws Exception
	 */
	public static String batchUploadFile(String requestUrl, List<NameValuePair> params,
			List<String> fileNameList) throws Exception {
		if (!MyApplication.isHasNetwork()) {
			return CodeValidator.getNoNetworkCodeResult();
		}
		HttpParams httpParams = new BasicHttpParams();
		// 连接超时
		httpParams.setParameter("http.connection.timeout", TIME_OUT);
		// 请求超时
		httpParams.setParameter("http.socket.timeout", TIME_OUT);
		httpParams.setParameter("http.protocol.content-charset", CHARSET);
		httpParams.setBooleanParameter("http.connection.stalecheck", false);
		HttpProtocolParams.setContentCharset(httpParams, "UTF-8");
		HttpProtocolParams.setHttpElementCharset(httpParams, "UTF-8");

		DefaultHttpClient httpClient = new DefaultHttpClient(httpParams);
		HttpPost postRequest = new HttpPost(requestUrl);
		MultipartEntity reqEntity = new MultipartEntity();

		postRequest.addHeader("Cookie", MyApplication.getInstance().getSessionId());

		// 上传的表单参数部分
		for (NameValuePair nameValue : params) {
			reqEntity.addPart(nameValue.getName(),
					new StringBody(nameValue.getValue(), Charset.forName("UTF-8")));
		}

		if (fileNameList == null || fileNameList.isEmpty())
			return null;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ByteArrayBody bab = null;
		// int fileSize = 0;
		for (String fileName : fileNameList) {
			if (fileName == null || fileName.equals(""))
				continue;

			// 先压缩图片再上传
			try {
				bos = BitmapUtils.getCompressBitmap(fileName, 1000, 1000, 100);
				byte[] data = bos.toByteArray();
				// fileSize += data.length;
				bab = new ByteArrayBody(data, fileName.substring(fileName.lastIndexOf("/") + 1));
				reqEntity.addPart("image", bab);
				bos.reset();// 清空
				bos.flush();
			} catch (Exception e) {
				reqEntity.addPart("image", new StringBody("image error"));
			}
		}
		bos.close();
		postRequest.setEntity(reqEntity);
		String result = null;
		try {
			// 获取HttpResponse实例
			HttpResponse response = httpClient.execute(postRequest);
			// 判断是够请求成功
			if (response.getStatusLine().getStatusCode() == 200) {
				// 获取返回的数据
				result = EntityUtils.toString(response.getEntity(), CHARSET);
				Log.d(MyApplication.LOG_TAG, "HttpPost方式请求成功，返回数据如下：");
				if (result != null) {
					RtnValueDto rtn = JSONUtil.getJson2Entity(result, RtnValueDto.class);
					Integer code = rtn.getCode();
					if (code.equals(100001)) {
						result = "您尚未登录！";
					} else if (code.equals(100002)) {
						result = "您无权限操作！";
					} else if (code.equals(100010)) {
						result = "登录不成功！";
					} else if (code.equals(200000)) {
						result = "操作成功！";
					}
				} else {
					result = "操作成功！";
				}
			} else {
				Log.d(MyApplication.LOG_TAG, "HttpPost方式请求失败");
				result = "发布失败";
			}

		} catch (ConnectTimeoutException e) {
			result = "请求超时";
		} catch (ParseException e) {
			result = "转化异常";
		} catch (IOException e) {
			result = "IO异常";
		} catch (Exception e) {
			result = "上传出现异常！";
		} finally {
			if (httpClient != null) {
				httpClient.getConnectionManager().shutdown();
				httpClient = null;
			}
		}

		return result;
	}

	/**
	 * 上传头像
	 * 
	 * @param requestUrl
	 * @param fileName
	 * @throws UnsupportedEncodingException
	 * @throws Exception
	 */
	public static String uploadHeadPhoto(String requestUrl, String fileName) throws Exception {
		if (!MyApplication.isHasNetwork()) {
			return CodeValidator.getNoNetworkCodeResult();
		}
		HttpParams httpParams = new BasicHttpParams();
		// 连接超时
		httpParams.setParameter("http.connection.timeout", TIME_OUT);
		// 请求超时
		httpParams.setParameter("http.socket.timeout", TIME_OUT);
		httpParams.setParameter("http.protocol.content-charset", CHARSET);
		httpParams.setBooleanParameter("http.connection.stalecheck", false);
		HttpProtocolParams.setContentCharset(httpParams, "UTF-8");
		HttpProtocolParams.setHttpElementCharset(httpParams, "UTF-8");

		DefaultHttpClient httpClient = new DefaultHttpClient(httpParams);
		HttpPost postRequest = new HttpPost(requestUrl);
		MultipartEntity reqEntity = new MultipartEntity();

		postRequest.addHeader("Cookie", MyApplication.getInstance().getSessionId());

		// // 上传的表单参数部分
		// if (params != null)
		// for (NameValuePair nameValue : params) {
		// reqEntity.addPart(nameValue.getName(),
		// new StringBody(nameValue.getValue(), Charset.forName("UTF-8")));
		// }

		if (fileName == null || fileName.equals(""))
			return "头像不能为空";
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ByteArrayBody bab = null;
		// 先压缩图片再上传
		try {
			bos = BitmapUtils.getCompressBitmap(fileName, 1000, 1000, 100);
			byte[] data = bos.toByteArray();
			bab = new ByteArrayBody(data, fileName.substring(fileName.lastIndexOf("/") + 1));
			reqEntity.addPart("file", bab);
			bos.reset();// 清空
			bos.flush();
		} catch (Exception e) {
			reqEntity.addPart("file", new StringBody("image error"));
		}
		bos.close();
		postRequest.setEntity(reqEntity);
		String result = null;
		try {
			// 获取HttpResponse实例
			HttpResponse response = httpClient.execute(postRequest);
			// 判断是够请求成功
			if (response.getStatusLine().getStatusCode() == 200) {
				// 获取返回的数据
				result = EntityUtils.toString(response.getEntity(), CHARSET);
				Log.d(MyApplication.LOG_TAG, "HttpPost方式请求成功，返回数据如下：");
				if (result != null) {
					RtnValueDto rtn = JSONUtil.getJson2Entity(result, RtnValueDto.class);
					if (rtn != null && rtn.getCode().equals(200000)) {
						result = rtn.getValidate().getMessage();
					} else {
						result = rtn.getValidate().getMessage();
					}
				}
			} else {
				Log.d(MyApplication.LOG_TAG, "HttpPost方式请求失败");
				result = "非法操作";
			}

		} catch (ConnectTimeoutException e) {
			result = "请求超时";
		} catch (ParseException e) {
			result = "转化异常";
		} catch (IOException e) {
			result = "IO异常";
		} catch (Exception e) {
			result = "上传出现异常！";
		} finally {
			if (httpClient != null) {
				httpClient.getConnectionManager().shutdown();
				httpClient = null;
			}
		}

		return result;
	}

	/**
	 * 上传身份证
	 * 
	 * @param requestUrl
	 * @param params
	 * @param filePath
	 * @return
	 * @throws Exception
	 */
	public static String uploadIdcardPhoto(String requestUrl, List<NameValuePair> params,
			String filePath,String backFilePath) throws Exception {
		if (!MyApplication.isHasNetwork()) {
			return CodeValidator.getNoNetworkCodeResult();
		}
		HttpParams httpParams = new BasicHttpParams();
		// 连接超时
		httpParams.setParameter("http.connection.timeout", 60000);
		// 请求超时
		httpParams.setParameter("http.socket.timeout", 60000);
		httpParams.setParameter("http.protocol.content-charset", CHARSET);
		httpParams.setBooleanParameter("http.connection.stalecheck", false);
		HttpProtocolParams.setContentCharset(httpParams, "UTF-8");
		HttpProtocolParams.setHttpElementCharset(httpParams, "UTF-8");

		DefaultHttpClient httpClient = new DefaultHttpClient(httpParams);
		HttpPost postRequest = new HttpPost(requestUrl);
		MultipartEntity reqEntity = new MultipartEntity();

		postRequest.addHeader("Cookie", MyApplication.getInstance().getSessionId());

		// // 上传的表单参数部分
		if (params != null)
			for (NameValuePair nameValue : params) {
				reqEntity.addPart(nameValue.getName(),
						new StringBody(nameValue.getValue(), Charset.forName("UTF-8")));
			}

		// if (filePath == null || filePath.equals(""))
		// return "图片不能为空";
		if (filePath != null && !filePath.equals("")) {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ByteArrayBody bab = null;
			// 先压缩图片再上传
			try {
				bos = BitmapUtils.getCompressBitmap(filePath, 500, 500, 50);
				byte[] data = bos.toByteArray();
				bab = new ByteArrayBody(data, filePath.substring(filePath.lastIndexOf("/") + 1));
				reqEntity.addPart("file", bab);
				bos.reset();// 清空
				bos.flush();
			} catch (Exception e) {
				reqEntity.addPart("file", new StringBody("image error"));
			}
			bos.close();
		}
		
		if (backFilePath != null && !backFilePath.equals("")) {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ByteArrayBody bab = null;
			// 先压缩图片再上传
			try {
				bos = BitmapUtils.getCompressBitmap(backFilePath, 500, 500, 50);
				byte[] data = bos.toByteArray();
				bab = new ByteArrayBody(data, backFilePath.substring(backFilePath.lastIndexOf("/") + 1));
				reqEntity.addPart("backfile", bab);
				bos.reset();// 清空
				bos.flush();
			} catch (Exception e) {
				reqEntity.addPart("backfile", new StringBody("image error"));
			}
			bos.close();
		}

		postRequest.setEntity(reqEntity);
		String result = null;
		try {
			// 获取HttpResponse实例
			HttpResponse response = httpClient.execute(postRequest);
			// 判断是够请求成功
			if (response.getStatusLine().getStatusCode() == 200) {
				// 获取返回的数据
				result = EntityUtils.toString(response.getEntity(), CHARSET);
				Log.d(MyApplication.LOG_TAG, "HttpPost方式请求成功，返回数据如下：");
			} else {
				Log.d(MyApplication.LOG_TAG, "HttpPost方式请求失败");
				result = "{\"code\":100000}";
			}

		} catch (ConnectTimeoutException e) {
			result = "请求超时";
		} catch (ParseException e) {
			result = "转化异常";
		} catch (IOException e) {
			result = "IO异常";
		} catch (Exception e) {
			result = "上传出现异常！";
		} finally {
			if (httpClient != null) {
				httpClient.getConnectionManager().shutdown();
				httpClient = null;
			}
		}

		return result;
	}

	public String readFileSdcardFile(String fileName) throws IOException {
		String res = "";
		try {
			FileInputStream fin = new FileInputStream(fileName);
			int length = fin.available();
			byte[] buffer = new byte[length];
			fin.read(buffer);
			res = EncodingUtils.getString(buffer, "UTF-8");
			fin.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}
}