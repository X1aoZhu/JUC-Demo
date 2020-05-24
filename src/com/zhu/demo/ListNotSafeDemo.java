package com.zhu.demo;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * @Author Zhu
 * @Date 2020/4/30 1:14
 */
public class ListNotSafeDemo {
    public static void main(String[] args) {
        listNotSafe();
    }

    private static void listNotSafe() {
        CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<>();

        int lessThreadCount = 3;
        int moreThreadCount = 100;
        for (int i = 0; i < moreThreadCount; i++) {
            new Thread(() -> {
                list.add(UUID.randomUUID().toString().substring(0, 8));
                System.out.println(list);
            }, String.valueOf(i)).start();
        }
    }
}
