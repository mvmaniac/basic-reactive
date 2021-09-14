package io.devfactory.rxjava3.utils;

import lombok.experimental.UtilityClass;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@UtilityClass
public class TimeUtil {

  public static long start;
  public static long end;

  static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

  public static long start() {
    start = System.currentTimeMillis();
    return start;
  }

  public static void end() {
    end = System.currentTimeMillis();
  }

  public static void takeTime() {
    System.out.println("# 실행시간: " + (end - start) + " ms");
  }

  public static String getCurrentTimeFormatted() {
    return LocalTime.now().format(formatter);
  }

  public static long getCurrentTime() {
    return System.currentTimeMillis();
  }

  public static void sleep(long interval) {
    try {
      Thread.sleep(interval);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

}
