package io.devfactory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

// WebFlux
// 단일 스레드, 비동기
// Stream을 통해 백프레셔가 적용된 데이터만큼 간헐적 응답 가능 + 데이터 소비가 끝나면 종료

// SSE (Server-Sent Events)
// 웹 소켓은 클라이언트와 서버 사이의 양방향 통신을 하지만 SSE는 서버에서 클라이언트로 데이터를 보내는 단방향 통신
// 데이터 소비가 끝나도 Stream 계속 유지 Servlet와 WebFlux 둘 다 적용 가능함,
// 다만 Servlet에서는 멀티 스레드 방식으로 동작함, WebFlux에서는 단일스레드, 비동기로 동작?
@ServletComponentScan
@SpringBootApplication
public class BasicReactiveApplication {

  public static void main(String[] args) {
    SpringApplication.run(BasicReactiveApplication.class, args);
  }

}
