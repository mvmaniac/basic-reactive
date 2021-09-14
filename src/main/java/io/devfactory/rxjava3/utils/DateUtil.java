package io.devfactory.rxjava3.utils;

import lombok.experimental.UtilityClass;

import java.text.SimpleDateFormat;
import java.util.Calendar;

@UtilityClass
public class DateUtil {

  public static String getNowDate() {
    return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
  }

}
