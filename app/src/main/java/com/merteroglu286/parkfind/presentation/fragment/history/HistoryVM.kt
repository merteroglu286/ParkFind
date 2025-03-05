package com.merteroglu286.parkfind.presentation.fragment.history

import androidx.lifecycle.viewModelScope
import com.merteroglu286.parkfind.domain.model.ParkModel
import com.merteroglu286.parkfind.domain.usecase.ParkUseCase
import com.merteroglu286.parkfind.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryVM @Inject constructor(
    private val parkUseCase: ParkUseCase
): BaseViewModel() {

    private val _parks = MutableStateFlow<List<ParkModel>?>(emptyList())
    val parks = _parks.asStateFlow()

    init {
        getParks()
    }

    private fun getParks() {
        viewModelScope.launch {
            parkUseCase.getAllParks().collect { parkList ->
                _parks.emit(parkList)
            }
        }
    }

    fun deletePark(park: ParkModel) {
        viewModelScope.launch {
            parkUseCase.deletePark(park)
        }
    }
}