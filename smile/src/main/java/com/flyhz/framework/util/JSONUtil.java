
package com.flyhz.framework.util;

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonEncoding;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.DeserializationConfig.Feature;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;

public class JSONUtil {

	public static final ObjectMapper	mapper			= new ObjectMapper();
	private static JsonGenerator		jsonGenerator	= null;
	static {
		mapper.configure(Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);

	}

	/**
	 * JSON字符串转换为对象 JSON:"{\"type\":\"tom\"}"; resultSetClass:Metadata.class实体对象
	 */
	public static <E> E getJson2Entity(String json, Class<E> resultSetClass) {
		Object obj = null;
		try {
			if (StringUtils.isBlank(json)) {
				return null;
			}
			obj = mapper.readValue(json, resultSetClass);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return (E) obj;
	}

	/**
	 * JSON字符串转换为泛型list
	 * 
	 * @param <E>
	 */
	public static <E> List<E> getJson2EntityList(String json, Class<?> collectionClass,
			Class<?>... elementClasses) {
		Object obj = null;
		try {
			if (StringUtils.isBlank(json)) {
				return null;
			}
			JavaType javaType = mapper.getTypeFactory().constructParametricType(collectionClass,
					elementClasses);
			obj = mapper.readValue(json, javaType);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return (List<E>) obj;
	}

	/**
	 * 对象转换为 JSON字符串 obj 实体对象
	 */
	public static <E> String getEntity2Json(Object obj) {
		String json = null;
		try {
			jsonGenerator = mapper.getJsonFactory().createJsonGenerator(System.out,
					JsonEncoding.UTF8);

			json = mapper.writeValueAsString(obj);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return json;
	}
}
