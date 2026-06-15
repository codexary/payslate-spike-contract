package com.elsworne.payslate.spike.contract;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/** Payslate's consumer-side adapter for a third-party roster export. */
public final class RosterClient {
  private final URI base;
  public RosterClient(URI base) { this.base = base; }
  public String fetchTimesheets(String date) throws IOException, InterruptedException {
    var request = HttpRequest.newBuilder(base.resolve("/timesheets?date=" + date)).GET().build();
    var response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
    if (response.statusCode() != 200 || !response.body().contains("\"employee\""))
      throw new IOException("Rostering response changed or failed");
    return response.body();
  }
}
