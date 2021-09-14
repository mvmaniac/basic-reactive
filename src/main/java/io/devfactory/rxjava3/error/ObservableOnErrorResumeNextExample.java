package io.devfactory.rxjava3.error;

import static io.devfactory.rxjava3.utils.LogType.ON_NEXT;
import static io.devfactory.rxjava3.utils.LogType.PRINT;

import io.devfactory.rxjava3.utils.Logger;
import io.devfactory.rxjava3.utils.TimeUtil;
import io.reactivex.rxjava3.core.Observable;
import java.util.concurrent.TimeUnit;

public class ObservableOnErrorResumeNextExample {

  public static void main(String[] args) {
    // onErrorResumeNext를 이용해서 에러 발생시, 다른 Observable로 대체하는 예제.
    Observable.just(5L)
        .flatMap(num -> Observable
            .interval(200L, TimeUnit.MILLISECONDS)
            .take(5)
            .map(i -> num / i)
            .onErrorResumeNext(throwable -> {
              Logger.log(PRINT, "# 운영자에게 이메일 발송: " + throwable.getMessage());
              return Observable.interval(200L, TimeUnit.MILLISECONDS).take(5).skip(1)
                  .map(i -> num / i);
            })
        ).subscribe(data -> Logger.log(ON_NEXT, data));

    TimeUtil.sleep(2000L);
  }

}
