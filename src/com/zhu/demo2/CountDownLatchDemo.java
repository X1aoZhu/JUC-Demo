package com.zhu.demo2;

import java.util.concurrent.CountDownLatch;

/**
 * JUC工具类：CountDownLatch
 * 实现功能：老师最后关门，同学先走，同学走完，老师才能走。
 * countdown 计数器
 *
 *
 * @Author Zhu
 * @Date 2020/5/24 20:00
 */
public class CountDownLatchDemo {
    public static void main(String[] args) throws InterruptedException {

        closeDoor();
    }

    private static void closeDoor() throws InterruptedException {
        int count = 6;
        CountDownLatch countDownLatch = new CountDownLatch(count);

        for (int i = 1; i <= count; i++) {
            new Thread(() -> {
                System.out.println(Thread.currentThread().getName() + "\t离开教室");
                countDownLatch.countDown();
            }, String.valueOf(i)).start();
        }

        countDownLatch.await();
        System.out.println(Thread.currentThread().getName() + "\t老师离开教室");
    }
}
