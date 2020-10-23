package io.devfactory.rxjava3.operators;

import static io.devfactory.utils.LogType.ON_NEXT;
import static io.devfactory.utils.LogType.PRINT;

import io.devfactory.rxjava3.common.Car;
import io.devfactory.rxjava3.common.CarMaker;
import io.devfactory.rxjava3.common.SampleData;
import io.devfactory.utils.Logger;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.observables.GroupedObservable;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@SuppressWarnings({"squid:S125", "squid:S1144", "squid:S1192"})
public class DataConversionOperatorExample2 {

  // 데이터 변환 연산자 2
  public static void main(String[] args) {
//    groupBy();
//    toList();
//    toMap();
//    quiz();
  }

  // groupBy
  // 하나의 Observable을 여러개의 새로운 GroupedByObservable로 만듬
  // 원본 Observable의 데이터를 그룹별로 묶는다기보다는 각각의 데이터들이 그룹에 해당하는 Key를 가지게 됨
  // GroupedByOservable은 getKey( )를 통해 구분된 그룹을 알 수 있게 해줌
  private static void groupBy() {
    Observable<GroupedObservable<CarMaker, Car>> observable =
        Observable.fromIterable(SampleData.carList).groupBy(Car::getCarMaker);

    Logger.log("---------------------------------------------------------------------");

    // Car 제조사 별로 그룹으로 묶어서 데이터를 통지
    observable.subscribe(
        groupedObservable -> groupedObservable.subscribe(
            car -> Logger.log(ON_NEXT,
                "Group: " + groupedObservable.getKey() + "\t Car name: " + car.getCarName())
        ));

    Logger.log("---------------------------------------------------------------------");

    // filter를 이용해 필터링한 Group의 데이터만 출력
    observable.subscribe(
        groupedObservable ->
            groupedObservable
                .filter(car -> Objects.equals(groupedObservable.getKey(), CarMaker.CHEVROLET))
                .subscribe(
                    car -> Logger.log(PRINT, "Group: "
                        + groupedObservable.getKey()
                        + "\t Car name: " + car.getCarName())
                )
    );
  }

  // toList
  // 통지 되는 데이터를 모두 List에 담아 통지
  // 원본 Observable 에서 완료 통지를 받는 즉시 리스트를 통지
  // 통지되는 데이터는 원본 데이터를 담은 리스트 하나이므로 Single로 반환
  private static void toList() {
    Single<List<Integer>> singleList = Observable.just(1, 3, 5, 7, 9).toList();
    singleList.subscribe(data -> Logger.log(ON_NEXT, data));

    Logger.log("---------------------------------------------------------------------");

    Observable.fromIterable(SampleData.carList).toList()
        .subscribe(carList -> Logger.log(ON_NEXT, carList));
  }

  // toMap
  // 통지 되는 데이터를 모두 Map에 담아 통지
  // 원본 Observable 에서 완료 통지를 받는 즉시 Map을 통지
  // 이미 사용중인 key(키)를 또 생성하면 기존에 있던 key(키)와 value(값)를 덮어씀
  // 통지되는 데이터는 원본 데이터를 담은 Map 하나이므로 Single로 반환
  private static void toMap() {
    Single<Map<String, String>> singleMap =
        Observable.just("a-Alpha", "b-Bravo", "c-Charlie", "e-Echo")
            .toMap(data -> data.split("-")[0]); // 반환값은 Map의 key가 된다.

    singleMap.subscribe(map -> Logger.log(ON_NEXT, map));

    Logger.log("---------------------------------------------------------------------");

    Single<Map<String, String>> single = Observable
        .just("a-Alpha", "b-Bravo", "c-Charlie", "e-Echo")
        .toMap(data -> data.split("-")[0], data -> data.split("-")[1]);

    single.subscribe(map -> Logger.log(ON_NEXT, map));
  }

  private static void quiz() {
    // toMap 을 이용하여 SampleData.carList 의 car 객체들을 carName을 key로, carMaker를 value로 가지는 Map으로 출력
    final Single<Map<String, CarMaker>> carSingleMap = Observable.fromIterable(SampleData.carList)
        .toMap(Car::getCarName, Car::getCarMaker);

    carSingleMap.subscribe(map -> Logger.log(ON_NEXT, map));
  }

}
