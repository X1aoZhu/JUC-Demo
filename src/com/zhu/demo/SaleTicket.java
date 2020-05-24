package com.zhu.demo;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Ticket {
    private int count = 300;

    private Lock lock = new ReentrantLock();

    void saleTicket() {

        lock.lock();
        try {
            if (count > 0) {
                System.out.println(Thread.currentThread().getName() + ", 当前卖出第 " + count-- + ", 还剩下：" + count + "张");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

}


/**
 * @Author Zhu
 * @Date 2020/4/23 1:04
 */
public class SaleTicket {
    private static int count = 400;

    public static void main(String[] args) {

        Ticket ticket = new Ticket();
        new Thread(() -> {
            for (int i = 0; i < count; i++) {
                ticket.saleTicket();
            }
        }, "A").start();

        new Thread(() -> {
            for (int i = 0; i < count; i++) {
                ticket.saleTicket();
            }
        }, "B").start();

        new Thread(() -> {
            for (int i = 0; i < count; i++) {
                ticket.saleTicket();
            }
        }, "C").start();


//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                for (int i = 0; i < count; i++) {
//                    ticket.saleTicket();
//                }
//            }
//        }, "A").start();
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                for (int i = 0; i < count; i++) {
//                    ticket.saleTicket();
//                }
//            }
//        }, "B").start();
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                for (int i = 0; i < count; i++) {
//                    ticket.saleTicket();
//                }
//            }
//        }, "C").start();
    }
}
