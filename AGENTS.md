# Repository Guidelines

## Project Structure & Module Organization
本仓库是基于 Maven 的 Kotlin/JavaFX 库项目。核心源码位于 `src/main/kotlin/club/xiaojiawei/kt`，按功能拆分为 `bean`、`dsl`、`controls`、`config`、`ext` 等包。测试代码位于 `src/test/kotlin`，资源目录为 `src/main/resources` 与 `src/test/resources`。构建产物输出到 `target/`，不应手动修改或提交。

## Build, Test, and Development Commands
常用命令：

```bash
mvn clean compile
mvn test
mvn clean package
mvn source:jar javadoc:jar
```

`mvn clean compile` 用于校验主源码能否编译；`mvn test` 运行 JUnit 5 与 Kotlin Test；`mvn clean package` 生成主产物；发布前可用 `mvn source:jar javadoc:jar` 检查源码包与文档包流程。

## Coding Style & Naming Conventions
遵循 Kotlin 官方风格，`pom.xml` 已声明 `kotlin.code.style=official`。统一使用 4 空格缩进，不使用 Tab。类、对象、接口使用 `PascalCase`，函数与属性使用 `camelCase`，常量使用 `UPPER_SNAKE_CASE`。DSL 扩展、Builder 与 JavaFX 绑定 API 应优先保持语义直观，例如 `bindText`、`observe`、`TaskBuilder`。包名保持全小写，新增代码优先放入已有功能包下。

## Testing Guidelines
当前测试框架为 `kotlin-test-junit5` 与 `junit-jupiter`。测试文件命名采用 `*Test.kt`，并与被测包路径保持对应，例如 `src/test/kotlin/club/xiaojiawei/kt/dsl/FxStateTest.kt`。新增公共 DSL、状态绑定或并发任务能力时，应补充正常路径与边界场景测试。提交前至少运行一次 `mvn test`。

## Commit & Pull Request Guidelines
提交历史以简洁前缀为主：`feat:`、`fix:`、`refactor:`，描述通常直接说明改动点，可使用中文；版本发布提交可直接使用版本号，如 `0.2.0`。建议每次提交只聚焦一个主题。PR 应包含改动目的、核心实现、测试结果；如果影响 DSL 用法或 UI 行为，补充示例代码或截图；若变更公开 API，请说明兼容性影响。

## Security & Configuration Tips
`pom.xml` 中包含私有仓库发布配置，勿在文档或截图中泄露内部地址、凭证或发布流程细节。新增依赖优先使用 Maven Central；若必须引入 JitPack 组件，需确认版本固定且来源可信。
