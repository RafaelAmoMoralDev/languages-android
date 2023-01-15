package learning.rafaamo.languages.presentation.ui.main.conversation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import learning.rafaamo.languages.domain.entity.Conversation
import learning.rafaamo.languages.domain.datasource.remote.util.Resource
import learning.rafaamo.languages.domain.datasource.remote.util.Resource.*
import learning.rafaamo.languages.domain.entity.user.IUser
import learning.rafaamo.languages.domain.repository.ConversationRepository
import learning.rafaamo.languages.presentation.ui.main.IConversationItem
import javax.inject.Inject

@HiltViewModel
class ConversationListViewModel @Inject constructor(private val conversationRepository: ConversationRepository): ViewModel() {

  private val _response: MutableStateFlow<Resource<List<ConversationItem>, Unit>> = MutableStateFlow(Load(emptyList()))
  val response: StateFlow<Resource<List<ConversationItem>, Unit>> = _response

  private val _userToRedirect = MutableSharedFlow<IUser>()
  val userToRedirect = _userToRedirect.asSharedFlow()

  private val _conversationToEdit = MutableSharedFlow<Conversation>()
  val conversationToEdit = _conversationToEdit.asSharedFlow()

  init {
    viewModelScope.launch {
      conversationRepository.getAll().collect { conversationsResource ->
        when (conversationsResource) {
          is Load -> {
            _response.emit(Load(processConversationList(conversationsResource.data)))
          }
          is Success -> {
            _response.emit(Success(processConversationList(conversationsResource.data)!!))
          }
          is Error -> {
            _response.emit(Error(conversationsResource.error))
          }
        }
      }
    }
  }

  private fun processConversationList(list: List<Conversation>?): List<ConversationItem>? {
    return list?.map { conversation ->
      ConversationItem(
        conversation = conversation,
        onUserClicked = {
          viewModelScope.launch {
            _userToRedirect.emit(conversation.user)
          }
        },
        onEditClicked = {
          viewModelScope.launch {
            _conversationToEdit.emit(conversation)
          }
        }
      )
    }
  }

  class ConversationItem(
    override val conversation: Conversation, override val onUserClicked: () -> Unit, val onEditClicked: () -> Unit
  ): IConversationItem

}