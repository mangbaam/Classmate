package mangbaam.classmate

import android.content.Context
import android.content.SharedPreferences

class PreferenceHelper {
    companion object {

        private fun getPreferences(context: Context): SharedPreferences {
            return context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
        }

        fun setBoolean(context: Context, key: String, value: Boolean) {
            val prefs = getPreferences(context)
            val editor = prefs.edit()
            editor.putBoolean(key, value)
            editor.apply()
        }

        fun getBoolean(context: Context, key: String):Boolean {
            val prefs = getPreferences(context)
            return prefs.getBoolean(key, DEFAULT_BOOLEAN_VALUE)
        }

        fun setString(context: Context, key: String, value: String) {
            val prefs = getPreferences(context)
            val editor = prefs.edit()
            editor.putString(key, value)
            editor.apply()
        }

        fun getString(context: Context, key: String): String? {
            val prefs = getPreferences(context)
            return prefs.getString(key, DEFAULT_STRING_VALUE)
        }

        fun setInt(context: Context, key: String, value: Int) {
            val prefs = getPreferences(context)
            val editor = prefs.edit()
            editor.putInt(key, value)
            editor.apply()
        }

        fun getInt(context: Context, key: String): Int? {
            val prefs = getPreferences(context)
            return prefs.getInt(key, DEFAULT_INT_VALUE)
        }

        fun removeKey(context: Context, key: String) {
            val prefs = getPreferences(context)
            val edit = prefs.edit()
            edit.remove(key)
            edit.apply()
        }

        fun clear(context: Context) {
            val prefs = getPreferences(context)
            val edit = prefs.edit()
            edit.clear().apply()
        }

        private const val PREFERENCE_NAME = "APP_SETTINGS"
        private const val DEFAULT_BOOLEAN_VALUE = false
        private const val DEFAULT_STRING_VALUE = ""
        const val DEFAULT_INT_VALUE = 30
    }
}