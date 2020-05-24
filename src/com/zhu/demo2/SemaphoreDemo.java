package com.zhu.demo2;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * JUC辅助类：信号灯
 * 实现：6辆汽车争抢3个停车位
 *
 * @Author Zhu
 * @Date 2020/5/24 20:20
 */
public class SemaphoreDemo {
    public static void main(String[] args) {
        int parkNum = 3;
        int carNum = 6;

        Semaphore semaphore = new Semaphore(parkNum);

        for (int i = 1; i <= carNum; i++) {
            new Thread(() -> {
                try {
                    semaphore.acquire();
                    System.out.println(Thread.currentThread().getName() + "\t停车");

                    TimeUnit.SECONDS.sleep(3);

                    System.out.println(Thread.currentThread().getName() + "\t释放资源----------");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }finally {
                    semaphore.release();
                }
            }, String.valueOf(i)).start();
        }

    }
}
