package io.devfactory.rxjava3.operators;

import io.devfactory.rxjava3.common.Searcher;
import io.devfactory.utils.Logger;
import io.devfactory.utils.TimeUtil;
import io.reactivex.rxjava3.core.Observable;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static io.devfactory.utils.LogType.*;

@SuppressWarnings({"squid:S125", "squid:S1144", "squid:S1192"})
public class DataConversionOperatorExample1 {

  // 데이터 변환 연산자 1
  public static void main(String[] args) throws InterruptedException {
//    map();
//    flatMap();
//    concatMap();
//    switchMap();
//    quiz();
  }

  // map
  // Observable 이 통지한 항목에 함수를 적용하여 통지된 값을 변환
  // null을 반환하면 NullpointException이 발생하므로 null이 아닌 데이터 하나를 반드시 반환
  private static void map() {
    List<Integer> oddList = Arrays.asList(1, 3, 5, 7);
    Observable.fromIterable(oddList)
        .map(num -> "1을 더한 결과: " + (num + 1))
        .subscribe(data -> Logger.log(ON_NEXT, data));

    Logger.log("---------------------------------------------------------------------");

    Observable.just("korea", "america", "canada", "paris", "japan", "china")
        .filter(country -> country.length() == 5)
        .map(country -> country.toUpperCase().charAt(0) + country.substring(1))
        .subscribe(data -> Logger.log(ON_NEXT, data));
  }

  // flatMap
  // 원본 데이터를 원하는 값으로 변환 후 통지하는것은 map과 같음
  // 1 대 다 변환하므로 데이터 한개로 여러 데이터를 통지
  // map은 변환된 데이터를 반환하지만 flatMap은 변환 된 여러개의 데이터를 담고 있는 새로운 Observable을 반환
  private static void flatMap() {
    Observable.just("Hello")
        .flatMap(hello -> Observable.just("자바", "파이썬", "안드로이드").map(lang -> hello + ", " + lang))
        .subscribe(data -> Logger.log(ON_NEXT, data));

    Logger.log("---------------------------------------------------------------------");

    Observable.range(2, 1)
        .flatMap(num -> Observable.range(1, 9).map(row -> num + " * " + row + " = " + num * row))
        .subscribe(data -> Logger.log(ON_NEXT, data));

    Logger.log("---------------------------------------------------------------------");

    // 원본 데이터와 변환된 데이터를 조합해서 새로운 데이터를 통지
    // Observable에 원본 데이터 + 변환된 데이터 = 최종 데이터 를 실어서 반환
    Observable.range(2, 1)
        .flatMap(
            data -> Observable.range(1, 9),
            (sourceData, transformedData) ->
                sourceData + " * " + transformedData + " = " + sourceData * transformedData
        )
        .subscribe(data -> Logger.log(ON_NEXT, data));
  }

  // concatMap
  // flatMap과 마찬가지로 받은 데이터를 변환하여 새로운 Observable 로 반환
  // 반환된 새로운 Observable을 하나씩 순서대로 실행하는것이 FlatMap과 다름
  // 데이터의 처리 순서는 보장하지만 처리중인 Observable의 처리가 끝나야 다음 Observable이 실행되므로 처리 성능에는 영향을 줄 수 있음
  private static void concatMap() {
    // 순서를 보장해주는 concatMap
    // 순차적으로 실행되기때문에 flatMap보다 느림
    TimeUtil.start();
    Observable.interval(100L, TimeUnit.MILLISECONDS)
        .take(4)
        .skip(2)
        .concatMap(
            num -> Observable.interval(200L, TimeUnit.MILLISECONDS)
                .take(10)
                .skip(1)
                .map(row -> num + " * " + row + " = " + num * row)
        ).subscribe(
        data -> Logger.log(ON_NEXT, data),
        error -> {
        },
        () -> {
          TimeUtil.end();
          TimeUtil.takeTime();
        }
    );

    TimeUtil.sleep(5000L);

    Logger.log("---------------------------------------------------------------------");

    // concatMap과 달리 순서를 보장해주지 않는 flatMap
    // 실행 속도가 concatMap 보다 빠르다.
    TimeUtil.start();
    Observable.interval(100L, TimeUnit.MILLISECONDS)
        .take(4)
        .skip(2)
        .flatMap(
            num -> Observable.interval(200L, TimeUnit.MILLISECONDS)
                .take(10)
                .skip(1)
                .map(row -> num + " * " + row + " = " + num * row)
        )
        .subscribe(
            data -> Logger.log(ON_NEXT, data),
            error -> {
            },
            () -> {
              TimeUtil.end();
              TimeUtil.takeTime();
            });

    TimeUtil.sleep(3000L);
  }

  // switchMap
  // concatMap과 마찬가지로 받은 데이터를 변환하여 새로운 Observable로 반환
  // concatMap과 다른점은 switchMap은 순서를 보장하지만 새로운 데이터가 통지되면 현재 처리중이던 작업을 바로 중단
  // 여러개의 발행된 값 중에 마지막에 들어온 값만 처리하고자 할 때 유용
  private static void switchMap() throws InterruptedException {
    Logger.log(PRINT, "# start");

    Observable.interval(100L, TimeUnit.MILLISECONDS)
        .take(4)
        .skip(2)
        .doOnNext(data -> Logger.log(DO_ON_NEXT, data))
        .switchMap(
            num -> Observable.interval(300L, TimeUnit.MILLISECONDS)
                .take(10)
                .skip(1)
                .map(row -> num + " * " + row + " = " + num * row)
        )
        .subscribe(data -> Logger.log(ON_NEXT, data));

    Thread.sleep(5000);

    Logger.log("---------------------------------------------------------------------");

    // witchMap 대신 concatMap을 쓸 경우 비효율적인 검색 예제
    TimeUtil.start();

    Searcher searcher = new Searcher();
    // 사용자가 입력하는 검색어라고 가정한다.
    final List<String> keywords = Arrays.asList("M", "Ma", "Mal", "Malay");

    Observable.interval(100L, TimeUnit.MILLISECONDS)
        .take(4)
        .concatMap(data -> { /** concatMap을 사용했기때문에 매번 모든 키워드 검색 결과를  다 가져온다.*/
          String keyword = keywords.get(data.intValue()); // 데이터베이스에서 조회한다고 가정한다.

          return Observable.just(searcher.search(keyword))
              .doOnNext(notUse -> Logger.log("============================================"))
              .delay(1000L, TimeUnit.MILLISECONDS);
        })
        .flatMap(Observable::fromIterable)
        .subscribe(
            data -> Logger.log(ON_NEXT, data),
            error -> {
            },
            () -> {
              TimeUtil.end();
              TimeUtil.takeTime();
            }
        );

    TimeUtil.sleep(6000L);

    Logger.log("---------------------------------------------------------------------");

    // switchMap을 이용한 효율적인 키워드 검색 예제
    TimeUtil.start();

    Observable.interval(100L, TimeUnit.MILLISECONDS)
        .take(4)
        .doOnNext(data -> Logger.log(DO_ON_NEXT, data))
        .switchMap(data -> { /** switchMap을 사용했기 때문에 마지막 키워드를 사용한 최신 검색 결과만 가져온다 */
          String keyword = keywords.get(data.intValue()); // 데이터베이스에서 조회한다고 가정한다.

          return Observable.just(searcher.search(keyword))
              .delay(1000L, TimeUnit.MILLISECONDS);
        })
        .flatMap(Observable::fromIterable)
        .subscribe(
            data -> Logger.log(ON_NEXT, data),
            error -> {
            },
            () -> {
              TimeUtil.end();
              TimeUtil.takeTime();
            }
        );

    TimeUtil.sleep(2000L);
  }

  private static void quiz() {
    // range, filter, map을 이용하여 1부터 15 까지의 숫자 중에 2의 배수만 필터링 한 후, 필터링된 숫자에 제곱한 숫자를 출력
    Observable.range(1, 15)
        .filter(data -> data % 2 == 0)
        .map(data -> Math.pow(data, 2))
        .subscribe(data -> Logger.log(ON_NEXT, data));

    // range, filter, flatMap을 이용하여 2에서 9까지의 구구단 중에서 짝수단만 출력
    Observable.range(2, 8)
        .filter(data -> data % 2 == 0)
        .flatMap(data -> Observable.range(1, 9)
            .map(row -> data + " * " + row + " = " + data * row))
        .subscribe(data -> Logger.log(ON_NEXT, data));
  }

}
