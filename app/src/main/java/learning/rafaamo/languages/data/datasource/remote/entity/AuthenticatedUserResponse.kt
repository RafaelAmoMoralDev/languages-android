package learning.rafaamo.languages.data.datasource.remote.entity

data class AuthenticatedUserResponse(
  val id: Long,
  val name: String?,
  val email: String,
  val phone: String?,
  val authToken: String?
)