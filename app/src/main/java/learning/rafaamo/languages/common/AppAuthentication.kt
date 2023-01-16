package learning.rafaamo.languages.common

import android.content.Context
import learning.rafaamo.languages.BuildConfig
import learning.rafaamo.languages.R
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class AppAuthentication @Inject constructor(private val app: App) {

  private val file = "${BuildConfig.APPLICATION_ID}.${app.applicationContext.getString(R.string.preference_file_key)}"
  private val userTokenKey = app.applicationContext.getString(R.string.preference_user_token_key)

  fun getUserToken(): String? {
    val sharedPref = app.getSharedPreferences(file, Context.MODE_PRIVATE) ?: return null
    return sharedPref.getString(userTokenKey, null)
  }

  fun storeUserToken(token: String?) {
    val sharedPref = app.getSharedPreferences(file, Context.MODE_PRIVATE) ?: return
    with(sharedPref.edit()) {
      putString(userTokenKey, token)
      commit()
    }
  }

  fun isUserLogged(): Boolean {
    return getUserToken() != null
  }

}