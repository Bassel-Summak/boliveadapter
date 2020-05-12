package com.bassel.example.boliveadapter

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.bassel.example.boliveadapter.api.DataApiService
import com.bassel.libs.boliveadapter.BoGenericResponse
import com.bassel.libs.boliveadapter.BoLiveDataAdapterFactory
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.CookieManager
import java.net.CookiePolicy
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    lateinit var client: DataApiService
    private val TAG: String = "AppDebug"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initClient()
        initClicks()
    }

    private fun initClicks(){

        btn_data.setOnClickListener{
            tv_info.text = ""
            callAPI(Constants.URLS.DATA_URL)
        }

        btn_error.setOnClickListener{
            tv_info.text = ""
            callAPI(Constants.URLS.ERROR_URL)
        }
    }



    private fun initClient(){

        val retrofitBuilder =   Retrofit.Builder()
            .baseUrl(Constants.URLS.BASE_URL)
            .addCallAdapterFactory(BoLiveDataAdapterFactory(listOf(400,200)))
            .client(getHttp())
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()))
            .build()

        client = retrofitBuilder.create(DataApiService::class.java)

    }

    private fun callAPI(url: String){


        GlobalScope.launch {
            withContext(Dispatchers.Main){

                client.getEmployeeData(url).observe(this@MainActivity, Observer { response->

                    when(response){
                        is BoGenericResponse.ApiSuccessResponse ->{
                            Log.i(TAG, "Returned Data -ApiSuccessResponse-:  $response")
                            tv_info.text = Constants.TOOLS.FormatStringToJson(response.body.toString())
                            Toast.makeText(this@MainActivity,"New Success Response Has Been Detected!",Toast.LENGTH_LONG).show()
                        }
                        is BoGenericResponse.ApiErrorResponse ->{
                            Log.i(TAG, " Returned Data -ApiErrorResponse-: $response")
                            tv_info.text = Constants.TOOLS.FormatStringToJson(response.errorBody.toString())
                            Toast.makeText(this@MainActivity,"New ${response.errorCode} Error Serialized Response Has Been Detected!",Toast.LENGTH_LONG).show()
                        }
                        is BoGenericResponse.ApiEmptyResponse ->{
                            Log.i(TAG, " Returned Data -ApiEmptyResponse-: $response")
                            Toast.makeText(this@MainActivity,"New Empty Response Has Been Detected!",Toast.LENGTH_LONG).show()
                        }
                        is BoGenericResponse.ApiUnhandledErrorResponse ->{
                            Log.i(TAG, " Returned Data -ApiUnhandledErrorResponse-: $response")
                            Toast.makeText(this@MainActivity,"New Unhandled Error Response Has Been Detected!",Toast.LENGTH_LONG).show()
                        }
                    }

                })
            }
        }
    }


    private fun getHttp() : OkHttpClient{

        val cookieManager = CookieManager()
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL)


        return OkHttpClient.Builder()
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .cookieJar(JavaNetCookieJar(cookieManager))
            .build()
    }
}
