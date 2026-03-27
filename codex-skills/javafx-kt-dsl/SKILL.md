---
name: javafx-kt-dsl
description: Kotlin JavaFX DSL 调用指南。用于在本仓库中编写、修改、解释 UI 代码时快速选择正确的 DSL 入口、构建器、绑定、事件、动画、样式和辅助扩展；适用于 stage/scene/root、容器与控件创建、属性绑定、线程切换、校验、菜单、对话框和文件选择器等场景。
---

# javafx-kt-dsl

优先按本仓库已有 DSL 调用，不要重新设计一套写法。

## 入口规则

- 优先使用顶层工厂函数：`launchApp {}`、`scene {}`、`vbox {}`、`hbox {}`、`button {}`、`label {}` 这类入口。
- 需要复用已有节点时，优先用 `xxx.config {}`，不要手写重复初始化代码。
- 需要嵌套子节点时，优先用 `addXXX{}`、`+node`、`+"text"` 这类已有约定。
- 不要假设 API 名称，先对照 `references/api-reference.md`。

## 调用偏好

- 容器构建优先用 DSL 容器函数，而不是直接 new JavaFX 类。
- 控件属性尽量放在对应 builder 里一次性配置。
- 绑定优先用 `bindings {}`、`stringBindings {}`、`numberBindings {}` 等入口。
- UI 线程相关逻辑优先用 `runUI {}`，仅在需要延后执行时用 `runUILater {}`。
- 所有Node要调用style()来加载默认的美化样式
- 需要样式时优先用 `styled {}` 和本库的样式辅助方法，而不是直接堆字符串。

## 常用模式

- 窗口：`launchApp { title(...); scene { root { ... } } }`
- 布局：`vbox { spacing(...); padding(...); add(...) }`
- 控件：`button("文本") { onClick { ... } }`
- 绑定：`bindings { a.textProperty() bind b.textProperty() }`
- 线程：`runUI { ... }`

## 易错点

- 不要把 JavaFX 原生属性赋值和 DSL builder 混写成两套互相覆盖的配置。
- 需要复用节点时，优先配置现有实例，不要重复创建同类型节点。
- 自定义控件与扩展函数以源码为准，未列出的名称不要臆造。

## 参考顺序

1. 先读 `references/api-reference.md`
2. 再读 `references/examples.md`
3. 如果还不确定，再回到源码确认函数签名
