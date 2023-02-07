package learning.rafaamo.languages.domain.datasource.remote.util

import kotlinx.coroutines.runBlocking
import learning.rafaamo.languages.common.AppAuthentication
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class ApiInterceptor @Inject constructor(private val userAuthentication: AppAuthentication) : Interceptor {

  override fun intercept(chain: Interceptor.Chain): Response {
    val token = runBlocking {
      userAuthentication.getUserToken()
    }

    val requestBuilder = chain.request().newBuilder().apply {
      addHeader("Authorization", token ?: "")
    }

    val response = chain.proceed(requestBuilder.build())
    if (response.code == 401) {
      runBlocking {
        userAuthentication.storeUserToken(null)
      }
    }

    return response
  }

}