package com.ramalwi.flowapp.ui.plantsFragment

import androidx.lifecycle.*
import com.ramalwi.flowapp.data.PlantRepository
import com.ramalwi.plant.models.GrowZone
import com.ramalwi.plant.models.GrowZone.Companion.NoGrowZone
import com.ramalwi.plant.models.Plant
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

    val plantsUsingFlow: LiveData<List<Plant>> = plantRepository.plantsFlow.asLiveData()

    init {
        clearGrowZoneNumber()

        launchDataLoad { plantRepository.tryUpdateRecentPlantsCache() }
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
