[English](FINDINGS.md) | 简体中文

# 排班服务契约验证结果

Payslate 要接入的排班 API 由第三方提供，团队无法控制它。本原型使用 Java 21 和 Pact JVM 4.7.5，在产品集成前把 Payslate 的接口需求落实为可执行的契约。

## 消费者测试约定了什么

`RosterConsumerPactTest` 通过 `RosterClient` 请求 Pact 的模拟服务器。在 `a roster export exists` 这一服务状态下，契约只定义了一次交互：

- 请求为 `GET /timesheets?date=2026-02-23`。
- 响应状态码为 `200`。
- 响应头包含 `Content-Type: application/json`。
- 响应体包含 `timesheets` 数组，数组中有一个条目，四个字段分别是 `employee: "Alice Test"`、`date: "2026-02-23"`、`start: "09:00"`、`end: "17:00"`。

这些都是具体的示例值，契约没有设置匹配规则，因此不能把它当成适用于所有排班数据的通用结构定义。测试断言返回内容包含 `Alice Test`；最小客户端只检查状态码和响应体中是否出现 `"employee"`，并没有自行解析、校验全部四个字段。

测试生成的文件是 `pacts/Payslate-RosterProvider.json`。仅包含消费者测试的第一次提交运行 `./mvnw -B -ntp verify`，结果为一项测试通过。提交后再次运行，生成的 pact 与已提交版本一致，工作区保持干净。

## 本地验证通过的结果

`StubProviderVerificationTest` 读取已提交的 pact，把契约中的请求发给监听临时端口的本地 HTTP 桩服务。桩服务返回固定的示例响应。加入它之后，运行 `./mvnw -B -ntp verify`，退出码为 0：消费者测试和桩服务验证各一项，均通过，没有失败、错误或跳过的测试。

```text
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

## 把 `employee` 改成 `worker` 的实验

实验只临时修改了 `StubProviderVerificationTest` 响应中的字段名：将 `employee` 改为 `worker`。字段值、其余字段、消费者测试以及已提交的 pact 都没有改动。随后运行 `./mvnw -B -ntp verify`，退出码为 1。消费者测试仍然通过，桩服务验证失败，诊断原文如下：

```text
      has a matching body (FAILED)

Failures:

1) Verifying a pact between Payslate and RosterProvider - timesheets for a date has a matching body

    1.1) body: $.timesheets[0] Actual map is missing the following keys: employee
```

构建汇总原文：

```text
[ERROR] Tests run: 2, Failures: 1, Errors: 0, Skipped: 0
[INFO] BUILD FAILURE
```

之后已把字段名恢复为 `employee`，再次运行验证，两项测试全部通过，工作区干净。这个失败案例说明：本地桩服务的响应缺少契约要求的 `employee` 字段时，验证能够发现问题。

## 结论的边界

桩服务是 Payslate 自己写的实现，不是第三方排班服务。它通过验证，不能说明线上服务是否符合契约。要声称某个第三方服务版本与 Payslate 兼容，必须先针对该发布版本验证同一份 pact；本原型尚未做这一步。
