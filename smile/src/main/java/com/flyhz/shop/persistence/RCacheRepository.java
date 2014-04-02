
package com.flyhz.shop.persistence;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisConnectionException;

import com.flyhz.framework.lang.CacheRepository;
import com.flyhz.framework.util.JSONUtil;

@Service
public class RCacheRepository implements CacheRepository, InitializingBean {
	Logger						log			= LoggerFactory.getLogger(getClass());

	@Resource
	@Value(value = "${smile.redis.ip}")
	private String				ip;
	@Resource
	@Value(value = "${smile.redis.port}")
	private Integer				port;
	private static JedisPool	pool;
	@Resource
	@Value(value = "${smile.redis.maxActive}")
	private Integer				maxActive;
	@Resource
	@Value(value = "${smile.redis.maxIdle}")
	private Integer				maxIdle;
	@Resource
	@Value(value = "${smile.redis.maxWait}")
	private Long				maxWait;
	@Resource
	@Value(value = "${smile.redis.time}")
	private Long				time;
	@Resource(name = "cache.prefixs")
	private Map<String, String>	prefixs;

	private String				STRING_NAME	= "java.lang.String";

	public RCacheRepository() throws Exception {

	}

	@Override
	public String getString(String key, Class<?> clazz) {
		if (StringUtils.isNotBlank(key) && clazz != null && !STRING_NAME.equals(clazz.getName())) {
			String prefix = prefixs.get(clazz.getName());
			Jedis jedis = getJedis();
			if (jedis != null && StringUtils.isNotBlank(prefix)) {
				String value = jedis.get(prefix + "@" + key);
				pool.returnResource(jedis);
				return value;
			}
		}
		return null;
	}

	@Override
	public Object getObject(String key, Class<?> clazz) {
		try {
			String jsonStr = getString(key, clazz);
			if (StringUtils.isNotBlank(jsonStr)) {
				Object object = JSONUtil.mapper.readValue(jsonStr, clazz);
				return object;
			}
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void set(String key, Object value) {
		try {
			if (StringUtils.isNotBlank(key) && !STRING_NAME.equals(value.getClass().getName())
					&& value != null) {
				String prefix = prefixs.get(value.getClass().getName());
				Jedis jedis = getJedis();
				if (jedis != null && StringUtils.isNotBlank(prefix)) {
					jedis.set(prefix + "@" + key, JSONUtil.mapper.writeValueAsString(value));
					pool.returnResource(jedis);
				}
			}
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void set(String prefix, String key, String value) {
		if (StringUtils.isNotBlank(key) && !STRING_NAME.equals(value.getClass().getName())
				&& value != null) {
			Jedis jedis = getJedis();
			if (jedis != null && StringUtils.isNotBlank(prefix)) {
				jedis.set(prefix + "@" + key, value);
				pool.returnResource(jedis);
			}
		}
	}

	@Override
	public void set(Map<String, Object> map) {
		try {
			if (map != null && !map.isEmpty()) {
				Jedis jedis = getJedis();
				if (jedis != null) {
					for (Entry<String, Object> entry : map.entrySet()) {
						String key = entry.getKey();
						Object value = entry.getValue();
						if (StringUtils.isNotBlank(key)
								&& !STRING_NAME.equals(value.getClass().getName()) && value != null) {
							String prefix = prefixs.get(value.getClass().getName());
							if (jedis != null && StringUtils.isNotBlank(prefix)) {
								jedis.set(prefix + "@" + key,
										JSONUtil.mapper.writeValueAsString(value), null, "EX", time);
								pool.returnResource(jedis);
							}
						}
					}
				}
			}
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setNull(String key, Object value) {
		if (StringUtils.isNotBlank(key) && !STRING_NAME.equals(value.getClass().getName())
				&& value != null) {
			String prefix = prefixs.get(value.getClass().getName());
			Jedis jedis = getJedis();
			if (jedis != null && StringUtils.isNotBlank(prefix)) {
				jedis.set(prefix + "@" + key, "");
				pool.returnResource(jedis);
			}
		}
	}

	protected Jedis getJedis() {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			return jedis;
		} catch (JedisConnectionException e) {
			log.error("", e);
			if (jedis != null)
				pool.returnBrokenResource(jedis);
		}
		return null;

	}

	public static void main(String[] args) {
		final Jedis jedis = new Jedis("10.22.23.64");
		for (int i = 0; i < 10; i++) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					System.out.println(jedis.get("n500"));
				}
			}).start();
		}
	}

	public void afterPropertiesSet() throws Exception {
		if (pool == null) {
			JedisPoolConfig config = new JedisPoolConfig();
			config.setMaxActive(maxActive);
			config.setMaxIdle(maxIdle);
			config.setMaxWait(maxWait);
			config.setTestOnBorrow(true);
			config.setTestOnReturn(true);
			config.setWhenExhaustedAction(GenericObjectPool.WHEN_EXHAUSTED_GROW);
			pool = new JedisPool(config, ip, port);
		}
	}

	public void setString(String prefix, String key, String value) {
		if (StringUtils.isNotBlank(key) && value != null) {
			Jedis jedis = getJedis();
			if (jedis != null && StringUtils.isNotBlank(prefix)) {
				jedis.set(prefix + "@" + key, value);
				pool.returnResource(jedis);
			}
		}
	}

	public void setStringNull(String prefix, String key) {
		if (StringUtils.isNotBlank(key)) {
			Jedis jedis = getJedis();
			if (jedis != null && StringUtils.isNotBlank(prefix)) {
				jedis.set(prefix + "@" + key, "");
				pool.returnResource(jedis);
			}
		}
	}
}
