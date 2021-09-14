package io.devfactory.rxjava3.operators;

import static io.devfactory.rxjava3.utils.LogType.ON_NEXT;

import io.devfactory.rxjava3.common.SampleData;
import io.devfactory.rxjava3.utils.Logger;
import io.devfactory.rxjava3.utils.NumberUtil;
import io.devfactory.rxjava3.utils.TimeUtil;
import io.reactivex.rxjava3.core.Observable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

// 결합 연산자
@SuppressWarnings({"squid:S125", "squid:S1144", "squid:S1192"})
public class DataCombinationOperatorExample {

  // 결합 연산자
  public static void main(String[] args) {
//    merge();
//    concat();
//    zip();
//    combineLatest();
//    quiz();
  }

  // merge
  // 다수의 Observable에서 통지된 데이터를 받아서 다시 하나의 Observable로 통지
  // 통지 시점이 빠른 Observable의 데이터부터 순차적으로 통지되고 통지 시점이 같을 경우에는 merge( )함수의 파라미터로 먼저 지정된 Observable의 데이터부터 통지
  private static void merge() {
    Observable<Long> observable1 = Observable.interval(200L, TimeUnit.MILLISECONDS)
        .take(5);

    Observable<Long> observable2 = Observable.interval(400L, TimeUnit.MILLISECONDS)
        .take(5)
        .map(num -> num + 1000);

    Observable.merge(observable1, observable2)
        .subscribe(data -> Logger.log(ON_NEXT, data));

    TimeUtil.sleep(4000);

    Logger.log("---------------------------------------------------------------------");

    Observable<String> observable3 =
        SampleData.getSpeedPerSection("A", 55L, SampleData.speedOfSectionA);
    Observable<String> observable4 =
        SampleData.getSpeedPerSection("B", 100L, SampleData.speedOfSectionB);
    Observable<String> observable5 =
        SampleData.getSpeedPerSection("C", 77L, SampleData.speedOfSectionC);

    Observable.merge(observable3, observable4, observable5)
        .subscribe(data -> Logger.log(ON_NEXT, data));

    TimeUtil.sleep(1000L);
  }

  // concat
  // 다수의 Observable에서 통지된 데이터를 받아서 다시 하나의 Observable로 통지
  // 하나의 Observable에서 통지가 끝나면 다음 Observable에서 연이어서 통지
  // 각 Observable의 통지 시점과는 상관없이 concat( ) 함수의 파라미터로 먼저 입력된 Observable의 데이터부터 모두 통지 된 후, 다음 Observable의 데이터가 통지
  private static void concat() {
    Observable<Long> observable1 =
        Observable.interval(500L, TimeUnit.MILLISECONDS)
            .take(4);

    Observable<Long> observable2 =
        Observable.interval(300L, TimeUnit.MILLISECONDS)
            .take(5)
            .map(num -> num + 1000);

    Observable.concat(observable2, observable1)
        .subscribe(data -> Logger.log(ON_NEXT, data));

    TimeUtil.sleep(3500L);

    Logger.log("---------------------------------------------------------------------");

    List<Observable<String>> speedPerSectionList = Arrays.asList(
        SampleData.getSpeedPerSection("A", 55L, SampleData.speedOfSectionA),
        SampleData.getSpeedPerSection("B", 100L, SampleData.speedOfSectionB),
        SampleData.getSpeedPerSection("C", 77L, SampleData.speedOfSectionC)
    );

    Observable.concat(speedPerSectionList)
        .subscribe(data -> Logger.log(ON_NEXT, data));

    TimeUtil.sleep(2000L);
  }

  // zip
  // 다수의 Observable에서 통지된 데이터를 받아서 다시 하나의 Observable로 통지
  // 각 Observable에서 통지된 데이터가 모두 모이면 각 Observable에서 동일한 index의 데이터로 새로운 데이터를 생성한 후 통지
  // 통지하는 데이터 개수가 가장 적은 Observable의 통지 시점에 완료 통지 시점을 맞춤
  private static void zip() {
    Observable<Long> observable1 =
        Observable.interval(200L, TimeUnit.MILLISECONDS)
            .take(4);

    Observable<Long> observable2 =
        Observable.interval(400L, TimeUnit.MILLISECONDS)
            .take(6);

    Observable.zip(observable1, observable2, Long::sum)
        .subscribe(data -> Logger.log(ON_NEXT, data));

    TimeUtil.sleep(3000L);

    Logger.log("---------------------------------------------------------------------");

    Observable<Integer> observable3 = Observable.fromIterable(SampleData.seoulPM10List);
    Observable<Integer> observable4 = Observable.fromIterable(SampleData.busanPM10List);
    Observable<Integer> observable5 = Observable.fromIterable(SampleData.incheonPM10List);

    Observable<Integer> observable13 = Observable.range(1, 24);

    Observable.zip(observable3, observable4, observable5, observable13,
        (data1, data2, data3, hour) -> hour + "시: " + Collections
            .max(Arrays.asList(data1, data2, data3)))
        .subscribe(data -> Logger.log(ON_NEXT, data));

  }

  // combineLatest
  // 다수의 Observable에서 통지된 데이터를 받아서 다시 하나의 Observable로 통지
  // 각 Observable에서 데이터를 통지할 때마다 모든 Observable에서 마지막으로 통지한 각 데이터를 함수형 인터페이스에 전달하고, 새로운 데이터를 생성해 통지
  private static void combineLatest() {
    Observable<Long> observable1 =
        Observable.interval(500L, TimeUnit.MILLISECONDS)
            //.doOnNext(data -> Logger.log("# observable 1 : " + data))
            .take(4);

    Observable<Long> observable2 =
        Observable.interval(700L, TimeUnit.MILLISECONDS)
            //.doOnNext(data -> Logger.log("# observable 2 : " + data))
            .take(4);

    Observable.combineLatest(observable1, observable2,
        (data1, data2) -> "data1: " + data1 + "\tdata2: " + data2)
        .subscribe(data -> Logger.log(ON_NEXT, data));

    TimeUtil.sleep(3000L);

    Logger.log("---------------------------------------------------------------------");

    // 랜덤 온도 데이터
    Observable<Integer> observable3 = Observable
        .interval(NumberUtil.randomRange(100, 500), TimeUnit.MILLISECONDS)
        .take(10)
        .map(notUse -> SampleData.temperatureOfSeoul[NumberUtil.randomRange(0, 5)]);

    // 랜덤 습도 데이터
    Observable<Integer> observable4 = Observable
        .interval(NumberUtil.randomRange(100, 500), TimeUnit.MILLISECONDS)
        .take(10)
        .map(notUse -> SampleData.humidityOfSeoul[NumberUtil.randomRange(0, 5)]);

    Observable.combineLatest(observable3, observable4,
        (temperature, humidity) -> "최신 온습도 데이터 - 온도: " + temperature + "도\t습도: " + humidity + "%")
        .subscribe(data -> Logger.log(ON_NEXT, data));

    TimeUtil.sleep(3000L);
  }

  private static void quiz() {
    // zip을 이용하여 각 지점별 월별 매출(SampleData.salesOfBranchA, SampleData.salesOfBranchB, SampleData.salesOfBranchC)
    // 을 각각의 월별로 합산하여 통합 월별 매출을 출력
    // (지점별 월별 매출 List(salesOfBranchA, salesOfBranchB, salesOfBranchC)는 index가 빠른 요소부터 1월.)
    List<Observable<Integer>> salesList = Arrays.asList(
        Observable.fromIterable(SampleData.salesOfBranchA),
        Observable.fromIterable(SampleData.salesOfBranchB),
        Observable.fromIterable(SampleData.salesOfBranchC)
    );

    Observable.zip(salesList, sales -> (int) sales[0] + (int) sales[1] + (int) sales[2])
        .subscribe(data -> Logger.log(ON_NEXT, data));
  }

}
