package io.devfactory.example.webflux;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import static java.time.Duration.ofSeconds;
import static org.springframework.http.MediaType.TEXT_EVENT_STREAM_VALUE;

@RequestMapping("/example/web-flux")
@RestController
public class WebFluxApi {

  @GetMapping("/flux")
  public Flux<Integer> flux() {
    // just: 순차적으로 하나 씩 onNext를 함, 데이터를 모아놓았다가 한번에 반환 됨
    return Flux.just(1, 2, 3, 4, 5).delayElements(ofSeconds(1)).log();
  }

  @GetMapping(value = "/flux-event", produces = TEXT_EVENT_STREAM_VALUE)
  public Flux<Integer> fluxStream() {
    return Flux.just(1, 2, 3, 4, 5).delayElements(ofSeconds(1)).log();
  }

}
