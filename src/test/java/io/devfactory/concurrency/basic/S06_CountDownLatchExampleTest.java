package io.devfactory.concurrency.basic;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;

@SuppressWarnings({"NewClassNamingConvention", "squid:S2925"})
@Slf4j
class S06_CountDownLatchExampleTest {

  @DisplayName("countDownLatch")
  @Test
  void testCountDownLatch() throws InterruptedException {
    int numberOfThreads = 5;

    CountDownLatch startSignal = new CountDownLatch(1);
    CountDownLatch doneSignal = new CountDownLatch(5);

    for (int i = 0; i < numberOfThreads; i++) {
      new Thread(new S06_CountDownLatchExampleTest.Worker(startSignal, doneSignal)).start();
    }

    Thread.sleep(3000);
    startSignal.countDown(); // startSignal의 count값이 1이기 때문에 스레드의 작업이 완료됨을 알림

    log.info("시작신호를 알렸습니다.");

    doneSignal.await(); // doneSignal 스레드의 작업이 완료될 때까지 대기

    log.info("모든 스레드의 작업이 완료되었습니다.");
  }

  @Slf4j
  public static class Worker implements Runnable {

    private final CountDownLatch startSignal;
    private final CountDownLatch doneSignal;

    public Worker(CountDownLatch startSignal, CountDownLatch doneSignal) {
      this.startSignal = startSignal;
      this.doneSignal = doneSignal;
    }

    @Override
    public void run() {
      try {
        startSignal.await(); // startSignal 스레드의 작업이 완료될 때까지 대기

        log.info("{} 가 작업을 수행하고 있습니다.", Thread.currentThread().getName());
        Thread.sleep(1000);
        log.info("{} 가 작업을 완료했습니다.", Thread.currentThread().getName());

      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      } finally {
        doneSignal.countDown();
      }
    }

  }

}
