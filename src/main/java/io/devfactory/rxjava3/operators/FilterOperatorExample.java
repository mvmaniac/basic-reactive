package io.devfactory.rxjava3.operators;

import static io.devfactory.rxjava3.common.CarMaker.CHEVROLET;
import static io.devfactory.rxjava3.common.CarMaker.SSANGYOUNG;
import static io.devfactory.utils.LogType.ON_NEXT;

import io.devfactory.rxjava3.common.Car;
import io.devfactory.rxjava3.common.SampleData;
import io.devfactory.utils.LogType;
import io.devfactory.utils.Logger;
import io.devfactory.utils.TimeUtil;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import java.util.concurrent.TimeUnit;

// 데이터 필터링 연산자
@SuppressWarnings("squid:S106")
public class FilterOperatorExample {

  public static void main(String[] args) {
    // filter
    Observable.fromIterable(SampleData.carList)
        .filter(car -> car.getCarMaker() == CHEVROLET)
        .subscribe(car -> Logger.log(ON_NEXT, car.getCarMaker() + ":" + car.getCarName()));

    System.out.println("--------------------------------------");

    Observable.fromIterable(SampleData.carList)
        .filter(car -> car.getCarMaker() == CHEVROLET)
        .filter(car -> car.getCarPrice() > 30000000)
        .subscribe(car -> System.out.println(car.getCarName()));

    System.out.println("=====================================");

    // distinct
    // 이미 통지한 데이터와 같은 데이터는 제외하고 통지
    // 유일한 값을 처리하고자 할때 사용
    Observable.fromArray(SampleData.carMakersDuplicated)
        .distinct()
        .subscribe(carMaker -> Logger.log(LogType.ON_NEXT, carMaker));

    System.out.println("--------------------------------------");

    Observable.fromArray(SampleData.carMakersDuplicated)
        .distinct()
        .filter(carMaker -> carMaker == SSANGYOUNG)
        .subscribe(carMaker -> Logger.log(ON_NEXT, carMaker));

    System.out.println("--------------------------------------");

    // 객체의 특정 필드를 기준으로 distinct 하는 예제
    Observable.fromIterable(SampleData.carList)
        .distinct(Car::getCarMaker)
        .subscribe(car -> Logger.log(LogType.ON_NEXT, car.getCarName()));

    System.out.println("=====================================");

    // take
    // 지정한 갯수만큼 데이터를 발행
    Observable.just("a", "b", "c", "d")
        .take(2)
        .subscribe(data -> Logger.log(ON_NEXT, data));

    System.out.println("--------------------------------------");

    // 지정한 시간동안 데이터를 계속 발행
    Observable.interval(1000L, TimeUnit.MILLISECONDS)
        .take(3500L, TimeUnit.MILLISECONDS)
        .subscribe(data -> Logger.log(ON_NEXT, data));

    TimeUtil.sleep(3500L);

    System.out.println("=====================================");

    // takeUntil
    // 파리미터로 지정한 조건이 될 때까지 데이터를 계속 발행
    Observable.fromIterable(SampleData.carList)
        .takeUntil((Car car) -> car.getCarName().equals("트랙스"))
        .subscribe(car -> System.out.println(car.getCarName()));

    TimeUtil.sleep(300L);

    System.out.println("--------------------------------------");

    // 파라미터로 받은 Flowable/Observable이 최초로 데이터를 발행할 때까지 계속 데이터를 발행
    // timer와 함께 사용하여 특정 시점이 되기전까지 데이터를 발행하는데 활용하기 용이
    Observable.interval(1000L, TimeUnit.MILLISECONDS)
        .takeUntil(Observable.timer(5500L, TimeUnit.MILLISECONDS))
        .subscribe(data -> Logger.log(ON_NEXT, data));

    TimeUtil.sleep(5500L);

    System.out.println("=====================================");

    // skip
    Observable.range(1, 15)
        .skip(3)
        .subscribe(data -> Logger.log(ON_NEXT, data));

    final Disposable skipSubscribe = Observable.interval(300L, TimeUnit.MILLISECONDS)
        .skip(1000L, TimeUnit.MILLISECONDS)
        .subscribe(data -> Logger.log(ON_NEXT, data));

    TimeUtil.sleep(3000L);
    skipSubscribe.dispose(); // 다음 예제를 위해 종료 시킴

    System.out.println("--------------------------------------");

    Observable.fromIterable(SampleData.carList)
        .skipWhile(car -> !car.getCarName().equals("티볼리"))
        .subscribe(car -> Logger.log(ON_NEXT, car.getCarName()));

    System.out.println("=====================================");

    // 1. filter를 이용하여 SampleData.carList 중에서 CarMaker가 SSANGYOUNG인 차들의 carName을 출력
    Observable.fromIterable(SampleData.carList)
        .filter(car -> car.getCarMaker() == SSANGYOUNG)
        .subscribe(car -> Logger.log(ON_NEXT, car.getCarMaker() + ":" + car.getCarName()));

    System.out.println("--------------------------------------");

    // 2. interval, takeWhile을 이용하여 발행된 숫자가 10이 아닌동안 데이터를 1초에 한번씩 계속 출력
    Observable.interval(1000L, TimeUnit.MILLISECONDS)
        .takeWhile(data -> data != 10)
        .subscribe(data -> Logger.log(ON_NEXT, data));

    TimeUtil.sleep(11500L);

    System.out.println("--------------------------------------");

    // 3. interval, skipUntil, timer를 이용하여 1초에 한번씩 발행된 데이터 중에서 3초 후에 발행된 데이터만 10까지 출력
    Observable.interval(1000L, TimeUnit.MILLISECONDS)
        .skipUntil(Observable.timer(3000L, TimeUnit.MILLISECONDS))
        .subscribe(data -> Logger.log(ON_NEXT, data));

    TimeUtil.sleep(11500L);

    System.out.println("--------------------------------------");

    // 4. range, skipLast를 이용하여 1부터 15까지의 숫자중에서 마지막에 발행된 숫자 3개를 제외한 나머지 숫자를 출력
    Observable.range(1, 15)
        .skipLast(3)
        .subscribe(data -> Logger.log(ON_NEXT, data));
  }

}
