package io.devfactory.concurrency.basic;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.lang.NonNull;

import java.util.concurrent.Executor;

@SuppressWarnings({"NewClassNamingConvention", "squid:S2925"})
@Slf4j
class S08_ExecutorExampleTest {

  @DisplayName("executor")
  @Test
  void testExecutor() {
    Executor syncExecutor = new SyncExecutor();
    syncExecutor.execute(() -> log.info("동기 작업 Executor 실행"));

    Executor asyncExecutor = new AsyncExecutor();
    asyncExecutor.execute(() -> log.info("비동기 작업 Executor 실행"));
  }

  static class SyncExecutor implements Executor {

    @Override
    public void execute(Runnable command) {
      // 메인 스레드에서 작업 수행
      command.run();
    }

  }

  static class AsyncExecutor implements Executor {

    @Override
    public void execute(@NonNull Runnable command) {
      // 별도의 스레드를 생성햐여 작업 수행
      Thread thread = new Thread(command);
      thread.start();
    }

  }

}
