package io.devfactory.rxjava3.object.single;

import static io.devfactory.utils.LogType.ON_ERROR;
import static io.devfactory.utils.LogType.ON_SUCCESS;

import io.devfactory.utils.DateUtil;
import io.devfactory.utils.Logger;
import io.reactivex.rxjava3.core.Single;

public class SingleJustExample {

  public static void main(String[] args) {
    Single.just(DateUtil.getNowDate())
        .subscribe(
            data -> Logger.log(ON_SUCCESS, "# 날짜시각: " + data),
            error -> Logger.log(ON_ERROR, error)
        );
  }

}
