package com.ramalwi.flowapp.ui.plantsFragment

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ramalwi.flowapp.data.NetworkService
import com.ramalwi.flowapp.data.PlantDatabase
import com.ramalwi.flowapp.data.PlantRepository

class PlantsViewModelFactory(
    private val repository: PlantRepository
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>) = PlantsViewModel(repository) as T
}

object DefaultViewModelProvider : ViewModelFactoryProvider {

    private fun plantService() = NetworkService()

    private fun plantDao(context: Context) =
        PlantDatabase.getInstance(context.applicationContext).plantDao()


    private fun getPlantRepository(context: Context): PlantRepository {
        return PlantRepository.getInstance(
            plantDao(context),
            plantService()
        )
    }

    override fun providePlantListViewModelFactory(context: Context): PlantsViewModelFactory {
        val repository = getPlantRepository(context)
        return PlantsViewModelFactory(repository)
    }
}

interface ViewModelFactoryProvider {
    fun providePlantListViewModelFactory(context: Context): PlantsViewModelFactory
}
