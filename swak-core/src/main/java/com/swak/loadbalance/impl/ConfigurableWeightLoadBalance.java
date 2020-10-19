package com.swak.loadbalance.impl;

import com.swak.loadbalance.Referer;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 配置权重
 *
 * @author lifeng
 * @date 2020年4月30日 上午11:03:25
 */
public class ConfigurableWeightLoadBalance<T> extends ActiveWeightLoadBalance<T> {

    private final String prefix;
    private final String weights;
    private volatile RefererListCacheHolder<T> holder;

    public ConfigurableWeightLoadBalance(String prefix, String weights) {
        this.weights = weights;
        this.prefix = prefix;
    }

    @Override
    protected String prefix() {
        return prefix;
    }

    @Override
    public void onRefresh(List<T> referers) {
        super.onRefresh(referers);
        this.holder = new MultiGroupHolder(weights, this.getReferers());
    }

    @Override
    protected Referer<T> doSelect() {
        return holder.next();
    }

    static abstract class RefererListCacheHolder<T> {

        /**
         * 下一个依赖
         *
         * @return 依赖
         */
        abstract Referer<T> next();
    }

    class MultiGroupHolder extends RefererListCacheHolder<T> {

        private int randomKeySize = 0;
        private List<String> randomKeyList = new ArrayList<>();
        private Map<String, AtomicInteger> cursors = new HashMap<>();
        private Map<String, List<Referer<T>>> groupReferers = new HashMap<>();

        MultiGroupHolder(String weights, List<Referer<T>> list) {
            String[] groupsAndWeights = weights.split(",");
            int[] weightsArr = new int[groupsAndWeights.length];
            Map<String, Integer> weightsMap = new HashMap<>(groupsAndWeights.length);
            int i = 0;
            for (String groupAndWeight : groupsAndWeights) {
                String[] gw = groupAndWeight.split(":");
                if (gw.length == 2) {
                    Integer w = Integer.valueOf(gw[1]);
                    weightsMap.put(gw[0], w);
                    groupReferers.put(gw[0], new ArrayList<>());
                    weightsArr[i++] = w;
                }
            }

            // 求出最大公约数，若不为1，对权重做除法
            int weightGcd = findGcd(weightsArr);
            if (weightGcd != 1) {
                weightsMap.replaceAll((k, v) -> v / weightGcd);
            }

            for (Map.Entry<String, Integer> entry : weightsMap.entrySet()) {
                for (int j = 0; j < entry.getValue(); j++) {
                    randomKeyList.add(entry.getKey());
                }
            }
            Collections.shuffle(randomKeyList);
            randomKeySize = randomKeyList.size();

            for (String key : weightsMap.keySet()) {
                cursors.put(key, new AtomicInteger(0));
            }

            for (Referer<T> referer : list) {
                groupReferers.get(referer.getName()).add(referer);
            }
        }

        @Override
        Referer<T> next() {
            String group = randomKeyList.get(ThreadLocalRandom.current().nextInt(randomKeySize));
            AtomicInteger ai = cursors.get(group);
            List<Referer<T>> referers = groupReferers.get(group);
            return referers.get(getNonNegative(ai.getAndIncrement()) % referers.size());
        }

        // 求最大公约数
        private int findGcd(int n, int m) {
            return (n == 0 || m == 0) ? n + m : findGcd(m, n % m);
        }

        // 求最大公约数
        private int findGcd(int[] arr) {
            int i = 0;
            for (; i < arr.length - 1; i++) {
                arr[i + 1] = findGcd(arr[i], arr[i + 1]);
            }
            return findGcd(arr[i], arr[i - 1]);
        }

        /**
         * 通过二进制位操作将originValue转化为非负数: 0和正数返回本身
         * 负数通过二进制首位取反转化为正数或0（Integer.MIN_VALUE将转换为0） return non-negative int value of
         * originValue
         *
         * @param originValue 原始值
         * @return positive int
         */
        public int getNonNegative(int originValue) {
            return 0x7fffffff & originValue;
        }
    }
}
