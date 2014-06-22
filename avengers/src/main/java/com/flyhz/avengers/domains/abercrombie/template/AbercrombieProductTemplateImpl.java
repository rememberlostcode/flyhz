
package com.flyhz.avengers.domains.abercrombie.template;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flyhz.avengers.framework.Analyze;
import com.flyhz.avengers.framework.Template;
import com.flyhz.avengers.framework.config.XConfiguration;

public class AbercrombieProductTemplateImpl implements Template {
	private static final Logger	LOG		= LoggerFactory.getLogger(AbercrombieProductTemplateImpl.class);
	private static final String	ENCODE	= "UTF-8";

	@Override
	public void apply(Map<String, Object> context) {
		try {
			LOG.info("abercrombie template begin..............");
			String analyzeUrl = (String) context.get(Analyze.ANALYZE_URL);
			String encode = getEncode(context);
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
				Document doc = Jsoup.parse(htmlDoc);
				String brand = "abercrombie";// 品牌
				String productId = doc.select("input[name=productId]").first().val();// 产品ID
				String collection = doc.select("input[name=collection]").first().val();// 款号
				String cseq = doc.select("input[name=cseq]").first().val();// 不同颜色seq
				// 定义添加产品的put
				tableProduct = new HTable(hconf, "av_product");
				Put putProduct = new Put(Bytes.toBytes(productId + cseq));
				// 获取产品名称
				String name = doc.select("input[name=name]").first().val();// 产品
				putProduct.add(Bytes.toBytes("info"), Bytes.toBytes("name"), Bytes.toBytes(name));
				System.out.println(name);
				// 获取产品描述
				String description = doc.select(".copy").first().text();
				putProduct.add(Bytes.toBytes("info"), Bytes.toBytes("description"),
						Bytes.toBytes(description));
				System.out.println(description);
				// 获取品牌名
				putProduct.add(Bytes.toBytes("info"), Bytes.toBytes("brand"), Bytes.toBytes(brand));
				System.out.println(brand);
				// 获取分类名称
				String category = doc.select("a[data-categoryId=13051]").first().text();
				putProduct.add(Bytes.toBytes("info"), Bytes.toBytes("category"),
						Bytes.toBytes(category));
				System.out.println(category);
				// 获取款号
				// Element element =
				// doc.select("li.selected").first().children().first();
				// String style = doc.select(".collection").first().text() +
				// element.attr("data-seq");
				putProduct.add(Bytes.toBytes("info"), Bytes.toBytes("style"),
						Bytes.toBytes(collection + cseq));
				System.out.println(collection + cseq);
				// 获取原始价格
				String originalPrice = doc.select(".list-price").first().text();
				originalPrice = originalPrice.substring(7);
				putProduct.add(Bytes.toBytes("info"), Bytes.toBytes("original_price"),
						Bytes.toBytes(originalPrice));
				System.out.println(originalPrice);
				// 获取当前价格
				// String presentPrice =
				// doc.select(".offer-price").first().text();
				// presentPrice = presentPrice.substring(3);
				String presentPrice = doc.select("input[name=price]").first().val();
				putProduct.add(Bytes.toBytes("info"), Bytes.toBytes("present_price"),
						Bytes.toBytes(presentPrice));
				System.out.println(presentPrice);
				// 获取颜色
				// String color =
				// doc.select("li.selected a span").first().text();
				String color = doc.select("input[name=color]").first().val();
				putProduct.add(Bytes.toBytes("info"), Bytes.toBytes("color"), Bytes.toBytes(color));
				System.out.println(color);
				try {
					// 获取颜色图片
					StringBuffer colorImgBuffer = new StringBuffer();
					colorImgBuffer.append(File.separator).append(brand).append(File.separator)
									.append("color").append(File.separator).append(color)
									.append(".jpg");
					File file = new File(colorImgBuffer.toString());
					// 颜色文件不存在，切割处理颜色文件
					if (!file.exists()) {
						String colorImgUrl = doc.select("a.swatch-link").first().attr("style");
					}
					// 颜色图片转换为bytes数组插入product
					FileInputStream fis = new FileInputStream(file);
					ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
					byte[] b = new byte[1000];
					int n;
					while ((n = fis.read(b)) != -1) {
						bos.write(b, 0, n);
					}
					fis.close();
					bos.close();
					byte[] colorImgBytes = bos.toByteArray();
					putProduct.add(Bytes.toBytes("info"), Bytes.toBytes("color_img"), colorImgBytes);
					// 获取产品图片
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				// 获取产品图片
				// 获取产品访问URL
				System.out.println(analyzeUrl);
				// 获取版本号
				// 产品数据插入HBase product表
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

	@SuppressWarnings("unchecked")
	private String getEncode(Map<String, Object> context) {
		if (context != null && !context.isEmpty()) {
			String analyzeUrl = (String) context.get(Analyze.ANALYZE_URL);
			Map<String, Object> domains = (Map<String, Object>) context.get(XConfiguration.AVENGERS_DOMAINS);
			// 判断参数URL属于哪个domain
			if (domains != null && !domains.isEmpty()) {
				Set<String> domainRoots = domains.keySet();
				for (String domainRoot : domainRoots) {
					if (analyzeUrl.indexOf(domainRoot) > -1) {
						// 获取匹配domain的fetchEvents
						Map<String, Object> domain = (Map<String, Object>) domains.get(domainRoot);
						if (domain != null && domain.get(XConfiguration.ENCODING) != null) {
							return (String) domain.get(XConfiguration.ENCODING);
						}
						break;
					}
				}
			}
		}
		return ENCODE;
	}

	public static void main(String[] args) {
		try {
			File file = new File("D:\\myfile\\smile\\anf_74549_sw56x32.jpg");
			BufferedImage image = ImageIO.read(file);
			// int srcWidth = image.getWidth();
			// int srcHeight = image.getHeight();
			System.out.println(image.getMinX());
			System.out.println(image.getMinY());
			System.out.println(image.getWidth());
			System.out.println(image.getHeight());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
