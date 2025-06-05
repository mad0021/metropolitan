package cat.dam.mamadou.metropolitan.data.model

data class SearchFilters(
    val query: String = "",
    val selectedDepartment: Department? = null,
    val hasImages: Boolean = true
)