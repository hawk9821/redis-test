package com.hawk.redis.lock;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author zhangdonghao
 * @date 2019/5/1
 */
public class RedisLock {

    public String getLock(String key, int timeout, int expire) {
        Jedis jedis = null;
        try {
            jedis = RedisManager.getJedisClient();
            String value = UUID.randomUUID().toString();
            //timeout时间内没有获取锁,就不再再去获取锁
            long end = System.currentTimeMillis() + timeout;
            while (System.currentTimeMillis() < end) {
                //当key不存在的时候，才会把value存储到reids中
                //如果key在redis中存在,那么setnx不做任何操作
                if (jedis.setnx(key, value) == 1) {
                    //设置锁的失效时间
                    jedis.expire(key, expire);
                    return value;
                }
                if (jedis.ttl(key) == -1) {  //代表没有失效时间
                    jedis.expire(key, expire);
                }
                TimeUnit.MILLISECONDS.sleep(1000);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    /**
     * 释放锁
     *
     * @param key
     * @param value
     * @return
     */
    public boolean releaseLock(String key, String value) {
        Jedis jedis = RedisManager.getJedisClient();
        try {
            jedis.watch(key);
            System.out.println(key + "  :   " + jedis.get(key));
            while (true) {
                //判断是否相等，说明是获得锁的线程
                if (value.equals(jedis.get(key))) {
                    Transaction transaction = jedis.multi();
                    transaction.del(key);
                    List<Object> exec = transaction.exec();
                    if (exec == null) {
                        continue;
                    }
                    return true;
                }
                jedis.unwatch();
                break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
        return false;
    }

    /**
     * lua脚本释放锁（删除key）
     *
     * @param key
     * @param value
     * @return
     */
    public boolean releaseLock1(String key, String value) {
        Jedis jedis = RedisManager.getJedisClient();
        String script = "if redis.call('get',KEYS[1]) == ARGV[1] then return redis.call('del',KEYS[1]) else return 0 end";
        Object result = jedis.eval(script, Collections.singletonList(key), Collections.singletonList(value));
        if ("1".equals(result)) {
            return true;
        }
        return false;
    }

    public static void main(String[] args) throws InterruptedException {
        RedisLock lock = new RedisLock();
        String value = lock.getLock("hawk_lock", 10000, 10);
        if (value != null) {
            System.out.println("获取到锁,执行业务");
        }
        lock.releaseLock1("hawk_lock", value);
        System.out.println("释放锁======");
        lock.isLimit("127.0.0.2", "60", "10");

    }



    public boolean isLimit(String ip, String exprise, String limit) {
        Jedis jedis = RedisManager.getJedisClient();
        String script = "local num = redis.call('incr',KEYS[1])\n" +
                "if tonumber(num) == 1 then\n" +
                "redis.call('expire',KEYS[1],ARGV[1])\n" + "return 1\n" +
                "elseif tonumber(num) > tonumber(ARGV[2]) then\n" +
                "return 0\n" +
                "else return 1\n" + "end";
        List argvs = new ArrayList();
        argvs.add(exprise);
        argvs.add(limit);
        Object result = jedis.eval(script, Collections.singletonList(ip), argvs);
        if ((Long) result == 1) {
            System.out.println("不限流.......");
            return false;
        } else {
            System.out.println("限流.......");
            return true;
        }
    }
}
