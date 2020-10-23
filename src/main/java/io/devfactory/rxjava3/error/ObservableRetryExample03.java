package io.devfactory.rxjava3.error;

import static io.devfactory.utils.LogType.ON_COMPLETE;
import static io.devfactory.utils.LogType.ON_ERROR;
import static io.devfactory.utils.LogType.ON_NEXT;
import static io.devfactory.utils.LogType.PRINT;

import io.devfactory.utils.Logger;
import io.devfactory.utils.TimeUtil;
import io.reactivex.rxjava3.core.Observable;

public class ObservableRetryExample03 {

  public static void main(String[] args) {
    // 에러 발생 시, 데이터 통지를 처음부터 다시 하는것을 보여줌
    Observable.just(10, 12, 15, 16)
        .zipWith(Observable.just(1, 2, 0, 4), (a, b) -> {
          int result;
          try {
            result = a / b;
          } catch (ArithmeticException ex) {
            Logger.log(PRINT, "error: " + ex.getMessage());
            throw ex;
          }
          return result;
        })
        .retry(3)
        .onErrorReturn(throwable -> -1)
        .subscribe(
            data -> Logger.log(ON_NEXT, data),
            error -> Logger.log(ON_ERROR, error),
            () -> Logger.log(ON_COMPLETE)
        );

    TimeUtil.sleep(5000L);
  }

}
