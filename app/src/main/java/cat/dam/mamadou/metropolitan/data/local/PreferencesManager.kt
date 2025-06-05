package cat.dam.mamadou.metropolitan.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(
        "metropolitan_prefs",
        Context.MODE_PRIVATE
    )

    companion object {
        private const val KEY_DARK_MODE = "dark_mode"
        private const val KEY_LAST_SEARCH_QUERY = "last_search_query"
        private const val KEY_LAST_SELECTED_DEPARTMENT = "last_selected_department"
    }

    var isDarkMode: Boolean
        get() = prefs.getBoolean(KEY_DARK_MODE, false)
        set(value) = prefs.edit { putBoolean(KEY_DARK_MODE, value) }

    var lastSearchQuery: String
        get() = prefs.getString(KEY_LAST_SEARCH_QUERY, "") ?: ""
        set(value) = prefs.edit { putString(KEY_LAST_SEARCH_QUERY, value) }

    var lastSelectedDepartmentId: Int
        get() = prefs.getInt(KEY_LAST_SELECTED_DEPARTMENT, -1)
        set(value) = prefs.edit { putInt(KEY_LAST_SELECTED_DEPARTMENT, value) }
}