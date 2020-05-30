package com.bassel.example.boliveadapter.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.bassel.example.boliveadapter.api.DataApiService
import com.bassel.example.boliveadapter.api.responses.EmployeeData
import com.bassel.example.boliveadapter.util.AbsentLiveData
import com.bassel.example.boliveadapter.viewmodel.state.EmployeeViewState
import com.bassel.libs.boliveadapter.BoGenericResponse
import kotlinx.coroutines.Job
import com.bassel.example.boliveadapter.api.responses.Error
import com.bassel.example.boliveadapter.util.DataState
import com.bassel.example.boliveadapter.util.Response
import com.bassel.example.boliveadapter.util.ResponseType
import com.bassel.example.boliveadapter.util.SuccessHandling.Companion.RESPONSE_DATA
import com.bassel.example.boliveadapter.util.SuccessHandling.Companion.RESPONSE_ERROR

class EmployeeRepository : JobManager("ERepository"){

    private val TAG: String = "AppDebug"

    fun getEmployeeData(
            url: String
    ): LiveData<DataState<EmployeeViewState>> {

        return object: NetworkBoundResource<EmployeeData,Error,EmployeeViewState>(
                true
        ){

            override suspend fun handleApiSuccessResponse(response: BoGenericResponse.ApiSuccessResponse<EmployeeData,Error>) {

                Log.i(TAG,"response R: " + response.body.data)

                onCompleteJob(
                    DataState.data(
                        data = EmployeeViewState(
                            jsonResponse = response.body.data.toString()
                        ),
                        response = Response(
                            RESPONSE_DATA,
                            ResponseType.None()
                        )
                    )
                )


            }


            override suspend fun handleApiErrorResponse(response: BoGenericResponse.ApiErrorResponse<EmployeeData,Error>) {

                Log.i(TAG,"response R: "  + response.errorBody)

                onCompleteJob(
                    DataState.data(
                        data = EmployeeViewState(
                            jsonResponse = response.errorBody.toString()
                        ),
                        response = Response(
                            RESPONSE_ERROR,
                            ResponseType.None()
                        )
                    )
                )


            }

            override fun createCall(): LiveData<BoGenericResponse<EmployeeData,Error>> {
                Log.i(TAG," creating call ")
               val client = retrofitBuilder.create(DataApiService::class.java)
                return client.getEmployeeData(
                        url = url)
            }

            // not applicable
            override fun loadFromCache(): LiveData<EmployeeViewState> {
                return AbsentLiveData.create()
            }

            override fun setJob(job: Job) {
                addJob("getEmployeeData",job)
            }

        }.asLiveData()

    }



}