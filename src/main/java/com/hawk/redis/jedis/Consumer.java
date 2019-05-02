package com.hawk.redis.jedis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

/**
 * @author zhangdonghao
 * @date 2019/5/1
 */
public class Consumer {
    public static void main(String[] args) {
        Jedis jedis = JedisUtil.getInstance("192.168.220.98",6379,null);
        jedis.subscribe(new JedisPubSub() {
            @Override
            public void onMessage(String channel, String message) {
                System.out.println("从 [" + channel + "]订阅消息, message = [" + message + "]");
                super.onMessage(channel, message);
            }
        },"hawk9821","hawk");
    }
}
