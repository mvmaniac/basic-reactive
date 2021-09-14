package io.devfactory.rxjava3.error;

import static io.devfactory.rxjava3.utils.LogType.ON_COMPLETE;
import static io.devfactory.rxjava3.utils.LogType.ON_ERROR;
import static io.devfactory.rxjava3.utils.LogType.ON_NEXT;
import static io.devfactory.rxjava3.utils.LogType.PRINT;

import io.devfactory.rxjava3.utils.Logger;
import io.devfactory.rxjava3.utils.TimeUtil;
import io.reactivex.rxjava3.core.Observable;
import java.util.concurrent.TimeUnit;

public class ObservableRetryExample02 {

  private final static int RETRY_MAX = 5;

  public static void main(String[] args) {
    Observable.just(5)
        .flatMap(
            num -> Observable
                .interval(200L, TimeUnit.MILLISECONDS)
                .map(i -> {
                  long result;
                  try {
                    result = num / i;
                  } catch (ArithmeticException ex) {
                    Logger.log(PRINT, "error: " + ex.getMessage());
                    throw ex;
                  }
                  return result;
                })
                .retry((retryCount, ex) -> {
                  Logger.log(PRINT, "# 재시도 횟수: " + retryCount);
                  TimeUtil.sleep(1000L);
                  return retryCount < RETRY_MAX;
                })
                .onErrorReturn(throwable -> -1L)

        ).subscribe(
        data -> Logger.log(ON_NEXT, data),
        error -> Logger.log(ON_ERROR, error),
        () -> Logger.log(ON_COMPLETE)
    );

    TimeUtil.sleep(6000L);
  }

}
