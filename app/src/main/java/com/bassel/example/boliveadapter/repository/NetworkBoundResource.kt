package com.bassel.example.boliveadapter.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.bassel.example.boliveadapter.Constants.URLS.Companion.NETWORK_TIMEOUT
import com.bassel.libs.boliveadapter.BoGenericResponse
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import com.bassel.example.boliveadapter.util.ErrorHandling.Companion.ERROR_CHECK_NETWORK_CONNECTION
import com.bassel.example.boliveadapter.util.ErrorHandling.Companion.ERROR_UNKNOWN
import com.bassel.example.boliveadapter.util.ErrorHandling.Companion.UNABLE_TO_RESOLVE_HOST
import com.bassel.example.boliveadapter.util.DataState
import com.bassel.example.boliveadapter.util.ErrorHandling
import com.bassel.example.boliveadapter.util.Response
import com.bassel.example.boliveadapter.util.ResponseType

abstract class NetworkBoundResource<ResponseObject,ErrorObject, ViewStateType>
    (
    requiresLoading: Boolean = true
) {

    private val TAG: String = "AppDebug"

    protected val result = MediatorLiveData<DataState<ViewStateType>>()
    protected lateinit var job: CompletableJob
    protected lateinit var coroutineScope: CoroutineScope

    init {
        setJob(initNewJob())
        setValue(DataState.loading(isLoading = requiresLoading, cachedData = null))
        doNetworkRequest()
    }

    fun doNetworkRequest(){

        Log.i(TAG," doNetworkRequest ")

        coroutineScope.launch {

            withContext(Main){

                // make network call
                val apiResponse = createCall()
                result.addSource(apiResponse){ response ->
                    result.removeSource(apiResponse)

                    coroutineScope.launch {
                        handleNetworkCall(response)
                    }
                }
            }
        }

        GlobalScope.launch(IO){
            delay(NETWORK_TIMEOUT)

            if(!job.isCompleted){
                Log.e(TAG, "NetworkBoundResource: UNABLE_TO_RESOLVE_HOST." )
                job.cancel(CancellationException(UNABLE_TO_RESOLVE_HOST))
            }
        }
    }

    suspend fun handleNetworkCall(response: BoGenericResponse<ResponseObject,ErrorObject>){

        when(response){
            is BoGenericResponse.ApiSuccessResponse ->{
                val responseBody = response.body
                Log.i(TAG, " Returned Data -ApiErrorResponse-: $responseBody")
                handleApiSuccessResponse(response)
            }
            is BoGenericResponse.ApiErrorResponse ->{
                val errorBody  = response.errorBody
                val errorCode  = response.errorCode
                Log.i(TAG, " Returned Data -ApiErrorResponse-: $errorBody")
                Log.i(TAG, " Returned Data -ApiErrorResponse-: $errorCode")
                handleApiErrorResponse(response)
            }
            is BoGenericResponse.ApiEmptyResponse ->{
                Log.i(TAG, " Returned Data -ApiEmptyResponse-: $response")
                onErrorReturn("HTTP 204. Returned NOTHING.",false ,true, false)
            }
            is BoGenericResponse.ApiUnhandledErrorResponse ->{
                val errorMessage : String = response.errorMessage
                Log.i(TAG, " Returned Data -ApiUnhandledErrorResponse-: $errorMessage")
                onErrorReturn(response.errorMessage, false,true, false)
            }
        }
    }

    fun onCompleteJob(dataState: DataState<ViewStateType>){
        GlobalScope.launch(Main) {
            job.complete()
            setValue(dataState)
        }
    }

    fun onErrorReturn(errorMessage: String?,shouldUseDropdown: Boolean = true,shouldUseDialog: Boolean = false, shouldUseToast: Boolean = false){
        var msg = errorMessage
        var useDialog = shouldUseDialog
        var responseType: ResponseType = ResponseType.None()
        if(msg == null){
            msg = ERROR_UNKNOWN
        }
        else if(ErrorHandling.isNetworkError(msg)){
            msg = ERROR_CHECK_NETWORK_CONNECTION
            useDialog = false
        }

        if(shouldUseDropdown){
            responseType = ResponseType.DropDownWarning()
        }
        if(shouldUseToast){
            responseType = ResponseType.Toast()
        }
        if(useDialog){
            responseType = ResponseType.Dialog()
        }

        onCompleteJob(DataState.error(
            Response(
                msg,
                responseType
            )
        ))
    }

    fun setValue(dataState: DataState<ViewStateType>){
        result.value = dataState
    }

    @UseExperimental(InternalCoroutinesApi::class)
    private fun initNewJob(): Job{
        Log.d(TAG, "initNewJob: called.")
        job = Job() // create new job
        job.invokeOnCompletion(onCancelling = true, invokeImmediately = true, handler = object: CompletionHandler{
            override fun invoke(cause: Throwable?) {
                if(job.isCancelled){
                    Log.e(TAG, "NetworkBoundResource: Job has been cancelled.")
                    cause?.let{
                        onErrorReturn(it.message, false,false, true)
                    }?: onErrorReturn("Unknown error.", false,false, true)
                }
                else if(job.isCompleted){
                    Log.e(TAG, "NetworkBoundResource: Job has been completed.")
                    // Do nothing? Should be handled already
                }
            }
        })
        coroutineScope = CoroutineScope(IO + job)
        return job
    }

    fun asLiveData() = result as LiveData<DataState<ViewStateType>>


    abstract suspend fun handleApiSuccessResponse(response: BoGenericResponse.ApiSuccessResponse<ResponseObject,ErrorObject>)

    abstract suspend fun handleApiErrorResponse(response: BoGenericResponse.ApiErrorResponse<ResponseObject,ErrorObject>)

    abstract fun createCall(): LiveData<BoGenericResponse<ResponseObject,ErrorObject>>

    abstract fun loadFromCache(): LiveData<ViewStateType>

    abstract fun setJob(job: Job)

}















