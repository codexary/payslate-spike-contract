[English](README.md) | 简体中文

# 排班服务契约原型

Payslate 需要接入第三方排班 API，但团队无法控制该服务。本原型使用 Pact JVM 4.7.5，在产品集成之前明确 Payslate 所需的接口响应。

`RosterConsumerPactTest` 让 `RosterClient` 请求 Pact 的模拟服务器，并生成 `pacts/Payslate-RosterProvider.json`。契约约定请求为 `GET /timesheets?date=2026-02-23`，响应状态为 `200`，响应头为 `Content-Type: application/json`，响应体的 `timesheets` 数组中包含带有 `employee`、`date`、`start`、`end` 四个字段的条目。契约直接使用具体示例值，没有配置匹配规则。

使用 Java 21 运行：

```sh
./mvnw -B -ntp verify
```

消费者侧的一个测试应当通过，并重新生成已提交的 pact 文件。`RosterClient` 只检查状态码和响应体中是否出现 `"employee"`，随后返回原始响应体；它只是最小适配器，还不是完整的 JSON 解析实现。该测试用于表达 Payslate 的需求，不能据此认定线上排班 API 已满足契约。

## 关于本仓库

Elsworne、Payslate 和团队成员均为虚构。

本仓库的代码与文档借助 AI 工具编写；记录的输出都来自实际运行。
