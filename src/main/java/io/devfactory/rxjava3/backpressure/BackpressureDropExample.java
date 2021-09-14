package io.devfactory.rxjava3.backpressure;

import static io.devfactory.rxjava3.utils.LogType.DO_ON_NEXT;
import static io.devfactory.rxjava3.utils.LogType.ON_ERROR;
import static io.devfactory.rxjava3.utils.LogType.ON_NEXT;
import static io.devfactory.rxjava3.utils.LogType.PRINT;

import io.devfactory.rxjava3.utils.Logger;
import io.devfactory.rxjava3.utils.TimeUtil;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import java.util.concurrent.TimeUnit;

@SuppressWarnings({"squid:S106","squid:S3457"})
public class BackpressureDropExample {

  // DROP 전략
  // 버퍼에 데이터가 모두 채워진 상태가 되면 이후에 생성되는 데이터를 버리고(DROP), 
  // 버퍼가 비워지는 시점에 DROP 되지 않은 데이터부터 다시 버퍼에 담음
  public static void main(String[] args) throws InterruptedException {
    System.out.println("# start : "+ TimeUtil.getCurrentTimeFormatted());

    Flowable.interval(300L, TimeUnit.MILLISECONDS)
        .doOnNext(data -> Logger.log(DO_ON_NEXT, data))
        .onBackpressureDrop(dropData -> Logger.log(PRINT, dropData + " Drop!"))
        .observeOn(Schedulers.computation(), false, 1)
        .subscribe(
            data -> {
              TimeUtil.sleep(1000L);
              Logger.log(ON_NEXT, data);
            },
            error -> Logger.log(ON_ERROR, error)
        );

    Thread.sleep(5500L);
  }

}
