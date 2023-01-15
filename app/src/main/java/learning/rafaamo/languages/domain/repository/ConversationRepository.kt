package learning.rafaamo.languages.domain.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import learning.rafaamo.languages.domain.entity.Conversation
import learning.rafaamo.languages.domain.entity.user.IUser
import learning.rafaamo.languages.domain.datasource.local.ConversationDAO
import learning.rafaamo.languages.domain.datasource.local.UserDAO
import learning.rafaamo.languages.domain.datasource.local.dto.ConversationDTO
import learning.rafaamo.languages.domain.datasource.local.dto.UserDTO
import learning.rafaamo.languages.domain.datasource.remote.API
import learning.rafaamo.languages.domain.datasource.remote.ConversationAPI
import learning.rafaamo.languages.domain.datasource.remote.entity.ConversationResponse
import learning.rafaamo.languages.domain.datasource.remote.util.*
import learning.rafaamo.languages.domain.entity.user.User
import learning.rafaamo.languages.presentation.util.Util
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class ConversationRepository @Inject constructor(
  api: API,
  private val conversationDAO: ConversationDAO,
  private val userDAO: UserDAO
) {

  private val api: ConversationAPI = api

  private suspend fun _getAll(): ApiResponse<List<ConversationResponse>> {
    val response = api.getAll()

    if (response is ApiResponse.Success) {
      val conversationDTOs: MutableList<ConversationDTO> = mutableListOf()
      val userDTOs: MutableList<UserDTO> = mutableListOf()

      response.body.forEach { conversation ->
        conversation.apply {
          conversationDTOs.add(ConversationDTO(id, location, datetime, conversation.userResponse.id, createdAt, updatedAt))
        }

        conversation.userResponse.apply {
          userDTOs.add(UserDTO(id, name, email, phone, description, image, likes, liked))
        }
      }

      conversationDAO.insert(conversationDTOs)
      userDAO.insert(userDTOs)
    }

    return response
  }

  suspend fun getAll(): Flow<Resource<List<Conversation>, Unit>> {
    return flow {
      when (_getAll()) {
        is ApiResponse.Success -> {
          val now = System.currentTimeMillis()
          val nextWeek = Util.getNextWeekMs(now)
          conversationDAO.getDateRangeConversations(now, nextWeek).distinctUntilChanged().collect { conversations ->
            val formattedConversations = conversations.map { conversationWithUser ->
              val user = conversationWithUser.userDTO.let {
                User(it.id, it.name, it.email, it.phone, it.description, it.image, it.likes, it.liked)
              }

              conversationWithUser.let {
                Conversation(it.id, it.location, it.datetime, user, it.updatedAt, it.createdAt)
              }
            }

            emit(Resource.Success(formattedConversations))
          }
        }
        is ApiResponse.Empty -> emit(Resource.Error(null))
        is ApiResponse.Error -> emit(Resource.Error(null))
        is ApiResponse.NetworkError -> emit(Resource.Error(null))
      }
    }
  }

  suspend fun getLastWeekConversations(): Flow<Resource<List<Conversation>, Unit>> {
    return flow {
      emit(Resource.Load(null))

      when (_getAll()) {
        is ApiResponse.Success -> {
          val now = System.currentTimeMillis()
          val lastWeek = Util.getLastWeekMs(now)

          conversationDAO.getDateRangeConversations(lastWeek, now).distinctUntilChanged().collect { conversations ->
            val formattedConversations = conversations.map { conversationWithUser ->
              val user = conversationWithUser.userDTO.let {
                User(it.id, it.name, it.email, it.phone, it.description, it.image, it.likes, it.liked)
              }

              conversationWithUser.let {
                Conversation(it.id, it.location, it.datetime, user, it.updatedAt, it.createdAt)
              }
            }

            emit(Resource.Success(formattedConversations))
          }
        }
        is ApiResponse.Empty -> emit(Resource.Error(null))
        is ApiResponse.Error -> emit(Resource.Error(null))
        is ApiResponse.NetworkError -> emit(Resource.Error(null))
      }
    }
  }

  suspend fun update(conversation: learning.rafaamo.languages.presentation.ui.main.conversation.bottom_sheet.Conversation): Resource<Unit, Unit> {
    withContext(Dispatchers.IO) {
      conversation.apply {
        conversationDAO.update(id, location, datetime)
      }
    }

    return when (api.update(conversation.id, conversation)) {
      is ApiResponse.Success -> Resource.Success(Unit)
      is ApiResponse.Empty -> Resource.Success(Unit)
      is ApiResponse.Error -> Resource.Error(null)
      is ApiResponse.NetworkError -> Resource.Error(null)
    }
  }

}