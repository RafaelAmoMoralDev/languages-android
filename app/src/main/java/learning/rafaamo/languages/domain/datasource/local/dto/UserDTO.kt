package learning.rafaamo.languages.domain.datasource.local.dto

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class UserDTO(
  @PrimaryKey val id: Long,
  val name: String,
  val email: String,
  val phone: String?,
  val description: String?,
  val image: String?,
  val likes: Int,
  val liked: Boolean
)
