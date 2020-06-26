package com.zhu.demo3;

import java.util.concurrent.*;


/**
 * JUC线程池 ThreadPool
 * <p>
 * 使用线程池初始化线程的三种方式
 *
 * @Author Zhu
 * @Date 2020/5/25 2:05
 */
public class MyThreadPoolDemo {
    public static void main(String[] args) {

        System.out.println(Runtime.getRuntime().availableProcessors());

        ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(2,
                Runtime.getRuntime().availableProcessors() + 1, 2L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(3),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy()
        );

        try {
            for (int i = 1; i <= 7; i++) {
                poolExecutor.execute(() -> System.out.println(Thread.currentThread().getName() + "\t受理业务"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            poolExecutor.shutdown();
        }
    }

    private static void threadPoolTest() {
        ExecutorService pool1 = Executors.newCachedThreadPool();
        ExecutorService pool2 = Executors.newSingleThreadExecutor();
        ExecutorService pool3 = Executors.newFixedThreadPool(3);

        try {
            for (int i = 1; i <= 10; i++) {
                pool3.execute(() -> System.out.println(Thread.currentThread().getName() + "\t受理业务"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool3.shutdown();
        }
    }
}
