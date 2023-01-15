package learning.rafaamo.languages.domain.datasource.remote.util

import learning.rafaamo.languages.common.AppAuthentication
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject


class ApiHeader @Inject constructor(private val userAuthentication: AppAuthentication) : Interceptor {

  override fun intercept(chain: Interceptor.Chain): Response {
    val requestBuilder = chain.request().newBuilder()
    requestBuilder.addHeader("Authorization", userAuthentication.getUserToken() ?: "")
    return chain.proceed(requestBuilder.build())
  }

}