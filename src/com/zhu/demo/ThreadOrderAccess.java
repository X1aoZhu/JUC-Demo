package com.zhu.demo;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 线程间的定制化通信，即精确通知和顺序访问
 * 题目：
 * 启动三个线程，要求：AA打印5次，BB打印10次，CC打印15次, 来10轮
 *
 * @Author Zhu
 * @Date 2020/4/27 0:09
 */
public class ThreadOrderAccess {
    public static void main(String[] args) {
        ShareResources shareResources = new ShareResources();
        int count = 10;

        new Thread(() -> {
            for (int i = 0; i < count; i++) {
                shareResources.print5();
            }
        }, "AA").start();

        new Thread(() -> {
            for (int i = 0; i < count; i++) {
                shareResources.print10();
            }
        }, "BB").start();

        new Thread(() -> {
            for (int i = 0; i < count; i++) {
                shareResources.print15();
            }
        }, "CC").start();

    }
}

class ShareResources {
    private int index = 1;

    private Lock lock = new ReentrantLock();
    private Condition condition1 = lock.newCondition();
    private Condition condition2 = lock.newCondition();
    private Condition condition3 = lock.newCondition();

    public void print5() {
        lock.lock();
        try {
            while (index != 1) {
                condition1.await();
            }
            for (int i = 0; i < 5; i++) {
                System.out.println(Thread.currentThread().getName() + ", 第 " + (i + 1) + " 次");
            }
            index = 2;
            condition2.signal();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public void print10() {
        lock.lock();
        try {
            while (index != 2) {
                condition2.await();
            }
            for (int i = 0; i < 10; i++) {
                System.out.println(Thread.currentThread().getName() + ", 第 " + (i + 1) + " 次");
            }
            index = 3;
            condition3.signal();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public void print15() {
        lock.lock();
        try {
            while (index != 3) {
                condition3.await();
            }
            for (int i = 0; i < 15; i++) {
                System.out.println(Thread.currentThread().getName() + ", 第 " + (i + 1) + " 次");
            }
            index = 1;
            condition1.signal();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
}
