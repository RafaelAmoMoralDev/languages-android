package learning.rafaamo.languages.domain.datasource.remote

import learning.rafaamo.languages.domain.datasource.remote.util.ApiResponse
import learning.rafaamo.languages.domain.datasource.remote.entity.ConversationResponse
import learning.rafaamo.languages.presentation.ui.main.conversation.bottom_sheet.Conversation
import retrofit2.http.*

private const val ENDPOINT = "conversations"

interface ConversationAPI {

  @GET(ENDPOINT)
  suspend fun getAllConversations(): ApiResponse<List<ConversationResponse>>

  @FormUrlEncoded
  @POST(ENDPOINT)
  suspend fun create(
    @Field("location") location: String,
    @Field("datetime") datetime: Long
  ): ApiResponse<ConversationResponse>

  @PATCH("$ENDPOINT/{id}")
  suspend fun update(
    @Path("id") id: Long,
    @Body conversation: Conversation
  ): ApiResponse<Unit>

}