package io.devfactory.rxjava3.object.flowable;

import static io.devfactory.rxjava3.utils.LogType.ON_COMPLETE;
import static io.devfactory.rxjava3.utils.LogType.ON_ERROR;
import static io.devfactory.rxjava3.utils.LogType.ON_NEXT;

import io.devfactory.rxjava3.utils.Logger;
import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.FlowableEmitter;
import io.reactivex.rxjava3.core.FlowableOnSubscribe;
import io.reactivex.rxjava3.schedulers.Schedulers;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public class HelloRxJavaFlowableCreateExample {

  public static void main(String[] args) throws InterruptedException {
    Flowable<String> flowable =
        Flowable.create(new FlowableOnSubscribe<String>() {
          @Override
          public void subscribe(FlowableEmitter<String> emitter) {
            String[] datas = {"Hello", "RxJava!"};

            for (String data : datas) {
              // 구독이 해지되면 처리 중단
              if (emitter.isCancelled()) {
                return;
              }

              // 데이터 통지
              emitter.onNext(data);
            }

            // 데이터 통지 완료를 알린다
            emitter.onComplete();
          }
        }, BackpressureStrategy.BUFFER); // 구독자의 처리가 늦을 경우 데이터를 버퍼에 담아두는 설정.

    flowable.observeOn(Schedulers.computation())
        .subscribe(new Subscriber<>() {

          // 데이터 개수 요청 및 구독을 취소하기 위한 Subscription 객체
          private Subscription subscription;

          @Override
          public void onSubscribe(Subscription subscription) {
            this.subscription = subscription;
            this.subscription.request(Long.MAX_VALUE);
          }

          @Override
          public void onNext(String data) {
            Logger.log(ON_NEXT, data);
          }

          @Override
          public void onError(Throwable error) {
            Logger.log(ON_ERROR, error);
          }

          @Override
          public void onComplete() {
            Logger.log(ON_COMPLETE);
          }

        });

    Thread.sleep(500L);
  }

}
