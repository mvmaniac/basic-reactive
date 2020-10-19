package io.devfactory.rxjava3.object.maybe;

import static io.devfactory.utils.LogType.ON_COMPLETE;
import static io.devfactory.utils.LogType.ON_ERROR;
import static io.devfactory.utils.LogType.ON_SUCCESS;

import io.devfactory.utils.Logger;
import io.reactivex.rxjava3.core.Maybe;

public class MaybeJustExample {

  public static void main(String[] args) {
//    Maybe.just(DateUtil.getNowDate())
//        .subscribe(
//            data -> Logger.log(ON_SUCCESS, "# 현재 날짜시각: " + data),
//            error -> Logger.log(ON_ERROR, error),
//            () -> Logger.log(ON_COMPLETE)
//        );

    Maybe.empty()
        .subscribe(
            data -> Logger.log(ON_SUCCESS, data),
            error -> Logger.log(ON_ERROR, error),
            () -> Logger.log(ON_COMPLETE)
        );
  }

}
