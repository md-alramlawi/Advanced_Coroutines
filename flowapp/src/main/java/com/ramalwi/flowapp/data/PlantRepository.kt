package com.ramalwi.flowapp.data

import androidx.annotation.AnyThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import com.ramalwi.plant.util.CacheOnSuccess
import com.ramalwi.plant.models.GrowZone
import com.ramalwi.plant.models.Plant
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class PlantRepository private constructor(
    private val plantDao: PlantDao,
    private val plantService: NetworkService,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) {
    val plantsFlow: Flow<List<Plant>>
        get() = plantDao.getPlantsFlow()

    fun getPlantsWithGrowZoneFlow(growZoneNumber: GrowZone): Flow<List<Plant>> {
        return plantDao.getPlantsWithGrowZoneNumberFlow(growZoneNumber.number)
    }

    private fun shouldUpdatePlantsCache(): Boolean {
        return true
    }

    suspend fun tryUpdateRecentPlantsCache() {
        if (shouldUpdatePlantsCache()) fetchRecentPlants()
    }

    private suspend fun fetchRecentPlants() {
        val plants = plantService.allPlants()
        plantDao.insertAll(plants)
    }

    private suspend fun fetchPlantsForGrowZone(growZone: GrowZone) {
        val plants = plantService.plantsByGrowZone(growZone)
        plantDao.insertAll(plants)
    }

    private var plantsListSortOrderCache =
        CacheOnSuccess(
            onErrorFallback = { listOf() },
            block = {
                plantService.customPlantSortOrder()
            }
        )

    private fun List<Plant>.applySort(customSortOrder: List<String>): List<Plant> {

        return this.sortedBy { plant ->
            val positionForItem = customSortOrder.indexOf(plant.plantId).let { order ->
                if (order > -1) order
                else Int.MAX_VALUE
            }
            com.ramalwi.plant.util.ComparablePair(positionForItem, plant.name)
        }
    }

    @AnyThread
    suspend fun List<Plant>.applyMainSafeSort(customSortOrder: List<String>) =
        withContext(defaultDispatcher) {
            this@applyMainSafeSort.applySort(customSortOrder)
        }

    companion object {

        @Volatile
        private var instance: PlantRepository? = null

        fun getInstance(plantDao: PlantDao, plantService: NetworkService) =
            instance ?: synchronized(this) {
                instance ?: PlantRepository(plantDao, plantService).also { instance = it }
            }
    }
}
