package learning.rafaamo.languages.domain.entity

import learning.rafaamo.languages.domain.entity.user.IUser


data class Conversation(
  val id: Long,
  val location: String,
  val datetime: Long,
  val IUser: IUser,
  val createdAt: Long,
  val updatedAt: Long
)
