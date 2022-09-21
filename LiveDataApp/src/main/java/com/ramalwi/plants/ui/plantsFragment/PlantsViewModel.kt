package com.ramalwi.plants.ui.plantsFragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.ramalwi.plants.data.PlantRepository
import com.ramalwi.plants.models.GrowZone
import com.ramalwi.plants.models.GrowZone.Companion.NoGrowZone
import com.ramalwi.plants.models.Plant
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class PlantsViewModel internal constructor(
    private val plantRepository: PlantRepository
) : ViewModel() {

    private val _snackbar = MutableLiveData<String?>()


    val snackbar: LiveData<String?>
        get() = _snackbar

    private val _spinner = MutableLiveData<Boolean>(false)


    val spinner: LiveData<Boolean>
        get() = _spinner


    private val growZone = MutableLiveData<GrowZone>(NoGrowZone)


    val plants: LiveData<List<Plant>> = growZone.switchMap {
        if (it == NoGrowZone) {
            plantRepository.plants
        } else {
            plantRepository.getPlantsWithGrowZone(it)
        }
    }

    init {
        clearGrowZoneNumber()
    }

    fun setGrowZoneNumber(num: Int) {
        growZone.value = GrowZone(num)
        launchDataLoad { plantRepository.tryUpdateRecentPlantsCache() }
    }

    fun clearGrowZoneNumber() {
        growZone.value = NoGrowZone
        launchDataLoad { plantRepository.tryUpdateRecentPlantsCache() }
    }

    fun isFiltered() = growZone.value != NoGrowZone

    fun onSnackbarShown() {
        _snackbar.value = null
    }

    private fun launchDataLoad(block: suspend () -> Unit): Job {
        return viewModelScope.launch {
            try {
                _spinner.value = true
                block()
            } catch (error: Throwable) {
                _snackbar.value = error.message
            } finally {
                _spinner.value = false
            }
        }
    }
}
