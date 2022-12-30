package learning.rafaamo.languages.data.datasource.remote

import learning.rafaamo.languages.data.datasource.remote.util.ApiResponse
import learning.rafaamo.languages.data.datasource.remote.entity.AuthenticatedUserResponse
import learning.rafaamo.languages.data.datasource.remote.entity.UserResponse
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

  @GET("$ENDPOINT/{id}")
  suspend fun get(@Path("id") id: Long): ApiResponse<UserResponse>

  @PATCH("$ENDPOINT/{id}/like")
  suspend fun like(@Path("id") id: Long): ApiResponse<Unit>

}