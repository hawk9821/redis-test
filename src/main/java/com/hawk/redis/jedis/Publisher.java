package com.hawk.redis.jedis;

import redis.clients.jedis.Jedis;

/**
 * @author zhangdonghao
 * @date 2019/5/1
 */
public class Publisher {

    public static void main(String[] args) {
        Jedis jedis = JedisUtil.getInstance("192.168.220.98",6379,null);
        jedis.publish("hawk9821","hello world!");
//        jedis.close();
    }
}
