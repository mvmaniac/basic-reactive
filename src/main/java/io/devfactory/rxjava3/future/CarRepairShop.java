package io.devfactory.rxjava3.future;

import io.devfactory.rxjava3.utils.LogType;
import io.devfactory.rxjava3.utils.Logger;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public class CarRepairShop {

  public int getCarRepairCostSync(int brokers) {
    return calculateCarRepair(brokers);
  }

  public Future<Integer> getCarRepairCostAsync(int brokers) {
    return CompletableFuture.supplyAsync(() -> calculateCarRepair(brokers));
  }

  private int calculateCarRepair(int brokers) {
    Logger.log(LogType.PRINT, "# 차량 수리비 계산 중................");
    delay();
    return brokers * 20000;
  }

  private void delay() {
    try {
      Thread.sleep(5000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

}
