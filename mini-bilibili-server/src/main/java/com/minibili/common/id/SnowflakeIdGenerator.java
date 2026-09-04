package com.minibili.common.id;

import org.springframework.stereotype.Component;
// 生成雪花id
@Component
public class SnowflakeIdGenerator implements IdGenerator {

    private final long workerId = 1L;

    private long sequence = 0L;
    private long lastTimestamp = -1L;

    private static final long EPOCH = 1767225600000L;

    private static final long WORKER_ID_BITS = 10L;
    private static final long SEQUENCE_BITS = 12L;

    private static final long MAX_WORKER_ID =
            ~(-1L << WORKER_ID_BITS);

    private static final long WORKER_ID_SHIFT =
            SEQUENCE_BITS;

    private static final long TIMESTAMP_LEFT_SHIFT =
            SEQUENCE_BITS + WORKER_ID_BITS;

    private static final long SEQUENCE_MASK =
            ~(-1L << SEQUENCE_BITS);

    @Override
    public synchronized Long nextId() {

        long timestamp = System.currentTimeMillis();

        // 防止系统时间回拨
        if (timestamp < lastTimestamp) {
            throw new RuntimeException("系统时间发生回拨，无法生成ID");
        }

        // 同一毫秒内
        if (timestamp == lastTimestamp) {

            sequence = (sequence + 1) & SEQUENCE_MASK;

            // 当前毫秒序列号用完
            if (sequence == 0) {
                timestamp = waitNextMillis(lastTimestamp);
            }

        } else {
            sequence = 0L;
        }

        lastTimestamp = timestamp;

        return ((timestamp - EPOCH) << TIMESTAMP_LEFT_SHIFT)
                | (workerId << WORKER_ID_SHIFT)
                | sequence;
    }

    private long waitNextMillis(long lastTimestamp) {

        long timestamp = System.currentTimeMillis();

        while (timestamp <= lastTimestamp) {
            timestamp = System.currentTimeMillis();
        }

        return timestamp;
    }
}
