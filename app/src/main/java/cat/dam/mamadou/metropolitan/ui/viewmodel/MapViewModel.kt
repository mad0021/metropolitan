package cat.dam.mamadou.metropolitan.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cat.dam.mamadou.metropolitan.data.model.ArtworkDetail
import cat.dam.mamadou.metropolitan.data.model.EuropeanCapital
import cat.dam.mamadou.metropolitan.data.model.NetworkResult
import cat.dam.mamadou.metropolitan.data.repository.MetropolitanRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// Temporalmente eliminamos la anotación @HiltViewModel y añadimos un constructor sin parámetros
class MapViewModel : ViewModel() {
    // Referencia temporal al repositorio sin inyección
    private val repository: MetropolitanRepository by lazy { 
        MetropolitanRepository() // Asumiendo que tiene un constructor sin parámetros
    }
    
    private val _selectedCapital = MutableStateFlow<EuropeanCapital?>(null)
    val selectedCapital: StateFlow<EuropeanCapital?> = _selectedCapital.asStateFlow()

    private val _artworksState = MutableStateFlow<NetworkResult<List<ArtworkDetail>>>(NetworkResult.Loading)
    val artworksState: StateFlow<NetworkResult<List<ArtworkDetail>>> = _artworksState.asStateFlow()

    private val _isBottomSheetVisible = MutableStateFlow(false)
    val isBottomSheetVisible: StateFlow<Boolean> = _isBottomSheetVisible.asStateFlow()

    fun onCapitalSelected(capital: EuropeanCapital) {
        _selectedCapital.value = capital
        _isBottomSheetVisible.value = true
        loadArtworksForCountry(capital.country)
    }

    fun hideBottomSheet() {
        _isBottomSheetVisible.value = false
    }

    private fun loadArtworksForCountry(country: String) {
        viewModelScope.launch {
            repository.searchArtworksByCountry(country).collect { result ->
                _artworksState.value = result
            }
        }
    }
}