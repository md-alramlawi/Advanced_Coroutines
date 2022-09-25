package com.ramalwi.flowapp.ui.plantsFragment

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import com.ramalwi.flowapp.R
import com.ramalwi.flowapp.databinding.FragmentPlantListBinding
import com.ramalwi.flowapp.ui.plantsFragment.DefaultViewModelProvider.providePlantListViewModelFactory
import com.ramalwi.plant.PlantAdapter

class PlantListFragment : Fragment() {

    private val viewModel: PlantsViewModel by viewModels {
        providePlantListViewModelFactory(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentPlantListBinding.inflate(inflater, container, false)
        context ?: return binding.root

        viewModel.spinner.observe(viewLifecycleOwner) { show ->
            binding.spinner.visibility = if (show) View.VISIBLE else View.GONE
        }

        viewModel.snackbar.observe(viewLifecycleOwner) { text ->
            text?.let {
                Snackbar.make(binding.root, text, Snackbar.LENGTH_SHORT).show()
                viewModel.onSnackbarShown()
            }
        }

        val adapter = PlantAdapter()
        binding.plantList.adapter = adapter
        subscribeUi(adapter)

        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_plant_list, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.filter_zone -> {
                updateData()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun subscribeUi(adapter: PlantAdapter) {
        viewModel.plantsUsingFlow.observe(viewLifecycleOwner) { plants ->
            adapter.submitList(plants)
        }
    }

    private fun updateData() {
        viewModel.apply {
            if(isFiltered()){
                clearGrowZoneNumber()
            }else{
                setGrowZoneNumber(9)
            }
        }
    }
}

