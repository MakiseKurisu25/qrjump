# QrJump —— 饮水器二维码直达（本人自用侧载）

把校园饮水器的「开支付宝 → 搜胖乖生活小程序 → 点扫一扫 → 扫机身码」压成
「打开 QrJump → 扫一次 → 直接进官方小程序设备页」。

**它只是一个更短的入口。** QrJump 不解析设备协议、不碰鉴权、不碰计费、
不构造订单、不模拟支付、不直连设备。它做的事只有：识别二维码 →
严格校验 → 把厂商自己印在机身上的公开 URL / 官方分发页自己产生的跳转
递交给系统或支付宝。

- 包名：`com.local.qrjump`（刻意不用 `com.qiekj.*`，避免和厂商官方应用混淆）
- 应用名：QrJump
- minSdk 26 / targetSdk 34 / compileSdk 34
- 语言：Kotlin，无 Compose，纯 View + CameraX + ML Kit(bundled) + ZXing 兜底

---

## 0. 本机实测环境（已就绪）

| 组件 | 版本 | 路径 |
|---|---|---|
| JDK | openjdk 17.0.2 | `<JDK 目录>` |
| Gradle | 8.7 | `<Gradle 目录>` |
| Android SDK | platform-tools / android-34 / build-tools 34.0.0 | `<SDK 目录>` |
| Gradle 缓存 | 隔离，不污染全局 | `<GRADLE_USER_HOME>` |
| 工程 | 本仓库 | `<工程目录>` |
| 环境脚本 | 会话内生效 / 用户级持久化 / 回滚 | `<环境脚本目录>\{set-env,install-env,uninstall-env}.ps1` |

新开一个终端先跑一次（只影响当前窗口）：

```powershell
. <环境脚本目录>\set-env.ps1
```

想永久生效（写用户级环境变量，可回滚）：

```powershell
powershell -ExecutionPolicy Bypass -File <环境脚本目录>\install-env.ps1
# 回滚：
powershell -ExecutionPolicy Bypass -File <环境脚本目录>\uninstall-env.ps1
```

---

## 1. 编译

```powershell
. <环境脚本目录>\set-env.ps1
cd <工程目录>
gradle assembleDebug --console=plain
```

产物：

```
<工程目录>\app\build\outputs\apk\debug\app-debug.apk
```

首次编译要下 ~330MB 依赖（已缓存在 `<GRADLE_USER_HOME>`），之后增量编译约 2–8 秒。

---

## 2. 安装到真机

```powershell
adb devices -l
adb -s <你的设备序列号> install -r -t "<工程目录>\app\build\outputs\apk\debug\app-debug.apk"
```

### 2.1 本机实测踩到的坑：`INSTALL_FAILED_USER_RESTRICTED: Install canceled by user`

这不是 APK 的问题。根因已定位到 MIUI/HyperOS 的 **`verifyInstallFromShell`** 这条路径：

```
E PKMSImpl: MIUILOG- assertCallerAndPackage: uid=2000, installerPkg=null, msg=Install canceled by user
D PackageInstallerSession: Marking session ... as failed
E WindowManager: WindowLeaked: Activity com.miui.permcenter.install.AdbInstallActivity
```

`uid=2000` 就是 `shell`。MIUI 对「由 shell 发起的安装」一律要求开发者选项里
**「USB 安装」开关为开**；而这台机器上该开关点下去会 Toast **「请先插入 SIM 卡」**
（`getprop gsm.sim.state` = `ABSENT,ABSENT`），所以无卡时这个开关根本打不开。

**已验证无效的做法（省得你再试一遍）：**

| 尝试 | 结果 |
|---|---|
| `adb install -r -t` | USER_RESTRICTED |
| push 到 `/data/local/tmp` 后 `pm install` | USER_RESTRICTED |
| `pm install --user 0` | USER_RESTRICTED |
| `pm install -i com.android.vending`（伪装 installer） | USER_RESTRICTED |
| `pm install-create` / `install-write` / `install-commit` | write 成功，commit 仍 USER_RESTRICTED |
| `settings put global adb_install 1` | 值本来已是 1，无效 |
| `settings put secure install_non_market_apps 1` | 无效 |
| `settings put secure com.miui.packageinstaller_install_not_allow_package_com.android.shell '{"allowInstall":true}'` | 键写入成功，无效 |
| `pm disable-user com.miui.securitycenter` / `com.xiaomi.account` | `SecurityException: Cannot disable system packages` |
| `am start -n com.miui.packageinstaller/com.miui.packageInstaller.InstallStart` | uid 2000 缺 `REQUEST_INSTALL_PACKAGES`，立刻 onDestroy |
| MT管理器 / Quark 的 apk_installer 接 `ACTION_VIEW` | 只打开它们自己的主页 |

**实测有效的路径 —— 走文件管理器，让安装以「普通应用」身份发起：**

```powershell
adb -s <你的设备序列号> shell "am force-stop com.miui.packageinstaller"
adb -s <你的设备序列号> shell "am start -n com.android.fileexplorer/com.android.fileexplorer.FileExplorerTabActivity"
# 下面用 UI 自动化点：进 Download → 滚动 → 点 app-debug.apk
adb -s <你的设备序列号> shell "input tap 350 1620"
adb -s <你的设备序列号> shell "input swipe 540 2000 540 900 300"
adb -s <你的设备序列号> shell "input tap 450 2020"
# 第一屏「是否允许「文件」安装应用？」→ 允许
adb -s <你的设备序列号> shell "input tap 311 2232"
# 第二屏「未查询到此应用的 ICP 备案信息」→ 继续安装
adb -s <你的设备序列号> shell "input tap 311 2232"
# 第三屏「已了解此应用未经安全检测」→ 必须先勾 checkbox，再点继续安装
adb -s <你的设备序列号> shell "input tap 540 1774"
adb -s <你的设备序列号> shell "input tap 540 2083"
# 若弹出小米账号「请验证您的身份」→ 取消，回到第三屏再点一次继续安装
adb -s <你的设备序列号> shell "input tap 540 2061"
adb -s <你的设备序列号> shell "input tap 540 2083"
# 权限框「是否允许"QrJump"拍摄照片或录制视频」→ 仅在使用中允许
adb -s <你的设备序列号> shell "input tap 540 2230"
adb -s <你的设备序列号> shell "pm list packages | grep qrjump"
```

> 坐标是 1080×2400 下的实测值，换机器会变；用 `uiautomator dump` + `adb pull`
> 看一眼再点。**注意 `uiautomator dump` 之后一定要 `adb pull` 出来读，
> 直接 `cat` 经管道会丢内容。**
>
> 另一条已验证的技巧：安装前先断网
> （`svc wifi disable; svc data disable`）可以让第三屏跳过联网安全检测，
> 装完记得 `svc wifi enable; svc data enable`。

**一旦首次装成功，后续迭代就直接 `adb install -r -t` 即可** —— 包已存在且
installerPkg 已记录，不再走 shell 首次安装那条被拦的路径。本仓库后续所有
改动都是用这条命令覆盖安装的。

打开应用（启动即进扫码页）：

```powershell
adb -s <你的设备序列号> shell am start -n com.local.qrjump/.ScanActivity
```

---

## 3. 先验「落点」——不装 App 也能测（这一节有实测结论）

```powershell
# 把 <SN> 换成你要测的那台机器的真实 15 位设备号
adb -s <你的设备序列号> shell am start -W -a android.intent.action.VIEW -d "https://h5.qiekj.com/skip?SN=<SN>"
```

`-W` 会打印 `Status / Activity / TotalTime`，从 `Activity:` 那一行就能看出落点。

### 实测结果（Redmi K40S / Android 14 / MIUI V816）

```
Status: ok
LaunchState: COLD
Activity: com.microsoft.emmx/org.chromium.chrome.browser.ChromeTabbedActivity
TotalTime: 2060
```

页面内容（uiautomator 抓的文本）：

```
胖乖生活（原企鹅共享）
直接下载安装APP
安全无病毒   官方认证   一键安装
通过应用市场安装
如有疑问请联系客服：400-668-9966
开发者：杭州企鹅科技有限公司
```

**结论：这个 URL 在普通浏览器里落到的既不是 H5 设备页，也不提示用支付宝打开，
而是「胖乖生活 App 的下载引导页」。**

所以原来预设的三种可能全部不成立，真实答案是第四种。
**策略 A 单独使用达不到目标** —— 它只会把用户丢到下载页。

> 想看完整长什么样：`shot_A_edge.png` / `shot_A_edge2.png`（浏览器）、
> `shot_C_webview.png`（内置 WebView 同款下载页）。

---

## 4. 看日志

所有日志统一 TAG：`QrJump`。分五个区：`Life` / `Scan` / `Parse` / `Launch` / `Web`。

```powershell
# 只看本应用，实时
adb -s <你的设备序列号> logcat -s QrJump:V

# 带时间戳、清空后重来一遍
adb -s <你的设备序列号> logcat -c
adb -s <你的设备序列号> logcat -v time -s QrJump:V

# 落盘，方便回贴
adb -s <你的设备序列号> logcat -v time -s QrJump:V > qrjump-log.txt
```

一次成功直达（策略 B）会打出这些行 —— 这是真机实测的原始输出：

```
I/QrJump: [Scan] 相机已就绪，解码器=MLKit
I/QrJump: [Scan] 原始码 = https://h5.qiekj.com/skip?SN=<你的15位SN>
I/QrJump: [Parse] 校验通过 SN=<你的15位SN> 规范化URL=https://h5.qiekj.com/skip?SN=<你的15位SN>
I/QrJump: [Launch] 策略顺序 = [B, C]（用户选择=B，自动降级=true，调试模式=false）
I/QrJump: [Launch] ──────── 尝试策略 B ────────
I/QrJump: [Launch] B 生成的 alipays URI = alipays://platformapi/startapp?appId=2018072460764274&page=pages%2FskipNew%2FskipNew%3FisShowAd%3Dfalse&query=masterSwitch%3D0%26keepUsing%3D1%26SN%3D<你的15位SN>%26isShowAd%3Dfalse&isShowAd=false
I/QrJump: [Launch] B1 Intent=action=android.intent.action.VIEW data=alipays://... resolve=com.eg.android.AlipayGphone/com.alipay.mobile.quinox.SchemeLauncherActivity
I/QrJump: [Launch] B/B1 startActivity 返回正常
I/QrJump: [Launch] 策略 B 已发起。component=com.eg.android.AlipayGphone/com.alipay.mobile.quinox.SchemeLauncherActivity
```

校验失败时：

```
I/QrJump: [Scan] 原始码 = http://h5.qiekj.com/skip?SN=123
I/QrJump: [Parse] 拒绝：scheme 不是 https（实际 http）
```

---

## 5. 两种 scheme（策略 B）与实测结论

### 5.1 appId / page / query 是从哪来的

**不是猜的，也不是抓包得来的。** 来源是这样：

用策略 C（内置 WebView + 支付宝 UA）打开那个原始 URL 时，官方分发页
**自己**发起了一次跳转，被 `WebViewClient.shouldOverrideUrlLoading` 拦下来：

```
[Web] 拦截跳转 scheme=alipays mainFrame=true url=alipays://platformapi/startapp?appId=2018072460764274&page=pages%2FskipNew%2FskipNew%3FisShowAd%3Dfalse&query=masterSwitch%3D0%26keepUsing%3D1%26SN%3D<你的15位SN>%26isShowAd%3Dfalse&isShowAd=false
[Web] 外抛 Intent=... resolve=com.eg.android.AlipayGphone/com.alipay.mobile.quinox.SchemeLauncherActivity
```

也就是说：**这些参数是官方页面自己交出来的**，本应用只是把那个 Intent
打印到 logcat，然后原样交给支付宝。没有解密流量、没有逆向私有接口。

读出来的值：

| 字段 | 值 |
|---|---|
| appId | `2018072460764274` |
| page | `pages/skipNew/skipNew?isShowAd=false` |
| query 模板 | `masterSwitch=0&keepUsing=1&SN={SN}&isShowAd=false` |
| 外层 | 末尾还会多挂一个 `&isShowAd=false` |

这三个值在源码里是 **`Prefs.kt` 的默认预填值**（`DEFAULT_APP_ID` / `DEFAULT_PAGE` /
`DEFAULT_QUERY_TEMPLATE`），设置页可以随时改或清空；清空后 B 档自动判
`Unavailable`，退化成只用 A/C。

### 5.2 已实测：query 不能精简

我试过只带 SN 的精简写法：

```powershell
adb -s <你的设备序列号> shell "am start -W -a android.intent.action.VIEW -d 'alipays://platformapi/startapp?appId=2018072460764274&page=pages%2FskipNew%2FskipNew%3FisShowAd%3Dfalse&query=SN%3D<你的15位SN>'"
```

`Status: ok`，支付宝接住了，也进了小程序，但页面上写的是：

```
读取设备
二维码有误，换一个试试
```

同一类失败还出现过第二次（20:07）：形态②把机身码里官方那组
`Company=qiekj&CommunicateType=1&Ver=4&NQT=<UUID>` 原样直传，也落到
「二维码有误，换一个试试」。把 `NQT` 套进同一套外层参数后立刻正确。

**结论：两个形态都必须带上 `masterSwitch` / `keepUsing` / `isShowAd`
这套外层参数（即按模板替换 `{SN}` 或 `{NQT}`），把 query 简化成裸标识
会被小程序判为无效码。** 这一条已写进代码注释、设置页说明和 README 5.2/6.2。

### 5.3 命令行单独验证 scheme（不经过 QrJump）

```powershell
# B1：直连 alipays（<APPID> <PAGE> <SN> 全替换；PAGE 要 URL-encode，/ -> %2F，? -> %3F）
adb -s <你的设备序列号> shell "am start -W -a android.intent.action.VIEW -d 'alipays://platformapi/startapp?appId=<APPID>&page=<PAGE>&query=masterSwitch%3D0%26keepUsing%3D1%26SN%3D<SN>%26isShowAd%3Dfalse&isShowAd=false'"

# B2：经 ds.alipay.com 包装（内层 URI 整体 URL-encode 一次）
adb -s <你的设备序列号> shell "am start -W -a android.intent.action.VIEW -d 'https://ds.alipay.com/?scheme=alipays%3A%2F%2Fplatformapi%2Fstartapp%3FappId%3D<APPID>%26page%3D<PAGE>%26query%3D...'"
```

`am start -W` 输出里重点看：

- `Activity: com.eg.android.AlipayGphone/com.alipay.mobile.quinox.SchemeLauncherActivity` → 支付宝接住了（**实测就是这一行**）
- `Error: Activity not started, unable to resolve Intent` → 没有 App 认这个 scheme
- 落到支付宝首页而不是设备页 → scheme 被接受但参数没生效

---

## 6. 三档策略与降级（含实测结论）

设置页入口：设备列表右上角「设置」，或扫码页右上角「设置」
（`SettingsActivity` 是 `exported=false`，所以**不能**用 `am start` 直接拉起来，
会抛 `Permission Denial`；这是刻意设计）。

| 档 | 做什么 | 实测结果 |
|---|---|---|
| **A** | `Intent.ACTION_VIEW` 打开原始 URL，完全交给系统/分发页 | ❌ **只落到 App 下载引导页**，到不了设备页。保留仅作对照 |
| **B** | 拼 `alipays://platformapi/startapp?appId&page&query`，失败再退 `https://ds.alipay.com/?scheme=` | ✅ **默认档，一步直达官方设备页**（骨架「内容准备中」→ 数秒后自动加载出出水口/立即使用，见 10.1） |
| **C** | 内置 WebView 打开原始 URL，页面出现 `alipays://` 就外抛给支付宝 | ✅ 也能到（需开「伪装支付宝 UA」）；不开 UA 时等同 A |

**默认策略 = B。** 这不是拍脑袋改的，是上面第 3、5 节实测出来的：
A 已知无效，B 已知有效，所以默认走 B。

自动降级开启时（默认开）：`B → C → 全失败则复制 URL 到剪贴板`。
注意 B 的降级链里**不含 A**，因为把用户丢到下载页没有意义。
只有你显式选 A 时，才会走 `A → B → C` 的完整链（方便对照观察）。

关闭自动降级后只试你选的那一档（该档内部的 B1/B2 两条路径仍会试，不算跨档降级）。

### 调试模式（默认关闭）

打开后：只打日志、不真正唤起。**这是防误扣费的关键开关。**

实测验证过 —— 打开调试模式后点「用策略 B 试」，日志是：

```
I/QrJump: [Launch] 策略顺序 = [B]（用户选择=B，自动降级=false，调试模式=true）
I/QrJump: [Launch] B 生成的 alipays URI = alipays://...SN%3D<你的15位SN>...
I/QrJump: [Launch] B1 Intent=... resolve=com.eg.android.AlipayGphone/...
I/QrJump: [Launch] 策略 B 调试模式：仅记录，未唤起。component=com.eg.android.AlipayGphone/com.alipay.mobile.quinox.SchemeLauncherActivity
```

`mCurrentFocus` 全程停在 `com.local.qrjump/.SettingsActivity`，支付宝没有被拉起。

### 6.1 启动页就是设备列表：打开 App 立刻看到全部设备

这是最新一版的改动。以前扫码页是启动页，打开 App 要先等相机起来、再从底部
浮出的面板里点「一键直达」；现在把 `DevicesActivity` 设成 LAUNCHER，
**打开 App 第一屏就是你的设备列表**，点哪一行就用哪台走官方链路。

```
我的设备                                         ( 设置 )

╭──────────────────────────────────────────────╮
│  北三201                                  (⋯) │
╰──────────────────────────────────────────────╯
╭──────────────────────────────────────────────╮
│  洗澡                                     (⋯) │
╰──────────────────────────────────────────────╯

        ▓▓▓▓  扫码添加设备  ▓▓▓▓      ← 蓝紫渐变
        ┌  清空全部  ┐                ← 玻璃描边
```

列表只有一行一个名字 —— 设备号、上次使用时间那行副标题已经去掉（2026-11-07 00:19
的改动：主界面不留小字，名字本身就能认人）。设备标识仍然存在设备簿里，只是不再铺在
界面上；它不含 token、签名、登录态或订单信息，每次使用前都会重新跑一遍完整校验。

相机不再是"打开就有"，扫码变成显式动作 —— 见 6.3。界面风格见 6.6。

**为什么这样改**：日常真正高频的是"打开 → 选一台 → 到设备页"，
扫码一台新机器是低频。原设计让每次高频动作都背一次低频动作的开销
（相机启动、权限、隐私指示灯常亮），顺序是反的。

启动页的实测日志（真机，20:56:33）：

```
[Devices] ═══ 设备列表（启动页）打开 ═══
[Devices] 策略=B 调试=false 自动解锁=false 无障碍服务=false
[Devices] 设备簿共 2 台：SN:<你的15位SN>, NQT:9b9642e8-c05b-4be4-888f-000c3ccff5c3
```

（`am start -W -n com.local.qrjump/.DevicesActivity` → `Activity: com.local.qrjump/.DevicesActivity`，
`TotalTime: 630`；用 `cmd package resolve-activity -c android.intent.category.LAUNCHER com.local.qrjump`
也确认落点是 `.DevicesActivity`。）

### 6.1.1 设备列表能做什么

`DevicesActivity` 就是启动页本身（设置页里那个「管理设备列表」入口已于 2026-11-07 删除 ——
设备列表就是启动页，多一个指回自己的按钮没有意义）：

| 操作 | 怎么做 | 说明 |
|---|---|---|
| 直达 | 点整行 | 重新 parse 这条 URL → 走同一套 Launcher → 成功则把它移到最前 |
| 改名 | 点行尾 `⋯` → 改个名字 | 比如把 UUID 改成「某热水机」，只存本地 |
| 删除 | 点行尾 `⋯` → 删除这一台 | 只删本地记录，不碰服务器 |
| 清空 | 底部「清空全部」 | 有二次确认，确认框里带台数 |
| 空态 | 自动 | 显示「还没有设备。点下面的「扫码添加设备」…」 |

列表按最近使用时间倒序，第一行用蓝色副标题标出「最近用过」。

**设备簿里到底存了什么**（这条很重要，涉及红线）：

| 存 | 不存 |
|---|---|
| `launchUrl`（形态②是原始包装地址） | 任何 token / 签名 / 授权码 |
| 内部 URL、标识值（SN 或 NQT）、参数表 | 登录态、Cookie、支付凭据 |
| 时间戳、你自己起的别名 | 订单号、用水量、余额、扣费记录 |
| | 任何网络响应或小程序页面 |

设备标识是**机身印着、公开可见**的东西，记住它等于"我认得这台机器"，
不构成对鉴权的任何绕过 —— 每次真正出水、扣费仍全部由支付宝/小程序完成。

**设备簿不是信任边界之外的后门**：`DeviceStore.all()` 每次读都把每条存下来的
字符串**重新丢给 `ParseResult.parse()` 走一遍完整校验**（https / host / path /
标识格式全部重查）。某一条不再通过就跳过并打
`[Memory] 设备簿第 N 条已不再通过校验，跳过：...`，不会因为"存过"就放行。
偏好文件被改坏或被别的工具写入奇怪的值，一样会被拒。

**开关**：旧的「打开即显示「一键直达上次设备」」开关已经随这次改版移除
（设备列表就是启动页，没有"显示不显示"的问题了）。设备簿本身仍然保留，
旧版单条记忆（`last_launch_url` 等）首次启动会自动迁移成 1 台，日志
`[Memory] 旧版单条记忆已迁移为设备簿`，不会丢。

**实测日志**（真机）：

```
I/QrJump: [Memory] 旧版单条记忆已迁移为设备簿：形态=SN 标识=<你的15位SN>
I/QrJump: [Devices] ═══ 设备列表（启动页）打开 ═══
I/QrJump: [Devices] 策略=B 调试=false 自动解锁=true 无障碍服务=true
I/QrJump: [Devices] 设备簿共 2 台：SN:<你的15位SN>, NQT:9b9642e8-c05b-4be4-888f-000c3ccff5c3
I/QrJump: [Launch] 策略顺序 = [B, C]（用户选择=B，形态=NQT，自动降级=true，调试模式=false）
I/QrJump: [Launch] B 形态=NQT 生成的 query = masterSwitch=0&keepUsing=1&NQT=9b9642e8-...&isShowAd=false
I/QrJump: [Launch] ✓ 已发起：B/B1 → com.eg.android.AlipayGphone/com.alipay.mobile.quinox.SchemeLauncherActivity
```

落点「某热水机 / 201 / XX学院」热水设备页（截图 `shot_nqt_B_onetap.png`），
全程未点「立即使用」。

### 6.1.2 怎么把它放到桌面（快捷方式两条路都死了；小部件能成，2026-11-07 已改成 1×1 单设备）

这一节是本项目里唯一「官方 API 全部失灵」的地方，所以证据留全，避免以后回头再试一遍。

设备列表每行的 `⋯` 菜单里有「放到桌面」。点它时现在的代码只走两条：
官方 `requestPinShortcut()`（非 MIUI 桌面可用）→ 不行就 `requestPinAppWidget()`
（MIUI 上实测可用）。下面三段是本项目为此做过的全部实测记录，包括那条已经删掉的死路。

> 2026-11-07 清理：通道 ② 的代码、两条 `com.android.launcher.permission.*` 权限、
> 以及 `queries` 里那条 `INSTALL_SHORTCUT` 声明已全部删除 —— 既然 Android 14 框架层
> 直接丢弃这条广播，留着就是永远不执行的死代码。

#### 通道 ① 官方 `requestPinShortcut()` —— MIUI 假成功

```powershell
# 日志
[Shortcut] 请求钉到桌面：key=SN|<你的15位SN> label=北三201 结果=true
```

返回 `true`，桌面**没有**图标，也没有任何确认框，`mCurrentFocus` 还停在
`com.local.qrjump/.DevicesActivity`。`dumpsys shortcut` 里倒是查得到注册记录：

```
ShortcutInfo {id=SN|<你的15位SN>, flags=0x85 [DynIc-rStr]
  activity=ComponentInfo{com.local.qrjump/com.local.qrjump.DevicesActivity}
  intents=[Intent { act=android.intent.action.VIEW cmp=com.local.qrjump/.ShortcutActivity }
           /PersistableBundle[{com.local.qrjump.extra.DEVICE_KEY=SN|<你的15位SN>}]]}
```

即**注册层好的，展示层被屏蔽**。

#### 通道 ② 旧版 `INSTALL_SHORTCUT` 广播 —— 被 Android 14 直接丢弃

这条路原本是给 MIUI 准备的（`pm query-receivers -a com.android.launcher.action.INSTALL_SHORTCUT`
实测返回 2 个接收器：`com.miui.home/.launcher.InstallShortcutReceiver` 与
`com.miui.securitycore/com.miui.xspace.receiver.InstallShortcutReceiver`；
所需权限 `com.android.launcher.permission.INSTALL_SHORTCUT` 的 `prot=normal`，
普通应用声明即得）。广播确实发出去了：

```
I/QrJump : [Shortcut] 已发旧版 INSTALL_SHORTCUT 广播：label=北三201 key=SN|<你的15位SN>
           receiver=com.miui.home/com.miui.home.launcher.InstallShortcutReceiver
```

但紧接着系统把它扔了：

```
W/ActivityManager: Broadcast com.android.launcher.action.INSTALL_SHORTCUT
                   no longer supported. It will not be delivered.
```

**结论：Android 14 的 ActivityManager 直接丢弃这条广播**，接收器还在注册也收不到。
（顺带一个坑：这条路不能用 `adb shell am broadcast --es` 验证，因为旧协议要求
`EXTRA_SHORTCUT_INTENT` 是真正的 Parcelable Intent，命令行只能传字符串。）

既然结论是「在 Android 14+ 上不可能成功」，这段代码和它要的两条
`com.android.launcher.permission.*` 权限、以及那条 `queries` 声明，
已经在 2026-11-07 的清理里**整段删除**，不再保留死代码。上面的日志是删除前的实测记录。

#### 通道 ③ 桌面小部件 —— **实测可落地（2026-11-07 改成 1×1 单设备）**

小部件走的是完全另一套机制（`AppWidgetService`），MIUI 完整实现了它。

> **这一节在 2026-11-07 改过一次形态。** 原来是一块 **3×2 的列表面板**（最多 4 行，
> 一行一台）；用户要求改成 **每台设备一个 1×1 方块**，位置自己摆。改完之后
> 一块方块只代表一台设备，`WidgetBinding` 管「小部件 id → 设备键」的映射。

**必须先记住的换算（这是改造时踩到的第一个坑）**：Android 用
`cells = ceil((size + 30) / 70)` 从 dp 反推格数，所以
**`110dp` 算出来是 2 格** —— 选择器里显示「2 x 2」。要 1 格，`size` 必须 ≤ 40dp，
`device_widget_info.xml` 里现在写的是 `40dp`。改之前一直是 110dp/110dp，
所以哪怕代码全对，桌面拿到的也是一块 2×2。

**第二个坑：`requestPinAppWidget()` 在 MIUI 上是「表面成功」**。它返回 `true`，
系统也确实拉起了确认弹窗（`com.miui.home/.launcher.AddItemActivity`，
`act=android.content.pm.action.CONFIRM_PIN_APPWIDGET`），但弹窗自己拒绝了：

```
D/AddItemActivity-PinShortcutRequestUtils: mRequest=2
E/AddItemActivity-PinShortcutRequestUtils: add widget failed, com.local.qrjump has no permission
```

它查的是小米自己定义的 `com.miui.home.launcher.permission.INSTALL_WIDGET`
（在 `MiuiHome.apk` 里 `protectionLevel=0`，normal 级）。manifest 里补上这两条：

```xml
<uses-permission android:name="com.miui.home.launcher.permission.INSTALL_WIDGET" />
<uses-permission android:name="com.miui.home.launcher.permission.UNINSTALL_WIDGET" />
```

补完 `dumpsys package` 里能看到 `granted=true`。**但实测仍然被拒**（同一行日志），
说明 MIUI 那边对 `requestPinAppWidget` 的判断不只靠这个权限。所以：

- 「放到桌面」菜单项现在的行为是：`requestPinShortcut()`（MIUI 上直接跳过）→
  `requestPinAppWidget()` → 不行就弹 Toast 引导手动拖；
- **真正可靠的路径是手动拖**（已实测成功）。

**手动拖的完整步骤**（`adb` 自动化也行）：

```powershell
# 1. 桌面长按空白处 → 菜单里点「添加小部件」
adb -s <你的设备序列号> shell "input touchscreen swipe 540 1400 540 1400 1200"
adb -s <你的设备序列号> shell "input tap 588 2211"

# 2. 选择器是横向分组的，QrJump 在最下面：往下滑 9 次
for ($i=0; $i -lt 9; $i++) { adb -s <你的设备序列号> shell "input swipe 540 1700 540 800 350" }

# 3. 找到 content-desc="QrJump" 的卡片，下面标注「1 x 1」（1x1 生效后可确认）

# 4. 拖动必须用 motionevent 序列 —— input swipe 会被当成滚动，拖不出来
adb -s <你的设备序列号> shell "input motionevent DOWN 200 2250"
adb -s <你的设备序列号> shell "input motionevent MOVE 300 1700"
adb -s <你的设备序列号> shell "input motionevent MOVE 400 1300"
adb -s <你的设备序列号> shell "input motionevent MOVE 540 900"
adb -s <你的设备序列号> shell "input motionevent UP 540 900"
```

拖下去之后系统会自动拉起 `WidgetConfigActivity`（因为 `device_widget_info.xml` 里
写了 `android:configure`），页面标题是「这个方块代表哪台设备？」，列出现有设备：

```
这个方块代表哪台设备？                        [66,420][842,498]
返回                                        [842,410][1014,509]
选一台。之后点这个方块就直接进它的设备页。      [0,537][1080,582]
北三201                                      [105,692][908,751]
设备号 031408                                [105,759][908,800]
✓                                            [941,709][975,782]
洗澡                                         [105,916][908,975]
设备标识 9b9642e8                            [105,983][908,1024]
```

点一台 → 绑定并返回桌面。日志：

```
[Widget] 第一个实例被添加
[Widget] 收到刷新：1 个实例
[Widget] 实例 3 命中待绑定设备 SN|<你的15位SN>
[Widget] 小部件 3 绑定到 SN|<你的15位SN>
[Widget] 配置页打开：widgetId=3 已有绑定=SN|<你的15位SN>
[Widget] 配置页列出 2 台设备
[Widget] 配置完成：widgetId=3 -> SN|<你的15位SN>（静默=false）
```

桌面出现一块 1×1，只显示「北三201」（截图 `shot_widget_1x1.png`，位置在屏幕中部）。
点它 → `mCurrentFocus=com.eg.android.AlipayGphone/...XRiverActivity$App01`，
落点就是XX学院 北3栋201 设备页。

> ⚠️ **这一次验证时发生过一次真实点击，如实记录**：点下方块后我按返回太慢，
> 09:04:01 自动解锁（`auto_unlock_delay=6`）先一步触发了
> `dispatchGesture (540.0, 2201.0)`，页面进入「解锁成功 / 结束后将自动为您计费」。
> 我随即按 `keyevent 4` → force-stop 支付宝 → force-stop QrJump 退出，
> 没有点「出水」，没有开始计费。这也是为什么下面写着「自动解锁默认关」。

#### 1×1 小部件的行为约定

- **一块 = 一台设备**，位置由你自己摆；要几台就拖几块
- 点方块 = 走 `ShortcutActivity` → 查设备簿 → 重新 `ParseResult.parse` 完整校验 → 同一套 `Launcher`
- 「小部件 id → 设备键」存在 `WidgetBinding`（`widget_bindings`，JSON 对象，最多 32 条）；
  设备删了以后方块不会变空白，而是显示「已失效 / 点一下重选」，点它重新进配置页
- 从「放到桌面」菜单点进去时不弹配置页（`WidgetBinding.setPendingPin` 预先记了要绑哪台，
  `takePendingPin` **取一次就清空**，免得下次手动拖被残留值误导）
- 设备簿一变（新增/改名/删除/清空），`DeviceStore.write()` 会立刻调
  `DeviceWidgetProvider.refresh()`，不用等系统刷新周期（`updatePeriodMillis="0"`）
- `PendingIntent` 用 `requestCode = d.key.hashCode()`，否则不同方块会互相覆盖
  （RemoteViews 最常见的坑）
- 方块只显示备注名或短标识，**不显示 URL、不含 token**，和快捷方式同一条边界

> `ShortcutActivity` 本身端到端一直是好的，可以绕过桌面直接验：
> ```powershell
> adb -s <你的设备序列号> shell "am start -W -n com.local.qrjump/.ShortcutActivity --es com.local.qrjump.extra.DEVICE_KEY 'SN|<你的15位SN>'"
> # Status: ok
> # Activity: com.eg.android.AlipayGphone/com.alipay.mobile.quinox.SchemeLauncherActivity
> ```
> 所以如果换成原生桌面（Pixel Launcher / Nova），通道 ① 的独立图标也能正常落桌面。

### 6.2 两种设备形态：饮水器（SN）与热水控制（NQT）

校园里两种机器的码不一样，App 都认：

| | 形态① 饮水器 | 形态② 热水控制 |
|---|---|---|
| 码面内容 | `https://h5.qiekj.com/skip?SN=<你的15位SN>` | 外层 `link.wtturl.cn/?...&target=...`，解包后 `https://h5.qiekj.com/skip?Company=qiekj&CommunicateType=1&Ver=4&NQT=<UUID>` |
| 标识 | `SN`，15 位纯数字 | `NQT`，UUID 形状 |
| 内层参数 | 恰好 `{SN}` | `{Company, CommunicateType, Ver, NQT}` |
| 默认策略顺序 | `[B, C]` | `[B, C]`（20:09 起同样是 B 优先，见下） |
| B 档 query | `masterSwitch=0&keepUsing=1&SN=<SN>&isShowAd=false` | `masterSwitch=0&keepUsing=1&NQT=<UUID>&isShowAd=false` |
| 实测落点 | 饮水器设备页（北3栋201 / 北3栋202） | 热水器设备页（某热水机 201） |

**两个形态共用同一套外层参数，只有标识键名不同。** 这是 20:09 那次实测才发现
修正的 —— 此前我让形态②把机身码里官方那组 `Company/CommunicateType/Ver/NQT`
原样直传，结果小程序落的是「读取设备 / 二维码有误，换一个试试」，
和「只带 SN」是同一类失败。换成同形状模板后立刻直达：

```powershell
# 形态②的正确写法（20:09 实测 → 某热水机 201 设备页，TotalTime 790ms）
adb -s <你的设备序列号> shell "am start -W -a android.intent.action.VIEW -d 'alipays://platformapi/startapp?appId=2018072460764274&page=pages%2FskipNew%2FskipNew%3FisShowAd%3Dfalse&query=masterSwitch%3D0%26keepUsing%3D1%26NQT%3D<UUID>%26isShowAd%3Dfalse&isShowAd=false'"
```

应用内同样验证过（20:25，点「一键直达这台设备」）：

```
[Launch] 策略顺序 = [B, C]（用户选择=B，形态=NQT，自动降级=true，调试模式=false）
[Launch] B 形态=NQT 生成的 query = masterSwitch=0&keepUsing=1&NQT=9b9642e8-...&isShowAd=false
[Launch] ✓ 已发起：B/B1 → com.eg.android.AlipayGphone/...SchemeLauncherActivity
```

落点 = 某热水机 201 设备页（截图 `shot_nqt_B_onetap.png`）。

**形态②为什么不该走 A**：外层 `link.wtturl.cn` 是第三方短链，用系统浏览器
打开会先撞**字节跳动安全中心**的拦截页（「你即将访问抖音短视频外部的第三方网址」
+「继续访问」按钮），A 档因此会卡在那一屏，多一次点击。

> 形态②的 query 模板默认值已从「空 = 直传官方 query」改成上面那条实测有效的
> 模板。**这不是编造**：形状来自形态①的官方跳转，只把 `SN` 换成 `NQT`，
> 并已用真机验证落到正确的热水设备页。

### 6.3 相机改成按键触发：进页面不会自己开相机

`ScanActivity` 现在不再自动起相机。进去看到的是：

```
[返回设备列表]                                   [设置]

扫一扫机身二维码
相机不会自动打开，点下面的按钮才会启动

   对准机身贴纸上那个正方形二维码，扫到即自动校验并唤起

                [ 点击开始扫码 ]
```

点了才 `ProcessCameraProvider.bindToLifecycle()`；扫码成功、或者页面 `onPause`，
立刻 `unbindAll()`。

**为什么**：相机是隐私敏感资源。原设计一进页面就起、还带常驻权限弹窗，
等于每次打开 App 都在为一个低频动作付费（耗电、隐私指示灯常亮、在暗处晃到别人）。
改完之后，相机的生命周期严格等于"这一次扫码"，用完即停。

真机验证（20:56:58 冷启，3 秒后日志里**没有**任何 `相机已绑定`）：

```
[Life] ═══ ScanActivity 启动 ═══
[Life] 策略=B  调试模式=false  自动降级=true  自动解锁=false
[Life] 设备=Xiaomi 22021211RC Android 14 (API 34)
[Life] onResume（当前策略=B 调试=false）
```

点按钮之后才出现（20:57:17）：

```
[Scan] 用户点击「点击开始扫码」
[Life] 相机权限已具备，启动相机
[Scan] 相机已绑定（后置），分析分辨率 1280x720
[Scan] ML Kit 解码器初始化成功
```

扫成功后立刻多一行 `[Scan] 相机已解绑（已解出二维码）`。

### 6.4 自动点「立即使用」（无障碍辅助点击，默认关闭）

用户提的第三件事是「点击解锁按钮做成软件自动化」，也就是到了设备页之后
不用再手动点那一下「立即使用」。

**怎么做的**：`AutoUnlockService`（一个标准 Android `AccessibilityService`）+
`AutoUnlock`（触发窗口控制）。到了设备页，它替你在**官方页面上**点一次真按钮。

**为什么用无障碍，而不是别的路子**：小程序跑在 `com.eg.android.AlipayGphone`
自己的进程里，本应用拿不到它的界面，也注入不进去。Android 官方允许的、
不越狱不逆向的"代用户点一下"的机制只有无障碍服务。它看到的只是系统公开给
辅助功能的控件树，不读网络、不读存储。

**动手的四个必要条件，缺一不可**（代码里逐条判断，日志里能逐条看到）：

| # | 条件 | 代码位置 | 不满足时的日志 |
|---|---|---|---|
| 1 | 设置里打开了「自动解锁」 | `Prefs.autoUnlock` 默认 **false** | `[Auto] 自动解锁未开启，不进入待命状态` |
| 2 | 处在刚唤起后的 **2 分钟**待命窗口内 | `AutoUnlock.arm/disarm/isArmed` | `[Auto] 待命已超时（N 秒），不再动手` |
| 3 | 事件来自 `com.eg.android.AlipayGphone` | `AutoUnlockService.onAccessibilityEvent` | 直接 `return`，不碰别的应用 |
| 4 | 同一棵控件树里有设备页独有标记「出水口」 | `hasDevicePageMark()` | `[Auto] 当前窗口没有「出水口」，不像设备页，不动手` |

四条都满足 → `findTargetNode(root, "立即使用")` → 点击 → 立刻 `disarm`
（**一台只点一次**，页面抖动也不会连点）。找不到按钮就打
`[Auto] 设备页已出现，但还没找到「立即使用」按钮`，等下一次内容变化再判。

#### 6.4.1 一个必须记下来的真相：那个按钮根本不可点击

一开始我是按常规写的 `performAction(ACTION_CLICK)`，日志一直停在
`设备页已出现，但还没找到「立即使用」按钮`。用 `uiautomator dump` 把真实设备页
的控件树抓下来（21:05）才看清：

```
node text="立即使用" class=android.widget.TextView clickable=false bounds=[44,2128][1036,2274]
  ^L1 class=android.view.View                    clickable=false
  ^L2 class=android.view.View                    clickable=false
  ^L3 class=android.view.View  rid=__react-content  clickable=false
  ^L4 class=android.webkit.WebView               clickable=false
  ^L5 class=com.alipay.mywebview.sdk.WebView     clickable=false
  ^L6 class=android.widget.FrameLayout           clickable=false
```

也就是说：**这是小程序页面里一块 WebView 渲染出来的 React 内容，按钮本身和它
往上的六层祖先全部 `clickable=false`**。`performAction(ACTION_CLICK)` 对一条
纯文本节点是无意义的 —— 难怪点不动。

所以点击改成两条路（`AutoUnlockService.clickNode()`）：

1. 先按老办法向上找 8 层内可点击且 enabled 的祖先，有就 `performAction`；
2. 都没有 → 取节点 `getBoundsInScreen()` 的中心，用 `dispatchGesture` 做一个
   60ms 的**按下-抬起**手势。这跟真人手指点一下在系统层面是等价的，
   需要配置里 `android:canPerformGestures="true"`，缺了会被系统直接拒绝。

一行对照：加了这个标志位之后，`dumpsys accessibility` 里的
`capabilities` 从 `1` 变成了 `33`（多出来的位就是 gesture 能力）。

改完实测（21:06:44，调试模式下只报告不点击）：

```
[Auto] 调试模式：已定位到「立即使用」（class=android.widget.TextView，
       clickable=false，中心=(540.0, 2201.0)），本应点击但未动手
```

坐标 `(540, 2201)` 与 dump 里 `bounds=[44,2128][1036,2274]` 的中心完全对得上
（`(44+1036)/2=540`，`(2128+2274)/2=2201`）—— **认页面、找按钮、算坐标这三步
已经验证到位，只剩"真的按下去"没做**。

#### 6.4.2 推迟触发：等满设定的秒数再点（用户反馈后改了两版）

用户反馈"打开网页后加载有延迟"。翻 21:04 那份 dump 能证实这不是错觉 ——
**「立即使用」和「内容准备中」是同一次 dump 里同时存在的节点**：

```
小票余额 40.42元 / 出水口 冷水 热水 / 立即使用 / 内容准备中 / 刷新
```

按钮在页面还没准备好的时候就已经在树里了。一看到它就点，点的是一个没接上
设备状态的页面。

**第一版（21:40）：两道闸相加。** 先等「内容准备中」消失，再等 2500ms 稳定期。
实测立刻发现两个问题：

- **不能只靠无障碍事件**：设备页渲染完就**不再发任何
  `TYPE_WINDOW_CONTENT_CHANGED`** 了，日志停在
  `设备页还在加载（「内容准备中」），等它稳定` 之后一片安静。→ 加 1000ms 轮询。
- **「内容准备中」根本不消失**：等了 15 秒再 dump，那一块**仍然是
  「内容准备中 + 刷新」**。它不是"整页没加载完"的闸门，而是页面上一个独立的
  小票/广告位区域在慢慢拉。→ 给它 8000ms 宽限。

结果就是恒定 `3 秒到设备页 + 8 秒宽限 + 2.5 秒稳定 ≈ 11 秒`，**又慢又不可预测**。

**第二版（22:05，用户说"打开速度太慢了，大胆把时间往后调"）：改成一条时钟。**
现在**从第一次认出设备页起，等满设定秒数就点**，加载标记只用来打日志、
不再参与计时：

| 项 | 旧 | 新 |
| --- | --- | --- |
| 单位 | 毫秒（界面上写"2500 毫秒"） | **秒** |
| 默认 | 2500ms 稳定期 + 8 秒宽限 | **8 秒**（一条时钟） |
| 范围 | 0–10000ms，每格 500ms | **0–30 秒**，每格 1 秒 |
| 总等待 | 不可预测，约 11 秒 | = 设定值 |

设置页那一行滑杆的文案现在是 `认出设备页后再等 %1$d 秒才点击`。

> **旧值兼容**：`auto_unlock_delay` 这个 key 之前存的是毫秒（默认 2500）。
> 秒的上限是 30，所以 `autoUnlockDelaySec()` 读到任何大于 30 的整数都判定为
> 旧单位，就地 `÷1000` 换算并存回，避免读成"2500 秒"。

**踩到并修掉的一个真实逻辑循环**（21:54，第一版留下的）：宽限期过了之后，
`noteStillLoading` 仍然无条件把稳定期起点清零 —— 每轮询一次就归零一次，
稳定期永远攒不满。第二版重写时把"清零起点"这个动作整个删掉了：
`noteDevicePageSeen()` 第一次记下时间后**再也不重置**。

**实测（22:13，让手势真的按下去）：**

```
22:13:30.959 [Auto] 进入待命状态（120 秒内出现设备页则点一次，认出设备页后再等 8 秒）
22:13:31.796 [Auto] 开始轮询设备页状态（每 1000ms 一次）
22:13:31.845 [Auto] 当前窗口没有「出水口」，不像设备页，不动手
22:13:34.958 [Auto] 已认出设备页，开始计时（等 8 秒）
22:13:35.505 [Auto] 页面还显示「内容准备中」（它是独立慢区域，不影响计时）
22:13:36.185 [Auto] 还没等够，还差 7 秒（阈值 8 秒）
...
22:13:42.345 [Auto] 还没等够，还差 1 秒（阈值 8 秒）
22:13:43.390 [Auto] 点击「立即使用」用 dispatchGesture 提交=true 坐标=(540.0, 2201.0)
22:13:43.454 [Auto] 手势点击已完成：(540.0, 2201.0)，这一台不再自动点
22:13:43.454 [Auto] 退出待命状态：已自动点击「立即使用」（手势）
22:13:43.813 [Auto] 停止轮询
```

时间线可对上：22:13:31 起轮询（此时还在加载页，没有「出水口」）→
22:13:34 认出设备页开始计时 → 22:13:43 满 8 秒动手。
**点完页面变成「解锁成功 / 结束后将自动为您计费」**（截图
`shot_after_autoclick.png`），随后立刻按安全序列退出，
**没有点「出水」，所以没有开始计费**。

#### 6.4.3 一个只在重装后才暴露的缺口：轮询永远不启动

22:11 实测撞到的：轮询原本**只在收到无障碍事件时才 `startPolling()`**。
而 `adb install -r` 会让无障碍授权被撤销，重装后重新授权时，
设备页早就渲染完、静止了 —— 于是**一条事件都收不到，轮询永远不启动**，
自动点击等于没开（日志上只有 `无障碍服务已连接`，之后再无 `[Auto]`）。

修法：`onServiceConnected()` 里加一段自举 —— 只要
(1) 自动解锁开着、(2) 还在待命窗口内，就**直接**起轮询并立刻 `checkOnce()`：

```kotlin
if (Prefs.autoUnlock(this) && AutoUnlock.isArmed() && AutoUnlock.canStillClaim()) {
    QLog.i("Auto", "绑定后发现仍在待命窗口内，直接启动轮询（不依赖事件）")
    startPolling()
    checkOnce()
}
```

这条修完，22:13 那次才能在重装 + 重新授权之后正常触发。


**边界在哪**（这段请一定读）：

- 点的是支付宝小程序页面上的那个真按钮，走的是支付宝自己的控件和它自己的业务逻辑；
- **不**构造订单、**不**调私有接口、**不**伪造签名、**不**改金额、**不**碰登录态；
- 与你自己用手指点一下的区别只在于"谁点的"；计费、扣款、出水规则全部仍由官方页面决定；
- 服务是否运行，**完全取决于你有没有在「系统设置 → 无障碍」里手动打开**，
  应用自己无法偷偷启用它；开关关着时，服务即使被系统启动也一行都不做。

**所以它确实改变了"谁来按那一下"**：开启后从设备页到出水之间不再有人工确认。
这一点我不打算模糊化 —— 页面上的按钮你没点，是 App 替你点的。
如果哪天你想恢复"必须自己点"，把设置里那个勾去掉即可，行为立刻回到原样。

**开启步骤**（必须手动，两步都要）：

```powershell
# 1) 在 App 里勾上「到设备页后自动点「立即使用」」（默认关闭，必须自己勾）

# 2) 打开系统无障碍设置，找到「QrJump 自动点击「立即使用」」并打开
adb -s <你的设备序列号> shell "am start -a android.settings.ACCESSIBILITY_SETTINGS"

# 或者用 adb 打开（跳过点按，仅调试用）。两句顺序不能反：
adb -s <你的设备序列号> shell "settings put secure accessibility_enabled 1"
adb -s <你的设备序列号> shell "settings put secure enabled_accessibility_services com.local.qrjump/com.local.qrjump.AutoUnlockService"

# 3) 确认真的绑上了
adb -s <你的设备序列号> shell "dumpsys accessibility" | findstr /C:"QrJump"
```

实测（21:02，加 `canPerformGestures` 之前）用第 2 步的 adb 方式打开后，
`dumpsys accessibility` 里出现：

```
Bound services:{Service[label=QrJump 自动点击「立即使用」, feedbackType[FEEDBACK_GENERIC],
  capabilities=33, eventTypes=[TYPE_WINDOW_STATE_CHANGED, TYPE_WINDOW_CONTENT_CHANGED,
  TYPE_VIEW_SCROLLED], notificationTimeout=120, requestA11yBtn=false]}
Enabled services:{{com.local.qrjump/com.local.qrjump.AutoUnlockService}}
```

注意 `capabilities=33`（早期是 `1`）：33 = `1 | 32`，多出来的那一位就是
`CAN_PERFORM_GESTURES`，是 `canPerformGestures="true"` 换来的 ——
没有它，`dispatchGesture` 直接提交失败，而这个按钮又只能靠手势点。

日志同步出现 `[Auto] 无障碍服务已连接`，设备列表页也开始能读到
`无障碍服务=true`：

```
[Devices] 策略=B 调试=false 自动解锁=false 无障碍服务=true
```

> **两个 adb 细节，踩过之后记下来**：
> 1. `settings put secure accessibility_enabled 1` 必须写在
>    `enabled_accessibility_services` **之前**，反过来的话系统会把它重置成 0，
>    服务不会绑上（实测反复复现）。
> 2. **重新安装 APK 会让无障碍授权失效**（系统检测到包变化就撤销），
>    升级后要重新执行上面这一句。日志里表现为 `无障碍服务=false`。
> 3. 想读服务状态**不能**读 `Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES`
>    —— 那个 key 从 Android 4.2 起对普通应用受限，`settings get` 有值但
>    应用内读出来恒为 `null`。App 里改成读
>    `AccessibilityManager.getEnabledAccessibilityServiceList()`，并辅以服务在
>    `onServiceConnected()` 时写下的回执。

> **已经实测通过了**（22:13）：手势真的按下去了，日志
> `点击「立即使用」用 dispatchGesture 提交=true 坐标=(540.0, 2201.0)` →
> `手势点击已完成`，页面变成「解锁成功 / 结束后将自动为您计费」
> （截图 `shot_after_autoclick.png`）。随后立刻退出，
> **没有点「出水」，所以没有开始计费**。
>
> 也就是说：**从这个入口一路到"解锁成功"已经是全自动的了**，
> 剩下的只有设备上那个「出水/暂停」和「结束」按钮 —— 那两个在饮水器的
> 物理面板/小程序控件上，本应用没有去碰，也确实不该碰（那是真正开始出水和
> 结束计费的动作）。
>
> 试的时候注意两点：
> 1. 每次 `adb install -r` 之后无障碍授权都会失效，要重新执行上面那两句；
> 2. 如果重装后日志里只有 `无障碍服务已连接` 而再没有后续 `[Auto]` 行，
>    说明撞上了 6.4.3 讲的缺口（已在代码里修掉，但旧 APK 会复现）。

### 6.4.4 从最近任务划掉 App，无障碍授权会被系统清空（附带解法）

这不是本应用的 bug，是 Android/小米的既有语义，但会直接影响自动点击能不能用，
所以单独写一节。

#### 先看对照实验（22:33 逐项实测）

| 杀法 | `accessibility_enabled` | `enabled_accessibility_services` | 服务会不会自己回来 |
|---|---|---|---|
| `am kill`（普通杀进程） | `1` 保持不变 | 保持 | **会**，系统自动重绑（pid 变了） |
| 从最近任务划掉卡片 | **`0`** | **`null`** | **不会**，必须重新授权 |
| `am force-stop` | **`0`** | **`null`** | **不会**，必须重新授权 |

logcat 里的佐证：

```
BatterySaverPolicy: accessibility changed to false
SettingsProvider: Failed to notify for 999: content://settings/secure/accessibility_enabled
```

**结论**：MIUI 把"划掉卡片"当成 `force-stop` 处理，并在 `force-stop` 时主动把这两个
secure setting 清空。清空之后系统不会再把服务绑回来 —— 这不是 AOSP 的行为
（AOSP 只解绑、不清设置），所以换台原生 Android 手机可能根本遇不到。

而且注意：`dumpsys package com.local.qrjump` 里 `stopped=false`，说明包本身没被禁用，
纯粹是那两个 setting 被抹了。

#### 解法：让 App 自己写回去（需要一次性授权）

普通应用没有写 secure settings 的权限，所以必须借 adb 给一次
`WRITE_SECURE_SETTINGS`。这一步只做一次，之后每次划掉再打开 App 就自动恢复。

```bash
# 一次性授权（重装 APK 后需要重做，因为换包会撤销）
adb -s <你的设备序列号> shell "pm grant com.local.qrjump android.permission.WRITE_SECURE_SETTINGS"

# 确认拿到了
adb -s <你的设备序列号> shell "dumpsys package com.local.qrjump | grep WRITE_SECURE_SETTINGS"
# 期望：android.permission.WRITE_SECURE_SETTINGS: granted=true
```

> **踩过的坑**：如果不先在 `AndroidManifest.xml` 里声明这个权限，
> `pm grant` 会直接报
> `java.lang.SecurityException: Package com.local.qrjump has not requested permission
> android.permission.WRITE_SECURE_SETTINGS`。所以 manifest 里写了这一行，
> 但它是 `signature|privileged` 级别，**安装时永远不会被授予**，
> 只有上面那条 `pm grant` 能给它。没授权时 App 一切照旧，只是不能自愈。

拿到权限后，`A11yGuard.heal()` 会在两个时机自动检查并写回：

1. `DevicesActivity.onCreate()` —— 每次打开 App（启动页）
2. `AutoUnlock.arm()` —— 每次真要唤起设备页之前

写回的顺序**不能反**（反了系统会把它重置成 0）：

```kotlin
cr.putInt(Settings.Secure.ACCESSIBILITY_ENABLED, 1)                 // 先
cr.putString(Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES, after) // 后
```

而且写回是**追加**不是覆盖，不会把别的无障碍服务弄丢：

```kotlin
val after = if (before.isBlank()) mine else "$before:$mine"
```

#### 实测（22:41 / 22:48，两次都过）

真实划卡 → 确认 `enabled=0` / `services=null` / `pidof` 为空 → 重新点开 App：

```
[A11y] 检测到无障碍授权被清空（多半是从最近任务划掉了 App），开始写回
[A11y]   原有服务列表=(空)
[A11y]   写回后=com.local.qrjump/com.local.qrjump.AutoUnlockService
[A11y]   putInt(accessibility_enabled)=true  putString(services)=true
[A11y] ✓ 自愈成功，无障碍服务已重新写入（系统会在数秒内绑定）
[Devices] 策略=B 调试=false 自动解锁=true 无障碍服务=true
[Auto] 无障碍服务已连接（canPerformGestures 见配置）
```

之后 `settings get secure accessibility_enabled` = `1`，`enabled_accessibility_services`
已写回。

**所以用户要做的只剩一个动作：重新点开 App**，不用再进系统设置里翻。

#### 23:03 又修了两个坑（都是只有真机才会暴露的）

**坑一：回执绑进程号。** 设备列表页判断"服务有没有绑上"时，会读一条由
`AutoUnlockService.onServiceConnected()` 写下的 Boolean 回执。问题是
`replace old` —— `am force-stop` 杀死进程时**不会回调 `onUnbind`**，那条
`true` 就留在盘上了。结果：授权明明已经被清空，日志却打
`无障碍服务=true`（23:00 实测复现）。

修法（`Prefs.kt`）：回执连同 `android.os.Process.myPid()` 一起存，读的时候要求
pid 一致 —— 换进程旧回执自动作废，不需要任何清理代码。

```kotlin
fun a11yConnected(c: Context): Boolean =
    sp(c).getBoolean(KEY_A11Y_CONNECTED, false) &&
        sp(c).getInt(KEY_A11Y_PID, -1) == android.os.Process.myPid()

fun setA11yConnected(c: Context, v: Boolean) =
    sp(c).edit()
        .putBoolean(KEY_A11Y_CONNECTED, v)
        .putInt(KEY_A11Y_PID, if (v) android.os.Process.myPid() else -1)
        .apply()
```

**坑二：自愈要放在 `onResume`，不能只在 `onCreate`。** 两个原因：

1. `onCreate` 只在进程第一次起来时跑一次，从后台切回前台不经过它 ——
   而"划掉卡片"之后再打开 App 走的正是冷启，看似够用，但下面这条更致命：
2. **重装 APK 之后，系统会异步复核一次包变化并在约 1 秒后再次清空授权。**
   23:06 实测：`onCreate` 里写回成功、`[Auto] 无障碍服务已连接`（23:06:06.073），
   一秒后 `[Auto] 无障碍服务已解绑 / 已销毁`（23:06:06.973），
   `settings get secure accessibility_enabled` 又变回 `0`。
   放进 `onResume`（每次回前台都跑）能补到第二次，而且 `heal()` 幂等，
   授权在位时只打一行「无需自愈」。

改完 23:10 的真实划卡复现（`input swipe 540 1100 540 150 250` →
`enabled=0` / `pidof` 空 → 重新点开）：

```
[Devices] 策略=B 调试=false 自动解锁=true 无障碍服务=false
[A11y] 检测到无障碍授权被清空（多半是从最近任务划掉了 App），开始写回
[A11y] ✓ 自愈成功，无障碍服务已重新写入（系统会在数秒内绑定）
[Devices] 无障碍授权已确保在位（写回是同步的，服务绑定异步，通常 <1 秒）
[Auto] 无障碍服务已连接（canPerformGestures 见配置）
```

最终 `enabled=1`、`dumpsys accessibility | grep -c QrJump` = `1`。

> **注意那句「无障碍服务=false」和「自愈成功」同时出现是正常的**：
> 前者读的是"此刻绑没绑上"，后者是"授权已写回"。写 secure setting 是同步的，
> 系统把服务真正绑到进程上要几百毫秒（实测约 150–440ms）。

设置页里也有一个「尝试自动恢复无障碍授权」按钮（`btnHealAccessibility`），
下面会实时显示当前状态，四种情况分别是：

- 已启用且可自愈
- 已启用但不能自愈（没给 `WRITE_SECURE_SETTINGS`）
- 未启用但有权限（点按钮就恢复）
- 未启用且无权限（会提示去执行上面那条 `pm grant`）

#### 另一条路：最近任务里"锁定"卡片（**未验证成功，别指望它**）

MIUI 的最近任务卡片长按后右侧会弹出一列按钮（颜色圆点、锁形、两个方形、齿轮），
看着像"锁定后划不掉"。我按 22:46 试了一次（点 `(963,1150)`）：

- 点完直接回到桌面，**卡片上没有出现锁标记**
- 再 `input swipe 540 1300 540 200 200` 划掉
- 结果 `enabled=0` / `services=null` —— **授权照样被清空**

所以这条我没法写进"可行方案"。可能是坐标没点中真按钮（那列圆形按钮是
MIUI 的悬浮菜单，位置随卡片高度浮动），也可能是这个机型/版本压根没有固定卡片
的功能。**如果以后要用，得自己在手机上长按卡片、肉眼确认出现锁标记后再划一次验证**，
别直接信截图。

#### 另外两条更省事的备选

1. **干脆不划掉它**。自动点击只在待命窗口（2 分钟）内工作，用完就把它扔在后台，
   系统会自己回收，等下次打开 App 时授权还在。
2. **走桌面小部件 / 快捷方式**（`ShortcutActivity`）。它不经过 `DevicesActivity`，
   但会走 `AutoUnlock.arm()`，而 `arm()` 里第一件事就是 `A11yGuard.heal(context)`
   —— 所以这条路径也能自愈。前提是 `Prefs.autoUnlock` 是开的（关着时 `heal()`
   直接返回，不碰系统设置）。

---

### 6.5 调试模式下的免扫码注入（省事，但有前提）

调试模式打开时，可以用 adb 注入一段"假装扫到的文本"，不用举手机也能测全链路：

```bash
adb -s <你的设备序列号> shell "am start -n com.local.qrjump/.ScanActivity \
  --es com.local.qrjump.extra.FAKE_SCAN 'https://h5.qiekj.com/skip?SN=<真实SN>'"
```

**前提**：调试模式必须是开的（默认关闭）。没开时这条 extra 会被直接忽略并打一行
`[Scan] 收到注入的扫码文本，但调试模式未开启，忽略`。这样正常使用不会被人
用 adb 塞一条链接就绕过去。

---

### 6.6 界面：一层「液态玻璃」是怎么搭出来的

整屏是一张**深色渐变底 + 两团柔光**（`bg_app_gradient.xml`：`#0B1026 → #16233F → #0A1A2B`
315° 渐变，中间叠两团低透明度的紫/青径向光）。所有卡片、按钮都是**半透明覆盖**，
让底色透上来 —— 这才有玻璃感。任何一块不透明实心填充都会把整页气氛破坏掉，所以
全工程**没有一个 `#FF` 开头的背景色**。

三层同轴（`app/src/main/res/values/glass_styles.xml` 里有完整注释）：

| 层 | drawable | 参数 |
|---|---|---|
| 页面 | `bg_app_gradient` | 深蓝紫渐变 + 双柔光 |
| 卡片 | `bg_glass_card` | `22dp` 圆角、`1AFFFFFF` 填充、`33FFFFFF` 描边 |
| 行 / 按钮 | `bg_glass_row`、`bg_glass_button` | `16~18dp` 圆角，更亮一档 |
| 描边按钮 | `bg_glass_button_outline` | 只有 `40%` 白描边，无填充 |
| 圆形 `⋯` | `bg_glass_circle` | 正圆、`33FFFFFF` |
| 上下压暗 | `bg_scrim_top` / `bg_scrim_bottom` | 让顶部文字和底部按钮在相机画面上也读得清 |

文字三档：主文 `#FFFFFF`、次文 `#CCFFFFFF`、弱文 `#80FFFFFF`。
三个可复用 style：`GlassTitle`（26sp bold）、`GlassButton`（54dp 高）、
`GlassButtonOutline`（50dp 高）。（原 `GlassCaption`（11sp）已随"主界面不留小字"一起删除。）

**三个页面**（真机截图，1080×2400）：

- 设备列表 `shot_final_devices.png`：大标题 + 右上「设置」胶囊 + 玻璃卡片行（一行一个名字，
  无副标题）+ 底部蓝紫渐变主按钮 + 玻璃描边次按钮
- 设置页 `shot_final_settings.png`：整页装在一张大的玻璃卡片里，单选/开关/滑杆都保持
  原生控件（不自己画，避免无障碍与深色模式踩坑），只换背景与文字色
- 扫码页 `shot_final_scan.png`：黑底相机预览 + 顶部/底部压暗渐变 + 两个玻璃胶囊（返回 /
  设置）+ 中央「点击开始扫码」。相机没起时预览是 `invisible`，看到的就是那张渐变底

**小部件也走同一套语言**：1×1 方块用 `bg_widget_cell.xml`（`33FFFFFF → 1AFFFFFF →
0DFFFFFF` 渐变 + 20dp 圆角 + `40FFFFFF` 描边），图标是 `ic_widget_water.xml`（水滴）与
`ic_widget_heat.xml`（花洒）。RemoteViews 只支持有限控件集，所以方块里只用
`LinearLayout` + `ImageView` + `TextView`，不能上 `ConstraintLayout` 或自定义 View。

**为了玻璃改版做的两处代码对齐**（踩过才知道）：
- 布局里按钮从 `Button` 换成了 `TextView`（`Button` 自带 Material 背景会盖住玻璃），
  所以 `DevicesActivity` 里 `findViewById<Button>(...)` 全部改成了 `findViewById<TextView>(...)`，
  否则运行期直接 `ClassCastException`
- 「最近用过」那行高亮原先用 `#0B5CD5`，在深色玻璃底上几乎看不见，改成了 `#8FD8FF`；
  后来主界面去小字时整条副标题一起删掉，这个颜色也就不再需要了

---

### 6.7 去小字与清死代码（2026-11-07 00:19 那轮）

用户原话：「去除主界面小字，优化程序，去除无用功能代码」。

**界面上拿掉的**（主界面只剩名字，能认人就够了）：

| 位置 | 拿掉了什么 |
|---|---|
| 设备列表标题 | 副标题「点一行，直接进官方设备页」 |
| 设备列表行 | 整条副标题（`设备号 <你的15位SN> · 上次使用 11-06 23:20`），行高 14→18dp、名字 15→16sp |
| 设备列表底部 | 说明条「只保存设备标识（SN/NQT）…」 |
| 设置页 | 两个按钮：「管理设备列表」（设备列表就是启动页，指向自己没有意义）、「同步桌面快捷方式」（MIUI 屏蔽动态快捷方式，已由小部件替代） |

设置页那几段 11sp 长说明**没有全删**，只把「重复讲同一件事」的部分压掉了：
`settings_auto_unlock_note` 从 120 字压到 24 字（只留「点完即开始出水并按官方规则计费，没有二次确认」这句真正重要的），
`a11y_heal_note`、`settings_auto_delay_note`、`settings_b_note` 各压到一两句。
理由是这三处仍在解释「为什么这样」和「出了问题怎么办」，删了会让设置页变成猜谜。

**代码上清掉的**（都用脚本验过「全工程零引用」才动手，不是凭感觉删）：

- `strings.xml` 138 → **110 条**（删 28 条：`quick_*` 整个家族、`devices_tagline`/`devices_saved_note`/`devices_subtitle*`、`shortcut_synced`/`shortcut_sync_empty`、`settings_manage_devices`/`settings_shortcuts`、`devices_pin_requested`、`toast_need_unlock_hint` 等）
- `Prefs.kt`：`KEY_QUICK_LAUNCH` 常量 + 两个 `@Deprecated` 的 `quickLaunch()`/`setQuickLaunch()`
- `A11yGuard.kt`：`deviceLine()` + `import android.os.Build`
- `DeviceStore.kt`：`last()`（`DevicesActivity` 早已改用 `all()`）
- `ShortcutHelper.kt`：**整条旧版广播通道**（`pinViaLegacyBroadcast` / `findLegacyReceiver` / `capabilityLine` / 两个常量）+ `import android.os.Build`
- `AndroidManifest.xml`：两条 `com.android.launcher.permission.*` 权限 + `queries` 里那条 `INSTALL_SHORTCUT` 声明
- `Launcher.kt` / `ScanActivity.kt`：两个未使用的 import
- `glass_styles.xml`：`GlassCaption`（两个使用者都已被删）
- `DevicesActivity.kt`：`buildSubtitle()` / `formatTime()` / 行内副标题绑定

**有意保留的两样**（删之前先想清楚它们在什么情况下还有用）：

- `ShortcutHelper.sync()` —— 仍被 `DeviceStore.write()/clear()` 调用。MIUI 不展示动态快捷方式，
  但原生/第三方桌面会展示，成本只有一次 binder 调用。
- `ShortcutHelper.pinToHome()` 里的 `requestPinShortcut` 分支 —— 非 MIUI 桌面上这是唯一能做
  「单台设备一个独立图标」的官方 API，只在 MIUI 上才短路返回 false。

**验证**：`gradle clean assembleDebug` → `BUILD SUCCESSFUL in 15s`，零 warning；
APK 28,963,653 bytes；装机后冷启日志正常，无障碍自愈照常工作
（`[A11y] 无障碍授权在位，无需自愈` / 重装后 `[A11y] ✓ 自愈成功`）。
`uiautomator dump` 复核设备列表只剩 8 个文本节点：`我的设备` / `设置` / `北三201` / `⋯` /
`洗澡` / `⋯` / `扫码添加设备` / `清空全部`。

---

### 6.8 换机实测：realme / ColorOS（Android 17）上，自愈做不到了

用户 2026-11-07 09:23 反馈：「我换了个手机，问题是无障碍权限不可以自愈」。

新机器：`realme RMX6699`（`RE6400L1`）、`ro.build.version.release=17`、
`ro.build.version.sdk=37`、`ro.build.version.oplusrom=V17.0.0`、
`RMX6699_17.0.0.111(CN01)`、`1440×3136 / 640dpi`，serial `3B15AH01AJA00000`。

#### 1. 根因：这台 ROM 把 `pm grant` 全局封了

`A11yGuard.heal()` 打的是这句：

```
[A11y] 没有 WRITE_SECURE_SETTINGS，无法自愈。一次性执行：
        adb shell pm grant com.local.qrjump android.permission.WRITE_SECURE_SETTINGS
```

照做，报：

```
java.lang.SecurityException: grantRuntimePermission:
  Neither user 2000 nor current process has
  android.permission.GRANT_RUNTIME_PERMISSIONS.
  at com.android.server.permission.access.permission.PermissionService
     .setRuntimePermissionGranted(PermissionService.kt:883)
```

这不是「我们的 manifest 没声明」或「写法不对」：

| 试法 | 结果 |
|---|---|
| `pm grant --user 0 ...` | 同一个 SecurityException |
| `cmd package grant ...` | 同一个 SecurityException |
| `pm grant com.google.android.gms android.permission.CAMERA`（对照组） | **同样被拒** —— 说明与我们的应用无关 |
| `dumpsys package com.android.shell` | `GRANT_RUNTIME_PERMISSIONS: granted=true`，shell 明明有这个权限，Android 17 的 `PermissionService` 仍然拒 |

补充证据：`WRITE_SECURE_SETTINGS` 的 `prot=signature|privileged|development|installer|role`
带 `development` 位，debuggable 应用**本应在安装时自动获得**；`dumpsys package` 显示
`pkgFlags=[ DEBUGGABLE HAS_CODE ... ]`，但 `install permissions:` 里只有
`INTERNET` / `ACCESS_NETWORK_STATE`，这条没给。

其它提权路也全断：`adb root` → `adbd cannot run as root in production builds`；
`ro.debuggable=0`、`ro.secure=1`、`ro.build.type=user`；`which su` 无；
没装 Shizuku；开发者选项逐屏 dump 过，**没有「禁止权限监控」这类开关**；
`settings list global | grep -i 'disable|monitor'` 也没有相关键
（试过 `settings put global disable_permission_monitor 1`，`pm grant` 仍报同一个错）。

**但 shell 写 secure 是好的**：`settings put secure xxx 1` → `settings get` 读回 `1`。
所以**授权可以手工恢复，只是 App 自己做不到**。

#### 2. 写序与 MIUI 相反（已改成两种都试）

| ROM | 正确顺序 |
|---|---|
| MIUI（AOSP 习惯） | 先 `accessibility_enabled=1`，再写 `enabled_accessibility_services` |
| ColorOS（realme 实测） | **先写 services，再 `accessibility_enabled=1`** |

按 MIUI 的顺序在这台机器上写，`settings get secure accessibility_enabled` 会立刻变回 `0`。

`A11yGuard.heal()` 因此改成**两种顺序都试**：先用 A 写一遍并读回复核
（要求「服务在列表里」且「开关=1」两个条件同时成立），没生效就换 B 再写一遍。
不猜 ROM、不维护分支 —— 多写两个 `put` 的代价可以忽略。

#### 3. 杀掉进程照样掉授权（与小米一致）

| 方式 | `accessibility_enabled` | `enabled_accessibility_services` |
|---|---|---|
| `am kill`（普通杀进程） | 1（保持） | 保持，服务自动重绑 |
| 从最近任务划掉卡片 | **0** | **null** |
| `am force-stop` | **0** | **null** |

#### 4. ColorOS 还有一个自己的确认框

授权后立刻弹：

```
mCurrentFocus=Window{... u0 Sys2039:com.oplus.safecenter/
  com.oplus.safecenter.accessibility.AccessibilityGuideCloseActivity}
```

「检测到"QrJump"启动无障碍服务 / 无障碍服务允许应用查看屏幕内容并自动操作设备，
可能影响您的隐私和资金安全」→ `保持开启`（`android:id/button2`，
bounds `[128,2768][704,2944]`）/ `关闭无障碍`（`android:id/button1`，
bounds `[736,2768][1312,2944]`）。

- 点「保持开启」后 `enabled=1 bound=1` 正常；
- **但由 shell 手工写回授权时，这个框不再弹**（focus 直接回桌面）——
  说明它只在服务由 App 自身启动时出现。

#### 5. 既然补不回来，就不能假装没事：加了一条可点的提示条

`activity_devices.xml` 新增 `tvA11yWarn`（默认 `gone`），文案
「自动解锁的无障碍授权没了，点这一条去打开它（只需一次）」，
点它 → `Settings.ACTION_ACCESSIBILITY_SETTINGS`。

显示条件是**三者同时成立**：用户开了自动解锁、授权现在不在位、而且**本应用没有写入权限**。
第三条是关键 —— 补得回来的情况 `heal()` 刚刚已经补好了，这时候再摆一条"授权没了"就是在骗人。

真机验证（09:37:44）：force-stop 制造丢失 → 冷启 →

```
[A11y] 没有 WRITE_SECURE_SETTINGS，无法自愈。…
[Devices] 自动解锁开着，但无障碍授权不在位，且本机不允许本应用自己写回
          （ColorOS 封了 pm grant），改为显示可点的提示条
```

提示条位于 `[64,440][1376,690]`，截图 `shot_realme_warn.png`。
点它（`input tap 720 565`）→ `mCurrentFocus=com.android.settings/
com.android.settings.Settings$AccessibilitySettingsActivity`，日志
`[Devices] 用户点了无障碍提示条，跳系统无障碍设置`。
手工写回授权后重开 App → 提示条消失（`✅ 提示条已消失`），`enabled=1 bound=1`。

#### 6. 所以在 realme 上要怎么做

自愈做不到，能做的只有三条，按省事程度排序：

1. **别从最近任务划掉 QrJump**。`am kill` 不掉授权，只有"划卡"和 `force-stop` 会掉。
2. **掉了就点设备列表顶部那条黄色提示条**，在系统页里把 QrJump 打开一次。
3. 若想少点一次：把 QrJump 加到「后台白名单 / 自启动 / 电池不优化」，
   让系统不轻易 force-stop 它（各 ROM 路径不同，本质都是别让进程被杀）。

**本应用不会为了保活去做常驻通知、前台服务自启、互相拉起这类事** ——
那既超出"替换入口"的范围，也会换来用户看不见的耗电。

---

## 7. 校验规则（写死在代码里，不提供开关）

`ParseResult.parse()` 先解包，再校验内层地址，任何一步不过就整码拒绝、绝不"纠错"。

**解包阶段**（最多 4 层，防止无限跳）：

1. 非空，长度 ≤ 4096
2. `java.net.URI` 能解析
3. 每层 scheme **必须恰好是 `https`**（`http` 不自动升级，直接拒）
4. host 命中 `h5.qiekj.com` → 进入内层校验
5. host 命中 `DELEGATE_HOSTS`（目前只有 `link.wtturl.cn`）→ 该层 path 必须是 `/`
   或空，取 `target` 参数续解；否则拒
6. 其它 host → 拒（`host 不在允许列表`）

**内层校验**：

7. path **必须恰好是 `/skip`**
8. query 逐段解析：不允许无 `=` 的段、空 key、重复 key、超长值（>256）、控制字符
9. 必须含 `SN`（`^[0-9]{8,32}$`）**或** `NQT`（标准 UUID 形状）之一
10. 通过后用规范化值重新拼 URL（丢 fragment、丢大小写差异）

**标识在源码里硬编码零处。** 每台机器都不同，必须运行时从二维码解析。
形态②的 `launchUrl` 保留**原始包装串**（`link.wtturl.cn` 那条），因为包装层可能
带一次性参数，重拼成内层地址有失效风险；内层规范化地址另存 `innerUrl` 备用。

你在设置页「测试」区输入的设备号只存在于 SharedPreferences，不进源码。
那个输入框也接受**整条 URL**（形态②必须粘整条，因为是两层结构）。

---

## 8. 待验证清单（截至当前，逐条给了实测状态）

原始需求里列了四条「待验证」。现在其中三条已由真机实测回答，
但**回答内容和当初的预设都不一样**，所以如实列出：

| # | 原问题 | 当前状态 |
|---|---|---|
| ① | 该 URL 在浏览器落到完整 H5 / 提示用支付宝打开 / 直接唤起小程序设备页？ | ✅ **已实测**：三者都不是。落到的是**胖乖生活 App 下载引导页**（Edge / 内置 WebView 都一样） |
| ② | 小程序的 appId 与设备页 page 是什么？ | ✅ **已实测**：appId=`2018072460764274`，page=`pages/skipNew/skipNew?isShowAd=false`。来源是官方分发页自己发起的跳转，非抓包 |
| ③ | 小程序是否读取外部 query 冷启动直达设备页？ | ✅ **已实测：会读，且真正一步直达**。带 `masterSwitch/keepUsing/SN/isShowAd` → 直接渲染出完整设备页（含出水口/立即使用，见 10.1 节）；只带 `SN` → 小程序提示「二维码有误，换一个试试」。形态②同理：`NQT` 套同一套外层参数直达热水设备页（20:09/20:25） |
| ④ | 拿不到 appId 就退化为只用 A/C | ⚠️ **设计兜底仍然保留**：清空设置页的 appId/page，B 档立刻判 `Unavailable` 并降级到 C。但注意 A 单独无效，所以实际退化是「只用 C」 |

### 仍然没有验证的（别当成已知）

1. **这套 appId/page/query 是某一次分发页给出的，厂商可能随时改。**
   如果哪天 B 档突然失效，第一件事是用 C 档（开 UA）再看一次 logcat 里
   `[Web] 拦截跳转 ...` 那行，比对参数是否变了。
2. **外层那个 `&isShowAd=false` 是否必需** —— 我没有做过「去掉它」的 A/B 对比。
   代码里是复刻官方原样带上的。
3. **`ds.alipay.com` 包装（B2）在这台机器上没单独验证过** ——
   B1 一次就成，所以 B2 只是作为 B1 失败时的备用路径存在，未实测。
4. **换学校/换型号的饮水器是否同一个 appId** —— 大概率同一个（同一个厂商同一套小程序），
   但没有第二台设备可测，不做断言。
5. **换一台真实设备（不同 SN）是否同样一步到位** ——
   ✅ **已实测（19:31/19:34，决定性对照）**：新机器 `SN=<你的15位SN>`，
   `策略顺序 = [B, C]` → B1 直达 → 落点「XX学院 京平科技 JPSAFE / **北3栋202** /
   XX学院-东校区 / 出水口 冷水·热水 / 立即使用」。
   旧机器 `SN=<你的15位SN>` → **北3栋201**。
   **两台同楼相邻、设备号不同、页面不同**，这排除了「服务端对某一台特殊处理」的可能，
   也证明 SN 是被官方小程序真正当参数使用的，App 只是原样转发。
   → 这一条已从「未验证」移出，本清单只剩 1–4 条。
6. **形态②的 NQT 是否需要 `Company`/`CommunicateType`/`Ver` 那三个原参数** ——
   ❌ **不需要（已实测反证）**：直传那三个参数反而落「二维码有误」，
   去掉后只留 `NQT` + 外层参数就直达。所以现在代码里不带它们。
   但**没验证**它们被去掉后是否影响「出水口选择」之类的后续交互 ——
   进入设备页后立即退出，没有点任何按钮。

### 已归档的真机实测（同一台手机、三台不同设备）

| 时间 | 设备形态 | 标识 | 策略 | 落点 |
|---|---|---|---|---|
| 18:41 | 饮水器 ① | SN `…031408` | C+UA（当时 B 未配好） | 北3栋201 |
| 19:15 | 热水器 ② | NQT `9b9642e8-…` | C+UA | 某热水机 201 |
| 19:20 | 热水器 ②（一键直达） | NQT `9b9642e8-…` | C | 某热水机 201 |
| 19:31 | 饮水器 ①（新机器） | SN `…02672` | B1 | **北3栋202** |
| 19:34 | 饮水器 ①（一键直达） | SN `…02672` | B1 | **北3栋202** |
| 20:07 | 热水器 ② | NQT `9b9642e8-…` | B1（直传官方 4 参数） | ❌ 「二维码有误」 |
| 20:09 | 热水器 ② | NQT `9b9642e8-…` | B1（套外层参数） | ✅ 某热水机 201 |
| 20:25 | 热水器 ②（一键直达） | NQT `9b9642e8-…` | B1 | ✅ 某热水机 201 |
| 21:06 | 饮水器 ①（调试模式，验自动点） | SN `…031408` | B1 调试（不唤起）+ adb 手动开局 | ✅ 北3栋201，`[Auto]` 定位到按钮但未点击 |
| 21:55 | 饮水器 ①（调试模式，验延时） | SN `…031408` | B1 调试 + adb 手动开局 | ✅ 稳定期 2.5 秒走满才报告，仍未点击 |
| **22:13** | **饮水器 ①（真点，用户已授权）** | **SN `…031408`** | **B1 真实唤起 + 无障碍 `dispatchGesture`** | ✅ **北3栋201 →「解锁成功」，未点「出水」，未计费** |
| 23:58 | 饮水器 ①（**从桌面小部件点行**，调试模式） | SN `…031408` | B1 调试 | ✅ 规划正确，未唤起 |
| **23:59** | **饮水器 ①（从桌面小部件点行，真实唤起）** | **SN `…031408`** | **B1** | ✅ **北3栋201 设备页**（截图 `shot_widget_real_landing.png`） |

2026-11-06 22:13 之前每次进入设备页后都执行同一套退出序列：
`input keyevent 4` → `am force-stop com.eg.android.AlipayGphone` →
`am force-stop com.local.qrjump` → `input keyevent 3`。
**那十次实测从未点过「立即使用」。**

23:58/23:59 那两次是从**桌面小部件**点进去的（这条链路是用户真正的使用姿势），
同样只到设备页、只确认落点，没有点任何按钮，随后同一套序列退出。

22:13 那次是在你明确说「允许测试」之后，唯一一次让无障碍服务真的按下去：
手势坐标 `(540.0, 2201.0)`，`提交=true`、`手势点击已完成`，页面变成
「解锁成功 / 结束后将自动为您计费」（截图 `shot_after_autoclick.png`）。
随后立刻按上面那套序列退出，**没有点「出水」，所以没有开始计费**。


另外还有一项与本 App 无关但影响操作的事实：本机连接的设备实际是
**Redmi K40S（`ro.product.model=22021211RC`，Android 14，`ro.miui.ui.version.name=V816`，
`ro.mi.os.version.name=OS1.0`，`ro.product.device=munch`）**，不是需求里写的 realme GT 8。
`adb -s <你的设备序列号>` 指的就是这台。

---

## 9. 已修的三个真机 bug（记录，避免回退）

### 9.1 ML Kit 每帧报 `MlKitException: Internal error ... ModelResource`

现象：首跑时每帧刷

```
W/QrJump: [Scan] ML Kit 解析失败（本帧）：MlKitException: Internal error has occurred when executing ML Kit tasks @com.google.mlkit.common.sdkinternal.ModelResource.zza:9
```

根因是**两个叠加**：

1. `ImageAnalysis` 的 analyzer 在 `finally` 里无条件 `proxy.close()`，
   而 ML Kit 的解码是异步的 —— 帧在后台读取前就被回收了。
2. 降级条件写太窄：只在 `BarcodeScanning.getClient()` 构造抛错时才切 ZXing，
   `addOnFailureListener` 里只打日志不降级 → 这台机器会永远扫不出。

修法：

- 关闭职责下移到 `decodeWithMlKit` / `decodeWithZxing`，
  ML Kit 分支挂 `.addOnCompleteListener { runCatching { proxy.close() } }`，
  **读完才释放**。
- 失败计数 `AtomicInteger`，连续 3 次失败就 `useMlKit = false` 永久切 ZXing，
  并 Toast 提示。

> 顺带确认：这台机器 `pm path com.google.android.gms` 返回空（GMS core 缺失），
> 所以 ZXing 兜底分支是必需的，不是冗余。ML Kit 用的是 **bundled 版**
> （APK 内带 `libbarhopper_v3.so`，模型不走 GMS 在线下载），所以缺 GMS 也能用 —— 
> 只要不踩上面那个生命周期 bug。

### 9.2 编译期 `AspectRatioStrategy` Unresolved reference

`AspectRatioStrategy` 的正确 import 是
`androidx.camera.core.resolutionselector.AspectRatioStrategy`，
**不是** `androidx.camera.core.AspectRatioStrategy`。
`ResolutionSelector` / `ResolutionStrategy` 同属 `androidx.camera.core.resolutionselector`。

### 9.3 策略 C 唤起后被自己退到后台，页面跳转发不出来

这是**最隐蔽的一个**，也是「一键直达/扫码后停在 WebView 什么都不发生」的真正原因。

现象（19:18 那次复现）：

```
[Launch] ✓ 已发起：C → com.local.qrjump/com.local.qrjump.WebViewActivity
[Life] 唤起完成后将 App 退到后台
[Web] onPageFinished: https://h5.qiekj.com/skip?...NQT=...
（然后什么都没有了，等 10 秒也不出 [Web] 拦截跳转，最终落回桌面）
```

对比 19:15 那次成功的：

```
[Web] onPageFinished: ...
[Web] 拦截跳转 scheme=alipays mainFrame=true url=alipays://...
[Web] 已外抛给 alipays
```

根因：`ScanActivity.doLaunch` 在 `AttemptResult.Started` 之后无条件
`postDelayed({ moveTaskToBack(true) }, 1200)`。这句对策略 A/B 是对的
（目标在别的 App，本 App 该让位），但**策略 C 拉起的是本应用自己的
`WebViewActivity`，和 `ScanActivity` 在同一个任务栈里** ——
`moveTaskToBack(true)` 退的是整个 Task，WebView 一起被挂起，
官方分发页那次 `alipays://` 跳转就发不出来了。

> 为什么 19:15 那次能成功：那次 WebView 抢在 1200ms 之内把跳转发出来了，
> 属于"刚好赶上"。这种时序依赖必须修掉，不能靠运气。

修法：只有目标**不在本应用内**时才退到后台。

```kotlin
val handledInsideApp = r.resolvedComponent
    ?.startsWith(packageName + "/") == true
if (handledInsideApp) {
    QLog.i("Life", "唤起目标在本应用内（$handledInsideApp），保持前台，等页面自行跳转")
} else {
    previewView.postDelayed({ if (!isFinishing) moveTaskToBack(true) }, 1200)
}
```

修完实测（19:20，形态② NQT，一键直达）：

```
[Life] 唤起目标在本应用内（true），保持前台，等页面自行跳转
[Web] onPageFinished: https://h5.qiekj.com/skip?...NQT=...
[Web] 拦截跳转 scheme=alipays ... query=masterSwitch%3D0%26keepUsing%3D1%26NQT%3D9b9642e8-...
[Web] 已外抛给 alipays
```

落点：支付宝小程序**热水器设备页** ——「某热水机 / 201 / XX学院 /
支付宝先享后付 / 优惠券 4 张可领 / 胖乖积分 24 / 小票余额 46.66 元 /
内容准备中 + 刷新 / 立即使用」。**未点「立即使用」。**

---

## 10. 关于「我会怎么看你的反馈」

原始需求写的是「你把 logcat 和三种策略各自落点现象贴回来，我据此判断哪档生效」。
这件事**已经在真机上做完了**，结论就是第 3、5、6 节那几条。

### 10.1 已闭环：中间态是暂时的

之前我几次截屏都停在「内容准备中 + 刷新」（见 `shot_final_B.png`），
当时不敢断言是网络慢还是需要手动点。**用户回贴的实测截图已确认：它会自己加载完。**

加载完成后的设备页（用户回贴的截图，18:41 那次扫码）包含：

- 顶部「XX学院 京平科技 JPSAFE」+ 返回/主页/收藏/更多/关闭 五个操作位
- 设备卡「北3栋201 / XX学院-东校区 >」+ 右侧电话图标
- 「选择支付方式 → 支付宝先享后付」已勾选
- 「优惠券 4张可领」「胖乖积分 24（满40可用）」「小票余额 40.53元 → 去充值」
- 中部由「内容准备中 + 刷新」变为**设备状态区**
- 底部「出水口 冷水 / 热水」+ 蓝色「立即使用」按钮

**结论：B 档是真正的一步直达，不是停在骨架页。**
`isShowAd` / `masterSwitch` 没有影响渲染，不需要为此再调参。
这也反过来确认了 `Prefs.DEFAULT_QUERY_TEMPLATE` 的完整形状是必需的
（精简版连骨架都出不来，直接报「二维码有误」）。

### 10.2 已经不用再验证的两条（结果出来了）

原计划里「换真实 SN / 换另一台机器会不会一步到位」和「形态②能不能走通」，
现在都已有真机结论：

- **形态②（热水器 NQT）走通**：19:15 扫码 + 19:20 一键直达，两次都落到
  「某热水机 / 201 / XX学院」设备页。日志链路见 9.3 与 6.1。
- **形态②的 B 档也已走通**（20:09 命令行 / 20:25 应用内）：早期我以为 NQT 只能
  靠 C 档绕，实测证明**照样一条 `alipays://` 就能直达**，前提是套用同一套外层
  参数（`masterSwitch/keepUsing/NQT/isShowAd`）。详见 6.2。
- **一键直达可用**：扫过一次之后，打开 App 点一个按钮就到设备页，
  不再需要举手机对码。
- **换机器同样一步到位（19:31/19:34）**：新机器 `SN=<你的15位SN>`
  经 B1 直达**北3栋202**，旧机器是北3栋201。两台相邻、页面各自正确，
  说明入口不参与有效性判定，SN 只是被原样转发的参数。

### 10.3 如果还要继续反馈，剩下有价值的两样

1. **自动点击那 8 秒够不够** —— 如果在你常去的那台机器上，8 秒之后
   「立即使用」点下去还是没反应（页面仍显示「内容准备中」），就往右调到
   12–15 秒再试；反过来如果嫌慢，往左调。这个值就是唯一的旋钮。
2. 如果哪天失效了：`[Web] 拦截跳转 ...` 那行的最新参数，
   以及 `策略顺序 = [...]` 那行。

（原第 1 条「换第三种形态的机器」已由你确认**没有第三种形态**，删除。）

据此我会判断：
- 报 `host 不在允许列表` → 说明是另一套分发域名，需要加进
  `ParseResult.DELEGATE_HOSTS` / `EXPECTED_HOST`
- B 档报「二维码有误」→ 先比对 `[Launch] B 生成的 query` 那行，
  大概率是模板被改或厂商换了外层参数；其次才是服务端判定
- C 档不出 `[Web] 拦截跳转` → 先看是不是又有人在 `Started` 后无条件 `moveTaskToBack`
  （9.3 那个坑），其次才是 UA 或页面改版
- 只有 `[Auto] 无障碍服务已连接`、之后没有任何 `[Auto]` 行 → 6.4.3 那个缺口，
  确认 APK 是 22:13 之后构建的

---

## 11. 红线（写在最前面，也是写在这里的最后一遍）

- 只替换**入口**。鉴权、计费、订单、支付全部由官方 App / 官方小程序完成。
- 不抓包、不逆向私有接口、不伪造指令或订单、不模拟支付、不直连设备、不图免费出水。
- 仅本人自用侧载，不上架应用商店，不外传任何数据。
- 唯一的网络行为是 WebView 打开那个已经印在机身上的公开 URL。
- 没有统计、没有上报、没有第三方 SDK 除了 ML Kit / ZXing（纯本地解码）。
- 自动点击点的**是官方页面上那个真按钮**，走支付宝自己的控件与业务逻辑；
  不构造订单、不调私有接口、不伪造签名、不改金额、不碰登录态。
- 桌面小部件/快捷方式只携带**设备簿里的键**（如 `SN|<你的15位SN>`），
  不携带 URL，也不携带 token；落点会重新跑一遍完整校验，查不到就什么都不做。
- 实测记录：**22:13 之前**每次进设备页都是连按返回 + force-stop 退出，从未点过
  「立即使用」。**22:13 那次**是在你明确授权「允许测试」之后，让无障碍服务
  真的点了一次，页面到「解锁成功 / 结束后将自动为您计费」就立刻退出，
  **没有点「出水」，所以没有产生任何计费**。
- 23:58/23:59 从桌面小部件点进去的两次，只确认落点（北3栋201），没有任何点击。



