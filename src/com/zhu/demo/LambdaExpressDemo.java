package com.zhu.demo;

/**
 * @Author Zhu
 * @Date 2020/4/23 1:31
 */
public class LambdaExpressDemo {
    public static void main(String[] args) {
        Foo foo = () -> System.out.println("Lambda Hello world");
        foo.say();

        ((Foo) () -> System.out.println("Hello World")).say();

        Foo foo1 = new Foo() {
            @Override
            public void say() {
                System.out.println("Hello World **********");
            }
        };
        foo1.say();

        //-----------------------------------------------------------------------------
        System.out.println("-----------------------------------------------------------");

        Foo1 f1 = (str1, str2) -> str1 + str2;
        System.out.println(f1.sub("Hello", "World"));
        System.out.println(f1.div(10, 5));
    }
}


interface Foo {
    void say();
}


interface Foo1 {
    String sub(String x, String y);

    default int div(int num1, int num2) {
        return num1 / num2;
    }
}