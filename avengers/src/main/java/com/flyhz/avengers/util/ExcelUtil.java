
package com.flyhz.avengers.util;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.util.Assert;

/**
 * 文件导出 util
 */
public class ExcelUtil {

	public final static Integer	EXCEL_97_MAX_ROWNUM	= 65535;

	/**
	 * @param objects
	 *            : 要插入的数据集
	 * @param columnTitles
	 *            : 表格的列填充标题集
	 * @param propertyList
	 *            : 对象的属性集，属性和列标题一一对应
	 * @param sheetTitle
	 *            : 表格标题名
	 * @param outputStream
	 *            : 输出流
	 */
	public static Workbook generateExcel(List<?> objects, String[] columnTitles,
			String[] propertyList, String sheetTitle) {
		Workbook workbook = new HSSFWorkbook();// 创建Excel文件
		Sheet sheet = workbook.createSheet(sheetTitle);// 创建标题为sheetTitle的表格
		// 生成表格的列标题
		generateColumnTitle(sheet, columnTitles);
		// 循环导入数据
		int i = 0;
		Map<String, Method> cellExecuteMethodMap = getPropertyMethodMap(objects.get(0),
				propertyList);
		for (Object object : objects) {
			if (i > EXCEL_97_MAX_ROWNUM) {
				sheet = workbook.createSheet(sheetTitle);
				generateColumnTitle(sheet, columnTitles);
				i = 0;
			}
			Row row = sheet.createRow(++i);
			try {
				insertRowData(row, propertyList, object, cellExecuteMethodMap);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		return workbook;
	}

	/**
	 * 生成表格的列标题
	 * 
	 * @param sheet
	 *            : 表格
	 * @param columnTitles
	 *            : 表格的列填充标题集
	 */
	public static void generateColumnTitle(Sheet sheet, String[] columnTitles) {
		int i = 0;
		Row row = sheet.createRow((short) 0);
		for (String columnTitle : columnTitles) {
			row.createCell(i).setCellValue(columnTitle);
			i++;
		}
	}

	/**
	 * 表格中插入一条数据
	 * 
	 * @param row
	 *            : 行
	 * @param propertyList
	 *            : 对象的属性集
	 * @param object
	 *            : 对象
	 * @param cellExecuteMethodMap
	 *            : <propertyName(属性名),getMethodName(get方法名)>的Map集
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 */
	public static void insertRowData(Row row, String[] propertyList, Object object,
			Map<String, Method> cellExecuteMethodMap) throws IllegalArgumentException,
			IllegalAccessException, InvocationTargetException {
		int i = 0;
		for (String property : propertyList) {
			Method method = cellExecuteMethodMap.get(property);
			if (method != null) {
				Object propertyValue = method.invoke(object);
				if (propertyValue != null) {
					Class<?> propertyType = propertyValue.getClass();
					// Integer/int类型数据
					if (Integer.class.equals(propertyType) || int.class.equals(propertyType)) {
						row.createCell(i++).setCellValue(String.valueOf(propertyValue));
						// Long/long类型数据
					} else if (Long.class.equals(propertyType) || long.class.equals(propertyType)) {
						row.createCell(i++).setCellValue(String.valueOf(propertyValue));
						// Double/double类型数据
					} else if (Double.class.equals(propertyType)
							|| double.class.equals(propertyType)) {
						row.createCell(i++).setCellValue((Double) propertyValue);
						// Boolean/boolean类型数据
					} else if (Boolean.class.equals(propertyType)
							|| boolean.class.equals(propertyType)) {
						row.createCell(i++).setCellValue(String.valueOf(propertyValue));
						// Date类型数据
					} else if (Date.class.equals(propertyType)) {
						row.createCell(i++).setCellValue((Date) propertyValue);
						// Calendar类型数据
					} else if (Calendar.class.equals(propertyType)) {
						row.createCell(i++).setCellValue((Calendar) propertyValue);
						// BigDecimal类型数据
					} else if (BigDecimal.class.equals(propertyType)) {
						row.createCell(i++).setCellValue(String.valueOf(propertyValue));
					} else {
						row.createCell(i++).setCellValue(String.valueOf(propertyValue));
					}
				} else {
					row.createCell(i++).setCellValue("");
				}
			}
		}
	}

	/**
	 * 获得对象object的<propertyName(属性名),getMethodName(get方法名)>的Map集
	 * 
	 * @param object
	 *            : 对象
	 * @param propertyList
	 *            : 对象的属性集
	 */
	public static Map<String, Method> getPropertyMethodMap(Object object, String[] propertyList) {
		Map<String, Method> cellExecuteMethodMap = new HashMap<String, Method>();
		Class<?> clazz = object.getClass();// 获得对象的类型
		Field[] fields = clazz.getDeclaredFields();// 获得对象的全部的声明属性
		Method[] methods = clazz.getDeclaredMethods();// 获得对象的全部的声明方法
		for (String propertyName : propertyList) {
			for (Field field : fields) {
				if (field.getName().equals(propertyName)) {
					Class<?> fieldType = field.getType();
					String getMethodName = "";
					if (boolean.class.equals(fieldType) || Boolean.class.equals(fieldType)) {
						getMethodName = "is" + StringUtil.firstLetterToUpperCase(propertyName);
					} else {
						getMethodName = "get" + StringUtil.firstLetterToUpperCase(propertyName);
					}
					for (Method method : methods) {
						if (method.getName().equals(getMethodName)) {
							cellExecuteMethodMap.put(propertyName, method);
						}
					}
				}
			}
		}
		return cellExecuteMethodMap;
	}

	/**
	 * 获取对象的属性值的返回值类型
	 * 
	 * @param object
	 *            : 实体对象
	 * @param propertyName
	 *            : 属性名
	 */
	public static Class<?> getReturnType(Object object, String propertyName) {
		Assert.notNull(object);
		Assert.hasText(propertyName);
		try {
			PropertyDescriptor descriptor = new PropertyDescriptor(propertyName, object.getClass());
			Method method = descriptor.getReadMethod();
			return method.getReturnType();
		} catch (IntrospectionException e) {
			e.printStackTrace();
			return Object.class;
		}
	}

	/**
	 * 获取对象的属性值，忽略public、protected修饰符的限制
	 * 
	 * @param object
	 *            : 实体对象
	 * @param propertyName
	 *            : 属性名
	 */
	public static Object getPropertyValue(Object object, String propertyName) {
		Assert.notNull(object);
		Assert.hasText(propertyName);
		try {
			PropertyDescriptor descriptor = new PropertyDescriptor(propertyName, object.getClass());
			Method method = descriptor.getReadMethod();
			return method.invoke(object, new Object[0]);
		} catch (Exception exception) {
			Field field = getDeclaredField(object, propertyName);
			Object result = null;
			boolean accessible = field.isAccessible();
			field.setAccessible(true);
			try {
				result = field.get(object);
			} catch (IllegalAccessException illegalAccessException) {
				illegalAccessException.printStackTrace();
			}
			field.setAccessible(accessible);
			return result;
		}
	}

	/**
	 * 循环向上转型，获得对象的声明属性 如果没有该属性，抛出RuntimeException
	 */
	public static Field getDeclaredField(Object object, String propertyName)
			throws RuntimeException {
		Assert.notNull(object);
		Assert.hasText(propertyName);
		return getDeclaredField(object.getClass(), propertyName);
	}

	/**
	 * 循环向上转型，获得对象的声明属性 如果没有该属性，抛出RuntimeException
	 */
	public static Field getDeclaredField(Class<?> clazz, String propertyName)
			throws RuntimeException {
		Assert.notNull(clazz, "clazz不能为空");
		Assert.hasText(propertyName, "propertyName");
		for (Class<?> superClass = clazz; superClass != Object.class; superClass = superClass.getSuperclass()) {
			try {
				return superClass.getDeclaredField(propertyName);
			} catch (NoSuchFieldException noSuchFieldException) {
				// Field不在当前类定义,继续向上转型
			}
		}
		throw new RuntimeException("No such field: " + clazz.getName() + '.' + propertyName);
	}
}
