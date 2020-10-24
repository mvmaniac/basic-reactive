package io.devfactory.rxjava3.operators;

import static io.devfactory.utils.LogType.DO_ON_NEXT;
import static io.devfactory.utils.LogType.ON_NEXT;
import static io.devfactory.utils.LogType.PRINT;

import io.devfactory.rxjava3.common.SampleData;
import io.devfactory.utils.LogType;
import io.devfactory.utils.Logger;
import io.reactivex.rxjava3.core.Observable;
import java.util.Arrays;

@SuppressWarnings({"squid:S106", "squid:S125", "squid:S1144", "squid:S1192"})
public class DataTotalOperatorExample {

  // 데이터 집계 연산자
  public static void main(String[] args) {
//    count();
//    reduce();
//    scan();
  }

  // count
  // Observable이 통지한 데이터의 총 개수를 통지
  // 총 개수만 통지하면 되므로 결과값은 Single로 반환
  // 데이터의 총 개수를 통지하는 시점은 완료 통지를 받은 시점임
  private static void count() {
    Observable.fromIterable(SampleData.carList)
        .count()
        .subscribe(data -> Logger.log(ON_NEXT, data));

    Logger.log("---------------------------------------------------------------------");

    Observable.concat(
        Arrays.asList(
            Observable.fromIterable(SampleData.seoulPM10List),
            Observable.fromIterable(SampleData.busanPM10List),
            Observable.fromIterable(SampleData.incheonPM10List)))
        .count()
        .subscribe(data -> Logger.log(ON_NEXT, data));
  }

  // reduce
  // Observable이 통지한 데이터를 이용해서 어떤 결과를 일정한 방식으로 합성한 후, 최종 결과를 반환
  // Observable이 통지한 데이터가 숫자일 경우 파라미터로 지정한 함수형 인터페이스에 정의된 계산 방식으로 값을 집계 할 수 있음
  private static void reduce() {
    Observable.just(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
        .doOnNext(data -> Logger.log(DO_ON_NEXT, data))
        .reduce(Integer::sum)
        .subscribe(result -> Logger.log(ON_NEXT, "# 1부터 10까지의 누적 합계: " + result));

    Logger.log("---------------------------------------------------------------------");

    Observable.just(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
        //.doOnNext(data -> Logger.log(DO_ON_NEXT, data))
        .reduce(0, (x, y) -> {
          Logger.log(PRINT, "# reduce 입력 값 : " + x + ", " + y);
          return x + y;
        })
        .subscribe(data -> Logger.log(ON_NEXT, "# 출력 결과: " + data));

    Logger.log("---------------------------------------------------------------------");

    Observable.just("a", "b", "c", "d", "e")
        //.doOnNext(data -> Logger.log(DO_ON_NEXT, data))
        .reduce((x, y) -> "(" + x + ", " + y + ")")
        .subscribe(data -> Logger.log(ON_NEXT, "# 출력 결과: " + data));
  }

  // scan
  // 집계 중간 결과를 계속해서 출력
  private static void scan() {
    Observable.just(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
        .doOnNext(data -> Logger.log(DO_ON_NEXT, data))
        .scan((x, y) -> x + y)
        .subscribe(result -> Logger.log(ON_NEXT, "# 1부터 10까지의 누적 합계: " + result));

    Logger.log("---------------------------------------------------------------------");

    Observable.just(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
        .scan(0, (x, y) -> x + y)
        .subscribe(data -> Logger.log(LogType.ON_NEXT, "# 출력 결과: " + data));

    Logger.log("---------------------------------------------------------------------");

    Observable.just("a", "b", "c", "d", "e")
        .scan((x, y) -> "(" + x + ", " + y + ")")
        .subscribe(data -> Logger.log(ON_NEXT, "# 출력 결과: " + data));
  }

}
