package com.ramalwi.flowapp.ui.plantsFragment

import androidx.lifecycle.*
import com.ramalwi.flowapp.data.PlantRepository
import com.ramalwi.plant.models.GrowZone
import com.ramalwi.plant.models.GrowZone.Companion.NoGrowZone
import com.ramalwi.plant.models.Plant
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*

@OptIn(ExperimentalCoroutinesApi::class)
class PlantsViewModel internal constructor(
    private val plantRepository: PlantRepository
) : ViewModel() {

    private val _snackbar = MutableLiveData<String?>()
    val snackbar: LiveData<String?>
        get() = _snackbar

    private val _spinner = MutableLiveData<Boolean>(false)
    val spinner: LiveData<Boolean>
        get() = _spinner

    private val growZoneFlow = MutableStateFlow<GrowZone>(NoGrowZone)

    /**[flatMapLatest] is switching between two data sources based on an event.*/
    val plantsUsingFlow: LiveData<List<Plant>> = growZoneFlow.flatMapLatest { growZone ->
        if (growZone == NoGrowZone) {
            plantRepository.plantsFlow
        } else {
            plantRepository.getPlantsWithGrowZoneFlow(growZone)
        }
    }.asLiveData()


    init {
        clearGrowZoneNumber()

        growZoneFlow.mapLatest { growZone ->
            _spinner.value = true
            if (growZone == NoGrowZone) {
                plantRepository.tryUpdateRecentPlantsCache()
            } else {
                plantRepository.tryUpdateRecentPlantsForGrowZoneCache(growZone)
            }
        }
            .onEach {  _spinner.value = false }
            .catch { throwable ->
                _snackbar.value = throwable.message
                _spinner.value = false
            }
            .launchIn(viewModelScope)
    }

    fun setGrowZoneNumber(num: Int) {
        growZoneFlow.value = GrowZone(num)
    }

    fun clearGrowZoneNumber() {
        growZoneFlow.value = NoGrowZone
    }

    fun isFiltered() = growZoneFlow.value != NoGrowZone

    fun onSnackbarShown() {
        _snackbar.value = null
    }
}
