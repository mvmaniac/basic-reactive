package io.devfactory.rxjava3.publisher;

import io.reactivex.rxjava3.processors.PublishProcessor;

@SuppressWarnings({"squid:S106","squid:S3457"})
public class HotPublisherExample {

  public static void main(String[] args) {
    // 뜨거운 생산자
    // 생산자는 소비자 수와 상괎없이 데이터를 한번만 통지
    // 데이터를 통지하는 타임 라인은 하나
    // 소비자는 발행된 데이터를 처음부터 전달 받는게 아니라 구독한 시점에 통지된 데이터를만 전달 받음
    final PublishProcessor<Object> processor = PublishProcessor.create();

    processor.subscribe(data -> System.out.printf("구독자 1: %s\n", data));
    processor.onNext(1);
    processor.onNext(3);

    processor.subscribe(data -> System.out.printf("구독자 2: %s\n", data));
    processor.onNext(5);
    processor.onNext(7);

    processor.onComplete();
  }

}
