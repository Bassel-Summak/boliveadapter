package com.bassel.example.boliveadapter.ui

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bassel.example.boliveadapter.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import com.bassel.example.boliveadapter.util.DataState
import com.bassel.example.boliveadapter.util.SuccessHandling
import com.bassel.example.boliveadapter.viewmodel.EmployeeViewModel

class MainActivity : AppCompatActivity() {

    lateinit var viewModel: EmployeeViewModel

    lateinit var loadingToast : Toast

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel = ViewModelProvider(this).get(EmployeeViewModel::class.java)
        subscribeObservers()
        loadingToast  = Toast.makeText(this,"Loading!",Toast.LENGTH_LONG)

    }

    private fun subscribeObservers(){


        viewModel.dataState.observe(this, Observer { dataState ->
            onDataStateChange(dataState)
            dataState.data?.let { data ->

                data.response?.let{event ->
                    event.peekContent().let{ response ->
                        response.message?.let{ message ->
                            showToast(message)
                        }
                    }
                }
            }
        })
    }


    fun onDataStateChange(dataState: DataState<*>?) {
        dataState?.let{
            GlobalScope.launch(Dispatchers.Main){

                Log.i("Data state chaange ", " onDataStateChange $it.loading.isLoading")

                displayLoadingToast(it.loading.isLoading)
            }
        }
    }


    private fun showToast(message: String) {

        if (message == SuccessHandling.RESPONSE_DATA)
            Toast.makeText(this,"New Success Response Has Been Detected!",Toast.LENGTH_LONG).show()
        else if (message == SuccessHandling.RESPONSE_DATA)
            Toast.makeText(this,"New Error Serialized Response Has Been Detected!",Toast.LENGTH_LONG).show()
    }

    fun displayLoadingToast(bool: Boolean) {

        if (bool)
            loadingToast.show()
        else
            loadingToast.cancel()
    }
}
