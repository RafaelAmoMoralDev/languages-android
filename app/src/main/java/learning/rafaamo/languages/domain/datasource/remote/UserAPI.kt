package learning.rafaamo.languages.domain.datasource.remote

import learning.rafaamo.languages.domain.datasource.remote.util.ApiResponse
import learning.rafaamo.languages.domain.datasource.remote.entity.user.AuthenticatedUserResponse
import learning.rafaamo.languages.domain.datasource.remote.entity.user.BasicUserResponse
import learning.rafaamo.languages.domain.datasource.remote.entity.user.UserResponse
import retrofit2.http.*

private const val ENDPOINT = "users"

interface UserAPI {

  @FormUrlEncoded
  @POST("$ENDPOINT/sign-in")
  suspend fun signIn(
    @Field("email") email: String,
    @Field("password") password: String,
  ): ApiResponse<AuthenticatedUserResponse>

  @FormUrlEncoded
  @POST("$ENDPOINT/sign-up")
  suspend fun signUp(
    @Field("email") email: String,
    @Field("name") name: String,
    @Field("password") password: String,
  ): ApiResponse<AuthenticatedUserResponse>

  @GET("$ENDPOINT/me")
  suspend fun me(): ApiResponse<UserResponse>

  @GET("$ENDPOINT/{id}")
  suspend fun get(@Path("id") id: Long): ApiResponse<BasicUserResponse>

  @GET(ENDPOINT)
  suspend fun getAllUsers(): ApiResponse<List<BasicUserResponse>>

  @PATCH("$ENDPOINT/{id}/like")
  suspend fun like(@Path("id") id: Long): ApiResponse<Unit>

}