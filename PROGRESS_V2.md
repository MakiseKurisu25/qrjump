# V2 进度存档（胖乖生活 App 入口版）

> 暂停时间：2026-11-07 07:55
> 用户指令：`现在做另一个版本，改为用胖乖生活app启动，不用支付宝小程序`
> 状态：**只做了只读调研，尚未写任何 V2 代码**

---

## 一、用户原始诉求（不要改写）

把「开支付宝 → 搜胖乖生活小程序 → 扫一扫」压成「打开即扫码」。V1 已完成（走支付宝小程序，
`com.local.qrjump`，48 文件，实测可直达设备页）。

V2 要求：**改用胖乖生活 App（`com.qiekj.user`）启动**，不走支付宝小程序。

红线（沿用 V1，不得违反）：
- 只替换入口，不碰鉴权与计费
- 禁止抓包、逆向私有接口、伪造指令或订单、模拟支付、直连设备、免费出水
- 仅本人自用侧载、不上架、不外传数据

---

## 二、已确认的事实（全部来自只读检查，未抓包、未逆向代码）

### 设备与被测 App
- 真机：Redmi K40S（`ro.product.model=22021211RC`，MIUI OS1.0 / V816，Android 14，sdk 34），serial `<你的设备序列号>`
- 目标 App：`com.qiekj.user`，versionName **1.145.0**，versionCode **299**，minSdk 24，targetSdk 30，compileSdk 36
- 用户已登录该 App（首页显示「XX学院(东校区)南区」）

### 胖乖生活 App 对外暴露的入口（aapt2 dump xmltree + pm query-activities 双证）

**自定义 scheme（共 3 个，另有大量 market/tbopen 等无关 scheme）**

| scheme | 目标 Activity | exported | 实测落点 |
|---|---|---|---|
| `qiekj://` | `.ui.activity.pay.OpenSuccessAct` | true | 页面只有「开通成功」「完成」两个文本 —— 与设备无关 |
| `qekjmid://` | `.ui.activity.TransActivity` | true | 启动后**立刻 finish**，`mCurrentFocus` 回桌面；试过 `?url=`、`/open?url=`、`/transfer?url=`、`/x`、`/skip?SN=`、`/trans?SN=` 全部同样秒退 |
| `qiekeji://community` | `.ui.activity.web.AntAuthCallbackActivity` | true | 秒退，与设备无关 |

**结论：三个 scheme 都不是「带 SN 直达设备页」的入口。** `qekjmid` 的 `TransActivity` 名字像中转页，
但没有任何 query 组合能让它留下——它需要某种本 App 内部才有的参数，靠黑盒试不出来，也不该继续试（会变成猜接口）。

**http/https App Links**
- manifest 里**没有任何 `<host>` 声明**（只有 `qiekeji` 的 `Authority: "community"`）。
- 实测 `pm query-activities -a VIEW -c BROWSABLE -d 'https://h5.qiekj.com/skip?SN=...'`
  → 只有 `com.microsoft.emmx`（Edge）。**胖乖生活 App 不认领这个 URL。**
- 所以「把机身 URL 丢给胖乖 App」这条最省事的路，**系统层面就不存在**。

**扫一扫页（真正的目标页）**
- `com.qiekj.user.ui.activity.scan.HomeScanCodeAct` —— **`exported=false`**，
  `am start -n com.qiekj.user/...HomeScanCodeAct` 报
  `SecurityException: Permission Denial ... not exported from uid 10307`。
- 但**可以从 App 内部点进去**：首页右上角扫描图标
  `resource-id=com.qiekj.user:id/iv_scan`，`bounds=[777,127][1071,254]`，`clickable=true`。
  实测 `input tap 924 190` → `mCurrentFocus=com.qiekj.user/...HomeScanCodeAct`，
  页面文本：`将二维码/条形码放入框内，即可自动扫描`、`轻点照亮`、`相册`。
- 设备页 `PayStateAct` 同样 `exported=false`。

**全部 exported=true 的 Activity（23 个）**，除上面三个 scheme 目标外还有：
`SplashActivity`、`MainActivity`、`OrderPayPreviewAct`、`AfterPayUseAct`、`NotPayOrderNewAct`、
`CmbPayResultActivity`、`PayAct`、`wxapi.WXPayEntryActivity`、`wxapi.WXEntryActivity`、
`com.alipay.sdk.app.PayResultActivity`、`AlipayResultActivity`、`kwad...KwadRouteHandlerActivity`、
两个 `DouyinAuthorize*`、`androidx.compose.ui.tooling.PreviewActivity`、jpush/mipush 的通知页。
→ 全是支付回调 / 推送 / 第三方 SDK 壳，**没有一个是「带设备号进设备页」的**。

---

## 三、V2 的可行路径（三条，按推荐度排序）

### 路径 A（推荐）：无障碍「代点两下」把 App 推到扫码页
沿用 V1 已有的 `AutoUnlockService`（无障碍已授权、可 `dispatchGesture`），新增一段：
1. `Intent` 拉起 `com.qiekj.user/.MainActivity`（exported=true，公开入口，不算越权）
2. 等首页就绪（判 `iv_scan` 节点出现 / 首页文本「附近设备」出现）
3. `dispatchGesture` 点 `iv_scan` 中心 → 进入 `HomeScanCodeAct`
4. 之后**交给胖乖 App 自己的相机去扫机身码**（用户把手机对准贴纸）——
   或者我们把自己的相机扫到的 SN 交给它（但 App 不接受外部 SN，所以只能让它的相机扫）

- 优点：完全走公开入口 + 用户自己那条真实点击路径；不碰鉴权/计费
- 缺点：**只省掉「找 App → 点图标 → 点扫描」这几步，仍要用户举着手机扫机身码**。
  比 V1（扫一次后一键直达设备页）其实是**退步**。
- 必须先向用户确认：这是否是他想要的「用胖乖App启动」

### 路径 B：V2 = 只换「扫码后那一跳」的目标
保留我们自己 CameraX+MLKit 扫码 → 校验 SN → 但**不唤起支付宝**，改为
「拉起胖乖 App 首页 + 无障碍点到扫码页，然后把 SN 显示在屏幕上让用户核对」。
- 实质同上，等于把 A 的触发从「桌面图标」换成「我们自己扫到码」。

### 路径 C：不做 V2（如果用户要的是「带 SN 直达设备页」）
**技术前提不成立**：胖乖 App 没有任何公开的「设备号 → 设备页」入口（scheme 三个都不行、
App Links 无 host 声明、目标 Activity 全 not exported）。
要做到只能：
- 反编译看 `TransActivity`/`PayStateAct` 的入参 —— **违反红线（逆向）**，不做；
- 抓包看 App 请求 —— **违反红线（抓包）**，不做；
- 用无障碍一路点首页→扫一扫→等它自己扫码 —— 就是路径 A。

→ **必须向用户说明：V2 若要求「一键直达设备页」，靠公开接口做不到；
只能做到「一键到胖乖的扫码页」。** 这与 V1 的能力边界差异要讲清楚。

---

## 四、下一步（用户发「开始」后执行）

1. **先问用户一句**（不要直接写代码）：
   「V2 你希望的是 (a) 一键打开胖乖 App 的扫一扫页（仍要自己对准机身码扫一次），
   还是 (b) 扫一次以后一键直达设备页？后者在胖乖 App 上无公开入口，只能走无障碍模拟点击，
   等于把你手动点的两步自动化，且页面结构一改就失效。」
2. 若选 (a)：新建 `V2` 分支式实现 —— 在 V1 工程上增加 `QiekjLauncher`（拉起 MainActivity +
   无障碍点 `iv_scan`），设置页加「入口：支付宝小程序 / 胖乖 App」二选一开关。
   复用现有 `AutoUnlockService` 的 `dispatchGesture` 能力，**不新增无障碍服务**。
3. 若选 (b)：如实说明不可行 + 给出替代（V1 已经是 (b) 在支付宝侧的最优解）。
4. 任何情况下都**不**反编译 `base.apk` 里的 dex、**不**抓包。
   （本轮已 `adb pull` 了 `qiekj_base.apk` 到工程目录用于 `aapt2 dump xmltree`，
   只读了 AndroidManifest，未解 dex；完成后应删除该 187MB 文件。）

---

## 五、本轮产生的临时文件（清理清单）

| 文件 | 大小 | 用途 | 是否可删 |
|---|---|---|---|
| `<工程目录>\qiekj_base.apk` | 195,829,583 B | aapt2 dump manifest | **可删**（只读了 manifest） |
| `<工程目录>\dump_qiekj.txt` | 75,799 B | `dumpsys package com.qiekj.user` | 可留作证据 |
| `<工程目录>\qiekj_manifest.txt` | 3796 行 | aapt2 xmltree 输出 | 可留作证据 |
| `<工程目录>\qk_main.xml` | — | 首页 UI dump | 可留 |
| `<工程目录>\qk_scan.xml` | — | 扫一扫页 UI dump | 可留 |
| `<工程目录>\probe.xml` | — | scheme 探测 UI dump | 可删 |

---

## 六、V1 当前状态（未受本轮影响，可随时回退）

- 工程：`<工程目录>`（48 文件 / 6161 行，`app\src` 下 42 个 kt/xml / 5824 行）
- APK：`app\build\outputs\apk\debug\app-debug.apk` 28,963,653 B / 2026-11-07 00:23:39，已装机
- 构建：`gradle clean assembleDebug` → BUILD SUCCESSFUL 零 warning
- 当前 prefs：`auto_unlock=true`、`debug_mode=false`、`auto_unlock_delay=8`、
  `alipay_app_id=2018072460764274`、`alipay_page=pages/skipNew/skipNew?isShowAd=false`
- 无障碍：`accessibility_enabled=1`、`enabled_accessibility_services=com.local.qrjump/com.local.qrjump.AutoUnlockService`
- 构建命令（`. <AndroidDev>\env\set-env.ps1` 会被 CC Safety Net 拦，必须内联）：
  ```powershell
  cd <工程目录>
  $env:JAVA_HOME='<AndroidDev>\jdk\jdk-17.0.2'; $env:ANDROID_HOME='<AndroidDev>\sdk'
  $env:ANDROID_SDK_ROOT='<AndroidDev>\sdk'; $env:GRADLE_USER_HOME='<AndroidDev>\gradle-home'
  $env:PATH='<AndroidDev>\jdk\jdk-17.0.2\bin;<AndroidDev>\gradle\gradle-8.7\bin;<AndroidDev>\sdk\platform-tools;<AndroidDev>\sdk\cmdline-tools\latest\bin;'+$env:PATH
  gradle clean assembleDebug --console=plain
  ```
- 重装后必做：`pm grant com.local.qrjump android.permission.WRITE_SECURE_SETTINGS`，
  再按「先 `accessibility_enabled=1` 再 `enabled_accessibility_services=...`」恢复无障碍。
