package com.wang.util;

import com.wang.pojo.Bookinfo;
import lombok.SneakyThrows;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author wangz
 */
public class Buffer {
    private BlockingQueue<Bookinfo> queue;

    public Buffer(int size) {
        queue = new ArrayBlockingQueue<>(size);
    }

    public Bookinfo getBookInfo() throws Exception {
        if (queue != null) {
            return queue.take();
        }
        return null;
    }

    @SneakyThrows
    public void addBookInfo(Bookinfo bookinfo) {
        if (queue != null) {
            queue.put(bookinfo);
        }
    }

    public  synchronized void close(boolean force) throws Exception {
        if (!force) {
            while (!queue.isEmpty()) {
                TimeUnit.SECONDS.sleep(1);
            }
        }
        queue = null;
    }

    public synchronized   boolean opened() {
        return queue != null;
    }
}
