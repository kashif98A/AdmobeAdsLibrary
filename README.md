# AdmobeAdsLibrary

AdmobeAdsLibrary is an Android library designed to simplify the management of Google AdMob ads,Update App,rating app and User Messaging Platform (UMP) consent. This library
provides a streamlined approach to integrating ads and handling user consent for privacy compliance,
while also offering a sample project to demonstrate its usage.

## Features

- **AdMob Ads Management**: Easily integrate and manage AdMob ads in your Android applications,
  including banner, collapsible banner, interstitial, app-open, and native ads.
- **Native ad styles**: Ready-made native views — `NativeLarge`, `NativeMedium`, `NativeCustom`
  (media-on-the-left card), `NativeFullScreen` (full-screen native), and `NativeCollapsible`
  (collapsible native card with a chevron toggle).
- **Instant / on-demand ads (preloading)**: Preload native and banner ads ahead of time and show
  them instantly (no shimmer wait) when a screen opens — via `NativeAdPreloader` /
  `BannerAdPreloader` and the `showNativeAd()` / `showBanner()` helpers.
- **Ad lifecycle callbacks**: A single `AdsCallBack` interface reports `onAdLoading`, `onAdLoaded`,
  `onFailedToLoad`, `onAdShownFromCache`, `onAdImpression`, `onAdClicked`, `onAdOpened`, and
  `onAdClosed` for native, banner, and interstitial ads.
- **App Update**: Is there a feature in the app to notify about updates, or will it automatically update.
- **Rate us**: Does the app have a 'Rate Us' feature where you can give a rating that will be displayed, helping to increase the app's overall rating.
- **UMP Consent Management**: Manage user consent using Google's User Messaging Platform (UMP) to
  comply with privacy regulations like GDPR and CCPA.
- **Sample Project**: A fully functional sample project to demonstrate how to use the library
  effectively in your own apps.

## Getting Started

### Prerequisites

- Android Studio Arctic Fox (2020.3.1) or later
- Gradle 7.0 or later
- A valid AdMob account


### Installation

1. **Add the library to your project**:

   Add it in your root setting.gradle at the end of repositories:

    ```Kotlin
  	dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
    }
   }
    ```
Add the following to your `build.gradle` file in the `dependencies` section:

 ```Kotlin
   implementation ("com.github.kashif98A:AdmobeAdsLibrary:2.0.3")
   ```

2. **Sync your project** with Gradle files.

### Usage

#### Initializing the Library

The `AdsConsentManager` should be initialized in the first activity of your application to ensure
that the consent form is displayed to the user as required.

   ```Kotlin

    class MainActivity : AppCompatActivity() {
    @Override
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize the AdsConsentManager
        requestconsentfome(this)
    }
}
  ```

#### Managing AdMob Ads

For displaying banner ad include following code in xml

```xml

<com.lib.admoblib.bannerAds.AdaptiveBanner
    android:id="@+id/adaptiveBanner"
    android:layout_width="match_parent"
    android:layout_height="wrap_content" />

    
    var adaptiveBanner: AdaptiveBanner = findViewById(R.id.adaptiveBanner)
    adaptiveBanner.loadAdaptiveBanner(this, "ca-app-pub-3940256099942544/6300978111", true)
 ```

Use following code to load banner ad:

```kotlin


// for Collapsible Banner Ad
 ```xml
<com.lib.admoblib.bannerAds.CollapsibleBanner
android:id="@+id/collapsibleBanner"
android:layout_width="match_parent"
android:layout_height="wrap_content"
app:layout_constraintBottom_toBottomOf="parent"/>
        

var collapsibleBanner: CollapsibleBanner = findViewById(R.id.collapsibleBanner)
collapsibleBanner.loadCollapsibleBanner(this, "ca-app-pub-3940256099942544/6300978111", true)
 ```

Similarly for NativeBannerMedium, NativeLarge

```xml
    <com.lib.admoblib.nativeAds.NativeMedium
    android:id="@+id/nativeMedium"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
/>


var nativeMedium: NativeMediumAds = findViewById(R.id.nativeMedium)
nativeMedium.loadNativeMedium(this,"ca-app-pub-3940256099942544/2247696110",true)

```


```xml
 <com.lib.admoblib.nativeAds.NativeLarge
    android:id="@+id/nativeLarge"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"/>

var nativeLarge: NativeLarge = findViewById(R.id.nativeLarge)
nativeLarge.loadNativeLarge(this, "ca-app-pub-3940256099942544/2247696110",true)
```

##### Custom native (media-on-the-left card)

```xml
<com.lib.admoblib.nativeAds.NativeCustom
    android:id="@+id/nativeCustom"
    android:layout_width="match_parent"
    android:layout_height="wrap_content" />
```

```kotlin
binding.nativeCustom.loadNativeCustom(this, "ca-app-pub-3940256099942544/2247696110", true)
```

##### Full-screen native

Give the view `match_parent` height (usually the only view in an Activity). A built-in close
button is provided.

```xml
<com.lib.admoblib.nativeAds.NativeFullScreen
    android:id="@+id/nativeFullScreen"
    android:layout_width="match_parent"
    android:layout_height="match_parent" />
```

```kotlin
binding.nativeFullScreen.setOnCloseClickListener { finish() }
binding.nativeFullScreen.loadNativeFullScreen(this, "ca-app-pub-3940256099942544/2247696110", true)

// forward lifecycle
override fun onResume()  { super.onResume();  binding.nativeFullScreen.onResume() }
override fun onPause()   { super.onPause();   binding.nativeFullScreen.onPause() }
override fun onDestroy() { super.onDestroy(); binding.nativeFullScreen.onDestroy() }
```

##### Collapsible native

A native card with a large media area and a chevron. Tapping the chevron collapses the media
(and the chevron), leaving the compact icon + text + Install card.

```xml
<com.lib.admoblib.nativeAds.NativeCollapsible
    android:id="@+id/nativeCollapsible"
    android:layout_width="match_parent"
    android:layout_height="wrap_content" />
```

```kotlin
binding.nativeCollapsible.loadNativeCollapsible(this, "ca-app-pub-3940256099942544/2247696110", true)
```

#### Instant / on-demand ads (preloading)

By default, an ad is requested when the screen opens, so the user waits (shimmer) while it loads.
To make ads appear **instantly**, initialize the Mobile Ads SDK once at app start and preload the
ad **before** the screen that needs it.

```kotlin
class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Warm up the SDK so the first ad isn't slow, then preload a native ad.
        MobileAds.initialize(this) {
            NativeAdPreloader.preload(this, "ca-app-pub-3940256099942544/2247696110")
        }
    }
}
```

Preload a banner on the previous screen (an Activity is needed for adaptive sizing):

```kotlin
BannerAdPreloader.preload(this, "ca-app-pub-3940256099942544/2014213617")                  // adaptive
BannerAdPreloader.preload(this, "ca-app-pub-3940256099942544/2014213617", collapsible = true)
```

Then, on the target screen, show it instantly (falls back to a normal load if nothing was
preloaded, and re-preloads for the next screen):

```kotlin
binding.nativeMedium.showNativeAd(this, "ca-app-pub-3940256099942544/2247696110", true)
binding.adaptiveBanner.showBanner(this, "ca-app-pub-3940256099942544/2014213617", true)
```

`showNativeAd()` is available on `NativeLarge`, `NativeMedium`, `NativeCustom`,
`NativeFullScreen`, and `NativeCollapsible`; `showBanner()` on `AdaptiveBanner` and
`CollapsibleBanner`.

#### Ad lifecycle callbacks

Every ad view reports its lifecycle through the `AdsCallBack` interface. All methods have default
(empty) bodies, so override only the ones you need.

```kotlin
binding.nativeLarge.nativeAdsCallback(object : AdsCallBack {
    override fun onAdLoading() {}          // request started (show your loader)
    override fun onAdLoaded() {}           // ad is on screen
    override fun onFailedToLoad(error: AdError?) {}
    override fun onAdShownFromCache() {}   // served instantly from the preload pool
    override fun onAdImpression() {}
    override fun onAdClicked() {}
    override fun onAdOpened() {}
    override fun onAdClosed() {}
})

// Banners use bannerAdsCallback(...)
binding.adaptiveBanner.bannerAdsCallback(object : AdsCallBack {
    override fun onAdLoaded() {}
    override fun onFailedToLoad(error: AdError?) {}
})
```


```kotlin
//call on OncreatMethod
 val  bottomSheetDialog = BottomSheetDialog(this)
bottomSheetDialog.setContentView(com.lib.admoblib.R.layout.bottom_sheet_dialog)
val nativeAdmob = bottomSheetDialog.findViewById<NativeLarge>(R.id.nativeLarge)
nativeAdmob!!.loadNativeLarge(this@MainActivity,"ca-app-pub-3940256099942544/2247696110",true)


override fun onBackPressed() {
    super.onBackPressed()
    showBottomSheetDialog(this,bottomSheetDialog)
}
```

In an Activity
To load interstitial ads can be loaded on request , use the following code."
```kotlin
InterAds.startLoadAdActivity(this,
    NextActivity::class.java.canonicalName, "ca-app-pub-3940256099942544/1033173712",
    "some_value",
    123, false
)
```

You can pass an optional `AdsCallBack` to receive the interstitial lifecycle events
(`onAdLoading`, `onAdLoaded`, `onFailedToLoad`, `onAdImpression`, `onAdClicked`, `onAdOpened`,
`onAdClosed`):

```kotlin
InterAds.startLoadAdActivity(this,
    NextActivity::class.java.canonicalName, "ca-app-pub-3940256099942544/1033173712",
    "some_value", 123, true,
    object : AdsCallBack {
        override fun onAdLoaded() {}
        override fun onFailedToLoad(error: AdError?) {}
        override fun onAdClosed() {}
    })
```

To load interstitial ads on demand use the following code. The optional `AdsCallBack` reports the
same lifecycle events; `onDismissed` always runs so your navigation continues.

```kotlin
LoadAndShowInterstitial.loadInterstitialAd(this, "ca-app-pub-3940256099942544/1033173712")

binding.NextButton.setOnClickListener {
    LoadAndShowInterstitial.showInterstitial(this, callback = null) {
        startActivity(Intent(this@SplashActivity, MainActivity::class.java))
    }
}

// Or load-and-show in one call:
LoadAndShowInterstitial.loadAndShowInterstitial(
    this, "ca-app-pub-3940256099942544/1033173712"
) {
    startActivity(Intent(this@SplashActivity, MainActivity::class.java))
}
```
AppOpen Splash & onResum
```kotlin

class MyApp:Application() {
    override fun onCreate() {
        super.onCreate()
        AppOpenManager(this, R.string.AppOpen)
    }
}



```

#### APP Update

Update the app by pasting the following code into MainActivity.

```Kotlin

       InAppUpdateManagerKotlin.checkForUpdates(this, 100)
```

#### Rate Us

Rate the app by pasting the following code into any Activity you want.

```Kotlin

  InAppReviewHelperJava.launchInAppReview(this) { isSuccess -> }
```

### Sample Project

A sample project is included in the `app` directory. It demonstrates how to use AdmobeAdsLibrary to
manage ads and user consent, including buttons on the main screen that open dedicated screens for
the **full-screen native** and **collapsible native** ads, plus inline **custom native** and
preloaded (instant) native/banner examples. Follow these steps to run the sample project:

1. Clone the repository:

   ```bash
   git clone https://github.com/kashif98A/AdmobeAdsLibrary.git
   ```

2. Open the sample project in Android Studio.

3. Replace placeholders with your own AdMob IDs and configure your app in the Google Play Console
   for in-app purchases.

4. Run the project on an Android device or emulator.


### License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

### Support

For any questions or issues, please open an issue in this repository or contact me
at [iqlevel51@gmail.com](mailto:iqlevel51@gmail.com).
