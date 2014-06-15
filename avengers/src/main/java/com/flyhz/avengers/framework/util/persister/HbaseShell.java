
package com.flyhz.avengers.framework.util.persister;

import java.io.IOException;
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
import org.apache.hadoop.hbase.client.HConnection;
import org.apache.hadoop.hbase.client.HConnectionManager;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HbaseShell {
	private static Logger	LOG		= LoggerFactory.getLogger(HbaseShell.class);
	private Configuration	conf	= null;

	private String			hbaseZookeeperQuorum;

	private String			hbaseZookeeperPropertyClientPort;

	public HbaseShell() {
		conf = HBaseConfiguration.create();
		conf.set("hbase.zookeeper.quorum", hbaseZookeeperQuorum);
		conf.set("hbase.zookeeper.property.clientPort", hbaseZookeeperPropertyClientPort);
	}

	public HBaseAdmin getHBaseAdmin() throws IOException {
		HConnection connection = HConnectionManager.createConnection(conf);
		HBaseAdmin hbaseAdmin = new HBaseAdmin(connection);
		return hbaseAdmin;
	}

	public boolean isTableExists(String tableName) throws IOException {
		HBaseAdmin hAdmin = null;
		try {
			hAdmin = getHBaseAdmin();
			return hAdmin.tableExists(tableName);
		} finally {
			if (hAdmin != null) {
				hAdmin.close();
			}

		}
	}

	// 创建数据库表
	protected boolean createTable(String tableName, String[] columnFamilys) throws IOException {
		HBaseAdmin hAdmin = null;
		try {
			hAdmin = getHBaseAdmin();
			if (hAdmin.tableExists(tableName)) {
				LOG.info("{}表已经存在", tableName);
				return false;
			} else {
				// 新建一个 scores 表的描述
				TableName name = TableName.valueOf(tableName);
				// TableSchema ts = TableSchema.;
				HTableDescriptor tableDesc = new HTableDescriptor(tableName);
				// 在描述里添加列族
				for (String columnFamily : columnFamilys) {
					tableDesc.addFamily(new HColumnDescriptor(columnFamily));
				}
				// 根据配置好的描述建表
				hAdmin.createTable(tableDesc);
				return true;
			}
		} finally {
			if (hAdmin != null) {
				hAdmin.close();
			}

		}
	}

	// 删除数据库表
	protected boolean deleteTable(String tableName) throws IOException {
		HBaseAdmin hAdmin = null;
		try {
			hAdmin = getHBaseAdmin();
			if (hAdmin.tableExists(tableName)) {
				// 关闭一个表
				hAdmin.disableTable(tableName);
				// 删除一个表
				hAdmin.deleteTable(tableName);
				LOG.info("table {} 删除表成功", tableName);
				return true;
			} else {
				LOG.info("table {} 表不存在，删除失败！", tableName);
				return false;
			}
		} finally {
			if (hAdmin != null) {
				hAdmin.close();
			}
		}
	}

	// 添加一条数据
	public void addRow(String tableName, String row, String columnFamily, String column,
			String value) throws IOException {
		HTable table = null;
		try {
			table = new HTable(conf, tableName);
			Put put = new Put(Bytes.toBytes(row));
			// 参数出分别：列族、列、值
			put.add(Bytes.toBytes(columnFamily), Bytes.toBytes(column), Bytes.toBytes(value));
			table.put(put);
		} finally {
			if (table != null) {
				table.close();
			}
		}
	}

	// 删除一条数据
	public void delRow(String tableName, String row) throws IOException {
		HTable table = null;
		try {
			table = new HTable(conf, tableName);
			Delete del = new Delete(Bytes.toBytes(row));
			table.delete(del);
		} finally {
			table.close();
		}
	}

	// 删除多条数据
	public void delMultiRows(String tableName, String[] rows) throws Exception {
		HTable table = null;
		try {
			table = new HTable(conf, tableName);
			List<Delete> list = new ArrayList<Delete>();
			for (String row : rows) {
				Delete del = new Delete(Bytes.toBytes(row));
				list.add(del);
			}
			table.delete(list);
		} finally {
			if (table != null) {
				table.close();
			}
		}
	}

	// get row
	public void getRow(String tableName, String row) throws Exception {
		HTable table = null;
		try {
			table = new HTable(conf, tableName);
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
		} finally {
			table.close();
		}
	}

	// get all records
	public void getAllRows(String tableName) throws Exception {
		HTable table = null;
		try {
			table = new HTable(conf, tableName);
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
		} finally {
			table.close();
		}
	}
}
