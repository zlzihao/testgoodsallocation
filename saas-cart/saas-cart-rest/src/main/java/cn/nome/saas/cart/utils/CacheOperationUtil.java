package cn.nome.saas.cart.utils;

import cn.nome.saas.cart.constant.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.HashMap;
import java.util.Map;

@Component
public class CacheOperationUtil {

	private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private JedisPool jedisPool;

	/**
	 * get
	 * 
	 * @param key
	 * @return
	 */
	public String get(String key) {
		String val = null;
		Jedis jedis = null;
		try {
			jedis = getJedis();
			val = jedis.get(key);
			LOGGER.debug("[GET]key:{},val:{}", key, val);
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("key:" + key + ",errMsg:" + e.getMessage());
			val = Constant.REDIS_ERROR_STRING;
		} finally {
			returnJedis(jedis);
		}
		return val;
	}

	private Jedis getJedis() {
		return jedisPool.getResource();
	}

	private void returnJedis(Jedis jedis) {
		if (jedis != null){
			jedis.close();
		}
	}

	/**
	 * set
	 * 
	 * @param key
	 * @param val
	 * @return
	 */
	public String set(String key, String val) {
		String statusCode = null;
		Jedis jedis = null;
		try {
			jedis = getJedis();
			statusCode = jedis.set(key, val);
			LOGGER.debug("[SET]key:{},val:{}", key, val);
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("key:" + key + ",errMsg:" + e.getMessage());
			statusCode = Constant.REDIS_ERROR_STRING;
		} finally {
			returnJedis(jedis);
		}
		return statusCode;
	}

	/**
	 * del
	 * 
	 * @param key
	 * @return
	 */
	public Long del(String key) {
		Long setL;
		Jedis jedis = null;
		try {
			jedis = getJedis();
			setL = jedis.del(key);
			LOGGER.debug("[DEL]key:{},val:{}", key, setL);
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("key:" + key + ",errMsg:" + e.getMessage());
			setL = Constant.REDIS_ERROR_LONG;
		} finally {
			returnJedis(jedis);
		}
		return setL;
	}

	/**
	 * hget
	 * 
	 * @param key
	 * @param field
	 * @return
	 */
	public String hget(String key, String field) {
		String bulkReply = null;
		Jedis jedis = null;
		try {
			jedis = getJedis();
			bulkReply = jedis.hget(key, field);
			LOGGER.debug("[HGET]key:{},field:{}", key, field);
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("key:" + key + ",errMsg:" + e.getMessage());
			bulkReply = Constant.REDIS_ERROR_STRING;
		} finally {
			returnJedis(jedis);
		}
		return bulkReply;
	}

	/**
	 * hdel
	 * 
	 * @param key
	 * @param field
	 * @return
	 */
	public Long hdel(String key, String... field) {
		Long val;
		Jedis jedis = null;
		try {
			jedis = getJedis();
			val = jedis.hdel(key, field);
			LOGGER.debug("[HDEL]key:{},field:{}", key, field);
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("key:" + key + ",errMsg:" + e.getMessage());
			val = Constant.REDIS_ERROR_LONG;
		} finally {
			returnJedis(jedis);
		}
		return val;
	}

	/**
	 * hgetAll
	 * 
	 * @param key
	 * @return
	 */
	public Map<String, String> hgetAll(String key) {
		Map<String, String> val;
		Jedis jedis = null;
		try {
			jedis = getJedis();
			val = jedis.hgetAll(key);
			LOGGER.debug("[HGETALL]key:{},val:{}", key, val);
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("key:" + key + ",errMsg:" + e.getMessage());
			val = new HashMap<>();
			val.put(Constant.REDIS_ERROR_STRING, Constant.REDIS_ERROR_STRING);
		} finally {
			returnJedis(jedis);
		}
		return val;
	}

	/**
	 * hset
	 * 
	 * @param key
	 * @param field
	 * @param val
	 * @return
	 */
	public Long hset(String key, String field, String val) {
		Long setL = null;
		Jedis jedis = null;
		try {
			jedis = getJedis();
			setL = jedis.hset(key, field, val);
			LOGGER.debug("[HSET]key:{},field:{},val:{}", key, field, val);
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("key:" + key + ",errMsg:" + e.getMessage());
			setL = Constant.REDIS_ERROR_LONG;
		} finally {
			returnJedis(jedis);
		}
		return setL;
	}

	public Long hlen(String key) {
		Long setL = null;
		Jedis jedis = null;
		try {
			jedis = getJedis();
			setL = jedis.hlen(key);
			LOGGER.debug("[HLEN]key:{},setL:{}", key, setL);
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("key:" + key + ",errMsg:" + e.getMessage());
			setL = Constant.REDIS_ERROR_LONG;
		} finally {
			returnJedis(jedis);
		}
		return setL;
	}

	/**
	 * 设置有效期存储
	 * 
	 * @param key
	 * @param val
	 * @param seconds
	 * @return
	 */
	public String setex(String key, String val, int seconds) {
		String statusCode = null;
		Jedis jedis = null;
		try {
			jedis = getJedis();
			statusCode = jedis.setex(key, seconds, val);
			LOGGER.debug("[SETEX]key:{},val:{},seconds:{}", key, val, seconds);
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("key:" + key + ",errMsg:" + e.getMessage());
			statusCode = Constant.REDIS_ERROR_STRING;
		} finally {
			returnJedis(jedis);
		}
		return statusCode;
	}
}
