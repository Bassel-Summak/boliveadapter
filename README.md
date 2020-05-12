# BoLive Adapter

[![](https://jitpack.io/v/Bassel-Summak/boliveadapter.svg)](https://jitpack.io/#Bassel-Summak/boliveadapter)

Retrofit library provides an ideal solution for Android to compose HTTP connections easily through a simple expressive interface. However, the library has a limitation. It only serializes non-error HTTP responses. Any error data returned from the server won't be serialized and will be stored in a string variable. Thus, you won't be able to make a decision or take any complicated actions based on the server response.

<br />

To overcome this issue, this library provides:

<img align="left" width="150" height="150" src="https://www.dropbox.com/s/2vgveii5dzs9d1c/app_launcher.png?raw=1">

**1-** The ability to serialize success and error responses into two different type classes.

**2-** Define the error codes that require serializing.

**3-** Merge the solution from google architecture components sample and improve it to provide observable LiveData objects.

<br />

# Installation

<br />

**Step 1**

Add this to your root build.gradle at the end of repositories.

```gradle
allprojects {
  repositories {
    ...
    maven { url 'https://jitpack.io' }
    }
 }
```

**Step 2**

Add the dependency.

  
```gradle
dependencies {
  ...
  implementation 'com.github.Bassel-Summak:boliveadapter:version-number'
}
```
<br />

If you are using maven, sbt or leiningen. You can check this link for more installation info.

https://jitpack.io/#Bassel-Summak/boliveadapter

<br />

# How to use

<br />

- Add the adapter factory to retrofit

```kotlin
  val retrofitBuilder =   Retrofit.Builder()
            .baseUrl(Constants.URLS.BASE_URL)
            .addCallAdapterFactory(BoLiveDataAdapterFactory())
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()))
            .build()
```

- Replace your functions in interface

```kotlin
// OLD
 @GET
    fun getEmployeeData(
        @Url url : String
    ): EmployeeData
```
With

```kotlin
// NEW
    @GET
    fun getEmployeeData(
        @Url url : String
    ): LiveData<BoGenericResponse<EmployeeData, Error>>
```

Where **EmployeeData** represents the class you want to serialize success responses to it, while **Error** for error ones.

<br />

- Then you can observe server response like this

```kotlin
client.getEmployeeData(url).observe(this@MainActivity, Observer { response->

                    when(response){
                        is BoGenericResponse.ApiSuccessResponse ->{
                            val responseBody : EmployeeData = response.body
                            Log.i(TAG, " Returned Data -ApiErrorResponse-: $responseBody")
                        }
                        is BoGenericResponse.ApiErrorResponse ->{
                            val errorBody : Error = response.errorBody
                            val errorCode : Int = response.errorCode
                            Log.i(TAG, " Returned Data -ApiErrorResponse-: $errorBody")
                            Log.i(TAG, " Returned Data -ApiErrorResponse-: $errorCode")
                        }
                        is BoGenericResponse.ApiEmptyResponse ->{
                            Log.i(TAG, " Returned Data -ApiEmptyResponse-: $response")
                        }
                        is BoGenericResponse.ApiUnhandledErrorResponse ->{
                            val errorMessage : String = response.errorMessage
                            Log.i(TAG, " Returned Data -ApiUnhandledErrorResponse-: $errorMessage")
                        }
                    }

                })
```
<br />

**ApiSuccessResponse:**  will be triggered for success responses.

**ApiErrorResponse:** will be triggered for serialized error responses.

**ApiUnhandledErrorResponse:** will be triggered for all non-serialized errors.

**ApiEmptyResponse:** will be triggered when the server returns an empty response.

<br />

# Notes

<br />

- The library will try to serialize all responses with error codes to the class you have selected. If you want to handle only specific errors and ignore others, the library support this. To enable it, you need to define them in the factory. Simply send a list of integers as a parameter to BoLiveDataAdapterFactory.

```kotlin
        val retrofitBuilder =   Retrofit.Builder()
            .baseUrl(Constants.URLS.BASE_URL)
            .addCallAdapterFactory(BoLiveDataAdapterFactory(listOf(400,417))) // error codes to handle
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()))
            .build()
```

This means the library will only handle errors with **400** & **417** codes. Others responses will be thrown to **ApiUnhandledErrorResponse**

<br />

- If you want to disable error handling mechanism and throw all error responses to **ApiUnhandledErrorResponse** only for one particular service. It's possible by sending **Unit** from Kotlin package as an error class.

```kotlin
    @GET
    fun getEmployeeData(
        @Url url : String
    ): LiveData<BoGenericResponse<EmployeeData, Unit>>
```
All errors will be sent to **ApiUnhandledErrorResponse** for **getEmployeeData** service, regardless the factory settings.

<br />

**For more details, you can check the demo app in this repository.**
