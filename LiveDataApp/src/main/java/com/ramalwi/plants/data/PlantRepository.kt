package com.ramalwi.plants.data

import androidx.annotation.AnyThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import com.ramalwi.plants.models.GrowZone
import com.ramalwi.plants.models.Plant
import com.ramalwi.plants.util.CacheOnSuccess
import com.ramalwi.plants.util.ComparablePair
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PlantRepository private constructor(
    private val plantDao: PlantDao,
    private val plantService: NetworkService,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) {
    val plants: LiveData<List<Plant>> = liveData<List<Plant>> {
        val plantsLiveData = plantDao.getPlants()
        val customSortOrder = plantsListSortOrderCache.getOrAwait()

        val sortedPlantsLiveData = plantsLiveData.map { plantList ->
            plantList.applySort(customSortOrder)
        }
        emitSource(sortedPlantsLiveData)
    }

    fun getPlantsWithGrowZone(growZone: GrowZone): LiveData<List<Plant>> {
        val plantsGrowZoneLiveData = plantDao.getPlantsWithGrowZoneNumber(growZone.number)
        return plantsGrowZoneLiveData.switchMap {
            liveData {
                val customSortOrder = plantsListSortOrderCache.getOrAwait()
                emit(it.applyMainSafeSort(customSortOrder))
            }
        }
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
            ComparablePair(positionForItem, plant.name)
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
