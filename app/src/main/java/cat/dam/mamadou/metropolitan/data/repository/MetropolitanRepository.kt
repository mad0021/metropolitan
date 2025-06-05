package cat.dam.mamadou.metropolitan.data.repository

import cat.dam.mamadou.metropolitan.data.api.ApiClient
import cat.dam.mamadou.metropolitan.data.model.ArtworkDetail
import cat.dam.mamadou.metropolitan.data.model.Department
import cat.dam.mamadou.metropolitan.data.model.NetworkResult
import cat.dam.mamadou.metropolitan.data.model.SearchFilters
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MetropolitanRepository @Inject constructor() {

    private val apiService = ApiClient.apiService

    suspend fun getDepartments(): Flow<NetworkResult<List<Department>>> = flow {
        emit(NetworkResult.Loading)
        try {
            val response = apiService.getDepartments()
            if (response.isSuccessful) {
                response.body()?.let { departmentsResponse ->
                    emit(NetworkResult.Success(departmentsResponse.departments))
                } ?: emit(NetworkResult.Error("No departments found"))
            } else {
                emit(NetworkResult.Error("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(NetworkResult.Error("Network error: ${e.localizedMessage}"))
        }
    }

    suspend fun searchArtworksByCountry(country: String): Flow<NetworkResult<List<ArtworkDetail>>> = flow {
        emit(NetworkResult.Loading)
        try {
            val searchResponse = apiService.searchArtworks(
                query = "",
                geoLocation = country,
                departmentId = 11, // European Paintings department
                hasImages = true
            )

            if (searchResponse.isSuccessful) {
                val objectIds = searchResponse.body()?.objectIDs ?: emptyList()
                if (objectIds.isEmpty()) {
                    emit(NetworkResult.Success(emptyList<ArtworkDetail>()))
                    return@flow
                }

                val artworks = mutableListOf<ArtworkDetail>()
                // Limitar a 20 obres per evitar massa càrrega
                val limitedIds = objectIds.take(20)

                for (id in limitedIds) {
                    try {
                        val artworkResponse = apiService.getArtworkDetail(id)
                        if (artworkResponse.isSuccessful) {
                            artworkResponse.body()?.let { artwork ->
                                artworks.add(artwork)
                            }
                        }
                    } catch (e: Exception) {
                        // Continuar amb la següent obra si hi ha error
                        continue
                    }
                }

                // Ordenar per nom de l'autor
                val sortedArtworks = artworks.sortedBy {
                    it.artistAlphaSort ?: it.artistDisplayName ?: "Unknown"
                }
                emit(NetworkResult.Success(sortedArtworks))
            } else {
                emit(NetworkResult.Error("Search failed: ${searchResponse.message()}"))
            }
        } catch (e: Exception) {
            emit(NetworkResult.Error("Network error: ${e.localizedMessage}"))
        }
    }

    suspend fun searchArtworks(filters: SearchFilters): Flow<NetworkResult<List<ArtworkDetail>>> = flow {
        emit(NetworkResult.Loading)
        try {
            val searchResponse = apiService.searchArtworks(
                query = filters.query,
                departmentId = filters.selectedDepartment?.departmentId,
                hasImages = filters.hasImages
            )

            if (searchResponse.isSuccessful) {
                val objectIds = searchResponse.body()?.objectIDs ?: emptyList()
                if (objectIds.isEmpty()) {
                    emit(NetworkResult.Success(emptyList<ArtworkDetail>()))
                    return@flow
                }

                val artworks = mutableListOf<ArtworkDetail>()
                // Limitar a 50 obres per la cerca general
                val limitedIds = objectIds.take(50)

                for (id in limitedIds) {
                    try {
                        val artworkResponse = apiService.getArtworkDetail(id)
                        if (artworkResponse.isSuccessful) {
                            artworkResponse.body()?.let { artwork ->
                                artworks.add(artwork)
                            }
                        }
                    } catch (e: Exception) {
                        continue
                    }
                }

                val sortedArtworks = artworks.sortedBy {
                    it.artistAlphaSort ?: it.artistDisplayName ?: "Unknown"
                }
                emit(NetworkResult.Success(sortedArtworks))
            } else {
                emit(NetworkResult.Error("Search failed: ${searchResponse.message()}"))
            }
        } catch (e: Exception) {
            emit(NetworkResult.Error("Network error: ${e.localizedMessage}"))
        }
    }

    suspend fun getArtworkDetail(objectId: Int): Flow<NetworkResult<ArtworkDetail>> = flow {
        emit(NetworkResult.Loading)
        try {
            val response = apiService.getArtworkDetail(objectId)
            if (response.isSuccessful) {
                response.body()?.let { artwork ->
                    emit(NetworkResult.Success(artwork))
                } ?: emit(NetworkResult.Error("Artwork not found"))
            } else {
                emit(NetworkResult.Error("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(NetworkResult.Error("Network error: ${e.localizedMessage}"))
        }
    }
}