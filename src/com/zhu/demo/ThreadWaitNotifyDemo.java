package com.zhu.demo;

/**
 * 问题：
 * 现在两个线程。可以操作一个初始值为0的变量，实现一个线程对改变量加一，一个线程对该变量减一。
 * 实现交替执行10轮后，变量初始值为0。
 * <p>
 * 1. 高内聚低耦合，线程操作资源类。如何实现线程通信
 *
 * @Author Zhu
 * @Date 2020/4/23 1:58
 */
public class ThreadWaitNotifyDemo {
    private static int count = 10;

    public static void main(String[] args) {
        Operation operation = new Operation();

        new Thread(() -> {
            for (int i = 0; i < count; i++) {
                try {
                    operation.increment();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "A").start();

        new Thread(() -> {
            for (int i = 0; i < count; i++) {
                try {
                    operation.decrement();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "B").start();

        new Thread(() -> {
            for (int i = 0; i < count; i++) {
                try {
                    operation.increment();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "C").start();

        new Thread(() -> {
            for (int i = 0; i < count; i++) {
                try {
                    operation.decrement();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "D").start();
    }
}

class Operation {
    private int number = 0;

    synchronized void increment() throws InterruptedException {
        while (number != 0) {
            this.wait();
        }
        number++;
        System.out.println(Thread.currentThread().getName() + ",\t" + number);
        this.notifyAll();
    }

    synchronized void decrement() throws InterruptedException {
        while (number == 0) {
            this.wait();
        }
        number--;
        System.out.println(Thread.currentThread().getName() + ",\t" + number);
        this.notifyAll();
    }
}
