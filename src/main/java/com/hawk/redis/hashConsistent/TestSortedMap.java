package com.hawk.redis.hashConsistent;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * @author zhangdonghao
 * @date 2019/5/1
 */
public class TestSortedMap {
    public static void main(String[] args) {
        Map<String, Integer> serverCountMap = new HashMap<>();
        serverCountMap.put("ser1", 0);
        serverCountMap.put("ser2", 0);
        serverCountMap.put("ser3", 0);
        serverCountMap.put("ser4", 0);

        SortedMap<Integer, String> map = new TreeMap<Integer, String>();
        map.put(0, "ser1");
        map.put(25, "ser2");
        map.put(50, "ser3");
        map.put(75, "ser4");
        for (int i = 0; i < 100; i++) {
            if (!map.containsKey(i)) {
                SortedMap<Integer, String> tail = map.tailMap(i);
                int serverKey = tail.isEmpty() ? map.firstKey() : tail.firstKey();
                String server = map.get(serverKey);
                if (server == null) {
                    System.out.println(serverKey);
                }
                serverCountMap.put(server, serverCountMap.get(server) + 1);
            } else {
                String server = map.get(i);
                serverCountMap.put(server, serverCountMap.get(server) + 1);
            }
        }

        for (Map.Entry enty : serverCountMap.entrySet()) {
            System.out.println(enty.getKey() + "    " + enty.getValue());
        }
    }
}
