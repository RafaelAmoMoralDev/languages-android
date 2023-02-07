package learning.rafaamo.languages.common

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import learning.rafaamo.languages.R
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppAuthentication @Inject constructor(private val app: App) {

  private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = app.applicationContext.getString(R.string.preference_file_key))
  private val userTokenKey = stringPreferencesKey(app.applicationContext.getString(R.string.preference_user_token_key))

  val logged: Flow<Boolean?> = app.dataStore.data.map { preferences ->
    preferences[userTokenKey] != null
  }

  suspend fun getUserToken(): String? {
    return app.dataStore.data.first()[userTokenKey]
  }

  suspend fun storeUserToken(token: String?) {
    app.dataStore.edit { settings ->
      if (token.isNullOrBlank()) {
        settings.remove(userTokenKey)
      } else {
        settings[userTokenKey] = token
      }
    }
  }

}