# 截屏事件监听 Demo

[English Version](README.md)

监听截屏事件，当事件发生时通知监听者。

## 用法

```kotlin
// 1. 创建一个 [ScreenShotMonitor] 实例
val screenShotMonitor = ScreenShotMonitor(context)

// 2.
// 开启监听
screenShotMonitor.startObserve()
// 关闭监听
screenShotMonitor.stopObserve()

// 3. 注册监听器
screenShotMonitor.registerListener(l)
```

## 原理

通过 ContentResolver 监听图片多媒体数据库变化，通过判断最新变化的一个图片文件是否是截屏图片，来判断是否是截屏事件。

判断是否是截屏图片的方法是：判断图片文件的路径名，是否包含**截屏相关**的字符串。

## 兼容性测试

| 设备 | API 级别 | 结果 |
| ------- | ---------- | ------ |
| RedMi K40 Pro | 31 | 通过 |
| XiaoMi Mi 8 | 29 | 通过 |
| XiaoMi Mi 4 | 23 | 通过 |
| Oppo R11s | 28 | 通过 |

## 版权

[WTFPL](http://www.wtfpl.net/)
![WTFPL](http://www.wtfpl.net/wp-content/uploads/2012/12/wtfpl-badge-4.png)