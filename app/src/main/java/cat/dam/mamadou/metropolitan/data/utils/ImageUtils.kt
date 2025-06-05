package cat.dam.mamadou.metropolitan.utils

object ImageUtils {
    fun getImageUrl(primaryImage: String?, primaryImageSmall: String?): String? {
        return when {
            !primaryImageSmall.isNullOrBlank() -> primaryImageSmall
            !primaryImage.isNullOrBlank() -> primaryImage
            else -> null
        }
    }

    fun getHighResImageUrl(primaryImage: String?, primaryImageSmall: String?): String? {
        return when {
            !primaryImage.isNullOrBlank() -> primaryImage
            !primaryImageSmall.isNullOrBlank() -> primaryImageSmall
            else -> null
        }
    }
}