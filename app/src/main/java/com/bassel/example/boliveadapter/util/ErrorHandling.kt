package com.bassel.example.boliveadapter.util

class ErrorHandling{

    companion object{

        const val UNABLE_TO_RESOLVE_HOST = "Unable to resolve host"
        val ERROR_CHECK_NETWORK_CONNECTION = "Make sure you have stable internet connection then try again"
        const val ERROR_UNKNOWN = "Unknown error"


        fun isNetworkError(msg: String): Boolean{
            when{
                msg.contains(UNABLE_TO_RESOLVE_HOST) -> return true
                else-> return false
            }
        }

    }

}