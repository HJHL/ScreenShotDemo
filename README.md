# ScreenShot Event Monitor Demo

[中文版本](README_zh.md)

Monitor screen shot event and notify to observers when screen shot is taken.

## Usage

```kotlin
// 1. create a instance of [ScreenShotMonitor]
val screenShotMonitor = ScreenShotMonitor(context)

// 2.
// start observe screen shot event
screenShotMonitor.startObserve()
// stop observe screen shot event
screenShotMonitor.stopObserve()

// 3. register listener
screenShotMonitor.registerListener(l)
```

## Principle

Observe image media database change by `ContentResolver`, and check if the latest added image is a
screen shot image, by check the file's path whether contains **screen shot** related keyword.

## Compatibility Test

| Device | API Level | Result |
| ------- | ---------- | ------ |
| RedMi K40 Pro | 31 | Passed |
| XiaoMi Mi 8 | 29 | Passed |
| XiaoMi Mi 4 | 23 | Passed |
| Oppo R11s | 28 | Passed |

## License

[WTFPL](http://www.wtfpl.net/)
![WTFPL](http://www.wtfpl.net/wp-content/uploads/2012/12/wtfpl-badge-4.png)