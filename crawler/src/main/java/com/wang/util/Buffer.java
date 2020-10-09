package com.wang.util;

import com.wang.pojo.Bookinfo;
import lombok.SneakyThrows;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author wangz
 */
public class Buffer {
    ReadWriteLock lock = new ReentrantReadWriteLock();
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

    public  void close(boolean force) throws Exception {
        lock.writeLock().lock();
        if (!force) {
            while (!queue.isEmpty()) {
                TimeUnit.SECONDS.sleep(1);
            }
        }
        queue = null;
        lock.writeLock().unlock();
    }

    public boolean opened() {
        return queue != null;
    }
}
