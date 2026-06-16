package com.elsworne.payslate.spike.contract;

import au.com.dius.pact.provider.junit5.HttpTestTarget;
import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.loader.PactFolder;
import au.com.dius.pact.provider.junitsupport.State;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

@Provider("RosterProvider")
@PactFolder("pacts")
class StubProviderVerificationTest {
  static HttpServer server;

  @BeforeAll static void start() throws IOException {
    server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
    server.createContext("/timesheets", exchange -> {
      byte[] body = "{\"timesheets\":[{\"employee\":\"Alice Test\",\"date\":\"2026-02-23\",\"start\":\"09:00\",\"end\":\"17:00\"}]}".getBytes(StandardCharsets.UTF_8);
      exchange.getResponseHeaders().add("Content-Type", "application/json");
      exchange.sendResponseHeaders(200, body.length);
      try (var output = exchange.getResponseBody()) { output.write(body); }
    });
    server.start();
  }

  @AfterAll static void stop() { server.stop(0); }
  @State("a roster export exists") void exportExists() {}
  @BeforeEach void target(PactVerificationContext context) {
    context.setTarget(new HttpTestTarget("127.0.0.1", server.getAddress().getPort()));
  }
  @TestTemplate @ExtendWith(PactVerificationInvocationContextProvider.class)
  void verify(PactVerificationContext context) { context.verifyInteraction(); }
}
