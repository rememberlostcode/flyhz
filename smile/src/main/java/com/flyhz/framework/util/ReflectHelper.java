
package com.flyhz.framework.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;

/**
 * @author Administrator 反射工具
 */
public class ReflectHelper {
	/** 默认的需排除的属性 */
	public static String[]	DEFAULT_EXCLUDES	= new String[] { "serialVersionUID" };

	/**
	 * 获取object对象fieldName的Field
	 * 
	 * @param obj
	 * @param fieldName
	 * @return
	 */
	public static Field getFieldByFieldName(Object object, String fieldName) {
		for (Class<?> superClass = object.getClass(); superClass != Object.class; superClass = superClass.getSuperclass()) {
			try {
				return superClass.getDeclaredField(fieldName);
			} catch (NoSuchFieldException e) {
			}
		}
		return null;
	}

	/**
	 * 获取object对象fieldName的属性值
	 * 
	 * @param obj
	 * @param fieldName
	 * @return
	 * @throws SecurityException
	 * @throws NoSuchFieldException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public static Object getValueByFieldName(Object object, String fieldName)
			throws SecurityException, NoSuchFieldException, IllegalArgumentException,
			IllegalAccessException {
		Field field = getFieldByFieldName(object, fieldName);
		Object value = null;
		if (field != null) {
			if (field.isAccessible()) {
				value = field.get(object);
			} else {
				field.setAccessible(true);
				value = field.get(object);
				field.setAccessible(false);
			}
		}
		return value;
	}

	/**
	 * 设置object对象fieldName的属性值
	 * 
	 * @param obj
	 * @param fieldName
	 * @param value
	 * @throws SecurityException
	 * @throws NoSuchFieldException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public static void setValueByFieldName(Object object, String fieldName, Object value)
			throws SecurityException, NoSuchFieldException, IllegalArgumentException,
			IllegalAccessException {
		Field field = object.getClass().getDeclaredField(fieldName);
		if (field.isAccessible()) {
			field.set(object, value);
		} else {
			field.setAccessible(true);
			field.set(object, value);
			field.setAccessible(false);
		}
	}

	/**
	 * 获得object对象的属性名数组
	 * 
	 * @param object
	 * @return String[]
	 */
	public static String[] getFiledNames(Object object) {
		Field[] fields = object.getClass().getDeclaredFields();
		String[] fieldNames = new String[fields.length];
		for (int i = 0; i < fields.length; i++) {
			fieldNames[i] = fields[i].getName();
		}
		return fieldNames;
	}

	/**
	 * 获得object对象的属性名数组
	 * 
	 * @param object
	 * @return String[]
	 */
	public static String[] getFiledNames(Class<?> clazz) {
		return getFiledNames(clazz, null);
	}

	/**
	 * 获得Class的属性名数组
	 * 
	 * @param object
	 * @param excludes
	 *            需排除的属性数组
	 * @return String[]
	 */
	public static String[] getFiledNames(Class<?> clazz, String[] excludes) {
		if (clazz != null) {
			// 获得需排除属性list
			List<String> excludeList = new ArrayList<String>();
			excludes = (String[]) ArrayUtils.addAll(excludes, DEFAULT_EXCLUDES);
			for (int i = 0; i < excludes.length; i++) {
				excludeList.add(excludes[i]);
			}
			// 获得全部属性list
			List<String> fieldNameList = new ArrayList<String>();
			Field[] fields = clazz.getDeclaredFields();
			for (int i = 0; i < fields.length; i++) {
				fieldNameList.add(fields[i].getName());
			}
			// 排除属性操作
			fieldNameList.removeAll(excludeList);
			// 将list集合转换为数组
			return fieldNameList.toArray(new String[] {});
		}
		return null;
	}
}
