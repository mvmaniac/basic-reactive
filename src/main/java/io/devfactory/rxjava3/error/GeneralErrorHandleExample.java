package io.devfactory.rxjava3.error;

import static io.devfactory.utils.LogType.DO_ON_NEXT;
import static io.devfactory.utils.LogType.ON_COMPLETE;
import static io.devfactory.utils.LogType.ON_ERROR;
import static io.devfactory.utils.LogType.ON_NEXT;

import io.devfactory.utils.Logger;
import io.devfactory.utils.TimeUtil;
import io.reactivex.rxjava3.core.Observable;
import java.util.concurrent.TimeUnit;

/**

 */
public class GeneralErrorHandleExample {

  public static void main(String[] args) {
    // 0으로 나누는 부분에서 예외가 발생
    // RxJava에서 에러를 처리하는 일반적인 방식
    // RxJava에서는 에러 발생 시, Observable을 생성한 함수에서 onError()를 호출하고,
    // subscribe의 onError()에서 해당 error를 받아서 처리하는 구조를 가짐
    Observable.just(5)
        .flatMap(num -> Observable
            .interval(200L, TimeUnit.MILLISECONDS)
            .doOnNext(data -> Logger.log(DO_ON_NEXT, data))
            .take(5)
            .map(i -> num / i))
        .subscribe(
            data -> Logger.log(ON_NEXT, data),
            error -> Logger.log(ON_ERROR, error),
            () -> Logger.log(ON_COMPLETE)
        );

    TimeUtil.sleep(1000L);
  }

}
