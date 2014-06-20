
package com.flyhz.avengers.domains.abercrombie.template;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HConnection;
import org.apache.hadoop.hbase.client.HConnectionManager;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flyhz.avengers.domains.abercrombie.AbercrombieEncodeUtil;
import com.flyhz.avengers.domains.abercrombie.AbercrombieImgUtil;
import com.flyhz.avengers.framework.Analyze;
import com.flyhz.avengers.framework.Template;
import com.flyhz.avengers.framework.util.Constants;
import com.flyhz.avengers.framework.util.WebClientUtil;

public class AbercrombieProductTemplateImpl implements Template {
	private static final Logger	LOG	= LoggerFactory.getLogger(AbercrombieProductTemplateImpl.class);

	@Override
	public void apply(Map<String, Object> context) {
		try {
			LOG.info("abercrombie template begin..............");
			String analyzeUrl = (String) context.get(Analyze.ANALYZE_URL);
			String encode = AbercrombieEncodeUtil.getAnalyzeUrlEncode(context);
			String brand = "abercrombie";// 产品品牌
			StringBuffer imageBuffer = new StringBuffer();
			String prefix = imageBuffer.append(Constants.IMAGE_PREFIX_PATH).append(File.separator)
										.append(brand).toString();// 产品图片保存路径前缀
			LOG.info("init hbase begin..............");
			Configuration hconf = HBaseConfiguration.create();
			hconf.set("hbase.zookeeper.quorum", "m1,s1,s2");
			hconf.set("hbase.zookeeper.property.clientPort", "2181");
			HConnection hConnection = HConnectionManager.createConnection(hconf);
			HBaseAdmin hbaseAdmin = new HBaseAdmin(hConnection);
			// 判断av_page是否存在
			if (!hbaseAdmin.tableExists("av_page")) {
				HTableDescriptor tableDesc = new HTableDescriptor(TableName.valueOf("av_page"));
				// 插入info列族
				HColumnDescriptor columnConfInfo = new HColumnDescriptor("info");
				tableDesc.addFamily(columnConfInfo);
				// 插入preference列族
				HColumnDescriptor columnConfPreference = new HColumnDescriptor("preference");
				tableDesc.addFamily(columnConfPreference);
				hbaseAdmin.createTable(tableDesc);
			}
			// 判断av_product是否存在
			if (!hbaseAdmin.tableExists("av_product")) {
				HTableDescriptor tableDesc = new HTableDescriptor(TableName.valueOf("av_product"));
				// 插入info列族
				HColumnDescriptor columnConfInfo = new HColumnDescriptor("info");
				tableDesc.addFamily(columnConfInfo);
				// 插入preference列族
				HColumnDescriptor columnConfPreference = new HColumnDescriptor("preference");
				tableDesc.addFamily(columnConfPreference);
				hbaseAdmin.createTable(tableDesc);
			}
			LOG.info("init hbase end..............");
			// 分析HTML数据，插入HBase数据库
			String htmlDoc = null;
			HTable table = null;
			HTable tableProduct = null;
			try {
				// 刷新HBase表
				hbaseAdmin.flush("av_page");
				hbaseAdmin.flush("av_product");
				// 获取待分析HTML数据
				table = new HTable(hconf, "av_page");
				Get get = new Get(Bytes.toBytes(analyzeUrl));
				get.addFamily(Bytes.toBytes("info"));
				get.addColumn(Bytes.toBytes("info"), Bytes.toBytes("response"));
				Result result = table.get(get);
				for (Cell cell : result.rawCells()) {
					htmlDoc = new String(cell.getValueArray());
				}
				LOG.info(htmlDoc);
				// 待分析HTML数据为空,URL请求获得response数据
				if (StringUtils.isBlank(htmlDoc)) {
					URL url = new URL(analyzeUrl);
					HttpURLConnection connection = (HttpURLConnection) url.openConnection();
					connection.setRequestMethod("GET");
					connection.setRequestProperty("accept", "*/*");
					connection.setRequestProperty("connection", "Keep-Alive");
					connection.setRequestProperty("user-agent",
							"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
					connection.setRequestProperty("Charset", encode);
					connection.connect();
					// 定义BufferedReader输入流来读取URL的响应
					BufferedReader in;
					in = new BufferedReader(new InputStreamReader(connection.getInputStream(),
							Charset.forName(encode)));
					StringBuffer sb = new StringBuffer();
					String line;
					while ((line = in.readLine()) != null) {
						sb.append(line);
					}
					htmlDoc = sb.toString();
					// 插入av_page表
					Put put = new Put(Bytes.toBytes(analyzeUrl));
					put.add(Bytes.toBytes("info"), Bytes.toBytes("response"),
							Bytes.toBytes(sb.toString()));
					table.put(put);
				}
				// 解析HTML获得所需要的产品数据
				htmlDoc = WebClientUtil.getContent(analyzeUrl, false, true);
				Document doc = Jsoup.parse(htmlDoc);
				String productId = doc.select("input[name=productId]").first().val();// 产品ID
				String collection = doc.select("input[name=collection]").first().val();// 款号ID
				String cseq = doc.select("input[name=cseq]").first().val();// 颜色seq
				// 定义添加产品的put
				tableProduct = new HTable(hconf, "av_product");
				Put putProduct = new Put(Bytes.toBytes(productId + cseq));
				// 获取产品名称
				String name = doc.select("input[name=name]").first().val();
				putProduct.add(Bytes.toBytes("info"), Bytes.toBytes("name"), Bytes.toBytes(name));
				// 获取产品描述
				String description = doc.select(".copy").first().text();
				putProduct.add(Bytes.toBytes("info"), Bytes.toBytes("description"),
						Bytes.toBytes(description));
				// 获取品牌名
				putProduct.add(Bytes.toBytes("info"), Bytes.toBytes("brand"), Bytes.toBytes(brand));
				// 获取分类名称
				String category = doc.select("#breadcrumb a").last().text();
				putProduct.add(Bytes.toBytes("info"), Bytes.toBytes("category"),
						Bytes.toBytes(category));
				// 获取款号
				putProduct.add(Bytes.toBytes("info"), Bytes.toBytes("style"),
						Bytes.toBytes(collection + cseq));
				// 获取原始价格
				String originalPrice = doc.select(".list-price").first().text();
				originalPrice = originalPrice.substring(7);
				putProduct.add(Bytes.toBytes("info"), Bytes.toBytes("original_price"),
						Bytes.toBytes(originalPrice));
				// 获取当前价格
				String presentPrice = doc.select("input[name=price]").first().val();
				putProduct.add(Bytes.toBytes("info"), Bytes.toBytes("present_price"),
						Bytes.toBytes(presentPrice));
				// 获取颜色
				String color = doc.select("input[name=color]").first().val();
				putProduct.add(Bytes.toBytes("info"), Bytes.toBytes("color"), Bytes.toBytes(color));
				// 获取颜色图片
				imageBuffer.setLength(0);
				imageBuffer.append(prefix).append(File.separator).append("color")
							.append(File.separator).append(color).append(".jpg");
				File colorFile = new File(imageBuffer.toString());
				// 颜色文件不存在，切割处理颜色文件
				if (!colorFile.exists()) {
					Elements elements = doc.select("a.swatch-link");
					if (elements != null) {
						// 获得混合颜色图片URL
						imageBuffer.setLength(0);
						String style = elements.first().attr("style");
						style = style.substring(style.indexOf("/"), style.lastIndexOf("'"));
						String colorImgUrl = imageBuffer.append("http:").append(style).toString();
						// 定义颜色前缀
						imageBuffer.setLength(0);
						String colorPrefix = imageBuffer.append(prefix).append(File.separator)
														.append("color").toString();
						// 获得颜色名称列表
						Elements colorElements = doc.select(".product-info .swatches .swatch-link span");
						AbercrombieImgUtil.cutColorImg(colorImgUrl, colorPrefix,
								AbercrombieImgUtil.getImgColorNames(colorElements));
					}
				}
				// 颜色图片转换为bytes数组插入product
				if (colorFile.exists()) {
					putProduct.add(Bytes.toBytes("info"), Bytes.toBytes("color_img"),
							AbercrombieImgUtil.getBytesFromFile(colorFile));
				}
				// 获取产品图片
				Elements productImgs = doc.select("ul.thumbnails > li");
				if (productImgs != null && !productImgs.isEmpty()) {
					List<byte[]> pimgBytes = new ArrayList<byte[]>();
					for (Element element : productImgs) {
						imageBuffer.setLength(0);
						String productImgUrl = imageBuffer.append("http:")
															.append(element.attr("data-image-name"))
															.toString();
						productImgUrl = productImgUrl.replace("productThumbnail", "productMain");
						pimgBytes.add(AbercrombieImgUtil.getBytesFromWebUrl(productImgUrl));
					}
					putProduct.add(Bytes.toBytes("info"), Bytes.toBytes("product_img"),
							Bytes.toBytes((ByteBuffer) pimgBytes));
				}
				// 获取产品访问URL
				putProduct.add(Bytes.toBytes("preference"), Bytes.toBytes("url"),
						Bytes.toBytes(analyzeUrl));
				// 获取版本号
				LOG.info("abercrombie template end..............");
			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {
				if (table != null) {
					table.close();
				}
				if (tableProduct != null) {
					tableProduct.close();
				}
				if (hbaseAdmin != null) {
					hbaseAdmin.close();
				}
				if (hConnection != null) {
					hConnection.close();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
