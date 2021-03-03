package personal.ivan.kotlin_flow_practice.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import personal.ivan.kotlin_flow_practice.io.util.IoStatus
import personal.ivan.kotlin_flow_practice.repository.GithubRepository
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val repository: GithubRepository) : ViewModel() {

    fun getUserList(): LiveData<IoStatus<List<String>>> = repository.getUserList()

    fun getUserDetails(): LiveData<IoStatus<String>> = repository.getUserDetails()
}