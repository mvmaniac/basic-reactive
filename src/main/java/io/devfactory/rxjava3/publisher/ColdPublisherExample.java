package io.devfactory.rxjava3.publisher;

import io.reactivex.rxjava3.core.Flowable;

@SuppressWarnings({"squid:S106","squid:S3457"})
public class ColdPublisherExample {

  public static void main(String[] args) {
    // 차가운 생산자
    // 생산자는 소비자가 구독할때 마다 데이터를 처음부터 새로 통지
    // 데이터를 통지하는 새로운 타임 라인이 생성됨
    // 소비자는 구독 시점과 상관없이 통지된 데이터를 처음부터 전달 받음
    final Flowable<Integer> flowable = Flowable.just(1, 3, 5, 6, 7);

    flowable.subscribe(data -> System.out.printf("구독자 1: %s\n", data));
    flowable.subscribe(data -> System.out.printf("구독자 2: %s\n", data));
  }

}
