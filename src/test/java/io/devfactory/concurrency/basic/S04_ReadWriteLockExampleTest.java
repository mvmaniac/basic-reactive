package io.devfactory.concurrency.basic;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@SuppressWarnings({"NewClassNamingConvention", "squid:S2925"})
@Slf4j
class S04_ReadWriteLockExampleTest {

  private final ReadWriteLock lock = new ReentrantReadWriteLock();
  // private final ReentrantReadWriteLock lock = new ReentrantLock(); // 이런식으로 해도 됨

  @DisplayName("readWriteLock")
  @Test
  void testReadWriteLock() throws InterruptedException {
    SharedData sharedData = new SharedData();

    Thread reader1 = new Thread(() -> {
      lock.readLock().lock();
      try {
        log.info("읽기 스레드 1이 데이터를 읽고 있습니다. 데이터: {}", sharedData.getData());

        try {
          Thread.sleep(1000);

        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }
      } finally {
        lock.readLock().unlock();
      }
    });

    Thread reader2 = new Thread(() -> {
      lock.readLock().lock();

      try {
        log.info("읽기 스레드 2가 데이터를 읽고 있습니다. 데이터: {}", sharedData.getData());

        try {
          Thread.sleep(1000);

        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }
      } finally {
        lock.readLock().unlock();
      }
    });

    Thread writer = new Thread(() -> {
      lock.writeLock().lock();

      try {
        log.info("쓰기 스레드가 데이터를 쓰고 있습니다");
        sharedData.setData(40);

        try {
          Thread.sleep(2000);
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }

        log.info("쓰기 스레드가 데이터를 변경 했습니다. 데이터: {}", sharedData.getData());
      } finally {
        lock.writeLock().unlock();
      }
    });

    writer.start();
    reader1.start();
    reader2.start();

    writer.join();
    reader1.join();
    reader2.join();
  }

  // 읽기 락의 비중이 많은 경우
  @DisplayName("readLock")
  @Test
  void testReadLock() throws InterruptedException {
    BankAccount account = new BankAccount(lock, 0);

    // 읽기 스레드가 잔액 조회
    for (int i = 0; i < 10; i++) {
      new Thread(() -> {
        int balance = account.getBalance();

        log.info("{} - 현재 잔액: {}", Thread.currentThread().getName(), balance);
      }).start();
    }

    // 쓰기 스레드가 입금
    for (int i = 0; i < 2; i++) {
      new Thread(() -> {
        int depositAmount = (int) (Math.random() * 1000);
        account.deposit(depositAmount);

        log.info("{} - 입금: {}", Thread.currentThread().getName(), depositAmount);
      }).start();
    }

    // 읽기 스레드가 잔액 조회
    for (int i = 0; i < 10; i++) {
      new Thread(() -> {
        int balance = account.getBalance();

        log.info("{} - 현재 잔액: {}", Thread.currentThread().getName(), balance);
      }).start();
    }

    Thread.sleep(10000);
  }

  // 쓰기 락의 비중이 많은 경우
  @DisplayName("writeLock")
  @Test
  void testWriteLock() throws InterruptedException {
    BankAccount account = new BankAccount(lock, 10000);

    // 읽기 스레드가 잔액 조회
    new Thread(() -> {
      int balance = account.getBalance();

      log.info("현재 잔액: {}", balance);
    }).start();


    // 여러 스레드가 출금
    for (int i = 0; i < 10; i++) {
      new Thread(() -> {
        int withdrawAmount = (int) (Math.random() * 1000);
        account.withdraw(withdrawAmount);

        log.info("출금: {}", withdrawAmount);
      }).start();
    }

    // 읽기 스레드가 잔액 조회
    new Thread(() -> {
      int balance = account.getBalance();

      log.info("현재 잔액: {}", balance);
    }).start();

    Thread.sleep(10000);
  }

  @Getter
  @Setter
  public static class SharedData {

    private int data = 0;

  }

  @Slf4j
  public static class BankAccount {

    private final ReadWriteLock lock;
    private final Map<String, Integer> balance;

    public BankAccount(ReadWriteLock lock, int amount) {
      this.lock = lock;

      balance = new HashMap<>();
      balance.put("account1", amount);
    }

    public int getBalance() {
      lock.readLock().lock();

      try {
        Thread.sleep(1000); // 읽기 스레드는 동시에 접근가능하기에 10개 스레드가 접근해도 1초밖에 안걸림
        return balance.get("account1");

      } catch (InterruptedException e) {
        throw new RuntimeException(e);

      } finally {
        lock.readLock().unlock();
      }
    }

    public void deposit(int amount) {
      lock.writeLock().lock();

      try {
        Thread.sleep(2000); // 쓰기 스레드는 동시에 접근 불가능하기에 2개 스레드가 접근하면 4초가 걸림

        int currentBalance = balance.get("account1");
        currentBalance += amount;

        balance.put("account1", currentBalance);

      } catch (InterruptedException e) {
        throw new RuntimeException(e);

      } finally {
        lock.writeLock().unlock();
      }
    }

    public void withdraw(int amount) {
      lock.writeLock().lock();

      try {
        int currentBalance = balance.get("account1");

        if (currentBalance >= amount) {
          currentBalance -= amount;
          balance.put("account1", currentBalance);

          log.info("{} - 출금 성공", Thread.currentThread().getName());

        } else {
          log.info("{} - 출금 실패, 잔액부족", Thread.currentThread().getName());
        }

        try {
          Thread.sleep(50);
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }
      } finally {
        lock.writeLock().unlock();
      }
    }

  }

}
