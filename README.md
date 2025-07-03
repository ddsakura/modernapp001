# modern app 001

## Android Modern App Practice
### Github Action Test
### Edge to Edge
- [Android 的各種 Bar：從 ActionBar 到 CollapsingToolbarLayout](https://medium.com/evan-android-note/android-%E7%9A%84%E5%90%84%E7%A8%AEbar-%E5%BE%9Eactionbar%E5%88%B0collapsingtoolbarlayout-c95d33640be4)
- [在 Android 15 中處理 Edge-to-Edge 的強制執行](https://developer.android.com/codelabs/edge-to-edge)
- [針對 Android 15 的 Edge-to-Edge 強制執行的 Insets 處理技巧](https://medium.com/androiddevelopers/insets-handling-tips-for-android-15s-edge-to-edge-enforcement-872774e8839b)
- [Android 15 中顯示 ActionBar 時，狀態欄變白的問題](https://blog.cybozu.io/entry/2024/10/21/080000)

## 專案結構

```
.
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/cc/ddsakura/modernapp001/
│   │   │   │   ├── MainActivity.kt  // 應用程式進入點
│   │   │   │   ├── ...              // 其他 Activities, Fragments, ViewModels
│   │   │   ├── res/                 // 資源檔 (layouts, drawables, etc.)
│   │   │   └── AndroidManifest.xml  // 應用程式資訊清單
│   └── build.gradle.kts             // App 模組的建構腳本
├── build.gradle.kts                 // 專案層級的建構腳本
└── gradle/libs.versions.toml        // 依賴項版本管理
```
