package com.bassel.example.boliveadapter.api.responses

import com.google.gson.annotations.SerializedName

import com.google.gson.annotations.Expose

data class Error(
    @Expose
    @SerializedName("message")
    val message: String = "",
    @Expose
    @SerializedName("status")
    val status: String = ""
) {
    override fun toString(): String {
        return "{message:$message," +
                "status='$status }"
    }
}