package com.zhu.demo3;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.BiConsumer;
import java.util.function.Function;

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
//            int num = 10 / 1;
            return 1024;
        });


//        completableFuture1.whenComplete(new BiConsumer<Integer, Throwable>() {
//            @Override
//            public void accept(Integer integer, Throwable throwable) {
//                System.out.println("whenComplete, accept:" + integer);
//                System.out.println("whenComplete, throwable: " + throwable);
//            }
//        });


//        System.out.println(completableFuture1.whenComplete((integer, throwable) -> {
//            System.out.println("whenComplete, integer: " + integer);
//            System.out.println("whenComplete, throwable: " + throwable);
//        }).exceptionally(throwable -> {
//            System.out.println("whenComplete exceptionally, message: " + throwable);
//            return 2020;
//        }).get());
//
//
        System.out.println(completableFuture1.whenComplete((t, u) -> {
            System.out.println("whenComplete, t: " + t);
            System.out.println("whenComplete, u: " + u);
        }).exceptionally(t -> {
            System.out.println("whenComplete exceptionally, message: " + t);
            return 4444;
        }).get());
    }
}
