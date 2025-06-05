package cat.dam.mamadou.metropolitan.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cat.dam.mamadou.metropolitan.data.model.ArtworkDetail
import cat.dam.mamadou.metropolitan.data.model.NetworkResult
import cat.dam.mamadou.metropolitan.data.repository.MetropolitanRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArtworkDetailViewModel @Inject constructor(
    private val repository: MetropolitanRepository
) : ViewModel() {

    private val _artworkState = MutableStateFlow<NetworkResult<ArtworkDetail>>(NetworkResult.Loading)
    val artworkState: StateFlow<NetworkResult<ArtworkDetail>> = _artworkState.asStateFlow()

    fun loadArtworkDetail(objectId: Int) {
        viewModelScope.launch {
            repository.getArtworkDetail(objectId).collect { result ->
                _artworkState.value = result
            }
        }
    }
}