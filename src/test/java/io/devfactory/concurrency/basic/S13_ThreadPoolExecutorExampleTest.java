package io.devfactory.concurrency.basic;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.*;

@SuppressWarnings({"NewClassNamingConvention", "squid:S2925"})
@Slf4j
class S13_ThreadPoolExecutorExampleTest {

  @DisplayName("poolSize")
  @Test
  void testPoolSize() {
    int corePoolSize = 2; // 새 작업이 제출 될 때 corePoolSize 미만의 스레드가 실행 중이면 corePoolSize 가 될 때까지 새 스레드를 생성함, corePoolSize만큼 미리 생성하지 않음
    int maxPoolSize = 4; // 큐가 가득 차 있는 경우는 maximumPoolSize 가 될 때까지 새 스레드가 생성됨
    long keepAliveTime = 0L; // corePoolSize 보다 더 많은 스레드가 존재하는 경우 각 스레드가 keepAliveTime 보다 오랜 시간 동안 유휴 상태였다면 해당 스레드는 종료됨

    // corePoolSize 를 초과할 경우 큐 사이즈가 남아 있으면 큐에 작업을 추가함
//    BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>(); // queue에 제한이 없음
    BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(4); // queue에 제한이 있음, taskNum은 최대 8로 해야함, 9이상은 에러 발생

    try (ThreadPoolExecutor executor = new ThreadPoolExecutor(corePoolSize, maxPoolSize, keepAliveTime, TimeUnit.SECONDS, workQueue)) {

//      executor.prestartCoreThread(); // 호출 할 때마다 1개의 스레드를 미리 생성함 (corePoolSize 만큼)
//      executor.prestartAllCoreThreads(); // corePoolSize 만큼의 스레드를 한번에 미리 생성함

//      executor.allowCoreThreadTimeOut(true); // keepAliveTime을 core 스레드에도 적용함

      int taskNum = 8;

      for (int i = 0; i < taskNum; i++) {
        final int taskId = i;
        executor.execute(() -> {
          log.info("{} 가 태스크 {}를 실행하고 있습니다.", Thread.currentThread().getName(), taskId);

          try {
            Thread.sleep(500);
          } catch (InterruptedException e) {
            throw new RuntimeException(e);
          }
        });
      }
    }
  }

  @DisplayName("rejectedExecutionHandler")
  @Test
  void testRejectedExecutionHandler() {
    int corePoolSize = 2;
    int maxPoolSize = 2;
    long keepAliveTime = 0L;
    int workQueueCapacity = 2;

    try (ThreadPoolExecutor executor = new ThreadPoolExecutor(
        corePoolSize,
        maxPoolSize,
        keepAliveTime,
        TimeUnit.SECONDS,
        new LinkedBlockingQueue<>(workQueueCapacity),
        new CustomRejectedExecutionHandler())) {

      // 최대 4개 작업만 실행, 5번째 작업은 거부됨
      for (int i = 1; i <= 5; i++) {
        final int taskId = i;
        executor.execute(() -> {
          log.info("Task {} is running on thread {}", taskId, Thread.currentThread().getName());

          try {
            Thread.sleep(3000);
          } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
          }
        });
      }
    }

  }

  // 4가지 정책이 있음
  // new ThreadPoolExecutor.AbortPolicy(); // 기본 정책, 작업을 거부하고 RejectedExecutionException을 발생시킴
  // new ThreadPoolExecutor.CallerRunsPolicy(); // 작업을 실행하는 스레드가 작업을 실행함 (예를 들어 main 스레드에서 실행)
  // new ThreadPoolExecutor.DiscardOldestPolicy(); // 큐의 가장 오래된 작업을 제거하고 새 작업을 큐에 추가함
  // new ThreadPoolExecutor.DiscardPolicy(); // 새 작업을 거부하고 아무것도 하지 않음
  // 아래는 직접 구혀하는 경우 Custom RejectedExecutionHandler
  @Slf4j
  static class CustomRejectedExecutionHandler implements RejectedExecutionHandler {

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
      log.info("{} is rejected", r.toString());
    }

  }

  @DisplayName("threadPoolExecutorHook")
  @Test
  void testThreadPoolExecutorHook() {
    int corePoolSize = 2;
    int maxPoolSize = 2;
    long keepAliveTime = 0L;
    int workQueueCapacity = 2;

    try (ThreadPoolExecutor executor = new ThreadPoolExecutorHook(
        corePoolSize,
        maxPoolSize,
        keepAliveTime,
        TimeUnit.SECONDS,
        new LinkedBlockingQueue<>(workQueueCapacity))) {

      for (int i = 1; i <= 4; i++) {
        final int taskId = i;
        executor.execute(() -> {
          log.info("{} 가 태스크 {}를 실행하고 있습니다.", Thread.currentThread().getName(), taskId);

          try {
            Thread.sleep(3000);
          } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
          }
        });
      }
    }

  }

  @Slf4j
  static class ThreadPoolExecutorHook extends ThreadPoolExecutor {

    public ThreadPoolExecutorHook(int corePoolSize, int maxPoolSize, long keepAliveTime,
        TimeUnit timeUnit, LinkedBlockingQueue<Runnable> queue) {
      super(corePoolSize, maxPoolSize, keepAliveTime, timeUnit, queue);
    }

    @Override
    protected void beforeExecute(Thread t, Runnable r) {
      log.info("{} 가 작업을 실행할려고 합니다.", t.getName());
      super.beforeExecute(t, r);
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
      if (t != null) {
        log.info("작업이 {} 예외가 발생했습니다.", t.getMessage());
      } else {
        log.info("작업이 성공적으로 완료했습니다.");
      }
      super.afterExecute(r, t);
    }

    @Override
    protected void terminated() {
      log.info("스레드 풀이 종료되었습니다.");
      super.terminated();
    }

  }


}
