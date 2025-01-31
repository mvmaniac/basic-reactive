package io.devfactory.concurrency.basic;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

@SuppressWarnings({"NewClassNamingConvention", "squid:S2925"})
@Slf4j
class S17_ForkJoinPoolExampleTest {

  @DisplayName("customForkJoinPool")
  @Test
  void testCustomForkJoinPool() {
    int[] array = new int[10];

    for (int i = 0; i < array.length; i++) {
      array[i] = i;
    }

    try (ForkJoinPool pool = new ForkJoinPool(Runtime.getRuntime().availableProcessors())) {
      CustomRecursiveTask task = new CustomRecursiveTask(array, 0, array.length);
      long result = pool.invoke(task);

      log.info("result = {}", result);
      log.info("pool = {}", pool);
      log.info("stealing = {}", pool.getStealCount());
    }
  }

  static class CustomRecursiveTask extends RecursiveTask<Long> {

    private static final int THRESHOLD = 2;

    private final int[] array;
    private final int start;
    private final int end;

    public CustomRecursiveTask(int[] array, int start, int end) {
      this.array = array;
      this.start = start;
      this.end = end;
    }

    @Override
    protected Long compute() {
      if (end - start < THRESHOLD) {
        long sum = 0;

        for (int i = start; i < end; i++) {
          sum += array[i];
        }

        return sum;

      } else {
        int mid = start + (end - start) / 2;

        CustomRecursiveTask left = new CustomRecursiveTask(array, start, mid);
        CustomRecursiveTask right = new CustomRecursiveTask(array, mid, end);

        left.fork();

        long rightResult = right.compute();
        long leftResult = left.join();

        return leftResult + rightResult;
      }
    }

  }

}
