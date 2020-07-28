package personal.ivan.kotlin_flow_practice.viewmodel

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import personal.ivan.kotlin_flow_practice.io.util.IoStatus
import personal.ivan.kotlin_flow_practice.repository.GithubRepository

@ExperimentalCoroutinesApi
class MainViewModel
@ViewModelInject
constructor(private val repository: GithubRepository) : ViewModel() {

    companion object {
        private val TAG = MainViewModel::class.java.simpleName
    }

    fun getUserList(): LiveData<IoStatus<List<String>>> = repository.getUserList()

    fun getUserDetails(): LiveData<IoStatus<String>> = repository.getUserDetails()

    fun aaa() {
        viewModelScope.launch {
            repository.aaa().collect {
                when (it) {
                    is IoStatus.Loading -> Log.d(TAG, "loading")
                    is IoStatus.Failed -> Log.d(TAG, "failed")
                    is IoStatus.Succeed -> Log.d(TAG, "succeed")
                }
            }
        }
    }
}