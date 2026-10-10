# DSL 继承层级调整

## 公共能力

本次补齐的构建器层级如下。具体控件的工厂函数、构建结果类型及即时／延迟构建模式保持不变。

```text
NodeBuilder
├── ShapeBuilder → CircleBuilder / TextBuilder / PolygonBuilder
└── ParentBaseBuilder
    └── RegionBaseBuilder
        └── ControlBuilder
            ├── TextInputControlBuilder → TextFieldBuilder / TextAreaBuilder
            ├── LabeledBuilder
            │   └── ButtonBaseBuilder
            │       ├── ButtonBuilder / CheckBoxBuilder
            │       └── ToggleButtonBaseBuilder → RadioButtonBuilder
            └── ComboBoxBaseBuilder
                └── ComboBoxControlBuilder → ComboBoxBuilder / FilterComboBoxBuilder
```

`ScrollPaneBuilder`、`SplitPaneBuilder`、`ListViewBuilder`、`TableViewBuilder`、`ProgressBarBuilder`、`SliderBuilder`、`SeparatorBuilder`、`PaginationBuilder` 也接入 `ControlBuilder`，可复用 `toolTip`、`contextMenu`、节点绑定和 Parent 样式表配置。

`ShapeBuilder` 统一提供填充、描边和抗锯齿配置；`CircleBuilder.fill(color = ...)` 保留。`ButtonBaseBuilder` 提供动作事件及 armed 状态的反向绑定、监听；`ToggleButtonBaseBuilder` 提供选中状态、分组和选中监听。

文本输入框的 `text(LocalizedText)` 仍只设置当前语言的文本，不绑定用户输入；`promptText(LocalizedText)` 仍跟随语言切换。按钮保留带事件参数的回调，复选框与单选按钮保留无参数回调，避免引入两个函数类型重载的歧义。

## 自定义 ComboBox 构建器迁移

旧 `ComboBoxBaseBuilder<S : ComboBox<T>, T>` 改名为 `ComboBoxControlBuilder<S : ComboBox<T>, T>`。如果自定义构建器需要 `items`、`converter`、`visibleRowCount` 或 `combo-box-ui` 样式，将父类改为：

```kotlin
class CustomComboBoxBuilder<T> : ComboBoxControlBuilder<CustomComboBox<T>, T>() {
    override fun buildInstance(): CustomComboBox<T> = CustomComboBox()
}
```

这里的 `CustomComboBox<T>` 指项目已有的 `ComboBox<T>` 子类。原先以旧基类作为参数或配置接收者、并调用上述专属方法的代码，也需改用 `ComboBoxControlBuilder`。

新的 `ComboBoxBaseBuilder<S : ComboBoxBase<T>, T>` 仅提供 JavaFX `ComboBoxBase` 的公共配置，如 `value`、`promptText`、`editable`、`onAction` 和值绑定／监听，可供 `DatePicker`、`ColorPicker` 等控件复用：

```kotlin
class DatePickerBuilder : ComboBoxBaseBuilder<DatePicker, LocalDate>() {
    override fun buildInstance(): DatePicker = DatePicker()
}
```

示例类型分别导入 `javafx.scene.control.DatePicker`、`java.time.LocalDate` 及对应 DSL 基类。通用基类不添加 `combo-box-ui` 样式，具体 `ComboBoxBuilder` 与 `FilterComboBoxBuilder` 仍保留原有主题样式。

现有 `comboBox { ... }`、`comboBox(items) { ... }`、`addComboBox { ... }`、`addFilterComboBox { ... }` 和 `.config { ... }` 用法不变。更新库后应重新编译调用方；本次不承诺旧版已编译自定义构建器的二进制兼容性。

## ScrollPane 的样式配置

`ScrollPaneBuilder` 移除重复的 `styled` 实现，继承 `NodeBuilder.styled`。普通 `styled { ... }` 调用不变；如果使用显式命名参数，将 `styled(block = { ... })` 改为 `styled(config = { ... })`。

## Pane / Region 已有实例配置

已有实例使用 `configPane { ... }`、`configRegion { ... }`，返回原对象并保留其具体类型：

```kotlin
import club.xiaojiawei.kt.dsl.*
import javafx.scene.layout.Pane
import javafx.scene.layout.Region
import javafx.scene.layout.VBox

val region = Region().configRegion {
    size(200.0, 80.0)
    padding(8.0)
}
val pane = Pane().configPane {
    addLabel("内容")
}
val column: VBox = VBox()
    .configRegion { padding(8.0) }
    .configPane { addLabel("标题") }
    .config { spacing(12.0) }
```

使用不同名称是为了避免 Kotlin 扩展重载歧义：直接增加 `Region.config`、`Pane.config` 会与现有 `VBox.config`、`Slider.config` 等冲突。已有具体类型的 `config { ... }` 无需迁移。

`regionConfig { ... }`、`paneConfig { ... }` 仍用于保存可复用的配置块，分别传给 `configRegion`、`configPane`。配置在传入实例上按顺序执行，不清空未设置的属性或已有子节点。复用配置且希望每个父容器拥有独立子节点时，使用 `addLabel { ... }` 等构建入口或 `add { ... }` 工厂，避免复用同一个 JavaFX Node。

## 范围

`CheckBox` 不属于 `ToggleButton` 分支；`Switch` 实际继承 `StackPane`，未接入 `ControlBuilder`。本次未新增具体控件工厂，也未调整 Window、Animation、ProgressIndicator 或 TableColumnBase 分支。
