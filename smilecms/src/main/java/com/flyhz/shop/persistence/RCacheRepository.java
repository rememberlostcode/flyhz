
package com.flyhz.shop.persistence;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.pool.impl.GenericObjectPool;
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

@Service
public class RCacheRepository implements CacheRepository, InitializingBean {
	Logger						log	= LoggerFactory.getLogger(getClass());

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

	public RCacheRepository() throws Exception {

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

	@Override
	public void delete(String key) {
		Jedis jedis = getJedis();
		if (jedis != null) {
			jedis.del(key);
			pool.returnResource(jedis);
		}
	}

	@Override
	public String getString(String key) {
		if (StringUtils.isNotBlank(key)) {
			Jedis jedis = getJedis();
			if (jedis != null) {
				String value = jedis.get(key);
				pool.returnResource(jedis);
				return value;
			}
		}
		return null;
	}

	public void setString(String key, String value) {
		if (StringUtils.isNotBlank(key) && value != null) {
			Jedis jedis = getJedis();
			if (jedis != null) {
				jedis.set(key, value);
				pool.returnResource(jedis);
			}
		}
	}

	public void hset(String key, String field, String value) {
		if (StringUtils.isNotBlank(key) && StringUtils.isNotBlank(field)
				&& StringUtils.isNotBlank(value)) {
			Jedis jedis = getJedis();
			if (jedis != null) {
				jedis.hset(key, field, value);
				pool.returnResource(jedis);
			}
		}
	}

	public String hget(String key, String field) {
		String result = null;
		if (StringUtils.isNotBlank(key) && StringUtils.isNotBlank(field)) {
			Jedis jedis = getJedis();
			if (jedis != null) {
				result = jedis.hget(key, field);
				pool.returnResource(jedis);
			}
		}
		return result;
	}

	public void hdel(String key, String field) {
		if (StringUtils.isNotBlank(key) && StringUtils.isNotBlank(field)) {
			Jedis jedis = getJedis();
			if (jedis != null) {
				jedis.hdel(key, field);
				pool.returnResource(jedis);
			}
		}
	}

	public Map<String, String> hgetall(String key) {
		Map<String, String> result = null;
		if (StringUtils.isNotBlank(key)) {
			Jedis jedis = getJedis();
			if (jedis != null) {
				result = jedis.hgetAll(key);
				pool.returnResource(jedis);
			}
		}
		return result;
	}

	@Override
	public void rpush(String key, String value) {
		if (StringUtils.isNotBlank(key) && StringUtils.isNotBlank(value)) {
			Jedis jedis = getJedis();
			if (jedis != null) {
				jedis.rpush(key, value);
				pool.returnResource(jedis);
			}
		}
	}

	@Override
	public void lpush(String key, String value) {
		if (StringUtils.isNotBlank(key) && StringUtils.isNotBlank(value)) {
			Jedis jedis = getJedis();
			if (jedis != null) {
				jedis.lpush(key, value);
				pool.returnResource(jedis);
			}
		}
	}

	@Override
	public Long llen(String key) {
		Long l = 0L;
		if (StringUtils.isNotBlank(key)) {
			Jedis jedis = getJedis();
			if (jedis != null) {
				l = jedis.llen(key);
				pool.returnResource(jedis);
			}
		}
		return l;
	}

	@Override
	public void lrem(String key, String value) {
		if (StringUtils.isNotBlank(key) && StringUtils.isNotBlank(value)) {
			Jedis jedis = getJedis();
			if (jedis != null) {
				jedis.lrem(key, 0, value);
				pool.returnResource(jedis);
			}
		}
	}

	@Override
	public List<String> lrange(String key) {
		List<String> list = null;
		if (StringUtils.isNotBlank(key)) {
			Jedis jedis = getJedis();
			if (jedis != null) {
				list = jedis.lrange(key, 0, jedis.llen(key));
				pool.returnResource(jedis);
			}
		}
		return list;
	}
}
