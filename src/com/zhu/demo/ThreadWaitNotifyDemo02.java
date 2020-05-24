package com.zhu.demo;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 问题：
 * 现在两个线程。可以操作一个初始值为0的变量，实现一个线程对改变量加一，一个线程对该变量减一。
 * 实现交替执行10轮后，变量初始值为0。
 *
 * @Author Zhu
 * @Date 2020/4/26 23:35
 */
public class ThreadWaitNotifyDemo02 {
    private static int count = 100;

    public static void main(String[] args) {
        Operation02 operation02 = new Operation02();

        new Thread(() -> {
            for (int i = 0; i < count; i++) {
                try {
                    operation02.increment();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "A").start();

        new Thread(() -> {
            for (int i = 0; i < count; i++) {
                try {
                    operation02.decrement();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "B").start();
    }
}


class Operation02 {
    private int number = 0;
    private Lock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();

    public void increment() throws InterruptedException {
       lock.lock();
       try {
           if (number != 0) {
               condition.await();
           }
           ++number;
           System.out.println(Thread.currentThread().getName() + ",\t" + number);
           condition.signalAll();
       } catch (Exception e) {
           e.printStackTrace();
       } finally {
           lock.unlock();
       }
    }


    public void decrement() throws InterruptedException {
        lock.lock();
        try {
            if (number == 0) {
                condition.await();
            }
            number--;
            System.out.println(Thread.currentThread().getName() + ",\t" + number);
            condition.signalAll();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
}
