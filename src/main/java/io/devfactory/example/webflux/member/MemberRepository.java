package io.devfactory.example.webflux.member;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface MemberRepository extends ReactiveCrudRepository<Member, Long> {

  @Query("select * from tb_member where useranme = :username")
  Flux<Member> findByUsername(String username);

}

