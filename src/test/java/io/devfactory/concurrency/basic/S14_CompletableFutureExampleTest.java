package io.devfactory.concurrency.basic;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

@SuppressWarnings({"NewClassNamingConvention", "squid:S2925"})
@Slf4j
class S14_CompletableFutureExampleTest {

  private final MyService myService = new MyService();

  @DisplayName("completableFuture")
  @Test
  void testCompletableFuture() throws Exception {
    int finalResult = CompletableFuture.supplyAsync(() -> {
      // 비동기 서비스 1의 작업 수행
      log.info("Service 1 시작");
      return 1;

    }).thenApplyAsync(result1 -> {
      // 비동기 서비스 2의 작업 수행 (service1 결과 사용)
      log.info("Service 2 시작");
      return result1 + 2;

    }).thenApplyAsync(result2 -> {
      // 비동기 서비스 3의 작업 수행 (service2 결과 사용)
      log.info("Service 3 시작");
      return result2 * 3;

    }).thenApplyAsync(result3 -> {
      // 비동기 서비스 4의 작업 수행 (service3 결과 사용)
      log.info("Service 4 시작");
      return result3 - 4;

    }).thenApplyAsync(result4 -> {
      // 비동기 서비스 5의 작업 수행 (service4 결과 사용)
      log.info("Service 5 시작");
      return result4 + 5;

    }).get();

    // 최종 결과를 얻기 위해 service5의 완료를 기다림
    log.info("최종 결과: {}", finalResult);
  }

  @DisplayName("runAsync")
  @Test
  void testRunAsync() {
    // CompletableFuture를 사용하는 경우
    CompletableFuture<Void> completableFuture = CompletableFuture.runAsync(() -> {
      log.info("{} 가 비동기 작업을 시작합니다", Thread.currentThread().getName());
      myService.getData().forEach(item -> log.info("{}", item));
    });

    completableFuture.join(); // main 스레드가 결과를 기다림

    log.info("메인 스레드 종료");
  }

  @DisplayName("supplyAsync")
  @Test
  void testSupplyAsync() {
    // CompletableFuture를 사용하는 경우
    CompletableFuture<List<Integer>> completableFuture = CompletableFuture.supplyAsync(() -> {
      log.info("{} 가 비동기 작업을 시작합니다", Thread.currentThread().getName());
      return myService.getData();
    });

    List<Integer> result = completableFuture.join(); // main 스레드가 결과를 기다림
    result.forEach(item -> log.info("{}", item));

    log.info("===================================================================================");

    // ExecutorService를 사용하는 경우
    try (ExecutorService executorService = Executors.newFixedThreadPool(1)) {
      Future<List<Integer>> future = executorService.submit(() -> {
        log.info("{} 가 비동기 작업을 시작합니다", Thread.currentThread().getName());
        return myService.getData();
      });

      try {
        List<Integer> result2 = future.get();
        result2.forEach(item -> log.info("{}", item));

      } catch (InterruptedException | ExecutionException e) {
        throw new RuntimeException(e);
      }
    }

    log.info("메인 스레드 종료");
  }

  @DisplayName("thenApply")
  @Test
  void testThenApply() throws ExecutionException, InterruptedException {
    long started = System.currentTimeMillis();

    CompletableFuture<Integer> completableFuture = CompletableFuture.supplyAsync(() -> {
      log.info("thread 1: {}", Thread.currentThread().getName());

      try {
        Thread.sleep(500);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }

      return 40;
    }).thenApply(result -> {
      // 작업을 실행하는 시점에서 이전 작업 결과가 완료 되었다면 메인 스레드에서 처리 (동기), 거의 보기 힘든 케이스라고 함
      // 그렇지 않다면 이전과 동일한 스레드에서 처리됨 (비동기)
      log.info("thread 2: {}", Thread.currentThread().getName());

      int r = myService.getData2();
      return r * result;
    }).thenApplyAsync(result -> {
      // 별도의 스레드에서 실행 됨 (비동기)
      // 별도의 스레드는 이전과 동일한 스레드 혹은 새롭게 생성된 스레드가 될 수 있음
      log.info("thread 3: {}", Thread.currentThread().getName());

      int r = myService.getData3();
      return r * result;
    });

    int result = completableFuture.get(); // 비동기 작업이 완료될 때까지 대기

    log.info("final result: {}", result);
    log.info("소요 시간: {}", System.currentTimeMillis() - started);
  }

  @DisplayName("thenAccept")
  @Test
  void testThenAccept() {
    CompletableFuture.supplyAsync(() -> {
      log.info("thread 1: {}", Thread.currentThread().getName());

      try {
        Thread.sleep(500);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }

      return 40;
    }).thenAccept(result -> {
      log.info("thread 2: {}", Thread.currentThread().getName());
      log.info("thread 2 result: {}", result);

      myService.getData().forEach(item -> log.info("{}", item));

    }).thenAcceptAsync(result -> {
      log.info("thread 3: {}", Thread.currentThread().getName());
      log.info("thread 3 result: {}", result);

      myService.getData().forEach(item -> log.info("{}", item));
    }).join();
  }

  @DisplayName("thenRun")
  @Test
  void testThenRun() {
    CompletableFuture<List<Integer>> forkJoinTask = CompletableFuture.supplyAsync(() -> {
      log.info("thread 1: {}", Thread.currentThread().getName());

      try {
        Thread.sleep(500);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }

      return 40;
    }).thenApply(result -> {
      log.info("thread 2: {}", Thread.currentThread().getName());
      return myService.getData();
    });

    // 비동기 작업 1 실행 및 결과 얻기
    List<Integer> result = forkJoinTask.join();
    log.info("비동기 작업 1 결과: {}", result);

    // 별도의 비동기 작업을 실행
    CompletableFuture<Void> runTask = forkJoinTask.thenRun(() -> {
      log.info("thread: {}", Thread.currentThread().getName());
      log.info("비동기 작업이 완료 되었습니다.");
    });

    // runTask의 thenRun 작업이 완료될 때까지 대기
    runTask.join();

    log.info("모든 작업 완료");
  }

  @DisplayName("thenCompose")
  @Test
  void testThenCompose() throws ExecutionException, InterruptedException {
    CompletableFuture<Integer> forkJoinTask1 = myService.getCompose1(5); // 비동기 작업 1
    CompletableFuture<Integer> forkJoinTask2 = forkJoinTask1.thenCompose(myService::getCompose2); // // 이전 비동기 작업의 결과를 사용 하여 다른 비동기 작업 실행
    log.info("final result: {}", forkJoinTask2.get());
  }

  @DisplayName("thenCombine")
  @Test
  void testThenCombine() throws ExecutionException, InterruptedException {
    CompletableFuture<String> forkJoinTask1 = myService.getCombine1(); // 비동기 작업 1
    CompletableFuture<String> forkJoinTask2 = myService.getCombine2();

    CompletableFuture<String> forkJoinTask3 = forkJoinTask1.thenCombine(forkJoinTask2, (r1, r2) -> r1 + r2);
    CompletableFuture<String> forkJoinTask4 = forkJoinTask3.thenCompose(result -> CompletableFuture.supplyAsync(() -> result + " Java"));

    log.info("final result: {}", forkJoinTask4.get());
  }

  @DisplayName("allOf")
  @Test
  void testAllOf() throws ExecutionException, InterruptedException {
    CompletableFuture<Integer> forkJoinTask1 = myService.getDataOf1();
    CompletableFuture<Integer> forkJoinTask2 = myService.getDataOf2();
    CompletableFuture<Integer> forkJoinTask3 = myService.getDataOf3();

    long started = System.currentTimeMillis();

    // 별도로 join이나 get으로 해야 메인 스레드에서 대기를 함
    CompletableFuture<Void> allForkJoinTask = CompletableFuture.allOf(forkJoinTask1, forkJoinTask2, forkJoinTask3);
    CompletableFuture<Integer> resultForkJoinTask = allForkJoinTask.thenApply(v -> {
      int result1 = forkJoinTask1.join();
      int result2 = forkJoinTask2.join();
      int result3 = forkJoinTask3.join();

      log.info("result1 = {}", result1);
      log.info("result2 = {}", result2);
      log.info("result3 = {}", result3);

      return result1 + result2 + result3;
    });

    resultForkJoinTask.join();

    log.info("최종 소요 시간: {}", (System.currentTimeMillis() - started));
//    log.info("최종결과: {}", allForkJoinTask.join());
    log.info("최종결과: {}", resultForkJoinTask.get());
    log.info("메인 스레드 종료");
  }

  @DisplayName("anyOf")
  @Test
  void testAnyOf() throws ExecutionException, InterruptedException {
    CompletableFuture<Integer> forkJoinTask1 = myService.getDataOf1();
    CompletableFuture<Integer> forkJoinTask2 = myService.getDataOf2();
    CompletableFuture<Integer> forkJoinTask3 = myService.getDataOf3();

    long started = System.currentTimeMillis();

    // 별도로 join이나 get으로 해야 메인 스레드에서 대기를 함
    CompletableFuture<Object> resultForkJoinTask = CompletableFuture.anyOf(forkJoinTask1, forkJoinTask2, forkJoinTask3);

    resultForkJoinTask.thenApply(result -> (int) result * 10);
    resultForkJoinTask.join();

    log.info("최종 소요 시간: {}", (System.currentTimeMillis() - started));
    log.info("최종결과: {}", resultForkJoinTask.get());
    log.info("메인 스레드 종료");
  }

  static class MyService {

    public List<Integer> getData() {
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
      return Arrays.asList(1, 2, 3);
    }

    public int getData2() {
      try {
        Thread.sleep(500);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
      return 20;
    }

    public int getData3() {
      try {
        Thread.sleep(500);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
      return 30;
    }

    public CompletableFuture<Integer> getCompose1(int input) {
      return CompletableFuture.supplyAsync(() -> {
        try {
          Thread.sleep(500);
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        }
        return input * 2;
      });
    }

    public CompletableFuture<Integer> getCompose2(int input) {
      return CompletableFuture.supplyAsync(() -> {
        try {
          Thread.sleep(500);
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        }
        return input * 2;
      });
    }

    public CompletableFuture<String> getCombine1() {
      return CompletableFuture.supplyAsync(() -> {
        try {
          Thread.sleep(500);
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        }
        return "Hello ";
      });
    }

    public CompletableFuture<String> getCombine2() {
      return CompletableFuture.supplyAsync(() -> {
        try {
          Thread.sleep(500);
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        }
        return "World";
      });
    }

    public CompletableFuture<Integer> getDataOf1() {
      return CompletableFuture.supplyAsync(() -> {
        try {
          Thread.sleep(500);
          log.info("비동기 작업 시작 1...");
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        }
        return 10;
      });
    }

    public CompletableFuture<Integer> getDataOf2() {
      return CompletableFuture.supplyAsync(() -> {
        try {
          Thread.sleep(2000);
          log.info("비동기 작업 시작 2...");
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        }
        return 20;
      });
    }

    public CompletableFuture<Integer> getDataOf3() {
      return CompletableFuture.supplyAsync(() -> {
        try {
          Thread.sleep(1000);
          log.info("비동기 작업 시작 3...");
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        }
        return 30;
      });
    }

  }

}
