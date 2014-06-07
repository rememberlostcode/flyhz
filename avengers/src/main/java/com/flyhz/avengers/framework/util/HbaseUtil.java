
package com.flyhz.avengers.framework.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HbaseUtil {
	private static Logger			LOG		= LoggerFactory.getLogger(HbaseUtil.class);
	private static Configuration	conf	= null;
	static {
		conf = HBaseConfiguration.create();
		conf.set("hbase.zookeeper.quorum", "m1,s1,s2");
		conf.set("hbase.zookeeper.property.clientPort", "2181");
	}

	// 创建数据库表
	@SuppressWarnings("resource")
	public static void createTable(String tableName, String[] columnFamilys) throws Exception {
		// 新建一个数据库管理员
		HBaseAdmin hAdmin = new HBaseAdmin(conf);
		if (hAdmin.tableExists(tableName)) {
			LOG.error("{}表已经存在", tableName);
			System.exit(0);
		} else {
			// 新建一个 scores 表的描述
			TableName name = TableName.valueOf(tableName);
			HTableDescriptor tableDesc = new HTableDescriptor(name);
			// 在描述里添加列族
			for (String columnFamily : columnFamilys) {
				tableDesc.addFamily(new HColumnDescriptor(columnFamily));
			}
			// 根据配置好的描述建表
			hAdmin.createTable(tableDesc);
			System.out.println("创建表成功");
		}
	}

	// 删除数据库表
	public static void deleteTable(String tableName) throws Exception {
		// 新建一个数据库管理员
		HBaseAdmin hAdmin = new HBaseAdmin(conf);

		if (hAdmin.tableExists(tableName)) {
			// 关闭一个表
			hAdmin.disableTable(tableName);
			// 删除一个表
			hAdmin.deleteTable(tableName);
			System.out.println("删除表成功");

		} else {
			System.out.println("删除的表不存在");
			System.exit(0);
		}
	}

	// 添加一条数据
	public static void addRow(String tableName, String row, String columnFamily, String column,
			String value) throws Exception {
		HTable table = new HTable(conf, tableName);
		Put put = new Put(Bytes.toBytes(row));
		// 参数出分别：列族、列、值
		put.add(Bytes.toBytes(columnFamily), Bytes.toBytes(column), Bytes.toBytes(value));
		table.put(put);
	}

	// 删除一条数据
	public static void delRow(String tableName, String row) throws Exception {
		HTable table = new HTable(conf, tableName);
		Delete del = new Delete(Bytes.toBytes(row));
		table.delete(del);
	}

	// 删除多条数据
	public static void delMultiRows(String tableName, String[] rows) throws Exception {
		HTable table = new HTable(conf, tableName);
		List<Delete> list = new ArrayList<Delete>();

		for (String row : rows) {
			Delete del = new Delete(Bytes.toBytes(row));
			list.add(del);
		}

		table.delete(list);
	}

	// get row
	public static void getRow(String tableName, String row) throws Exception {
		HTable table = new HTable(conf, tableName);
		Get get = new Get(Bytes.toBytes(row));
		Result result = table.get(get);
		// 输出结果
		for (Cell cell : result.rawCells()) {
			System.out.print("Row Name: " + new String(cell.getRowArray()) + " ");
			System.out.print("Timestamp: " + cell.getTimestamp() + " ");
			System.out.print("column Family: " + new String(cell.getFamilyArray()) + " ");
			System.out.print("Row Name:  " + new String(cell.getQualifierArray()) + " ");
			System.out.println("Value: " + new String(cell.getValueArray()) + " ");
		}
	}

	// get all records
	public static void getAllRows(String tableName) throws Exception {
		HTable table = new HTable(conf, tableName);
		Scan scan = new Scan();
		ResultScanner results = table.getScanner(scan);
		// 输出结果
		for (Result result : results) {
			for (Cell cell : result.rawCells()) {
				System.out.print("Row Name: " + new String(cell.getRowArray()) + " ");
				System.out.print("Timestamp: " + cell.getTimestamp() + " ");
				System.out.print("column Family: " + new String(cell.getFamilyArray()) + " ");
				System.out.print("Row Name:  " + new String(cell.getQualifierArray()) + " ");
				System.out.println("Value: " + new String(cell.getValueArray()) + " ");
			}
		}
	}

	public static void main(String[] args) {
		try {
			String tablename = "scores";
			String[] familys = { "grade", "course" };
			HbaseUtil.deleteTable(tablename);
			HbaseUtil.createTable(tablename, familys);

			// add record zkb
			HbaseUtil.addRow(tablename, "zkb", "grade", "", "5");
			HbaseUtil.addRow(tablename, "zkb", "course", "", "90");
			HbaseUtil.addRow(tablename, "zkb", "course", "math", "97");
			HbaseUtil.addRow(tablename, "zkb", "course", "art", "87");
			// add record baoniu
			HbaseUtil.addRow(tablename, "baoniu", "grade", "", "4");
			HbaseUtil.addRow(tablename, "baoniu", "course", "math", "89");

			System.out.println("===========get one record========");
			HbaseUtil.getRow(tablename, "zkb");

			System.out.println("===========show all record========");
			HbaseUtil.getAllRows(tablename);

			System.out.println("===========del one record========");
			HbaseUtil.delRow(tablename, "baoniu");
			HbaseUtil.getAllRows(tablename);

			System.out.println("===========show all record========");
			HbaseUtil.getAllRows(tablename);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
