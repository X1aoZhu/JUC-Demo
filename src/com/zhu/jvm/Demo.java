package com.zhu.jvm;

import java.util.Random;
import java.util.UUID;

/**
 * @Author Zhu
 * @Date 2020/5/31 23:23
 */
public class Demo {
    public static void main(String[] args) {
        System.out.println(Runtime.getRuntime().availableProcessors());

        System.out.println(Runtime.getRuntime().maxMemory() / 1024 / 1024);
        System.out.println(Runtime.getRuntime().totalMemory() / 1024 / 1024);
        System.out.println(Runtime.getRuntime().freeMemory() / 1024 / 1024);
        String str = "Hello World";
        while (true) {
            str += str + UUID.randomUUID().toString().substring(0, 9) + new Random().nextInt(99999999);
        }
    }
}
