package learning.rafaamo.languages.common

import android.content.Context
import learning.rafaamo.languages.BuildConfig
import learning.rafaamo.languages.R
import learning.rafaamo.languages.data.datasource.entity.User
import learning.rafaamo.languages.data.datasource.remote.entity.AuthenticatedUserResponse
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class AppAuthentication @Inject constructor(private val app: App) {

  private val file = "${BuildConfig.APPLICATION_ID}.${app.applicationContext.getString(R.string.preference_file_key)}"
  private val userTokenKey = app.applicationContext.getString(R.string.preference_user_token_key)
  private val userIdKey = app.applicationContext.getString(R.string.preference_user_id_key)

  fun setAuthenticatedUser(authenticatedUserResponse: AuthenticatedUserResponse?) {
    storeUserToken(authenticatedUserResponse?.authToken)
    storeUserId(authenticatedUserResponse?.id)
  }

  fun isUserLogged(): Boolean {
    return getUserToken() != null
  }

  fun getUserToken(): String? {
    val sharedPref = app.getSharedPreferences(file, Context.MODE_PRIVATE) ?: return null
    return sharedPref.getString(userTokenKey, null)
  }

  //TODO: Estudiar después del proyeto - Cuando debo de usar
  // https://www.youtube.com/watch?v=aaSck7jBDbw&ab_channel=PhilippLackner
  private fun storeUserToken(token: String?) {
    val sharedPref = app.getSharedPreferences(file, Context.MODE_PRIVATE) ?: return
    with(sharedPref.edit()) {
      putString(userTokenKey, token)
      commit()
    }
  }

  fun getUserId(): Long? {
    val sharedPref = app.getSharedPreferences(file, Context.MODE_PRIVATE) ?: return null
    val id = sharedPref.getLong(userIdKey, -1L)
    return if (id == -1L) null else id
  }

  private fun storeUserId(id: Long?) {
    val sharedPref = app.getSharedPreferences(file, Context.MODE_PRIVATE) ?: return
    with(sharedPref.edit()) {
      if (id == null) {
        remove(userIdKey)
      } else {
        putLong(userIdKey, id)
      }
      commit()
    }
  }

}