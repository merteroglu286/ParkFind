package com.merteroglu286.parkfind.presentation.fragment.history

import android.view.LayoutInflater
import android.view.ViewGroup
import com.merteroglu286.parkfind.databinding.FragmentHistoryBinding
import com.merteroglu286.parkfind.domain.model.ParkModel
import com.merteroglu286.parkfind.presentation.adapter.ParkAdapter
import com.merteroglu286.parkfind.presentation.base.BaseFragment
import com.merteroglu286.parkfind.utility.MapUtils
import com.merteroglu286.parkfind.utility.extension.myScope
import com.merteroglu286.parkfind.utility.extension.vertical
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class HistoryFragment : BaseFragment<FragmentHistoryBinding, HistoryVM>() {

    private val adapter by lazy { ParkAdapter(::parkAdapterHandle) }
    private var lastAddedPark: ParkModel? = null

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        attachToParent: Boolean
    ): FragmentHistoryBinding {
        return FragmentHistoryBinding.inflate(inflater, container, attachToParent)
    }

    override fun initUI() {
        super.initUI()
        createAdapter()
    }

    private fun createAdapter() {
        binding.parkRecyclerView.vertical(adapter)
    }

    override fun setReceivers() {
        super.setReceivers()

        with(viewModel) {
            parks.onEach(::handleParks).launchIn(myScope)
        }
    }

    private fun handleParks(parks: List<ParkModel>?) {
        parks?.let {
            lastAddedPark = it.lastOrNull()
        }
        adapter.submitList(parks?.reversed())
    }

    private fun parkAdapterHandle(event: ParkAdapter.Event) {
        when (event) {
            is ParkAdapter.Event.OnClickGoButton -> {
                MapUtils.openMap(
                    requireContext(),
                    event.lat, event.lon
                )
            }

            is ParkAdapter.Event.OnClickDeleteButton -> {
                showConfirmPopup("Bu kaydı silmek istiyor musunuz?", {
                    if (event.item == lastAddedPark) {
                        setLastLocation(0.0,0.0)
                    }
                    viewModel.deletePark(event.item)
                }, {})
            }

            is ParkAdapter.Event.OnClickItem -> {

            }
        }
    }
}