package ec.edu.uisek.githubclient.services

import android.content.Context
import androidx.security.crypto.MasterKey
import androidx.security.crypto.EncryptedSharedPreferences
import ec.edu.uisek.githubclient.models.UserCredentials


class SessionManager (context: Context){
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        "user_shared_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    companion object{
        private const val USERNAME = "username"
        private const val PASSWORD = "password"

    }

    fun saveCredentials(username: String, password: String){
        sharedPreferences.edit()
            .putString(USERNAME, username)
            .putString(PASSWORD, password)
            .apply()

    }

    fun getCredentials(): UserCredentials? {
        val username = sharedPreferences.getString(USERNAME, null)
        val password = sharedPreferences.getString(PASSWORD, null)

        return if (username != null && password != null) {
            UserCredentials(username, password)
        } else {
            null
        }
    }

    fun clearCredentoials(){
        sharedPreferences.edit()
            .remove(USERNAME)
            .remove(PASSWORD)
            .apply()

    }

}
