package com.zhu.demo;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * 多线程锁问题
 * 题目：经典多线程8锁
 * 1. 标准访问，请问先打印邮件还是短信？      email,sms
 * 2. 邮件方法暂停2秒，请问先打印邮件还是短信？     暂停2s email,sms
 * 3. 新增普通方法sayHello，请问先打印邮件还是sayHello？     sayHello,暂停2s email
 * 4. 两部手机，请问先打印邮件还是短信？phone1.endEmail, phone2.sendSMS?     sendSMS,暂停2s email
 * 5. 两个静态同步方法，一部手机，先邮件还是短信？       暂停2s email,sms
 * 6. 两个静态同步方法，两部手机，先邮件还是短信？       暂停2s email,sms
 * 7. 一个普通方法，一个静态方法，一部手机，首先邮件还是短信？      sms,暂停2s email
 * 8. 一个普通方法，一个静态方法，两部手机，首先邮件还是短信？      sms,暂停2s email
 *
 * @Author Zhu
 * @Date 2020/4/27 0:44
 */
public class Lock8 {
    public static void main(String[] args) {
        Phone phone = new Phone();
        Phone phone1 = new Phone();

        new Thread(() -> {
            phone.sendEmail();
        }, "AA").start();

        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        new Thread(() -> {
            phone1.sendSMS();
//            phone.sayHello();
//            phone1.sendSMS();
        }, "BB").start();
    }
}

class Phone {

    static synchronized void sendEmail() {
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("------------SendEmail");
    }

//    static synchronized void sendSMS() {
//        System.out.println("------------sendSMS");
//    }

    synchronized void sendSMS() {
        System.out.println("------------sendSMS");
    }

    void sayHello() {
        System.out.println("Hello");
    }
}