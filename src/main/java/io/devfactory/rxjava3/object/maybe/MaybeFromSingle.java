package io.devfactory.rxjava3.object.maybe;

import static io.devfactory.utils.LogType.ON_COMPLETE;
import static io.devfactory.utils.LogType.ON_ERROR;
import static io.devfactory.utils.LogType.ON_SUCCESS;

import io.devfactory.utils.DateUtil;
import io.devfactory.utils.Logger;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;

public class MaybeFromSingle {

  public static void main(String[] args) {
    Single<String> single = Single.just(DateUtil.getNowDate());

    Maybe.fromSingle(single)
        .subscribe(
            data -> Logger.log(ON_SUCCESS, "# 현재 날짜시각: " + data),
            error -> Logger.log(ON_ERROR, error),
            () -> Logger.log(ON_COMPLETE)
        );
  }

}
