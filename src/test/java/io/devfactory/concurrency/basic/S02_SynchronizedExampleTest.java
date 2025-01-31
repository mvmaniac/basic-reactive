package io.devfactory.concurrency.basic;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@SuppressWarnings({"NewClassNamingConvention", "squid:S2925"})
@Slf4j
class S02_SynchronizedExampleTest {

  @DisplayName("synchronized")
  @Test
  void testSynchronized() throws InterruptedException {
    SynchronizedBlock example = new SynchronizedBlock();

    new Thread(example::instanceMethod).start();
    new Thread(example::instanceBlock).start();
    new Thread(SynchronizedBlock::staticMethod).start();
    new Thread(SynchronizedBlock::staticBlock).start();

    Thread.sleep(3000);

    log.info("testSynchronized");
  }

  @DisplayName("methodBlockSynchronized")
  @Test
  void testMethodBlockSynchronized() throws InterruptedException {
    MethodBlockSynchronized example = new MethodBlockSynchronized();

    Thread t1 = new Thread(() -> {
      for (int i = 0; i < 10; i++) {
        example.incrementInstanceMethod();
      }
    }, "스레드1");

    Thread t2 = new Thread(() -> {
      for (int i = 0; i < 10; i++) {
        example.incrementInstanceBlockThis();
      }
    }, "스레드2");

    Thread t3 = new Thread(() -> {
      for (int i = 0; i < 10; i++) {
        MethodBlockSynchronized.incrementStaticBlockClass();
      }
    }, "스레드3");

    Thread t4 = new Thread(() -> {
      for (int i = 0; i < 10; i++) {
        example.incrementInstanceBlockLockObject();
      }
    }, "스레드4");

    Thread t5 = new Thread(() -> {
      for (int i = 0; i < 10; i++) {
        MethodBlockSynchronized.incrementStaticMethod();
      }
    }, "스레드5");

    Thread t6 = new Thread(() -> {
      for (int i = 0; i < 10; i++) {
        MethodBlockSynchronized.incrementStaticBlockOtherClass();
      }
    }, "스레드6");

    t1.start();
    t2.start();
    t3.start();
    t4.start();
    t5.start();
    t6.start();

    t1.join(); // this
    t2.join(); // this
    t3.join(); // MethodBlockSynchronized
    t4.join(); // Object
    t5.join(); // MethodBlockSynchronized
    t6.join(); // MethodBlock

    log.info("example.count: {}", MethodBlockSynchronized.count);
  }

  @Slf4j
  public static class SynchronizedBlock {

    private int instanceCount = 0;
    private static int staticCount = 0;

    public synchronized void instanceMethod() {
      instanceCount++;
      log.info("인스턴스 메서드 동기화: {}", instanceCount);
    }

    public static synchronized void staticMethod() {
      staticCount++;
      log.info("정적 메서드 동기화: {}", staticCount);
    }

    public void instanceBlock() {
      synchronized (this) {
        instanceCount++;
        log.info("인스턴스 블록 동기화: {}", instanceCount);
      }
    }

    public static void staticBlock() {
      synchronized (SynchronizedBlock.class) {
        staticCount++;
        log.info("정적 블록 동기화: {}", staticCount);
      }
    }

  }

  public static class MethodBlock {}

  @Slf4j
  public static class MethodBlockSynchronized {

    private static int count = 0;

    private final Object lockObject = new Object(); // 별도의 객체를 생성하여 모니터로 사용

    // this 가 모니터가 된다
    public synchronized void incrementInstanceMethod() {
      count++;

      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }

      log.info("{} - 메서드 동기화로 증가: {}", Thread.currentThread().getName(), count);
    }

    // MethodBlockSynchronized 가 모니터가 된다
    public static synchronized void incrementStaticMethod() {
      count++;

      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }

      log.info("{} - 메서드 동기화로 증가: {}", Thread.currentThread().getName(), count);
    }

    // 특정 객체의 블록에 synchronized 키워드를 사용하는 방법
    public void incrementInstanceBlockThis() {
      // this 가 모니터가 된다
      synchronized (this) {
        count++;

        try {
          Thread.sleep(1000);
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }

        log.info("{} - 블록 동기화로 증가: {}", Thread.currentThread().getName(), count);
      }
    }

    // 별도의 객체를 사용하여 동기화하는 방법
    public void incrementInstanceBlockLockObject() {
      // Object 가 모니터가 된다
      synchronized (lockObject) {
        count++;

        try {
          Thread.sleep(1000);
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }

        log.info("{} - 별도 객체 동기화로 증가: {}", Thread.currentThread().getName(), count);
      }
    }

    // 별도의 클래스를 사용하여 동기화하는 방법
    public static void incrementStaticBlockClass() {
      // MethodBlockSynchronized 가 모니터가 된다
      synchronized (MethodBlockSynchronized.class) {
        count++;

        try {
          Thread.sleep(1000);
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }

        log.info("{} - 별도 클래스 동기화로 증가: {}", Thread.currentThread().getName(), count);
      }
    }

    // 별도의 클래스를 사용하여 동기화하는 방법
    public static void incrementStaticBlockOtherClass() {
      // MethodBlock 가 모니터가 된다
      synchronized (MethodBlock.class) {
        count++;

        try {
          Thread.sleep(1000);
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }

        log.info("{} - 별도 클래스 동기화로 증가: {}", Thread.currentThread().getName(), count);
      }
    }

  }

}
