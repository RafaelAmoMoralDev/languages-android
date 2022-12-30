package learning.rafaamo.languages.data.datasource.entity

data class User(
  val id: Long,
  val name: String,
  val email: String,
  val phone: String?,
  val description: String?,
  val image: String?,
  val likes: Int,
  val liked: Boolean
)
