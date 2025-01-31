package io.devfactory.concurrency.basic;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@SuppressWarnings({"NewClassNamingConvention", "squid:S2925"})
@Slf4j
class S01_JoinExampleTest {

  @DisplayName("join")
  @Test
  void testJoin() throws InterruptedException {
    final var thread1 = new Thread(() -> {
      final var threadName = Thread.currentThread().getName();

      try {
        log.info("[dev] {} 3초 동안 작동합니다", threadName);
        Thread.sleep(3000);
        log.info("[dev] {} 작동 완료", threadName);
      } catch (InterruptedException e) {
        log.warn("{}, InterruptedException occurred...", threadName, e);
        Thread.currentThread().interrupt();
      }
    });

    final var thread2 = new Thread(() -> {
      final var threadName = Thread.currentThread().getName();

      try {
        log.info("[dev] {} 2초 동안 작동합니다 ", threadName);
        Thread.sleep(2000);
        log.info("[dev] {} 작동 완료", threadName);
      } catch (InterruptedException e) {
        log.warn("{}, InterruptedException occurred...", threadName, e);
        Thread.currentThread().interrupt();
      }
    });

    thread1.setName("thread1");
    thread2.setName("thread2");

    thread1.start();
    thread2.start();

    log.info("[dev] 메인 스레드가 다른 스레드의 완료를 기다립니다.");

    thread1.join();
    thread2.join();

    log.info("[dev] 메인 스레드가 계속 진행합니다");
  }

}
