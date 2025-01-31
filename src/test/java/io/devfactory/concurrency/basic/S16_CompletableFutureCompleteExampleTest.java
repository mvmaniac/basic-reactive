package io.devfactory.concurrency.basic;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.*;

@SuppressWarnings({"NewClassNamingConvention", "squid:S2925", "BusyWait"})
@Slf4j
class S16_CompletableFutureCompleteExampleTest {

  private final CompleteService completeService = new CompleteService();

  @DisplayName("complete")
  @Test
  void testComplete() {
    CompletableFuture<Integer> forkJoinTask = completeService.performTask();
    CompletableFuture<Integer> forkJoinTaskApply = forkJoinTask.thenApply(r -> r + 20);

    log.info("result1: {}", forkJoinTask.join());
    log.info("result2: {}", forkJoinTaskApply.join());
    log.info("메인 스레드 종료");
  }

  @DisplayName("completedFuture")
  @Test
  void testCompletedFuture() {
    // 바로 작업이 완료된 상태로 반환 됨, 아래 주석 코드와 동일
    // CompletableFuture<String> forkJoinTask = new CompletableFuture<>();
    // forkJoinTask.complete("Hello World");
    CompletableFuture<String> forkJoinTask = CompletableFuture.completedFuture("Hello World");

    forkJoinTask.thenAccept(r -> {
      log.info("result: {}", r);
    });
  }

  @DisplayName("completeExceptionally")
  @Test
  void testCompleteExceptionally() {
    CompletableFuture<String> forkJoinTask = new CompletableFuture<>();

    completeService.getDataForExceptionally(forkJoinTask);

    CompletableFuture<String> forkJoinTaskExceptionally = forkJoinTask
        .thenApply(result -> {
          log.info("{}", result);
          return result.toUpperCase();
        })
        .handle((r, e) -> {
          if (e != null) {
            log.info("Exception: {}", e.getMessage());
            return "noname";
          }
          return r;
        });

    log.info("result: {}", forkJoinTaskExceptionally.join());
  }

  @DisplayName("completeOnTimeout")
  @Test
  void testCompleteOnTimeout() {
    completeService.getDataForTimeout()
        .completeOnTimeout("Hello Java", 2, TimeUnit.SECONDS)
        .thenAccept(r -> {
          log.info("r = {}", r);
        })
        .join();
  }

  @DisplayName("isDone")
  @Test
  void testIsDone() throws ExecutionException, InterruptedException {
    CompletableFuture<Integer> forkJoinTask1 = CompletableFuture.supplyAsync(() -> {
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        // nothing
      }

      return 42;
    });

    CompletableFuture<Integer> forkJoinTask2 = forkJoinTask1.thenApplyAsync(result -> {
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        // nothing
      }

      return result * 2;
    });

    while (!forkJoinTask1.isDone() || !forkJoinTask2.isDone()) {
      log.info("작업이 아직 완료되지 않았습니다.");

      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        log.info("", e);
      }
    }

    // 결과 가져오기
    int firstResult = forkJoinTask1.get();
    int secondResult = forkJoinTask2.get();

    log.info("첫 번째 결과: {}", firstResult);
    log.info("두 번째 결과: {}", secondResult);
  }

  @DisplayName("isCompletedExceptionally")
  @Test
  void testIsCompletedExceptionally() {
    CompletableFuture<Integer> forkJoinTask1 = CompletableFuture.supplyAsync(() -> 10);
    CompletableFuture<Integer> forkJoinTask2 = CompletableFuture.supplyAsync(() -> {
      return 20;
//      throw new RuntimeException("error");
    });

    // cancel를 하는 경우에도 예외가 발생함
//    forkJoinTask2.cancel(true);

    CompletableFuture<Integer> combineTask = forkJoinTask1.thenCombine(forkJoinTask2.exceptionally(e -> 99), (result1, result2) -> {
      if (forkJoinTask2.isCancelled()) {
        return 0; // 취소 완료

      } else if (forkJoinTask2.isCompletedExceptionally()) {
        return result2; // 예외 완료

      } else if (forkJoinTask2.isDone()) {
        return result1 + result2; // 정상 완료

      }

      return -1;
    });

    // 결과 가져오기
    log.info("result: {}", combineTask.join());
  }

  static class CompleteService {

    public CompletableFuture<Integer> performTask() {
      CompletableFuture<Integer> forkJoinTask = new CompletableFuture<>();

      try (ExecutorService executorService = Executors.newSingleThreadExecutor()) {
        executorService.submit(() -> {
          try {
            Thread.sleep(1000);
          } catch (InterruptedException e) {
            throw new RuntimeException(e);
          }

          forkJoinTask.complete(40);
        });
      }

      return forkJoinTask;
    }

    public CompletableFuture<String> getDataForTimeout() {
      return CompletableFuture.supplyAsync(() -> {
        try {
          Thread.sleep(2000);
          return "Hello World";
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }
      });
    }

    public void getDataForExceptionally(CompletableFuture<String> forkJoinTask) {
      try {
        log.info("비동기 작업 수행 중..");

        Thread.sleep(500);
        throw new IllegalArgumentException("error");

      } catch (Exception e) {
        forkJoinTask.completeExceptionally(e);
      }

      forkJoinTask.complete("Hello World");
    }

  }

}
