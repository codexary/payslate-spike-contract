package com.elsworne.payslate.spike.contract;

import static org.junit.jupiter.api.Assertions.assertTrue;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;
import au.com.dius.pact.consumer.junit5.PactConsumerTest;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import org.junit.jupiter.api.Test;

@PactConsumerTest
@PactTestFor(providerName = "RosterProvider", pactVersion = au.com.dius.pact.core.model.PactSpecVersion.V3)
class RosterConsumerPactTest {
  @Pact(consumer = "Payslate", provider = "RosterProvider")
  RequestResponsePact roster(PactDslWithProvider builder) {
    return builder.given("a roster export exists")
        .uponReceiving("timesheets for a date")
        .path("/timesheets").query("date=2026-02-23").method("GET")
        .willRespondWith().status(200).headers(java.util.Map.of("Content-Type", "application/json"))
        .body("{\"timesheets\":[{\"employee\":\"Alice Test\",\"date\":\"2026-02-23\",\"start\":\"09:00\",\"end\":\"17:00\"}]}")
        .toPact();
  }

  @Test void consumesRosterExport(MockServer server) throws Exception {
    String body = new RosterClient(java.net.URI.create(server.getUrl())).fetchTimesheets("2026-02-23");
    assertTrue(body.contains("Alice Test"));
  }
}
