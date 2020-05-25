package com.zhu.demo3;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * JUC异步回调
 *
 * @Author Zhu
 * @Date 2020/5/25 23:06
 */
public class CompletableFutureDemo {
    public static void main(String[] args) throws ExecutionException, InterruptedException {

        /**
         * 同步调用
         */
        CompletableFuture<Void> completableFuture = CompletableFuture.runAsync(() -> {
            System.out.println(Thread.currentThread().getName() + "\tCompletableFuture execute runAsync");
        });
        completableFuture.get();

        System.out.println("-----------------------------");

        /**
         * 异步调用
         */
        CompletableFuture<Integer> completableFuture1 = CompletableFuture.supplyAsync(() -> {
            System.out.println(Thread.currentThread().getName() + "\tCompletableFuture execute supplyAsync");
            // int num = 10 / 0;
            return 1024;
        });

        System.out.println(completableFuture1.whenComplete((t, u) -> {
            System.out.println("whenComplete, t: " + t);
            System.out.println("whenComplete, u: " + u);
        }).exceptionally(t -> {
            System.out.println("whenComplete exceptionally, message: " + t);
            return 4444;
        }).get());
    }
}
