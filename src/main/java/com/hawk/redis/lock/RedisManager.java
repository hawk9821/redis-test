package com.hawk.redis.lock;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author zhangdonghao
 * @date 2019/5/1
 */
public class RedisManager {
    private static JedisPool jedisPool;
    static {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxIdle(10);
        config.setMaxTotal(20);
        jedisPool = new JedisPool(config,"192.168.220.98",6379);
    }
    public static Jedis getJedisClient(){
        if (jedisPool != null){
            return jedisPool.getResource();
        }
        return null;
    }

    private static void returnJedis(JedisPool pool,Jedis jedis){
        if (jedis != null){
            pool.returnResource(jedis);
        }
    }
}
