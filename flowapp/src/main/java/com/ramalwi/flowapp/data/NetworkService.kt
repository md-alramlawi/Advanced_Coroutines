package com.ramalwi.flowapp.data

import com.ramalwi.plant.models.GrowZone
import com.ramalwi.plant.models.Plant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import java.util.concurrent.TimeUnit

class NetworkService {

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://raw.githubusercontent.com/")
        .client(
            OkHttpClient()
            .newBuilder()
            .callTimeout(10, TimeUnit.SECONDS)
            .build()
        )
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val api = retrofit.create(ApiService::class.java)

    suspend fun allPlants(): List<Plant> = withContext(Dispatchers.Default) {
        val result = api.getAllPlants()
        result.shuffled()
    }

    suspend fun plantsByGrowZone(growZone: GrowZone) = withContext(Dispatchers.Default) {
        val result = api.getAllPlants()
        result.filter { it.equals(growZone)}.shuffled()
    }

    suspend fun customPlantSortOrder(): List<String> = withContext(Dispatchers.Default) {
        val result = api.getCustomPlantSortOrder()
        result.map { plant -> plant.plantId }
    }
}

interface ApiService {
    @GET("googlecodelabs/kotlin-coroutines/master/advanced-coroutines-codelab/sunflower/src/main/assets/plants.json")
    suspend fun getAllPlants(): List<Plant>

    @GET("googlecodelabs/kotlin-coroutines/master/advanced-coroutines-codelab/sunflower/src/main/assets/custom_plant_sort_order.json")
    suspend fun getCustomPlantSortOrder(): List<Plant>
}