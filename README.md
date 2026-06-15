English | [简体中文](README.zh-CN.md)

# Rostering contract prototype

Payslate consumes a third-party rostering API that the team does not control. This prototype uses Pact JVM 4.7.5 to pin the response Payslate needs before product integration.

`RosterConsumerPactTest` exercises `RosterClient` against Pact's mock server and writes `pacts/Payslate-RosterProvider.json`. It defines `GET /timesheets?date=2026-02-23`, status `200`, `Content-Type: application/json`, and a `timesheets` array containing an entry with `employee`, `date`, `start` and `end`. The pact uses concrete example values without matching rules.

Run with Java 21:

```sh
./mvnw -B -ntp verify
```

The consumer test passes with one test and regenerates the committed pact. `RosterClient` returns the response body after checking the status and the presence of the `"employee"` token; it is a minimal adapter, not a complete JSON parser. This consumer test defines Payslate's expectations; it does not establish compatibility with the live rostering API.

## About this repository

Elsworne, Payslate and the team are fictional.

The code and documents in this repository were written with the help of AI tools; every recorded output comes from actually running them.
