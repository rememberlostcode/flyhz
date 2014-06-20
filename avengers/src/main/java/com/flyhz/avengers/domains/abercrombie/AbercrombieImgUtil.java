
package com.flyhz.avengers.domains.abercrombie;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.lang.StringUtils;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * 处理AbercrombieImg
 * 
 * @author fuwb
 */
public class AbercrombieImgUtil {
	/**
	 * 
	 * @param colorImgUrl
	 *            混合颜色图片访问路径
	 * @param colorNames
	 *            颜色名称集合
	 * @param colorPrefix
	 *            切割后单个颜色图片保存路径前缀
	 * @return
	 */
	public static void cutColorImg(String colorImgUrl, String colorPrefix, List<String> colorNames) {
		if (StringUtils.isNotBlank(colorImgUrl) && StringUtils.isNotBlank(colorPrefix)
				&& colorNames != null && colorNames.size() > 1) {
			try {
				// 获取混合颜色图片
				URL url = new URL(colorImgUrl);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setRequestMethod("GET");
				conn.setConnectTimeout(10 * 1000);
				InputStream inStream = conn.getInputStream();
				byte[] data = readInputStream(inStream);
				// 获取混合颜色图片名称
				StringBuffer colorBuffer = new StringBuffer();
				String fileName = colorImgUrl.substring(colorImgUrl.lastIndexOf("/") + 1,
						colorImgUrl.lastIndexOf("?"));
				File imageFile = new File(colorBuffer.append(colorPrefix).append(File.separator)
														.append(fileName).append(".jpg").toString());
				// 文件所在目录是否存在，不存在则创建目录
				if (!imageFile.getParentFile().exists()) {
					imageFile.getParentFile().mkdirs();
				}
				// 文件不存在，则写入图片文件
				if (!imageFile.exists()) {
					FileOutputStream outStream = new FileOutputStream(imageFile);
					outStream.write(data);
					outStream.close();
				}
				// 切割图片
				BufferedImage image = ImageIO.read(imageFile);
				int width = image.getWidth();
				int height = image.getHeight();
				int cutX = image.getMinX();
				int cutY = image.getMinY();
				int perHeight = height / colorNames.size();
				for (String colorName : colorNames) {
					colorBuffer.setLength(0);
					File colorFile = new File(colorBuffer.append(colorPrefix)
															.append(File.separator)
															.append(colorName).append(".jpg")
															.toString());
					if (colorFile.exists()) {
						continue;
					}
					BufferedImage colorImage = image.getSubimage(cutX, cutY, width, perHeight);
					ImageIO.write(colorImage, "JPG", colorFile);
					cutY += perHeight;
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 文件转换为byte数组
	 * 
	 * @param fileFullPath
	 * @return byte[]
	 */
	public static byte[] getBytesFromFile(String fileFullPath) {
		byte[] buffer = null;
		try {
			File file = new File(fileFullPath);
			if (file.exists()) {
				FileInputStream fis = new FileInputStream(file);
				ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
				byte[] b = new byte[1000];
				int n;
				while ((n = fis.read(b)) != -1) {
					bos.write(b, 0, n);
				}
				fis.close();
				bos.close();
				buffer = bos.toByteArray();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return buffer;
	}

	/**
	 * 文件转换为byte数组
	 * 
	 * @param file
	 * @return byte[]
	 */
	public static byte[] getBytesFromFile(File file) {
		byte[] buffer = null;
		try {
			if (file.exists()) {
				FileInputStream fis = new FileInputStream(file);
				ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
				byte[] b = new byte[1000];
				int n;
				while ((n = fis.read(b)) != -1) {
					bos.write(b, 0, n);
				}
				fis.close();
				bos.close();
				buffer = bos.toByteArray();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return buffer;
	}

	/**
	 * byte数组转换为文件
	 * 
	 * @param bfile
	 * @param fileFullPath
	 * @return
	 */
	public static void getFileFromBytes(byte[] bfile, String fileFullPath) {
		BufferedOutputStream bos = null;
		FileOutputStream fos = null;
		File file = new File(fileFullPath);
		try {
			// 判断文件目录是否存在
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
			fos = new FileOutputStream(file);
			bos = new BufferedOutputStream(fos);
			bos.write(bfile);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (bos != null) {
				try {
					bos.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	/**
	 * 获取产品全部颜色名称
	 * 
	 * @param elements
	 * @return List<String>
	 */
	public static List<String> getImgColorNames(Elements elements) {
		List<String> colorNames = null;
		if (elements != null && !elements.isEmpty()) {
			colorNames = new ArrayList<String>();
			for (Element element : elements) {
				String colorName = element.text();
				if (StringUtils.isNotBlank(colorName)) {
					colorNames.add(colorName);
				}
			}
		}
		return colorNames;
	}

	private static byte[] readInputStream(InputStream inStream) throws Exception {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		while ((len = inStream.read(buffer)) != -1) {
			outStream.write(buffer, 0, len);
		}
		inStream.close();
		return outStream.toByteArray();
	}

	public static void main(String[] args) {
		String colorImgUrl = "http://anf.scene7.com/is/image/anf/anf_74549_sw56x32?$productPageSwatch$";
		String colorPrefix = "D:\\oklfile\\abercrombie\\color";
		List<String> colorNames = new ArrayList<String>();
		colorNames.add("HEATHER GREY");
		colorNames.add("NAVY");
		colorNames.add("BLUE");
		colorNames.add("DARK GREEN");
		colorNames.add("GREEN");
		colorNames.add("PINK");
		colorNames.add("RED");
		colorNames.add("WHITE");
		colorNames.add("LIGHT PINK");
		cutColorImg(colorImgUrl, colorPrefix, colorNames);
	}
}
