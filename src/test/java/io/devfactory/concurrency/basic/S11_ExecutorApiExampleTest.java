package io.devfactory.concurrency.basic;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@SuppressWarnings({"NewClassNamingConvention", "squid:S2925", "BusyWait"})
@Slf4j
class S11_ExecutorApiExampleTest {

  @DisplayName("shutdown")
  @Test
  void testShutdown() {
    try (ExecutorService executorService = Executors.newFixedThreadPool(2)) {
      for (int i = 0; i < 5; i++) {
        executorService.execute(() -> {
          try {
            Thread.sleep(1000);
            log.info("{} : 작업 종료", Thread.currentThread().getName());

          } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("인터럽트 걸림...");
          }
        });
      }

      // 정상적인 방법으로 shutdown를 하더라고 바로 종료되지 않음
      executorService.shutdown();

      // shutdownNow를 하지 않으면 바로 종료하지는 않음
      try {
        if (!executorService.awaitTermination(1, TimeUnit.SECONDS)) {
          executorService.shutdownNow();
          log.info("스레드 풀 강제 종료 수행...");
        }
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        throw new RuntimeException(e);
      }

      if (executorService.isShutdown()) {
        log.info("스레드 풀 종료 여부: {}", executorService.isShutdown());
      }

      if (executorService.isTerminated()) {
        log.info("스레드 풀 완전 종료 여부: {}", executorService.isTerminated());
      }

      while (!executorService.isTerminated()) {
        try {
          Thread.sleep(1000);
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
          throw new RuntimeException(e);
        }
        log.info("스레드 풀 종료 중...");
      }

      log.info("모든 작업이 종료되고 스레드 풀이 종료됨...");
    }
  }

  @DisplayName("invokeAll")
  @Test
  void testInvokeAll() {
    try (ExecutorService executor = Executors.newFixedThreadPool(3)) {
      List<Callable<Integer>> tasks = new ArrayList<>();

      tasks.add(() -> {
        Thread.sleep(3000);
        return 3;
      });

      tasks.add(() -> {
        Thread.sleep(2000);
        return 2;
      });

      tasks.add(() -> {
        throw new RuntimeException("invokeAll");
//        Thread.sleep(1000);
//        return 1;
      });

      long started = 0;

      try {
        started = System.currentTimeMillis();

        // 여러 작업을 제출하고 결과를 반환받음
        List<Future<Integer>> results = executor.invokeAll(tasks);

        for (Future<Integer> future : results) {
          try {
            // 결과는 제출한 순서(add한 순서)대로 가지고 옴
            Integer value = future.get();
            log.info("result: {}", value);

          } catch (ExecutionException e) {
            // 작업 중 예외가 발생한 경우 처리
            Throwable cause = e.getCause();

            if (cause instanceof RuntimeException) {
              log.info("exception: {}", cause.getMessage());
            } else {
              log.info("{}", e.getMessage(), e);
            }
          }
        }
      } catch (InterruptedException e) {
        log.info("{}", e.getMessage(), e);
      }

      log.info("총 소요시간: {}", (System.currentTimeMillis() - started));
    }
  }

  @DisplayName("invokeAny")
  @Test
  void testInvokeAny() {
    try (ExecutorService executor = Executors.newFixedThreadPool(3)) {
      List<Callable<String>> tasks = new ArrayList<>();

      tasks.add(() -> {
        Thread.sleep(2000);
        return "Task 1";
      });

      tasks.add(() -> {
        Thread.sleep(1000);
        throw new RuntimeException("error"); // 예외는 무시됨
      });

      tasks.add(() -> {
        Thread.sleep(3000);
        return "Task 3";
      });

      long started = 0;

      try {
        started = System.currentTimeMillis();

        // 가장 빠르게 작업이 완료되는 결과(예외를 던지지 않은)를 반환하고 나머지 작업은 취소
        // Future가 아닌 작업결과를 가져옴
        String result = executor.invokeAny(tasks);

        log.info("result: {}", result);

      } catch (InterruptedException | ExecutionException e) {
        log.info("{}", e.getMessage(), e);
      }

      log.info("총 소요시간: {}", (System.currentTimeMillis() - started));
    }
  }

}
