# NextVerSMS (DEMO)
This is library for send SMS

## Implementation
### 1. Gradle (settings.gradle)

```groovy
dependencyResolutionManagement {
    ...
    
    repositories {
        google()
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
```

### 2. Gradle (build.gradle/app)

```groovy
dependencies {
    implementation 'com.github.alidartmedia:nextversms:1.0.0'
}
```

### 3. Sample Code

```kotlin
val nextVerSMS = NextVerSMS.Builder()
      .apiKey(API_KEY)
      .apiSecret(API_SECRET)
      .build()
```

```kotlin
nextVerSMS.verify(PHONE_NUMBER, object : VerifyListener {
    override fun onSuccess() {
        //Do something when success
    }

    override fun onFailed(errorMessage: String) {
        //Do something when failed
    }
})
```
