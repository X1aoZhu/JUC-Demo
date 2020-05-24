package com.zhu.demo2;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * JUC辅助类：CyclicBarrier循环栅栏
 * 实现：集齐七颗龙珠召唤神龙
 *
 * @Author Zhu
 * @Date 2020/5/24 20:06
 */
public class CyclicBarrierDemo {
    public static void main(String[] args) {

        int count = 7;

        // CyclicBarrier(int parties, Runnable barrierAction)
        CyclicBarrier barrier = new CyclicBarrier(count, () -> System.out.println("召唤神龙"));

        for (int i = 1; i <= count; i++) {
            final int num = i;
            new Thread(() -> {
                System.out.println(Thread.currentThread().getName() + "\t收集到第" + num + "颗龙珠");
                try {
                    barrier.await();
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                }
            }, String.valueOf(i)).start();

        }

    }
}
