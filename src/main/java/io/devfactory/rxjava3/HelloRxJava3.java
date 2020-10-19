package io.devfactory.rxjava3;

import io.reactivex.rxjava3.core.Observable;

@SuppressWarnings("squid:S106")
public class HelloRxJava3 {

  public static void main(String[] args) {
    final Observable<String> observable = Observable.just("Hello", "RxJava3");
    observable.subscribe(System.out::println);
  }

}
