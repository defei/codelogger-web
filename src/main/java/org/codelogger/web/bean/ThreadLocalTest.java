package org.codelogger.web.bean;

import static java.lang.String.format;

public class ThreadLocalTest implements Runnable {

  private static MyThreadLocal<Integer> counter = new MyThreadLocal<Integer>() {

    @Override
    protected Integer initialValue() {

      return 0;
    }
  };

  @Override
  public void run() {

    for (int i = 0; i < 3; i++) {

      System.out.println(format("%s: %s", Thread.currentThread().getName(), getNumber()));
    }
  }

  public Integer getNumber() {

    counter.set(counter.get() + 1);
    return counter.get();
  }

  public static void main(final String[] args) {

    ThreadLocalTest threadLocalTest = new ThreadLocalTest();

    Thread thread1 = new Thread(threadLocalTest);
    Thread thread2 = new Thread(threadLocalTest);
    Thread thread3 = new Thread(threadLocalTest);

    thread1.start();
    thread2.start();
    thread3.start();
  }
}