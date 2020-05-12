package com.bassel.example.boliveadapter.api.responses

import com.google.gson.annotations.SerializedName

import com.google.gson.annotations.Expose


data class EmployeeData(

    @Expose
    @SerializedName("data")
    val `data`: Data = Data(),
    @Expose
    @SerializedName("status")
    val status: String = ""



)
{

    override fun toString(): String {
        return "{data:{$data," +
                "status='$status }"
    }


    data class Data(

        @Expose
        @SerializedName("employee_age")
        val employeeAge: String = "",
        @Expose
        @SerializedName("employee_name")
        val employeeName: String = "",
        @Expose
        @SerializedName("employee_salary")
        val employeeSalary: String = "",
        @Expose
        @SerializedName("id")
        val id: String = "",
        @Expose
        @SerializedName("profile_image")
        val profileImage: String = ""
    ) {
        override fun toString(): String {
            return "employeeAge:$employeeAge," +
                    "employeeName:$employeeName," +
                    "employeeSalary:$employeeSalary," +
                    "id:$id," +
                    "profileImage:$profileImage }"
        }
    }
}










