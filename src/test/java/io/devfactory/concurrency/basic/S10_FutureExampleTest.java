package io.devfactory.concurrency.basic;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@SuppressWarnings({"NewClassNamingConvention", "squid:S2925", "BusyWait"})
@Slf4j
class S10_FutureExampleTest {

  @DisplayName("future")
  @Test
  void testFuture() {
    try (ExecutorService executorService = Executors.newSingleThreadExecutor()) {
      Future<Integer> future = executorService.submit(() -> {
        try {
          Thread.sleep(1000);
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }
        return 42;
      });

      log.info("Future 비동기 작업 시작");

      try {
        int result = future.get();
        log.info("Future 비동기 작업결과: {}", result);

      } catch (InterruptedException | ExecutionException e) {
        throw new RuntimeException(e);
      }
    }
  }

  @DisplayName("futureApi")
  @Test
  void testFutureApi() throws InterruptedException {
    try (ExecutorService executorService = Executors.newSingleThreadExecutor()) {
      Future<Integer> future = executorService.submit(() -> {
        log.info("Future API 비동기 작업 시작");

        try {
          Thread.sleep(1000);
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }

        log.info("Future API 비동기 작업 완료");
        return 42;
      });

      while (!future.isDone()) {
        System.out.println("Waiting for the result...");
        Thread.sleep(500);
      }

      // 작업 취소 시도, 결과가 완료된 경우는 효과가 없다
      // true인 경우 현재 작업을 실행 중인 스레드를 인터럽트 하며, 작업결과를 가져올 때 취소 예외가 발생
      // false인 경우 진행 중인 작업은 완료 할 수 있으며, 작업결과를 가져올 때 취소 예외가 발생
      boolean cancel = future.cancel(true);
      log.info("Cancel: {}", cancel);

      if (!future.isCancelled()) {
        try {
          Integer result = future.get();
          log.info("Result: {}", result);

        } catch (Exception e) {
          throw new RuntimeException(e);
        }

      } else {
        log.info("Task was cancelled");
      }
    }
  }

  @DisplayName("callback")
  @Test
  void testCallback() {
    try (ExecutorService executorService = Executors.newSingleThreadExecutor()) {
      executorService.execute(() -> {
        try {
          Thread.sleep(1000);
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }
        int result = 42;

        Callback callback = new MyCallback();
        callback.onComplete(result);
      });

      log.info("Callback 비동기 작업 시작");
    }
  }

  interface Callback {

    void onComplete(int result);

  }

  static class MyCallback implements Callback {

    @Override
    public void onComplete(int result) {
      log.info("Callback 비동기 작업 결과: {}", result);
    }

  }

}
