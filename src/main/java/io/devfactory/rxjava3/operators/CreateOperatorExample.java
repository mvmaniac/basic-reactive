package io.devfactory.rxjava3.operators;

import static io.devfactory.utils.LogType.ON_NEXT;
import static io.devfactory.utils.LogType.PRINT;

import io.devfactory.utils.LogType;
import io.devfactory.utils.Logger;
import io.devfactory.utils.TimeUtil;
import io.reactivex.rxjava3.core.Observable;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@SuppressWarnings({"squid:S125", "squid:S1144", "squid:S1192"})
public class CreateOperatorExample {

  // Flowable/Observable 생성 연산자
  // create 연산자는 object 패키지 참고
  public static void main(String[] args) throws InterruptedException {
//    interval();
//    range();
//    timer();
//    defer();
//    fromIterable();
//    fromFuture();
  }

  // interval
  // polling 용도로 주로 사용
  // 호출한 스레드와는 별도의 스레드에서 실행
  private static void interval() {
    Logger.log(PRINT, "# start");

    Observable.interval(1000L, TimeUnit.MILLISECONDS)
        .map(num -> num + " count")
        .subscribe(data -> Logger.log(ON_NEXT, data));

    TimeUtil.sleep(3000);
  }

  // range
  // for, while 문 등의 반복으로 사용
  private static void range() {
    Observable<Integer> source = Observable.range(0, 5);
    source.subscribe(num -> Logger.log(ON_NEXT, num));
  }

  // timer
  // 설정한 시간이 지난 후에 특정 동작을 수행하고자 할때 사용
  // 호출한 스레드와는 별도의 스레드에서 실행
  private static void timer() {
    Logger.log(PRINT, "# start");

    Observable<String> observable =
        Observable.timer(2000, TimeUnit.MILLISECONDS).map(count -> "Do work!");

    observable.subscribe(data -> Logger.log(ON_NEXT, data));

    TimeUtil.sleep(3000);
  }

  // defer
  // 선언한 시점의 데이터를 통지하는 것이 아니라 호출 시점의 데이터를 통지
  // 실제 구독이 발생할 때 Observable 를 새로 반환하여 새로운 Observable 를 생성
  // defer 를 활용하면 데이터 흐름의 생성을 지연하는 효과를 보여주기 때문에 최신 데이터를 얻고자할 때 활용할
  private static void defer() throws InterruptedException {
    Observable<LocalTime> observable = Observable.defer(() -> {
      LocalTime currentTime = LocalTime.now();
      return Observable.just(currentTime);
    });

    Observable<LocalTime> observableJust = Observable.just(LocalTime.now());

    observable.subscribe(time -> Logger.log(PRINT, " # defer() 구독1의 구독 시간: " + time));
    observableJust.subscribe(time -> Logger.log(PRINT, " # just() 구독1의 구독 시간: " + time));

    Thread.sleep(3000);

    observable.subscribe(time -> Logger.log(PRINT, " # defer() 구독2의 구독 시간: " + time));
    observableJust.subscribe(time -> Logger.log(PRINT, " # just() 구독자2의 구독 시간: " + time));
  }

  // fromIterable
  private static void fromIterable() {
    List<String> countries = Arrays.asList("Korea", "Canada", "USA", "Italy");
    Observable.fromIterable(countries).subscribe(country -> Logger.log(ON_NEXT, country));
  }

  // fromFuture
  public static void fromFuture() {
    Logger.log(PRINT, "# start time");

    // 긴 처리 시간이 걸리는 작업
    Future<Double> future = longTimeWork();

    // 짧은 처리 시간이 걸리는 작업
    shortTimeWork();

    Observable.fromFuture(future)
        .subscribe(data -> Logger.log(LogType.PRINT, "# 긴 처리 시간 작업 결과 : " + data));

    Logger.log(LogType.PRINT, "# end time");
  }

  private static CompletableFuture<Double> longTimeWork(){
    return CompletableFuture.supplyAsync(CreateOperatorExample::calculate);
  }

  private static Double calculate() {
    Logger.log(PRINT, "# 긴 처리 시간이 걸리는 작업 중.........");
    TimeUtil.sleep(6000L);
    return 100000000000000000.0;
  }

  private static void shortTimeWork() {
    TimeUtil.sleep(3000L);
    Logger.log(PRINT, "# 짧은 처리 시간 작업 완료!");
  }

}
