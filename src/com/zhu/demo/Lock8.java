package com.zhu.demo;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 多线程锁问题
 * 题目：经典多线程8锁
 *
 * @Author Zhu
 * @Date 2020/4/27 0:44
 */
public class Lock8 {
    public static void main(String[] args) throws InterruptedException {
        Phone phone = new Phone();

        new Thread(phone::sendEmail, "AA").start();

        Thread.sleep(200);

        new Thread(phone::sayHello, "BB").start();


    }
}

class Phone {
    private Lock lock = new ReentrantLock();

//    public void sendEmail() {
//        lock.lock();
//        try {
//            System.out.println("------------SendEmail");
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            lock.unlock();
//        }
//    }
//
//    public void sendSMS() {
//        lock.lock();
//        try {
//            System.out.println("------------SendSMS");
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            lock.unlock();
//        }
//    }

    synchronized void sendEmail() {
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("------------SendEmail");
    }

    synchronized void sendSMS() {
        System.out.println("------------sendSMS");
    }

    void sayHello() {
        System.out.println("Hello");
    }
}