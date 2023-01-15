package learning.rafaamo.languages.domain.datasource.remote

import learning.rafaamo.languages.domain.datasource.remote.util.ApiResponse
import learning.rafaamo.languages.domain.datasource.remote.entity.ConversationResponse
import learning.rafaamo.languages.presentation.ui.main.conversation.bottom_sheet.Conversation
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path

private const val ENDPOINT = "conversations"

interface ConversationAPI {

  @GET(ENDPOINT)
  suspend fun getAll(): ApiResponse<List<ConversationResponse>>

  @PATCH("$ENDPOINT/{id}")
  suspend fun update(
    @Path("id") id: Long,
    @Body conversation: Conversation
  ): ApiResponse<Unit>

}