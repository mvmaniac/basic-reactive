package io.devfactory.rxjava3.object.completable;

import static io.devfactory.utils.LogType.ON_COMPLETE;
import static io.devfactory.utils.LogType.ON_ERROR;
import static io.devfactory.utils.LogType.PRINT;

import io.devfactory.utils.Logger;
import io.devfactory.utils.TimeUtil;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class CompletableCreateExample {

  public static void main(String[] args) {
    // 데이터를 통지하는것이 아니라 특정 작업을 수행한 후, 완료를 통지한다.
    Completable completable = Completable.create(emitter -> {
      int sum = 0;

      for (int i = 0; i < 100; i++) {
        sum += i;
      }

      Logger.log(PRINT, "# 합계: " + sum);
      emitter.onComplete();
    });

    // no lambda
    /*
    completable
        .subscribeOn(Schedulers.computation())
        .subscribe(new CompletableObserver() {
          @Override
          public void onSubscribe(Disposable disposable) {
            // nothing
          }

          @Override
          public void onComplete() {
            Logger.log(ON_COMPLETE);
          }

          @Override
          public void onError(Throwable error) {
            Logger.log(ON_ERROR, error);
          }
        });
     */

    completable
        .subscribeOn(Schedulers.computation())
        .subscribe(
            () -> Logger.log(ON_COMPLETE),
            error -> Logger.log(ON_ERROR, error)
        );

    TimeUtil.sleep(100L);
  }

}
