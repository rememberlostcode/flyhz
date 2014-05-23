
package com.holding.smile.tools;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.codehaus.jackson.JsonEncoding;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.DeserializationConfig.Feature;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;

import android.util.Log;

import com.holding.smile.activity.MyApplication;
import com.holding.smile.dto.RtnValueDto;
import com.holding.smile.entity.JGoods;

/**
 * 
 * 类说明:Json工具类
 * 
 * @author robin
 * @version 创建时间：2013-11-27 下午5:03:37
 * 
 */
public class JSONUtil {
	public static final ObjectMapper	objectMapper	= new ObjectMapper();
	@SuppressWarnings("unused")
	private static JsonGenerator		jsonGenerator	= null;
	static {
		objectMapper.configure(Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
	}

	/**
	 * JSON字符串转换为对象 JSON:"{\"type\":\"tom\"}"; resultSetClass:Metadata.class实体对象
	 */
	@SuppressWarnings("unchecked")
	public static <E> E getJson2Entity(String json, Class<E> resultSetClass) {
		Object obj = null;
		try {
			obj = objectMapper.readValue(json, resultSetClass);
		} catch (Exception e) {
			Log.e(MyApplication.LOG_TAG, "", e);
		}
		return (E) obj;
	}

	/**
	 * 对象转换为 JSON字符串 obj 实体对象
	 */
	public static <E> String getEntity2Json(Object obj) {
		String json = null;
		try {
			jsonGenerator = objectMapper.getJsonFactory().createJsonGenerator(System.out,
					JsonEncoding.UTF8);

			json = objectMapper.writeValueAsString(obj);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return json;
	}

	public static List<JGoods> changeJson2JGoodsList(String json) {
		if (json != null && !"".equals(json.trim())) {
			try {
				JGoods[] jGoods = objectMapper.readValue(json, JGoods[].class);
				return new ArrayList<JGoods>(Arrays.asList(jGoods));
			} catch (Exception e) {
				Log.e(MyApplication.LOG_TAG, "", e);
			}
		}
		return null;
	}

	public static String[] changeJson2StringArray(String json) {
		if (json != null && !"".equals(json.trim())) {
			try {
				String[] strs = objectMapper.readValue(json, String[].class);
				return strs;
			} catch (Exception e) {
				Log.e(MyApplication.LOG_TAG, "", e);
			}
		}
		return null;
	}

	/**
	 * 转化成字符串列表
	 * 
	 * @param json
	 * @return
	 */
	public static List<String> changeJson2StringList(String json) {
		if (json != null && !"".equals(json.trim())) {
			try {
				String[] strs = objectMapper.readValue(json, String[].class);
				return new ArrayList<String>(Arrays.asList(strs));
			} catch (Exception e) {
				Log.e(MyApplication.LOG_TAG, "", e);
			}
		}
		return null;
	}

	public static RtnValueDto changeJson2RtnValueDto(String json) {
		RtnValueDto obj = null;
		if (json != null && !"".equals(json.trim())) {
			obj = JSONUtil.getJson2Entity(json, RtnValueDto.class);
		}
		return obj;
	}

	/**
	 * JSON字符串转换为泛型list
	 * 
	 * @param <E>
	 */
	@SuppressWarnings("unchecked")
	public static <E> List<E> getJson2EntityList(String json, Class<?> collectionClass,
			Class<?>... elementClasses) {
		Object obj = null;
		try {
			if (json == null || "".equals(json)) {
				return null;
			}
			JavaType javaType = objectMapper.getTypeFactory().constructParametricType(
					collectionClass, elementClasses);
			obj = objectMapper.readValue(json, javaType);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return (List<E>) obj;
	}

}
