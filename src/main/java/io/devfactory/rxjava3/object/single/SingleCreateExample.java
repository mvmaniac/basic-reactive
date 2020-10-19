package io.devfactory.rxjava3.object.single;

import static io.devfactory.utils.LogType.ON_ERROR;
import static io.devfactory.utils.LogType.ON_SUCCESS;

import io.devfactory.utils.DateUtil;
import io.devfactory.utils.Logger;
import io.reactivex.rxjava3.core.Single;

public class SingleCreateExample {

  public static void main(String[] args) {
    // 데이터를 1건만 통지하거나 에러를 통지
    // 데이터 통지 자체가 완료를 의미하기 때문에 완료 통지를 하지 않음
    // 데이터를 1건만 통지하므로 데이터 개수를 요청할 필요가 없음
    final Single<String> single = Single
        .create(emitter -> emitter.onSuccess(DateUtil.getNowDate()));

    // no lambda
    /*
    single.subscribe(new SingleObserver<>() {
      @Override
      public void onSubscribe(@NonNull Disposable d) {
        // nothing
      }

      @Override
      public void onSuccess(@NonNull String data) {
        Logger.log(ON_SUCCESS, "# 날짜시각: " + data);
      }

      @Override
      public void onError(@NonNull Throwable error) {
        Logger.log(ON_ERROR, error);
      }
    });
    */

    single.subscribe(
        data -> Logger.log(ON_SUCCESS, "# 날짜시각: " + data),
        error -> Logger.log(ON_ERROR, error)
    );

  }

}
