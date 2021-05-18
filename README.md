# JUC-Demo
----

#### JUC 知识点总结
# 写在前面


1. 源码 GITHUB 地址：[源码地址][https://github.com/X1aoZhu/JUC-Demo][][]

---

# JUC 基础

## 1. 什么是线程和进程？举例说明

+ 进程是系统中正在运行的一个程序，是系统资源分配的独立实体，每个进程都拥有独立的地址空间，程序一旦运行就是进程。

+ 进程是一个具有一定独立功能的程序关于某个数据集合的一次运行活动。它是操作系统动态执行的基本单元，在传统的操作系统中，进程既是基本的分配单元，也是基本的执行单元。

  ---

+ 线程通常在一个进程中可以包含若干个线程，当然一个进程中至少有一个线程，不然没有存在的意义。

+ 线程可以利用进程所拥有的资源，在引入线程的操作系统中，通常都是把进程作为分配资源的基本单位，而把线程作为独立运行和独立调度的基本单位，由于线程比进程更小，基本上不拥有系统资源，故对它的调度所付出的开销就会小得多，能更高效的提高系统多个程序间并发执行的程度。

  ---

+ 举例：QQ / WeChat 就是一个进程，每一个聊天窗口就是一个个线程。

---

## 2. 线程的状态有哪些？

+ 线程有六种状态：新建，运行，阻塞，等待，定时等待，终结

+ `java.lang.Thread.State `   枚举类

+ ```java
  public enum State {
   
         NEW,
   
         RUNNABLE,
   
         BLOCKED,
   
         WAITING,
   
         TIMED_WAITING,
   
         TERMINATED;
     }
  ```

---

## 3. wait 和 sleep 的区别？

+ wait() 是Object 包提供的方法，sleep() 是Thread包下提供的方法
+ `java.lang.Thread#sleep()`
+ `java.lang.Object#wait()`
+ wait 是放开锁睡眠，睡眠结束从新争取资源
+ sleep 线程不会释放对象锁睡眠，睡眠结束不需要争取资源，整个程序在其睡眠期间阻塞等待

---

## 4. 什么是并发，什么是并行？

+ 并发：同一时刻多个线程访问同一个资源，多个线程对一个点。如：秒杀
+ 并行：多项任务同时进行。（一边... 一边...，泡方便面，电水壶烧水，一边撕调料倒入桶中）

---

## 5. JUC 常见异常

+ `java.util.ConcurrentModificationException`：集合多线程
+ `java.util.NoSuchElementException`：常发生在阻塞队列
+ `java.util.concurrent.RejectedExecutionException`：触发线程池默认拒绝策略，`AbortPolicy`

---

# JUC Lock接口

## 1. Lock 和 Synchronized 区别在哪？

+ Lock是个接口，而 synchronized 是java关键字，synchronized 是内置语言实现
+ synchronized在发生**异常**时，会自动释放线程占有的锁，因此不会导致死锁现象的发生；而Lock在发生异常时，如果没有主动通过unlock()去释放锁，则很有可能造成死锁现象，因此使用Lock时需要在finally 块中释放锁。
+ 通过Lock可以知道有没有成功获取锁，而synchronized却无法办到，不能判断锁状态
+ 在性能上来说，如果资源竞争不激烈的话，两者的性能是差不多的；而当资源竞争非常激烈（即有大量线程同时竞争）时，Lock的性能要远远优于synchronized
+ 少量同步可用 synchronized，大量同步时一般使用 Lock

---

## 2. 经典买票问题

+ synchronized 

  ```java
  public class SaleTicket {
  
      public static void main(String[] args) {
  
          Ticket ticket = new Ticket();
          int number = 2000;
  
          new Thread(() -> {
              for (int i = 0; i < number; i++) {
                  ticket.sale();
              }
          }, "A").start();
  
          new Thread(() -> {
              for (int i = 0; i < number; i++) {
                  ticket.sale();
              }
          }, "B").start();
  
          new Thread(() -> {
              for (int i = 0; i < number; i++) {
                  ticket.sale();
              }
          }, "C").start();
      }
  }
  
  
  class Ticket {
      private int number = 300;
  
      synchronized void sale() {
          if (number > 0) {
              System.out.println(Thread.currentThread().getName() 
                                 + "剩余：" + --number + "张");
          }
      }
  }
  ```

  ---

+ Lock

  ```java
  public class SaleTicket02 {
      public static void main(String[] args) {
          Ticket02 ticket = new Ticket02();
  
          int number = 4000;
  
          new Thread(() -> {
              for (int i = 0; i < number; i++) {
                  ticket.sale();
              }
          }, "AA").start();
  
          new Thread(() -> {
              for (int i = 0; i < number; i++) {
                  ticket.sale();
              }
          }, "BB").start();
  
          new Thread(() -> {
              for (int i = 0; i < number; i++) {
                  ticket.sale();
              }
          }, "CC").start();
      }
  }
  
  
  class Ticket02 {
      private Lock lock = new ReentrantLock();
  
      private int number = 3000;
  
      void sale() {
          lock.lock();
          try {
              if (number > 0) {
                  System.out.println(Thread.currentThread().getName() 
                                     + "剩余：" + --number + "张");
              }
          } catch (Exception e) {
              e.printStackTrace();
          } finally {
              lock.unlock();
          }
      }
  }
  ```

---

# JUC 线程间通信

## 1. 多线程的虚假唤醒



---

## 2. 面试题一：

> 问题： 现在两个线程。可以操作一个初始值为0的变量，实现一个线程对改变量加一，一个线程对该变量减一。实现交替执行10轮后，变量初始值为0。

+ synchronized 方式：

  ```java
  class Operation {
      private int number = 0;
  
      synchronized void increment() throws InterruptedException {
          // 必须为while，防止线程虚假唤醒
          while (number != 0) {
              this.wait();
          }
          System.out.println(Thread.currentThread().getName() + ",当前数+1, 为： " + ++number);
          this.notifyAll();
      }
  
      synchronized void decrement() throws InterruptedException {
          while (number != 1) {
              this.wait();
          }
          System.out.println(Thread.currentThread().getName() + ",当前数-1, 为： " + --number);
          this.notifyAll();
      }
  }
  
  public class ThreadWaitNotifyDemo {
      public static void main(String[] args) {
          int num = 11;
          Operation operation = new Operation();
  
          new Thread(() -> {
              for (int i = 1; i < num; i++) {
                  try {
                      operation.increment();
                  } catch (InterruptedException e) {
                      e.printStackTrace();
                  }
              }
          }, "AA").start();
  
          new Thread(() -> {
              for (int i = 1; i < num; i++) {
                  try {
                      operation.decrement();
                  } catch (InterruptedException e) {
                      e.printStackTrace();
                  }
              }
          }, "BB").start();
      }
  }
  ```

  ---

+ Lock 方式

  ```java
  public class ThreadWaitNotifyDemo02 {
      public static void main(String[] args) {
          int num = 10;
          Operation02 operation02 = new Operation02();
  
          new Thread(() -> {
              for (int i = 0; i < num; i++) {
                  operation02.increment();
              }
          }, "AA").start();
  
          new Thread(() -> {
              for (int i = 0; i < num; i++) {
                  operation02.decrement();
              }
          }, "BB").start();
      }
  }
  
  class Operation02 {
      private int number = 0;
  
      private ReentrantLock lock = new ReentrantLock();
      private Condition condition = lock.newCondition();
  
      void increment() {
          lock.lock();
          try {
              while (number != 0) {
                  condition.await();
              }
              System.out.println(Thread.currentThread().getName() + ",\t" + ++number);
              condition.signalAll();
          } catch (Exception e) {
              e.printStackTrace();
          } finally {
              lock.unlock();
          }
      }
  
      void decrement() {
          lock.lock();
          try {
              while (number != 1) {
                  condition.await();
              }
              System.out.println(Thread.currentThread().getName() + ",\t" + --number);
              condition.signalAll();
          } catch (Exception e) {
              e.printStackTrace();
          } finally {
              lock.unlock();
          }
      }
  }
  ```

---

## 3. 面试题二：

> 两个线程，一个线程打印1-52，另一个打印字母A-Z打印顺序为12A34B...5152Z，要求用线程间通信。

```java
public class ThreadWaitNotifyDemo03 {
    public static void main(String[] args) {
        Handler handler = new Handler();

        new Thread(handler::printNum, "打印1-52").start();

        new Thread(handler::printChar, "打印A-Z").start();

    }
}

class Handler {
    private static int num = 1;

    private Lock lock = new ReentrantLock();
    private Condition priNum = lock.newCondition();
    private Condition priChr = lock.newCondition();

    void printNum() {
        lock.lock();
        try {
            int total = 53;
            for (int i = 1; i < total; i++) {
                while (num % 3 == 0) {
                    priNum.await();
                }
                System.out.println(Thread.currentThread().getName() + 
                                   "===========>" + i);
                num++;
                priChr.signal();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    void printChar() {
        lock.lock();
        try {
            for (char i = 'A'; i <= 'Z'; i++) {
                while (num % 3 != 0) {
                    priChr.await();
                }
                System.out.println(Thread.currentThread().getName() + 
                                   "===========>" + i);
                num++;
                priNum.signal();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
}
```

---

# JUC 线程的定制化通信

## 1. 面试题一：

> 问题：启动三个线程，要求：AA打印5次，BB打印10次，CC打印15次

```java
public class ThreadAccessDemo {
    public static void main(String[] args) {
        int count = 10;
        ShareResources shareResources = new ShareResources();

        new Thread(() -> {
            for (int i = 0; i < count; i++) {
                shareResources.print5(i);
            }
        }, "AA").start();

        new Thread(() -> {
            for (int i = 0; i < count; i++) {
                shareResources.print10(i);
            }
        }, "BB").start();

        new Thread(() -> {
            for (int i = 0; i < count; i++) {
                shareResources.print15(i);
            }
        }, "CC").start();
    }
}

class ShareResources {

    private int number = 1;

    private ReentrantLock lock = new ReentrantLock();
    private Condition c1 = lock.newCondition();
    private Condition c2 = lock.newCondition();
    private Condition c3 = lock.newCondition();

    void print5(int count) {
        lock.lock();
        try {
            while (number != 1) {
                c1.await();
            }
            for (int i = 0; i < 5; i++) {
                System.out.println(Thread.currentThread().getName() + "\t第 " + (count + 1) + " 轮" +
                        ",第 " + (i + 1) + " 次");
            }
            number = 2;
            c2.signal();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    void print10(int count) {
        lock.lock();
        try {
            while (number != 2) {
                c2.await();
            }
            for (int i = 0; i < 10; i++) {
                System.out.println(Thread.currentThread().getName() + "\t第 " + (count + 1) + " 轮" +
                        ",第 " + (i + 1) + " 次");
            }
            number = 3;
            c3.signal();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    void print15(int count) {
        lock.lock();
        try {
            while (number != 3) {
                c3.await();
            }
            for (int i = 0; i < 15; i++) {
                System.out.println(Thread.currentThread().getName() + "\t第 " + (count + 1) + " 轮" +
                        ",第 " + (i + 1) + " 次");
            }
            number = 1;
            c1.signal();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
}
```

---

# JUC 集合类线程不安全

## 1. 面试题一：

> 题目：请举例说明集合类是不安全的

+ 举例代码：`ListNotSafeDemo`

  ```java
  public class ListNotSafeDemo {
      public static void main(String[] args) {
          listNotSafe();
      }
  
      static void listNotSafe() {
          List<String> list = new ArrayList<>();
  //      List<String> list = new ArrayList<>();
  //      List<String> list = Collections.synchronizedList(new ArrayList<>());
          
          for (int i = 1; i < 30; i++) {
              new Thread(() -> {
                  list.add(UUID.randomUUID().toString().substring(0, 8));
                  System.out.println(list);
              }, String.valueOf(i)).start();
          }
      }
  }
  ```

+ 源码举例：

  + List源码中，add方法并未加锁

  ```java
      // ArrayList 源码 add方法
  	public boolean add(E e) {
          ensureCapacityInternal(size + 1);  // Increments modCount!!
          elementData[size++] = e;
          return true;
      }
  
  ```

---

## 2. 怎样解决集合(List)线程问题，为什么？

+ 可以使用 Vector 类解决，但不使用

  + List 接口实现类 Vector可以解决线程安全问题，其所有方法都是 synchronized 修饰
  + Vector 集合类是JDK1.0时期的集合类，ArrayList 的前身，效率很低，不使用

  ---

+ 使用集合工具类提供的方法，将线程不安全集合转化为线程安全集合

  + ```java
    List<String> list = Collections.synchronizedList(new ArrayList<>());
    
    Map<Object, Object> map = Collections.synchronizedMap(new HashMap<>());
    
    Set<Object> set = Collections.synchronizedSet(new HashSet<>());
    
    ```

  + 不推荐使用，性能较低。

  + synchronized 是同步锁，无法同时读和写，所以性能较低

  ---

+ 使用 JDK提供的官方 JUC 类 `java.util.concurrent.CopyOnWriteArrayList`

  + JUC 写时复制技术，性能较高，推荐使用

  + ```java
    List<String> list = new CopyOnWriteArrayList<>();
    
    ```

---

## 3. JUC 写时复制技术

+ 简述：

  > + CopyOnWrite容器即写时复制的容器。
  > + 往一个容器添加元素的时候，不直接往当前容器Object[]添加，而是先将当前容器Object[]进行Copy，复制出一个新的容器Object[] newElements，然后向新的容器Object[] newElements里添加元素。
  > + 添加元素后，再将原容器的引用指向新的容器setArray(newElements)。
  > + 这样做的好处是可以对CopyOnWrite容器进行并发的读，而不需要加锁，因为当前容器不会添加任何元素。
  > + 所以CopyOnWrite容器也是一种读写分离的思想，读和写不同的容器。

  ---

+ 源码：

  > + `java.util.concurrent.CopyOnWriteArrayList`
  >
  > + ```java
  >   public boolean add(E e) {
  >       final ReentrantLock lock = this.lock;
  >       lock.lock();
  >       try {
  >        	//获取当前集合和长度   
  >           Object[] elements = getArray();
  >           int len = elements.length;
  >           //初始化一个新的长度为当前集合长度+1的新数组
  >           Object[] newElements = Arrays.copyOf(elements, len + 1);
  >           //添加的数据放入新的数组
  >           newElements[len] = e;
  >           //复制数据
  >           setArray(newElements);
  >           return true;
  >       } finally {
  >           lock.unlock;
  >       }
  >   }
  >   
  >   ```

---

## 4. 补充：证明Set线程问题和解决

+ 举例代码：

  ```java
  public class SetNotSafeDemo {
      public static void main(String[] args) {
          setNotSet();
      }
  
      private static void setNotSet() {
  //        HashSet<Object> set = new HashSet<>();
  
  //        Set<Object> set = Collections.synchronizedSet(new HashSet<>());
  
          /**
           * 对应TreeSet
           */
  //        Set<Object> set = new ConcurrentSkipListSet<>();
          
          /**
           * 对应HashSet
           */
          CopyOnWriteArraySet<Object> set = new CopyOnWriteArraySet<>();
  
          int threadTotal = 20;
  
          for (int i = 0; i < threadTotal; i++) {
              new Thread(() -> {
                  set.add(UUID.randomUUID().toString().substring(0, 8));
                  System.out.println(set);
              }, String.valueOf(i + 1)).start();
          }
      }
  }
  
  ```

  ---

+ 源码：

  + set 添加数据是没有 synchronized 或 lock 关键字修饰

  ```java
      public boolean add(E e) {
          return map.put(e, PRESENT)==null;
      }
  
  ```

  ---

+ 解决：

  > 1. 使用 `Collections.synchronizedSet`
  >
  >    ```java
  >    Set<Object> set = Collections.synchronizedSet(new HashSet<>());
  >    
  >    ```
  >
  > 2. 使用 `CopyOnWriteArraySet()`
  >
  >    + 对应 HashSet
  >
  >    ```java
  >    CopyOnWriteArraySet<Object> set = new CopyOnWriteArraySet<>();
  >    
  >    ```
  >
  > 3. 使用 JUC包的 `ConcurrentSkipListSet<>()`
  >
  >    + 对应 
  >
  >    ```java
  >    Set<Object> set = new ConcurrentSkipListSet<>();
  >    
  >    ```

---

## 5. 补充：证明Map线程问题和解决

+ 证明代码

  ```java
  public class MapNotSafeDemo {
      public static void main(String[] args) {
          mapNotSafe();
      }
  
      private static void mapNotSafe() {
          HashMap<Object, Object> map = new HashMap<>();
  
          int threadTotal = 20;
  
          for (int i = 0; i < threadTotal; i++) {
              new Thread(() -> {
                  map.put(UUID.randomUUID().toString().substring(0, 4), 
                          UUID.randomUUID().toString().substring(0, 8));
                  System.out.println(map);
              }, String.valueOf(i + 1)).start();
          }
      }
  
  ```

  ---

+ 源码

  + map 添加数据是没有 synchronized 或 lock 关键字修饰

  ```java
      public V put(K key, V value) {
          return putVal(hash(key), key, value, false, true);
      }
  
  ```

  ---

+ 解决

  > 1. 使用 `Collections.synchronizedMap`
  >
  >    ```java
  >    Map<Object, Object> map = Collections.synchronizedMap(new HashMap<>());
  >    
  >    ```
  >
  > 2. 使用 JUC包的`ConcurrentHashMap()`
  >
  >    ```java
  >    Map<Object, Object> map = new ConcurrentHashMap<>();
  >    
  >    ```
  >
  > 3. 使用 HashTable
  >
  >    + HashTable也是Map的实现类，其 put 方法被 synchronized 修饰
  >
  >    ```java
  >    Map<Object, Object> map = new Hashtable<>();
  >    
  >    ```

  ---

+ JUC包的结局方案性能最优

+ [参考博客][https://www.cnblogs.com/supiaopiao/p/9341625.html]

---

# JUC 多线程8锁

## 1. Code Demo

```java
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
            phone.sendSMS();
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

```

---

## 2. Question：

+ 标准访问，请问先打印还是邮件还是短信？
  + 首先邮件，然后短信
+ 在邮件方法内睡四秒，先打印短信还是邮件？
  + 首先停四秒，先邮件，后短信
+ 普通的hello方法，是先打邮件还是hello？
  + 先执行普通方法，后执行邮件
+ 现在有两部手机，先打印短信还是邮件？
  + 先执行线程没有延迟的。
  + 先短信，延迟3秒，后邮件（邮件方法线程睡眠4秒）
+ 两个静态同步方法，1部手机，先打印短信还是邮件？
  + 首先停四秒，先邮件，后短信
+ 两个静态同步方法，2部手机，先打印短信还是邮件？
  + 首先停四秒，先邮件，后短信
+ 1个静态同步方法，1个普通同步方法，1部手机，先打印短信还是邮件？
  + 先短信，停四秒，后邮件
+ 1个静态同步方法，1个普通同步方法，2部手机，先打印短信还是邮件？
  + 先短信，停四秒，后邮件

---

## 3. 结论

+ 一个对象里面如果有多个 synchronized 方法，某一个时刻内，只要一个线程去调用该类其中的一个synchronized方法了，其它的线程都只能等待，换句话说，某一个时刻内，只能有唯一一个线程去访问这些synchronized方法

+ 锁的是当前对象this，被锁定后，其它的线程都不能进入到当前对象的其它的synchronized方法

+ 例子：虽然手机有多个功能，但是同一时间只能有一个人去操作当前手机，即使手机功能非常多，其他人也只能等待。

  ---

+ 加个普通方法后发现和同步锁无关，二者之间没有任何联系，执行顺序不受影响。

+ 相当于手机和手机壳的关系

  ---

+ 换成两个对象之后，即使加锁，也不是同一把锁，所以相互之间不受影响，各自执行。

+ 例子：两部手机，两个人用，二者之间不受影响

  ---

+ synchronized实现同步的基础：Java中的每一个对象都可以作为锁。

  + 对于普通同步方法，锁是当前实例对象。（this）
  + 对于静态同步方法，锁是当前类的Class对象。
  + 对于同步方法块，锁是Synchonized括号里配置的对象

  ---

+ 当一个线程试图访问同步代码块时，它首先必须得到锁，退出或抛出异常时必须释放锁。也就是说如果一个实例对象的非静态同步方法获取锁后，该实例对象的其他非静态同步方法必须等待获取锁的方法释放锁后才能获取锁，可是别的实例对象的非静态同步方法因为跟当前实例对象的非静态同步方法用的是不同的锁，所以毋须等待该实例对象已获取锁的非静态同步方法释放锁就可以获取他们自己的锁。

+ 所有的静态同步方法用的也是同一把锁——类对象本身（Class），这两把锁是两个不同的对象，所以静态同步方法与非静态同步方法之间是不会有竞态条件的。

+ 但是一旦一个静态同步方法获取锁后，其他的静态同步方法都必须等待该方法释放锁后才能获取锁，而不管是同一个实例对象的静态同步方法之间，还是不同的实例对象的静态同步方法之间，只要它们同一个类的实例对象！

+ 思考：static 修饰的属性方法和普通属性方法加载时间。

----

# JUC 获取线程第三种方式

## 1. 面试题：

+ Java中获取线程的方法有几种？

  > 1. 继承 Thread 类
  > 2. 实现 Runnable 接口
  > 3. 实现 Callable 接口
  > 4. 使用 Java线程池获得
  >
  > ---
  >
  > + 传统的是继承 thread 类和实现 runnable 接口，java5以后又有实现 callable 接口和 java的线程池获得

  ---

+ 多线程中，Callable 接口和 Runnable 接口的区别？

  > 1. Runnable 接口时 java.lang 包下，Java1.1版本已经出现，而 Callable 接口在JDK1.5加入，是   java.util.concurrent 包下，两个都是函数式接口。
  >
  > 2. Runnable 接口方法返回类型为 void，Callable 接口方法返回类型为具体实现类型，是泛型
  >
  > 3. Runnable 接口方法为 run()，Callable 接口方法为 call()
  >
  > 4. Runnable 不能抛异常，Callable 可以抛异常
  >
  > 5. ```java
  >    class MyRunable implements Runnable {
  >        @Override
  >        public void run() {}
  >    }
  >    
  >    class MyCallable implements Callable {
  >        @Override
  >        public Object call() throws Exception {
  >            return null;
  >        }
  >    }
  >    
  >    ```

  ---

+ Callable 是否可以替代 Runnable 接口？

  > 1. 不能，Thread 类没有提供 Callable 接口相关构造方法，参照 JDK API
  > 2. ![image-20200523105418711](C:\Users\18380\AppData\Roaming\Typora\typora-user-images\image-20200523105418711.png)

---

## 2. Callable接口使用

+ 查看 JDK API，Thread 类没有提供关于Callable接口的构造器

+ 查看 Runnable 接口API，该接口有多个实现类，其中有一个 FutureTask 实现类，该类提供 Callable 接口相关的构造器。

+ ```java
  public interface RunnableFuture<V> extends Runnable, Future<V> {
      /**
       * Sets this Future to the result of its computation
       * unless it has been cancelled.
       */
      void run();
  }
  
  public class FutureTask<V> implements RunnableFuture<V> {
      //....
      
      public FutureTask(Callable<V> callable) {
          if (callable == null)
              throw new NullPointerException();
          this.callable = callable;
          this.state = NEW;       // ensure visibility of callable
      }
  }
  
  @FunctionalInterface
  public interface Callable<V> {
      /**
       * Computes a result, or throws an exception if unable to do so.
       *
       * @return computed result
       * @throws Exception if unable to compute a result
       */
      V call() throws Exception;
  }
  
  ```

+ 是多态的最典型的应用

----

## 3. FutureTask原理

+ 在主线程中需要执行比较耗时的操作时，但又不想阻塞主线程时，可以把这些作业交给Future对象在后台完成，当主线程将来需要时，就可以通过Future对象获得后台作业的计算结果或者执行状态。
+ 一般FutureTask多用于耗时的计算，主线程可以在完成自己的任务后，再去获取结果。异步调用。
+ 仅在计算完成时才能检索结果；如果计算尚未完成，则阻塞 get 方法，也就是说，当 FutureTask 类处理任务还未结束完成时 ，就调用 get() 方法获取计算机结果，此时，程序阻塞，等待计算结果。
+ 一旦计算完成，就不能再重新开始或取消计算。get方法而获取结果只有在计算完成时获取，否则会一直阻塞直到任务转入完成状态，然后会返回结果或者抛出异常。 
+ FutureTask 类只计算一次，多个线程调用 FutureTask 只执行一次。一般情况下，get() 获取计算结果应放在最后。

---

```java
public class CallableDemo {
    public static void main(String[] args) throws Exception {
        
        FutureTask<Integer> futureTask = new FutureTask<>(() -> {
            System.out.println(Thread.currentThread().getName());
            TimeUnit.SECONDS.sleep(4);
            return 1024;
        });

        new Thread(futureTask, "AA").start();

        System.out.println(futureTask.get());
    }
}

```

```java
public class CallableDemo {
    public static void main(String[] args) throws Exception {
        FutureTask<Integer> task = new FutureTask<>(new MyCallable());
        new Thread(task, "BB").start();

        System.out.println(task.get());
    }
}

class MyCallable implements Callable<Integer> {

    @Override
    public Integer call() throws Exception {
        System.out.println("Callable接口使用");
        return 200;
    }
}

```

---

# JUC 辅助类

## 1. CountDownLatch 减少计数

+ 原理

  > 2. 其它线程调用countDown方法会将计数器减1（调用countDown方法的线程不会阻塞）
  > 3. 当计数器的值变为0时，因await方法阻塞的线程会被唤醒，继续执行

---

+ 代码实现

  ```java
  /**
   * JUC辅助类: CountDownLatch
   * 实现：六名同学离开教室，班长最后离开
   *
   * @Description: 让一些线程阻塞直到另一些线程完成一系列操作后才被唤醒。
   * @Author Zhu
   * @Date 2020/5/23 14:15
   */
  public class CountDownLatchDemo {
      public static void main(String[] args) throws InterruptedException {
          int count = 6;
          CountDownLatch countDownLatch = new CountDownLatch(count);
  
          for (int i = 0; i < count; i++) {
              new Thread(() -> {
                  System.out.println(Thread.currentThread().getName()
                                     + "\t同学离开");
                  countDownLatch.countDown();
              }, String.valueOf(i + 1)).start();
          }
  
          countDownLatch.await();
          System.out.println(Thread.currentThread().getName() + "\t，班长离开");
      }
  }
  
  ```

---

## 2. CyclicBarrier 循环栅栏

+ 原理

  > 1. CyclicBarrier 的字面意思是可循环（Cyclic）使用的屏障（Barrier）
  > 2. 让一组线程到达一个屏障（也可以叫同步点）时被阻塞，直到最后一个线程到达屏障时，屏障才会开门，所有被屏障拦截的线程才会继续干活。线程进入屏障通过CyclicBarrier的await()方法。

  ---

+ 代码实现

  ```java
  /**
   * JUC辅助类：CyclicBarrier循环栅栏
   * 实现：集齐七颗龙珠召唤神龙
   *
   * @Author Zhu
   * @Date 2020/5/23 14:37
   */
  public class CyclicBarrierDemo {
      public static void main(String[] args) {
          int count = 7;
  		
          // CyclicBarrier(int parties, Runnable barrierAction) 
          CyclicBarrier cyclicBarrier = new CyclicBarrier(count, () -> {
              System.out.println("集齐七颗龙珠召唤神龙");
          });
  
          for (int i = 1; i <= count; i++) {
              new Thread(() -> {
                  System.out.println(Thread.currentThread().getName() 
                                     + "\t星龙珠被收集");
                  try {
                      cyclicBarrier.await();
                  } catch (InterruptedException | BrokenBarrierException e) {
                      e.printStackTrace();
                  }
              }, String.valueOf(i)).start();
          }
      }
  }
  
  
  ```

---

## 3. Semaphore 信号灯

+ 原理

  > 1. 在信号量上我们定义两种操作：
  >    + acquire（获取）：当一个线程调用acquire操作时，它要么通过成功获取信号量（信号量减1），要么一直等下去，直到有线程释放信号量，或超时。
  >    + release（释放）：实际上会将信号量的值加1，然后唤醒等待的线程。
  > 2. 信号量主要用于两个目的，一个是用于多个共享资源的互斥使用，另一个用于并发线程数的控制。
  > 3. 就是控制线程并发数

  ---

+ 代码实现

  ```java
  /**
   * JUC辅助类：信号灯
   * 实现：6辆汽车争抢3个停车位
   *
   * @Author Zhu
   * @Date 2020/5/23 14:48
   */
  public class SemaphoreDemo {
      public static void main(String[] args) {
          int parkTotal = 3;
          int carNum = 6;
  
          Semaphore semaphore = new Semaphore(parkTotal);
  
          for (int i = 1; i <= carNum; i++) {
              new Thread(() -> {
                  try {
                      semaphore.acquire();
  
                      System.out.println(Thread.currentThread().getName()
                                         + "\t号车驶入停车位!!!");
  
                      TimeUnit.SECONDS.sleep(2);
  
                      System.out.println(Thread.currentThread().getName()
                                         + "\t号车离开停车位****");
                  } catch (InterruptedException e) {
                      e.printStackTrace();
                  } finally {
                      semaphore.release();
                  }
              }, String.valueOf(i)).start();
          }
      }
  }
  
  ```

  ---

+ 扩展

  1. 当 `Semaphore semaphore = new Semaphore(num)` 线程数为1，即num为1时，和synchronized没区别。

---

# JUC ReadWriteLock 读写锁

+ API：All Known Implementing Classes ==> ReentrantReadWriteLock

---

## 1. 读锁和写锁

+ 读锁：共享锁，多个线程可以加多把锁，保证性能。
+ 写锁：独占锁，多个线程争抢一把锁
+ 写操作：独占，保证数据一致性；读操作：共享，保证性能
+ 多个线程同时读一个资源类没有问题，为了满足并发量，读取共享资源可以同时进行
+ 如果有一个线程想去写共享资源，就不应该再由其他线程对该资源进行读或写
+ 总结：
  + 读 - 读 能共存
  + 读 - 写 不能共存
  + 写 - 写 不能共存

---

## 2. 代码实现

```java
/**
 * JUC读写锁，简单缓存demo
 * 五个线程分别读和写
 *
 * @Author Zhu
 * @Date 2020/5/24 20:55
 */
public class ReadWriteLockDemo {
    public static void main(String[] args) {
        MyCache myCache = new MyCache();
        int count = 5;

        for (int i = 1; i <= count; i++) {
            final int num = i;
            new Thread(() -> {
                myCache.put(num + "", 
                            UUID.randomUUID().toString().substring(0, 8));
            }, String.valueOf(i)).start();
        }

        for (int i = 1; i <= count; i++) {
            final int num = i;
            new Thread(() -> {
                myCache.get(num + "");
            }, String.valueOf(i)).start();
        }

    }
}

class MyCache {
    private volatile Map<String, Object> map = new HashMap<>();

    private ReadWriteLock lock = new ReentrantReadWriteLock();
    void put(String key, Object value) {
        lock.writeLock().lock();
        try {
            System.out.println(Thread.currentThread().getName() 
                               + "\t,写数据," + key);
            map.put(key, value);
            System.out.println(Thread.currentThread().getName() 
                               + "\t,写数据完成");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.writeLock().unlock();
        }
    }

    void get(String key) {
        lock.readLock().lock();
        try {
            System.out.println(Thread.currentThread().getName() 
                               + "\t,读数据," + key);
            Object val = map.get(key);
            System.out.println(Thread.currentThread().getName() 
                               + "\t,读数据完成，值为：" + val);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.readLock().unlock();
        }
    }
}

```

---

# JUC BlockingQueue 阻塞队列

+ java.util.concurrent ，Interface  BlockingQueue<E>

## 1. What & Why

+ What

  > 1. 先进先出，参照MQ，特殊的MQ
  > 2. 两种情况：必须要阻塞  or  不得不阻塞
  > 3. 当队列是空的，从队列中获取元素的操作将会被阻塞
  > 4. 当队列是满的，向队列中添加元素的操作将会被阻塞
  > 5. 试图从空的队列中获取元素的线程将会被阻塞，直到其他线程往空的队列插入新的元素
  > 6. 试图向已满的队列中添加新元素的线程将会被阻塞，直到其他线程从队列中移除一个或多个元素或者完全清空，使队列变得空闲起来并后续新增

  ---

+ Why

  > 1. 在多线程领域：所谓阻塞，在某些情况下会挂起线程（即阻塞），一旦条件满足，被挂起的线程又会自动被唤起
  > 2. 好处是我们不需要关心什么时候需要阻塞线程，什么时候需要唤醒线程，因为这一切BlockingQueue都给你一手包办了
  > 3. 在concurrent包发布以前，在多线程环境下，我们每个程序员都必须去自己控制这些细节，尤其还要兼顾效率和线程安全，而这会给我们的程序带来不小的复杂度。

---

## 2. BlockingQueue 架构图

![image-20200523162106752](C:\Users\18380\AppData\Roaming\Typora\typora-user-images\image-20200523162106752.png)

---

## 3. BlockingQueue 实现

+ **ArrayBlockingQueue：由数组结构组成的有界阻塞队列。**
+ **LinkedBlockingQueue：由链表结构组成的有界（但大小默认值为integer.MAX_VALUE）阻塞队列。**
+ **SynchronousQueue：不存储元素的阻塞队列，也即单个元素的队列。**

---

+ PriorityBlockingQueue：支持优先级排序的无界阻塞队列。
+ DelayQueue：使用优先级队列实现的延迟无界阻塞队列。
+ LinkedTransferQueue：由链表组成的无界阻塞队列。
+ LinkedBlockingDeque：由链表组成的双向阻塞队列。

---

## 4. BlockingQueue 核心方法

+ 抛出异常

  > 1. add()：当阻塞队列满时，再往队列里add插入元素会抛 IllegalStateException:Queue full
  > 2. remove()：当阻塞队列空时，再往队列里remove移除元素会抛 NoSuchElementException
  > 3. element()：检察元素，并不会改变队列中元素个数。如果当前队列中没有元素，那么会报错，NoSuchElementException

  ---

+ 特殊值

  > 1. offer()：插入方法，成功ture失败false，不会抛异常
  > 2. poll()：移除方法，成功返回出队列的元素，队列里没有就返回null，不抛异常
  > 3. peek()：检察元素，并不会改变队列中元素个数。如果有，输入队列元素，如果没有，不会抛异常，只返回null。

  ---

+ 阻塞

  > 1. put()：当阻塞队列满时，生产者线程继续往队列里put元素，队列会一直阻塞生产者线程直到put数据 or 响应中断退出。阻塞队列已满，一直阻塞，程序不会结束
  > 2. take()：当阻塞队列空时，消费者线程试图从队列里take元素，队列会一直阻塞消费者线程直到队列可用。阻塞队列为空，那么会一直阻塞，不会结束。

  ---

+ 超时

  > 1. offer(e,time,unit)，poll(time,unit)
  > 2. 当阻塞队列满时，队列会阻塞生产者线程一定时间，超过限时后生产者线程会退出，返回false

---

## 5. 代码实现

+ [代码实现][https://github.com/X1aoZhu/JUC-Demo]

---

# JUC ThreadPool 线程池

## 1. 优势和特点

+ 优势

  + 线程池做的工作只要是控制运行的线程数量，处理过程中将任务放入队列，然后在线程创建后启动这些任务，如果线程数量超过了最大数量，超出数量的线程排队等候，等其他线程执行完毕，再从队列中取出任务来执行。

  ---

+ 特点

  > 1. 线程复用；控制最大并发数；管理线程
  >
  >    ---
  >
  > 2. 降低资源消耗。通过重复利用已创建的线程降低线程创建和销毁造成的销耗。
  >
  > 3. 提高响应速度。当任务到达时，任务可以不需要等待线程创建就能立即执行。
  >
  > 4. 提高线程的可管理性。线程是稀缺资源，如果无限制的创建，不仅会销耗系统资源，还会降低系统的稳定性，使用线程池可以进行统一的分配，调优和监控。

---

## 2. 初始化

+ `Executors.newFixedThreadPool(int)`

  > 1. 执行长期任务性能好，创建一个线程池，一池有N个固定的线程，有固定线程数的线程
  >
  > 2. ```java
  >    private static void threadPoolTest() {
  >        ExecutorService pool3 = Executors.newFixedThreadPool(3);
  >    
  >        try {
  >            for (int i = 1; i <= 10; i++) {
  >                pool3.execute(() -> System.out.println(
  >                    Thread.currentThread().getName() + "\t受理业务"));
  >            }
  >        } catch (Exception e) {
  >            e.printStackTrace();
  >        } finally {
  >            pool3.shutdown();
  >        }
  >    }
  >    
  >    ```

  ---

+ `Executors.newSingleThreadExecutor()`

  > 1. 一个任务一个任务的执行，一池一线程
  >
  > 2. ```java
  >    private static void threadPoolTest() {
  >        ExecutorService pool2 = Executors.newSingleThreadExecutor();
  >    
  >        try {
  >            for (int i = 1; i <= 10; i++) {
  >                pool2.execute(() -> System.out.println(
  >                    Thread.currentThread().getName() + "\t受理业务"));
  >            }
  >        } catch (Exception e) {
  >            e.printStackTrace();
  >        } finally {
  >            pool2.shutdown();
  >        }
  >    }
  >    
  >    ```

  ---

+ `Executors.newCachedThreadPool()`

  > 1. 执行很多短期异步任务，线程池根据需要创建新线程，但在先前构建的线程可用时将重用它们。可扩容，遇强则强
  >
  > 2. ```java
  >    private static void threadPoolTest() {
  >        ExecutorService pool1 = Executors.newCachedThreadPool();
  >    
  >        try {
  >            for (int i = 1; i <= 10; i++) {
  >                pool1.execute(() -> System.out.println(
  >                    Thread.currentThread().getName() + "\t受理业务"));
  >            }
  >        } catch (Exception e) {
  >            e.printStackTrace();
  >        } finally {
  >            pool1.shutdown();
  >        }
  >    }
  >    
  >    ```

---

## 3. ThreadPoolExecutor底层原理

```java
public static ExecutorService newSingleThreadExecutor(ThreadFactory threadFactory) {
    return new FinalizableDelegatedExecutorService
        (new ThreadPoolExecutor(1, 1,
                                0L, TimeUnit.MILLISECONDS,
                                new LinkedBlockingQueue<Runnable>(),
                                threadFactory));
}


public static ExecutorService newCachedThreadPool() {
    return new ThreadPoolExecutor(0, Integer.MAX_VALUE,
                                  60L, TimeUnit.SECONDS,
                                  new SynchronousQueue<Runnable>());
}


public static ExecutorService newCachedThreadPool(ThreadFactory threadFactory) {
    return new ThreadPoolExecutor(0, Integer.MAX_VALUE,
                                  60L, TimeUnit.SECONDS,
                                  new SynchronousQueue<Runnable>(),
                                  threadFactory);
}

```

---

## 4. 线程池的7大参数

| 参数            | 意义                                                         |
| --------------- | ------------------------------------------------------------ |
| corePoolSize    | 线程池中的常驻核心线程数                                     |
| maximumPoolSize | 线程池中能够容纳同时执行的最大线程数，此值必须大于等于1      |
| keepAliveTime   | 多余的空闲线程的存活时间当前池中线程数量超过corePoolSize时，当空闲时间达到keepAliveTime时，多余线程会被销毁直到只剩下corePoolSize个线程为止 |
| unit            | keepAliveTime的单位                                          |
| workQueue       | 任务队列，被提交但尚未被执行的任务                           |
| threadFactory   | 表示生成线程池中工作线程的线程工厂，用于创建线程，一般默认的即可 |
| handler         | 拒绝策略，表示当队列满了，并且工作线程大于等于线程池的最大线程数（maximumPoolSize）时如何来拒绝请求执行的runnable的策略 |

---

```java
public ThreadPoolExecutor(int corePoolSize,
                          int maximumPoolSize,
                          long keepAliveTime,
                          TimeUnit unit,
                          BlockingQueue<Runnable> workQueue,
                          ThreadFactory threadFactory) {
    this(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue,
         threadFactory, defaultHandler);
}

public ThreadPoolExecutor(int corePoolSize,
                          int maximumPoolSize,
                          long keepAliveTime,
                          TimeUnit unit,
                          BlockingQueue<Runnable> workQueue,
                          ThreadFactory threadFactory,
                          RejectedExecutionHandler handler) {
    if (corePoolSize < 0 ||
        maximumPoolSize <= 0 ||
        maximumPoolSize < corePoolSize ||
        keepAliveTime < 0)
        throw new IllegalArgumentException();
    if (workQueue == null || threadFactory == null || handler == null)
        throw new NullPointerException();
    this.acc = System.getSecurityManager() == null ?
        null :
    AccessController.getContext();
    this.corePoolSize = corePoolSize;
    this.maximumPoolSize = maximumPoolSize;
    this.workQueue = workQueue;
    this.keepAliveTime = unit.toNanos(keepAliveTime);
    this.threadFactory = threadFactory;
    this.handler = handler;
}

```

----

## 5. 线程池工作原理

+ 初始化线程池对象时，线程池中的线程数为0，系统不会真正的创建核心线程（corePoolSize）实例。类似于懒加载思想，而是当程序调用了execute() 方法时，系统才真正的初始化核心线程
+ 当调用 execute() 方法添加一个请求任务时，线程池会做出如下判断
  + 如果当前运行的线程数小于核心线程数（corePoolSize），会创建新的核心线程执行任务
  + 如果正在运行的线程数量大于（扩容）或等于corePoolSize，那么将这个任务放入队列；
  + 如果这个时候队列满了且正在运行的线程数量还小于maximumPoolSize，那么还是要创建非核心线程立刻运行这个任务，**注意：这里创建非核心线程处理的任务是刚刚新来的任务，而之前的在队列中阻塞的任务依旧在阻塞，创建的新线程默认优先执行外部的新任务，处理完成之后，再从队列取出任务处理**
  + **创建新线程永远默认处理新任务**
  + 如果队列满了且正在运行的线程数量大于或等于 maximumPoolSize，那么线程池会启动饱和拒绝策略来执行
+ 当一个线程完成任务时，它会从队列中取下一个任务来执行。
+ 当一个线程无事可做超过一定的时间（keepAliveTime）时，线程会判断：
  + 如果当前运行的线程数大于corePoolSize，那么这个线程就被停掉。
  + 所以线程池的所有任务完成后，它最终会收缩到corePoolSize的大小。

## 6. 线程池选择，为什么？

+ 在工作中， 单一方式`newSingleThreadExecutor`，固定数方式`newFixedThreadPool`，可变方式`newCachedThreadPool`的三种方式，**都不会使用。**
+ 实际工作生产中，以上三种线程初始化方式，都不会使用，只能使用自定义方式

---

+ 阿里巴巴 Java 开发手册中，明确指出，以上三种创建线程池的方法，都有BUG

  > + FixedThreadPool 和 SingleThreadPool
  >
  >   + ```java
  >     public static ExecutorService newFixedThreadPool(int nThreads) {
  >         return new ThreadPoolExecutor(nThreads, nThreads,
  >                                       0L, TimeUnit.MILLISECONDS,
  >                                       new LinkedBlockingQueue<Runnable>());
  >     }
  >     
  >     public static ExecutorService newSingleThreadExecutor() {
  >         return new FinalizableDelegatedExecutorService
  >             (new ThreadPoolExecutor(1, 1,
  >                                     0L, TimeUnit.MILLISECONDS,
  >                                     new LinkedBlockingQueue<Runnable>()));
  >     }
  >     
  >     public LinkedBlockingQueue() {
  >         this(Integer.MAX_VALUE);
  >     }
  >     
  >     ```
  >
  >   + 一池N线程和一池一线程，其默认阻塞队列为`LinkedBlockingQueue`，而队列默认初始化长度为整型的最大值，为21亿多，那么一旦出现高并发，阻塞队列虽然没有塞满，但是机器内存已经满了，极易出现 OOM
  >
  >   ---
  >
  > + CachedThreadPool
  >
  >   + ```java
  >     public static ExecutorService newCachedThreadPool() {
  >         return new ThreadPoolExecutor(0, Integer.MAX_VALUE,
  >                                       60L, TimeUnit.SECONDS,
  >                                       new SynchronousQueue<Runnable>());
  >     }
  >     
  >     ```
  >
  >   + 可扩展线程池，虽然阻塞队列没有问题，但其最大线程池为`Integer.MAX_VALUE`，同样会造成OOM

---

## 7. 手写线程池

```java
ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(2,
         Runtime.getRuntime().availableProcessors() + 1, 
         2L, TimeUnit.SECONDS,
         new LinkedBlockingQueue<>(3),
         Executors.defaultThreadFactory(),
         new ThreadPoolExecutor.AbortPolicy()
);

```

---

## 8. 线程池拒绝策略

+ AbortPolicy（AbortPolicy默认）

  + 直接抛出 RejectedExecutionException 异常阻止系统正常运行
  + 如果当前请求大于`maximumPoolSize + BlockQueue长度`时，系统直接回抛出异常，终止运行
  + `java.util.concurrent.RejectedExecutionException`

  ---

+ CallerRunsPolicy

  + “调用者运行”一种调节机制，该策略既不会抛弃任务，也不会抛出异常，而是将某些任务回退到调用者，从而降低新任务的流量。
  + 当前线程无法处理过多的请求时，直接将请求返回给调用者，也就是main线程执行。整个程序不会抛出异常。

  ---

+ DiscardOldestPolicy

  + 抛弃队列中等待最久的任务，然后把当前任务加人队列中，尝试再次提交当前任务
  + 程序不会抛异常终止

  ---

+ DiscardPolicy

  + 该策略默默地丢弃无法处理的任务，不予任何处理也不抛出异常。如果允许任务丢失，这是最好的一种策略。
  + 如果不要求强一致性，该策略最实用
  + 同样的，也不会抛出异常

---

```java
public class MyThreadPoolDemo {
    public static void main(String[] args) {
		
        // 返回当前机器硬件的最大线程数
        System.out.println(Runtime.getRuntime().availableProcessors());

        ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(2,
                Runtime.getRuntime().availableProcessors() + 1,
                3L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(3),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.DiscardOldestPolicy());

        try {
            for (int i = 0; i < 20; i++) {
                poolExecutor.execute(() -> System.out.println(
                    Thread.currentThread().getName() + "\t受理业务"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            poolExecutor.shutdown();
        }
    }
}

```

---

## 9. 扩展

+ 线程崩溃的临界值为：maximumPoolSize + BlockQueue长度

+ 在初始化自定义线程池时，如何确定核心线程数？

  + 首先区分当前线程池是CPU密集型方式工作，还是IO密集型方式工作

  + 如果是CPU密集型方式，那么最大线程数应该设置为当前机器CPU的最大线程数 + 1

    + ```java
      Runtime.getRuntime().availableProcessors() + 1
      
      ```

  + 如果是IO密集型，核心线程数 = CPU核数 / （1-阻塞系数）   例如阻塞系数 0.8，CPU核数为4

  + 

---

# JUC 分支合并框架

## 1. 解释

+ 将一个复杂任务，分为多个小任务，分给不同的线程执行，最后将每个线程执行的最终结果相加即是整个复杂任务的最终结果
+ Fork：把一个复杂任务进行分拆，大事化小
+ Join：把分拆任务的结果进行合并
+ 要实现分支合并框架，该类必须继承`RecursiveTask`类
+ ![image-20200604234503920](C:\Users\18380\AppData\Roaming\Typora\typora-user-images\image-20200604234503920.png)

---

## 2. 案例

+ 使用分之合并思想实现 1+...+100

+ ```java
  public class ForkJoinDemo {
      public static void main(String[] args) throws ExecutionException, InterruptedException {
          ForkJoinPool forkJoinPool = new ForkJoinPool();
  
          MyTask myTask = new MyTask(1, 100);
  
          ForkJoinTask<Integer> forkJoinTask = forkJoinPool.submit(myTask);
  
          System.out.println(forkJoinTask.get());
  
          forkJoinPool.shutdown();
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
  
          if ((begin - end) <= ADJUST_VALUE) {
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
  
  ```

---

# JUC 异步回调 CompletableFuture

## 1. 初始化

```java
// 1
public static CompletableFuture<Void> runAsync(Runnable runnable) {
    return asyncRunStage(asyncPool, runnable);
}

// 2
public static CompletableFuture<Void> runAsync(Runnable runnable,
                                                   Executor executor) {
        return asyncRunStage(screenExecutor(executor), runnable);
}


// 3
public static <U> CompletableFuture<U> supplyAsync(Supplier<U> supplier) {
    return asyncSupplyStage(asyncPool, supplier);
}

// 4
public static <U> CompletableFuture<U> supplyAsync(Supplier<U> supplier,
                                                   Executor executor) {
    return asyncSupplyStage(screenExecutor(executor), supplier);
}

```

---

## 2. 异步调用

+ `CompletableFuture.runAsync(Runnable)`
+ 没有返回值

```java
CompletableFuture<Void> completableFuture = CompletableFuture.runAsync(() ->
                System.out.println(Thread.currentThread().getName() 
                                   + "\tCompletableFuture execute runAsync")
        );
completableFuture.get();

```

---

## 3. 异步回调

+ `CompletableFuture.supplyAsync(Runnable)`

+ 有返回值

+ ```java
  CompletableFuture<Integer> completableFuture1 
      = 	CompletableFuture.supplyAsync(() -> {
      	System.out.println(Thread.currentThread().getName() 
                         + "\tCompletableFuture execute supplyAsync");
          int num = 10 / 0;
          return 1024;
  });
  
  System.out.println(completableFuture1.whenComplete((t, u) -> {
      System.out.println("whenComplete, t: " + t);
      System.out.println("whenComplete, u: " + u);
  }).exceptionally(t -> {
      System.out.println("whenComplete exceptionally, message: " + t);
      return 4444;
  }).get());
  
  ```

---

## 4. 异步调用和回调代码

```java
/**
 * JUC 异步调用
 *
 * @Author Zhu
 * @Date 2020/6/4 23:52
 */
public class CompletableFutureDemo {
    public static void main(String[] args) throws Exception {

        CompletableFuture<Void> completableFuture = CompletableFuture.runAsync(() ->
                System.out.println(Thread.currentThread().getName() + "\tCompletableFuture execute runAsync")
        );
        completableFuture.get();

        System.out.println("------------------------------------");

        CompletableFuture<Integer> completableFuture1 = CompletableFuture.supplyAsync(() -> {
            System.out.println(Thread.currentThread().getName() + "\tCompletableFuture execute supplyAsync");
            int num = 10 / 0;
            return 1024;
        });

        System.out.println(completableFuture1.whenComplete((t, u) -> {
            System.out.println("whenComplete, t: " + t);
            System.out.println("whenComplete, u: " + u);
        }).exceptionally(t -> {
            System.out.println("whenComplete exceptionally, message: " + t);
            return 4444;
        }).get());
    }
}

```



