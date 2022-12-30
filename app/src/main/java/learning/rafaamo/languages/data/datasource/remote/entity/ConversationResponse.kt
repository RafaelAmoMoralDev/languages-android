package learning.rafaamo.languages.data.datasource.remote.entity

import com.google.gson.annotations.SerializedName

data class ConversationResponse(
  val id: Long,
  val location: String,
  val datetime: Long,
  @SerializedName("user")
  val userResponse: UserResponse,
  val createdAt: Long,
  val updatedAt: Long
)
