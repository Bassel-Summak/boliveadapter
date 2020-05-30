package com.bassel.example.boliveadapter.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.bassel.example.boliveadapter.Constants
import com.bassel.example.boliveadapter.R
import com.bassel.example.boliveadapter.api.DataApiService
import com.bassel.example.boliveadapter.viewmodel.EmployeeViewModel
import kotlinx.android.synthetic.main.fragment_employee.*
import androidx.lifecycle.ViewModelProvider
import com.bassel.example.boliveadapter.util.SuccessHandling
import com.bassel.example.boliveadapter.viewmodel.state.EmployeeStateEvent

class EmployeeFragment : Fragment(){

    private val TAG: String = "AppDebug"
    lateinit var viewModel: EmployeeViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        viewModel = activity?.run {
            ViewModelProvider(this).get(EmployeeViewModel::class.java)
        } ?: throw Exception("Invalid Activity")

        val view = inflater.inflate(R.layout.fragment_employee, container, false)

        subscribeObservers()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initClicks()
    }

    private fun initClicks(){

        btn_data.setOnClickListener{
            tv_info.text = ""
            callAPI(Constants.URLS.DATA_URL)
        }

        btn_error.setOnClickListener{
            tv_info.text = ""
            callAPI(Constants.URLS.ERROR_URL)
        }
    }


    private fun callAPI(url: String){


        viewModel.setStateEvent(EmployeeStateEvent.FetchEmployeeData(url))
    }


    fun subscribeObservers() {

        viewModel.dataState.observe(viewLifecycleOwner, Observer { data ->

            data?.let {dataState ->

                dataState.data?.let {
                    it.data?.let { event ->
                        event.getContentIfNotHandled()?.let {viewState ->

                            viewState.jsonResponse?.let { response ->
                                Log.i(TAG, " dataState $response")

                                viewModel.addResponse(response)
                            }
                        }
                    }
                }
            }
        })





        viewModel.viewState.observe(viewLifecycleOwner, Observer {viewState ->
            Log.i(TAG, "view State $viewState ")
            viewState?.let {viewState ->
                viewState.jsonResponse?.let {
                    updateUI(it)
                }
            }
        })


    }

    private fun updateUI(response :String){

        tv_info.text = Constants.TOOLS.FormatStringToJson(response)
    }





}