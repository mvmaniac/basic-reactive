package io.devfactory.concurrency.basic;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@SuppressWarnings({"NewClassNamingConvention", "squid:S2925"})
@Slf4j
class S03_ReentrantLockExampleTest {

  private final Lock lock = new ReentrantLock();
  // private final ReentrantLock lock = new ReentrantLock(); // 이런식으로 해도 됨

  @DisplayName("lock")
  @Test
  void testLock() throws InterruptedException {
    LockCount lockCount = new LockCount();

    Thread thread1 = new Thread(() -> {
      for (int i = 0; i < 10000; i++) {
        lockCount.increment();
      }
    });

    Thread thread2 = new Thread(() -> {
      for (int i = 0; i < 10000; i++) {
        lockCount.increment();
      }
    });

    thread1.start();
    thread2.start();

    thread1.join();
    thread2.join();

    log.info("Count: {}", lockCount.getCount());
  }

  @DisplayName("lockState")
  @Test
  void testLockState() throws InterruptedException {
    Thread thread1 = new Thread(() -> {
      lock.lock(); // 락 획득 (1번)

      try {
        log.info("스레드 1이 락을 1번 획득했습니다.");

        lock.lock(); // 락 획득 (2번)

        try {
          log.info("스레드 1이 락을 2번 획득했습니다.");

          lock.lock(); // 락 획득 (3번)

          try {
            log.info("스레드 1이 락을 3번 획득했습니다.");

          } finally {
            lock.unlock(); // 락 해제 (1번)
            log.info("스레드 1이 락을 1번 해제했습니다.");
          }
        } finally {
          lock.unlock(); // 락 해제 (2번)
          log.info("스레드 1이 락을 2번 해제했습니다.");
        }
      } finally {
        lock.unlock(); // 락 해제 (3번)
        log.info("스레드 1이 락을 3번 해제했습니다.");
      }
    });

    Thread thread2 = new Thread(() -> {
      lock.lock(); // 락 획득 (스레드 1이 락을 세 번 해제할 때까지 대기)
      try {
        log.info("스레드 2가 락을 획득했습니다.");
      } finally {
        lock.unlock(); // 락 해제
        log.info("스레드 2가 락을 해제했습니다.");
      }
    });

    thread1.start();
    thread2.start();

    thread1.join();
    thread2.join();
  }

  // 불 공정성을 따름, 대기 중인 스레드와 관계없이 락을 즉시 획득
  @DisplayName("tryLock")
  @Test
  void testTryLock() throws InterruptedException {
    Thread thread1 = new Thread(() -> {
      boolean acquired = false;

      while (!acquired) {
        acquired = lock.tryLock();

        if (acquired) {
          try {
            System.out.println("스레드 1이 락을 획득했습니다");
            Thread.sleep(2000); // 스레드 1이 잠시 락을 보유

          } catch (InterruptedException e) {
            log.error("testTryLock.InterruptedException", e);

          } finally {
            lock.unlock();
            System.out.println("스레드 1이 락을 해제했습니다");
          }

        } else {
          System.out.println("스레드 1이 락을 획득하지 못했습니다. 잠시 대기합니다.");

          try {
            Thread.sleep(1000); // 1초 대기 후 다시 시도

          } catch (InterruptedException e) {
            log.error("testTryLock.InterruptedException", e);
          }
        }
      }
    });

    Thread thread2 = new Thread(() -> {
      boolean acquired = false;

      while (!acquired) {
        acquired = lock.tryLock();

        if (acquired) {
          try {
            System.out.println("스레드 2가 락을 획득했습니다");

          } finally {
            lock.unlock();
            System.out.println("스레드 2가 락을 해제했습니다");
          }

        } else {
          System.out.println("스레드 2가 락을 획득하지 못했습니다. 잠시 대기합니다.");

          try {
            Thread.sleep(1000); // 1초 대기 후 다시 시도

          } catch (InterruptedException e) {
            log.error("testTryLock.InterruptedException", e);
          }
        }
      }
    });

    thread1.start();
    thread2.start();

    thread1.join();
    thread2.join();
  }

  // 공정성을 따름, 대기 중인 스레드 중에서 가장 오래 기다린 스레드가 락을 획득
  @DisplayName("tryLockWithTimeout")
  @Test
  void testTryLockWithTimeout() throws InterruptedException {
    Thread thread1 = new Thread(() -> {
      try {
        if (lock.tryLock(2, TimeUnit.SECONDS)) {
          try {
            log.info("스레드 1이 락을 획득했습니다");
            Thread.sleep(3000); // 스레드 1이 락을 보유 (시간 초과)

          } finally {
            lock.unlock();
            log.info("스레드 1이 락을 해제했습니다");
          }
        } else {
          log.info("스레드 1이 락을 획득하지 못했습니다");
        }

      } catch (InterruptedException e) {
        log.info("스레드 1이 인터럽트를 받았습니다"); // 인터럽트 처리
      }
    });

    Thread thread2 = new Thread(() -> {
      try {
        if (lock.tryLock(2, TimeUnit.SECONDS)) {
          try {
            log.info("스레드 2가 락을 획득했습니다");

          } finally {
            lock.unlock();
            log.info("스레드 2가 락을 해제했습니다");
          }
        } else {
          log.info("스레드 2가 락을 획득하지 못했습니다");
        }
      } catch (InterruptedException e) {
        log.info("스레드 2가 인터럽트를 받았습니다"); // 인터럽트 처리
      }
    });

    thread1.start();
    thread2.start();

    thread1.join();
    thread2.join();
  }

  @DisplayName("lockInterruptibly")
  @Test
  void testLockInterruptibly() throws InterruptedException {
    Thread thread1 = new Thread(() -> {
      try {
        lock.lockInterruptibly(); // 락을 시도하며, 인터럽트가 들어오면 중단

        try {
          log.info("스레드 1이 락을 획득했습니다");
        } finally {
          lock.unlock();
          log.info("스레드 1이 락을 해제했습니다");
        }
      } catch (InterruptedException e) {
        log.info("스레드 1이 인터럽트를 받았습니다");
      }
    });

    Thread thread2 = new Thread(() -> {
      try {
        lock.lockInterruptibly(); // 락을 시도하며, 인터럽트가 들어오면 중단

        try {
          log.info("스레드 2가 락을 획득했습니다");
        } finally {
          lock.unlock();
          log.info("스레드 2가 락을 해제했습니다");
        }
      } catch (InterruptedException e) {
        log.info("스레드 2가 인터럽트를 받았습니다");
      }
    });

    thread1.start();
    thread2.start();

    thread1.interrupt();
//    thread2.interrupt();

    Thread.sleep(500);

    thread1.join();
    thread2.join();
  }

  @DisplayName("lockFairness")
  @Test
  void testLockFairness() {
    int threadCount = 4;

    Lock fairLock = new ReentrantLock(true);
    Lock unfairLock = new ReentrantLock(false);

    long fairElapsedTime = runLockFairness(threadCount, fairLock);
    long unfairElapsedTime = runLockFairness(threadCount, unfairLock);

    log.info("공정한 락의 실행 시간: {}밀리초", fairElapsedTime);
    log.info("불 공정한 락의 실행 시간: {}밀리초", unfairElapsedTime);
  }

  private long runLockFairness(int threadCount, Lock lock) {
    long startTime = System.currentTimeMillis();

    Thread[] threads = new Thread[4];

    for (int i = 0; i < threadCount; i++) {
      threads[i] = new Thread(() -> {
        for (int j = 0; j < 100_000; j++) {
          lock.lock();

          try {
            // 자원에 대한 작업 수행
          } finally {
            lock.unlock();
          }
        }
      });
    }

    for (Thread thread : threads) {
      thread.start();
    }

    for (Thread thread : threads) {
      try {
        thread.join();
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    }

    long endTime = System.currentTimeMillis();
    return endTime - startTime;
  }

  public static class LockCount {

    private final Lock lock = new ReentrantLock();
    private int count = 0;

    public void increment() {
      lock.lock(); // 락을 명시적으로 활성화

      try {
        count++;
      } finally {
        lock.unlock(); // 락을 해제, finally 블록에서 작성
      }
    }

    public int getCount() {
      lock.lock(); // 락을 명시적으로 활성화

      try {
        return count;
      } finally {
        lock.unlock(); // 락을 해제, finally 블록에서 작성
      }
    }

  }

}
