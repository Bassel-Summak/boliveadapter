package com.bassel.example.boliveadapter.viewmodel.state


sealed class EmployeeStateEvent {

    data class FetchEmployeeData(val url: String): EmployeeStateEvent()

    object None : EmployeeStateEvent()
}