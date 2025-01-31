package io.devfactory.concurrency.basic;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

@SuppressWarnings({"NewClassNamingConvention", "squid:S2925"})
@Slf4j
class S07_CyclicBarrierExampleTest {

  private final static int[] PARALLEL_SUM = new int[2];

  @DisplayName("cyclicBarrier")
  @Test
  void testCyclicBarrier() {
    int[] numbers = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
    int numberOfThreads = 2;
    CyclicBarrier barrier = new CyclicBarrier(numberOfThreads, new BarrierAction(PARALLEL_SUM));

    for (int i = 0; i < numberOfThreads; i++) {
      new Thread(new Worker(i, numbers, barrier, PARALLEL_SUM)).start();
    }

    log.info("testCyclicBarrier start...");
  }

  @Slf4j
  public static class BarrierAction implements Runnable {

    private final int[] parallelSum;

    public BarrierAction(int[] parallelSum) {
      this.parallelSum = parallelSum;
    }

    public void run() {
      int finalSum = 0;
      for (int sum : parallelSum) {
        finalSum += sum;
      }
      log.info("Final Sum: {}", finalSum);
    }

  }

  @Slf4j
  public static class Worker implements Runnable {

    private final int id;
    private final int[] numbers;
    private final CyclicBarrier barrier;
    private final int[] parallelSum;

    public Worker(int id, int[] numbers, CyclicBarrier barrier, int[] parallelSum) {
      this.id = id;
      this.numbers = numbers;
      this.barrier = barrier;
      this.parallelSum = parallelSum;
    }

    public void run() {
      int start = id * (numbers.length / 2);
      int end = (id + 1) * (numbers.length / 2);
      int sum = 0;

      for (int i = start; i < end; i++) {
        sum += numbers[i];
      }

      parallelSum[id] = sum;

      try {
        barrier.await();
        log.info("{} 대기에서 풀려났습니다.", Thread.currentThread().getName());

      } catch (InterruptedException | BrokenBarrierException e) {
        throw new RuntimeException(e);
      }
    }

  }

}
