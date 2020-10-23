package io.devfactory.rxjava3.operators;

import io.devfactory.utils.LogType;
import io.devfactory.utils.Logger;
import io.devfactory.utils.NumberUtil;
import io.devfactory.utils.TimeUtil;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static io.devfactory.utils.LogType.*;

@SuppressWarnings({"squid:S125", "squid:S1144", "squid:S1192"})
public class UtilityOperatorExample {

  // 유틸리티 연산자
  public static void main(String[] args) {
//    delay();
//    delaySubscription();
//    timeout();
//    timeInterval();
//    materializeAndDematerialize();
  }

  // delay
  // 생산자가 데이터를 생성 및 통지를 하지만 설정한 시간만큼 소비자쪽으로의 데이터 전달을 지연
  // 파라미터로 생성되는 Observable이 데이터를 통지할때까지 각각의 원본 데이터의 통지를 지연
  private static void delay() {
    Logger.log(PRINT, "# 실행 시작 시간");

    Observable.just(1, 3, 4, 6)
        .doOnNext(data -> Logger.log(DO_ON_NEXT, data))
        .delay(2000L, TimeUnit.MILLISECONDS)
        .subscribe(data -> Logger.log(ON_NEXT, data));

    TimeUtil.sleep(2500L);

    Logger.log("---------------------------------------------------------------------");

    Observable.just(1, 3, 5, 7)
        .delay(item -> {
          TimeUtil.sleep(1000L);

          // 새로운 Observable의 통지 시점에, 원본 데이터를 통지한다.
          return Observable.just(item);
        }).subscribe(data -> Logger.log(ON_NEXT, data));
  }

  // delaySubscription
  // 생산자가 데이터의 생성 및 통지 자체를 설정한 시간만큼 지연
  // 소비자가 구독을 해도 구독 시점 자체가 지연
  private static void delaySubscription() {
    Logger.log(PRINT, "# 실행 시작 시간");

    Observable.just(1, 3, 4, 6)
        .doOnNext(data -> Logger.log(DO_ON_NEXT, data))
        .delaySubscription(2000L, TimeUnit.MILLISECONDS)
        .subscribe(data -> Logger.log(ON_NEXT, data));

    TimeUtil.sleep(2500L);
  }

  // timeout
  // 각각의 데이터 통지 시, 지정된 시간안에 통지가 되지 않으면 에러를 통지
  // 에러 통지 시 전달되는 에러 객체는 TimeoutException
  // 네트워크 연결 지연 등으로 인한 처리를 위해 사용하기 좋은 연산자
  private static void timeout() {
    Observable.range(1, 5)
        .map(num -> {
          long time = 1000L;
          if (num == 4) {
            time = 1500L;
          }
          TimeUtil.sleep(time);
          return num;
        })
        .timeout(1200L, TimeUnit.MILLISECONDS)
        .subscribe(
            data -> Logger.log(ON_NEXT, data),
            error -> Logger.log(ON_ERROR, error)
        );

    TimeUtil.sleep(4000L);
  }

  // timeInterval
  // 각각의 데이터가 통지되는데 걸린 시간을 통지
  // 통지된 데이터와 데이터가 통지되는데 걸린 시간을 소비자쪽에서 모두 처리
  private static void timeInterval() {
    Observable.just(1, 3, 5, 7, 9)
        .delay(item -> {
          TimeUtil.sleep(NumberUtil.randomRange(100, 1000));
          return Observable.just(item);
        })
        .timeInterval()
        .subscribe(
            timed -> Logger.log(ON_NEXT,
                "# 통지하는데 걸린 시간: " + timed.time() + "\t# 통지된 데이터: " + timed.value())
        );
  }

  // materialize
  // 통지된 데이터와 통지된 데이터의 통지 타입 자체를 Notification 객체에 담고 이 Notification 객체를 통지
  // 통지 데이터의 메타 데이터를 포함해서 통지한다고 볼 수 있음
  // dematerialize
  // 통지된 Notification 객체를 원래의 통지 데이터로 변환해서 통지
  private static void materializeAndDematerialize() {
    Observable.just(1, 2, 3, 4, 5, 6)
        .materialize()
        .subscribe(notification -> {
          String notificationType =
              notification.isOnNext() ? "onNext()"
                  : (notification.isOnError() ? "onError()" : "onComplete()");
          Logger.log(PRINT, "notification 타입: " + notificationType);
          Logger.log(ON_NEXT, notification.getValue());
        });

    Logger.log("---------------------------------------------------------------------");

    // 특정 Observable 에서 에러가 발생 할 경우 해당 에러에 대해서 구체적으로 처리할 수 있음
    Observable.concatEager(
        Observable.just(
            getDBUser().subscribeOn(Schedulers.io()),
            getAPIUser()
                .subscribeOn(Schedulers.io())
                .materialize()
                .map(notification -> {
                  if (notification.isOnError()) {
                    // 관리자에게 에러 발생을 알림
                    Logger.log(LogType.PRINT, "# API user 에러 발생!");
                  }
                  return notification;
                })
                .filter(notification -> !notification.isOnError())
                .dematerialize(notification -> notification)
        )
    ).subscribe(
        data -> Logger.log(ON_NEXT, data),
        error -> Logger.log(ON_ERROR, error),
        () -> Logger.log(ON_COMPLETE)
    );

    TimeUtil.sleep(1000L);
  }

  private static Observable<String> getDBUser() {
    return Observable.fromIterable(
        Arrays.asList("DB user1", "DB user2", "DB user3", "DB user4", "DB user5"));
  }

  private static Observable<String> getAPIUser() {
    return Observable
        .just("API user1", "API user2", "Not User", "API user4", "API user5")
        .map(user -> {
          if(user.equals("Not User"))
            throw new RuntimeException();
          return user;
        });
  }

}
