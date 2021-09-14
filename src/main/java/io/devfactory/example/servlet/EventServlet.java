package io.devfactory.example.servlet;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import static org.springframework.http.MediaType.TEXT_EVENT_STREAM_VALUE;

@SuppressWarnings("squid:S2142")
@Slf4j
@WebServlet(name = "eventServlet", urlPatterns = "/example/servlet/event")
public class EventServlet extends HttpServlet {

  @Override
  protected void service(HttpServletRequest request,
      HttpServletResponse response) throws IOException {

    // text/event-stream 타입으로 지정하면 브라우저에서 SSE 프로토콜(?)로 인식함
    response.setContentType(TEXT_EVENT_STREAM_VALUE);
    response.setCharacterEncoding(StandardCharsets.UTF_8.name());

    log.debug("[dev] event servlet call...");
    final var responseWriter = response.getWriter();

    // 일반 서블릿과 다르게 text/event-stream 형식이기 때문에 바로바로 출력됨
    for (var i = 0; i < 5; i++) {
      responseWriter.println(String.format("응답: %d번", i + 1));
      responseWriter.flush();

      try {
        TimeUnit.SECONDS.sleep(1);
      } catch (InterruptedException e) {
        log.error("EventServlet.service.InterruptedException: {}", e.getMessage(), e);
      }
    }
  }

}
