package com.ygaberman.babypoo.db

import androidx.lifecycle.*
import kotlinx.coroutines.launch

class ActivityViewModel(private val repository: ActivityRepository) : ViewModel() {
    val allActivities: LiveData<List<Activity>> = repository.activities.asLiveData()

    fun insert(activity: Activity) = viewModelScope.launch {
        repository.insert(activity)
    }
}

class ActivityViewModelFactory(private val repository: ActivityRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ActivityViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ActivityViewModel(repository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}