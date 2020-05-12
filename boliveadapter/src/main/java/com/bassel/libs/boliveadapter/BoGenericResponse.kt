package com.bassel.libs.boliveadapter

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Response
import java.lang.reflect.Type


/**
 *
 * The idea of this sealed class has been used from Architecture components google sample:
 * This is just an improvement for it
 *
 */

sealed class BoGenericResponse<T,E> {



    class ApiEmptyResponse<T,E> : BoGenericResponse<T,E>()

    data class ApiSuccessResponse<T,E>(val body: T) : BoGenericResponse<T,E>()

    data class ApiUnhandledErrorResponse<T,E>(val errorMessage: String) : BoGenericResponse<T,E>()

    data class ApiErrorResponse<T,E>(val errorCode: Int,val errorBody: E) : BoGenericResponse<T,E>()



    companion object {


        fun <T,E> create(error: Throwable): ApiUnhandledErrorResponse<T,E> {

            return ApiUnhandledErrorResponse(
                    error.message ?: "unknown error"
            )
        }

        fun <T,E> create(response: Response<T>,errorType: Type, listSupportedErrorCodes: List<Int>): BoGenericResponse<T,E> {


            if(response.isSuccessful){
                val body = response.body()
                return if (body == null || response.code() == 204) {
                    ApiEmptyResponse()
                }
                else if(response.code() == 401){
                    ApiUnhandledErrorResponse("401 Unauthorized. Token may be invalid.")
                }
                else {
                    ApiSuccessResponse(body = body)
                }
            }
            else{
                val msg = response.errorBody()?.string()

                val errorMsg = if (msg.isNullOrEmpty()) {
                    response.message()
                } else {
                    msg
                }

                errorMsg?.let {bodyResponse ->

                    val unitType =  TypeToken.getParameterized(Unit::class.java).rawType

                    if (errorType != unitType && (listSupportedErrorCodes.isEmpty() || listSupportedErrorCodes.any{it ==response.code() })){
                        val errorBody: E = Gson().fromJson(bodyResponse, errorType)
                        return ApiErrorResponse(errorCode = response.code(),errorBody = errorBody)
                    }
                }


                return ApiUnhandledErrorResponse(
                        errorMsg ?: "unknown error"
                )
            }
        }

    }


}

