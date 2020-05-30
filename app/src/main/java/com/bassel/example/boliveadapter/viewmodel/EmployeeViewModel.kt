package com.bassel.example.boliveadapter.viewmodel
import androidx.lifecycle.*
import com.bassel.example.boliveadapter.util.DataState
import com.bassel.example.boliveadapter.repository.EmployeeRepository
import com.bassel.example.boliveadapter.util.AbsentLiveData
import com.bassel.example.boliveadapter.viewmodel.state.EmployeeStateEvent
import com.bassel.example.boliveadapter.viewmodel.state.EmployeeViewState

class EmployeeViewModel : BaseViewModel<EmployeeStateEvent, EmployeeViewState>()
{

     val employeeRepository: EmployeeRepository = EmployeeRepository()


    override fun handleStateEvent(stateEvent: EmployeeStateEvent): LiveData<DataState<EmployeeViewState>> {

        return when(stateEvent){

            is EmployeeStateEvent.FetchEmployeeData ->{
                employeeRepository.getEmployeeData(stateEvent.url)
            }

            is EmployeeStateEvent.None ->{
                AbsentLiveData.create()
            }
        }
    }

    fun addResponse(response : String){
        val update = getCurrentViewStateOrNew()
        update.jsonResponse = response
        setViewState(update)
    }


    override fun initNewViewState(): EmployeeViewState {
        return EmployeeViewState()
    }


    fun cancelActiveJobs(){
        handlePendingData()
        employeeRepository.cancelActiveJobs()
    }

    fun handlePendingData(){
        setStateEvent(EmployeeStateEvent.None)
    }

    override fun onCleared() {
        super.onCleared()
        cancelActiveJobs()
    }
}