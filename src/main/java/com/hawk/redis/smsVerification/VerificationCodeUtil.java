package com.hawk.redis.smsVerification;

import com.hawk.redis.lock.RedisManager;
import redis.clients.jedis.Jedis;

import java.util.*;

/**
 * 验证码
 * @author zhangdonghao
 * @date 2019/5/3
 */
public class VerificationCodeUtil {
    private static Jedis jedis;
    private static final String EXPIRE = "60";

    public static String getVerificationCode(String id,String phone){
        jedis = RedisManager.getJedisClient();
        String script = "do redis.call('set',KEYS[1],ARGV[1])\n" +
                "redis.call('expire',KEYS[1],ARGV[2])\n" +
                "return redis.call('get',KEYS[1])" +
                "end";
        List<String> keys = new ArrayList<>();
        List<String> argvs = new ArrayList<>();
        keys.add(id+":"+phone);
        argvs.add(VerificationCodeUtil.buildVerificationCode());
        argvs.add(VerificationCodeUtil.EXPIRE);
        Object result = jedis.eval(script, keys, argvs);
        return result.toString();
    }

    public static String buildVerificationCode(){
        StringBuffer vCode = new StringBuffer();
        //方法一
        int code1 = (int)((Math.random()*9+1)*100000);
        System.out.println("方法一： "+ code1);
        //方法二
        StringBuffer code2 = new StringBuffer();
        for (int i = 0; i < 6; i++) {
            code2.append((int)(Math.random()*10));
        }
        System.out.println("方法二： "+ code2);
        //方法三
        StringBuffer code3 = new StringBuffer();
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            code3.append(random.nextInt(10));
        }
        System.out.println("方法三： "+ code3);
        //方法四
        String source = "0123456789abcdefghijklmnopqrstuvwxyz";
        StringBuffer code4 = new StringBuffer();
        for (int i = 0; i < 6; i++) {
            code4.append(source.charAt(random.nextInt(36)));
        }
        System.out.println("方法四： "+ code4);

        vCode.append(code4);
        return vCode.toString();


    }

    public static boolean checkVerificationCode(String key,String code){
        jedis = RedisManager.getJedisClient();
        String script ="if redis.call('get',KEYS[1]) == ARGV[1] then\n" +
                "return 1\n" +
                "else\n " +
                "return 0\n end";
        Object result = jedis.eval(script, Collections.singletonList(key),Collections.singletonList(code));
        if ((Long)result == 1){
            return true;
        }
        return false;
    }

    public static void main(String[] args) throws InterruptedException {
        String uuid = UUID.randomUUID().toString();
        String code = VerificationCodeUtil.getVerificationCode(uuid,"17713559821");
        System.out.println("获取验证码 : " + code);
        Thread.sleep(61000);
        boolean flag = VerificationCodeUtil.checkVerificationCode(uuid + ":" + "17713559821", code);
        if (flag){
            System.out.println("验证码为：" + code +  "  验证成功!");
        }else {
            System.out.println("验证码为：" + code +  "  验证失败!");
        }
    }
}
