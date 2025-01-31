package io.devfactory.concurrency.basic;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

@SuppressWarnings({"NewClassNamingConvention", "squid:S2925"})
@Slf4j
class S15_CompletableFutureExceptionExampleTest {

  @DisplayName("exceptionally")
  @Test
  void testExceptionally() {
    CompletableFuture<Integer> forkJoinTask = CompletableFuture.supplyAsync(() -> {
          try {
            Thread.sleep(500);
          } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
          }
          return 10;
        })
        .thenApply(r -> r + 20)
        .exceptionally(e -> {
          log.info("Exception 1: {}", e.getMessage());
          return -1;
        })
        .thenApply(r -> 3 / 0)
        .exceptionally(e -> {
          log.info("Exception 2: {}", e.getMessage());
          return -2;
        });

    log.info("result: {}", forkJoinTask.join());
  }

  @DisplayName("handle")
  @Test
  void testHandle() {
    CompletableFuture<Integer> forkJoinTask1 = CompletableFuture.supplyAsync(() -> {
      try {
        Thread.sleep(500);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
      return 10;

    }).handle((r, e) -> {
      if (e != null) {
        log.info("비동기 예외처리 1: {}", e.getMessage());
        return -1;
      }
      return r;
    });

    CompletableFuture<Integer> forkJoinTask2 = CompletableFuture.supplyAsync(() -> {
      try {
        Thread.sleep(500);
        throw new RuntimeException("error");

      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
      return 20;
    }).handle((r, e) -> {
      if (e != null) {
        log.info("비동기 예외처리 2: {}", e.getMessage());
        return -1;
      }
      return r;
    });

    CompletableFuture<Integer> forkJoinTask3 = forkJoinTask1.thenCombine(forkJoinTask2, (r1, r2) -> {
      if (r1 == -1 || r2 == -1) {
        // 둘 중 하나라도 예외가 발생하면 예외 처리
        return -2;
      }
      // 두 결과를 조합하여 복잡한 작업 수행
      return r1 + r2;
    });

    log.info("result: {}", forkJoinTask3.join());
  }

  // handle과 달리 반환값이 없음, 다만 값을 반환받는 곳에서 별도의 예외처리 필요
  @DisplayName("whenComplete")
  @Test
  void testWhenComplete() {
    CompletableFuture<Integer> futureJoinTask = CompletableFuture.supplyAsync(() -> {
      try {
        Thread.sleep(500);
        throw new RuntimeException("error");
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
      return 10;
    }).whenComplete((r, e) -> {
      if (e != null) {
        log.info("Exception: {}", e.getMessage());
      } else {
        log.info("result: {}", r);
      }
    });

    try {
      Thread.sleep(2000);
      futureJoinTask.join();
    } catch (CompletionException e) {
      log.info("예외 처리를 합니다");
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

}
