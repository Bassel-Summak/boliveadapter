package com.bassel.example.boliveadapter.repository

import android.util.Log
import com.bassel.example.boliveadapter.Constants
import com.bassel.libs.boliveadapter.BoLiveDataAdapterFactory
import com.google.gson.GsonBuilder
import kotlinx.coroutines.Job
import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.CookieManager
import java.net.CookiePolicy
import java.util.concurrent.TimeUnit

open class JobManager(
    private val className: String
) {

    private val TAG: String = "AppDebug"

    private val jobs: HashMap<String, Job> = HashMap()

    var retrofitBuilder  : Retrofit

    init {
        retrofitBuilder = initRetrofit()
    }
    fun addJob(methodName: String, job: Job){
        cancelJob(methodName)
        jobs[methodName] = job
    }

    fun cancelJob(methodName: String){
        getJob(methodName)?.cancel()
    }

    fun getJob(methodName: String): Job? {
        if(jobs.containsKey(methodName)){
            jobs[methodName]?.let {
                return it
            }
        }
        return null
    }

    fun cancelActiveJobs(){
        for((methodName, job) in jobs){
            if(job.isActive){
                Log.e(TAG, "$className: cancelling job in method: '$methodName'")
                job.cancel()
            }
        }
    }

    private fun initRetrofit() : Retrofit{

        return Retrofit.Builder()
            .baseUrl(Constants.URLS.BASE_URL)
            .addCallAdapterFactory(BoLiveDataAdapterFactory(listOf(400,417))) // error codes to handle
            .client(getHttp())
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()))
            .build()
    }


    private fun getHttp() : OkHttpClient {

        val cookieManager = CookieManager()
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL)


        return OkHttpClient.Builder()
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .cookieJar(JavaNetCookieJar(cookieManager))
            .build()
    }


}
