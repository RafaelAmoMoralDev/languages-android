package learning.rafaamo.languages.domain.datasource.remote.entity.user

data class AuthenticatedUserResponse(
  override val id: Long,
  override val name: String,
  override val email: String,
  override val phone: String?,
  override val description: String?,
  override val image: String?,
  override val likes: Int,
  val authToken: String?
): IUserResponse