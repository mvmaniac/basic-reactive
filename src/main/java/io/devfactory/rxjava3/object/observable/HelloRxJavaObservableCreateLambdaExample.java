package io.devfactory.rxjava3.object.observable;

import io.devfactory.utils.LogType;
import io.devfactory.utils.Logger;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class HelloRxJavaObservableCreateLambdaExample {

  public static void main(String[] args) throws InterruptedException {
    Observable<String> observable =
        Observable.create(emitter -> {
          String[] datas = {"Hello", "RxJava!"};

          for (String data : datas) {
            if (emitter.isDisposed()) {
              return;
            }

            emitter.onNext(data);
          }

          emitter.onComplete();
        });

    observable.observeOn(Schedulers.computation())
        .doOnSubscribe(disposable -> {/** nothing */})
        .subscribe(
            data -> Logger.log(LogType.ON_NEXT, data),
            error -> Logger.log(LogType.ON_ERROR, error),
            () -> Logger.log(LogType.ON_COMPLETE)
        );

    Thread.sleep(500L);
  }
}
