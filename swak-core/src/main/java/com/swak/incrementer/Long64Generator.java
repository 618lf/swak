package com.swak.incrementer;

/**
 * 64byte位ID生成规则(42(毫秒)+5(机器ID)+5(业务编码)+12(重复累加))
 *
 * @author: lifeng
 * @date: 2020/3/29 11:47
 */
public class Long64Generator implements IdGenerator {

    /**
     * 机器标识位数
     */
    private final static long WORKER_ID_BITS = 5L;
    /**
     * 数据中心标识位数
     */
    private final static long DATACENTER_ID_BITS = 5L;
    /**
     * 机器ID最大值
     */
    private final static long MAX_WORKER_ID = ~(-1L << WORKER_ID_BITS);
    /**
     * 数据中心ID最大值
     */
    private final static long MAX_DATACENTER_ID = ~(-1L << DATACENTER_ID_BITS);
    /**
     * 毫秒内自增位
     */
    private final static long SEQUENCE_BITS = 12L;
    /**
     * 机器ID偏左移12位
     */
    private final static long WORKER_ID_SHIFT = SEQUENCE_BITS;
    /**
     * 数据中心ID左移17位
     */
    private final static long DATACENTER_ID_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;
    /**
     * 时间毫秒左移22位
     */
    private final static long TIMESTAMP_LEFT_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS + DATACENTER_ID_BITS;
    private final static long SEQUENCE_MASK = ~(-1L << SEQUENCE_BITS);
    private static long lastTimestamp = -1L;

    private final static long TWEPOCH = 1288834974657L;
    private long sequence = 0L;
    private final long workerId;
    private final long datacenterId;

    public Long64Generator(long workerId, long datacenterId) {
        if (workerId > MAX_WORKER_ID || workerId < 0) {
            throw new IllegalArgumentException("worker Id can't be greater than %d or less than 0");
        }
        if (datacenterId > MAX_DATACENTER_ID || datacenterId < 0) {
            throw new IllegalArgumentException("datacenter Id can't be greater than %d or less than 0");
        }
        this.workerId = workerId;
        this.datacenterId = datacenterId;
    }

    public synchronized long nextId() {
        long timestamp = timeGen();
        if (timestamp < lastTimestamp) {
            throw new RuntimeException("Clock moved backwards.  Refusing to generate id for "
                    + (lastTimestamp - timestamp) + " milliseconds");
        }
        if (lastTimestamp == timestamp) {
            // 当前毫秒内，则+1
            sequence = (sequence + 1) & SEQUENCE_MASK;
            if (sequence == 0) {
                // 当前毫秒内计数满了，则等待下一秒
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0;
        }

        lastTimestamp = timestamp;

        // ID偏移组合生成最终的ID，并返回ID
        return ((timestamp - TWEPOCH) << TIMESTAMP_LEFT_SHIFT) | (datacenterId << DATACENTER_ID_SHIFT)
                | (workerId << WORKER_ID_SHIFT) | sequence;
    }

    private long tilNextMillis(final long lastTimestamp) {
        long timestamp = this.timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = this.timeGen();
        }
        return timestamp;
    }

    private long timeGen() {
        return System.currentTimeMillis();
    }

    /**
     * 对外提供服务
     */
    @Override
    @SuppressWarnings("unchecked")
    public Long id() {
        return this.nextId();
    }
}