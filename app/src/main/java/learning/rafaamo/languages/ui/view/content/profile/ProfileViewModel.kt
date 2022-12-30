package learning.rafaamo.languages.ui.view.content.profile

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import learning.rafaamo.languages.R
import learning.rafaamo.languages.common.AppAuthentication
import learning.rafaamo.languages.data.datasource.entity.User
import learning.rafaamo.languages.data.datasource.remote.util.Resource.*
import learning.rafaamo.languages.data.repository.ConversationRepository
import learning.rafaamo.languages.data.repository.UserRepository
import learning.rafaamo.languages.ui.view.content.profile.ProfileItem.*
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
  appUserAuthentication: AppAuthentication,
  val savedStateHandle: SavedStateHandle,
  @ApplicationContext context: Context,
  private val userRepository: UserRepository,
  private val conversationRepository: ConversationRepository
) : ViewModel() {

  val id: Long = savedStateHandle["id"] ?: appUserAuthentication.getUserId()!!

  /** True only if the current user displayed in the view is the app user */
  private val loadConversations = id == appUserAuthentication.getUserId()!!

  private val _uiState = MutableStateFlow(UiState())
  val uiState = _uiState.asStateFlow()

  private val _userToRedirect = MutableSharedFlow<User>()
  val userToRedirect = _userToRedirect.asSharedFlow()

  private val _fetchConversationsError = MutableSharedFlow<Unit>()
  val fetchConversationsError = _fetchConversationsError.asSharedFlow()

  init {
    viewModelScope.launch {
      userRepository.get(id).collect { userResponse ->
        when (userResponse) {
          is Load -> {
            _uiState.emit(_uiState.value.copy(userLoading = true))
          }
          is Success -> {
            val user = userResponse.data
            val headerItem = HeaderItem(user, loadConversations) {
              viewModelScope.launch {
                userRepository.like(user)
              }
            }

            val isUserUpdate = _uiState.value.list != null
            val list: MutableList<ProfileItem> = if (isUserUpdate) {
              val unreferencedList = uiState.value.list!!.toMutableList()
              unreferencedList.apply {
                set(0, headerItem)
              }
            } else {
              mutableListOf<ProfileItem>().apply {
                add(headerItem)
              }
            }

            _uiState.emit(_uiState.value.copy(userLoading = false, list = list))
          }
          is Error -> {
            _uiState.emit(_uiState.value.copy(userLoading = false, userError = userResponse.error?.message ?: context.getString(R.string.error_unknown)))
          }
        }
      }
    }

    if (loadConversations) {
      viewModelScope.launch {
        uiState.collect { uiState ->
          val userResponseWasSuccess = !uiState.userLoading && uiState.list != null
          if (userResponseWasSuccess) {

            conversationRepository.getLastWeekConversations().collect { conversationsResponse ->
              val list: MutableList<ProfileItem> = mutableListOf<ProfileItem>().apply {
                add((_uiState.value.list!!.first() as HeaderItem))
              }

              when (conversationsResponse) {
                is Load -> {
                  list.add(LoaderItem)
                }
                is Success -> {
                  list.addAll(conversationsResponse.data.map { conversation ->
                    ConversationItem(
                      conversation,
                      onUserClicked = {
                        viewModelScope.launch {
                          _userToRedirect.emit(conversation.user)
                        }
                      },
                      onEditClicked = null
                    )
                  })
                }
                is Error -> {
                  _fetchConversationsError.emit(Unit)
                }
              }

              _uiState.emit(_uiState.value.copy(list = list))
            }
          }
        }
      }
    }
  }

}