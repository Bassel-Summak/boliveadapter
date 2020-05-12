package com.bassel.example.boliveadapter.api

import androidx.lifecycle.LiveData
import com.bassel.example.boliveadapter.api.responses.EmployeeData
import com.bassel.example.boliveadapter.api.responses.Error
import com.bassel.libs.boliveadapter.BoGenericResponse
import retrofit2.http.*


interface DataApiService {


    @GET
    fun getEmployeeData(
        @Url url : String
    ): LiveData<BoGenericResponse<EmployeeData, Error>>

}
