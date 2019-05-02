package com.hawk.redis.hashConsistent;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * @author zhangdonghao
 * @date 2019/5/1
 */
public class HashFunction {

    public static int hash(Object key) {
        ByteBuffer buffer = ByteBuffer.wrap(key.toString().getBytes());
        int seed = 0x1234ABCD;
        ByteOrder order = buffer.order();
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        long m = 0xc6a4a7935bd1e995L;
        int r = 47;
        long h = seed ^ (buffer.remaining() * m);

        long k;
        while (buffer.remaining() >= 8) {
            k = buffer.getLong();
            k *= m;
            k ^= k >>> r;
            k *= m;

            h ^= k;
            h *= m;
        }
        if (buffer.remaining() > 0) {
            ByteBuffer finish = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN);
            finish.put(buffer).rewind();
            h ^= finish.getLong();
            h *= m;
        }
        h ^= h >>> r;
        h *= m;
        h ^= h >>> r;
        buffer.order(order);
        return (int) h;
    }
}

