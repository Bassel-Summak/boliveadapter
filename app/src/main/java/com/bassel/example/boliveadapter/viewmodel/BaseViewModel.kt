package com.bassel.example.boliveadapter.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.bassel.example.boliveadapter.util.DataState


abstract class BaseViewModel<StateEvent, ViewState> : ViewModel()
{


    val TAG: String = "AppDebug"

    private val _stateEvent: MutableLiveData<StateEvent> = MutableLiveData()
    private val _viewState: MutableLiveData<ViewState> = MutableLiveData()

    val viewState: LiveData<ViewState>
        get() = _viewState

    val dataState: LiveData<DataState<ViewState>> = Transformations
        .switchMap(_stateEvent){stateEvent ->
            stateEvent?.let {
                handleStateEvent(stateEvent)
            }
        }

    fun setStateEvent(event: StateEvent){
        Log.i("UPDATE STATE EVENT"," setStateEvent: $event")
        _stateEvent.value = event
    }

     fun getCurrentViewStateOrNew(): ViewState{
        val value = viewState.value?.let{
            it
        }?: initNewViewState()
        return value
    }

    fun setViewState(viewState: ViewState){
        _viewState.value = viewState
    }

    abstract fun handleStateEvent(stateEvent: StateEvent): LiveData<DataState<ViewState>>

    abstract fun initNewViewState(): ViewState

}