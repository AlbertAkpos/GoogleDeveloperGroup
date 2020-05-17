package me.alberto.googledevelopergroup.search

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.alberto.googledevelopergroup.network.GdgApi
import me.alberto.googledevelopergroup.network.GdgChapter
import java.io.IOException

class GdgListViewModel : ViewModel() {

    private val repository = GdgChapterRepository(GdgApi.retrofitService)

    private var filter = FilterHolder()
    private var currentJob: Job? = null

    private val _gdgList = MutableLiveData<List<GdgChapter>>()
    private val _regionList = MutableLiveData<List<String>>()
    private val _showNeedLocation = MutableLiveData<Boolean>()

    val gdgList: LiveData<List<GdgChapter>>
        get() = _gdgList

    val regionList: LiveData<List<String>>
        get() = _regionList

    val showNeedLocation: LiveData<Boolean>
        get() = _showNeedLocation


    init {
        onQueryChanged()
        viewModelScope.launch {
            delay(5_000)
            _showNeedLocation.value = !repository.isFullyInitialized
        }
    }

    private fun onQueryChanged() {
        currentJob?.cancel() // cancel any running previous query
        currentJob = viewModelScope.launch {
            try {
                _gdgList.value = repository.getChaptersForFilter(filter.currentValue)
                repository.getFilters().let {
                    if (it != _regionList.value){
                        _regionList.value = it
                    }
                }
            } catch (e: IOException){
                _gdgList.value = listOf()
            }
        }
    }

    fun onLocationUpdated(location: Location){
        viewModelScope.launch {
            repository.onLocationChanged(location)
            onQueryChanged()
        }
    }


    fun onFilterChanged(filter: String, isChecked: Boolean){
        if (this.filter.update(filter, isChecked)){
            onQueryChanged()
        }
    }


    private class FilterHolder {
        var currentValue: String ? = null
            private set

        fun update(changedFilter: String, isChecked: Boolean): Boolean {
            if (isChecked){
                currentValue = changedFilter
            } else if (currentValue == changedFilter) {
                currentValue = null
                return true
            }
            return false
        }
    }
}