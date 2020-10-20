package io.devfactory.rxjava3.common;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Car {

  private CarMaker carMaker;
  private CarType carType;
  private String carName;
  private int carPrice;
  private boolean isNew;

  public Car(String carName) {
    this.carName = carName;
  }

  public Car(String carName, CarType carType) {
    this.carName = carName;
    this.carType = carType;
  }

  public Car(CarMaker carMaker, String carName, CarType carType, int carPrice) {
    this.carMaker = carMaker;
    this.carName = carName;
    this.carType = carType;
    this.carPrice = carPrice;
  }

  public Car(CarMaker carMaker, CarType carType, String carName, int carPrice, boolean isNew) {
    this.carMaker = carMaker;
    this.carType = carType;
    this.carName = carName;
    this.carPrice = carPrice;
    this.isNew = isNew;
  }

}
