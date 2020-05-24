package com.zhu.demo2;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * JUC读写锁，简单缓存demo
 * 五个线程分别读和写
 *
 * @Author Zhu
 * @Date 2020/5/24 20:55
 */
public class ReadWriteLockDemo {
    public static void main(String[] args) {
        MyCache myCache = new MyCache();
        int count = 5;

        for (int i = 1; i <= count; i++) {
            final int num = i;
            new Thread(() -> {
                myCache.put(num + "", UUID.randomUUID().toString().substring(0, 8));
            }, String.valueOf(i)).start();
        }

        for (int i = 1; i <= count; i++) {
            final int num = i;
            new Thread(() -> {
                myCache.get(num + "");
            }, String.valueOf(i)).start();
        }

    }
}

class MyCache {
    private volatile Map<String, Object> map = new HashMap<>();

    private ReadWriteLock lock = new ReentrantReadWriteLock();
    void put(String key, Object value) {
        lock.writeLock().lock();
        try {
            System.out.println(Thread.currentThread().getName() + "\t,写数据," + key);
            map.put(key, value);
            System.out.println(Thread.currentThread().getName() + "\t,写数据完成");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.writeLock().unlock();
        }
    }

    void get(String key) {
        lock.readLock().lock();
        try {
            System.out.println(Thread.currentThread().getName() + "\t,读数据," + key);
            Object val = map.get(key);
            System.out.println(Thread.currentThread().getName() + "\t,读数据完成，值为：" + val);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.readLock().unlock();
        }
    }
}
