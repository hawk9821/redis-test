package com.hawk.redis.jedis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author zhangdonghao
 * @date 2019/4/30
 */
public class JedisUtil {
    private static JedisPool jedisPool;

    public static Jedis getInstance(String host,int port,String password) {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(8);
        poolConfig.setMaxIdle(8);
        poolConfig.setMinIdle(0);
        poolConfig.setMaxWaitMillis(1000);
        if (jedisPool == null){
            jedisPool = new JedisPool(poolConfig, host, port, 1000, password);
        }
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
        } catch (Exception e) {
            e.getMessage();
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return jedis;
    }
}
