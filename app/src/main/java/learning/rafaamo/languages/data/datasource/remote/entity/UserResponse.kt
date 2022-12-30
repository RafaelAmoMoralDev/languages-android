package learning.rafaamo.languages.data.datasource.remote.entity


data class UserResponse(
  val id: Long,
  val name: String,
  val email: String,
  val phone: String?,
  val description: String?,
  val image: String?,
  val likes: Int,
  val liked: Boolean
)
