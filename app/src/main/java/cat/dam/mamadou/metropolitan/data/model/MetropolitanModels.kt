package cat.dam.mamadou.metropolitan.data.model

import kotlinx.serialization.Serializable

@Serializable
data class SearchResponse(
    val total: Int,
    val objectIDs: List<Int>? = null
)

@Serializable
data class ArtworkDetail(
    val objectID: Int,
    val isHighlight: Boolean? = null,
    val primaryImage: String? = null,
    val primaryImageSmall: String? = null,
    val title: String? = null,
    val artistDisplayName: String? = null,
    val artistAlphaSort: String? = null,
    val artistNationality: String? = null,
    val artistBeginDate: String? = null,
    val artistEndDate: String? = null,
    val objectDate: String? = null,
    val medium: String? = null,
    val dimensions: String? = null,
    val creditLine: String? = null,
    val geographyType: String? = null,
    val city: String? = null,
    val state: String? = null,
    val county: String? = null,
    val country: String? = null,
    val region: String? = null,
    val subregion: String? = null,
    val locale: String? = null,
    val locus: String? = null,
    val excavation: String? = null,
    val river: String? = null,
    val classification: String? = null,
    val rightsAndReproduction: String? = null,
    val linkResource: String? = null,
    val metadataDate: String? = null,
    val repository: String? = null,
    val objectURL: String? = null,
    val tags: List<Tag>? = null,
    val objectWikidata_URL: String? = null,
    val isTimelineWork: Boolean? = null,
    val GalleryNumber: String? = null
)

@Serializable
data class Tag(
    val term: String,
    val AAT_URL: String? = null,
    val Wikidata_URL: String? = null
)

@Serializable
data class Department(
    val departmentId: Int,
    val displayName: String
)

@Serializable
data class DepartmentsResponse(
    val departments: List<Department>
)
