package com.ramalwi.flowapp.data

import androidx.annotation.AnyThread
import com.ramalwi.plant.models.GrowZone
import com.ramalwi.plant.models.Plant
import com.ramalwi.plant.util.CacheOnSuccess
import com.ramalwi.plant.util.ComparablePair
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class PlantRepository private constructor(
    private val plantDao: PlantDao,
    private val plantService: NetworkService,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) {

    @OptIn(FlowPreview::class)
    val plantsFlow: Flow<List<Plant>>
        get() = combine<List<Plant>, List<String>, List<Plant>>(
            flow = plantDao.getPlantsFlow(),
            flow2 = plantsListSortOrderCache::getOrAwait
                .asFlow()
                .onStart {
                    emit(emptyList())
                    delay(1500)
                },
            transform = { plants, sortOrder ->
                plants.applySort(sortOrder)
            }
        ).flowOn(defaultDispatcher)

    fun getPlantsWithGrowZoneFlow0(growZone: GrowZone): Flow<List<Plant>> {
        return plantDao.getPlantsWithGrowZoneNumberFlow(growZone.number)
            .map { plantList ->
                val sortOrderFromNetwork = plantsListSortOrderCache.getOrAwait()
                val nextValue = plantList.applyMainSafeSort(sortOrderFromNetwork)
                nextValue
            }
    }


    fun getPlantsWithGrowZoneFlow1(growZone: GrowZone): Flow<List<Plant>> {
        return plantDao.getPlantsWithGrowZoneNumberFlow(growZone.number)
            .map { plantList ->
                val sortOrderFromNetwork = plantsListSortOrderCache.getOrAwait()
                val nextValue = plantList.applyMainSafeSort(sortOrderFromNetwork)
                nextValue
            }
    }

    fun getPlantsWithGrowZoneFlow(growZone: GrowZone): Flow<List<Plant>> {
        return combine(
            flow = plantDao.getPlantsWithGrowZoneNumberFlow(growZone.number),
            flow2 = plantsListSortOrderCache::getOrAwait.asFlow(),
            transform = { plants, sortOrder ->
                plants.applySort(sortOrder)
            }
        ).flowOn(defaultDispatcher)
    }

    private var plantsListSortOrderCache =
        CacheOnSuccess(
            onErrorFallback = { listOf() },
            block = {
                plantService.customPlantSortOrder()
            }
        )

    private fun shouldUpdatePlantsCache(): Boolean {
        return true
    }

    suspend fun tryUpdateRecentPlantsCache() {
        if (shouldUpdatePlantsCache()) fetchRecentPlants()
    }

    suspend fun tryUpdateRecentPlantsForGrowZoneCache(growZone: GrowZone) {
        if (shouldUpdatePlantsCache()) fetchPlantsForGrowZone(growZone)
    }

    private suspend fun fetchRecentPlants() {
        val plants = plantService.allPlants()
        plantDao.insertAll(plants)
    }

    private suspend fun fetchPlantsForGrowZone(growZone: GrowZone) {
        val plants = plantService.plantsByGrowZone(growZone)
        plantDao.insertAll(plants)
    }

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
