# 示例

## 1. 启动窗口

```kotlin
launchApp {
    title("Demo")
    size(800.0, 600.0)
    scene {
        root {
            vbox {
                spacing(12.0)
                padding(16.0)
                add(label("Hello"))
                add(button("Click") {
                    onClick {
                        println("clicked")
                    }
                })
            }
        }
    }
}
```

## 2. 复用已有节点

```kotlin
val view = vbox {
    spacing(8.0)
}

view.config {
    padding(20.0)
    add(textField {
        promptText("输入内容")
    })
}
```

## 3. 属性绑定

```kotlin
bindings {
    label1.textProperty() bind textField1.textProperty()
    progressBar1.progressProperty() bind slider1.valueProperty().divide(100.0)
}
```

## 4. 线程切换

```kotlin
runUI {
    label1.text = "updated"
}
```

## 5. 事件与样式

```kotlin
button("提交") {
    styled {
        backgroundColor("#3498db")
        textFill("white")
        fontSize(16.0)
    }

    onClick {
        println("submit")
    }
}
```

## 6. 容器快捷写法

```kotlin
hbox {
    spacing(10.0)
    +label("A")
    +button("B")
}
```
