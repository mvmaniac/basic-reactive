package io.devfactory.concurrency.basic;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@SuppressWarnings({"NewClassNamingConvention", "squid:S2925"})
@Slf4j
class S05_ConditionExampleTest {

  private static final int CAPACITY = 5;

  private final Queue<Integer> queue = new LinkedList<>();

  private final Lock lock = new ReentrantLock();
  private final Condition notEmpty = lock.newCondition();
  private final Condition notFull = lock.newCondition();

  @DisplayName("condition")
  @Test
  void testCondition() throws InterruptedException {
    S05_ConditionExampleTest test = new S05_ConditionExampleTest();

    // 생산자 스레드
    Thread producerThread = new Thread(() -> {
      try {
        test.produce();
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    });

    // 소비자 스레드
    Thread consumerThread = new Thread(() -> {
      try {
        test.consume();
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    });

    producerThread.start();
    consumerThread.start();

    producerThread.join();
    consumerThread.join();
  }

  @SuppressWarnings({"InfiniteLoopStatement", "BusyWait"})
  public void produce() throws InterruptedException {
    int value = 0;

    while (true) {
      lock.lock();

      try {
        while (queue.size() == CAPACITY) {
          log.info("큐가 가득 차서 대기함");
          notFull.await(); // 큐가 가득 찼을 때 대기
        }

        queue.offer(value);
        log.info("생산: {}, 큐 크기: {}", value, queue.size());
        value++;

        notEmpty.signal(); // 큐에 데이터가 추가되었으므로 소비자에게 알림

      } finally {
        lock.unlock();
      }

      Thread.sleep(500); // 생산 속도 제어 (예제를 보기 쉽게 하기 위해)
    }
  }

  @SuppressWarnings({"InfiniteLoopStatement", "BusyWait"})
  public void consume() throws InterruptedException {
    while (true) {
      lock.lock();

      try {
        while (queue.isEmpty()) {
          log.info("큐가 비어 있어 대기함");
          notEmpty.await(); // 큐가 비었을 때 대기
        }

        int value = queue.poll();
        log.info("소비: {}, 큐 크기: {}", value, queue.size());

        notFull.signal(); // 큐에서 데이터를 가져왔으므로 생산자에게 알림

      } finally {
        lock.unlock();
      }

      Thread.sleep(1000); // 소비 속도 제어 (예제를 보기 쉽게 하기 위해)
    }
  }

}
