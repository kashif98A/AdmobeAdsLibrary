# AdmobeAdsLibrary

AdmobeAdsLibrary is an Android library designed to simplify the management of Google AdMob ads,Update App,rating app and User Messaging Platform (UMP) consent. This library
provides a streamlined approach to integrating ads and handling user consent for privacy compliance,
while also offering a sample project to demonstrate its usage.

## Features

- **AdMob Ads Management**: Easily integrate and manage AdMob ads in your Android applications,
  including banner, interstitial, and native ads.
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
   implementation ("com.github.kashif98A:AdmobeAdsLibrary:1.1.3")
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
    NextActivity::class.java.canonicalName, "ca-app-pub-3940256099942544/1033173712"),
    "some_value",
    123,false
)
```

To load interstitial ads  on Demand use following code.
```kotlin
  loadInterstitialAd(this@SplashActivity, "ca-app-pub-3940256099942544/1033173712")


 binding.NextButton.setOnClickListener(View.OnClickListener {
     ///show Here
    showInterstitial(this) {
        val intent = Intent(this@SplashActivity, MainActivity::class.java)
        startActivity(intent)
    }
})


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
manage ads, purchases, and user consent. Follow these steps to run the sample project:

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
