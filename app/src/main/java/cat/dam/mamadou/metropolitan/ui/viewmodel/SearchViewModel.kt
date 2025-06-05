package cat.dam.mamadou.metropolitan.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cat.dam.mamadou.metropolitan.data.model.*
import cat.dam.mamadou.metropolitan.data.repository.MetropolitanRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: MetropolitanRepository
) : ViewModel() {

    private val _departmentsState = MutableStateFlow<NetworkResult<List<Department>>>(NetworkResult.Loading)
    val departmentsState: StateFlow<NetworkResult<List<Department>>> = _departmentsState.asStateFlow()

    private val _searchResultsState = MutableStateFlow<NetworkResult<List<ArtworkDetail>>>(NetworkResult.Loading)
    val searchResultsState: StateFlow<NetworkResult<List<ArtworkDetail>>> = _searchResultsState.asStateFlow()

    private val _searchFilters = MutableStateFlow(SearchFilters())
    val searchFilters: StateFlow<SearchFilters> = _searchFilters.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    init {
        loadDepartments()
    }

    private fun loadDepartments() {
        viewModelScope.launch {
            repository.getDepartments().collect { result ->
                _departmentsState.value = result
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchFilters.value = _searchFilters.value.copy(query = query)
    }

    fun updateSelectedDepartment(department: Department?) {
        _searchFilters.value = _searchFilters.value.copy(selectedDepartment = department)
    }

    fun updateHasImages(hasImages: Boolean) {
        _searchFilters.value = _searchFilters.value.copy(hasImages = hasImages)
    }

    fun performSearch() {
        _isSearching.value = true
        viewModelScope.launch {
            repository.searchArtworks(_searchFilters.value).collect { result ->
                _searchResultsState.value = result
                _isSearching.value = false
            }
        }
    }

    fun clearSearch() {
        _searchFilters.value = SearchFilters()
        _searchResultsState.value = NetworkResult.Loading
    }
}