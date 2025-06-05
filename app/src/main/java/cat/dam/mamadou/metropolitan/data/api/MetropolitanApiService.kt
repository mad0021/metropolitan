package cat.dam.mamadou.metropolitan.data.api

import cat.dam.mamadou.metropolitan.data.model.*
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MetropolitanApiService {
    @GET("public/collection/v1/departments")
    suspend fun getDepartments(): Response<DepartmentsResponse>

    @GET("public/collection/v1/search")
    suspend fun searchArtworks(
        @Query("q") query: String? = null,
        @Query("departmentId") departmentId: Int? = null,
        @Query("hasImages") hasImages: Boolean? = null,
        @Query("geoLocation") geoLocation: String? = null
    ): Response<SearchResponse>

    @GET("public/collection/v1/objects/{objectId}")
    suspend fun getArtworkDetail(@Path("objectId") objectId: Int): Response<ArtworkDetail>
}
