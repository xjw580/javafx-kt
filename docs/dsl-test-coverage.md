# DSL 测试组织与覆盖

运行全部测试：`mvn test`。JavaFX 测试需要可用的图形环境；不跳过工具包初始化失败。

## 原生构建对照

预期对象由 JavaFX 构造器、setter、属性绑定或事件 API 独立构建，再与 DSL 对象比较。比较配置结果、节点结构以及属性变化后的行为，不比较不同对象的身份。DSL 的便捷默认值使用等价的原生配置作为对照。

| 测试类 | 覆盖内容 |
| --- | --- |
| `NodeDSLEquivalenceTest` | Node/Parent/Region/Control/Labeled 公共属性；按钮、文本输入、列表、表格、选择框、进度条、滑块、分页、图形、图像和自定义控件；事件与绑定变化 |
| `PaneDSLEquivalenceTest` | Region、Pane 与各容器；嵌套子节点顺序、布局属性、实际布局尺寸、Grid 行列、已有实例配置 |
| `LayoutConstraintsDSLTest` | Anchor/Grid/Box 约束、快捷方法、添加节点时设置约束及返回身份 |
| `ApplicationDSLTest` | Scene/Stage 构建、窗口属性与事件处理器、已有实例配置、缺少根节点 |
| `FileChooserDSLTest` | 文件与目录选择器、过滤器顺序与选择、初始目录检查、已有实例配置 |
| `MenuDSLTest` | 菜单项、单选菜单项、嵌套菜单、上下文菜单、事件、监听器移除、延迟构建 |
| `TransitionDSLTest` | 8 种动画、组合顺序、公共属性、完成回调、便捷预设和配置覆盖 |
| `EventHandlerDSLTest` | 实际鼠标/键盘事件分发、快捷键、捕获与冒泡、消费事件、拖拽开始 |
| `FxStateTest` | 单源/多源文本观察、依赖更新、解绑和重新绑定 |
| `DslBuilderTest` | 即时与延迟构建、重复构建、已有实例配置、clear、条件配置 |
| `StyleDSLTest` / `LabelSeparatorDSLTest` | 保留既有回归测试，并补充原生 CSS／原生控件对照 |

控件和容器主要使用 `BuildMode` 参数化测试，分别报告即时与延迟模式。其他构建器在对应测试中检查两种模式，并覆盖代表性的 `config` 路径；不宣称覆盖每个重载或每种组合。

## 既有回归测试

- `BuilderHierarchyTest`：继承层级调整后的类型、重载、默认行为及兼容性。
- `BindingParameterTypeTest` / `NodeBindingTest`：属性类型兼容、绑定方向和传播。
- `ParentDSLTest`：样式表生命周期、布局状态绑定与监听器移除。
- `I18nDSLTest`：语言切换、编辑文本归属和本地化构建入口。
- `StyleDSLTest`：CSS 级联、伪类状态、样式表归属和资源更新。
- `LabelSeparatorDSLTest`：图形独立性、文本绑定及原生皮肤渲染。

这些断言保留在原功能测试中，不与构建对照用例重复搬迁。`I18nVisualQaTest` 继续生成语言切换截图，输出位于 `target/visual-qa/`；窗口在 `finally` 中关闭。

## 公共测试设施与边界

`club.xiaojiawei.kt.test.onFxThread` 统一启动并复用 JavaFX 工具包、调度线程、超时与原始异常传播。原有 DSL、任务视图和集合测试共用此工具，不再维护各自的启动逻辑。`FxTestSupportTest` 检查重入及失败后的可继续执行性。

`Examples.kt` 和 `StylePseudoClassQa.kt` 是手动运行入口，不计作自动单元测试。`DialogDSL.kt` 当前为注释草稿，没有编译后的 API。测试不会打开系统文件选择对话框、调用 `Application.launch` 或用等待动画播放来断言时间；真实拖放会话和动画播放时序不在本次单元测试范围内。
