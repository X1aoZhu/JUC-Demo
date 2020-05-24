package com.zhu.demo;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/**
 * 多线程 callable接口
 *
 * @Author Zhu
 * @Date 2020/5/24 19:48
 */
public class CallableDemo {
    public static void main(String[] args) throws Exception {

        FutureTask<Integer> task = new FutureTask<>(new MyCallable());

        new Thread(task, "AA").start();

        System.out.println(task.get());

    }
}

class MyCallable implements Callable<Integer> {

    @Override
    public Integer call() throws Exception {
        System.out.println("Callable Interface");
        return 1024;
    }
}
