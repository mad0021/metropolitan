package cat.dam.mamadou.metropolitan.utils

object StringUtils {
    fun String?.orDash(): String {
        return if (this.isNullOrBlank()) "-" else this
    }

    fun String.truncate(maxLength: Int): String {
        return if (this.length <= maxLength) {
            this
        } else {
            "${this.substring(0, maxLength)}..."
        }
    }

    fun String.capitalizeWords(): String {
        return this.split(" ").joinToString(" ") { word ->
            word.lowercase().replaceFirstChar { it.uppercase() }
        }
    }
}