package com.zhu.queue;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * @Author Zhu
 * @Date 2020/7/20 20:49
 */
public class BlockQueueDemo {
    public static void main(String[] args) throws InterruptedException {
        ArrayBlockingQueue<Integer> arrayBlockingQueue = new ArrayBlockingQueue<>(1);
//        arrayBlockingQueue.add(1);
//        arrayBlockingQueue.add(2);
//        System.out.println(arrayBlockingQueue.remove());
//        arrayBlockingQueue.remove();
//        System.out.println(arrayBlockingQueue.element());

//        System.out.println(arrayBlockingQueue.offer(1));
//        System.out.println(arrayBlockingQueue.offer(1));
//        System.out.println(arrayBlockingQueue.poll());
//        System.out.println(arrayBlockingQueue.poll());

        arrayBlockingQueue.put(1);
        arrayBlockingQueue.put(1);


    }
}
