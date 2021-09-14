package io.devfactory.rxjava3.operators;

import static io.devfactory.rxjava3.utils.LogType.DO_ON_COMPLETE;
import static io.devfactory.rxjava3.utils.LogType.DO_ON_NEXT;
import static io.devfactory.rxjava3.utils.LogType.ON_NEXT;

import io.devfactory.rxjava3.common.Car;
import io.devfactory.rxjava3.common.CarMaker;
import io.devfactory.rxjava3.common.SampleData;
import io.devfactory.rxjava3.utils.LogType;
import io.devfactory.rxjava3.utils.Logger;
import io.devfactory.rxjava3.utils.TimeUtil;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@SuppressWarnings({"squid:S125", "squid:S1144", "squid:S1192"})
public class ConditionOperatorExample {

  // 조건과 불린 연산자
  public static void main(String[] args) {
//    all();
//    amb();
//    contains();
//    defaultIfEmpty();
//    sequenceEqual();
  }

  // all
  // 통지되는 모든 데이터가 설정한 조건에 맞는지를 판단
  // 결과값을 한번만 통지하면 되기때문에 true/false 값을 Single로 반환
  // 통지된 데이터가 조건에 맞지 않는다면 이후 데이터는 구독 해지되어 통지 되지 않음
  private static void all() {
    Observable.fromIterable(SampleData.carList)
        .doOnNext(car -> Logger.log(DO_ON_NEXT,
            "Car Maker: " + car.getCarMaker() + ", \tCar Name: " + car.getCarName()))
        .map(Car::getCarMaker)
        .all(carMaker -> carMaker.equals(CarMaker.CHEVROLET))
//        .all(CarMaker.CHEVROLET::equals)
        .subscribe(data -> Logger.log(ON_NEXT, data));
  }

  // amb
  // 여러개의 Observable 중에서 최초 통지 시점이 가장 빠른 Observable의 데이터만 통지되고, 나머지 Observable은 무시
  // 가장 먼저 통지를 시작한 Observable의 데이터만 통지
  private static void amb() {
    List<Observable<Integer>> observables = Arrays.asList(
        Observable.fromIterable(SampleData.salesOfBranchA)
            .delay(200L, TimeUnit.MILLISECONDS)
            .doOnComplete(() -> Logger.log(DO_ON_COMPLETE, "# branch A's sales")),
        Observable.fromIterable(SampleData.salesOfBranchB)
            .delay(300L, TimeUnit.MILLISECONDS)
            .doOnComplete(() -> Logger.log(DO_ON_COMPLETE, "# branch B's sales")),
        Observable.fromIterable(SampleData.salesOfBranchC)
            .delay(500L, TimeUnit.MILLISECONDS)
            .doOnComplete(() -> Logger.log(DO_ON_COMPLETE, "# branch C's sales"))
    );

    Observable.amb(observables)
        .doOnComplete(() -> Logger.log(DO_ON_COMPLETE, "# 완료"))
        .subscribe(data -> Logger.log(ON_NEXT, data));

    TimeUtil.sleep(1000L);
  }

  // contains
  // 파라미터의 데이터가 Observable에 포함되어 있는지를 판단
  // 결과값을 한번만 통지하면 되기때문에 true/false 값을 Single로 반환
  // 결과 통지 시점은 Observable에 포함된 데이터를 통지하거나 완료를 통지할때임
  private static void contains() {
    Observable.fromArray(SampleData.carMakersDuplicated)
        .doOnNext(data -> Logger.log(DO_ON_NEXT, data))
        .contains(CarMaker.SAMSUNG)
        .subscribe(data -> Logger.log(ON_NEXT, data));
  }

  // defaultIfEmpty
  // 통지할 데이터가 없을 경우 파라미터로 입력된 값을 통지
  // 연산자 이름 의미 그대로 Observable에 통지할 데이터가 없이 비어 있는 상태일때 디폴트 값을 통지
  private static void defaultIfEmpty() {
    Observable.just(1, 2, 3, 4, 12, 5, 11)
        .filter(num -> num > 10)
        .defaultIfEmpty(10)
        .subscribe(data -> Logger.log(LogType.ON_NEXT, data));
  }

  // sequenceEqual
  // 두 Observable이 동일한 순서로 동일한 갯수의 같은 데이터를 통지하는지 판단
  // 통지 시점과 무관하게 데이터의 정합성만 판단하므로 통지 시점이 다르더라도 조건이 맞다면 true를 통지
  private static void sequenceEqual() {
    Observable<CarMaker> observable1 =
        Observable
            .fromArray(SampleData.carMakers)
            .subscribeOn(Schedulers.computation())
            .delay(carMaker -> {
              TimeUtil.sleep(500L);
              return Observable.just(carMaker);
            }).doOnNext(data -> Logger.log(DO_ON_NEXT, "# observable1 : " + data));

    Observable<CarMaker> observable2 =
        Observable
            .fromArray(SampleData.carMakersDuplicated)
            .delay(carMaker -> {
              TimeUtil.sleep(1000L);
              return Observable.just(carMaker);
            }).doOnNext(data -> Logger.log(DO_ON_NEXT, "# observable2 : " + data));

    Observable.sequenceEqual(observable1, observable2)
        .subscribe(data -> Logger.log(ON_NEXT, data));
  }

}
