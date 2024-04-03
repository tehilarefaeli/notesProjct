package com.example.notesapp

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.example.notesapp.activities.ui.main.models.User
import com.google.gson.Gson

fun Context.getSharedPreferences() : SharedPreferences{
    return sharedPreferences
}

val VERSION = 1
var KEY_User = "KEY_User"
val PREFERENCES_NAME = "NotesPref$VERSION"

private var _user : User?=null
var SharedPreferences.user : User?
    get()  {
        if(_user == null) {
            var json = getString(KEY_User, "")
            if (json.isNullOrEmpty().not()) {
                _user = json.fromJson(User::class.java)
            }
        }

        return _user
    }
    set(value) {
        _user = value
        editSharedPreferences { editor ->
            editor.put(KEY_User to value.toJson())
        }
    }

val sharedPreferences: SharedPreferences get() = App.context.getSharedPreferences(
    PREFERENCES_NAME, MODE_PRIVATE)

private inline fun SharedPreferences.editSharedPreferences(operation: (SharedPreferences.Editor) -> Unit) {
    val editSharePreferences = edit()
    operation(editSharePreferences)
    editSharePreferences.apply()
}

private fun SharedPreferences.Editor.put(pair: Pair<String, Any?>) {
    val key = pair.first
    when (val value = pair.second) {
        is String -> putString(key, value)
        is Int -> putInt(key, value)
        is Boolean -> putBoolean(key, value)
        is Long -> putLong(key, value)
        is Float -> putFloat(key, value)
    }
}

fun Any?.toJson():String {
    this ?: return ""

    return try {
        Gson().toJson(this)
    } catch (e: Exception) {
        ""
    }
}

fun <T> String?.fromJson(classOfT: Class<T>): T? =
    takeIf { !it.isNullOrEmpty()
    }?.runCatching { Gson().fromJson(this, classOfT) }?.getOrNull()
