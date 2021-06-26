package io.devfactory.servlet;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import static org.springframework.http.MediaType.TEXT_HTML_VALUE;

@SuppressWarnings("squid:S2142")
@Slf4j
@WebServlet(name = "basicServlet", urlPatterns = "/servlet/basic")
public class BasicServlet extends HttpServlet {

  @Override
  protected void service(HttpServletRequest request,
      HttpServletResponse response) throws IOException {

    response.setContentType(TEXT_HTML_VALUE);
    response.setCharacterEncoding(StandardCharsets.UTF_8.name());

    log.debug("[dev] basic servlet call...");
    final var responseWriter = response.getWriter();

    // 일반 서블릿 총 5초 정도 기다린 후 출력 됨
    for (var i = 0; i < 5; i++) {
      responseWriter.println(String.format("응답: %d번<br/>", i + 1));
      responseWriter.flush();

      try {
        TimeUnit.SECONDS.sleep(1);
      } catch (InterruptedException e) {
        log.error("BasicServlet.service.InterruptedException: {}", e.getMessage(), e);
      }
    }
  }

}
