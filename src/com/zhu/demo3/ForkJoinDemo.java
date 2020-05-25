package com.zhu.demo3;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

/**
 * JUC ForkJoin
 * 实现：1+..+100
 *
 * @Author Zhu
 * @Date 2020/5/25 22:43
 */
public class ForkJoinDemo {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ForkJoinPool pool = new ForkJoinPool();

        MyTask myTask = new MyTask(0, 100);

        ForkJoinTask<Integer> forkJoinTask = pool.submit(myTask);

        System.out.println(forkJoinTask.get());

        pool.shutdown();
    }
}

class MyTask extends RecursiveTask<Integer> {

    private static final Integer ADJUST_VALUE = 10;

    private int begin;
    private int end;

    private int result;

    public MyTask(int begin, int end) {
        this.begin = begin;
        this.end = end;
    }

    @Override
    protected Integer compute() {
        if ((end - begin) <= ADJUST_VALUE) {
            for (int i = begin; i <= end; i++) {
                result += i;
            }
        } else {
            int middle = (begin + end) / 2;
            MyTask myTask1 = new MyTask(begin, middle);
            MyTask myTask2 = new MyTask(middle + 1, end);
            myTask1.fork();
            myTask2.fork();
            result = myTask1.join() + myTask2.join();
        }
        return result;
    }
}