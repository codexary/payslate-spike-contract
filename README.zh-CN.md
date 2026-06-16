[English](README.md) | 简体中文

# 排班服务契约原型

Payslate 需要接入第三方排班 API，但团队无法控制该服务。本原型使用 Pact JVM 4.7.5，在产品集成之前明确 Payslate 所需的接口响应。

`RosterConsumerPactTest` 让 `RosterClient` 请求 Pact 的模拟服务器，并生成 `pacts/Payslate-RosterProvider.json`。契约约定请求为 `GET /timesheets?date=2026-02-23`，响应状态为 `200`，响应头为 `Content-Type: application/json`，响应体的 `timesheets` 数组中包含带有 `employee`、`date`、`start`、`end` 四个字段的条目。契约直接使用具体示例值，没有配置匹配规则。

`StubProviderVerificationTest` 读取已提交的 pact，用本地 HTTP 桩服务验证它。桩服务监听临时分配的端口，由 Payslate 自己编写，只返回固定的示例响应；第三方服务没有参与验证。

使用 Java 21 运行这两项测试：

```sh
./mvnw -B -ntp verify
```

运行后应有两项测试通过：消费者测试重新生成已提交的 pact 文件，另一项测试验证本地桩服务。`RosterClient` 只检查状态码和响应体中是否出现 `"employee"`，随后返回原始响应体；它只是最小适配器，还不是完整的 JSON 解析实现。该测试用于表达 Payslate 的需求，不能据此认定线上排班 API 已满足契约。

桩服务通过验证，只能说明我们写的示例响应符合我们定义的契约。只有针对第三方服务的某个发布版本验证同一份 pact，才能声称该版本与 Payslate 兼容。

## 关于本仓库

Elsworne、Payslate 和团队成员均为虚构。

本仓库的代码与文档借助 AI 工具编写；记录的输出都来自实际运行。
