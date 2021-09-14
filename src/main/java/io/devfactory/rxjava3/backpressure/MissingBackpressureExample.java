package io.devfactory.rxjava3.backpressure;

import static io.devfactory.rxjava3.utils.LogType.DO_ON_NEXT;
import static io.devfactory.rxjava3.utils.LogType.ON_COMPLETE;
import static io.devfactory.rxjava3.utils.LogType.ON_ERROR;
import static io.devfactory.rxjava3.utils.LogType.ON_NEXT;
import static io.devfactory.rxjava3.utils.LogType.PRINT;

import io.devfactory.rxjava3.utils.Logger;
import io.devfactory.rxjava3.utils.TimeUtil;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import java.util.concurrent.TimeUnit;

public class MissingBackpressureExample {

  public static void main(String[] args) throws InterruptedException {
    Flowable.interval(1L, TimeUnit.MILLISECONDS)
        .doOnNext(data -> Logger.log(DO_ON_NEXT, data))
        .observeOn(Schedulers.computation())
        .subscribe(
            data -> {
              Logger.log(PRINT, "# 소비자 처리 대기 중...");
              TimeUtil.sleep(1000L);
              Logger.log(ON_NEXT, data);
            },
            error -> Logger.log(ON_ERROR, error),
            () -> Logger.log(ON_COMPLETE)
        );

    Thread.sleep(2000L);
  }

}
