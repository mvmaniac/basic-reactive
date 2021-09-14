package io.devfactory.example.webflux.member;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

import static lombok.AccessLevel.PROTECTED;

@Getter
@Table("tb_member")
public class Member {

  @Id
  private Long id;

  private final String email;

  private final String username;

  private final String password;

  private final LocalDateTime createdDate;

  @Builder(access = PROTECTED)
  private Member(String email, String username, String password, LocalDateTime createdDate) {
    this.email = email;
    this.username = username;
    this.password = password;
    this.createdDate = createdDate;
  }

  public static Member of(String email, String username, String password) {
    return new Member(email, username, password, LocalDateTime.now());
  }

}
