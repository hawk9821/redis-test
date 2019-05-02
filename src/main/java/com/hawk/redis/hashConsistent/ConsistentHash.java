package com.hawk.redis.hashConsistent;

import java.util.Collection;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * @author zhangdonghao
 * @date 2019/5/1
 */
public class ConsistentHash<T> {
    /**
     * 哈希函数
     */
    private final HashFunction function;
    /**
     * 虚拟节点数,越大分布越均衡，但越大，在初始化和变更的时候效率差一点。 测试中，设置200基本就均衡了
     */
    private final int numberOfReplicas;
    /**
     * 环形空间
     */
    private final SortedMap<Integer, T> circle = new TreeMap<Integer, T>();

    public ConsistentHash(HashFunction function, int numberOfReplicas, Collection<T> nodes) {
        this.function = function;
        this.numberOfReplicas = numberOfReplicas;
        for (T node : nodes) {
            this.addNode(node);
        }

    }

    public void addNode(T node) {
        for (int i = 0; i < numberOfReplicas; i++) {
            int hashValue = function.hash(node.toString() + i);
            circle.put(hashValue, node);
        }
    }

    public void removeNode(T node) {
        for (int i = 0; i < numberOfReplicas; i++) {
            int hashValue = function.hash(node.toString() + i);
            circle.remove(hashValue, node);
        }
    }

    public T getNode(Object key){
        if (circle.isEmpty()){
            return null;
        }
        int hashValue = function.hash(key);
//        System.out.println("key---" + key + " : hash---" + hashValue);
        if (!circle.containsKey(hashValue)){
            // 返回键大于或等于hash的node，即沿环的顺时针找到一个虚拟节点
            SortedMap<Integer,T> tailMap = circle.tailMap(hashValue);
            //如果是空,得到首个节点的Key
            hashValue = tailMap.isEmpty() ? circle.firstKey() : tailMap.firstKey();
        }
        return circle.get(hashValue);
    }

}


