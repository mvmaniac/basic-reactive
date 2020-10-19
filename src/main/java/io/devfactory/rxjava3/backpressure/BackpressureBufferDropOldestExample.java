package io.devfactory.rxjava3.backpressure;

import static io.devfactory.utils.LogType.DO_ON_NEXT;
import static io.devfactory.utils.LogType.ON_ERROR;
import static io.devfactory.utils.LogType.ON_NEXT;
import static io.devfactory.utils.LogType.PRINT;
import static io.reactivex.rxjava3.core.BackpressureOverflowStrategy.DROP_LATEST;
import static io.reactivex.rxjava3.core.BackpressureOverflowStrategy.DROP_OLDEST;

import io.devfactory.utils.Logger;
import io.devfactory.utils.TimeUtil;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import java.util.concurrent.TimeUnit;

@SuppressWarnings({"squid:S106","squid:S3457"})
public class BackpressureBufferDropOldestExample {

  // BUFFER 전략 - DROP_OLDEST
  // 버퍼가 가득 찬 시점에 버퍼내에서 가장 오래전에(먼저) 버퍼로 들어온 데이터를 DROP
  // DROP 된 빈 자리에 버퍼 밖에서 대기하던 데이터를 채움
  public static void main(String[] args) throws InterruptedException {
    System.out.println("# start : "+ TimeUtil.getCurrentTimeFormatted());

    Flowable.interval(300L, TimeUnit.MILLISECONDS)
        .doOnNext(data -> Logger.log(DO_ON_NEXT, data))
        .onBackpressureBuffer(2, () -> Logger.log(PRINT, "Overflow 발생!"), DROP_OLDEST)
        .observeOn(Schedulers.computation(), false, 1)
        .subscribe(
            data -> {
              TimeUtil.sleep(1000L);
              Logger.log(ON_NEXT, data);
            },
            error -> Logger.log(ON_ERROR, error)
        );

    Thread.sleep(2000L);
  }

}
