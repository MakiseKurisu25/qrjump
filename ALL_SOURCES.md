# QrJump 全量源码

生成时间：2026-11-07 12:24　文件数：53

| # | 文件 | 行数 | 字节 |
|---|---|---|---|
| 1 | `.gitignore` | 17 | 173 |
| 2 | `settings.gradle.kts` | 15 | 261 |
| 3 | `build.gradle.kts` | 4 | 142 |
| 4 | `gradle.properties` | 6 | 187 |
| 5 | `app/build.gradle.kts` | 58 | 1906 |
| 6 | `app/proguard-rules.pro` | 2 | 81 |
| 7 | `app/src/main/AndroidManifest.xml` | 185 | 10163 |
| 8 | `app/src/main/java/com/local/qrjump/A11yGuard.kt` | 206 | 10621 |
| 9 | `app/src/main/java/com/local/qrjump/AutoUnlock.kt` | 199 | 9514 |
| 10 | `app/src/main/java/com/local/qrjump/AutoUnlockService.kt` | 347 | 17546 |
| 11 | `app/src/main/java/com/local/qrjump/DevicesActivity.kt` | 287 | 13965 |
| 12 | `app/src/main/java/com/local/qrjump/DeviceStore.kt` | 211 | 9356 |
| 13 | `app/src/main/java/com/local/qrjump/DeviceWidgetProvider.kt` | 163 | 8241 |
| 14 | `app/src/main/java/com/local/qrjump/Launcher.kt` | 351 | 17778 |
| 15 | `app/src/main/java/com/local/qrjump/ParseResult.kt` | 267 | 13073 |
| 16 | `app/src/main/java/com/local/qrjump/Prefs.kt` | 205 | 13065 |
| 17 | `app/src/main/java/com/local/qrjump/QLog.kt` | 26 | 1104 |
| 18 | `app/src/main/java/com/local/qrjump/ScanActivity.kt` | 554 | 26195 |
| 19 | `app/src/main/java/com/local/qrjump/SettingsActivity.kt` | 341 | 16912 |
| 20 | `app/src/main/java/com/local/qrjump/ShortcutActivity.kt` | 79 | 3894 |
| 21 | `app/src/main/java/com/local/qrjump/ShortcutHelper.kt` | 194 | 9917 |
| 22 | `app/src/main/java/com/local/qrjump/WebViewActivity.kt` | 205 | 9028 |
| 23 | `app/src/main/java/com/local/qrjump/WidgetBinding.kt` | 101 | 4706 |
| 24 | `app/src/main/java/com/local/qrjump/WidgetConfigActivity.kt` | 100 | 5352 |
| 25 | `app/src/main/res/drawable/bg_app_gradient.xml` | 52 | 1962 |
| 26 | `app/src/main/res/drawable/bg_glass_button_outline.xml` | 22 | 810 |
| 27 | `app/src/main/res/drawable/bg_glass_button.xml` | 37 | 1375 |
| 28 | `app/src/main/res/drawable/bg_glass_card.xml` | 25 | 1148 |
| 29 | `app/src/main/res/drawable/bg_glass_circle.xml` | 20 | 684 |
| 30 | `app/src/main/res/drawable/bg_glass_panel_light.xml` | 22 | 913 |
| 31 | `app/src/main/res/drawable/bg_glass_row.xml` | 34 | 1231 |
| 32 | `app/src/main/res/drawable/bg_scrim_bottom.xml` | 22 | 827 |
| 33 | `app/src/main/res/drawable/bg_scrim_top.xml` | 19 | 743 |
| 34 | `app/src/main/res/drawable/bg_widget_cell.xml` | 19 | 682 |
| 35 | `app/src/main/res/drawable/ic_launcher_background.xml` | 10 | 330 |
| 36 | `app/src/main/res/drawable/ic_launcher_foreground.xml` | 42 | 1392 |
| 37 | `app/src/main/res/drawable/ic_widget_heat.xml` | 23 | 901 |
| 38 | `app/src/main/res/drawable/ic_widget_water.xml` | 14 | 579 |
| 39 | `app/src/main/res/layout/activity_devices.xml` | 123 | 5709 |
| 40 | `app/src/main/res/layout/activity_scan.xml` | 160 | 6506 |
| 41 | `app/src/main/res/layout/activity_settings.xml` | 373 | 15508 |
| 42 | `app/src/main/res/layout/activity_webview.xml` | 59 | 2189 |
| 43 | `app/src/main/res/layout/activity_widget_config.xml` | 77 | 3116 |
| 44 | `app/src/main/res/layout/item_device.xml` | 52 | 2039 |
| 45 | `app/src/main/res/layout/item_widget_choice.xml` | 50 | 1892 |
| 46 | `app/src/main/res/layout/widget_cell.xml` | 44 | 2040 |
| 47 | `app/src/main/res/mipmap-anydpi-v26/ic_launcher.xml` | 7 | 427 |
| 48 | `app/src/main/res/values/glass_styles.xml` | 43 | 2012 |
| 49 | `app/src/main/res/values/strings.xml` | 127 | 11358 |
| 50 | `app/src/main/res/values/themes.xml` | 63 | 3672 |
| 51 | `app/src/main/res/xml/accessibility_service_config.xml` | 29 | 2051 |
| 52 | `app/src/main/res/xml/device_widget_info.xml` | 26 | 1548 |
| 53 | `gradle/wrapper/gradle-wrapper.properties` | 7 | 250 |

**合计 53 个文件 / 5724 行 / 277074 字节**

## .gitignore

```
# Gradle
build/
.gradle/
.gradle-home/

# 本机 SDK 路径，不该提交
local.properties

# 产物
*.apk
*.aab
*.ap_
*.dex
captures/

# IDE
.idea/
*.iml
*.hprof
.DS_Store
``````

## settings.gradle.kts

```kotlin
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "QrJump"
include(":app")
``````

## build.gradle.kts

```kotlin
plugins {
    id("com.android.application") version "8.5.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.24" apply false
}
``````

## gradle.properties

```properties
org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8
org.gradle.parallel=true
org.gradle.caching=true

android.useAndroidX=true
android.nonTransitiveRClass=true

kotlin.code.style=official
``````

## app/build.gradle.kts

```kotlin
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    // 包名刻意不用 com.qiekj.*：避免被误认为厂商官方应用，也不碰任何厂商命名空间。
    namespace = "com.local.qrjump"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.local.qrjump"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        // 不声明任何敏感权限，只有 CAMERA 与 INTERNET
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        viewBinding = false
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")

    // CameraX
    implementation("androidx.camera:camera-core:1.3.4")
    implementation("androidx.camera:camera-camera2:1.3.4")
    implementation("androidx.camera:camera-lifecycle:1.3.4")
    implementation("androidx.camera:camera-view:1.3.4")

    // 主解码器：ML Kit（GMS 版）。国行机若没有 GMS，运行时自动降级到 ZXing。
    implementation("com.google.mlkit:barcode-scanning:17.3.0")

    // 备选解码器：ZXing core，纯本地、无 GMS 依赖，只用来兜底扫二维码。
    implementation("com.google.zxing:core:3.5.3")
}
``````

## app/proguard-rules.pro

```
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
``````

## app/src/main/AndroidManifest.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">

    <!--
      权限只要三个：
        CAMERA   —— 扫码必需
        INTERNET —— 策略 C 的内置 WebView 需要
        （无障碍服务不需要权限声明，它由用户在系统设置里手动授权）

      WRITE_SECURE_SETTINGS 是第四个，但它【不是普通权限】：
      它是 signature|privileged 级，安装时系统一律拒绝授予，只有
      `adb shell pm grant` 能给它。所以：
        - 不授权时，App 一切照旧（只是无法在授权被系统清掉后自愈）；
        - 授权后，才能把「无障碍授权被划卡清掉」这件事自动修回来。
      声明在先，pm grant 才认（不声明会报 has not requested permission）。

      没有定位、没有存储、没有读取剪贴板（只写不读）、没有后台服务。
    -->
    <uses-permission android:name="android.permission.CAMERA" />
    <uses-permission android:name="android.permission.INTERNET" />

    <!--
        MIUI 的桌面在「确认添加小部件」弹窗里会自己再查一次权限：
            E/AddItemActivity-PinShortcutRequestUtils: add widget failed,
              com.local.qrjump has no permission
        它查的是小米自己声明的 com.miui.home.launcher.permission.INSTALL_WIDGET
        （在 MiuiHome.apk 的 manifest 里 protectionLevel=0，即 normal 级）。
        normal 级权限声明即得，不需要 adb 授权；不声明则 requestPinAppWidget
        虽然返回 true，MIUI 的确认弹窗会直接丢弃这次请求，桌面什么都不出现。
        这两条不影响任何其它桌面（原生 Launcher 会忽略未定义的自定义权限）。
    -->
    <uses-permission android:name="com.miui.home.launcher.permission.INSTALL_WIDGET" />
    <uses-permission android:name="com.miui.home.launcher.permission.UNINSTALL_WIDGET" />
    <uses-permission
        android:name="android.permission.WRITE_SECURE_SETTINGS"
        tools:ignore="ProtectedPermissions" />

    <!--
      桌面快捷方式的两条通道都已在 2026-11-06 实测废弃，相关权限与 queries 一并删除：
        · ShortcutManagerCompat.requestPinShortcut() 在 MIUI 上返回 true 却不出图标；
        · 旧版 com.android.launcher.action.INSTALL_SHORTCUT 广播被 Android 框架直接丢弃
          （W/ActivityManager: ... no longer supported. It will not be delivered.）。
      「放到桌面」最终走的是下面的桌面小部件，不需要任何额外权限。
    -->

    <uses-feature
        android:name="android.hardware.camera.any"
        android:required="true" />

    <!--
      Android 11+ 的包可见性限制：不声明 queries 的话
      resolveActivity() 会永远返回 null，日志就失去诊断意义。
      这里只声明我们真正关心的两类：https 浏览、支付宝（含 alipays scheme）。
    -->
    <queries>
        <intent>
            <action android:name="android.intent.action.VIEW" />
            <data android:scheme="https" />
        </intent>
        <intent>
            <action android:name="android.intent.action.VIEW" />
            <data android:scheme="alipays" />
        </intent>
        <package android:name="com.eg.android.AlipayGphone" />
    </queries>

    <application
        android:allowBackup="false"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:supportsRtl="false"
        android:theme="@style/Theme.QrJump">

        <!--
          启动页 = 设备列表。这就是「点开 App 立即呈现所有设备」。

          刻意【不】注册 https://h5.qiekj.com/skip 的 deep link filter：
          一旦注册，策略 A 用 ACTION_VIEW 打开这个 URL 时，系统可能把请求
          路由回本 App 自己，形成自我循环。少一个 filter，少一种故障模式。
        -->
        <activity
            android:name=".DevicesActivity"
            android:exported="true"
            android:label="@string/devices_title"
            android:theme="@style/Theme.QrJump.Dark">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>

        <!--
          扫码页。不再是启动页，改为只有从设备列表点「扫码添加设备」才进入，
          进入后相机也不自动打开，还要再点一次「点击开始扫码」。

          exported 仍然是 true，原因只有一个：README 里那套 adb 调试命令需要从
          shell 用 extra「com.local.qrjump.extra.FAKE_SCAN」启动它。
          它没有 intent-filter，普通用户点不到，也没有任何外部输入能改变行为。
        -->
        <activity
            android:name=".ScanActivity"
            android:configChanges="orientation|screenSize|keyboardHidden"
            android:exported="true"
            android:screenOrientation="portrait"
            android:theme="@style/Theme.QrJump.Fullscreen" />

        <!-- 设置页，不对外导出 -->
        <activity
            android:name=".SettingsActivity"
            android:exported="false"
            android:label="@string/title_settings"
            android:theme="@style/Theme.QrJump.Light" />

        <!-- 策略 C 的内置 WebView，不对外导出 -->
        <activity
            android:name=".WebViewActivity"
            android:exported="false"
            android:label="@string/title_webview"
            android:theme="@style/Theme.QrJump.Light" />

        <!--
          快捷方式落点。
          它是唯一一个 exported 的次要组件，原因：桌面快捷方式的启动
          Intent 由系统 Launcher 进程发出，从本应用的角度看来自"外部"。

          但它的输入只有「设备簿里的键」（形如 SN|<15位数字>），
          不接受 URL。查不到就直接结束，查到也要重新跑 ParseResult.parse。
          所以最坏情况 = 打开一台用户自己扫过的机器的官方设备页，
          与用户手点一次完全等价，不会产生订单或出水。
        -->
        <activity
            android:name=".ShortcutActivity"
            android:excludeFromRecents="true"
            android:exported="true"
            android:noHistory="true"
            android:taskAffinity=""
            android:theme="@style/Theme.QrJump.Transparent" />

        <!--
          自动点击「立即使用」的无障碍服务。

          exported 不写（服务不是 activity，默认就是由系统绑定，无需导出）。
          permission 必须写 BIND_ACCESSIBILITY_SERVICE，否则这个服务
          可以被任意应用绑定 —— 那是真正危险的情况。

          它是否真的运行，完全取决于用户有没有在「系统设置 → 无障碍」里手动打开，
          应用自己无法偷偷启用它。
        -->
        <service
            android:name=".AutoUnlockService"
            android:exported="false"
            android:label="@string/accessibility_label"
            android:permission="android.permission.BIND_ACCESSIBILITY_SERVICE">
            <intent-filter>
                <action android:name="android.accessibilityservice.AccessibilityService" />
            </intent-filter>
            <meta-data
                android:name="android.accessibilityservice"
                android:resource="@xml/accessibility_service_config" />
        </service>

        <!--
          桌面小部件：这台机器上唯一真正能"把设备摆到桌面"的机制。

          为什么需要它 —— 2026-11-06 23:35 的实测把两条快捷方式通道都堵死了：
            1. requestPinShortcut 返回 true，桌面不画（MIUI 屏蔽展示层）；
            2. 旧版 INSTALL_SHORTCUT 广播发出去了，系统直接丢弃：
               W/ActivityManager: Broadcast com.android.launcher.action.INSTALL_SHORTCUT
                                  no longer supported. It will not be delivered.
          而小部件机制从 Android 1.5 至今未被废弃，MIUI 完整支持。

          形态：**一台设备一个 1×1 方块**（2026-11-07 从 3×2 列表改过来）。
          每个实例绑定哪台设备记在 WidgetBinding 里，由下面的配置页决定。
          方块只带设备键（SN|8622...），点击后同样走 ShortcutActivity，
          由它回设备簿查询并重新校验。不存 URL、不存 token。
        -->
        <receiver
            android:name=".DeviceWidgetProvider"
            android:exported="false">
            <intent-filter>
                <action android:name="android.appwidget.action.APPWIDGET_UPDATE" />
            </intent-filter>
            <meta-data
                android:name="android.appwidget.provider"
                android:resource="@xml/device_widget_info" />
        </receiver>

        <!--
          小部件的配置页：放置时决定这个方块绑哪台设备。

          必须 exported="true"：拉起它的不是本应用，而是系统的 AppWidgetService
          （在放置小部件/方块失效重选时），从本应用的角度看属于外部调用。
          它的输入只有 EXTRA_APPWIDGET_ID（一个系统分配的整数 id），
          拿不到有效 id 就什么都不做，也不能凭它启动任何 URL。

          excludeFromRecents + noHistory：配置完就退，不该留在最近任务里。
        -->
        <activity
            android:name=".WidgetConfigActivity"
            android:excludeFromRecents="true"
            android:exported="true"
            android:noHistory="true"
            android:theme="@style/Theme.QrJump.Dark">
            <intent-filter>
                <action android:name="android.appwidget.action.APPWIDGET_CONFIGURE" />
            </intent-filter>
        </activity>

    </application>

</manifest>
``````

## app/src/main/java/com/local/qrjump/A11yGuard.kt

```kotlin
package com.local.qrjump

import android.accessibilityservice.AccessibilityServiceInfo
import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.provider.Settings
import android.view.accessibility.AccessibilityManager

/**
 * 无障碍授权的「自愈」。
 *
 * ── 为什么需要它 ──────────────────────────────────────────────
 * 22:33 在真机上把三种杀进程方式逐个试了一遍（Redmi K40S / MIUI OS1.0 / Android 14）：
 *
 *   | 方式                        | accessibility_enabled | enabled_accessibility_services |
 *   |-----------------------------|-----------------------|--------------------------------|
 *   | `am kill`（普通杀进程）      | 1（保持）             | 保持，服务自动重绑             |
 *   | 从最近任务划掉卡片           | **0**                 | **null**                       |
 *   | `am force-stop`             | **0**                 | **null**                       |
 *
 * 也就是说 MIUI 把「划掉卡片」当成了 force-stop，并且在 force-stop 时
 * 主动把这两个 secure setting 清空 —— 这不是 AOSP 的行为（AOSP 只解绑不清设置），
 * 是 MIUI 的安全策略。清空之后系统不会再自动绑回来，必须重新授权。
 *
 * ── 怎么办 ────────────────────────────────────────────────────
 * 普通应用无权写 secure settings，所以「自己把自己救回来」需要
 * `android.permission.WRITE_SECURE_SETTINGS`。它是 signature|privileged 级，
 * 安装时系统一律不授予，**只有 adb 能给它一次**：
 *
 *     adb shell pm grant com.local.qrjump android.permission.WRITE_SECURE_SETTINGS
 *
 * （manifest 里必须先声明，否则 pm grant 会报
 *   `Package ... has not requested permission ...`，22:35 实测过。）
 *
 * 给过一次之后，本类就能在被清空的**下一次启动时**把授权写回去：
 *   - 打开 App（DevicesActivity.onCreate）→ 检查并自愈
 *   - 每次真的要唤起设备页之前（arm 之前）→ 再检查并自愈
 *
 * 换句话说：划掉卡片的那一瞬间救不回来（进程已经死了，没有任何代码在跑），
 * 但**下一次你打开 App 或点设备的那一刻**它会自动补回来，
 * 不需要你再去系统设置里点一次。
 *
 * ── 边界 ──────────────────────────────────────────────────────
 * 只写两个 key，且是**追加**而不是覆盖：会先读出现有的服务列表，
 * 只有当本应用的服务不在里面时才追加，绝不碰别的无障碍服务。
 * 没有这个权限时，本类所有方法都是安全的空操作，App 行为与以前完全一致。
 */
object A11yGuard {

    private const val TAG_AREA = "A11y"

    /** 本应用的无障碍服务组件全名，写进 secure setting 的就是这个字符串。 */
    private fun component(context: Context): String =
        ComponentName(context, AutoUnlockService::class.java).flattenToString()

    /** 当前 App 是否拿到了那个「只有 adb 能给」的权限。 */
    fun canWrite(context: Context): Boolean =
        context.checkSelfPermission(android.Manifest.permission.WRITE_SECURE_SETTINGS) ==
            PackageManager.PERMISSION_GRANTED

    /**
     * 读系统设置里，本应用的无障碍服务到底在不在。
     *
     * 注意：这个方法**只有**在拿到 WRITE_SECURE_SETTINGS 之后才可靠 ——
     * 否则读 `Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES` 会抛异常或返回 null
     * （该 key 从 Android 4.2 起对普通应用受限，README 6.4 记过）。
     * 拿不到权限时回退到 AccessibilityManager 的运行时视图。
     */
    fun isEnabled(context: Context): Boolean {
        if (canWrite(context)) {
            val raw = runCatching {
                Settings.Secure.getString(
                    context.contentResolver,
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
                )
            }.getOrNull()
            if (raw != null) {
                return raw.split(':').any { it.equals(component(context), ignoreCase = true) }
            }
        }
        // 回退：运行时的已启用列表
        val am = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as? AccessibilityManager
            ?: return false
        val enabled = runCatching {
            am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK)
        }.getOrNull().orEmpty()
        return enabled.any {
            val info = it.resolveInfo?.serviceInfo ?: return@any false
            info.packageName == context.packageName &&
                info.name == AutoUnlockService::class.java.name
        }
    }

    /**
     * 尝试把被系统清掉的授权写回去。
     *
     * @return true 表示调用后确认已启用（可能是本来就在、也可能是刚补回来）。
     *
     * ── 写序：两套 ROM 的顺序是相反的 ────────────────────────────
     * - MIUI（Redmi K40S / Android 14）：**先** `accessibility_enabled=1`，
     *   **再** 写 services。反过来系统会把前者重置回 0。
     * - ColorOS（realme RMX6699 / Android 17）：**先** 写 services，
     *   **再** `accessibility_enabled=1`。反过来同样被重置回 0。
     *
     * 2026-11-07 09:34 在 realme 上实测：按 MIUI 顺序写，`settings get
     * accessibility_enabled` 直接变回 0，服务也绑不上。
     *
     * 所以这里不猜平台，而是**两种顺序都试**：先用 MIUI 顺序写一遍并复核，
     * 没生效就改用 ColorOS 顺序再写一遍。写两次的代价可以忽略，
     * 比在两台机器上各维护一个分支可靠得多。
     */
    fun heal(context: Context): Boolean {
        if (!Prefs.autoUnlock(context)) {
            // 用户没开这个功能，就别去动系统的无障碍设置
            return false
        }
        if (!canWrite(context)) {
            QLog.w(
                TAG_AREA,
                "没有 WRITE_SECURE_SETTINGS，无法自愈。" +
                    "一次性执行：adb shell pm grant ${context.packageName} " +
                    "android.permission.WRITE_SECURE_SETTINGS"
            )
            return isEnabled(context)
        }
        if (isEnabled(context)) {
            QLog.i(TAG_AREA, "无障碍授权在位，无需自愈")
            return true
        }

        val mine = component(context)
        val cr = context.contentResolver

        // 读出现有列表，只做「追加」，绝不覆盖别人的服务
        val before = runCatching {
            Settings.Secure.getString(cr, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES)
        }.getOrNull().orEmpty()

        val after = if (before.isBlank()) mine else "$before:$mine"

        QLog.w(TAG_AREA, "检测到无障碍授权被清空（多半是从最近任务划掉了 App），开始写回")
        QLog.i(TAG_AREA, "  原有服务列表=${before.ifBlank { "(空)" }}")
        QLog.i(TAG_AREA, "  写回后=$after")

        // 顺序 A（MIUI）：先 enabled，再 services
        writeAll(cr, enabledFirst = true, services = after)
        if (stillEnabled(cr, mine)) {
            QLog.i(TAG_AREA, "✓ 自愈成功（顺序 A：先 enabled 后 services）")
            return true
        }

        // 顺序 B（ColorOS）：先 services，再 enabled
        QLog.i(TAG_AREA, "顺序 A 没生效，改试顺序 B（先 services 后 enabled）")
        writeAll(cr, enabledFirst = false, services = after)
        val ok = stillEnabled(cr, mine)
        if (ok) {
            QLog.i(TAG_AREA, "✓ 自愈成功（顺序 B：先 services 后 enabled）")
        } else {
            QLog.e(
                TAG_AREA,
                "✗ 两种写序都没生效，系统拦截了第三方写入。" +
                    "需要你手动去系统设置里重新打开一次无障碍"
            )
        }
        return ok
    }

    /** 按指定顺序写两个 key。返回是否两次写入都无异常。 */
    private fun writeAll(
        cr: android.content.ContentResolver,
        enabledFirst: Boolean,
        services: String
    ): Boolean {
        val putEnabled = {
            runCatching {
                Settings.Secure.putInt(cr, Settings.Secure.ACCESSIBILITY_ENABLED, 1)
            }.isSuccess
        }
        val putServices = {
            runCatching {
                Settings.Secure.putString(cr, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES, services)
            }.isSuccess
        }
        val a: Boolean
        val b: Boolean
        if (enabledFirst) {
            a = putEnabled(); b = putServices()
        } else {
            a = putServices(); b = putEnabled()
        }
        QLog.i(TAG_AREA, "  write(enabledFirst=$enabledFirst) 两次写入结果=$a/$b")
        return a && b
    }

    /** 用系统视角复核：服务在列表里、且开关为 1。别只信写入的返回值。 */
    private fun stillEnabled(cr: android.content.ContentResolver, mine: String): Boolean {
        val services = runCatching {
            Settings.Secure.getString(cr, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES)
        }.getOrNull()
        val flag = runCatching {
            Settings.Secure.getInt(cr, Settings.Secure.ACCESSIBILITY_ENABLED, 0)
        }.getOrDefault(0)
        val inList = services?.contains(mine, ignoreCase = true) == true
        if (!inList || flag != 1) {
            QLog.i(TAG_AREA, "  复核：服务在列表=$inList  开关=$flag")
        }
        return inList && flag == 1
    }

    /** 给设置页/日志用的一行状态描述。 */
    fun statusLine(context: Context): String {
        val enabled = isEnabled(context)
        val writable = canWrite(context)
        return when {
            enabled && writable -> "无障碍：已启用，且可自愈"
            enabled && !writable -> "无障碍：已启用，但不能自愈（划掉卡片后会掉，需重新授权）"
            !enabled && writable -> "无障碍：未启用，但有权限，下次唤起前会自动补上"
            else -> "无障碍：未启用，且无自愈权限"
        }
    }
}
``````

## app/src/main/java/com/local/qrjump/AutoUnlock.kt

```kotlin
package com.local.qrjump

import android.content.Context
import android.os.SystemClock

/**
 * 「到设备页后自动点一下立即使用」的**触发窗口**控制。
 *
 * ── 为什么需要一个窗口，而不是常开 ──
 * 无障碍服务一旦开启就是全局的。如果它在任何时候看到「立即使用」就去点，
 * 那用户自己手动打开支付宝、自己翻到某台设备页时也会被莫名其妙点掉，
 * 而且可能点的是别的楼栋、别的机器。所以这里做了一个「只在刚唤起后的一小段
 * 时间里生效」的约束：
 *
 *   本应用成功唤起一次设备页  →  [arm] 打一个时间戳
 *   2 分钟内出现官方设备页  →  等够设定秒数后点一次，然后立刻 [disarm]
 *   超过 2 分钟  →  自动失效
 *
 * 这个设计让自动点击的作用域严格等于「我自己刚唤起的这一次」，
 * 而不是「任何一次出现在屏幕上的设备页」。
 *
 * ── 为什么要等（2026-11-06 用户反馈「打开网页后加载有延迟」）──
 * 翻 21:04 的控件树 dump 能证实：
 *
 *   小票余额 40.42元 / 出水口 冷水 热水 / **立即使用** / **内容准备中** / 刷新
 *
 * ——「立即使用」和「内容准备中」是同一次 dump 里同时存在的节点。
 * 也就是说按钮在页面还没准备好的时候就已经在树里了。这时候点下去，
 * 点的是一个没接上设备状态的页面，官方那边多半不理，或者提示要刷新。
 *
 * ── 计时口径（2026-11-06 22:05 改成单一时钟，单位秒）──
 * 之前是两道闸相加：等「内容准备中」消失（最多 8 秒宽限）+ 就绪后再等
 * 稳定期（2.5 秒）。实测总时长 3 + 8 + 2.5 = 11 秒左右，而且**不可预测**：
 * 那个「内容准备中」根本不会消失（它其实是页面上独立的小票/广告位慢区域），
 * 于是每次都恒定吃满 8 秒宽限，再叠加稳定期。
 *
 * 现在改成一条可控的时钟：**从第一次认出设备页起，等满设定秒数就点**。
 * 加载标记只用来打日志，不再参与计时。好处是总等待时间 = 设定值，
 * 用户可以自己按楼里的网络情况调（默认 8 秒，设置页 0–30 秒）。
 *
 * ── 红线在哪 ──
 * 这里点的是支付宝小程序页面上的那个真按钮，走的是支付宝自己的控件和
 * 它自己的业务逻辑。我们不构造订单、不调私有接口、不伪造签名、不改金额，
 * 也不碰登录态 —— 与用户自己用手指点一下的区别只在于「谁点的」。
 * 计费、扣款、出水规则全部仍然由官方页面决定。
 */
object AutoUnlock {

    /** 触发窗口：2 分钟。够慢吞吞地从设备列表走到设备页，又不至于长时间挂着一把枪。 */
    private const val WINDOW_MS = 2 * 60 * 1000L

    /** 用 elapsedRealtime 而不是 System.currentTimeMillis：不受用户改系统时间影响。 */
    @Volatile
    private var armedAt = 0L

    /** 已经自动点过一次就不再点，避免页面抖动导致连点。 */
    @Volatile
    private var alreadyClicked = false

    /** 第一次认出「这像设备页」的时刻。整个等待时间从这里起算。 */
    @Volatile
    private var pageSeenAt = 0L

    /** 上一次打印"还差多久"时用的剩余秒数，用来给日志降噪。 */
    @Volatile
    private var lastRemainingSec = -1L

    /** 上一次打过日志的加载标记，避免同一状态每秒刷一行。 */
    @Volatile
    private var lastLoadingMark: String? = null

    /**
     * arm 时记下的等待秒数。放这里是因为 [noteDevicePageSeen] 拿不到 Context，
     * 而它需要这个值来打一行"开始等 N 秒"的日志。
     */
    @Volatile
    private var armedDelaySec = 0

    /**
     * 成功发起一次唤起后调用。
     *
     * 只有在设置里真的打开了自动解锁时才计时；关着就什么都不做，
     * 保证「默认关闭」是彻底的关闭（服务即使被系统启动也不会动手）。
     */
    fun arm(context: Context) {
        if (!Prefs.autoUnlock(context)) {
            QLog.i("Auto", "自动解锁未开启，不进入待命状态")
            return
        }
        // 每次真要唤起设备页之前，先检查无障碍授权还在不在。
        // MIUI 会把「从最近任务划掉卡片」当成 force-stop 并清空授权，
        // 拿到 WRITE_SECURE_SETTINGS 时这里能自动补回来（见 A11yGuard）。
        A11yGuard.heal(context)
        armedAt = SystemClock.elapsedRealtime()
        alreadyClicked = false
        pageSeenAt = 0L
        lastRemainingSec = -1L
        lastLoadingMark = null
        val sec = Prefs.autoUnlockDelaySec(context)
        armedDelaySec = sec
        QLog.i(
            "Auto",
            "进入待命状态（${WINDOW_MS / 1000} 秒内出现设备页则点一次，" +
                "认出设备页后再等 $sec 秒）"
        )
    }

    fun disarm(reason: String) {
        if (armedAt == 0L) return
        armedAt = 0L
        pageSeenAt = 0L
        QLog.i("Auto", "退出待命状态：$reason")
    }

    /** 当前是否处在可动手的窗口内。 */
    fun isArmed(): Boolean {
        if (armedAt == 0L) return false
        val elapsed = SystemClock.elapsedRealtime() - armedAt
        if (elapsed > WINDOW_MS) {
            QLog.i("Auto", "待命已超时（${elapsed / 1000} 秒），不再动手")
            armedAt = 0L
            pageSeenAt = 0L
            return false
        }
        return true
    }

    /**
     * 记录"这次真的看到设备页了"，并从这里开始计时。
     * 重复调用不会重置起点 —— 否则页面每刷一帧就把等待清零，永远等不完。
     */
    fun noteDevicePageSeen() {
        val now = SystemClock.elapsedRealtime()
        if (pageSeenAt != 0L) return
        pageSeenAt = now
        QLog.i("Auto", "已认出设备页，开始计时（等 $armedDelaySec 秒）")
    }

    /**
     * 页面上还有「内容准备中」之类的标记。
     *
     * 现在它**只用于打日志**，不参与计时：实测这块东西是页面上独立的慢区域
     * （小票/广告位），等它等于永远等。真正决定什么时候点的是 [isSettledEnough]。
     */
    fun noteLoading(mark: String) {
        if (lastLoadingMark == mark) return
        lastLoadingMark = mark
        QLog.i("Auto", "页面还显示「$mark」（它是独立慢区域，不影响计时）")
    }

    /** 从认出设备页到现在过去了多少毫秒。还没认出时返回 0。 */
    fun waitedMs(): Long {
        if (pageSeenAt == 0L) return 0L
        return SystemClock.elapsedRealtime() - pageSeenAt
    }

    /**
     * 从认出设备页起，是否已经等够了 [delaySec] 秒。
     *
     * 没等够时打一条带剩余秒数的日志（同样的剩余值只打一次，避免刷屏）。
     */
    fun isSettledEnough(delaySec: Int): Boolean {
        if (pageSeenAt == 0L) return false
        val waited = waitedMs()
        val threshold = delaySec * 1000L
        if (waited >= threshold) return true
        val remainingSec = (threshold - waited + 999) / 1000
        if (remainingSec != lastRemainingSec) {
            lastRemainingSec = remainingSec
            QLog.i("Auto", "还没等够，还差 $remainingSec 秒（阈值 $delaySec 秒）")
        }
        return false
    }

    /** 只有第一次调用返回 true，用来保证「只点一次」。 */
    fun claimOnce(): Boolean {
        if (alreadyClicked) return false
        alreadyClicked = true
        return true
    }

    /**
     * 还没点过、人也还在窗口里吗。
     *
     * 与 [claimOnce] 的区别：这里**不消耗**名额，只用于"值不值得去遍历控件树"
     * 的前置过滤。设备页每渲染一帧都会发事件，点过之后就该直接短路掉，
     * 不然会一直白白遍历上千个节点。
     */
    fun canStillClaim(): Boolean = !alreadyClicked && armedAt != 0L

    /**
     * 判断无障碍服务当前是否真的在跑。
     *
     * 两条路都试，因为每条在各家 ROM 上都不完全可靠：
     *   1) `AccessibilityManager.getEnabledAccessibilityServiceList()` ——
     *      公开 API，正常机型上最准。
     *   2) `AutoUnlockService.onServiceConnected()` 自己写下的回执标记 ——
     *      最实在的口径：服务真的被系统绑上了才会写。
     * 另外 `Settings.Secure.getString(ENABLED_ACCESSIBILITY_SERVICES)` 不可用：
     * 那个 key 从 Android 4.2 起对普通应用受限，本机实测恒为 null。
     */
    fun isServiceEnabled(context: Context): Boolean {
        val am = context.getSystemService(Context.ACCESSIBILITY_SERVICE)
            as? android.view.accessibility.AccessibilityManager
        if (am != null) {
            val expected = AutoUnlockService::class.java.name
            val hit = am.getEnabledAccessibilityServiceList(
                android.accessibilityservice.AccessibilityServiceInfo.FEEDBACK_ALL_MASK
            ).any { info ->
                val name = info.resolveInfo?.serviceInfo?.name ?: ""
                name == expected || name.endsWith(".$expected")
            }
            if (hit) return true
        }
        // 退一步：服务自己连上时写的回执
        return Prefs.a11yConnected(context)
    }
}
``````

## app/src/main/java/com/local/qrjump/AutoUnlockService.kt

```kotlin
package com.local.qrjump

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.graphics.Rect
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

/**
 * 无障碍服务：**只在很窄的条件下**替用户点一下官方设备页上的「立即使用」。
 *
 * ── 为什么用无障碍，而不是别的路子 ──
 * 支付宝小程序跑在支付宝自己的进程里（`com.eg.android.AlipayGphone`），
 * 本应用拿不到它的界面、也注入不了任何东西。唯一被 Android 官方允许的、
 * 不越狱不逆向的「代用户点一下」的机制就是无障碍服务。
 * 它看到的只是系统公开给辅助功能的控件树，我们不读它的网络、不读它的存储。
 *
 * ── 动手的四个必要条件，缺一不可 ──
 *   1. 用户**在设置里打开了**「自动解锁」开关（Prefs.autoUnlock）
 *   2. 处在 [AutoUnlock] 的待命窗口内（刚由本应用唤起，2 分钟内）
 *   3. 发出事件的窗口属于支付宝（`com.eg.android.AlipayGphone`）——
 *      别的应用哪怕有同名按钮也绝不碰
 *   4. 同一棵控件树里同时能找到「出水口」这个只有设备页才有的标记，
 *      避免把别的页面上的同名按钮误判成设备页
 *
 * 四条都满足才点，点完立刻退出待命，一台设备只点一次。
 *
 * ── 两种点击方式（2026-11-06 21:05 实测得到的分支）──
 * 用 `uiautomator dump` 把真实设备页的控件树抓下来看过：
 *
 *   node text="立即使用" class=android.widget.TextView clickable=false bounds=[44,2128][1036,2274]
 *     ^L1 class=android.view.View        clickable=false
 *     ^L2 class=android.view.View        clickable=false
 *     ^L3 class=android.view.View rid=__react-content clickable=false
 *     ^L4 class=android.webkit.WebView   clickable=false
 *     ^L5 class=com.alipay.mywebview.sdk.WebView clickable=false
 *     ^L6 class=android.widget.FrameLayout clickable=false
 *
 * 也就是说这是小程序页面里的一块 WebView 渲染出来的 React 内容，
 * **按钮和它往上的六层祖先全都不是 clickable**，`performAction(ACTION_CLICK)`
 * 对它没有意义。所以点击走两条路：
 *   · 先按老办法找可点击的自身/祖先，有就 performAction；
 *   · 没有就用 `dispatchGesture` 在那个节点的屏幕坐标中心**按下并抬起一次**，
 *     跟人手指点一下完全等价（这也是系统给辅助功能的正规接口，
 *     需要配置里的 `canPerformGestures="true"`）。
 *
 * ── 不做什么 ──
 * 不读屏幕上的账号、手机号、余额、优惠券；不遍历无关节点做记录；
 * 不做滑动、输入、返回等任何其它手势；不常驻后台做别的事。
 */
class AutoUnlockService : AccessibilityService() {

    private companion object {
        /** 官方页面上的按钮文案。实测两种形态的设备页都是这四个字。 */
        const val TARGET_TEXT = "立即使用"

        /** 设备页独有的标记。有它才说明这是设备页，而不是随便某个有「立即使用」的页面。 */
        const val DEVICE_PAGE_MARK = "出水口"

        /**
         * 「页面还没准备好」的标记。设备页在拉设备状态的那几秒里会显示「内容准备中」
         * 加一个「刷新」按钮，而**这时候「立即使用」已经在控件树里了**
         * （2026-11-06 21:04 的 dump 里两者同时存在）。看到这些字样就说明还不能点。
         */
        val LOADING_MARKS = listOf("内容准备中", "正在加载", "加载中")

        /** 支付宝包名。只在这个窗口里动手。 */
        const val ALIPAY_PKG = "com.eg.android.AlipayGphone"

        /** 两次尝试之间的最小间隔，避免页面还在渲染时疯狂判断。 */
        const val MIN_INTERVAL_MS = 400L

        /** 手势按下到抬起的时长。太短会被系统当成抖动丢掉，60ms 与真人点击相当。 */
        const val TAP_DURATION_MS = 60L

        /**
         * 轮询间隔。设备页渲染完就不再发无障碍事件，只能自己定时看；
         * 1 秒一次足够跟上一个"加载有延迟"的页面，又不会明显耗电。
         * 只在待命窗口内（最长 2 分钟）跑，点过或超时立刻停。
         */
        const val POLL_INTERVAL_MS = 1000L
    }

    private var lastCheckAt = 0L

    /** 上一条日志的内容，用来去掉同一状态下的重复刷屏。 */
    private var lastNote = ""

    override fun onServiceConnected() {
        super.onServiceConnected()
        // 写一条"我真的被系统绑上了"的回执。设备列表页读它来判断服务状态，
        // 因为 Settings.Secure 的 enabled_accessibility_services 普通应用读不到。
        Prefs.setA11yConnected(this, true)
        QLog.i("Auto", "无障碍服务已连接（canPerformGestures 见配置）")

        // 2026-11-06 22:11 实测到的一个缺口：轮询原本只在收到无障碍事件时才启动，
        // 而设备页渲染完就静止了。如果服务是在页面已经静止**之后**才被绑上
        // （重装 APK 后重新授权、或用户在系统设置里刚打开开关），就一条事件也收不到，
        // 轮询永远不启动，自动点击等于没开。
        // 所以绑定成功时，只要（1）自动解锁开着（2）处在待命窗口内，就直接起轮询。
        if (Prefs.autoUnlock(this) && AutoUnlock.isArmed() && AutoUnlock.canStillClaim()) {
            QLog.i("Auto", "绑定后发现仍在待命窗口内，直接启动轮询（不依赖事件）")
            startPolling()
            checkOnce()
        }
    }

    override fun onUnbind(intent: android.content.Intent?): Boolean {
        Prefs.setA11yConnected(this, false)
        QLog.i("Auto", "无障碍服务已解绑")
        stopPolling()
        return super.onUnbind(intent)
    }

    /**
     * 无障碍事件只是"可能该看一眼"的信号，真正的判断在 [checkOnce]。
     *
     * ── 为什么不能只靠事件（2026-11-06 21:50 实测）──
     * 设备页把内容渲染完之后就**不再发任何 TYPE_WINDOW_CONTENT_CHANGED**了。
     * 日志停在 `设备页还在加载（「内容准备中」），等它稳定` 之后就一片安静，
     * 于是"等下一次事件再判"永远等不到。所以这里除了事件，还起了一个
     * 轻量轮询：只要还在待命窗口内，就每 [POLL_INTERVAL_MS] 毫秒自己看一眼。
     * 待命窗口最长 2 分钟，且一旦点过或超时就停，不会常驻耗电。
     */
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        val e = event ?: return

        // 只在"窗口内容变化"这类事件上判断，滚动/通知之类直接跳过
        when (e.eventType) {
            AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED,
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED,
            AccessibilityEvent.TYPE_VIEW_SCROLLED -> Unit
            else -> return
        }

        if (!Prefs.autoUnlock(this)) return
        if (!AutoUnlock.isArmed()) {
            stopPolling()
            return
        }
        if (!AutoUnlock.canStillClaim()) {
            stopPolling()
            return
        }

        // 事件必须来自支付宝。e.packageName 可能为 null（合成事件），直接跳过。
        val pkg = e.packageName?.toString() ?: return
        if (pkg != ALIPAY_PKG) return

        startPolling()
        checkOnce()
    }

    /** 轮询：设备页静止后不发事件，只能自己定时看。 */
    private val pollHandler = android.os.Handler(android.os.Looper.getMainLooper())
    private var polling = false
    private val pollRunnable = object : Runnable {
        override fun run() {
            if (!polling) return
            if (!Prefs.autoUnlock(this@AutoUnlockService) ||
                !AutoUnlock.isArmed() ||
                !AutoUnlock.canStillClaim()
            ) {
                stopPolling()
                return
            }
            checkOnce()
            if (polling) pollHandler.postDelayed(this, POLL_INTERVAL_MS)
        }
    }

    private fun startPolling() {
        if (polling) return
        polling = true
        QLog.i("Auto", "开始轮询设备页状态（每 ${POLL_INTERVAL_MS}ms 一次）")
        pollHandler.postDelayed(pollRunnable, POLL_INTERVAL_MS)
    }

    private fun stopPolling() {
        if (!polling) return
        polling = false
        pollHandler.removeCallbacks(pollRunnable)
        QLog.i("Auto", "停止轮询")
    }

    /** 看一眼当前窗口，够条件就动手。事件与轮询都走这里，保证判断只有一份。 */
    private fun checkOnce() {
        if (!Prefs.autoUnlock(this)) return
        if (!AutoUnlock.isArmed()) return
        if (!AutoUnlock.canStillClaim()) return

        val now = android.os.SystemClock.elapsedRealtime()
        if (now - lastCheckAt < MIN_INTERVAL_MS) return
        lastCheckAt = now

        val root = rootInActiveWindow ?: return
        try {
            val marks = scanPage(root)
            if (!marks.hasDeviceMark) {
                note("当前窗口没有「$DEVICE_PAGE_MARK」，不像设备页，不动手")
                return
            }
            // 给计时一个起点：从"第一次认出这是设备页"开始算，之后不再重置。
            AutoUnlock.noteDevicePageSeen()
            // 加载标记只用于日志。实测「内容准备中」是页面上独立的小票/广告位
            // 慢区域，等它等于永远等（21:47 那次数了 15 秒仍在）。真正决定
            // 什么时候动手的是下面那条单一时钟：认出设备页起等满设定秒数。
            marks.loadingMark?.let { AutoUnlock.noteLoading(it) }

            val button = marks.target
            if (button == null) {
                note("设备页已出现，但还没找到「$TARGET_TEXT」按钮")
                return
            }

            // 单一时钟：认出设备页起等满 autoUnlockDelaySec 秒（默认 8 秒）。
            val delaySec = Prefs.autoUnlockDelaySec(this)
            if (!AutoUnlock.isSettledEnough(delaySec)) return

            // 调试模式下只报告、不动手。这是唯一能"完整验证这条链"又不真的
            // 开始出水计费的办法：日志会证明"设备页认出来了、加载完了、按钮找到了、
            // 坐标算对了"，只是不真的按下。
            if (Prefs.debugMode(this)) {
                val r = Rect().also { button.getBoundsInScreen(it) }
                QLog.i(
                    "Auto",
                    "调试模式：已定位到「$TARGET_TEXT」（class=${button.className}，" +
                        "clickable=${button.isClickable}，中心=(${r.exactCenterX()}, ${r.exactCenterY()})，" +
                        "已等 ${AutoUnlock.waitedMs() / 1000} 秒 / 阈值 ${delaySec} 秒），本应点击但未动手"
                )
                // 调试模式已经拿到要的证据了，停掉轮询免得每秒刷一行同样的日志
                stopPolling()
                return
            }

            if (!AutoUnlock.claimOnce()) return
            clickNode(button)
        } catch (t: Throwable) {
            QLog.e("Auto", "自动点击过程异常", t)
        } finally {
            // 节点用完要回收，避免跨进程句柄泄漏。
            // recycle() 在 API 33 起被标记废弃（系统自动池化），但低版本仍需手动回收，
            // 所以这里保留调用并显式压制废弃警告。
            @Suppress("DEPRECATION")
            runCatching { root.recycle() }
        }
    }

    /** 同一条提示只打一次，页面纹丝不动时不要每 400ms 刷一行。 */
    private fun note(msg: String) {
        if (msg != lastNote) {
            lastNote = msg
            QLog.i("Auto", msg)
        }
    }

    /**
     * 点这个节点。两条路：
     *   1. 节点自身或往上 8 层祖先里有 enabled 且 clickable 的 → `performAction(ACTION_CLICK)`
     *   2. 都没有（WebView 里的文字按钮就是这种）→ 用 `dispatchGesture` 在节点中心点一下
     */
    private fun clickNode(node: AccessibilityNodeInfo) {
        var cur: AccessibilityNodeInfo? = node
        var guard = 0
        while (cur != null && guard < 8) {
            if (cur.isClickable && cur.isEnabled) {
                val ok = cur.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                QLog.i("Auto", "点击「$TARGET_TEXT」用 ACTION_CLICK 结果=$ok（class=${cur.className}）")
                if (ok) AutoUnlock.disarm("已自动点击「$TARGET_TEXT」")
                return
            }
            cur = cur.parent
            guard++
        }

        // 走到这里说明整条祖先链都不是 clickable，只能点坐标。
        val rect = Rect()
        node.getBoundsInScreen(rect)
        if (rect.width() <= 0 || rect.height() <= 0) {
            QLog.i("Auto", "「$TARGET_TEXT」节点没有有效坐标（$rect），放弃")
            return
        }
        val cx = rect.exactCenterX()
        val cy = rect.exactCenterY()
        val path = Path().apply { moveTo(cx, cy) }
        val gesture = GestureDescription.Builder()
            .addStroke(GestureDescription.StrokeDescription(path, 0L, TAP_DURATION_MS))
            .build()
        val accepted = dispatchGesture(
            gesture,
            object : GestureResultCallback() {
                override fun onCompleted(gestureDescription: GestureDescription?) {
                    QLog.i("Auto", "手势点击已完成：($cx, $cy)，这一台不再自动点")
                    AutoUnlock.disarm("已自动点击「$TARGET_TEXT」（手势）")
                }

                override fun onCancelled(gestureDescription: GestureDescription?) {
                    QLog.w("Auto", "手势点击被系统取消：($cx, $cy)")
                }
            },
            null
        )
        QLog.i("Auto", "点击「$TARGET_TEXT」用 dispatchGesture 提交=$accepted 坐标=($cx, $cy)")
    }

    /** 一次遍历的扫描结果。三件事一次看完，避免对同一棵树反复 BFS。 */
    private class PageScan {
        var hasDeviceMark = false
        var loadingMark: String? = null
        var target: AccessibilityNodeInfo? = null
        var targetIsExact = false
    }

    /**
     * 扫一遍当前窗口，同时回答三个问题：
     *   1. 这是不是设备页（有没有「出水口」）
     *   2. 是不是还在加载（有没有「内容准备中」之类）
     *   3. 「立即使用」在哪（优先完全相等的文本，退一步用包含匹配）
     *
     * 一次遍历做完，是因为设备页每重绘一帧都会发事件，分开三次 BFS 在
     * 上千个节点上会明显拖慢主线程。
     */
    private fun scanPage(root: AccessibilityNodeInfo): PageScan {
        val result = PageScan()
        val queue = ArrayDeque<AccessibilityNodeInfo>()
        queue.add(root)
        var visited = 0

        while (queue.isNotEmpty() && visited < 1500) {
            val node = queue.removeFirst()
            visited++

            if (node.isVisibleToUser) {
                val text = node.text?.toString()
                val desc = node.contentDescription?.toString()
                val combined = when {
                    text == null -> desc
                    desc == null -> text
                    else -> "$text $desc"
                }

                if (combined != null) {
                    if (!result.hasDeviceMark &&
                        (text == DEVICE_PAGE_MARK || desc == DEVICE_PAGE_MARK ||
                            text?.contains(DEVICE_PAGE_MARK) == true)
                    ) {
                        result.hasDeviceMark = true
                    }

                    if (result.loadingMark == null) {
                        for (mark in LOADING_MARKS) {
                            if (combined.contains(mark)) {
                                result.loadingMark = mark
                                break
                            }
                        }
                    }

                    // 完全相等优先；包含匹配只在还没拿到精确命中时顶上
                    if (text == TARGET_TEXT) {
                        if (!result.targetIsExact) {
                            result.target = node
                            result.targetIsExact = true
                        }
                    } else if (result.target == null &&
                        (text?.contains(TARGET_TEXT) == true || desc?.contains(TARGET_TEXT) == true)
                    ) {
                        result.target = node
                    }
                }
            }

            for (i in 0 until node.childCount) {
                node.getChild(i)?.let { queue.add(it) }
            }
        }
        return result
    }

    override fun onInterrupt() {
        QLog.i("Auto", "无障碍服务被中断")
    }

    override fun onDestroy() {
        stopPolling()
        QLog.i("Auto", "无障碍服务已销毁")
        super.onDestroy()
    }
}
``````

## app/src/main/java/com/local/qrjump/DevicesActivity.kt

```kotlin
package com.local.qrjump

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

/**
 * 启动页 = 设备列表。
 *
 * ── 为什么把它设成启动页 ──
 * 日常真正高频的动作是"打开 → 选一台 → 到设备页"，而扫码一台新机器是低频动作。
 * 之前把扫码页设成启动页，每次都要等相机起来、再点一下一键直达面板，
 * 一个日常动作被拆成了两步。现在打开就是列表，点一行就走。
 *
 * ── 红线在这里怎么体现 ──
 * 列表里只放**设备标识**（SN / NQT）与用户自己起的备注名。
 * 点击一行做的事和扫到那张码完全一样：走 [Launcher] 的 A/B/C 三档，
 * 把官方地址原样递出去。不产生任何下单、开机、出水、支付指令，
 * 也不携带任何 token / 签名 / 登录态。
 */
class DevicesActivity : AppCompatActivity() {

    private lateinit var container: LinearLayout
    private lateinit var emptyView: TextView

    // 液态玻璃改版后，底部两个按钮与右上角入口都从 Button 换成了 TextView
    // （Material Button 自带实心底色，放不进玻璃背景，会一眼看出是贴上去的色块）。
    // 类型跟着改成 TextView，否则 findViewById<Button> 会抛 ClassCastException。
    private lateinit var btnClear: TextView
    private lateinit var btnAdd: TextView
    private lateinit var a11yWarn: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_devices)

        container = findViewById(R.id.containerDevices)
        emptyView = findViewById(R.id.tvDevicesEmpty)
        btnClear = findViewById(R.id.btnDevicesClear)
        btnAdd = findViewById(R.id.btnAddDevice)
        a11yWarn = findViewById(R.id.tvA11yWarn)

        // 提示条本身就是入口：点一下直达系统无障碍页。
        // 不弹对话框、不解释第二遍 —— 用户点它的动作已经表达了意图。
        a11yWarn.setOnClickListener {
            QLog.i("Devices", "用户点了无障碍提示条，跳系统无障碍设置")
            openA11ySettings()
        }

        btnAdd.setOnClickListener {
            QLog.i("Devices", "去扫码添加设备")
            startActivity(Intent(this, ScanActivity::class.java))
        }

        findViewById<TextView>(R.id.btnDevicesSettings).setOnClickListener {
            QLog.i("Devices", "从设备列表打开设置页")
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        btnClear.setOnClickListener { confirmClear() }

        QLog.i("Devices", "═══ 设备列表（启动页）打开 ═══")
        QLog.i(
            "Devices",
            "策略=${Prefs.strategy(this)} 调试=${Prefs.debugMode(this)} " +
                "自动解锁=${Prefs.autoUnlock(this)} 无障碍服务=${AutoUnlock.isServiceEnabled(this)}"
        )
    }

    override fun onResume() {
        super.onResume()
        // 无障碍自愈放在 onResume 而不是 onCreate（2026-11-06 23:03 实测修正）。
        //
        // 两个理由：
        //  1) MIUI 在「从最近任务划掉卡片」时会把 accessibility_enabled /
        //     enabled_accessibility_services 清空。App 回到前台是最自然的补救时机，
        //     而 onCreate 只在进程第一次起来时跑一次 —— 从后台切回来不经过它。
        //  2) 重装 APK 之后系统会**异步复核**一次包变化并再次清除授权（实测约 1 秒），
        //     所以 onCreate 那一次写回可能被随后的系统动作抹掉；
        //     放进 onResume（每次回前台都会跑）能补到第二次。
        //
        // heal() 自身是幂等的：授权在位时只打一行「无需自愈」就返回。
        if (A11yGuard.heal(this)) {
            QLog.i("Devices", "无障碍授权已确保在位（写回是同步的，服务绑定异步，通常 <1 秒）")
        }
        refreshA11yWarn()
        render()
    }

    /**
     * 决定那条黄色提示条要不要露出来。
     *
     * 三个条件同时满足才显示：用户开了自动解锁、授权现在不在位、而且本应用**补不回来**。
     * 第三点是关键 —— 补得回来的情况，heal() 刚刚已经补好了，
     * 这时候再摆一条「授权没了」就是在骗人。
     */
    private fun refreshA11yWarn() {
        val wanted = Prefs.autoUnlock(this)
        val enabled = A11yGuard.isEnabled(this)
        val writable = A11yGuard.canWrite(this)
        val show = wanted && !enabled && !writable
        a11yWarn.visibility = if (show) View.VISIBLE else View.GONE
        if (show) {
            QLog.w(
                "Devices",
                "自动解锁开着，但无障碍授权不在位，且本机不允许本应用自己写回" +
                    "（ColorOS 封了 pm grant），改为显示可点的提示条"
            )
        }
    }

    private fun openA11ySettings() {
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
        try {
            startActivity(intent)
        } catch (t: Throwable) {
            QLog.e("Devices", "打开无障碍设置失败", t)
            Toast.makeText(this, R.string.devices_a11y_open_failed, Toast.LENGTH_LONG).show()
        }
    }

    // ------------------------------------------------------------ 渲染

    private fun render() {
        val list = DeviceStore.all(this)
        QLog.i("Devices", "设备簿共 ${list.size} 台：${list.joinToString { "${it.idKind}:${it.id}" }}")

        container.removeAllViews()

        if (list.isEmpty()) {
            emptyView.visibility = View.VISIBLE
            btnClear.visibility = View.GONE
            return
        }
        emptyView.visibility = View.GONE
        btnClear.visibility = View.VISIBLE

        val inflater = LayoutInflater.from(this)
        for (d in list) {
            val row = inflater.inflate(R.layout.item_device, container, false)

            val name = row.findViewById<TextView>(R.id.tvDeviceName)
            val more = row.findViewById<TextView>(R.id.btnDeviceMore)

            name.text = if (d.alias.isNotBlank()) d.alias else defaultName(d)
            // 「⋯」是个装饰性的三点，用文字更稳（不同 MIUI 版本对 icon 支持不一）
            more.text = "⋯"

            row.setOnClickListener {
                QLog.i("Devices", "点选设备：形态=${d.idKind} 标识=${d.id} 备注=${d.alias}")
                launchDevice(d)
            }

            more.setOnClickListener { showRowMenu(d) }

            container.addView(row)
        }
    }

    private fun defaultName(d: DeviceStore.Device): String = if (d.idKind == ParseResult.ID_SN) {
        getString(R.string.quick_id_sn, d.id)
    } else {
        getString(R.string.quick_id_nqt, d.id)
    }

    // ------------------------------------------------------------ 送达

    /**
     * 用这台设备走一遍和扫码完全相同的唤起流程。
     *
     * 注意这里重新 parse 一次，保证交到 Launcher 手里的永远是通过完整校验的结果。
     */
    private fun launchDevice(d: DeviceStore.Device) {
        val ok = when (val r = ParseResult.parse(d.launchUrl)) {
            is ParseResult.Ok -> r
            is ParseResult.Reject -> {
                QLog.w("Devices", "这台设备的地址已不再通过校验，拒绝唤起：${r.reason}")
                Toast.makeText(this, r.reason, Toast.LENGTH_LONG).show()
                return
            }
        }

        val debug = Prefs.debugMode(this)
        val strategy = Prefs.strategy(this)
        val autoFb = Prefs.autoFallback(this)

        QLog.i(
            "Devices",
            "从设备列表唤起：形态=${ok.idKind} 标识=${ok.id} 策略=$strategy 调试=$debug 降级=$autoFb"
        )

        val r = Launcher.launch(this, ok, strategy, debug, autoFb)

        when (r) {
            is AttemptResult.Started -> {
                QLog.i("Devices", "✓ 已发起：${r.strategy} → ${r.resolvedComponent}")
                // 设备簿里把它的时间刷新到最前
                DeviceStore.remember(this, ok)
                // 进入了自动解锁的待命窗口（开关关着时 arm 内部会直接返回）
                AutoUnlock.arm(this)
                finish()
            }

            is AttemptResult.PlannedDryRun -> {
                QLog.i("Devices", "⚠ 调试模式：未真正唤起（${r.strategy}）")
                Toast.makeText(this, getString(R.string.toast_dryrun), Toast.LENGTH_LONG).show()
            }

            is AttemptResult.Unavailable ->
                Toast.makeText(this, getString(R.string.status_failed, r.reason), Toast.LENGTH_LONG).show()

            is AttemptResult.Failed ->
                Toast.makeText(this, getString(R.string.status_failed, r.reason), Toast.LENGTH_LONG).show()
        }
    }

    // ------------------------------------------------------------ 行菜单

    private fun showRowMenu(d: DeviceStore.Device) {
        val items = arrayOf(
            getString(R.string.devices_rename),
            getString(R.string.devices_pin),
            getString(R.string.devices_delete)
        )
        AlertDialog.Builder(this)
            .setTitle(if (d.alias.isNotBlank()) d.alias else defaultName(d))
            .setItems(items) { _, which ->
                when (which) {
                    0 -> askRename(d)
                    1 -> pinDevice(d)
                    2 -> askDelete(d)
                }
            }
            .show()
    }

    /**
     * 放到桌面：给这**一台**设备放一个 1×1 方块。
     *
     * 2026-11-06 23:28 之前这里只调 requestPinShortcut，在 MIUI 上它返回 true
     * 却什么都不画，用户看到的是"点了没反应"。现在改成按桌面能力选通道，
     * 并且**把结果如实告诉用户** —— 成了就说成了，没成也说清楚下一步。
     *
     * 2026-11-07 从「一块列表」改成「一格一台」：列表小部件在桌面上是个面板，
     * 点之前还要在面板里找行；1×1 的位置由用户自己摆，按位置记比按文字记快。
     */
    private fun pinDevice(d: DeviceStore.Device) {
        val label = if (d.alias.isNotBlank()) d.alias else defaultName(d)
        QLog.i("Devices", "请求放到桌面：key=${d.key} label=$label")

        // 第一选择：独立快捷方式（能落到原生/第三方桌面）。
        if (ShortcutHelper.pinToHome(this, d)) {
            Toast.makeText(this, R.string.devices_pin_ok, Toast.LENGTH_LONG).show()
            return
        }

        // 第二选择：请求放这台设备自己的 1×1 小部件。
        // 快捷方式在这台机器的桌面上不可用（MIUI 实测是假成功），
        // 但小部件走的是 AppWidgetService，是另一套机制，实测可用。
        // 设备键会先记进 WidgetBinding 的 pending 槽，配置页取走后立刻返回，
        // 用户只需要在系统那个确认框上点一下。
        QLog.w("Devices", "快捷方式不可用，改为请求放置单设备小部件")
        if (ShortcutHelper.pinWidget(this, d)) {
            Toast.makeText(this, R.string.devices_pin_widget_ok, Toast.LENGTH_LONG).show()
            return
        }

        // 两条都不可用：说清楚下一步，别让用户以为是坏了。
        QLog.w("Devices", "小部件请求也未被接受，提示手动添加")
        Toast.makeText(this, R.string.devices_pin_failed, Toast.LENGTH_LONG).show()
    }

    private fun askRename(d: DeviceStore.Device) {
        val input = EditText(this).apply {
            inputType = InputType.TYPE_CLASS_TEXT
            hint = getString(R.string.devices_rename_hint)
            setText(d.alias)
            setSelection(text.length)
            setPadding(48, 36, 48, 36)
        }

        AlertDialog.Builder(this)
            .setTitle(R.string.devices_rename)
            .setView(input)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                DeviceStore.rename(this, d.key, input.text.toString())
                Toast.makeText(this, R.string.devices_renamed, Toast.LENGTH_SHORT).show()
                render()
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    private fun askDelete(d: DeviceStore.Device) {
        AlertDialog.Builder(this)
            .setTitle(R.string.devices_delete)
            .setMessage(if (d.alias.isNotBlank()) d.alias else defaultName(d))
            .setPositiveButton(android.R.string.ok) { _, _ ->
                DeviceStore.forget(this, d.key)
                Toast.makeText(this, R.string.devices_deleted, Toast.LENGTH_SHORT).show()
                render()
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    private fun confirmClear() {
        val n = DeviceStore.all(this).size
        if (n == 0) return
        AlertDialog.Builder(this)
            .setTitle(R.string.devices_clear)
            .setMessage(getString(R.string.devices_clear_confirm, n))
            .setPositiveButton(android.R.string.ok) { _, _ ->
                DeviceStore.clear(this)
                Toast.makeText(this, R.string.devices_cleared, Toast.LENGTH_SHORT).show()
                render()
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }
}
``````

## app/src/main/java/com/local/qrjump/DeviceStore.kt

```kotlin
package com.local.qrjump

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

/**
 * 设备簿：记住扫过的每一台机器，供「设备列表」自由选择。
 *
 * ── 这里存的是什么 ──
 * 只存**通过完整校验的设备码原文**，以及从里面解出来的设备标识（SN / NQT）
 * 和用户自己起的备注名。设备标识机身印着、公开可见，不含任何 token /
 * 签名 / 登录态 / 订单信息。所以记住它，本质上是「本 App 认得这台机器」，
 * 不涉及鉴权，也不代替官方身份校验。
 *
 * ── 为什么每次读出来还要重新校验 ──
 * [all] 会把每条存下来的地址重新丢给 [ParseResult.parse] 走完整校验，
 * 而不是直接信任 SharedPreferences 里的内容。这样即使偏好文件被改坏、
 * 或被别的工具写入奇怪的值，也不会绕过 host / path / 标识格式的检查。
 * 校验不过的条目会被跳过并记日志，不会成为"信任边界之外的后门"。
 *
 * ── 不做的事 ──
 * 不缓存任何网络响应，不缓存小程序页面，不保存支付结果、用水量或余额。
 * 只记"这台机器是哪台"。
 */
object DeviceStore {

    /** 最多记这么多台，防止无限增长。超出时丢掉最久没用的。 */
    const val MAX_DEVICES = 20

    /**
     * 一台设备。
     *
     * @param launchUrl 通过校验的完整地址（形态②下是原始包装地址）
     * @param innerUrl  解包后的内层官方地址
     * @param id        设备标识（SN 的 15 位数字 / NQT 的 UUID）
     * @param idKind    [ParseResult.ID_SN] 或 [ParseResult.ID_NQT]
     * @param params    内层地址的 query 原文
     * @param time      上次使用的毫秒时间戳，仅用于显示排序
     * @param alias     用户自己起的名字，空 = 用标识当名字
     */
    data class Device(
        val launchUrl: String,
        val innerUrl: String,
        val id: String,
        val idKind: String,
        val params: String,
        val time: Long,
        val alias: String
    ) {
        /** 去重/查找用的稳定键。 */
        val key: String get() = "$idKind|$id"
    }

    private fun sp(context: Context) =
        context.applicationContext.getSharedPreferences(Prefs.FILE, Context.MODE_PRIVATE)

    // ---------------------------------------------------------------- 读

    /**
     * 全部设备，最近用过的排在最前。
     *
     * 每条都会重新走一遍 [ParseResult.parse]：校验不过的直接丢弃（并在日志里说明），
     * 所以记忆槽不是"信任边界之外的后门"。
     */
    fun all(context: Context): List<Device> {
        val raw = sp(context).getString(Prefs.KEY_DEVICES, null)
        if (raw.isNullOrBlank()) return migrateLegacy(context)

        val arr = try {
            JSONArray(raw)
        } catch (t: Throwable) {
            QLog.w("Memory", "设备簿 JSON 解析失败，按空处理：${QLog.describe(t)}")
            return emptyList()
        }

        val out = ArrayList<Device>(arr.length())
        for (i in 0 until arr.length()) {
            val o = arr.optJSONObject(i) ?: continue
            val url = o.optString("url", "")
            if (url.isBlank()) continue

            val parsed = ParseResult.parse(url)
            if (parsed !is ParseResult.Ok) {
                QLog.w(
                    "Memory",
                    "设备簿第 ${i + 1} 条已不再通过校验，跳过：" +
                        (parsed as? ParseResult.Reject)?.reason
                )
                continue
            }
            out.add(
                Device(
                    launchUrl = parsed.launchUrl,
                    innerUrl = parsed.innerUrl,
                    id = parsed.id,
                    idKind = parsed.idKind,
                    params = parsed.paramsText(),
                    time = o.optLong("time", 0L),
                    alias = o.optString("alias", "")
                )
            )
        }
        out.sortByDescending { it.time }
        return out
    }

    /** 按 key 取一台；不存在返回 null。 */
    fun get(context: Context, idKind: String, id: String): Device? =
        all(context).firstOrNull { it.idKind == idKind && it.id == id }

    // ---------------------------------------------------------------- 写

    /**
     * 记住一次成功解析的设备码。已存在同一个标识时只更新时间（保留备注名），
     * 并把它移到最前。
     */
    fun remember(context: Context, ok: ParseResult.Ok) {
        val now = System.currentTimeMillis()
        val list = all(context).toMutableList()

        val idx = list.indexOfFirst { it.idKind == ok.idKind && it.id == ok.id }
        val alias = if (idx >= 0) list[idx].alias else ""

        val d = Device(
            launchUrl = ok.launchUrl,
            innerUrl = ok.innerUrl,
            id = ok.id,
            idKind = ok.idKind,
            params = ok.paramsText(),
            time = now,
            alias = alias
        )
        if (idx >= 0) list.removeAt(idx)
        list.add(0, d)

        val capped = if (list.size > MAX_DEVICES) list.subList(0, MAX_DEVICES).toList() else list
        write(context, capped)

        QLog.i(
            "Memory",
            "已记住设备：形态=${ok.idKind} 标识=${ok.id}（${if (idx >= 0) "已存在，更新时间" else "新设备"}，" +
                "当前共 ${capped.size} 台）"
        )
    }

    /** 改备注名。传空字符串等于取消备注。 */
    fun rename(context: Context, key: String, alias: String) {
        val list = all(context).toMutableList()
        val idx = list.indexOfFirst { it.key == key }
        if (idx < 0) {
            QLog.w("Memory", "改名前没找到这台设备：$key")
            return
        }
        list[idx] = list[idx].copy(alias = alias.trim())
        write(context, list)
        QLog.i("Memory", "设备改名：$key → 「${alias.trim()}」")
    }

    /** 删掉一台。 */
    fun forget(context: Context, key: String) {
        val list = all(context).filterNot { it.key == key }
        write(context, list)
        QLog.i("Memory", "已删除设备：$key（剩余 ${list.size} 台）")
    }

    /** 清空设备簿。 */
    fun clear(context: Context) {
        sp(context).edit()
            .remove(Prefs.KEY_DEVICES)
            .remove(Prefs.KEY_LAST_URL)
            .remove(Prefs.KEY_LAST_INNER)
            .remove(Prefs.KEY_LAST_ID)
            .remove(Prefs.KEY_LAST_ID_KIND)
            .remove(Prefs.KEY_LAST_PARAMS)
            .remove(Prefs.KEY_LAST_TIME)
            .apply()
        // 设备簿清空后，桌面上的快捷方式也必须一起清掉，否则会留下点了没反应的僵尸入口。
        ShortcutHelper.sync(context)
        DeviceWidgetProvider.refresh(context)
        QLog.i("Memory", "已清空设备簿")
    }

    // ---------------------------------------------------------------- 内部

    private fun write(context: Context, list: List<Device>) {
        val arr = JSONArray()
        for (d in list) {
            val o = JSONObject()
            o.put("url", d.launchUrl)
            o.put("inner", d.innerUrl)
            o.put("id", d.id)
            o.put("kind", d.idKind)
            o.put("params", d.params)
            o.put("time", d.time)
            o.put("alias", d.alias)
            arr.put(o)
        }
        sp(context).edit().putString(Prefs.KEY_DEVICES, arr.toString()).apply()

        // 设备簿一变，桌面快捷方式跟着变，避免留下去不掉的僵尸入口。
        // sync 内部会重新 all() 一次，读的是刚写进去的内容。
        ShortcutHelper.sync(context)
        // 桌面小部件同理：设备簿变了，桌面那块也要立刻反映（否则要等下次开机）。
        DeviceWidgetProvider.refresh(context)
    }

    /**
     * 旧版本只有"一台"的记忆槽，迁移成列表。
     *
     * 迁移后保留旧键不删，避免迁移过程中出意外把唯一的数据弄丢；
     * 下一轮 remember 写入新键后它们自然不再被读。
     */
    private fun migrateLegacy(context: Context): List<Device> {
        val url = sp(context).getString(Prefs.KEY_LAST_URL, null)
        if (url.isNullOrBlank()) return emptyList()

        val parsed = ParseResult.parse(url)
        if (parsed !is ParseResult.Ok) {
            QLog.w("Memory", "旧记忆槽内容已不再通过校验，不迁移")
            return emptyList()
        }
        val d = Device(
            launchUrl = parsed.launchUrl,
            innerUrl = parsed.innerUrl,
            id = parsed.id,
            idKind = parsed.idKind,
            params = parsed.paramsText(),
            time = sp(context).getLong(Prefs.KEY_LAST_TIME, 0L),
            alias = ""
        )
        write(context, listOf(d))
        QLog.i("Memory", "旧版单条记忆已迁移为设备簿：形态=${d.idKind} 标识=${d.id}")
        return listOf(d)
    }
}
``````

## app/src/main/java/com/local/qrjump/DeviceWidgetProvider.kt

```kotlin
package com.local.qrjump

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews

/**
 * 桌面小部件：**一台设备一个 1×1 方块**。
 *
 * ── 为什么是小部件 ──
 * 「放到桌面」原本靠两条路，在这台 MIUI 机器上**都不通**（2026-11-06 实测）：
 *   1. `requestPinShortcut` 返回 true 却什么都不画（MIUI 屏蔽了展示层）；
 *   2. 旧版 `INSTALL_SHORTCUT` 广播被系统直接丢弃，logcat 原文：
 *      `W/ActivityManager: Broadcast com.android.launcher.action.INSTALL_SHORTCUT
 *       no longer supported. It will not be delivered.`
 * 而桌面小部件是 Android 从 1.5 就有、从未被废弃、MIUI 也完整支持的机制。
 *
 * ── 从「一块列表」改成「一格一台」的理由 ──
 * 列表版是 3×2 的一大块，桌面上一眼看去是个面板，点之前还要在面板里找行。
 * 1×1 是一格一个方块，位置由用户自己摆，直觉上就等于"这台机器"：
 * 南五801 放左边、洗澡放右边，按位置记比按文字记快。
 *
 * ── 每个实例怎么知道自己是哪台 ──
 * 系统给每个小部件分配一个独立的 id，但**不会**告诉你它绑给谁。
 * 所以对应关系存在 [WidgetBinding] 里（id -> 设备键）。两条入口：
 *   · 从 App 里点「放到桌面」：先记一次 pending，系统拉起 [WidgetConfigActivity]
 *     后它直接把 pending 绑上去，用户不用再选一次；
 *   · 手动从选择器拖：没有 pending，[WidgetConfigActivity] 会正常弹一个选择框。
 *
 * ── 安全边界 ──
 * 小部件只携带设备键（`SN|8622...`），点击后交给 [ShortcutActivity]，
 * 由它回设备簿查询并**重新跑一遍完整校验**。小部件本身不存 URL、不存 token，
 * 也不会因为没有绑定就退化成"打开某个固定网址"。
 */
class DeviceWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        QLog.i("Widget", "收到刷新：${appWidgetIds.size} 个实例")
        appWidgetIds.forEach { id ->
            // 有些机型（含 MIUI）在"钉到桌面"成功后会先发一次 UPDATE 再拉配置页，
            // 这时 pending 还在：先绑上，用户等会儿就不必再选一遍。
            if (WidgetBinding.deviceKey(context, id) == null) {
                val pending = WidgetBinding.takePendingPin(context)
                if (pending != null) {
                    QLog.i("Widget", "实例 $id 命中待绑定设备 $pending")
                    WidgetBinding.bind(context, id, pending)
                }
            }
            appWidgetManager.updateAppWidget(id, buildViews(context, id))
        }
        WidgetBinding.prune(
            context,
            appWidgetManager.getAppWidgetIds(ComponentName(context, DeviceWidgetProvider::class.java)).toSet()
        )
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        super.onDeleted(context, appWidgetIds)
        appWidgetIds.forEach { WidgetBinding.unbind(context, it) }
        QLog.i("Widget", "用户移除 ${appWidgetIds.size} 个实例")
    }

    override fun onEnabled(context: Context) {
        QLog.i("Widget", "第一个实例被添加")
    }

    override fun onDisabled(context: Context) {
        QLog.i("Widget", "最后一个实例被移除")
    }

    companion object {

        /**
         * 设备簿一变就调它（和 [ShortcutHelper.sync] 并列调用）。
         *
         * 绑定的设备被删掉时，这里会把它显示成「已失效，点一下重新选择」，
         * 而不是留一个点了没反应的方块。
         */
        fun refresh(context: Context) {
            val mgr = AppWidgetManager.getInstance(context) ?: return
            val cn = ComponentName(context, DeviceWidgetProvider::class.java)
            val ids = try {
                mgr.getAppWidgetIds(cn)
            } catch (t: Throwable) {
                QLog.w("Widget", "取小部件实例失败：${QLog.describe(t)}")
                return
            }
            if (ids.isEmpty()) return
            QLog.i("Widget", "设备簿变化，刷新 ${ids.size} 个实例")
            ids.forEach { id -> mgr.updateAppWidget(id, buildViews(context, id)) }
            WidgetBinding.prune(context, ids.toSet())
        }

        /** 某个实例换绑之后立刻重画它自己。 */
        fun updateOne(context: Context, widgetId: Int) {
            val mgr = AppWidgetManager.getInstance(context) ?: return
            mgr.updateAppWidget(widgetId, buildViews(context, widgetId))
        }

        private fun buildViews(context: Context, widgetId: Int): RemoteViews {
            val views = RemoteViews(context.packageName, R.layout.widget_cell)
            val key = WidgetBinding.deviceKey(context, widgetId)
            val d = key?.let { k -> DeviceStore.all(context).firstOrNull { it.key == k } }

            if (d == null) {
                // 没绑 / 绑的设备已被删：显示成一个可点的"空位"，点它去重新选。
                // 不显示成空白方块 —— 那样用户会以为小部件坏了。
                views.setImageViewResource(R.id.widgetCellIcon, R.drawable.ic_widget_water)
                views.setTextViewText(
                    R.id.widgetCellName,
                    context.getString(if (key == null) R.string.widget_cell_unbound else R.string.widget_cell_gone)
                )
                views.setOnClickPendingIntent(R.id.widgetCellRoot, configPending(context, widgetId))
                return views
            }

            views.setImageViewResource(
                R.id.widgetCellIcon,
                if (d.idKind == ParseResult.ID_SN) R.drawable.ic_widget_water else R.drawable.ic_widget_heat
            )
            views.setTextViewText(R.id.widgetCellName, nameOf(d))
            views.setOnClickPendingIntent(R.id.widgetCellRoot, launchPending(context, d))
            return views
        }

        private fun launchPending(context: Context, d: DeviceStore.Device): PendingIntent {
            val intent = Intent(context, ShortcutActivity::class.java).apply {
                action = Intent.ACTION_VIEW
                putExtra(ShortcutActivity.EXTRA_KEY, d.key)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
            // requestCode 用设备键的哈希，保证不同方块之间不互相覆盖
            // （RemoteViews 上最常见的坑：不给不同 requestCode 就全被当成同一个）。
            return PendingIntent.getActivity(
                context,
                d.key.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }

        private fun configPending(context: Context, widgetId: Int): PendingIntent {
            val intent = Intent(context, WidgetConfigActivity::class.java).apply {
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            return PendingIntent.getActivity(
                context,
                widgetId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }

        /**
         * 方块上那一行字。
         *
         * 只有一格宽，15 位设备号放不下，所以优先用用户自己起的备注名；
         * 没起名就退成「饮水器 031408」这种能认得出来的短名。
         */
        private fun nameOf(d: DeviceStore.Device): String =
            if (d.alias.isNotBlank()) {
                d.alias
            } else if (d.idKind == ParseResult.ID_SN) {
                "饮水器 …" + d.id.takeLast(6)
            } else {
                "热水 …" + d.id.take(4)
            }
    }
}
``````

## app/src/main/java/com/local/qrjump/Launcher.kt

```kotlin
package com.local.qrjump

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

/**
 * 一次唤起尝试的结果。策略之间靠这个类型传递，便于日志与 UI 如实展示。
 */
sealed class AttemptResult {
    /** 已经真正发起了 Intent（或 WebView Activity）。 */
    data class Started(
        val strategy: String,
        val intent: Intent?,
        val resolvedComponent: String?,
        val extra: String = ""
    ) : AttemptResult()

    /** 调试模式：只算出来要做什么，没有真正唤起。 */
    data class PlannedDryRun(
        val strategy: String,
        val intent: Intent?,
        val resolvedComponent: String?,
        val extra: String = ""
    ) : AttemptResult()

    /** 这一档在当前配置下压根不可用（例如 B 没填 appId），跳到下一档。 */
    data class Unavailable(val strategy: String, val reason: String) : AttemptResult()

    /** 尝试了但失败了，reason 说明失败点。 */
    data class Failed(val strategy: String, val reason: String) : AttemptResult()
}

/**
 * 三档唤起策略的实现与降级编排。
 *
 * 红线在这里体现为：
 *   - 只构造"打开某个官方页面/官方小程序"的 Intent
 *   - 不构造任何支付、下单、开机、出水的指令
 *   - 不读取、不篡改、不伪造任何 token / 签名 / 订单号
 *   - 设备标识只作为 URL query 里的普通参数传递，和机身二维码里带的一模一样
 */
object Launcher {

    const val STRATEGY_A = "A"   // ACTION_VIEW 直接打开原始 URL
    const val STRATEGY_B = "B"   // 经支付宝 scheme / ds.alipay.com 唤起小程序
    const val STRATEGY_C = "C"   // 内置 WebView

    /**
     * 主入口。按策略顺序尝试，失败按需降级，全失败则复制到剪贴板。
     *
     * @param ok 已通过 [ParseResult] 全部校验的结果，携带 launchUrl / 参数 / 标识
     */
    fun launch(
        activity: Activity,
        ok: ParseResult.Ok,
        strategy: String,
        debugMode: Boolean,
        autoFallback: Boolean
    ): AttemptResult {

        val order = buildOrder(activity, strategy, autoFallback)
        QLog.i(
            "Launch",
            "策略顺序 = $order（用户选择=$strategy，形态=${ok.idKind}，自动降级=$autoFallback，调试模式=$debugMode）"
        )

        var lastFailure: AttemptResult? = null

        for (s in order) {
            QLog.i("Launch", "──────── 尝试策略 $s ────────")
            val r = when (s) {
                STRATEGY_A -> tryA(activity, ok, debugMode)
                STRATEGY_B -> tryB(activity, ok, debugMode)
                STRATEGY_C -> tryC(activity, ok, debugMode)
                else -> AttemptResult.Unavailable(s, "未知策略 $s")
            }

            when (r) {
                is AttemptResult.Started -> {
                    QLog.i("Launch", "策略 $s 已发起。component=${r.resolvedComponent} ${r.extra}")
                    return r
                }
                is AttemptResult.PlannedDryRun -> {
                    QLog.i("Launch", "策略 $s 调试模式：仅记录，未唤起。component=${r.resolvedComponent} ${r.extra}")
                    return r
                }
                is AttemptResult.Unavailable -> {
                    QLog.w("Launch", "策略 $s 不可用：${r.reason}")
                    lastFailure = r
                }
                is AttemptResult.Failed -> {
                    QLog.w("Launch", "策略 $s 失败：${r.reason}")
                    lastFailure = r
                }
            }
        }

        // 全失败 → 剪贴板兜底
        QLog.w("Launch", "所有策略均未成功，最后失败=${lastFailure?.let { describe(it) }}，转入剪贴板兜底")
        copyToClipboard(activity, ok.launchUrl)
        return lastFailure ?: AttemptResult.Failed("-", "没有任何可用策略")
    }

    private fun describe(r: AttemptResult): String = when (r) {
        is AttemptResult.Unavailable -> "${r.strategy}:unavailable(${r.reason})"
        is AttemptResult.Failed -> "${r.strategy}:failed(${r.reason})"
        is AttemptResult.Started -> "${r.strategy}:started"
        is AttemptResult.PlannedDryRun -> "${r.strategy}:dryrun"
    }

    /**
     * 计算尝试顺序。这里有两层考虑：
     *
     * 1. **形态**。B 档（alipays 直连）的一步直达**只在形态①（SN）上实测过**。
     *    形态②（NQT）不知道小程序需要哪些 query 参数，所以不把它当默认首选 ——
     *    改成让 C 档先上：C 打开官方包装页，页面自己会发起它自己的 alipays://
     *    跳转，那是官方给的完整参数，比我猜的可靠。
     *
     * 2. **A 档不参与降级**。A 已知只会落到 App 下载引导页，挂在链上等于
     *    把用户丢到下载页。只有用户显式选 A 时才走 A → B → C 的完整链。
     */
    private fun buildOrder(
        activity: Activity,
        strategy: String,
        autoFallback: Boolean
    ): List<String> {
        if (!autoFallback) return listOf(strategy)

        val bUsable = Prefs.alipayAppId(activity).isNotEmpty() && Prefs.alipayPage(activity).isNotEmpty()

        // 2026-11-06 20:09 起，两种形态的 B 档都实测能直达设备页：
        //   形态① SN  → masterSwitch=0&keepUsing=1&SN=<15位>&isShowAd=false
        //   形态② NQT → masterSwitch=0&keepUsing=1&NQT=<UUID>&isShowAd=false
        // 所以不再需要「形态② C 优先」的特殊分支，统一 B 优先。
        return when (strategy) {
            STRATEGY_A -> if (bUsable) listOf(STRATEGY_A, STRATEGY_B, STRATEGY_C)
                          else listOf(STRATEGY_A, STRATEGY_C)

            STRATEGY_B -> if (bUsable) listOf(STRATEGY_B, STRATEGY_C)
                          else listOf(STRATEGY_C)

            STRATEGY_C -> listOf(STRATEGY_C)

            else -> if (bUsable) listOf(STRATEGY_B, STRATEGY_C) else listOf(STRATEGY_C)
        }.distinct()
    }

    // ---------------------------------------------------------------- 策略 A

    /**
     * A：ACTION_VIEW 打开原始 URL，交给系统与厂商官方分发页自行处理。
     * 这是最"干净"的一档：App 完全不解释这个 URL，只是把它递出去。
     *
     * 用 launchUrl 而不是 innerUrl：形态②的机身码是带包装层的完整地址，
     * 包装层可能含一次性参数，重拼反而可能失效。
     */
    private fun tryA(activity: Activity, ok: ParseResult.Ok, debugMode: Boolean): AttemptResult {
        val url = ok.launchUrl
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
            addCategory(Intent.CATEGORY_BROWSABLE)
        }

        val resolved = resolve(activity, intent)
        QLog.i("Launch", "A 构造 Intent=${dumpIntent(intent)}")
        QLog.i("Launch", "A resolveActivity=${resolved ?: "null"}")

        if (resolved == null) {
            return AttemptResult.Failed(STRATEGY_A, "没有 App 能处理 https VIEW（resolveActivity=null）")
        }

        if (debugMode) {
            return AttemptResult.PlannedDryRun(STRATEGY_A, intent, resolved, "动作=startActivity(ACTION_VIEW, $url)")
        }

        return start(activity, intent, STRATEGY_A, resolved)
    }

    // ---------------------------------------------------------------- 策略 B

    /**
     * B：经支付宝唤起小程序。
     *
     * 两条内部路径，按顺序试：
     *   B1  alipays://platformapi/startapp?appId=..&page=..&query=..
     *   B2  https://ds.alipay.com/?scheme=<URLEncode(B1 的 uri)>
     *
     * appId / page / query 模板全部来自设置页（有实测默认预填值）。
     * 未配置时本档直接判 Unavailable，不构造半截 URI。
     *
     * query 的构造分形态，两个形态用同一套外层参数，只有标识键名不同：
     *   形态①（SN）  -> masterSwitch=0&keepUsing=1&SN=<15位数字>&isShowAd=false
     *   形态②（NQT） -> masterSwitch=0&keepUsing=1&NQT=<UUID>&isShowAd=false
     *
     * 两次实测教训（都记在 README 第 6.2 节）：
     *   1. 只带 SN（query=SN%3Dxxx）-> 小程序判「二维码有误，换一个试试」；
     *   2. 形态②直传机身码里官方那组 Company/CommunicateType/Ver/NQT
     *      -> 同样落到「二维码有误」。
     * 所以两个模板都不能精简成裸标识，必须带上外层参数。
     */
    private fun tryB(activity: Activity, ok: ParseResult.Ok, debugMode: Boolean): AttemptResult {
        val appId = Prefs.alipayAppId(activity)
        val page = Prefs.alipayPage(activity)

        if (appId.isEmpty() || page.isEmpty()) {
            return AttemptResult.Unavailable(
                STRATEGY_B,
                "未配置 appId/page（appId=${if (appId.isEmpty()) "空" else "已填"}，" +
                    "page=${if (page.isEmpty()) "空" else "已填"}）"
            )
        }

        val query = buildQuery(activity, ok)
        val schemeUri = buildAlipayScheme(appId, page, query)
        QLog.i("Launch", "B 形态=${ok.idKind} 生成的 query = $query")
        QLog.i("Launch", "B 生成的 alipays URI = $schemeUri")

        // --- B1: 直连 alipays scheme ---
        val i1 = Intent(Intent.ACTION_VIEW, Uri.parse(schemeUri))
        val r1 = resolve(activity, i1)
        QLog.i("Launch", "B1 Intent=${dumpIntent(i1)}  resolve=${r1 ?: "null"}")

        if (r1 != null) {
            if (debugMode) {
                return AttemptResult.PlannedDryRun(STRATEGY_B, i1, r1, "子路径=B1(alipays 直连)")
            }
            val started = start(activity, i1, "$STRATEGY_B/B1", r1)
            if (started is AttemptResult.Started) return started
            QLog.w("Launch", "B1 启动抛异常，继续试 B2：${(started as? AttemptResult.Failed)?.reason}")
        }

        // --- B2: ds.alipay.com 包装 ---
        val wrapped = "https://ds.alipay.com/?scheme=" + Uri.encode(schemeUri)
        val i2 = Intent(Intent.ACTION_VIEW, Uri.parse(wrapped)).apply {
            addCategory(Intent.CATEGORY_BROWSABLE)
        }
        val r2 = resolve(activity, i2)
        QLog.i("Launch", "B2 包装 URL=$wrapped")
        QLog.i("Launch", "B2 Intent=${dumpIntent(i2)}  resolve=${r2 ?: "null"}")

        if (r2 == null) {
            return AttemptResult.Failed(STRATEGY_B, "B1 无接收方，B2 也没有 App 能处理 ds.alipay.com")
        }

        if (debugMode) {
            return AttemptResult.PlannedDryRun(STRATEGY_B, i2, r2, "子路径=B2(ds.alipay.com 包装)")
        }

        return start(activity, i2, "$STRATEGY_B/B2", r2)
    }

    /**
     * 按形态拼 query。
     *
     * 模板里支持的占位符：
     *   {SN}    → 形态①的数字设备号
     *   {NQT}   → 形态②的 UUID
     *   {ID}    → 当前形态的标识值（两者皆可）
     *   {QUERY} → 官方内层地址的**原始 query 整串**（键顺序保持机上原样）
     *
     * 形态②模板为空时直接返回 {QUERY}，即把官方给的那组参数原样带过去，
     * 不添加任何自己编的参数。
     */
    private fun buildQuery(activity: Activity, ok: ParseResult.Ok): String {
        val tpl = if (ok.idKind == ParseResult.ID_NQT) {
            Prefs.alipayQueryTemplateNqt(activity)
        } else {
            Prefs.alipayQueryTemplate(activity)
        }

        if (tpl.isEmpty()) {
            val raw = ok.paramsText()
            QLog.i("Launch", "query 模板为空 → 直传官方 query（${ok.params.size} 个参数）")
            return raw
        }

        var out = tpl
            .replace("{SN}", ok.params[ParseResult.ID_SN] ?: "")
            .replace("{NQT}", ok.params[ParseResult.ID_NQT] ?: "")
            .replace("{ID}", ok.id)
            .replace("{QUERY}", ok.paramsText())

        // 模板写了 isShowAd 才补外层那一个，复刻官方跳转形状
        if (tpl.contains("isShowAd", ignoreCase = true) && !out.trimEnd().endsWith("isShowAd=false")) {
            out = "$out&isShowAd=false"
        }
        return out
    }

    /**
     * 拼 alipays://platformapi/startapp?appId=..&page=..&query=..
     *
     * page 需要 URL 编码（小程序页面路径含 / 与 ?）。
     * query 整体编码一次（它自己内部还有 & 和 =）。
     *
     * 注意：外层 &isShowAd=false 由 [buildQuery] 按模板决定（模板里出现 isShowAd
     * 才补外层那一个），这里不再无条件追加，这样用户清空模板时不会凭空多出参数。
     */
    private fun buildAlipayScheme(appId: String, page: String, query: String): String {
        val encPage = Uri.encode(page, "-_.!~*'()")   // 保留 path 分隔符
        val encQuery = Uri.encode(query)
        return "alipays://platformapi/startapp" +
            "?appId=$appId" +
            "&page=$encPage" +
            "&query=$encQuery"
    }

    // ---------------------------------------------------------------- 策略 C

    /**
     * C：内置 WebView 打开原始 URL，把页面里可能出现的
     *    alipays:// / alipay:// 等 scheme 外抛给支付宝 App。
     *
     * 这一档不解析、不注入、不修改页面任何内容，只是当个浏览器。
     *
     * 对形态②特别重要：机身码外层是第三方跳转包装，包装页自己知道该往哪跳，
     * 让它走完是最不容易出错的做法。
     */
    private fun tryC(activity: Activity, ok: ParseResult.Ok, debugMode: Boolean): AttemptResult {
        val intent = WebViewActivity.newIntent(activity, ok.launchUrl)

        if (debugMode) {
            return AttemptResult.PlannedDryRun(
                STRATEGY_C, intent, WebViewActivity::class.java.name,
                "动作=startActivity(WebViewActivity, ${ok.launchUrl})；UA伪装=${Prefs.useAlipayUa(activity)}"
            )
        }

        val resolved = resolve(activity, intent)
        return start(activity, intent, STRATEGY_C, resolved)
    }

    // ---------------------------------------------------------------- 公共

    /**
     * resolveActivity 封装。Android 11+ 需要 manifest 里的 <queries>，
     * 已在 AndroidManifest.xml 中声明 https / alipays / 支付宝包名。
     */
    private fun resolve(activity: Activity, intent: Intent): String? {
        return try {
            @Suppress("DEPRECATION")
            val ri = activity.packageManager.resolveActivity(
                intent, android.content.pm.PackageManager.MATCH_DEFAULT_ONLY
            )
            ri?.activityInfo?.let { "${it.packageName}/${it.name}" }
        } catch (t: Throwable) {
            QLog.e("Launch", "resolveActivity 抛异常", t)
            null
        }
    }

    private fun start(activity: Activity, intent: Intent, label: String, resolved: String?): AttemptResult {
        return try {
            QLog.i("Launch", "$label 即将 startActivity: ${dumpIntent(intent)}")
            activity.startActivity(intent)
            QLog.i("Launch", "$label startActivity 返回正常")
            AttemptResult.Started(label, intent, resolved)
        } catch (t: ActivityNotFoundException) {
            QLog.e("Launch", "$label ActivityNotFoundException", t)
            AttemptResult.Failed(label, "ActivityNotFoundException: ${t.message}")
        } catch (t: SecurityException) {
            QLog.e("Launch", "$label SecurityException（可能被系统/厂商限制）", t)
            AttemptResult.Failed(label, "SecurityException: ${t.message}")
        } catch (t: Throwable) {
            QLog.e("Launch", "$label 未预期异常", t)
            AttemptResult.Failed(label, "${t.javaClass.simpleName}: ${t.message}")
        }
    }

    /** 把 Intent 关键字段拍平成一行，日志可读且够诊断。 */
    fun dumpIntent(i: Intent): String {
        val data = i.dataString ?: "null"
        val comp = i.component?.flattenToShortString() ?: "null"
        val pkg = i.`package` ?: "null"
        val cats = i.categories?.joinToString(",") ?: "[]"
        val flags = "0x${Integer.toHexString(i.flags)}"
        return "action=${i.action} data=$data pkg=$pkg component=$comp categories=$cats flags=$flags"
    }

    /** 兜底：把 URL 放进剪贴板并提示，用户可手动粘贴到浏览器/支付宝。 */
    fun copyToClipboard(activity: Activity, url: String) {
        try {
            val cm = activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            cm.setPrimaryClip(ClipData.newPlainText("QrJump URL", url))
            QLog.i("Launch", "已将 URL 复制到剪贴板：$url")
            Toast.makeText(
                activity,
                "无法唤起，已复制链接到剪贴板\n请手动粘贴到浏览器或支付宝打开",
                Toast.LENGTH_LONG
            ).show()
        } catch (t: Throwable) {
            QLog.e("Launch", "复制剪贴板失败", t)
            Toast.makeText(activity, "无法唤起，且复制失败：${t.message}", Toast.LENGTH_LONG).show()
        }
    }
}
``````

## app/src/main/java/com/local/qrjump/ParseResult.kt

```kotlin
package com.local.qrjump

import java.net.URI
import java.net.URLDecoder

/**
 * 扫码结果的解析与严格校验。
 *
 * ── 为什么有「两种形态」──
 * 真机上遇到了两种机身码，它们指向同一个官方分发页，但参数完全不同：
 *
 *   形态①（饮水器）
 *     https://h5.qiekj.com/skip?SN=<你的15位SN>
 *     query = { SN }，值是 15 位纯数字
 *
 *   形态②（洗澡热水控制）
 *     https://link.wtturl.cn/?app_id=1128&...&target=<URL编码后的内层地址>
 *     解码 target 后 = https://h5.qiekj.com/skip?Company=qiekj&CommunicateType=1&Ver=4&NQT=<UUID>
 *     query = { Company, CommunicateType, Ver, NQT }，标识是 NQT（UUID）
 *
 * 所以校验不能只认 SN。改成分两层：
 *
 *   A. 「解包」——把码还原到 h5.qiekj.com/skip
 *      允许两种入口：直接是官方域名；或是指定的一家跳转包装域名（形态②的
 *      link.wtturl.cn），此时取它的 target 参数继续解，最多解 4 层。
 *      **包装层的 target 解出来之后仍然要过后面全部校验**，
 *      所以"套个壳就能让我打开任意地址"是不成立的。
 *
 *   B. 「校验」——只看解包后的内层地址
 *      1. scheme 恰为 https
 *      2. host 恰为 h5.qiekj.com（精确相等，不做后缀匹配，挡掉 h5.qiekj.com.evil.tld）
 *      3. path 恰为 /skip
 *      4. query 至少有一个参数，无重复 key，值里不含控制字符
 *      5. 必须含一个已知标识：SN（纯数字）或 NQT（UUID 形状）
 *
 * 刻意不做的事：
 *   - 不"纠正" http→https 之类的错误协议（宁可拒绝，也不猜用户意图）
 *   - 不跟随 HTTP 重定向（只看字符串，不发请求）
 *   - 不硬编码任何设备号，标识一律运行时从码里取
 *   - 不因为"看起来像我们的设备"就放行未知域名
 */
sealed class ParseResult {

    /**
     * 校验通过。
     *
     * @param launchUrl 给策略 A / C 用：真正要丢给浏览器或官方分发的地址。
     *                  形态②下它是**原始包装地址**（保持机上印的那串不动，
     *                  因为包装层可能带一次性参数，重拼反而可能失效）。
     * @param innerUrl  解包并规范化后的 h5.qiekj.com/skip 地址（一定是官方域名）。
     * @param id        设备标识的值：形态①是 15 位数字，形态②是 UUID 字符串。
     * @param idKind    标识种类，[ID_SN] 或 [ID_NQT]。
     * @param params    内层地址的全部 query 参数（有序），给策略 B 拼 query 用。
     */
    data class Ok(
        val launchUrl: String,
        val innerUrl: String,
        val id: String,
        val idKind: String,
        val params: LinkedHashMap<String, String>
    ) : ParseResult() {

        /** 形如 "SN=<你的15位SN>" 或 "Company=qiekj&...&NQT=..." 的可读串。 */
        fun paramsText(): String = params.entries.joinToString("&") { "${it.key}=${it.value}" }
    }

    /** 校验不通过，reason 是给日志和 UI 看的原因。 */
    data class Reject(val reason: String) : ParseResult()

    companion object {
        const val EXPECTED_HOST = "h5.qiekj.com"
        const val EXPECTED_PATH = "/skip"

        const val ID_SN = "SN"
        const val ID_NQT = "NQT"

        /**
         * 允许的「跳转包装」域名。
         *
         * 目前只有真机上实际观察到的 link.wtturl.cn（第三方短链/安全跳转服务，
         * 形态②的机身码就是它）。遇到新的一层包装时，把域名加进来即可，
         * 但**加进来不等于放行**：它的 target 解出来后仍要过全套校验。
         */
        val DELEGATE_HOSTS: Set<String> = setOf("link.wtturl.cn")

        /** 单条码内容上限。形态②的包装地址实测约 600 字符，留足余量。 */
        private const val MAX_TEXT = 4096

        /** 最多解几层包装，防构造环形跳转把解析拖死。 */
        private const val MAX_UNWRAP = 4

        /** 标识格式。SN：8~32 位纯数字；NQT：标准 UUID 形状。 */
        private val SN_PATTERN = Regex("^[0-9]{8,32}$")
        private val NQT_PATTERN = Regex(
            "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-" +
                "[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$"
        )

        /** 参数值里不允许出现的控制字符。 */
        private val CONTROL_CHARS = Regex("[\\u0000-\\u001F\\u007F]")

        /** 参数值的长度上限，防超长串塞爆日志与 Intent。 */
        private const val MAX_VALUE_LEN = 256

        fun parse(rawText: String?): ParseResult {
            val text = rawText?.trim().orEmpty()
            if (text.isEmpty()) return ParseResult.Reject("扫码内容为空")
            if (text.length > MAX_TEXT) {
                return ParseResult.Reject("内容过长（${text.length} 字符），疑似非设备码")
            }

            // ── 第一步：解包，还原到官方内层地址 ──
            var current = text
            var depth = 0
            while (true) {
                if (depth > MAX_UNWRAP) {
                    return ParseResult.Reject("跳转包装超过 $MAX_UNWRAP 层，拒绝继续解")
                }

                val uri = try {
                    URI(current)
                } catch (e: Throwable) {
                    return ParseResult.Reject("不是合法 URI: ${QLog.describe(e)}")
                }

                // 1. scheme 必须在每一层都成立
                val scheme = uri.scheme
                if (scheme != "https") {
                    return ParseResult.Reject("scheme 不是 https（实际=${scheme ?: "null"}）")
                }

                val host = uri.host
                if (host.isNullOrEmpty()) {
                    return ParseResult.Reject("URL 没有 host")
                }

                if (host.equals(EXPECTED_HOST, ignoreCase = true)) {
                    // 到官方域名了，接下来做完整校验
                    return validateInner(uri, launchUrl = if (depth == 0) null else text, depth = depth)
                }

                if (DELEGATE_HOSTS.any { it.equals(host, ignoreCase = true) }) {
                    if (uri.path != null && uri.path != "/" && uri.path != "") {
                        return ParseResult.Reject("包装地址的 path 不是 /（实际=${uri.path}）")
                    }
                    val target = queryParam(uri.rawQuery, "target")
                        ?: return ParseResult.Reject("包装地址 $host 里没有 target 参数")
                    QLog.i("Parse", "解包第 ${depth + 1} 层（$host）→ $target")
                    current = target
                    depth++
                    continue
                }

                return ParseResult.Reject("host 不在允许列表（实际=$host，允许=$EXPECTED_HOST 或 ${DELEGATE_HOSTS.joinToString("/")}）")
            }
        }

        /**
         * 对已经落在官方域名上的内层地址做全部校验。
         *
         * @param launchUrl 非 null 表示这次是解包出来的（形态②），
         *                  A/C 档要用原始包装地址而不是重拼后的内层地址。
         */
        private fun validateInner(uri: URI, launchUrl: String?, depth: Int): ParseResult {
            // 3. path 精确相等
            val path = uri.path
            if (path != EXPECTED_PATH) {
                return ParseResult.Reject("path 不匹配（实际=${path ?: "null"}，期望=$EXPECTED_PATH）")
            }

            // 4. query 解析
            val rawQuery = uri.rawQuery
            if (rawQuery.isNullOrEmpty()) {
                return ParseResult.Reject("缺少 query")
            }

            val params = LinkedHashMap<String, String>()
            for (pair in rawQuery.split("&")) {
                if (pair.isEmpty()) continue
                val idx = pair.indexOf('=')
                if (idx < 0) {
                    return ParseResult.Reject("query 段缺少 '='：$pair")
                }
                val k: String
                val v: String
                try {
                    k = URLDecoder.decode(pair.substring(0, idx), "UTF-8")
                    v = URLDecoder.decode(pair.substring(idx + 1), "UTF-8")
                } catch (e: Throwable) {
                    return ParseResult.Reject("query 解码失败: ${QLog.describe(e)}")
                }
                if (k.isEmpty()) return ParseResult.Reject("query 里有空 key")
                if (params.containsKey(k)) {
                    return ParseResult.Reject("query 出现重复参数：$k")
                }
                if (v.length > MAX_VALUE_LEN) {
                    return ParseResult.Reject("参数 $k 过长（${v.length} 字符）")
                }
                if (CONTROL_CHARS.containsMatchIn(k) || CONTROL_CHARS.containsMatchIn(v)) {
                    return ParseResult.Reject("参数 $k 含控制字符")
                }
                params[k] = v
            }

            if (params.isEmpty()) return ParseResult.Reject("query 没有任何参数")

            // 5. 必须有一个已知标识
            val sn = params[ID_SN]
            val nqt = params[ID_NQT]

            val id: String
            val idKind: String
            when {
                sn != null && SN_PATTERN.matches(sn) -> {
                    id = sn
                    idKind = ID_SN
                }
                nqt != null && NQT_PATTERN.matches(nqt) -> {
                    id = nqt
                    idKind = ID_NQT
                }
                sn != null -> return ParseResult.Reject("SN 不是纯数字或长度异常（len=${sn.length}）")
                nqt != null -> return ParseResult.Reject("NQT 不是 UUID 形状（len=${nqt.length}）")
                else -> return ParseResult.Reject(
                    "query 里既没有 SN 也没有 NQT（实际参数=${params.keys}）"
                )
            }

            // 规范化内层地址：用校验过的 host/path/params 重拼，
            // 丢掉 fragment 与 host 大小写差异。
            val normalized = StringBuilder().apply {
                append("https://").append(EXPECTED_HOST).append(EXPECTED_PATH).append('?')
                var first = true
                for ((k, v) in params) {
                    if (!first) append('&')
                    first = false
                    append(k).append('=').append(v)
                }
            }.toString()

            if (depth > 0) {
                QLog.i("Parse", "已解包到官方内层地址（$depth 层包装）：$normalized")
            }

            // 形态②的 launchUrl 用原始包装串；形态①用规范化后的内层串
            return ParseResult.Ok(
                launchUrl = launchUrl ?: normalized,
                innerUrl = normalized,
                id = id,
                idKind = idKind,
                params = params
            )
        }

        /** 从 rawQuery 里取单个参数（已 URL 解码），取不到返回 null。 */
        private fun queryParam(rawQuery: String?, key: String): String? {
            if (rawQuery.isNullOrEmpty()) return null
            for (pair in rawQuery.split("&")) {
                if (pair.isEmpty()) continue
                val idx = pair.indexOf('=')
                if (idx < 0) continue
                val k = URLDecoder.decode(pair.substring(0, idx), "UTF-8")
                if (k == key) {
                    return URLDecoder.decode(pair.substring(idx + 1), "UTF-8")
                }
            }
            return null
        }

        /**
         * 设置页预览/测试用：把设置里填的东西变成一个可用的 [Ok]。
         *
         * - 填的是整条 URL（含包装层）→ 走完整 [parse]
         * - 只填了裸标识        → 按形态①合成 `?SN=<值>`
         */
        fun fromUserInput(input: String): ParseResult {
            val t = input.trim()
            if (t.isEmpty()) return ParseResult.Reject("输入为空")
            if (t.startsWith("http://", true) || t.startsWith("https://", true)) {
                return parse(t)
            }
            if (!SN_PATTERN.matches(t) && !NQT_PATTERN.matches(t)) {
                return ParseResult.Reject("既不是 URL，也不是合法的 SN（纯数字）或 NQT（UUID）")
            }
            val params = LinkedHashMap<String, String>()
            val kind = if (SN_PATTERN.matches(t)) ID_SN else ID_NQT
            params[kind] = t
            val url = "$EXPECTED_HOST_PREFIX$EXPECTED_PATH?$kind=$t"
            return ParseResult.Ok(
                launchUrl = url,
                innerUrl = url,
                id = t,
                idKind = kind,
                params = params
            )
        }

        private const val EXPECTED_HOST_PREFIX = "https://h5.qiekj.com"
    }
}
``````

## app/src/main/java/com/local/qrjump/Prefs.kt

```kotlin
package com.local.qrjump

import android.content.Context
import android.content.SharedPreferences

/**
 * 设置项集中管理。全部用 SharedPreferences，不引入 DataStore，减少构建面。
 *
 * ── 默认策略为什么是 B 而不是 A ──
 * 真机实测：用 ACTION_VIEW 打开 https://h5.qiekj.com/skip?SN=...，
 * Edge 落到的是「胖乖生活 App 下载引导页」，既不是设备页也不提示用支付宝打开。
 * 而 B 档（alipays:// 直连）实测能一步进到官方小程序设备页。所以默认 B。
 * 但注意：B 的这个结论**只在形态①（SN 参数）的设备上验证过**。
 *
 * ── 两种设备形态 ──
 * 形态①饮水器：  .../skip?SN=<15位数字>
 * 形态②热水控制：.../skip?Company=qiekj&CommunicateType=1&Ver=4&NQT=<UUID>
 * 形态②在机上还多套了一层 link.wtturl.cn 的包装。
 * 形态②下 B 档的 query 曾按「直传机身码里那 4 个参数」拼，实测会被支付宝判
 * 「二维码有误」；2026-11-06 20:09 起改为与形态①同一套外层参数、只换标识键名
 * （masterSwitch=0&keepUsing=1&NQT=<UUID>&isShowAd=false），实测同样直达设备页。
 * 所以现在两种形态都是 B 优先、C 兜底。
 */
object Prefs {

    /** 偏好文件名。注意：[DeviceStore] 也用同一个文件，所以这里不能是 private。 */
    const val FILE = "qrjump_prefs"

    const val KEY_STRATEGY = "strategy"                 // "A" / "B" / "C"
    const val KEY_DEBUG_MODE = "debug_mode"             // Boolean，默认 false
    const val KEY_AUTO_FALLBACK = "auto_fallback"       // Boolean，默认 true
    const val KEY_AUTO_UNLOCK = "auto_unlock"           // Boolean，默认 false（自动点「立即使用」）
    const val KEY_AUTO_UNLOCK_DELAY = "auto_unlock_delay" // Int，**秒**，默认 8
    /** 认出设备页后等多久才点：默认 8 秒，允许 0–30 秒。 */
    const val DEFAULT_AUTO_UNLOCK_DELAY_SEC = 8
    const val MAX_AUTO_UNLOCK_DELAY_SEC = 30
    const val KEY_A11Y_CONNECTED = "a11y_connected"      // Boolean，无障碍服务连上时写的回执
    const val KEY_A11Y_PID = "a11y_pid"                  // Int，写回执时的进程号（换进程即作废）
    // 策略 B 的参数。留空则该档判为不可用，自动降级。
    const val KEY_ALIPAY_APP_ID = "alipay_app_id"       // 小程序 appId，留空=未配置
    const val KEY_ALIPAY_PAGE = "alipay_page"           // 小程序页面路径，留空=未配置
    const val KEY_ALIPAY_QUERY = "alipay_query_tpl"     // 形态①的 query 模板，含 {SN}
    const val KEY_ALIPAY_QUERY_NQT = "alipay_query_nqt" // 形态②的 query 模板，含 {NQT}

    /**
     * 下面三个默认值不是猜的，是**观察到的**：
     * 用策略 C（内置 WebView + 支付宝 UA）打开原始 URL 时，官方分发页自己会发起一次
     *   alipays://platformapi/startapp?appId=...&page=...&query=...
     * 跳转，我们只是把这个「官方页面自己产生的 Intent」原样打印到 logcat 并接手执行。
     * 没有抓包、没有逆向接口、没有任何伪造参数 —— 参数全部由官方页面给出。
     *
     * 即便如此，它们仍只是「默认预填值」：设置页可以随时清空或改写；
     * 清空 appId/page 后策略 B 自动判为不可用，退化成只用 A/C。
     */
    const val DEFAULT_APP_ID = "2018072460764274"
    const val DEFAULT_PAGE = "pages/skipNew/skipNew?isShowAd=false"

    /**
     * 形态①（SN）的 query 模板。{SN} 会被替换成运行时解析出来的数字设备号。
     *
     * 实测教训：只带 SN（query=SN%3Dxxx）会被小程序判「二维码有误，换一个试试」；
     * 必须连 masterSwitch / keepUsing 一起带上才能直达设备页。
     * 所以这里保留官方页面给出的完整形状，而不是精简成单个参数。
     */
    const val DEFAULT_QUERY_TEMPLATE = "masterSwitch=0&keepUsing=1&SN={SN}&isShowAd=false"

    /**
     * 形态②（NQT）的 query 模板。{NQT} 会被替换成运行时解析出来的设备 UUID。
     *
     * 实测教训（2026-11-06 20:07）：把机身码里官方那组
     *   Company=qiekj&CommunicateType=1&Ver=4&NQT=<UUID>
     * 原样直传，小程序落的是「读取设备 → 二维码有误，换一个试试」。
     * 换成和形态①同形状、只把 SN 换成 NQT 的模板后
     *   masterSwitch=0&keepUsing=1&NQT=<UUID>&isShowAd=false
     * 直接落到热水设备页（某热水机 201 / XX学院 / 小票余额 46.66 元）。
     * 所以两个形态共用同一套外层参数，区别只在标识键名。
     */
    const val DEFAULT_QUERY_TEMPLATE_NQT = "masterSwitch=0&keepUsing=1&NQT={NQT}&isShowAd=false"

    // ── 「扫过一次，以后一键直达」用的记忆槽 ──
    // 存的是上一次通过校验的设备码解析结果。它只是"我记得这台机器是哪台"，
    // 不含任何 token / 签名 / 登录态，也不代替官方鉴权。
    const val KEY_LAST_URL = "last_launch_url"
    const val KEY_LAST_INNER = "last_inner_url"
    const val KEY_LAST_ID = "last_id"
    const val KEY_LAST_ID_KIND = "last_id_kind"
    const val KEY_LAST_PARAMS = "last_params"
    const val KEY_LAST_TIME = "last_time"

    /**
     * 设备簿（多台）的存储键。里面是一个 JSON 数组，每项含
     * url / inner / id / kind / params / time / alias。
     *
     * 与上面的 KEY_LAST_* 并存：旧版只有单条记忆，[DeviceStore] 首次读不到
     * 数组时会用旧键迁移出一条，保证升级后"上次那台"不会丢。
     */
    const val KEY_DEVICES = "devices"

    const val KEY_ALIPAY_UA = "alipay_ua"               // 策略 C 是否伪装支付宝 UA
    const val KEY_ALIPAY_UA_VALUE = "alipay_ua_value"   // 自定义 UA 文本
    const val KEY_WEBVIEW_AUTO_SCHEME = "webview_auto_scheme"

    /**
     * 支付宝 Android WebView 的典型 UA 片段。
     * 注意：这只是"让 H5 认出请求来自支付宝"的常规手段，
     * 不携带任何身份、token 或登录态，也不绕过任何鉴权。
     */
    val DEFAULT_ALIPAY_UA: String = buildString {
        append("Mozilla/5.0 (Linux; U; Android 14; zh-CN; ")
        append(android.os.Build.MODEL.replace(' ', '_'))
        append(" Build/UP1A.231005.007) AppleWebKit/537.36 (KHTML, like Gecko) ")
        append("Version/4.0 Chrome/69.0.3497.100 Mobile Safari/537.36 ")
        append("AliApp(AP/10.5.96.8000) AlipayDefined(nt:WIFI,ws:412|915|2.75) ")
        append("AlipayClient/10.5.96.8000 Language/zh-Hans Region/CN")
    }

    private fun sp(c: Context): SharedPreferences =
        c.applicationContext.getSharedPreferences(FILE, Context.MODE_PRIVATE)

    fun strategy(c: Context): String = sp(c).getString(KEY_STRATEGY, "B") ?: "B"
    fun setStrategy(c: Context, v: String) = sp(c).edit().putString(KEY_STRATEGY, v).apply()

    fun debugMode(c: Context): Boolean = sp(c).getBoolean(KEY_DEBUG_MODE, false)
    fun setDebugMode(c: Context, v: Boolean) = sp(c).edit().putBoolean(KEY_DEBUG_MODE, v).apply()

    fun autoFallback(c: Context): Boolean = sp(c).getBoolean(KEY_AUTO_FALLBACK, true)
    fun setAutoFallback(c: Context, v: Boolean) = sp(c).edit().putBoolean(KEY_AUTO_FALLBACK, v).apply()

    // appId / page 留空 -> 该档不可用；未写过时用实测默认值预填（可在设置页改）
    fun alipayAppId(c: Context): String =
        (sp(c).getString(KEY_ALIPAY_APP_ID, DEFAULT_APP_ID) ?: "").trim()

    fun setAlipayAppId(c: Context, v: String) = sp(c).edit().putString(KEY_ALIPAY_APP_ID, v.trim()).apply()

    fun alipayPage(c: Context): String =
        (sp(c).getString(KEY_ALIPAY_PAGE, DEFAULT_PAGE) ?: "").trim()

    fun setAlipayPage(c: Context, v: String) = sp(c).edit().putString(KEY_ALIPAY_PAGE, v.trim()).apply()

    fun alipayQueryTemplate(c: Context): String =
        (sp(c).getString(KEY_ALIPAY_QUERY, DEFAULT_QUERY_TEMPLATE) ?: "").trim()

    fun setAlipayQueryTemplate(c: Context, v: String) =
        sp(c).edit().putString(KEY_ALIPAY_QUERY, v.trim()).apply()

    fun alipayQueryTemplateNqt(c: Context): String =
        (sp(c).getString(KEY_ALIPAY_QUERY_NQT, DEFAULT_QUERY_TEMPLATE_NQT) ?: "").trim()

    fun setAlipayQueryTemplateNqt(c: Context, v: String) =
        sp(c).edit().putString(KEY_ALIPAY_QUERY_NQT, v.trim()).apply()

    /**
     * 策略 C 的 UA 伪装，默认开启。
     *
     * 原因是实测出来的：WebView 用默认 UA 打开 /skip 只落到 App 下载引导页；
     * 换上支付宝 UA 后，那个页面才会自己发起 alipays:// 跳转，C 档才有效。
     * 不开启伪装时 C 档基本等于 A 档，没有存在意义。
     */
    fun useAlipayUa(c: Context): Boolean = sp(c).getBoolean(KEY_ALIPAY_UA, true)
    fun setUseAlipayUa(c: Context, v: Boolean) = sp(c).edit().putBoolean(KEY_ALIPAY_UA, v).apply()



    /**
     * 自动点击「立即使用」。**默认关闭**。
     *
     * 关闭时，无障碍服务即使被系统启动也不会动手（AutoUnlock.arm 直接 return），
     * 保证"默认关闭"是彻底的关闭，而不只是界面上的一个勾。
     */
    fun autoUnlock(c: Context): Boolean = sp(c).getBoolean(KEY_AUTO_UNLOCK, false)
    fun setAutoUnlock(c: Context, v: Boolean) = sp(c).edit().putBoolean(KEY_AUTO_UNLOCK, v).apply()

    /**
     * 自动点击前，认出设备页之后再等多久（**秒**）。默认 **8 秒**。
     *
     * ── 为什么要等 ──
     * 2026-11-06 21:04 的 dump 证明：页面还在显示「内容准备中」的时候，
     * 「立即使用」这个节点**已经出现在控件树里了**。如果一看到它就点，
     * 点的是一个还没接上设备状态的页面，官方那边很可能不理、或者报错。
     * 用户实际体感也是「打开网页后加载有延迟」。
     *
     * ── 为什么单位是秒（2026-11-06 22:05 改）──
     * 之前的单位是毫秒、默认 2500，界面上是可读性很差的"2500 毫秒"，
     * 而且当时是两道闸相加（8 秒宽限 + 2.5 秒稳定 = 恒定 10.5 秒既慢又不可预测）。
     * 现在改成一条时钟：**认出设备页起等满这么多秒就点**，可预测、可调。
     * 默认从 2.5 秒提到 8 秒，是按用户「打开速度太慢、大胆往后调」的要求放的，
     * 覆盖楼里信号差、小程序冷启动慢的情况。设置页可调 0–30 秒。
     */
    fun autoUnlockDelaySec(c: Context): Int {
        val raw = sp(c).getInt(KEY_AUTO_UNLOCK_DELAY, DEFAULT_AUTO_UNLOCK_DELAY_SEC)
        // 旧版存的是毫秒（默认 2500）。秒的上限是 30，任何大于它的存量值
        // 都只可能是旧单位，就地换算一次并存回去，避免读出"2500 秒"。
        if (raw > MAX_AUTO_UNLOCK_DELAY_SEC) {
            val sec = (raw / 1000).coerceIn(0, MAX_AUTO_UNLOCK_DELAY_SEC)
            sp(c).edit().putInt(KEY_AUTO_UNLOCK_DELAY, sec).apply()
            return sec
        }
        return raw.coerceIn(0, MAX_AUTO_UNLOCK_DELAY_SEC)
    }

    fun setAutoUnlockDelaySec(c: Context, v: Int) =
        sp(c).edit().putInt(KEY_AUTO_UNLOCK_DELAY, v.coerceIn(0, MAX_AUTO_UNLOCK_DELAY_SEC)).apply()

    /**
     * 无障碍服务是否真的被系统绑上过。
     *
     * 由 [AutoUnlockService.onServiceConnected] 写 true、
     * [AutoUnlockService.onUnbind] 写 false。
     * 这比读 Settings.Secure 可靠：那个 key 普通应用读出来是 null。
     *
     * ── 回执必须绑进程号（2026-11-06 22:55 实测到的缺陷）──────────
     * 一开始只存了一个 Boolean。结果 `am force-stop` 之后授权被系统清空、
     * 服务也死了，但**进程被杀时不会回调 onUnbind**，那个 true 就留在盘上了，
     * 于是设备列表页明明没授权，日志还打 `无障碍服务=true`。
     * 现在连 `android.os.Process.myPid()` 一起存：进程重开之后 pid 变了，
     * 旧回执自动作废，不需要任何代码去清理它。
     */
    fun a11yConnected(c: Context): Boolean =
        sp(c).getBoolean(KEY_A11Y_CONNECTED, false) &&
            sp(c).getInt(KEY_A11Y_PID, -1) == android.os.Process.myPid()

    fun setA11yConnected(c: Context, v: Boolean) =
        sp(c).edit()
            .putBoolean(KEY_A11Y_CONNECTED, v)
            .putInt(KEY_A11Y_PID, if (v) android.os.Process.myPid() else -1)
            .apply()

    fun webviewAutoScheme(c: Context): Boolean = sp(c).getBoolean(KEY_WEBVIEW_AUTO_SCHEME, true)
    fun setWebviewAutoScheme(c: Context, v: Boolean) =
        sp(c).edit().putBoolean(KEY_WEBVIEW_AUTO_SCHEME, v).apply()

    fun customAlipayUa(c: Context): String {
        val v = (sp(c).getString(KEY_ALIPAY_UA_VALUE, "") ?: "").trim()
        return if (v.isEmpty()) DEFAULT_ALIPAY_UA else v
    }

    fun setCustomAlipayUa(c: Context, v: String) =
        sp(c).edit().putString(KEY_ALIPAY_UA_VALUE, v.trim()).apply()
}
``````

## app/src/main/java/com/local/qrjump/QLog.kt

```kotlin
package com.local.qrjump

import android.util.Log

/**
 * 全链路日志。所有输出统一用 TAG = QrJump，方便一条 adb 命令抓全。
 *
 * 分区（area）约定：
 *   Life    —— Activity 生命周期、权限
 *   Scan    —— 相机、解码器、原始扫码内容
 *   Parse   —— URL 校验的每一步结论
 *   Launch  —— 策略选择、resolveActivity、最终 Intent
 *   Web     —— 策略 C 的 WebView 内部事件
 *
 * 过滤命令： adb logcat -s QrJump
 */
object QLog {

    const val TAG = "QrJump"

    fun i(area: String, msg: String) = Log.i(TAG, "[$area] $msg")

    fun w(area: String, msg: String) = Log.w(TAG, "[$area] $msg")

    fun e(area: String, msg: String, tr: Throwable? = null) =
        Log.e(TAG, "[$area] $msg", tr)

    /** 异常统一格式化：类型 + message + 首帧栈，够定位又不刷屏。 */
    fun describe(t: Throwable): String {
        val head = t.stackTrace.firstOrNull()?.let { " @${it.className}.${it.methodName}:${it.lineNumber}" } ?: ""
        return "${t.javaClass.simpleName}: ${t.message}$head"
    }
}
``````

## app/src/main/java/com/local/qrjump/ScanActivity.kt

```kotlin
package com.local.qrjump

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Size
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.core.resolutionselector.AspectRatioStrategy
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.core.resolutionselector.ResolutionStrategy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.MultiFormatReader
import com.google.zxing.PlanarYUVLuminanceSource
import com.google.zxing.common.HybridBinarizer
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

/**
 * 扫码页。**不再自动开相机**：进页面先看到一块提示 + 「点击开始扫码」大按钮，
 * 点了才 bindToLifecycle；扫成功后立刻 unbindAll，避免举着手机时白跑分析。
 *
 * 为什么改成按键触发：
 *   启动页已经是设备列表，扫码是低频动作。原来那版一进页面相机就起、还带个
 *   常驻的权限弹窗，等于每次都为一个不常用的动作付代价（耗电、隐私指示灯常亮、
 *   在暗处晃到别人）。改成显式点击后，相机的生命周期严格等于"这次扫码"。
 *
 * 解码器分两层：
 *   1) 首选 ML Kit（精度高、对焦容忍度好）。
 *   2) ML Kit 不可用或连续失败时，自动切 ZXing core（纯本地，无需 GMS）。
 */
class ScanActivity : AppCompatActivity() {

    private lateinit var previewView: PreviewView
    private lateinit var statusText: TextView
    private lateinit var resultPanel: LinearLayout
    private lateinit var resultDetail: TextView
    private lateinit var progress: ProgressBar
    private lateinit var hintText: TextView
    private lateinit var startPanel: LinearLayout
    private lateinit var btnStartScan: TextView

    private val cameraExecutor = Executors.newSingleThreadExecutor()

    /** 已经处理过一帧就不再处理，直到用户点"继续扫描"。 */
    private val consumed = AtomicBoolean(false)

    /** 相机是否已经由用户点按钮启动过。用来防止重复 bind。 */
    private var cameraStarted = false

    private var cameraProvider: ProcessCameraProvider? = null
    private var mlKitScanner: BarcodeScanner? = null

    /**
     * ML Kit 是否可用。构造函数抛错、或运行期连续失败达到阈值，
     * 都会把它置为 false 并永久切到 ZXing。
     */
    @Volatile
    private var useMlKit = true

    /** 运行期失败计数；达到 [ML_KIT_FAIL_THRESHOLD] 就降级。 */
    private val mlKitFailures = java.util.concurrent.atomic.AtomicInteger(0)

    /** ML Kit 运行期连续失败多少次后降级。取 3 是为了容忍偶发失败。 */
    private val ML_KIT_FAIL_THRESHOLD = 3

    /** ZXing 兜底用的 reader，只在 ML Kit 不可用时使用。 */
    private val zxingReader = MultiFormatReader().apply {
        setHints(
            mapOf(
                DecodeHintType.POSSIBLE_FORMATS to listOf(
                    com.google.zxing.BarcodeFormat.QR_CODE
                ),
                DecodeHintType.TRY_HARDER to true,
                DecodeHintType.CHARACTER_SET to "UTF-8"
            )
        )
    }

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                QLog.i("Life", "相机权限已授予")
                startCamera()
            } else {
                QLog.w("Life", "相机权限被拒绝")
                statusText.text = getString(R.string.msg_need_camera)
                hintText.text = getString(R.string.msg_need_camera_hint)
                Toast.makeText(this, R.string.msg_need_camera, Toast.LENGTH_LONG).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan)

        previewView = findViewById(R.id.previewView)
        statusText = findViewById(R.id.statusText)
        resultPanel = findViewById(R.id.resultPanel)
        resultDetail = findViewById(R.id.resultDetail)
        progress = findViewById(R.id.scanProgress)
        hintText = findViewById(R.id.hintText)
        startPanel = findViewById(R.id.startPanel)
        btnStartScan = findViewById(R.id.btnStartScan)

        QLog.i("Life", "═══ ScanActivity 启动 ═══")
        QLog.i(
            "Life",
            "策略=${Prefs.strategy(this)}  调试模式=${Prefs.debugMode(this)}  " +
                "自动降级=${Prefs.autoFallback(this)}  自动解锁=${Prefs.autoUnlock(this)}"
        )
        QLog.i(
            "Life",
            "设备=${android.os.Build.MANUFACTURER} ${android.os.Build.MODEL} " +
                "Android ${android.os.Build.VERSION.RELEASE} (API ${android.os.Build.VERSION.SDK_INT})"
        )

        // 相机由这个按钮显式启动
        btnStartScan.setOnClickListener {
            QLog.i("Scan", "用户点击「点击开始扫码」")
            startPanel.visibility = View.GONE
            previewView.visibility = View.VISIBLE
            consumed.set(false)
            ensurePermissionThenStart()
        }

        // 返回设备列表
        findViewById<TextView>(R.id.btnScanBack).setOnClickListener {
            QLog.i("Scan", "用户点击返回，退出扫码页")
            finish()
        }

        // 设置入口
        findViewById<TextView>(R.id.btnSettings).setOnClickListener {
            QLog.i("Life", "打开设置页")
            startActivity(android.content.Intent(this, SettingsActivity::class.java))
        }

        // 继续扫描（结果面板上的按钮）
        findViewById<TextView>(R.id.btnRescan).setOnClickListener {
            QLog.i("Scan", "用户点击继续扫描")
            resultPanel.visibility = View.GONE
            hintText.visibility = View.VISIBLE
            previewView.visibility = View.VISIBLE
            statusText.text = getString(R.string.scan_scanning)
            consumed.set(false)
            ensurePermissionThenStart()
        }

        // 重新尝试唤起（用上一次解出的结果）
        findViewById<TextView>(R.id.btnRetryLaunch).setOnClickListener {
            val ok = lastValidOk
            if (ok == null) {
                Toast.makeText(this, R.string.msg_no_last_url, Toast.LENGTH_SHORT).show()
            } else {
                QLog.i("Launch", "用户手动重试唤起：${ok.launchUrl}")
                doLaunch(ok)
            }
        }

        // 调试模式下允许 adb 注入扫码文本：不用举手机对准二维码也能测完整链路。
        consumeInjectedScan()
    }

    /** 调试模式下，允许用 adb 注入一段"假装扫到的文本"，方便不举手机也能测全链路。 */
    private fun consumeInjectedScan() {
        val raw = intent?.getStringExtra(EXTRA_FAKE_SCAN) ?: return
        if (!Prefs.debugMode(this)) {
            QLog.w("Scan", "收到注入的扫码文本，但调试模式未开启，忽略")
            return
        }
        QLog.i("Scan", "收到注入的扫码文本（调试模式）：$raw")
        startPanel.visibility = View.GONE
        onRawText(raw)
    }

    companion object {
        const val EXTRA_FAKE_SCAN = "com.local.qrjump.extra.FAKE_SCAN"
    }

    /** 上一次通过校验的完整结果，供「重新唤起」按钮复用。 */
    private var lastValidOk: ParseResult.Ok? = null

    // ------------------------------------------------------------ 相机

    /** 有权限就直接起；没有就先申请（申请结果回调里再起）。 */
    private fun ensurePermissionThenStart() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED
        ) {
            QLog.i("Life", "相机权限已具备，启动相机")
            startCamera()
        } else {
            QLog.i("Life", "请求相机权限")
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun startCamera() {
        if (cameraStarted) {
            // 已经在跑：只确保预览可见，不重复 bind（重复 bind 会闪一下并重置对焦）
            QLog.i("Scan", "相机已在运行，跳过重复启动")
            statusText.text = getString(R.string.scan_scanning)
            progress.visibility = View.VISIBLE
            return
        }
        cameraStarted = true
        statusText.text = getString(R.string.scan_starting)
        progress.visibility = View.VISIBLE

        val future = ProcessCameraProvider.getInstance(this)
        future.addListener({
            try {
                val provider = future.get()
                cameraProvider = provider
                bindUseCases(provider)
            } catch (t: Throwable) {
                QLog.e("Scan", "相机初始化失败", t)
                statusText.text = getString(R.string.scan_camera_failed, t.message ?: "")
                progress.visibility = View.GONE
                startPanel.visibility = View.VISIBLE
                cameraStarted = false
            }
        }, ContextCompat.getMainExecutor(this))
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun bindUseCases(provider: ProcessCameraProvider) {
        val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(previewView.surfaceProvider)
        }

        // 目标 1280x720、16:9 优先；设备不支持该尺寸时按"最接近且不小于"回退。
        val resolutionSelector = ResolutionSelector.Builder()
            .setAspectRatioStrategy(AspectRatioStrategy.RATIO_16_9_FALLBACK_AUTO_STRATEGY)
            .setResolutionStrategy(
                ResolutionStrategy(
                    Size(1280, 720),
                    ResolutionStrategy.FALLBACK_RULE_CLOSEST_HIGHER_THEN_LOWER
                )
            )
            .build()

        val analysis = ImageAnalysis.Builder()
            .setResolutionSelector(resolutionSelector)
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()

        // 注意：ImageProxy 的关闭职责下移到 analyzeFrame / decodeWithMlKit。
        // ML Kit 是异步的，若在分析线程里 finally 直接 close，会与 ML Kit 的
        // 后台读取竞争（真机上表现为 Internal error / 帧被提前回收）。
        analysis.setAnalyzer(cameraExecutor) { proxy ->
            try {
                if (consumed.get()) {
                    proxy.close()
                    return@setAnalyzer
                }
                analyzeFrame(proxy)
            } catch (t: Throwable) {
                QLog.e("Scan", "分析帧异常", t)
                runCatching { proxy.close() }
            }
        }

        try {
            provider.unbindAll()
            provider.bindToLifecycle(
                this,
                CameraSelector.DEFAULT_BACK_CAMERA,
                preview,
                analysis
            )
            statusText.text = getString(R.string.scan_scanning)
            QLog.i("Scan", "相机已绑定（后置），分析分辨率 1280x720")
        } catch (t: Throwable) {
            QLog.e("Scan", "bindToLifecycle 失败", t)
            statusText.text = getString(R.string.scan_camera_failed, t.message ?: "")
            progress.visibility = View.GONE
            startPanel.visibility = View.VISIBLE
            cameraStarted = false
        }
    }

    /** 扫到之后 / 离开页面时把相机停掉，别让分析线程空转。 */
    private fun stopCamera(reason: String) {
        try {
            cameraProvider?.unbindAll()
            QLog.i("Scan", "相机已解绑（$reason）")
        } catch (t: Throwable) {
            QLog.w("Scan", "unbindAll 异常：${QLog.describe(t)}")
        }
        progress.visibility = View.GONE
        cameraStarted = false
    }

    // ------------------------------------------------------------ 解码

    /**
     * 单帧分析：先试 ML Kit，失败或不可用时用 ZXing。
     * 注意：这里的 ML Kit 是异步回调，ZXing 是同步。两条路径共享 consumed 标志。
     */
    private fun analyzeFrame(proxy: ImageProxy) {
        if (useMlKit) {
            decodeWithMlKit(proxy)
        } else {
            decodeWithZxing(proxy)
        }
    }

    private fun decodeWithMlKit(proxy: ImageProxy) {
        val mediaImage = proxy.image
        if (mediaImage == null) {
            QLog.w("Scan", "ImageProxy.image == null，跳过该帧")
            proxy.close()
            return
        }

        val scanner = synchronized(this) {
            if (mlKitScanner == null && useMlKit) {
                try {
                    val options = BarcodeScannerOptions.Builder()
                        .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                        .build()
                    mlKitScanner = BarcodeScanning.getClient(options)
                    QLog.i("Scan", "ML Kit 解码器初始化成功")
                } catch (t: Throwable) {
                    QLog.e("Scan", "ML Kit 初始化失败，切换到 ZXing 兜底", t)
                    useMlKit = false
                }
            }
            mlKitScanner
        }

        if (scanner == null || !useMlKit) {
            // 本帧改用 ZXing（decodeWithZxing 自己负责 close）
            decodeWithZxing(proxy)
            return
        }

        val image = try {
            InputImage.fromMediaImage(mediaImage, proxy.imageInfo.rotationDegrees)
        } catch (t: Throwable) {
            QLog.e("Scan", "InputImage 构造失败，本帧交 ZXing", t)
            decodeWithZxing(proxy)
            return
        }

        scanner.process(image)
            .addOnCompleteListener {
                // ML Kit 已读完帧缓冲，此时才能安全释放
                runCatching { proxy.close() }
            }
            .addOnSuccessListener { barcodes ->
                if (consumed.get() || barcodes.isEmpty()) return@addOnSuccessListener
                val raw = barcodes.firstNotNullOfOrNull { it.rawValue }
                if (raw != null) {
                    QLog.i("Scan", "ML Kit 解出原始内容：$raw")
                    consumed.set(true)
                    onRawText(raw)
                }
            }
            .addOnFailureListener { t ->
                val n = mlKitFailures.incrementAndGet()
                QLog.w("Scan", "ML Kit 解析失败（第 $n 次）：${QLog.describe(t)}")
                if (n >= ML_KIT_FAIL_THRESHOLD && useMlKit) {
                    synchronized(this) {
                        if (useMlKit) {
                            useMlKit = false
                            QLog.w(
                                "Scan",
                                "ML Kit 连续失败 $n 次，永久降级到 ZXing 兜底" +
                                    "（本机 GMS 可能缺失或不可用）"
                            )
                            runOnUiThread {
                                Toast.makeText(
                                    this,
                                    R.string.toast_mlkit_fallback,
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
                }
            }
    }

    private fun decodeWithZxing(proxy: ImageProxy) {
        try {
            val yPlane = proxy.planes[0]
            val buffer = yPlane.buffer
            val data = ByteArray(buffer.remaining())
            buffer.get(data)

            val width = proxy.width
            val height = proxy.height
            val rowStride = yPlane.rowStride
            val pixelStride = yPlane.pixelStride

            // 有 padding 的机型需要按行重排成紧密排列
            val tight = if (rowStride == width && pixelStride == 1) {
                data
            } else {
                val out = ByteArray(width * height)
                for (row in 0 until height) {
                    val rowStart = row * rowStride
                    for (col in 0 until width) {
                        val p = rowStart + col * pixelStride
                        out[row * width + col] = if (p < data.size) data[p] else 0
                    }
                }
                out
            }

            val source = PlanarYUVLuminanceSource(tight, width, height, 0, 0, width, height, false)
            val bitmap = BinaryBitmap(HybridBinarizer(source))
            val result = zxingReader.decodeWithState(bitmap)
            val text = result.text
            if (text != null && !consumed.get()) {
                QLog.i("Scan", "ZXing 解出原始内容：$text")
                consumed.set(true)
                onRawText(text)
            }
        } catch (t: com.google.zxing.NotFoundException) {
            // 很常见：这一帧没对准。静默忽略，别刷日志。
        } catch (t: Throwable) {
            QLog.w("Scan", "ZXing 解码异常：${QLog.describe(t)}")
        } finally {
            zxingReader.reset()
            // ZXing 是同步的，读完即可释放
            runCatching { proxy.close() }
        }
    }

    // ------------------------------------------------------------ 校验 + 唤起

    /**
     * 拿到原始码内容后的统一处理：校验 → 记录 → 按策略唤起。
     */
    private fun onRawText(raw: String) {
        QLog.i("Parse", "── 开始校验原始内容 ──")
        QLog.i("Parse", "原始码全文：$raw")

        when (val r = ParseResult.parse(raw)) {
            is ParseResult.Reject -> {
                QLog.w("Parse", "校验失败：${r.reason}")
                runOnUiThread {
                    statusText.text = getString(R.string.scan_rejected)
                    hintText.text = r.reason
                    hintText.visibility = View.VISIBLE
                    Toast.makeText(this, getString(R.string.toast_rejected), Toast.LENGTH_SHORT).show()
                }
                // 让它能继续扫：延迟一会儿再放开
                previewView.postDelayed({ consumed.set(false) }, 1500)
            }

            is ParseResult.Ok -> {
                QLog.i("Parse", "校验通过 launchUrl=${r.launchUrl}")
                QLog.i("Parse", "  形态=${r.idKind}  标识=${r.id}")
                QLog.i("Parse", "  内层 URL=${r.innerUrl}")
                QLog.i("Parse", "  参数=${r.paramsText()}")
                lastValidOk = r
                // 记住这台设备：下次打开 App 就在列表里，不必再对准机身码
                DeviceStore.remember(this, r)
                // 已解出，停相机省电（也防止误扫旁边第二张码）
                stopCamera("已解出二维码")
                runOnUiThread { showResult(r) }
                doLaunch(r)
            }
        }
    }

    private fun showResult(ok: ParseResult.Ok) {
        resultPanel.visibility = View.VISIBLE
        previewView.visibility = View.INVISIBLE
        hintText.visibility = View.GONE
        resultDetail.text = buildString {
            append(getString(R.string.result_id_kind, ok.idKind, ok.id))
            append("\n\n")
            append(getString(R.string.result_url, ok.launchUrl))
            append("\n\n")
            append(getString(R.string.result_params, ok.paramsText()))
            append("\n\n")
            append(getString(R.string.result_strategy, Prefs.strategy(this@ScanActivity)))
            if (Prefs.debugMode(this@ScanActivity)) {
                append("\n")
                append(getString(R.string.result_debug_on))
            }
        }
    }

    private fun doLaunch(ok: ParseResult.Ok) {
        val strategy = Prefs.strategy(this)
        val debug = Prefs.debugMode(this)
        val autoFb = Prefs.autoFallback(this)

        QLog.i(
            "Launch",
            "── 开始唤起 ── 形态=${ok.idKind} 标识=${ok.id} 策略=$strategy 调试=$debug 降级=$autoFb"
        )
        QLog.i("Launch", "launchUrl=${ok.launchUrl}")

        val r = Launcher.launch(this, ok, strategy, debug, autoFb)

        runOnUiThread {
            when (r) {
                is AttemptResult.Started -> {
                    QLog.i("Launch", "✓ 已发起：${r.strategy} → ${r.resolvedComponent}")
                    statusText.text = getString(R.string.status_started, r.strategy)

                    // 进入自动解锁的待命窗口。开关关着时 arm 内部会直接返回，
                    // 所以这里不需要再判一次设置。
                    AutoUnlock.arm(this)

                    // 关键：只有当唤起目标**不在本应用内**时才退到后台。
                    //
                    // 策略 C 拉起的是本应用自己的 WebViewActivity，它和本页在同一个
                    // 任务栈里。此时 moveTaskToBack(true) 会把整个任务（含 WebView）
                    // 一起退到后台，页面随即被系统降频/挂起，官方分发页交付的
                    // alipays:// 跳转就发不出来 —— 表现为"停在 WebView 什么都不发生"。
                    // 实测复现：19:18:54 一键直达 → C → onPageFinished 后一直没有
                    // "[Web] 拦截跳转"，最终落回桌面。
                    val handledInsideApp = r.resolvedComponent
                        ?.startsWith(packageName + "/") == true
                    if (handledInsideApp) {
                        QLog.i("Life", "唤起目标在本应用内（$handledInsideApp），保持前台，等页面自行跳转")
                    } else {
                        previewView.postDelayed({
                            if (!isFinishing) {
                                QLog.i("Life", "唤起完成后将 App 退到后台")
                                moveTaskToBack(true)
                            }
                        }, 1200)
                    }
                }

                is AttemptResult.PlannedDryRun -> {
                    QLog.i("Launch", "⚠ 调试模式：未真正唤起（${r.strategy}）")
                    statusText.text = getString(R.string.status_dryrun, r.strategy)
                    Toast.makeText(
                        this,
                        getString(R.string.toast_dryrun),
                        Toast.LENGTH_LONG
                    ).show()
                    // 调试模式下也照样进待命窗口（前提是自动解锁开关开着），
                    // 这样能在**不真的唤起**的前提下单独验证「自动点击」那条链：
                    // 用 adb 手动 am start 官方 URI 把设备页打开，无障碍服务就能走到
                    // "找到按钮但不点"这一层并留下日志 —— 服务侧同样判 debugMode，
                    // 调试模式下它绝不会真的 performAction。
                    AutoUnlock.arm(this)
                    // 调试模式下不退出，停在结果页方便看日志
                }

                is AttemptResult.Unavailable -> {
                    statusText.text = getString(R.string.status_failed, r.reason)
                }

                is AttemptResult.Failed -> {
                    statusText.text = getString(R.string.status_failed, r.reason)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        QLog.i("Life", "onResume（当前策略=${Prefs.strategy(this)} 调试=${Prefs.debugMode(this)}）")
    }

    override fun onPause() {
        super.onPause()
        // 用户切走时停相机：不留预览在后台空跑
        if (cameraStarted) {
            stopCamera("onPause")
            if (!consumed.get()) {
                startPanel.visibility = View.VISIBLE
                previewView.visibility = View.INVISIBLE
                statusText.text = getString(R.string.scan_stopped)
            }
        }
    }

    override fun onDestroy() {
        QLog.i("Life", "onDestroy，释放解码器与相机")
        try {
            mlKitScanner?.close()
        } catch (t: Throwable) {
            QLog.w("Scan", "关闭 ML Kit 异常：${QLog.describe(t)}")
        }
        try {
            cameraProvider?.unbindAll()
        } catch (t: Throwable) {
            QLog.w("Scan", "unbindAll 异常：${QLog.describe(t)}")
        }
        cameraExecutor.shutdown()
        super.onDestroy()
    }
}
``````

## app/src/main/java/com/local/qrjump/SettingsActivity.kt

```kotlin
package com.local.qrjump

import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

/**
 * 设置页。所有会影响唤起行为的开关都在这里，方便逐个试。
 */
class SettingsActivity : AppCompatActivity() {

    private lateinit var rgStrategy: RadioGroup
    private lateinit var rbA: RadioButton
    private lateinit var rbB: RadioButton
    private lateinit var rbC: RadioButton
    private lateinit var cbDebug: CheckBox
    private lateinit var cbAutoFallback: CheckBox
    private lateinit var cbAutoUnlock: CheckBox
    private lateinit var etAppId: EditText
    private lateinit var etPage: EditText
    private lateinit var etQuery: EditText
    private lateinit var etQueryNqt: EditText
    private lateinit var etSn: EditText
    private lateinit var cbAlipayUa: CheckBox
    private lateinit var etUa: EditText
    private lateinit var cbWebScheme: CheckBox
    private lateinit var tvResolve: TextView
    private lateinit var tvAutoDelay: TextView
    private lateinit var sbAutoDelay: SeekBar
    private lateinit var tvA11yStatus: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        rgStrategy = findViewById(R.id.rgStrategy)
        rbA = findViewById(R.id.rbA)
        rbB = findViewById(R.id.rbB)
        rbC = findViewById(R.id.rbC)
        cbDebug = findViewById(R.id.cbDebug)
        cbAutoFallback = findViewById(R.id.cbAutoFallback)
        cbAutoUnlock = findViewById(R.id.cbAutoUnlock)
        etAppId = findViewById(R.id.etAppId)
        etPage = findViewById(R.id.etPage)
        etQuery = findViewById(R.id.etQuery)
        etQueryNqt = findViewById(R.id.etQueryNqt)
        etSn = findViewById(R.id.etSn)
        cbAlipayUa = findViewById(R.id.cbAlipayUa)
        etUa = findViewById(R.id.etUa)
        cbWebScheme = findViewById(R.id.cbWebScheme)
        tvResolve = findViewById(R.id.tvResolve)
        tvAutoDelay = findViewById(R.id.tvAutoDelay)
        sbAutoDelay = findViewById(R.id.sbAutoDelay)

        loadIntoViews()

        rgStrategy.setOnCheckedChangeListener { _, id ->
            val s = when (id) {
                R.id.rbB -> Launcher.STRATEGY_B
                R.id.rbC -> Launcher.STRATEGY_C
                else -> Launcher.STRATEGY_A
            }
            Prefs.setStrategy(this, s)
            QLog.i("Life", "设置：策略切换为 $s")
            refreshResolveInfo()
        }

        cbDebug.setOnCheckedChangeListener { _, checked ->
            Prefs.setDebugMode(this, checked)
            QLog.i("Life", "设置：调试模式=$checked")
        }

        cbAutoFallback.setOnCheckedChangeListener { _, checked ->
            Prefs.setAutoFallback(this, checked)
            QLog.i("Life", "设置：自动降级=$checked")
        }

        cbAutoUnlock.setOnCheckedChangeListener { _, checked ->
            Prefs.setAutoUnlock(this, checked)
            QLog.i("Life", "设置：自动解锁=$checked 无障碍服务=${AutoUnlock.isServiceEnabled(this)}")
            if (checked && !AutoUnlock.isServiceEnabled(this)) {
                // 只是提醒，不强行跳转：用户可能正在改别的设置
                Toast.makeText(this, R.string.toast_accessibility_needed, Toast.LENGTH_LONG).show()
            }
        }

        // 等待滑杆：进度 0–30，每格 1 秒。默认 8 秒。
        sbAutoDelay.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(bar: SeekBar?, progress: Int, fromUser: Boolean) {
                val sec = progress * DELAY_STEP_SEC
                tvAutoDelay.text = getString(R.string.settings_auto_delay, sec)
                if (fromUser) {
                    // 拖一下就落盘，不用等"保存"按钮：这个值用户在试的时候会反复调
                    Prefs.setAutoUnlockDelaySec(this@SettingsActivity, sec)
                    QLog.i("Life", "设置：自动点击等待=${sec} 秒")
                }
            }

            override fun onStartTrackingTouch(bar: SeekBar?) = Unit
            override fun onStopTrackingTouch(bar: SeekBar?) = Unit
        })

        findViewById<Button>(R.id.btnOpenAccessibility).setOnClickListener {
            QLog.i("Life", "跳转系统无障碍设置")
            try {
                startActivity(android.content.Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS))
            } catch (t: Throwable) {
                QLog.e("Life", "打开无障碍设置失败", t)
                Toast.makeText(this, R.string.toast_accessibility_needed, Toast.LENGTH_LONG).show()
            }
        }

        // 自愈：把被系统清掉的无障碍授权写回去（需要 WRITE_SECURE_SETTINGS）
        tvA11yStatus = findViewById(R.id.tvA11yStatus)
        findViewById<Button>(R.id.btnHealAccessibility).setOnClickListener {
            QLog.i("A11y", "设置页点了「尝试自动恢复无障碍授权」")
            val ok = A11yGuard.heal(this)
            refreshA11yStatus()
            val msg = when {
                ok -> getString(R.string.a11y_heal_ok)
                A11yGuard.canWrite(this) -> getString(R.string.a11y_heal_failed)
                else -> getString(R.string.a11y_heal_no_perm)
            }
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
        }
        refreshA11yStatus()

        cbAlipayUa.setOnCheckedChangeListener { _, checked ->
            Prefs.setUseAlipayUa(this, checked)
            QLog.i("Life", "设置：策略C 伪装支付宝UA=$checked")
        }

        cbWebScheme.setOnCheckedChangeListener { _, checked ->
            Prefs.setWebviewAutoScheme(this, checked)
            QLog.i("Life", "设置：策略C 外抛 scheme=$checked")
        }

        findViewById<Button>(R.id.btnSave).setOnClickListener {
            Prefs.setAlipayAppId(this, etAppId.text.toString())
            Prefs.setAlipayPage(this, etPage.text.toString())
            Prefs.setAlipayQueryTemplate(this, etQuery.text.toString())
            Prefs.setAlipayQueryTemplateNqt(this, etQueryNqt.text.toString())
            Prefs.setCustomAlipayUa(this, etUa.text.toString())
            QLog.i(
                "Life",
                "设置已保存：appId=${mask(Prefs.alipayAppId(this))} " +
                    "page=${Prefs.alipayPage(this)} " +
                    "querySN=${Prefs.alipayQueryTemplate(this)} " +
                    "queryNQT=${Prefs.alipayQueryTemplateNqt(this).ifEmpty { "（空=直传官方）" }}"
            )
            Toast.makeText(this, R.string.settings_saved, Toast.LENGTH_SHORT).show()
            refreshResolveInfo()
        }

        findViewById<Button>(R.id.btnTestA).setOnClickListener {
            testLaunch(Launcher.STRATEGY_A)
        }
        findViewById<Button>(R.id.btnTestB).setOnClickListener {
            testLaunch(Launcher.STRATEGY_B)
        }
        findViewById<Button>(R.id.btnTestC).setOnClickListener {
            testLaunch(Launcher.STRATEGY_C)
        }

        findViewById<Button>(R.id.btnShowResolve).setOnClickListener {
            refreshResolveInfo()
        }

        findViewById<Button>(R.id.btnReset).setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle(R.string.settings_reset)
                .setMessage(R.string.settings_reset_confirm)
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    Prefs.setStrategy(this, Launcher.STRATEGY_B)
                    Prefs.setDebugMode(this, false)
                    Prefs.setAutoFallback(this, true)
                    Prefs.setAlipayAppId(this, Prefs.DEFAULT_APP_ID)
                    Prefs.setAlipayPage(this, Prefs.DEFAULT_PAGE)
                    Prefs.setAlipayQueryTemplate(this, Prefs.DEFAULT_QUERY_TEMPLATE)
                    Prefs.setAlipayQueryTemplateNqt(this, Prefs.DEFAULT_QUERY_TEMPLATE_NQT)
                    Prefs.setUseAlipayUa(this, true)
                    Prefs.setCustomAlipayUa(this, "")
                    Prefs.setWebviewAutoScheme(this, true)
                    Prefs.setAutoUnlock(this, false)
                    Prefs.setAutoUnlockDelaySec(this, Prefs.DEFAULT_AUTO_UNLOCK_DELAY_SEC)
                    QLog.i("Life", "设置已重置为默认值（策略 B + 实测 appId/page/query）")
                    loadIntoViews()
                    refreshResolveInfo()
                }
                .setNegativeButton(android.R.string.cancel, null)
                .show()
        }

        refreshResolveInfo()
    }

    private fun loadIntoViews() {
        when (Prefs.strategy(this)) {
            Launcher.STRATEGY_B -> rbB.isChecked = true
            Launcher.STRATEGY_C -> rbC.isChecked = true
            else -> rbA.isChecked = true
        }
        cbDebug.isChecked = Prefs.debugMode(this)
        cbAutoFallback.isChecked = Prefs.autoFallback(this)
        cbAutoUnlock.isChecked = Prefs.autoUnlock(this)
        // 滑杆位置就是秒数：进度 0–30 ↔ 0–30 秒
        val delaySec = Prefs.autoUnlockDelaySec(this).coerceIn(0, Prefs.MAX_AUTO_UNLOCK_DELAY_SEC)
        sbAutoDelay.progress = delaySec / DELAY_STEP_SEC
        tvAutoDelay.text = getString(R.string.settings_auto_delay, delaySec)
        cbAlipayUa.isChecked = Prefs.useAlipayUa(this)
        cbWebScheme.isChecked = Prefs.webviewAutoScheme(this)
        etAppId.setText(Prefs.alipayAppId(this))
        etPage.setText(Prefs.alipayPage(this))
        etQuery.setText(Prefs.alipayQueryTemplate(this))
        etQueryNqt.setText(Prefs.alipayQueryTemplateNqt(this))
        etUa.setText(Prefs.customAlipayUa(this))
        etSn.hint = getString(R.string.settings_sn_hint)
    }

    /**
     * 用设置页里填的内容做测试。支持三种输入：
     *   1) 纯 SN（形态①）：拼成 https://h5.qiekj.com/skip?SN=...
     *   2) 纯 NQT（形态②）：拼成 https://h5.qiekj.com/skip?Company=qiekj&CommunicateType=1&Ver=4&NQT=...
     *   3) 整条二维码链接（原样解析，会走完整校验，含 link.wtturl.cn 解包）
     *
     * 这里只拼字符串 / 解析字符串，**不发任何网络请求**，也不校验设备是否真实存在。
     */
    private fun testLaunch(strategy: String) {
        val input = etSn.text.toString().trim()
        if (input.isEmpty()) {
            Toast.makeText(this, R.string.settings_need_sn, Toast.LENGTH_SHORT).show()
            return
        }

        val parsed = ParseResult.fromUserInput(input)
        if (parsed is ParseResult.Reject) {
            Toast.makeText(
                this,
                getString(R.string.settings_sn_invalid) + "\n" + parsed.reason,
                Toast.LENGTH_LONG
            ).show()
            QLog.w("Launch", "设置页测试输入无效：${parsed.reason}")
            return
        }

        val ok = parsed as ParseResult.Ok
        QLog.i("Launch", "═══ 设置页测试唤起 ═══ 策略=$strategy 形态=${ok.idKind} 标识=${ok.id}")
        QLog.i("Launch", "launchUrl=${ok.launchUrl}")

        val debug = Prefs.debugMode(this)
        val r = Launcher.launch(this, ok, strategy, debug, false)
        val msg = when (r) {
            is AttemptResult.Started -> "已发起：${r.strategy}\ncomponent=${r.resolvedComponent}"
            is AttemptResult.PlannedDryRun -> "调试模式：未唤起\n${r.extra}\ncomponent=${r.resolvedComponent}"
            is AttemptResult.Unavailable -> "不可用：${r.reason}"
            is AttemptResult.Failed -> "失败：${r.reason}"
        }
        tvResolve.text = msg
        QLog.i("Launch", "测试结果：$msg")
    }

    /** 把当前配置下三种 Intent 的 resolveActivity 结果直接显示出来，省得只看日志。 */
    private fun refreshResolveInfo() {
        val input = etSn.text.toString().trim()
        val ok = run {
            val p = if (input.isEmpty()) {
                ParseResult.fromUserInput("000000000000000")
            } else {
                ParseResult.fromUserInput(input)
            }
            p as? ParseResult.Ok
        }

        val sb = StringBuilder()
        val sampleNote = if (ok == null) "（测试输入无效，以下用占位符演示）" else ""
        sb.append(resolveLine("A  https VIEW", buildA(ok?.launchUrl ?: "https://h5.qiekj.com/skip?SN=000000000000000")))
        if (ok != null) {
            sb.append("\n   A 将打开：").append(ok.launchUrl)
        }
        if (sampleNote.isNotEmpty()) sb.append("\n   ").append(sampleNote)

        val appId = Prefs.alipayAppId(this)
        val page = Prefs.alipayPage(this)

        if (appId.isNotEmpty() && page.isNotEmpty()) {
            val q = buildQueryForPreview(ok)
            val b1 = android.content.Intent(
                android.content.Intent.ACTION_VIEW,
                android.net.Uri.parse(
                    "alipays://platformapi/startapp?appId=$appId" +
                        "&page=${android.net.Uri.encode(page, "-_.!~*'()")}" +
                        "&query=${android.net.Uri.encode(q)}"
                )
            )
            sb.append("\n").append(resolveLine("B1 alipays", b1))
            sb.append("\n   B1 的 query = ").append(q.ifEmpty { "（空）" })

            val wrapped = "https://ds.alipay.com/?scheme=" +
                android.net.Uri.encode(b1.dataString ?: "")
            sb.append("\n").append(
                resolveLine(
                    "B2 ds.alipay",
                    android.content.Intent(
                        android.content.Intent.ACTION_VIEW,
                        android.net.Uri.parse(wrapped)
                    )
                )
            )
        } else {
            sb.append("\nB  alipays    —— 未配置 appId/page，本档不可用")
        }

        sb.append("\nC  WebView     —— 本应用内部，无需 resolve")
        sb.append("\n   C 将打开：").append(ok?.launchUrl ?: "（无效输入）")
        sb.append("\n   C 伪装支付宝UA = ").append(Prefs.useAlipayUa(this))

        // 形态②：2026-11-06 20:09 起 B 档也已实测直达，不再提示顺序会变
        if (ok != null && ok.idKind == ParseResult.ID_NQT) {
            sb.append("\n\n注：形态②（NQT）的 B 档已于 2026-11-06 20:09 实测直达热水设备页，")
            sb.append("\n与形态①一样 B 优先，C 作为降级。")
        }

        tvResolve.text = sb.toString()
        QLog.i("Launch", "resolve 面板刷新：\n$sb")
    }

    /** 预览用：按当前形态估一个 query（与 Launcher.buildQuery 的规则保持一致）。 */
    private fun buildQueryForPreview(ok: ParseResult.Ok?): String {
        if (ok == null) return Prefs.alipayQueryTemplate(this).replace("{SN}", "000000000000000")

        val tpl = if (ok.idKind == ParseResult.ID_NQT) {
            Prefs.alipayQueryTemplateNqt(this)
        } else {
            Prefs.alipayQueryTemplate(this)
        }
        if (tpl.isEmpty()) return ok.paramsText()

        var out = tpl
            .replace("{SN}", ok.params[ParseResult.ID_SN] ?: "")
            .replace("{NQT}", ok.params[ParseResult.ID_NQT] ?: "")
            .replace("{ID}", ok.id)
            .replace("{QUERY}", ok.paramsText())
        if (tpl.contains("isShowAd", ignoreCase = true) && !out.trimEnd().endsWith("isShowAd=false")) {
            out = "$out&isShowAd=false"
        }
        return out
    }

    private fun buildA(url: String): android.content.Intent =
        android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(url))

    private fun resolveLine(label: String, i: android.content.Intent): String {
        val ri = packageManager.resolveActivity(
            i, android.content.pm.PackageManager.MATCH_DEFAULT_ONLY
        )
        val target = ri?.activityInfo?.let { "${it.packageName}/${it.name}" } ?: "null（无接收方）"
        return "$label -> $target"
    }

    /** 日志里别把 appId 全打出来，留头尾即可。 */
    private fun mask(s: String): String =
        if (s.length <= 6) s else "${s.take(3)}***${s.takeLast(3)}"

    /** 把无障碍授权的真实状态（含能不能自愈）显示出来，别让用户猜。 */
    private fun refreshA11yStatus() {
        val line = A11yGuard.statusLine(this)
        tvA11yStatus.text = line
        QLog.i("A11y", "设置页状态：$line")
    }

    private companion object {
        /** 稳定等待滑杆每格 1 秒，进度 0–30 对应 0–30 秒。 */
        const val DELAY_STEP_SEC = 1
    }
}
``````

## app/src/main/java/com/local/qrjump/ShortcutActivity.kt

```kotlin
package com.local.qrjump

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

/**
 * 桌面快捷方式 / 长按图标快捷方式的落点。
 *
 * ── 它做什么 ──
 * 只做一件事：把快捷方式带来的**设备簿键**（`SN|<你的15位SN>` 这种）
 * 在本地设备簿里查出来，然后走和扫码、和设备列表完全相同的那条路：
 * [ParseResult.parse] 重新校验 → [Launcher.launch] 按 A/B/C 递交给官方。
 *
 * ── 它不做什么 ──
 * 不接受任何外部传来的 URL，只接受「设备簿里已经存在的键」。
 * 所以即使有别的 App 构造一个 Intent 打进来，它能做到的最坏情况也只是
 * 「打开某台我已经扫过的机器的官方设备页」——和用户自己点一下完全等价，
 * 不会产生订单、不会开阀出水、也拿不到任何 token。
 *
 * 这也是为什么它可以 exported：真正的安全边界在 [ParseResult] 与
 * [DeviceStore.all] 里，不在 Activity 的 exported 标记上。
 */
class ShortcutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val key = intent?.getStringExtra(EXTRA_KEY)
        QLog.i("Shortcut", "═══ 快捷方式启动 ═══ key=${key ?: "（空）"}")

        if (key.isNullOrBlank()) {
            QLog.w("Shortcut", "没有带设备键，直接结束")
            Toast.makeText(this, R.string.shortcut_bad_key, Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val device = DeviceStore.all(this).firstOrNull { it.key == key }
        if (device == null) {
            QLog.w("Shortcut", "设备簿里找不到这个键，可能已被删除：$key")
            Toast.makeText(this, R.string.shortcut_missing, Toast.LENGTH_LONG).show()
            finish()
            return
        }

        // 与设备列表同一套：重新 parse，拿通过校验的 Ok 再交给 Launcher
        val ok = when (val r = ParseResult.parse(device.launchUrl)) {
            is ParseResult.Ok -> r
            is ParseResult.Reject -> {
                QLog.w("Shortcut", "这台设备的地址已不再通过校验，拒绝唤起：${r.reason}")
                Toast.makeText(this, r.reason, Toast.LENGTH_LONG).show()
                finish()
                return
            }
        }

        val strategy = Prefs.strategy(this)
        val debug = Prefs.debugMode(this)
        val autoFb = Prefs.autoFallback(this)
        QLog.i(
            "Shortcut",
            "从快捷方式唤起：形态=${ok.idKind} 标识=${ok.id} 策略=$strategy 调试=$debug 降级=$autoFb"
        )

        when (val r = Launcher.launch(this, ok, strategy, debug, autoFb)) {
            is AttemptResult.Started -> {
                QLog.i("Shortcut", "✓ 已发起：${r.strategy} → ${r.resolvedComponent}")
                DeviceStore.remember(this, ok)
                // 与设备列表同一套：进入自动解锁的待命窗口（开关关着时 arm 内部直接返回）
                AutoUnlock.arm(this)
                finish()
            }

            is AttemptResult.PlannedDryRun -> {
                QLog.i("Shortcut", "⚠ 调试模式：未真正唤起（${r.strategy}）")
                Toast.makeText(this, getString(R.string.toast_dryrun), Toast.LENGTH_LONG).show()
                finish()
            }

            is AttemptResult.Unavailable ->
                Toast.makeText(this, getString(R.string.status_failed, r.reason), Toast.LENGTH_LONG).show()

            is AttemptResult.Failed ->
                Toast.makeText(this, getString(R.string.status_failed, r.reason), Toast.LENGTH_LONG).show()
        }
    }

    companion object {
        const val EXTRA_KEY = "com.local.qrjump.extra.DEVICE_KEY"
    }
}
``````

## app/src/main/java/com/local/qrjump/ShortcutHelper.kt

```kotlin
package com.local.qrjump

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat

/**
 * 把设备簿同步成桌面快捷方式，并支持「放到桌面」。
 *
 * ── 为什么做这个 ──
 * 目标是把「开支付宝 → 搜小程序 → 点扫一扫」压成尽量少的动作。
 * 设备列表已经省掉了"对准机身码"这一步，但还要：打开 App → 点一行。
 * 桌面上一个独立图标直接选中某台机器，再省掉"打开 App + 点一行"两步。
 *
 * ── 实测结论（2026-11-06 23:28–23:45，Redmi K40S / MIUI V816 / Android 14）──
 * 两条快捷方式通道在这台机器上都不可用，所以「放到桌面」最终落到小部件：
 *
 *   ① [pinViaPinApi]：requestPinShortcut 返回 true 但桌面什么都不画（MIUI 假成功）。
 *   ② 旧版 com.android.launcher.action.INSTALL_SHORTCUT 广播：
 *      MIUI 的 InstallShortcutReceiver 虽然还注册着，但广播刚发出就被系统丢掉 ——
 *      `W/ActivityManager: Broadcast com.android.launcher.action.INSTALL_SHORTCUT
 *       no longer supported. It will not be delivered.`
 *      这是 Android 框架层面的拦截，不是 MIUI 的锅，所以 android 14+ 上留着它也是死代码，
 *      已整段删掉（原先的 [findLegacyReceiver] / 清单里的两条 launcher 权限一并移除）。
 *
 * ── 安全边界 ──
 * 快捷方式只携带**设备簿里的键**（`SN|862270...`），不携带 URL，
 * 也不携带任何 token。落点 [ShortcutActivity] 会拿这个键回设备簿里查，
 * 查不到就什么都不做；查到也会重新跑一遍 [ParseResult.parse] 完整校验。
 * 所以快捷方式的存在不扩大任何攻击面。
 */
object ShortcutHelper {

    /** 最多发布这么多个快捷方式（系统对动态快捷方式数量有上限，一般 5 个左右安全）。 */
    private const val MAX_SHORTCUTS = 4

    // ------------------------------------------------------------ 动态快捷方式

    /**
     * 按当前设备簿重建「长按图标」的动态快捷方式。设备为空时清空（不留陈旧的僵尸入口）。
     *
     * 注意：MIUI 不展示这一层，所以这条对 MIUI 用户基本是空转；
     * 保留它是为了在原生/第三方桌面上仍然好用，成本只有一次 binder 调用。
     */
    fun sync(context: Context) {
        val devices = DeviceStore.all(context)

        if (devices.isEmpty()) {
            val removed = try {
                ShortcutManagerCompat.removeAllDynamicShortcuts(context)
            } catch (t: Throwable) {
                QLog.w("Shortcut", "清空快捷方式失败：${QLog.describe(t)}")
                false
            }
            QLog.i("Shortcut", "设备簿为空，已清空快捷方式（removed=$removed）")
            return
        }

        val list = devices.take(MAX_SHORTCUTS).map { d ->
            val label = labelOf(d)
            ShortcutInfoCompat.Builder(context, d.key)
                .setShortLabel(label)
                .setLongLabel(label)
                .setIcon(IconCompat.createWithResource(context, R.drawable.ic_launcher_foreground))
                .setIntent(shortcutIntent(context, d))
                .build()
        }

        val ok = try {
            ShortcutManagerCompat.setDynamicShortcuts(context, list)
            true
        } catch (t: Throwable) {
            QLog.w("Shortcut", "发布快捷方式失败：${QLog.describe(t)}")
            false
        }
        QLog.i(
            "Shortcut",
            "已同步快捷方式 ${list.size} 个（成功=$ok）：" +
                list.joinToString { it.shortLabel.toString() }
        )
    }

    // ------------------------------------------------------------ 放到桌面

    /**
     * 把某台设备放到桌面。返回 true 表示「至少有一条通道被系统接受了」。
     *
     * 之所以要返回值，是因为以前这里只有一条路，而那条路在 MIUI 上
     * fake-succeed（返回 true 却不画图标），用户根本无从判断。
     */
    fun pinToHome(context: Context, d: DeviceStore.Device): Boolean {
        val label = labelOf(d)

        // MIUI 的启动器对 requestPinShortcut 是"假成功"：返回 true，桌面什么都不画。
        // 既然已经知道它不认，就别再假装成功 —— 直接让调用方去走小部件，
        // 免得用户回桌面找半天找不到，以为应用坏了。
        if (isMiuiLauncher(context)) {
            QLog.w("Shortcut", "当前桌面是 MIUI（${homePackage(context)}），pin API 实测假成功，跳过")
            return false
        }

        if (pinViaPinApi(context, d, label)) {
            QLog.i("Shortcut", "官方 pin API 已接受")
            return true
        }

        QLog.w("Shortcut", "官方 pin API 未被接受，应改用桌面小部件方案")
        return false
    }

    /**
     * 请求把这**一台**设备放到桌面（1×1 方块）。
     *
     * ── 为什么最终走这条路 ──
     * 实测（Redmi K40S / MIUI V816 / Android 14）两条快捷方式通道全废：
     * requestPinShortcut 返回 true 但不画图；INSTALL_SHORTCUT 广播被
     * ActivityManager 直接丢弃。而小部件是**系统级的另一套机制**
     * （AppWidgetService），MIUI 完整实现了它，从选择器里能正常拖出来。
     *
     * ── 怎么把「哪个方块 = 哪台设备」传下去 ──
     * requestPinAppWidget 的 extras 不会直接进 Provider，所以先把设备键
     * 记到 [WidgetBinding] 的 pending 槽，系统随后拉起
     * [WidgetConfigActivity] 时它取走 pending、绑好、立刻返回 ——
     * 用户看到的就是"点一下，确认添加"，不用再选设备。
     *
     * @return true 表示系统接受了请求（可能仍弹确认框等用户点）。
     */
    fun pinWidget(context: Context, d: DeviceStore.Device): Boolean {
        WidgetBinding.setPendingPin(context, d.key)
        QLog.i("Shortcut", "请求放置单设备小部件：key=${d.key} label=${labelOf(d)}")

        val provider = ComponentName(context, DeviceWidgetProvider::class.java)
        val mgr = try {
            android.appwidget.AppWidgetManager.getInstance(context)
        } catch (t: Throwable) {
            QLog.w("Shortcut", "取 AppWidgetManager 失败：${QLog.describe(t)}")
            WidgetBinding.takePendingPin(context)
            null
        }
        if (mgr == null) {
            QLog.w("Shortcut", "这台机器没有 AppWidgetManager，无法放小部件")
            return false
        }
        if (!mgr.isRequestPinAppWidgetSupported) {
            QLog.w("Shortcut", "桌面声明不支持 requestPinAppWidget，请手动长按桌面 → 添加小部件")
            return false
        }
        val ok = try {
            mgr.requestPinAppWidget(provider, null, null)
        } catch (t: Throwable) {
            QLog.w("Shortcut", "requestPinAppWidget 抛错：${QLog.describe(t)}")
            false
        }
        if (!ok) {
            // 请求没被接受就别把 pending 留在那儿，否则下次手动拖小部件
            // 会被这次的残留值误导，绑到一台并不想要的设备上。
            WidgetBinding.takePendingPin(context)
        }
        QLog.i("Shortcut", "requestPinAppWidget：provider=$provider 结果=$ok")
        return ok
    }

    /** 当前默认桌面是哪个包。查不到返回 null。 */
    private fun homePackage(context: Context): String? {
        val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME)
        return try {
            context.packageManager.resolveActivity(intent, 0)?.activityInfo?.packageName
        } catch (t: Throwable) {
            QLog.w("Shortcut", "查默认桌面失败：${QLog.describe(t)}")
            null
        }
    }

    private fun isMiuiLauncher(context: Context): Boolean =
        homePackage(context)?.startsWith("com.miui.home") == true

    /** 官方 Android 8.0+ 通道。 */
    private fun pinViaPinApi(context: Context, d: DeviceStore.Device, label: String): Boolean {
        val info = ShortcutInfoCompat.Builder(context, "${d.key}#pinned")
            .setShortLabel(label)
            .setLongLabel(label)
            .setIcon(IconCompat.createWithResource(context, R.drawable.ic_launcher_foreground))
            .setIntent(shortcutIntent(context, d))
            .build()

        val ok = try {
            ShortcutManagerCompat.requestPinShortcut(context, info, null)
        } catch (t: Throwable) {
            QLog.w("Shortcut", "requestPinShortcut 抛错：${QLog.describe(t)}")
            false
        }
        QLog.i("Shortcut", "requestPinShortcut：key=${d.key} label=$label 结果=$ok")
        return ok
    }

    private fun shortcutIntent(context: Context, d: DeviceStore.Device): Intent =
        Intent(context, ShortcutActivity::class.java).apply {
            action = Intent.ACTION_VIEW
            putExtra(ShortcutActivity.EXTRA_KEY, d.key)
            // 让每个设备的快捷方式各有各的任务栈语义，避免互相顶替
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }

    private fun labelOf(d: DeviceStore.Device): String =
        if (d.alias.isNotBlank()) d.alias else shortLabel(d)

    /** 默认短名：只有 SN 时用后 6 位，避免桌面图标下面一行放不下 15 位数字。 */
    private fun shortLabel(d: DeviceStore.Device): String = if (d.idKind == ParseResult.ID_SN) {
        "饮水器 …" + d.id.takeLast(6)
    } else {
        "热水器 …" + d.id.take(4)
    }
}
``````

## app/src/main/java/com/local/qrjump/WebViewActivity.kt

```kotlin
package com.local.qrjump

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

/**
 * 策略 C：内置 WebView。
 *
 * 定位：一个"什么都不改"的浏览器。页面怎么显示就怎么显示，
 * 页面想跳 alipays:// 就把它交给支付宝 App。
 *
 * 明确的边界：
 *   - 不注入 JS，不读取页面 DOM，不抓包，不改写请求体
 *   - 不拦截、不记录、不重放任何网络请求
 *   - 不开 file:// 访问，不允许 http 明文，不允许混合内容
 *   - 不保存表单、不保存密码、不清除也不伪造 cookie
 *
 * 只做一件事：遇到非 http(s) 的 scheme 就外抛。
 */
class WebViewActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var progress: ProgressBar
    private lateinit var statusText: TextView
    private lateinit var fallbackBar: LinearLayout

    private var targetUrl: String = ""
    private var pageErrored = false

    companion object {
        private const val EXTRA_URL = "extra_url"

        fun newIntent(c: Context, url: String): Intent =
            Intent(c, WebViewActivity::class.java).putExtra(EXTRA_URL, url)
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview)

        webView = findViewById(R.id.webView)
        progress = findViewById(R.id.progress)
        statusText = findViewById(R.id.statusText)
        fallbackBar = findViewById(R.id.fallbackBar)

        targetUrl = intent.getStringExtra(EXTRA_URL).orEmpty()
        if (targetUrl.isEmpty()) {
            QLog.w("Web", "EXTRA_URL 为空，直接结束")
            finish()
            return
        }

        QLog.i("Web", "打开 URL = $targetUrl")

        val useUa = Prefs.useAlipayUa(this)
        val autoScheme = Prefs.webviewAutoScheme(this)
        QLog.i("Web", "配置: 伪装UA=$useUa  外抛scheme=$autoScheme")

        with(webView.settings) {
            javaScriptEnabled = true          // 官方 H5 依赖 JS，关掉页面就是白屏
            domStorageEnabled = true
            databaseEnabled = true
            loadWithOverviewMode = true
            useWideViewPort = true
            builtInZoomControls = false
            displayZoomControls = false
            allowFileAccess = false           // 安全：禁止 file://
            allowContentAccess = false
            javaScriptCanOpenWindowsAutomatically = false
            mediaPlaybackRequiresUserGesture = true
            mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW
            cacheMode = WebSettings.LOAD_DEFAULT
            userAgentString = if (useUa) Prefs.customAlipayUa(this@WebViewActivity) else userAgentString
        }
        if (useUa) {
            QLog.i("Web", "UA 已设置为支付宝 UA: ${webView.settings.userAgentString}")
        }

        webView.webViewClient = object : WebViewClient() {

            override fun shouldOverrideUrlLoading(
                view: WebView,
                request: WebResourceRequest
            ): Boolean = handleUrl(request.url, request.isForMainFrame)

            @Deprecated("兼容 API 21-23")
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean =
                handleUrl(Uri.parse(url), true)

            override fun onPageStarted(view: WebView, url: String?, favicon: Bitmap?) {
                QLog.i("Web", "onPageStarted: $url")
                progress.visibility = View.VISIBLE
                pageErrored = false
                statusText.text = getString(R.string.web_loading)
            }

            override fun onPageFinished(view: WebView, url: String?) {
                QLog.i("Web", "onPageFinished: $url")
                progress.visibility = View.GONE
                statusText.text = getString(R.string.web_loaded)
                // 加载完成后如果页面明显是空的，也不要报警——交给用户判断，
                // 因为"页面是否有内容"不是本 App 能可靠断言的事。
            }

            override fun onReceivedError(
                view: WebView,
                request: WebResourceRequest,
                error: android.webkit.WebResourceError
            ) {
                if (request.isForMainFrame) {
                    pageErrored = true
                    QLog.e("Web", "主文档加载失败: ${error.errorCode} ${error.description} url=${request.url}")
                    progress.visibility = View.GONE
                    statusText.text = getString(R.string.web_error, error.errorCode)
                    fallbackBar.visibility = View.VISIBLE
                }
            }
        }

        webView.webChromeClient = object : android.webkit.WebChromeClient() {
            override fun onProgressChanged(view: WebView, newProgress: Int) {
                progress.progress = newProgress
            }
        }

        statusText.text = getString(R.string.web_loading)
        webView.loadUrl(targetUrl)

        findViewById<Button>(R.id.btnCopy).setOnClickListener {
            Launcher.copyToClipboard(this, targetUrl)
        }
        findViewById<Button>(R.id.btnOpenExternal).setOnClickListener {
            openExternally(targetUrl)
        }
        findViewById<Button>(R.id.btnClose).setOnClickListener { finish() }
    }

    /**
     * 处理页面内的跳转。
     *
     * @return true = 本 WebView 不处理这次跳转（已交给别的 App 或已静默丢弃）
     */
    private fun handleUrl(uri: Uri, isMainFrame: Boolean): Boolean {
        val scheme = uri.scheme?.lowercase()
        QLog.i("Web", "拦截跳转 scheme=$scheme mainFrame=$isMainFrame url=$uri")

        // http(s) 一律留在 WebView 内，不外抛
        if (scheme == "http" || scheme == "https") {
            return false
        }

        if (!Prefs.webviewAutoScheme(this)) {
            QLog.w("Web", "外抛已关闭，丢弃该跳转: $uri")
            return true
        }

        // 其它 scheme（alipays / alipay / taobao ...）交给系统对应的 App
        return try {
            val out = Intent(Intent.ACTION_VIEW, uri).apply {
                if (!isMainFrame) addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            val ri = packageManager.resolveActivity(
                out, android.content.pm.PackageManager.MATCH_DEFAULT_ONLY
            )
            QLog.i("Web", "外抛 Intent=${Launcher.dumpIntent(out)} resolve=${ri?.activityInfo?.let { "${it.packageName}/${it.name}" } ?: "null"}")

            if (ri == null) {
                QLog.w("Web", "没有任何 App 能处理 scheme=$scheme，丢弃")
                Toast.makeText(this, getString(R.string.web_no_handler, scheme), Toast.LENGTH_SHORT).show()
                true
            } else {
                startActivity(out)
                QLog.i("Web", "已外抛给 $scheme")
                // 交给支付宝之后本页就没用了，自己关掉：
                // 既少一次返回，也避免它留在任务栈里被系统降频后干扰自动点击。
                finish()
                true
            }
        } catch (t: Throwable) {
            QLog.e("Web", "外抛 scheme=$scheme 失败", t)
            Toast.makeText(this, getString(R.string.web_scheme_failed, t.message ?: ""), Toast.LENGTH_SHORT).show()
            true
        }
    }

    /** 用系统浏览器打开当前 URL（不经过本 App 的策略逻辑，纯手动）。 */
    private fun openExternally(url: String) {
        try {
            QLog.i("Web", "手动外抛到系统: $url")
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        } catch (t: Throwable) {
            QLog.e("Web", "手动外抛失败", t)
            Toast.makeText(this, t.message ?: "打开失败", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onBackPressed() {
        if (this::webView.isInitialized && webView.canGoBack()) {
            webView.goBack()
        } else {
            @Suppress("DEPRECATION")
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        try {
            if (this::webView.isInitialized) {
                webView.stopLoading()
                webView.webViewClient = WebViewClient()
                webView.destroy()
            }
        } catch (t: Throwable) {
            QLog.e("Web", "销毁 WebView 异常", t)
        }
        super.onDestroy()
    }
}
``````

## app/src/main/java/com/local/qrjump/WidgetBinding.kt

```kotlin
package com.local.qrjump

import android.content.Context
import org.json.JSONObject

/**
 * 「哪个桌面小部件 -> 哪台设备」的绑定表。
 *
 * ── 为什么需要单独一张表 ──
 * 改成「一台设备一个 1×1 方块」之后，桌面上可以同时存在多个实例：
 * 南五801 一个、洗澡一个。每个实例都是一个独立的小部件 id
 * （AppWidgetManager 分配），彼此之间唯一的区别就是「绑给谁」。
 * 这个 id 与设备的对应关系必须自己存 —— 系统不知道也不关心。
 *
 * ── 存的是什么 ──
 * 只存 `设备键`（形如 `SN|<你的15位SN>` / `NQT|9b9642e8-...`）：
 * 设备标识机身印着、公开可见，不含 token / 签名 / 登录态 / 订单。
 * 地址本身也不存 —— 点击时拿键回 [DeviceStore] 查，查到还要重新跑一遍
 * [ParseResult.parse]。所以即使这张表被改坏，也顶多指向一台"查不到的设备"，
 * 不会变成可以任意 URL 启动的后门。
 */
object WidgetBinding {

    /** JSON 对象：`{"widgetId": "设备键"}`。与其它设置同在一个偏好文件里。 */
    private const val KEY_BINDINGS = "widget_bindings"

    /**
     * 「放到桌面」时先记下这台设备，等系统拉起配置页就自动绑它、不再问一遍。
     * 手动从选择器拖出来的小部件没有这个值，配置页会正常弹选择框。
     */
    private const val KEY_PENDING = "widget_pending_pin"

    /** 只保留这轮还活着的小部件 id，别让绑定表无限膨胀。 */
    private const val MAX_BINDINGS = 32

    private fun sp(context: Context) =
        context.applicationContext.getSharedPreferences(Prefs.FILE, Context.MODE_PRIVATE)

    private fun load(context: Context): JSONObject {
        val raw = sp(context).getString(KEY_BINDINGS, null)
        if (raw.isNullOrBlank()) return JSONObject()
        return try {
            JSONObject(raw)
        } catch (t: Throwable) {
            QLog.w("Widget", "绑定表解析失败，按空处理：${QLog.describe(t)}")
            JSONObject()
        }
    }

    private fun save(context: Context, o: JSONObject) {
        sp(context).edit().putString(KEY_BINDINGS, o.toString()).apply()
    }

    /** 这个小部件绑的是哪台；没绑过返回 null。 */
    fun deviceKey(context: Context, widgetId: Int): String? {
        val v = load(context).optString(widgetId.toString(), "")
        return if (v.isBlank()) null else v
    }

    fun bind(context: Context, widgetId: Int, deviceKey: String) {
        val o = load(context)
        o.put(widgetId.toString(), deviceKey)
        if (o.length() > MAX_BINDINGS) {
            // 顶多 32 条，正常用不到；真到了就整表重建，反正下一轮 onUpdate 会补齐。
            val trimmed = JSONObject()
            o.keys().asSequence().toList().takeLast(MAX_BINDINGS).forEach { k ->
                trimmed.put(k, o.optString(k, ""))
            }
            save(context, trimmed)
        } else {
            save(context, o)
        }
        QLog.i("Widget", "小部件 $widgetId 绑定到 $deviceKey")
    }

    fun unbind(context: Context, widgetId: Int) {
        val o = load(context)
        if (!o.has(widgetId.toString())) return
        o.remove(widgetId.toString())
        save(context, o)
        QLog.i("Widget", "小部件 $widgetId 的绑定已删除")
    }

    /**
     * 清理已经不存在的实例。
     *
     * 用户从桌面拖掉一个方块时系统不会通知得很及时（onDeleted 有时收不到），
     * 所以每次刷新顺便按「当前活着的 id 集合」清一遍，避免表里留垃圾。
     */
    fun prune(context: Context, alive: Set<Int>) {
        val o = load(context)
        if (o.length() == 0) return
        val dead = o.keys().asSequence().filter { k ->
            val id = k.toIntOrNull()
            id == null || id !in alive
        }.toList()
        if (dead.isEmpty()) return
        dead.forEach { o.remove(it) }
        save(context, o)
        QLog.i("Widget", "清理了 ${dead.size} 条已失效的小部件绑定")
    }

    // ------------------------------------------------------------ 待绑定

    fun setPendingPin(context: Context, deviceKey: String) {
        sp(context).edit().putString(KEY_PENDING, deviceKey).apply()
    }

    /** 取出并清空（一次有效，避免下次手动添加小部件时被这次的值误导）。 */
    fun takePendingPin(context: Context): String? {
        val v = sp(context).getString(KEY_PENDING, null)
        if (v.isNullOrBlank()) return null
        sp(context).edit().remove(KEY_PENDING).apply()
        return v
    }
}
``````

## app/src/main/java/com/local/qrjump/WidgetConfigActivity.kt

```kotlin
package com.local.qrjump

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

/**
 * 1×1 小部件的配置页：决定「这个方块代表哪台设备」。
 *
 * ── 它什么时候出现 ──
 * 小部件的 `android:configure` 指向这里，所以系统在放置小部件时会拉起它。
 * 有两种进入方式：
 *   · 从 App 里点「放到桌面」→ [WidgetBinding] 里已经记了 pending，
 *     这里直接用它绑上、立刻返回，用户看不到这个界面；
 *   · 手动长按桌面 → 添加小部件 → 拖出来 → 没有 pending，
 *     这时才真的弹出来让用户选一台。
 *
 * ── 为什么不做「以后还能改」的入口 ──
 * 官方机制里配置页只在放置时调用一次。放置后要改，用户直接拖掉重新放一个
 * 就行（小部件没有"编辑"这个通用入口，硬做反而要教用户去点两下）。
 * 但如果绑的设备在 App 里被删了，方块会变成「已失效，点一下重新选择」，
 * 那时点它会重新进到这里 —— 这是唯一一个"放置后还能改"的入口。
 */
class WidgetConfigActivity : AppCompatActivity() {

    private var widgetId = AppWidgetManager.INVALID_APPWIDGET_ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 默认结果设成"取消"：用户按返回键退出时小部件不该被放下。
        // 这是官方文档明确要求的写法，否则会出现一个什么都不显示的占位方块。
        setResult(Activity.RESULT_CANCELED)

        widgetId = intent?.getIntExtra(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID

        QLog.i("Widget", "配置页打开：widgetId=$widgetId 已有绑定=${WidgetBinding.deviceKey(this, widgetId)}")

        // 从「放到桌面」进来的：直接绑、直接结束，别让用户多点一次。
        val pending = WidgetBinding.takePendingPin(this)
        if (pending != null) {
            finishWithBinding(pending, silent = true)
            return
        }

        setContentView(R.layout.activity_widget_config)
        findViewById<TextView>(R.id.tvWidgetConfigBack).setOnClickListener { finish() }
        findViewById<TextView>(R.id.tvWidgetConfigTitle).text =
            getString(R.string.widget_config_title)
        findViewById<TextView>(R.id.tvWidgetConfigHint).text =
            getString(R.string.widget_config_hint)

        renderChoices()
    }

    private fun renderChoices() {
        val container = findViewById<LinearLayout>(R.id.containerWidgetChoices)
        container.removeAllViews()

        val devices = DeviceStore.all(this)
        if (devices.isEmpty()) {
            // 一台都没有：说清楚要先扫码，然后让用户退出。
            // 这里不放"去扫码"按钮 —— 从配置页跳去扫码页，扫完回来小部件
            // 已经因为 Activity 被回收而拿不到 widgetId 了，容易做出一个空壳。
            findViewById<TextView>(R.id.tvWidgetConfigEmpty).visibility = View.VISIBLE
            findViewById<TextView>(R.id.tvWidgetConfigEmpty).text =
                getString(R.string.widget_config_empty)
            return
        }
        findViewById<TextView>(R.id.tvWidgetConfigEmpty).visibility = View.GONE

        val current = WidgetBinding.deviceKey(this, widgetId)
        devices.forEach { d ->
            val row = layoutInflater.inflate(R.layout.item_widget_choice, container, false)
            row.findViewById<TextView>(R.id.tvChoiceName).text =
                if (d.alias.isNotBlank()) d.alias else defaultName(d)
            row.findViewById<TextView>(R.id.tvChoiceSub).text = defaultName(d)
            row.findViewById<TextView>(R.id.tvChoiceCheck).visibility =
                if (d.key == current) View.VISIBLE else View.INVISIBLE
            row.setOnClickListener { finishWithBinding(d.key, silent = false) }
            container.addView(row)
        }
        QLog.i("Widget", "配置页列出 ${devices.size} 台设备")
    }

    private fun finishWithBinding(deviceKey: String, silent: Boolean) {
        if (widgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            QLog.w("Widget", "配置页拿不到 widgetId，无法绑定")
            if (!silent) Toast.makeText(this, R.string.widget_config_failed, Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        WidgetBinding.bind(this, widgetId, deviceKey)
        DeviceWidgetProvider.updateOne(this, widgetId)

        val result = Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
        setResult(Activity.RESULT_OK, result)
        QLog.i("Widget", "配置完成：widgetId=$widgetId -> $deviceKey（静默=$silent）")
        finish()
    }

    private fun defaultName(d: DeviceStore.Device): String = if (d.idKind == ParseResult.ID_SN) {
        getString(R.string.quick_id_sn, d.id.takeLast(6))
    } else {
        getString(R.string.quick_id_nqt, d.id.take(8))
    }
}
``````

## app/src/main/res/drawable/bg_app_gradient.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<!--
  整页背景：深色底 + 两团柔光。

  为什么不用真模糊：Android 直到 12（API 31）才有 RenderEffect 背景模糊，
  且要求 targetSdk 31+ 并且对性能有明显开销；这台是 Android 14 但目标是
  minSdk 26，用不了。所以这里用「深色渐变 + 两团半透明径向光斑」模拟
  液态玻璃下面那层折射光——视觉上足够接近，且是纯 drawable，零运行时开销。

  层叠顺序（layer-list 从下往上）：
    1. 基础线性渐变（深靛蓝 → 近黑紫）
    2. 左上偏青的一团柔光
    3. 右下偏品红的一团柔光
-->
<layer-list xmlns:android="http://schemas.android.com/apk/res/android">

    <item>
        <shape android:shape="rectangle">
            <gradient
                android:angle="270"
                android:startColor="#0A0F1E"
                android:centerColor="#141A2E"
                android:endColor="#1A1030"
                android:type="linear" />
        </shape>
    </item>

    <item
        android:left="-160dp"
        android:top="-220dp"
        android:right="-60dp"
        android:bottom="-420dp">
        <shape android:shape="oval">
            <gradient
                android:type="radial"
                android:gradientRadius="320dp"
                android:startColor="#4D2EC5FF"
                android:centerColor="#1A2EC5FF"
                android:endColor="#002EC5FF" />
        </shape>
    </item>

    <item
        android:left="-80dp"
        android:top="-540dp"
        android:right="-200dp"
        android:bottom="-120dp">
        <shape android:shape="oval">
            <gradient
                android:type="radial"
                android:gradientRadius="300dp"
                android:startColor="#40B14BFF"
                android:centerColor="#14B14BFF"
                android:endColor="#00B14BFF" />
        </shape>
    </item>

</layer-list>
``````

## app/src/main/res/drawable/bg_glass_button_outline.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<!-- 次级按钮：只有一圈极淡的玻璃边，不填色，避免和主按钮抢注意力。 -->
<selector xmlns:android="http://schemas.android.com/apk/res/android">

    <item android:state_pressed="true">
        <shape android:shape="rectangle">
            <corners android:radius="26dp" />
            <solid android:color="#26FFFFFF" />
            <stroke
                android:width="1dp"
                android:color="#59FFFFFF" />
        </shape>
    </item>

    <item>
        <shape android:shape="rectangle">
            <corners android:radius="26dp" />
            <solid android:color="#0DFFFFFF" />
            <stroke
                android:width="1dp"
                android:color="#33FFFFFF" />
        </shape>
    </item>

</selector>
``````

## app/src/main/res/drawable/bg_glass_button.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<!--
  主按钮：玻璃胶囊。

  不用 Material 默认填充按钮，因为纯色块放在玻璃背景上会"破功"——
  一眼就看出是实心色块贴上去的。改成：半透明高光渐变 + 亮边 + 全圆角，
  读起来像一颗被光打透的液态胶囊。
-->
<selector xmlns:android="http://schemas.android.com/apk/res/android">

    <item android:state_pressed="true">
        <shape android:shape="rectangle">
            <corners android:radius="26dp" />
            <gradient
                android:angle="315"
                android:startColor="#802EC5FF"
                android:centerColor="#665FA8FF"
                android:endColor="#4D8B5CF6"
                android:type="linear" />
            <stroke
                android:width="1dp"
                android:color="#80FFFFFF" />
        </shape>
    </item>

    <item>
        <shape android:shape="rectangle">
            <corners android:radius="26dp" />
            <gradient
                android:angle="315"
                android:startColor="#CC2EC5FF"
                android:centerColor="#B35FA8FF"
                android:endColor="#998B5CF6"
                android:type="linear" />
            <stroke
                android:width="1dp"
                android:color="#59FFFFFF" />
        </shape>
    </item>

</selector>
``````

## app/src/main/res/drawable/bg_glass_card.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<!--
  「液态玻璃」卡片底。

  四个细节决定它像不像玻璃：
    1. 半透明白（不是纯白）—— 让背后的光斑透上来，这是玻璃的第一特征；
    2. 斜向渐变 —— 玻璃不是均匀的，光穿过它有方向性；
    3. 1dp 白色描边（低不透明度）—— 边缘高光，这是"液态"的关键，
       纯色描边会显得像塑料；
    4. 大圆角（22dp）—— 圆角越大越柔和，越像液滴表面张力形成的边界。

  API 31+ 可以在此基础上用 RenderEffect 做真模糊，但没有必要：
  背后本来就是我们自己画的柔光，模糊一层等于把柔光糊开，肉眼看不出区别。
-->
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="rectangle">

    <corners android:radius="22dp" />

    <gradient
        android:angle="315"
        android:startColor="#2EFFFFFF"
        android:centerColor="#1AFFFFFF"
        android:endColor="#12FFFFFF"
        android:type="linear" />

    <stroke
        android:width="1dp"
        android:color="#33FFFFFF" />
</shape>
``````

## app/src/main/res/drawable/bg_glass_circle.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<!-- 顶部图标按钮用的圆形玻璃底（右上角「设置」）。 -->
<selector xmlns:android="http://schemas.android.com/apk/res/android">

    <item android:state_pressed="true">
        <shape android:shape="oval">
            <solid android:color="#4DFFFFFF" />
            <stroke
                android:width="1dp"
                android:color="#80FFFFFF" />
        </shape>
    </item>

    <item>
        <shape android:shape="oval">
            <solid android:color="#1AFFFFFF" />
            <stroke
                android:width="1dp"
                android:color="#40FFFFFF" />
        </shape>
    </item>

</selector>
``````

## app/src/main/res/drawable/bg_glass_panel_light.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<!--
  设置页外层的「浅色磨砂玻璃」。

  与设备页的深色玻璃是同一套语言的两面：
    设备页 = 深色玻璃（内容是设备名，需要白字压暗底才显干净）
    设置页 = 浅色磨砂（内容是一堆表单，深字压亮底才读得清）

  两者共用同一个页面级渐变背景 bg_app_gradient，
  差别只在玻璃层的明度 —— 这样翻页时背景是连续的，只有玻璃层在换。
-->
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="rectangle">

    <corners android:radius="26dp" />

    <gradient
        android:angle="315"
        android:startColor="#F2FFFFFF"
        android:centerColor="#E6FFFFFF"
        android:endColor="#D9FFFFFF"
        android:type="linear" />

    <stroke
        android:width="1dp"
        android:color="#66FFFFFF" />
</shape>
``````

## app/src/main/res/drawable/bg_glass_row.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<!--
  设备行。比外层卡片再亮一点，做出「玻璃叠玻璃」的层次：
  底层是页面（最深）→ 卡片（中）→ 这一行（最亮）。
  按下时整体提亮，给一点液态的"受力变形"暗示。
-->
<selector xmlns:android="http://schemas.android.com/apk/res/android">

    <item android:state_pressed="true">
        <shape android:shape="rectangle">
            <corners android:radius="18dp" />
            <gradient
                android:angle="315"
                android:startColor="#4DFFFFFF"
                android:endColor="#33FFFFFF"
                android:type="linear" />
            <stroke
                android:width="1dp"
                android:color="#66FFFFFF" />
        </shape>
    </item>

    <item>
        <shape android:shape="rectangle">
            <corners android:radius="18dp" />
            <gradient
                android:angle="315"
                android:startColor="#1FFFFFFF"
                android:endColor="#0FFFFFFF"
                android:type="linear" />
            <stroke
                android:width="1dp"
                android:color="#26FFFFFF" />
        </shape>
    </item>

</selector>
``````

## app/src/main/res/drawable/bg_scrim_bottom.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<!--
  扫码结果面板的底。

  原来是 #E6000000（近乎不透明的黑），盖在相机画面上像一块黑板贴上去。
  改成"底部深、往上渐隐"的竖向渐变：底部仍然够暗以保证白字可读，
  往上逐渐透出相机画面 —— 既保住可读性，又不像贴块黑布。
-->
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="rectangle">

    <corners
        android:topLeftRadius="26dp"
        android:topRightRadius="26dp" />

    <gradient
        android:angle="90"
        android:startColor="#F20A0F1E"
        android:centerColor="#D90A0F1E"
        android:endColor="#B30A0F1E"
        android:type="linear" />

    <stroke
        android:width="1dp"
        android:color="#26FFFFFF" />
</shape>
``````

## app/src/main/res/drawable/bg_scrim_top.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<!--
  扫码页顶部遮罩。

  原来是 #80000000（半透明黑方块），压在相机画面上会切出一条硬边。
  改成"顶部深、往下渐隐"，与底部结果面板用同一套逻辑，
  视觉上是相机画面在上下两端自然沉进深色，而不是被两块色块夹住。
-->
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="rectangle">

    <corners
        android:bottomLeftRadius="26dp"
        android:bottomRightRadius="26dp" />

    <gradient
        android:angle="270"
        android:startColor="#F20A0F1E"
        android:centerColor="#B30A0F1E"
        android:endColor="#000A0F1E"
        android:type="linear" />

</shape>
``````

## app/src/main/res/drawable/bg_widget_cell.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<!--
  1×1 单设备小部件的底板。

  比原来那块 3×2 列表小部件更薄一点：1×1 只占一格，
  太厚会把图标和名字一起糊掉。仍然是同一套「液态玻璃」语言：
  315° 渐变 + 大圆角 + 1dp 高光描边。
-->
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="rectangle">
    <gradient
        android:angle="315"
        android:centerColor="#1AFFFFFF"
        android:endColor="#0DFFFFFF"
        android:startColor="#33FFFFFF" />
    <corners android:radius="20dp" />
    <stroke
        android:width="1dp"
        android:color="#40FFFFFF" />
</shape>
``````

## app/src/main/res/drawable/ic_launcher_background.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="108dp"
    android:height="108dp"
    android:viewportWidth="108"
    android:viewportHeight="108">
    <path
        android:fillColor="#0B5CD5"
        android:pathData="M0,0h108v108h-108z" />
</vector>
``````

## app/src/main/res/drawable/ic_launcher_foreground.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<!-- 一个简化的二维码轮廓：三个角标 + 若干点，纯矢量，无位图依赖 -->
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="108dp"
    android:height="108dp"
    android:viewportWidth="108"
    android:viewportHeight="108">

    <!-- 左上角定位块 -->
    <path
        android:fillColor="#FFFFFF"
        android:pathData="M26,26h18v18h-18z" />
    <path
        android:fillColor="#0B5CD5"
        android:pathData="M30,30h10v10h-10z" />

    <!-- 右上角定位块 -->
    <path
        android:fillColor="#FFFFFF"
        android:pathData="M64,26h18v18h-18z" />
    <path
        android:fillColor="#0B5CD5"
        android:pathData="M68,30h10v10h-10z" />

    <!-- 左下角定位块 -->
    <path
        android:fillColor="#FFFFFF"
        android:pathData="M26,64h18v18h-18z" />
    <path
        android:fillColor="#0B5CD5"
        android:pathData="M30,68h10v10h-10z" />

    <!-- 右下角数据点 -->
    <path
        android:fillColor="#FFFFFF"
        android:pathData="M64,64h8v8h-8z" />
    <path
        android:fillColor="#FFFFFF"
        android:pathData="M76,64h6v6h-6z" />
    <path
        android:fillColor="#FFFFFF"
        android:pathData="M64,76h6v6h-6z" />
    <path
        android:fillColor="#FFFFFF"
        android:pathData="M74,74h8v8h-8z" />
</vector>
``````

## app/src/main/res/drawable/ic_widget_heat.xml

```xml
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp"
    android:height="24dp"
    android:viewportWidth="24"
    android:viewportHeight="24">
    <!-- 热水器：花洒 + 水滴 -->
    <path
        android:fillColor="#C9B6FF"
        android:pathData="M4.2,4.6 L10.4,7.4 L8.6,11.2 L2.4,8.4 Z" />
    <path
        android:fillColor="#C9B6FF"
        android:pathData="M9.2,10.4 L12.2,9 L14.6,14.4 L11.6,15.8 Z" />
    <!-- 三滴水 -->
    <path
        android:fillColor="#8FD8FF"
        android:pathData="M8.4,16.4 m-1.1,0 a1.1,1.4 0 1,0 2.2,0 a1.1,1.4 0 1,0 -2.2,0" />
    <path
        android:fillColor="#8FD8FF"
        android:pathData="M13.2,17.8 m-1.1,0 a1.1,1.4 0 1,0 2.2,0 a1.1,1.4 0 1,0 -2.2,0" />
    <path
        android:fillColor="#8FD8FF"
        android:pathData="M18,19.2 m-1.1,0 a1.1,1.4 0 1,0 2.2,0 a1.1,1.4 0 1,0 -2.2,0" />
</vector>
``````

## app/src/main/res/drawable/ic_widget_water.xml

```xml
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp"
    android:height="24dp"
    android:viewportWidth="24"
    android:viewportHeight="24">
    <!-- 饮水器：一滴水 -->
    <path
        android:fillColor="#8FD8FF"
        android:pathData="M12,2.2 C12,2.2 5.2,10 5.2,14.2 A6.8,6.8 0 0,0 18.8,14.2 C18.8,10 12,2.2 12,2.2 Z" />
    <!-- 高光，让水滴不至于是一块死色 -->
    <path
        android:fillColor="#CCFFFFFF"
        android:pathData="M9.6,13.6 A1.6,2.2 0 0,0 9.6,17.4 A1.7,2.4 0 0,1 9.6,13.6 Z" />
</vector>
``````

## app/src/main/res/layout/activity_devices.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<!--
  启动页 = 设备列表（液态玻璃版）。

  结构没变（标题 → 说明 → 列表 → 底部按钮），只换了皮：
  所有控件 id 与旧版**完全一致**，所以 DevicesActivity.kt 一行都不用改。

  层次：页面渐变底 → 玻璃卡片包住列表 → 行内是更亮的一层玻璃。
-->
<FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@drawable/bg_app_gradient">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:orientation="vertical"
        android:paddingStart="16dp"
        android:paddingEnd="16dp"
        android:paddingTop="18dp"
        android:paddingBottom="14dp">

        <!-- 标题行：大标题 + 胶囊设置按钮 -->
        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="horizontal"
            android:gravity="center_vertical"
            android:paddingStart="6dp"
            android:paddingEnd="2dp">

            <TextView
                style="@style/GlassTitle"
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                android:layout_weight="1"
                android:text="@string/devices_title" />

            <TextView
                android:id="@+id/btnDevicesSettings"
                android:layout_width="wrap_content"
                android:layout_height="40dp"
                android:background="@drawable/bg_glass_button_outline"
                android:gravity="center"
                android:minWidth="72dp"
                android:paddingStart="16dp"
                android:paddingEnd="16dp"
                android:text="@string/devices_settings"
                android:textColor="#D9FFFFFF"
                android:textSize="12sp" />
        </LinearLayout>

        <!--
          无障碍掉线提示条。默认 gone，只有「授权真的丢了」时才出现。

          为什么需要它：MIUI 上本应用能靠 WRITE_SECURE_SETTINGS 自己把授权写回去；
          ColorOS（realme）把 pm grant 全局封了（连给 GMS 授权都拒，2026-11-07 实测），
          所以自动补不回来。既然补不回来，就不能假装没事 —— 直接在这个位置
          摆一个可点的条，点一下直达系统无障碍页，用户开一下就行。
        -->
        <TextView
            android:id="@+id/tvA11yWarn"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginTop="12dp"
            android:background="@drawable/bg_glass_card"
            android:lineSpacingExtra="4dp"
            android:paddingStart="16dp"
            android:paddingEnd="16dp"
            android:paddingTop="14dp"
            android:paddingBottom="14dp"
            android:text="@string/devices_a11y_warn"
            android:textColor="#FFD98C"
            android:textSize="12sp"
            android:visibility="gone" />

        <!-- 列表卡片：玻璃容器，里面是可滚动的设备行 -->
        <FrameLayout
            android:layout_width="match_parent"
            android:layout_height="0dp"
            android:layout_weight="1"
            android:layout_marginTop="18dp"
            android:background="@drawable/bg_glass_card"
            android:paddingStart="8dp"
            android:paddingEnd="8dp"
            android:paddingTop="10dp"
            android:paddingBottom="10dp">

            <ScrollView
                android:layout_width="match_parent"
                android:layout_height="match_parent"
                android:fillViewport="true"
                android:scrollbars="none">

                <LinearLayout
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:orientation="vertical">

                    <TextView
                        android:id="@+id/tvDevicesEmpty"
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:gravity="center"
                        android:lineSpacingExtra="5dp"
                        android:paddingStart="20dp"
                        android:paddingEnd="20dp"
                        android:paddingTop="52dp"
                        android:paddingBottom="52dp"
                        android:text="@string/devices_empty"
                        android:textColor="#99FFFFFF"
                        android:textSize="13sp"
                        android:visibility="gone" />

                    <LinearLayout
                        android:id="@+id/containerDevices"
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:orientation="vertical" />
                </LinearLayout>
            </ScrollView>
        </FrameLayout>

        <!-- 底部：主操作扫码添加，次操作清空 -->
        <TextView
            android:id="@+id/btnAddDevice"
            style="@style/GlassButton"
            android:layout_marginTop="16dp"
            android:text="@string/devices_add" />

        <TextView
            android:id="@+id/btnDevicesClear"
            style="@style/GlassButtonOutline"
            android:layout_marginTop="8dp"
            android:text="@string/devices_clear" />

    </LinearLayout>

</FrameLayout>
``````

## app/src/main/res/layout/activity_scan.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<!--
  扫码页。相机**不会**自动打开：进页面先看到一块黑底 + 一个大按钮，
  点「点击开始扫码」才 bindToLifecycle。扫码成功后相机立刻解绑，
  避免举着手机时一直在跑分析、也避免误扫第二张码。

  返回箭头放在左上角，点了直接 finish() 回设备列表。
-->
<FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@drawable/bg_app_gradient">

    <!-- 相机预览：扑满全屏，只有点按钮之后才可见、才绑定 -->
    <androidx.camera.view.PreviewView
        android:id="@+id/previewView"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:importantForAccessibility="no"
        android:visibility="invisible" />

    <!-- 顶部：状态文字 -->
    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_gravity="top"
        android:orientation="vertical"
        android:paddingStart="20dp"
        android:paddingTop="112dp"
        android:paddingEnd="20dp"
        android:paddingBottom="14dp"
        android:background="@drawable/bg_scrim_top">

        <TextView
            android:id="@+id/statusText"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="@string/scan_title"
            android:textColor="#FFFFFF"
            android:textSize="19sp"
            android:textStyle="bold" />

        <TextView
            android:id="@+id/hintText"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginTop="4dp"
            android:text="@string/scan_start_hint"
            android:textColor="#CCFFFFFF"
            android:textSize="12sp" />
    </LinearLayout>

    <!-- 相机未启动时的中央大按钮 -->
    <LinearLayout
        android:id="@+id/startPanel"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_gravity="center"
        android:orientation="vertical"
        android:gravity="center"
        android:paddingStart="36dp"
        android:paddingEnd="36dp">

        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:gravity="center"
            android:lineSpacingExtra="5dp"
            android:text="@string/scan_aim_hint"
            android:textColor="#99FFFFFF"
            android:textSize="13sp" />

        <TextView
            android:id="@+id/btnStartScan"
            style="@style/GlassButton"
            android:layout_marginTop="22dp"
            android:layout_height="58dp"
            android:text="@string/scan_tap_start" />
    </LinearLayout>

    <!-- 相机运行中的细进度条 -->
    <ProgressBar
        android:id="@+id/scanProgress"
        style="?android:attr/progressBarStyleHorizontal"
        android:layout_width="match_parent"
        android:layout_height="2dp"
        android:layout_gravity="bottom"
        android:indeterminate="true"
        android:visibility="gone" />

    <!-- 扫完的结果面板 -->
    <LinearLayout
        android:id="@+id/resultPanel"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_gravity="bottom"
        android:orientation="vertical"
        android:background="@drawable/bg_scrim_bottom"
        android:padding="20dp"
        android:visibility="gone">

        <TextView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="@string/quick_title"
            android:textColor="#FFFFFF"
            android:textSize="15sp"
            android:textStyle="bold" />

        <TextView
            android:id="@+id/resultDetail"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginTop="8dp"
            android:textColor="#CCFFFFFF"
            android:textSize="11sp"
            android:fontFamily="monospace" />

        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginTop="14dp"
            android:orientation="horizontal">

            <TextView
                android:id="@+id/btnRetryLaunch"
                style="@style/GlassButton"
                android:layout_width="0dp"
                android:layout_weight="1"
                android:layout_height="48dp"
                android:text="@string/btn_retry_launch"
                android:textSize="12sp" />

            <TextView
                android:id="@+id/btnRescan"
                style="@style/GlassButtonOutline"
                android:layout_width="0dp"
                android:layout_weight="1"
                android:layout_height="48dp"
                android:layout_marginStart="8dp"
                android:text="@string/btn_rescan"
                android:textSize="12sp" />
        </LinearLayout>
    </LinearLayout>

    <!-- 左上角返回（玻璃胶囊） -->
    <TextView
        android:id="@+id/btnScanBack"
        android:layout_width="wrap_content"
        android:layout_height="40dp"
        android:layout_gravity="top|start"
        android:layout_marginTop="44dp"
        android:layout_marginStart="16dp"
        android:background="@drawable/bg_glass_button_outline"
        android:gravity="center"
        android:paddingStart="16dp"
        android:paddingEnd="16dp"
        android:text="@string/btn_scan_back"
        android:textColor="#D9FFFFFF"
        android:textSize="12sp" />

    <!-- 右上角设置入口（玻璃胶囊） -->
    <TextView
        android:id="@+id/btnSettings"
        android:layout_width="wrap_content"
        android:layout_height="40dp"
        android:layout_gravity="top|end"
        android:layout_marginTop="44dp"
        android:layout_marginEnd="16dp"
        android:background="@drawable/bg_glass_button_outline"
        android:gravity="center"
        android:paddingStart="16dp"
        android:paddingEnd="16dp"
        android:text="@string/title_settings"
        android:textColor="#D9FFFFFF"
        android:textSize="12sp" />

</FrameLayout>
``````

## app/src/main/res/layout/activity_settings.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@drawable/bg_app_gradient">

    <ScrollView
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:fillViewport="true"
        android:layout_margin="12dp"
        android:background="@drawable/bg_glass_panel_light"
        android:padding="4dp">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical"
        android:padding="16dp">

        <!-- 策略 -->
        <TextView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="@string/settings_section_strategy"
            android:textStyle="bold"
            android:textSize="15sp" />

        <RadioGroup
            android:id="@+id/rgStrategy"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="vertical">

            <RadioButton
                android:id="@+id/rbA"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:text="@string/settings_strategy_a"
                android:textSize="13sp" />

            <RadioButton
                android:id="@+id/rbB"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:text="@string/settings_strategy_b"
                android:textSize="13sp" />

            <RadioButton
                android:id="@+id/rbC"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:text="@string/settings_strategy_c"
                android:textSize="13sp" />
        </RadioGroup>

        <View
            android:layout_width="match_parent"
            android:layout_height="1dp"
            android:layout_marginTop="12dp"
            android:layout_marginBottom="12dp"
            android:background="#1A0B1220" />

        <!-- 行为 -->
        <TextView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="@string/settings_section_behavior"
            android:textStyle="bold"
            android:textSize="15sp" />

        <CheckBox
            android:id="@+id/cbDebug"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="@string/settings_debug"
            android:textSize="13sp" />

        <TextView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="@string/settings_debug_note"
            android:textSize="11sp"
            android:textColor="#7A000000"
            android:paddingStart="8dp"
            android:paddingEnd="8dp"
            android:paddingBottom="8dp" />

        <CheckBox
            android:id="@+id/cbAutoFallback"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="@string/settings_auto_fallback"
            android:textSize="13sp" />

        <!-- 自动解锁：默认关闭，开了才让无障碍服务在待命窗口内动手 -->
        <TextView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginTop="4dp"
            android:text="@string/settings_section_auto"
            android:textStyle="bold"
            android:textSize="15sp" />

        <CheckBox
            android:id="@+id/cbAutoUnlock"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="@string/settings_auto_unlock"
            android:textSize="13sp" />

        <TextView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="@string/settings_auto_unlock_note"
            android:textSize="11sp"
            android:textColor="#7A000000"
            android:paddingStart="8dp"
            android:paddingEnd="8dp"
            android:paddingBottom="8dp" />

        <!-- 稳定等待：页面加载完再等这么久才点，避免点在还没接上设备状态的页面上 -->
        <TextView
            android:id="@+id/tvAutoDelay"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="@string/settings_auto_delay"
            android:textSize="13sp"
            android:paddingStart="8dp"
            android:paddingEnd="8dp" />

        <SeekBar
            android:id="@+id/sbAutoDelay"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:max="30"
            android:layout_marginStart="8dp"
            android:layout_marginEnd="8dp" />

        <TextView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="@string/settings_auto_delay_note"
            android:textSize="11sp"
            android:textColor="#7A000000"
            android:paddingStart="8dp"
            android:paddingEnd="8dp"
            android:paddingBottom="8dp" />

        <Button
            android:id="@+id/btnOpenAccessibility"
            style="?attr/materialButtonOutlinedStyle"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="@string/settings_open_accessibility"
            android:textSize="13sp" />

        <!--
          无障碍授权的「自愈」状态 + 一键补回。
          放在这里是因为：授权被系统清掉之后，用户第一反应就是来设置页，
          不该让他再往系统设置里翻一遍。
        -->
        <TextView
            android:id="@+id/tvA11yStatus"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:paddingStart="8dp"
            android:paddingEnd="8dp"
            android:paddingTop="10dp"
            android:text="@string/a11y_status_unknown"
            android:textSize="12sp"
            android:textColor="#B3000000" />

        <Button
            android:id="@+id/btnHealAccessibility"
            style="?attr/materialButtonOutlinedStyle"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="@string/a11y_heal"
            android:textSize="13sp" />

        <TextView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:paddingStart="8dp"
            android:paddingEnd="8dp"
            android:paddingTop="4dp"
            android:paddingBottom="8dp"
            android:text="@string/a11y_heal_note"
            android:textSize="11sp"
            android:textColor="#7A000000" />

        <View
            android:layout_width="match_parent"
            android:layout_height="1dp"
            android:layout_marginTop="12dp"
            android:layout_marginBottom="12dp"
            android:background="#1A0B1220" />

        <!-- 策略 B 参数 -->
        <TextView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="@string/settings_section_b"
            android:textStyle="bold"
            android:textSize="15sp" />

        <TextView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="@string/settings_b_note"
            android:textSize="11sp"
            android:textColor="#7A000000"
            android:paddingBottom="8dp" />

        <EditText
            android:id="@+id/etAppId"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:hint="@string/settings_appid_hint"
            android:inputType="text"
            android:textSize="13sp"
            android:autofillHints="" />

        <TextView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="@string/settings_appid"
            android:textSize="11sp"
            android:textColor="#99000000" />

        <EditText
            android:id="@+id/etPage"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginTop="8dp"
            android:hint="@string/settings_page_hint"
            android:inputType="text"
            android:textSize="13sp"
            android:autofillHints="" />

        <TextView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="@string/settings_page"
            android:textSize="11sp"
            android:textColor="#99000000" />

        <EditText
            android:id="@+id/etQuery"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginTop="8dp"
            android:hint="@string/settings_query_hint"
            android:inputType="text"
            android:textSize="13sp"
            android:autofillHints="" />

        <TextView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="@string/settings_query"
            android:textSize="11sp"
            android:textColor="#99000000" />

        <EditText
            android:id="@+id/etQueryNqt"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginTop="8dp"
            android:hint="@string/settings_query_nqt_hint"
            android:inputType="text"
            android:textSize="13sp"
            android:autofillHints="" />

        <TextView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="@string/settings_query_nqt"
            android:textSize="11sp"
            android:textColor="#99000000" />

        <View
            android:layout_width="match_parent"
            android:layout_height="1dp"
            android:layout_marginTop="12dp"
            android:layout_marginBottom="12dp"
            android:background="#1A0B1220" />

        <!-- 策略 C 参数 -->
        <TextView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="@string/settings_section_c"
            android:textStyle="bold"
            android:textSize="15sp" />

        <CheckBox
            android:id="@+id/cbAlipayUa"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="@string/settings_alipay_ua"
            android:textSize="13sp" />

        <EditText
            android:id="@+id/etUa"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:hint="@string/settings_ua"
            android:inputType="textMultiLine"
            android:minLines="2"
            android:gravity="top"
            android:textSize="11sp"
            android:autofillHints="" />

        <CheckBox
            android:id="@+id/cbWebScheme"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="@string/settings_web_scheme"
            android:textSize="13sp" />

        <View
            android:layout_width="match_parent"
            android:layout_height="1dp"
            android:layout_marginTop="12dp"
            android:layout_marginBottom="12dp"
            android:background="#1A0B1220" />

        <!-- 测试 -->
        <TextView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="@string/settings_section_test"
            android:textStyle="bold"
            android:textSize="15sp" />

        <EditText
            android:id="@+id/etSn"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:hint="@string/settings_sn_hint"
            android:inputType="textNoSuggestions"
            android:textSize="13sp"
            android:autofillHints="" />

        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="horizontal">

            <Button
                android:id="@+id/btnTestA"
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                android:layout_weight="1"
                android:text="@string/settings_test_a"
                android:textSize="11sp" />

            <Button
                android:id="@+id/btnTestB"
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                android:layout_weight="1"
                android:layout_marginStart="4dp"
                android:text="@string/settings_test_b"
                android:textSize="11sp" />

            <Button
                android:id="@+id/btnTestC"
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                android:layout_weight="1"
                android:layout_marginStart="4dp"
                android:text="@string/settings_test_c"
                android:textSize="11sp" />
        </LinearLayout>

        <TextView
            android:id="@+id/tvResolve"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginTop="8dp"
            android:fontFamily="monospace"
            android:textSize="11sp"
            android:textColor="#E6000000"
            android:background="#0F000000"
            android:padding="8dp" />

        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginTop="12dp"
            android:orientation="horizontal">

            <Button
                android:id="@+id/btnSave"
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                android:layout_weight="1"
                android:text="@string/settings_save" />

            <Button
                android:id="@+id/btnShowResolve"
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                android:layout_weight="1"
                android:layout_marginStart="6dp"
                android:text="@string/settings_show_resolve"
                android:textSize="11sp" />

            <Button
                android:id="@+id/btnReset"
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                android:layout_weight="1"
                android:layout_marginStart="6dp"
                android:text="@string/settings_reset"
                android:textSize="11sp" />
        </LinearLayout>

    </LinearLayout>
    </ScrollView>
</FrameLayout>
``````

## app/src/main/res/layout/activity_webview.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical">

    <ProgressBar
        android:id="@+id/progress"
        style="?android:attr/progressBarStyleHorizontal"
        android:layout_width="match_parent"
        android:layout_height="3dp"
        android:visibility="gone" />

    <TextView
        android:id="@+id/statusText"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:paddingStart="16dp"
        android:paddingTop="8dp"
        android:paddingEnd="16dp"
        android:paddingBottom="4dp"
        android:textSize="12sp"
        android:textColor="#666666"
        android:text="@string/web_loading" />

    <WebView
        android:id="@+id/webView"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_weight="1" />

    <LinearLayout
        android:id="@+id/fallbackBar"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="horizontal"
        android:padding="8dp"
        android:visibility="gone">

        <Button
            android:id="@+id/btnOpenExternal"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:text="用系统浏览器打开"
            android:textSize="11sp" />

        <Button
            android:id="@+id/btnCopy"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:layout_marginStart="6dp"
            android:text="复制链接"
            android:textSize="11sp" />

        <Button
            android:id="@+id/btnClose"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:layout_marginStart="6dp"
            android:text="关闭"
            android:textSize="11sp" />
    </LinearLayout>

</LinearLayout>
``````

## app/src/main/res/layout/activity_widget_config.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<!--
  1×1 小部件的配置页：选这个方块代表哪台设备。

  从 App 里点「放到桌面」进来时它不会显示（pending 已经在手，直接绑完就退），
  所以这个界面主要是给「手动从选择器拖出来」和「绑定失效后重选」用的。
-->
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@drawable/bg_app_gradient"
    android:orientation="vertical"
    android:paddingTop="120dp"
    android:paddingBottom="32dp">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:gravity="center_vertical"
        android:orientation="horizontal"
        android:paddingStart="24dp"
        android:paddingEnd="24dp">

        <TextView
            android:id="@+id/tvWidgetConfigTitle"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:fontFamily="sans-serif-medium"
            android:text="@string/widget_config_title"
            android:textColor="#FFFFFF"
            android:textSize="21sp" />

        <TextView
            android:id="@+id/tvWidgetConfigBack"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:background="@drawable/bg_glass_button_outline"
            android:paddingStart="18dp"
            android:paddingTop="9dp"
            android:paddingEnd="18dp"
            android:paddingBottom="9dp"
            android:text="@string/widget_config_back"
            android:textColor="#E6FFFFFF"
            android:textSize="13sp" />
    </LinearLayout>

    <TextView
        android:id="@+id/tvWidgetConfigHint"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginTop="10dp"
        android:paddingStart="24dp"
        android:paddingEnd="24dp"
        android:text="@string/widget_config_hint"
        android:textColor="#A6FFFFFF"
        android:textSize="12sp" />

    <TextView
        android:id="@+id/tvWidgetConfigEmpty"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginTop="28dp"
        android:paddingStart="24dp"
        android:paddingEnd="24dp"
        android:text="@string/widget_config_empty"
        android:textColor="#A6FFFFFF"
        android:textSize="14sp"
        android:visibility="gone" />

    <ScrollView
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_marginTop="24dp"
        android:layout_weight="1"
        android:paddingStart="20dp"
        android:paddingEnd="20dp">

        <LinearLayout
            android:id="@+id/containerWidgetChoices"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="vertical" />
    </ScrollView>

</LinearLayout>
``````

## app/src/main/res/layout/item_device.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<!--
  设备列表的一行（液态玻璃版）。

  id 与旧版一致（rowDevice / tvDeviceName / btnDeviceMore），
  DevicesActivity 的 findViewById 全部照旧能用。

  排版：左边一行名字，右边一个圆形「⋯」。
  刻意不再放第二行小字（设备号 / 上次使用时间）：
  名字就是用户自己起的那个叫法，需要核对机身时看行菜单标题即可。
-->
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:id="@+id/rowDevice"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="horizontal"
    android:gravity="center_vertical"
    android:background="@drawable/bg_glass_row"
    android:clickable="true"
    android:focusable="true"
    android:layout_marginStart="4dp"
    android:layout_marginEnd="4dp"
    android:layout_marginBottom="8dp"
    android:paddingStart="16dp"
    android:paddingEnd="8dp"
    android:paddingTop="18dp"
    android:paddingBottom="18dp">

    <TextView
        android:id="@+id/tvDeviceName"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_weight="1"
        android:ellipsize="end"
        android:maxLines="1"
        android:textColor="#FFFFFF"
        android:textSize="16sp"
        android:textStyle="bold" />

    <!--
      「⋯」用 TextView 而不是 Button：
      Material Button 自带背景/最小宽度/内边距，套在玻璃行里会很突兀，
      而且它默认强制大写、会把这个符号渲染得很怪。
    -->
    <TextView
        android:id="@+id/btnDeviceMore"
        android:layout_width="44dp"
        android:layout_height="44dp"
        android:background="@drawable/bg_glass_circle"
        android:clickable="true"
        android:focusable="true"
        android:gravity="center"
        android:text="@string/devices_more"
        android:textColor="#CCFFFFFF"
        android:textSize="16sp" />

</LinearLayout>
``````

## app/src/main/res/layout/item_widget_choice.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<!-- 配置页里一行可选的设备。复用设备列表那套玻璃卡片语言。 -->
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_marginBottom="10dp"
    android:background="@drawable/bg_glass_card"
    android:clickable="true"
    android:focusable="true"
    android:gravity="center_vertical"
    android:orientation="horizontal"
    android:paddingStart="18dp"
    android:paddingTop="16dp"
    android:paddingEnd="18dp"
    android:paddingBottom="16dp">

    <LinearLayout
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_weight="1"
        android:orientation="vertical">

        <TextView
            android:id="@+id/tvChoiceName"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:ellipsize="end"
            android:maxLines="1"
            android:textColor="#FFFFFF"
            android:textSize="16sp"
            android:textStyle="bold" />

        <TextView
            android:id="@+id/tvChoiceSub"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginTop="3dp"
            android:ellipsize="end"
            android:maxLines="1"
            android:textColor="#8AFFFFFF"
            android:textSize="11sp" />
    </LinearLayout>

    <TextView
        android:id="@+id/tvChoiceCheck"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginStart="12dp"
        android:text="✓"
        android:textColor="#8FD8FF"
        android:textSize="18sp"
        android:textStyle="bold"
        android:visibility="invisible" />

</LinearLayout>
``````

## app/src/main/res/layout/widget_cell.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<!--
  单台设备的 1×1 小部件。

  只有两样东西：一个区分「饮水器 / 热水控制」的小图标，加一行名字。
  一行名字就够 —— 1×1 里塞两行字时，第二行必然被裁掉半截，
  而且桌面图标下面本来就有系统自带的一行字，再加就成了三层。

  尺寸按「一格」的实际可用面积倒推（2026-11-07 实测）：一格约 70dp，
  减去桌面自己的 8dp 边距，真正能画的不到 60dp。所以图标压到 26dp、
  名字 10sp 单行、整体 padding 4dp —— 一开始按 30dp+11sp+8dp 写，
  在实际一格里的效果是名字被压掉半行。

  注意：RemoteViews 支持范围很窄，只能用 LinearLayout / FrameLayout /
  TextView / ImageView 这类原生控件，不能用 ConstraintLayout、不能用
  自定义 View、不能写 drawableStart 之后再改，所以层级全是写死的。
-->
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:id="@+id/widgetCellRoot"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@drawable/bg_widget_cell"
    android:clickable="true"
    android:focusable="true"
    android:gravity="center"
    android:orientation="vertical"
    android:padding="4dp">

    <ImageView
        android:id="@+id/widgetCellIcon"
        android:layout_width="26dp"
        android:layout_height="26dp"
        android:contentDescription="@null"
        android:scaleType="fitCenter"
        android:src="@drawable/ic_widget_water" />

    <TextView
        android:id="@+id/widgetCellName"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginTop="2dp"
        android:ellipsize="end"
        android:gravity="center"
        android:includeFontPadding="false"
        android:maxLines="1"
        android:textColor="#FFFFFF"
        android:textSize="10sp"
        android:textStyle="bold" />

</LinearLayout>
``````

## app/src/main/res/mipmap-anydpi-v26/ic_launcher.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<!-- minSdk=26，自适应图标已全平台支持，无需再放各密度 PNG -->
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@drawable/ic_launcher_background" />
    <foreground android:drawable="@drawable/ic_launcher_foreground" />
    <monochrome android:drawable="@drawable/ic_launcher_foreground" />
</adaptive-icon>
``````

## app/src/main/res/values/glass_styles.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>

    <!--
      ── 液态玻璃设计语言 ──

      三层同轴：
        页面  bg_app_gradient（最深，两团柔光）
        卡片  bg_glass_card     （半透明，22dp 圆角，亮边）
        行/按钮 bg_glass_row / bg_glass_button（最亮，圆角更大）

      文字三档：
        主文 #FFFFFF（标题、设备名）
        次文 #CCFFFFFF（说明、副标题）
        弱文 #80FFFFFF（提示、描述）

      刻意不用 Material 的实心色块：玻璃感来自"底色能透上来"，
      任何一块不透明填充都会把整页气氛破坏掉。
    -->

    <style name="GlassButton">
        <item name="android:layout_width">match_parent</item>
        <item name="android:layout_height">54dp</item>
        <item name="android:background">@drawable/bg_glass_button</item>
        <item name="android:textColor">#FFFFFF</item>
        <item name="android:textSize">15sp</item>
        <item name="android:textStyle">bold</item>
        <item name="android:letterSpacing">0.04</item>
        <item name="android:stateListAnimator">@null</item>
        <item name="android:gravity">center</item>
    </style>

    <style name="GlassButtonOutline">
        <item name="android:layout_width">match_parent</item>
        <item name="android:layout_height">50dp</item>
        <item name="android:background">@drawable/bg_glass_button_outline</item>
        <item name="android:textColor">#D9FFFFFF</item>
        <item name="android:textSize">13sp</item>
        <item name="android:letterSpacing">0.04</item>
        <item name="android:stateListAnimator">@null</item>
        <item name="android:gravity">center</item>
    </style>

    <style name="GlassTitle">
        <item name="android:textColor">#FFFFFF</item>
        <item name="android:textSize">26sp</item>
        <item name="android:textStyle">bold</item>
        <item name="android:letterSpacing">-0.01</item>
    </style>


</resources>
``````

## app/src/main/res/values/strings.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="app_name">QrJump</string>
    <string name="title_settings">设置</string>
    <string name="title_webview">内置浏览器（策略 C）</string>

    <!-- 扫码页 -->
    <string name="btn_scan_back">返回设备列表</string>
    <string name="scan_starting">相机启动中…</string>
    <string name="scan_scanning">对准饮水器上的二维码</string>
    <string name="scan_camera_failed">相机启动失败：%1$s</string>
    <string name="scan_rejected">这不是胖乖设备的二维码</string>
    <string name="msg_need_camera">需要相机权限才能扫码</string>
    <string name="msg_need_camera_hint">请到「系统设置 → 应用 → QrJump → 权限」里手动开启相机权限</string>
    <string name="msg_no_last_url">还没有扫描到有效二维码</string>

    <string name="result_id_kind">标识（%1$s）：%2$s</string>
    <string name="result_url">链接：%1$s</string>
    <string name="result_params">参数：%1$s</string>
    <string name="result_strategy">当前策略：%1$s</string>
    <string name="result_debug_on">（调试模式已开启，不会真正唤起）</string>

    <string name="status_started">已用策略 %1$s 唤起，正在跳转…</string>
    <string name="status_dryrun">策略 %1$s：调试模式，未真正唤起</string>
    <string name="status_failed">未成功：%1$s</string>

    <string name="toast_rejected">只认 h5.qiekj.com/skip 的设备码</string>
    <string name="toast_dryrun">调试模式：只打印日志，没有唤起</string>
    <string name="toast_mlkit_fallback">本机 ML Kit 不可用，已自动切换到 ZXing 解码</string>

    <string name="btn_rescan">继续扫描</string>
    <string name="btn_retry_launch">重新唤起</string>

    <!-- 设备 ID 展示 -->
    <string name="quick_title">上次使用的设备</string>
    <string name="quick_id_sn">设备号 %1$s</string>
    <string name="quick_id_nqt">设备标识 %1$s</string>

    <!-- 设备列表 -->
    <string name="devices_clear">清空全部</string>
    <string name="devices_clear_confirm">将删除全部 %1$d 台设备的记忆（不影响支付宝里的小程序，也不影响任何账户数据）。继续？</string>
    <string name="devices_cleared">已清空设备列表</string>
    <string name="devices_rename">改个名字</string>
    <string name="devices_delete">删除这一台</string>
    <string name="devices_rename_hint">例如「北3栋201 冷水」</string>
    <string name="devices_renamed">已改名</string>
    <string name="devices_deleted">已删除</string>
    <string name="devices_more">⋯</string>
    <string name="devices_pin">放到桌面</string>
    <string name="devices_pin_ok">已请求添加到桌面。系统可能会弹一个确认框，点「添加」即可。</string>
    <string name="devices_pin_widget_ok">桌面不接受独立快捷方式，已改为请求放置「这台设备」的 1×1 方块。若桌面没反应，长按桌面空白处 → 添加小部件 → 「QrJump 设备」手动拖一个，拖出来时会问你要绑哪台。</string>
    <string name="devices_pin_failed">这台机器的桌面既不接受快捷方式，也不接受小部件请求。请手动添加：长按桌面空白处 → 添加小部件 → 「QrJump 设备」</string>

    <!-- 桌面小部件（一台设备一个 1×1 方块） -->
    <string name="widget_desc">一台设备一个方块，点一下直接进设备页（不代替你确认出水）</string>
    <string name="widget_cell_unbound">未绑定\n点一下选择</string>
    <string name="widget_cell_gone">已失效\n点一下重选</string>
    <string name="widget_config_title">这个方块代表哪台设备？</string>
    <string name="widget_config_hint">选一台。之后点这个方块就直接进它的设备页。</string>
    <string name="widget_config_empty">设备列表还是空的。先打开 QrJump 扫码添加一台，再回来放小部件。</string>
    <string name="widget_config_back">返回</string>
    <string name="widget_config_failed">没能绑定这台设备，请重新放置小部件</string>

    <!-- 策略 C -->
    <string name="web_loading">页面加载中…</string>
    <string name="web_loaded">页面已加载</string>
    <string name="web_error">页面加载失败（错误码 %1$d）</string>
    <string name="web_no_handler">没有 App 能处理 %1$s 链接</string>
    <string name="web_scheme_failed">跳转失败：%1$s</string>

    <!-- 设备列表（启动页） -->
    <string name="devices_title">我的设备</string>
    <string name="devices_add">扫码添加设备</string>
    <string name="devices_settings">设置</string>
    <string name="devices_empty">还没有设备。点下面的「扫码添加设备」，对准饮水器或热水器机身上的二维码扫一次，它就会出现在这里。</string>
    <string name="devices_a11y_warn">自动解锁的无障碍授权没了，点这一条去打开它（只需一次）</string>
    <string name="devices_a11y_open_failed">打不开系统设置，请手动到「设置 → 无障碍」里打开 QrJump</string>

    <!-- 扫码页：按键触发 -->
    <string name="scan_tap_start">点击开始扫码</string>
    <string name="scan_title">扫一扫机身二维码</string>
    <string name="scan_aim_hint">对准机身贴纸上那个正方形二维码，扫到即自动校验并唤起</string>
    <string name="scan_start_hint">相机不会自动打开，点下面的按钮才会启动</string>
    <string name="scan_stopped">相机已暂停</string>

    <!-- 自动点击「立即使用」 -->
    <string name="settings_section_auto">自动解锁</string>
    <string name="settings_auto_unlock">到设备页后自动点「立即使用」</string>
    <string name="settings_auto_unlock_note">点完即开始出水并按官方规则计费，没有二次确认。</string>
    <string name="settings_open_accessibility">打开系统的无障碍设置</string>
    <string name="a11y_status_unknown">无障碍状态：读取中…</string>
    <string name="a11y_heal">尝试自动恢复无障碍授权</string>
    <string name="a11y_heal_note">MIUI 把「从最近任务划掉卡片」当作强制停止，会清空无障碍授权。拿到 WRITE_SECURE_SETTINGS 后本应用会自己写回来，该权限只能 adb 给：\nadb shell pm grant com.local.qrjump android.permission.WRITE_SECURE_SETTINGS</string>
    <string name="a11y_heal_ok">已恢复无障碍授权，系统会在几秒内重新绑定</string>
    <string name="a11y_heal_failed">写入没有生效，系统拒绝了。请到系统的无障碍设置里手动打开一次</string>
    <string name="a11y_heal_no_perm">还没有 WRITE_SECURE_SETTINGS，无法自动恢复。用 adb 执行一次 pm grant 再来试</string>
    <string name="settings_auto_delay">认出设备页后再等 %1$d 秒才点击</string>
    <string name="settings_auto_delay_note">「内容准备中」是页面上独立的慢区域，不会自己消失，所以不拿它当闸门：从认出设备页起等满设定秒数再点。慢了就往右调（最多 30 秒）。</string>
    <string name="toast_accessibility_needed">需要在系统设置里为 QrJump 打开无障碍服务，自动点击才会生效</string>
    <!-- 设置页 -->
    <string name="settings_saved">已保存</string>
    <string name="settings_reset">恢复默认</string>
    <string name="settings_reset_confirm">将把 appId/page/query 恢复为实测默认值、清空自定义 UA，并把策略恢复为 B。继续？</string>
    <string name="settings_need_sn">请先填一个设备号或整条二维码链接用于测试</string>
    <string name="settings_sn_invalid">SN 需 8–32 位纯数字，NQT 需标准 UUID，也可以直接粘贴整条二维码链接</string>
    <string name="settings_sn_hint">可填：15 位 SN / UUID 形式的 NQT / 整条二维码链接</string>

    <string name="settings_section_strategy">唤起策略</string>
    <string name="settings_strategy_a">A · 打开原始 URL（只到下载页）</string>
    <string name="settings_strategy_b">B · 经支付宝唤起小程序（默认）</string>
    <string name="settings_strategy_c">C · 内置 WebView（外抛官方 scheme）</string>

    <string name="settings_section_behavior">行为</string>
    <string name="settings_debug">调试模式（只打印日志，不真正唤起）</string>
    <string name="settings_debug_note">只记录解析结果与将要执行的 Intent，不进设备页，避免误触发扣费。</string>
    <string name="settings_auto_fallback">启用自动降级（B → C）</string>
    <string name="shortcut_bad_key">这个快捷方式没有关联到设备，请从应用里重新长按图标创建</string>
    <string name="shortcut_missing">这台设备已从设备列表里删除，请重新扫码</string>

    <string name="settings_section_b">策略 B 参数（来源：官方分发页自己发起的跳转）</string>
    <string name="settings_appid">小程序 appId</string>
    <string name="settings_appid_hint">清空则本档不可用，退化为只用 A/C</string>
    <string name="settings_page">小程序页面路径 page</string>
    <string name="settings_page_hint">清空则本档不可用，退化为只用 A/C</string>
    <string name="settings_query">query 模板（形态①/饮水器，{SN} 会被替换成真实设备号）</string>
    <string name="settings_query_hint">保留 {SN} 占位符</string>
    <string name="settings_query_nqt">query 模板（形态②/热水控制，留空=直传官方 query）</string>
    <string name="settings_query_nqt_hint">留空即把机身码里的 Company/CommunicateType/Ver/NQT 原样带过去；可用 {NQT} {QUERY} 占位符</string>
    <string name="settings_b_note">这三个默认值来自实测：用策略 C 打开原始 URL 时，官方分发页自己会发起一次 alipays:// 跳转，本应用只是把那次跳转打印出来并接手执行 —— 没有抓包、没有逆向接口。清空即本档不可用。</string>

    <string name="settings_section_c">策略 C 参数</string>
    <string name="settings_alipay_ua">伪装支付宝 UA</string>
    <string name="settings_ua">自定义 UA（留空用内置默认）</string>
    <string name="settings_web_scheme">允许页面把 alipays:// 等 scheme 外抛给支付宝</string>

    <string name="settings_section_test">测试</string>
    <string name="settings_test_a">用策略 A 试</string>
    <string name="settings_test_b">用策略 B 试</string>
    <string name="settings_test_c">用策略 C 试</string>
    <string name="settings_show_resolve">刷新 resolveActivity 结果</string>
    <string name="settings_save">保存参数</string>
    <string name="accessibility_label">QrJump 自动点击「立即使用」</string>
    <string name="accessibility_desc">仅在从 QrJump 唤起官方设备页后的 2 分钟内生效，用来替你在官方页面上点一次「立即使用」。不做任何其它操作，不读取屏幕上的账号、密码、余额等信息。</string>
</resources>
``````

## app/src/main/res/values/themes.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <style name="Theme.QrJump" parent="Theme.Material3.DayNight.NoActionBar">
        <item name="colorPrimary">#1F6FEB</item>
        <item name="colorOnPrimary">#FFFFFF</item>
        <item name="colorSecondary">#0B5CD5</item>
        <item name="android:windowBackground">@drawable/bg_app_gradient</item>
        <!-- 旧主题也要透明状态栏，避免短暂白屏/蓝带漏出来 -->
        <item name="android:statusBarColor">@android:color/transparent</item>
        <item name="android:navigationBarColor">@android:color/transparent</item>
    </style>

    <!-- 扫码页全屏黑底，相机预览铺满 -->
    <style name="Theme.QrJump.Fullscreen" parent="Theme.QrJump">
        <item name="android:windowBackground">@android:color/black</item>
        <item name="android:statusBarColor">@android:color/transparent</item>
        <item name="android:navigationBarColor">@android:color/transparent</item>
        <item name="android:windowTranslucentStatus">true</item>
    </style>

    <!--
      快捷方式落点用的透明主题：不画界面、不闪白屏，
      点完桌面图标后直接把官方页面顶上来。
    -->
    <style name="Theme.QrJump.Transparent" parent="Theme.QrJump">
        <item name="android:windowBackground">@android:color/transparent</item>
        <item name="android:windowIsTranslucent">true</item>
        <item name="android:windowNoTitle">true</item>
        <item name="android:windowContentOverlay">@null</item>
        <item name="android:windowAnimationStyle">@null</item>
        <item name="android:statusBarColor">@android:color/transparent</item>
    </style>

    <!--
      设置页与 WebView 页专用的【固定浅色】主题。

      为什么必须固定成浅色、不能用 DayNight：
      设置页现在是一块近白色的磨砂玻璃面板（bg_glass_panel_light）。
      如果跟着系统进深色模式，Material3 会把默认文字刷成白色，
      白字压在浅玻璃上 = 整页看不见。这种"局部反色"是玻璃拟态最常见的翻车点，
      解决办法不是给每个 TextView 手写颜色，而是让这一页永远走浅色分支。
    -->
    <style name="Theme.QrJump.Light" parent="Theme.Material3.Light.NoActionBar">
        <item name="colorPrimary">#1F6FEB</item>
        <item name="colorOnPrimary">#FFFFFF</item>
        <item name="colorSecondary">#0B5CD5</item>
        <item name="android:windowBackground">@drawable/bg_app_gradient</item>
        <item name="android:forceDarkAllowed">false</item>
        <!--
          状态栏必须透明，否则它会用 colorPrimary 刷成一条不透明蓝带，
          正好压在紫色渐变顶上，一眼就"破功"。
          透明之后渐变会一直延伸到屏幕顶端，玻璃面板像是浮在光里。
        -->
        <item name="android:statusBarColor">@android:color/transparent</item>
        <item name="android:navigationBarColor">@android:color/transparent</item>
        <item name="android:windowLightStatusBar">false</item>
    </style>

    <!-- 设备列表：深色玻璃，主题也固定深色，保证白字始终对 -->
    <style name="Theme.QrJump.Dark" parent="Theme.Material3.Dark.NoActionBar">
        <item name="colorPrimary">#2EC5FF</item>
        <item name="colorOnPrimary">#08111F</item>
        <item name="colorSecondary">#8B5CF6</item>
        <item name="android:windowBackground">@drawable/bg_app_gradient</item>
        <item name="android:statusBarColor">@android:color/transparent</item>
        <item name="android:navigationBarColor">#00000000</item>
    </style>
</resources>
``````

## app/src/main/res/xml/accessibility_service_config.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<!--
  无障碍服务的声明。

  accessibilityEventTypes 只订阅窗口内容变化与窗口切换、滚动：这三类足够
  发现设备页渲染完成，订阅多了只会白耗电。

  accessibilityFeedbackType="feedbackGeneric"：给系统一个通用反馈类型，
  不声明会打断说话类服务。

  canRetrieveWindowContent="true"：必须开，否则拿不到控件树（也就点不了按钮）。

  canPerformGestures="true"：必须开。2026-11-06 21:05 用 uiautomator dump 抓了
  真实设备页的控件树，发现「立即使用」是 WebView 渲染出来的 React 文本节点，
  它自己以及往上六层祖先**全部 clickable=false**，performAction(ACTION_CLICK)
  对它无效。所以真正能点动它的只有 dispatchGesture 按坐标点一下，
  而 dispatchGesture 需要这个标志位，缺了会被系统直接拒绝。

  notificationTimeout="120"：事件合并窗口，略长一点可以少被高频重绘打断。

  packageNames 刻意**不设置**：如果写死成只订阅支付宝，某些 ROM 上会漏事件；
  真正的过滤放在 AutoUnlockService.onAccessibilityEvent 里用 e.packageName 判断，
  那样更可靠、也更容易在日志里看清"因为不是支付宝所以没动手"。

  description 会展示在系统无障碍设置页里，让用户明确知道这个服务干什么、
  什么时候会动手 —— 不写会变成"未提供说明"的黑盒。
-->
<accessibility-service xmlns:android="http://schemas.android.com/apk/res/android"
    android:accessibilityEventTypes="typeWindowContentChanged|typeWindowStateChanged|typeViewScrolled"
    android:accessibilityFeedbackType="feedbackGeneric"
    android:accessibilityFlags="flagReportViewIds|flagIncludeNotImportantViews"
    android:canRetrieveWindowContent="true"
    android:canPerformGestures="true"
    android:description="@string/accessibility_desc"
    android:notificationTimeout="120"
    android:settingsActivity="com.local.qrjump.SettingsActivity" />
``````

## app/src/main/res/xml/device_widget_info.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<!--
  单设备小部件的信息声明。

  尺寸按「1×1 格」写。这里有个必须记下来的换算（2026-11-07 实测踩到）：
  Android 用 cells = ceil((size + 30) / 70) 从 dp 反推格数，
  所以 110dp 算出来是 ceil(140/70) = **2 格**，选择器里显示成「2 x 2」。
  要 1 格，size 必须 ≤ 40dp，这里取 40dp。
  targetCellWidth/Height 是 Android 12+ 的新字段，直接声明占几格，比 dp 更准；
  但在旧系统上会被忽略，所以 minWidth/minHeight 也必须写对，不能只靠它。

  resizeMode 刻意不写：方格被拉大就成了一块空地，而它上面只有一行字，
  拉大反而难看。想要大块的信息，用列表小部件比拉大这个更合适。

  updatePeriodMillis=0：不靠系统定时刷新。设备簿一变，[DeviceStore] 会主动
  调 [DeviceWidgetProvider.refresh]；改名后配置页也会 updateOne。

  configure 指向 [WidgetConfigActivity]：没有它，手动拖出来的方块不知道该绑谁。
-->
<appwidget-provider xmlns:android="http://schemas.android.com/apk/res/android"
    android:configure="com.local.qrjump.WidgetConfigActivity"
    android:description="@string/widget_desc"
    android:initialLayout="@layout/widget_cell"
    android:minHeight="40dp"
    android:minWidth="40dp"
    android:previewLayout="@layout/widget_cell"
    android:targetCellHeight="1"
    android:targetCellWidth="1"
    android:updatePeriodMillis="0"
    android:widgetCategory="home_screen" />
``````

## gradle/wrapper/gradle-wrapper.properties

```properties
distributionBase=GRADLE_USER_HOME
distributionPath=wrapper/dists
distributionUrl=https\://services.gradle.org/distributions/gradle-8.7-bin.zip
networkTimeout=10000
validateDistributionUrl=true
zipStoreBase=GRADLE_USER_HOME
zipStorePath=wrapper/dists
``````

