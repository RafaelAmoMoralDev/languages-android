package learning.rafaamo.languages.domain.datasource.remote.entity

import com.google.gson.annotations.SerializedName
import learning.rafaamo.languages.domain.datasource.remote.entity.user.BasicUserResponse

data class ConversationResponse(
  val id: Long,
  val location: String,
  val datetime: Long,
  @SerializedName("user")
  val userResponse: BasicUserResponse,
  val createdAt: Long,
  val updatedAt: Long
)
