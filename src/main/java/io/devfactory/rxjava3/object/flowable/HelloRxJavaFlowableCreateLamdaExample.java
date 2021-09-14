package io.devfactory.rxjava3.object.flowable;

import static io.devfactory.rxjava3.utils.LogType.ON_ERROR;
import static io.devfactory.rxjava3.utils.LogType.ON_NEXT;

import io.devfactory.rxjava3.utils.LogType;
import io.devfactory.rxjava3.utils.Logger;
import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class HelloRxJavaFlowableCreateLamdaExample {

  public static void main(String[] args) throws InterruptedException {
    Flowable<String> flowable =
        Flowable.create(emitter -> {
          String[] datas = {"Hello", "RxJava!"};

          for (String data : datas) {
            // 구독이 해지되면 처리 중단
            if (emitter.isCancelled()) {
              return;
            }

            // 데이터 발행
            emitter.onNext(data);
          }

          // 데이터 발행 완료를 알린다
          emitter.onComplete();
        }, BackpressureStrategy.BUFFER);

    flowable.observeOn(Schedulers.computation())
        .doOnSubscribe(subscription -> subscription.request(Long.MAX_VALUE))
        .subscribe(data -> Logger.log(ON_NEXT, data),
            error -> Logger.log(ON_ERROR, error),
            () -> Logger.log(LogType.ON_COMPLETE));

    Thread.sleep(500L);
  }

}
