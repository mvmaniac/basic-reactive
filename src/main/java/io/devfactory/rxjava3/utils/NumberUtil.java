package io.devfactory.rxjava3.utils;

import lombok.experimental.UtilityClass;

import java.util.Random;

@UtilityClass
public class NumberUtil {

  private final Random random = new Random();

  public static int randomRange(int min, int max) {
    return (int) (random.nextDouble() * (max - min + 1)) + min;
  }

}
