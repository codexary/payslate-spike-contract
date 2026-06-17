English | [简体中文](FINDINGS.zh-CN.md)

# Rostering contract findings

Payslate is the consumer of a third-party rostering API that the team does not control. This prototype records the contract Payslate needs before product integration, using Pact JVM 4.7.5 and Java 21.

## What the consumer test pins

`RosterConsumerPactTest` calls `RosterClient` against Pact's mock server. For the provider state `a roster export exists`, it defines one interaction:

- Request: `GET /timesheets?date=2026-02-23`.
- Response status: `200`.
- Response header: `Content-Type: application/json`.
- Response body: a `timesheets` array containing one entry with `employee: "Alice Test"`, `date: "2026-02-23"`, `start: "09:00"` and `end: "17:00"`.

The pact uses these concrete example values, without matching rules; it is not a general schema for all rosters. The test checks that the returned body contains `Alice Test`, while the minimal client checks the status and the presence of the `"employee"` token. The client does not parse or validate all four fields itself.

The test writes `pacts/Payslate-RosterProvider.json`. After the consumer-only commit, `./mvnw -B -ntp verify` reported one passing test. Running it again regenerated the same committed pact and left the working tree clean.

## Passing local verification

`StubProviderVerificationTest` reads the committed pact and sends its request to a local HTTP stub on an ephemeral port. The stub returns a fixed response. With the stub included, `./mvnw -B -ntp verify` exited with code 0: one consumer test and one provider verification, with no failures, errors or skipped tests.

```text
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

## Renaming `employee` to `worker`

Only the response field name in `StubProviderVerificationTest` was temporarily changed from `employee` to `worker`. Its value, the other fields, the consumer test and the committed pact stayed unchanged. Running `./mvnw -B -ntp verify` exited with code 1. The consumer test still passed; the stub verification failed. These are the exact diagnostic lines:

```text
      has a matching body (FAILED)

Failures:

1) Verifying a pact between Payslate and RosterProvider - timesheets for a date has a matching body

    1.1) body: $.timesheets[0] Actual map is missing the following keys: employee
```

The build summary was:

```text
[ERROR] Tests run: 2, Failures: 1, Errors: 0, Skipped: 0
[INFO] BUILD FAILURE
```

The field rename was then reverted. Verification passed again with two tests, and the working tree was clean. The failure demonstrates that this pact detects the removal of the required `employee` field from the local stub response.

## Limit of this result

The stub is Payslate's own implementation, not the third-party provider. Passing local verification says nothing about whether the live provider satisfies the contract. The same pact must be verified against an actual provider release before anyone claims compatibility with that release; this prototype has not performed that verification.
