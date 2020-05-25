package com.zhu.demo3;

import com.zhu.entity.User;

import java.util.Arrays;
import java.util.List;

/**
 * Java8 Stream流式计算
 * <p>
 * 实现：偶数id，年龄大于24，用户名转为大写，名字倒排序，只输出一个，用户名字
 *
 * @Author Zhu
 * @Date 2020/5/25 21:17
 */
public class StreamDemo {
    public static void main(String[] args) {

        User u1 = new User(2, "a", 24);
        User u2 = new User(4, "b", 25);
        User u3 = new User(6, "c", 26);
        User u4 = new User(8, "d", 27);
        User u5 = new User(10, "e", 28);

        List<User> list = Arrays.asList(u1, u2, u3, u4, u5);

        list.stream()
                .filter(t -> t.getId() % 2 == 0)
                .filter(t -> t.getAge() > 24)
                .map(u -> u.getName().toUpperCase())
                .sorted((o1, o2) -> o2.compareTo(o1))
                .limit(1)
                .forEach(System.out::println);
    }
}
