package io.devfactory.rxjava3.object.maybe;

import static io.devfactory.utils.LogType.ON_COMPLETE;
import static io.devfactory.utils.LogType.ON_ERROR;
import static io.devfactory.utils.LogType.ON_SUCCESS;

import io.devfactory.utils.Logger;
import io.reactivex.rxjava3.core.Maybe;

public class MaybeCreateExample {

  public static void main(String[] args) {
    // 데이터를 1건만 통지하거나 1건도 통지하지 않고 완료 또는 에러를 통지
    // 데이터 통지 자체가 완료를 의미하기 때문에 완료 통지를 하지 않음
    // 단 데이터를 1건도 통지하지 않고 처리가 종료될 경우에는 완료 통지를 함
    final Maybe<String> maybe = Maybe
        .create(emitter -> {
          //emitter.onSuccess(DateUtil.getNowDate());
          emitter.onComplete();
        });

    // no lambda
    /*
    maybe.subscribe(new MaybeObserver<>() {
      @Override
      public void onSubscribe(Disposable disposable) {
        // nothing
      }

      @Override
      public void onSuccess(String data) {
        Logger.log(ON_SUCCESS, "# 현재 날짜시각: " + data);
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
    */

    maybe.subscribe(
        data -> Logger.log(ON_SUCCESS, "# 현재 날짜시각: " + data),
        error -> Logger.log(ON_ERROR, error),
        () -> Logger.log(ON_COMPLETE)
    );

  }

}
