package com.bassel.libs.boliveadapter

import android.util.Log
import androidx.lifecycle.LiveData
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Callback
import retrofit2.Response
import java.lang.reflect.Type
import java.util.concurrent.atomic.AtomicBoolean

internal class BoLiveDataAdapter<R,E : Any>(private val responseType: Type, private val errorType: Type, val listSupportedErrorCodes: List<Int>) :
    CallAdapter<R, LiveData<BoGenericResponse<R,E>>> {

    override fun responseType() = responseType

    override fun adapt(call: Call<R>): LiveData<BoGenericResponse<R,E>> {
        return object : LiveData<BoGenericResponse<R,E>>() {
            private var started = AtomicBoolean(false)
            override fun onActive() {
                super.onActive()
                if (started.compareAndSet(false, true)) {
                    call.enqueue(object : Callback<R> {
                        override fun onResponse(call: Call<R>, response: Response<R>) {
                            Log.i("LiveDataCallAdapter "," response")

                            postValue(BoGenericResponse.create(response,errorType,listSupportedErrorCodes))
                        }

                        override fun onFailure(call: Call<R>, throwable: Throwable) {
                            Log.i("LiveDataCallAdapter "," Throwable")
                            postValue(BoGenericResponse.create(throwable))
                        }
                    })
                }
            }
        }
    }
}