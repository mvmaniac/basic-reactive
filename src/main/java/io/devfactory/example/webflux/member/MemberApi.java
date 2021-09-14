package io.devfactory.example.webflux.member;

import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import static java.time.Duration.ofSeconds;
import static org.springframework.http.MediaType.TEXT_EVENT_STREAM_VALUE;

@RequestMapping("/web-flux")
@RestController
public class MemberApi {

  private final MemberRepository memberRepository;
  private final Sinks.Many<Member> sinks;

  public MemberApi(MemberRepository memberRepository) {
    this.memberRepository = memberRepository;
    this.sinks = Sinks.many().multicast().onBackpressureBuffer();
  }

  @GetMapping("/members")
  public Flux<Member> retrieveMembers() {
    return memberRepository.findAll().log();
  }

  // TODO: 한글 인코딩....
  @GetMapping(value = "/members/stream", produces = TEXT_EVENT_STREAM_VALUE + "; charset=utf8")
  public Flux<Member> retrieveMembersWithStream() {
    return memberRepository.findAll().delayElements(ofSeconds(1)).log();
  }

  // TODO: 최초는 접속 시에는 잘되는데 새로 고침한 경우 처리?
  // produces 생략 가능
  @GetMapping(value = "/members/sinks")
  public Flux<ServerSentEvent<Member>> retrieveMembersWithSinks() {
    return sinks.asFlux().map(member -> ServerSentEvent.builder(member).build())
        .doOnCancel(() -> sinks.asFlux().blockLast());
  }

  @GetMapping("/members/{id}")
  public Mono<Member> retrieveMember(@PathVariable("id") Long id) {
    return memberRepository.findById(id).log();
  }

  @PostMapping("/members")
  public Mono<Member> createMember() {
    return memberRepository.save(Member.of("dev10@gmail.com", "개발자10", "1234"))
        .doOnNext(sinks::tryEmitNext);
  }

}
