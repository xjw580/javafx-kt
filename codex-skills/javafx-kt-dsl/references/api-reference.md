# API 参考

以下只列本仓库里最常用、最适合让其他 AI 直接调用的入口。

## 应用与窗口

- `launchApp {}`: 启动 JavaFX 应用并配置主 `Stage`
- `StageBuilder`: 窗口标题、大小、位置、`scene {}`、事件回调
- `SceneBuilder`: 根节点、尺寸、`stylesheets`、`stylesheet {}`、`fill(...)`

## 容器工厂

- `pane {}` / `paneBuilder {}` / `Pane.config {}`: 基础容器
- `vbox {}` / `vboxBuilder {}` / `VBox.config {}`: 纵向布局
- `hbox {}` / `hboxBuilder {}` / `HBox.config {}`: 横向布局
- `stackPane {}` / `borderPane {}` / `gridPane {}` / `anchorPane {}` / `flowPane {}` / `tilePane {}` / `scrollPane {}` / `splitPane {}` / `titledPane {}`: 其他布局容器

## 节点与控件工厂

- `text {}`、`label {}`、`button {}`、`textField {}`、`textArea {}`、`checkBox {}`、`radioButton {}`、`comboBox {}`、`listView {}`、`tableView {}`、`progressBar {}`、`slider {}`、`imageView {}`、`separator {}`、`contextMenu {}`、`menuItem {}`、`radioMenuItem {}`
- 大部分控件同时提供 `xxxBuilder {}` 和 `xxx.config {}` 入口

## 布局与节点辅助

- `add(...)`、`addAll(...)`
- `+node`、`+"文本"`
- `padding(...)`、`size(...)`、`fixedSize(...)`
- `background(...)`、`border(...)`
- `hgrow(...)`、`vgrow(...)`
- `alignCenter()`、`alignLeft()`、`alignRight()` 等对齐辅助
- `setLeftAnchor(...)`、`setTopAnchor(...)`、`setHgrow(...)`、`setVgrow(...)`

## 事件

- `onClick {}`、`onHover {}`、`events {}`、`mouseHandlers {}`
- 鼠标/拖拽/滚轮类回调优先走节点 builder 里的事件方法

## 样式

- `styled {}`: 统一样式入口
- `styleMain()`、`styleNormal()`、`styleSuccess()`、`styleWarn()`、`styleError()`
- 常见样式项：`backgroundColor(...)`、`textFill(...)`、`fontSize(...)`、`fontWeight(...)`、`padding(...)`、`backgroundRadius(...)`、`cursor(...)`

## 绑定

- `bindings {}`: 普通属性绑定入口
- `stringBindings {}`、`numberBindings {}`、`booleanBindings {}`、`conditionalBindings {}`、`listBindings {}`
- 常见用法：
  - `a bind b`
  - `a bindBidirectional b`
  - `textProperty().bind(...)`
  - `observe(...)`、`observes(...)`

## 动画

- `fadeIn(...)`、`fadeOut(...)`
- `scaleAnimation {}`、`rotateAnimation {}`、`translateAnimation {}`、`parallelAnimation {}`、`sequentialAnimation {}`
- 常见补充动作：`shake()`、`pulse()`

## 工具函数

- `runUI {}`: 在 FX 线程执行
- `runUILater {}`: 延后到 FX 线程执行
- `removeSelfFromParent()`: 将节点从父容器移除

## 自定义控件

- 项目里有自定义/封装控件，例如 `Title`、`Switch`、`FilterComboBox`
- 这类控件优先按源码里的 builder/扩展函数方式调用，不要按标准 JavaFX 盲猜
