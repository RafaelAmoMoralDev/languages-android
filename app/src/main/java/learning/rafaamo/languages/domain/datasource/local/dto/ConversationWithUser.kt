package learning.rafaamo.languages.domain.datasource.local.dto

import androidx.room.ColumnInfo
import androidx.room.Relation

data class ConversationWithUser(
  val id: Long,
  val location: String,
  val datetime: Long,
  @ColumnInfo(name = "user_id")
  var userId: Long,
  @ColumnInfo(name = "created_at")
  val createdAt: Long,
  @ColumnInfo(name = "updated_at")
  val updatedAt: Long,
  @Relation(parentColumn = "user_id", entityColumn = "id")
  var userDTO: UserDTO
)