package io.devfactory.concurrency.basic;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.lang.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@SuppressWarnings({"NewClassNamingConvention"})
@Slf4j
class S12_ThreadFactoryExampleTest {

  @DisplayName("theadFactory")
  @Test
  void testTheadFactory() {
    ThreadFactory threadFactory = new CustomThreadFactory("CustomThread");

    try (ExecutorService executor = Executors.newFixedThreadPool(3, threadFactory)) {
      List<Future<Integer>> futures = new ArrayList<>();

      for (int i = 0; i < 10; i++) {
        final int taskNumber = i;

        Callable<Integer> task = () -> {
          log.info("Thread: {}, Result: {}", Thread.currentThread().getName(), taskNumber + 1);
          return taskNumber + 1;
        };

        Future<Integer> future = executor.submit(task);
        futures.add(future);
      }

      for (Future<Integer> future : futures) {
        try {
          future.get();
        } catch (InterruptedException | ExecutionException e) {
          throw new RuntimeException(e);
        }
      }
    }
  }

  static class CustomThreadFactory implements ThreadFactory {

    private final String name;
    private int threadCount = 0;

    public CustomThreadFactory(String name) {
      this.name = name;
    }

    @Override
    public Thread newThread(@NonNull Runnable r) {
      threadCount++;

      String threadName = name + "-" + threadCount;
      Thread newThread = new Thread(r, threadName);

      log.info("스레드 이름: {}", threadName);

      return newThread;
    }

  }

}
