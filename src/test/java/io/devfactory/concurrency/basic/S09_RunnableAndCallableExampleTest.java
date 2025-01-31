package io.devfactory.concurrency.basic;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.*;

@SuppressWarnings({"NewClassNamingConvention", "squid:S2925"})
@Slf4j
class S09_RunnableAndCallableExampleTest {

  @DisplayName("runnable")
  @Test
  void testRunnable() {
    // 결과를 리턴하거나 예외를 던질 수 없음
    try (ExecutorService executorService = Executors.newSingleThreadExecutor()) {
      Runnable runnableTask = () -> {
        log.info("Runnable 작업 수행 중...");
        log.info("Runnable 작업 완료");
      };

      executorService.execute(runnableTask);
    }

    // AutoCloseable를 사용하지 않을 경우 명시적으로 사용
    // executorService.shutdown();
  }

  @DisplayName("callable")
  @Test
  void testCallable() {
    // 결과를 리턴하거나 예외를 던질 수 있음
    try (ExecutorService executorService = Executors.newSingleThreadExecutor()) {
      Callable<Integer> callableTask = () -> {
        log.info("Callable 작업 수행중...");
        log.info("Callable 작업 완료");

        return 42;
      };

      Future<Integer> future = executorService.submit(callableTask);
      int result;

      try {
        result = future.get();
      } catch (InterruptedException | ExecutionException e) {
        throw new RuntimeException(e);
      }

      log.info("Callable 작업 결과 : {}", result);
    }

    // AutoCloseable를 사용하지 않을 경우 명시적으로 사용
    // executorService.shutdown();
  }

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
