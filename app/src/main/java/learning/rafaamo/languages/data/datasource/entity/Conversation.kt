package learning.rafaamo.languages.data.datasource.entity


data class Conversation(
  val id: Long,
  val location: String,
  val datetime: Long,
  val user: User,
  val createdAt: Long,
  val updatedAt: Long
)
