package learning.rafaamo.languages.data.datasource.local.dto

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "conversation")
data class ConversationDTO(
  @PrimaryKey val id: Long,
  val location: String,
  val datetime: Long,
  @ColumnInfo(name = "user_id")
  val userId: Long,
  @ColumnInfo(name = "created_at")
  val createdAt: Long,
  @ColumnInfo(name = "updated_at")
  val updatedAt: Long,
)