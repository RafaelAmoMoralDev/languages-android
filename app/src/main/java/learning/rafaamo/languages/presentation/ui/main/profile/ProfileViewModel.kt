package learning.rafaamo.languages.presentation.ui.main.profile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import learning.rafaamo.languages.R
import learning.rafaamo.languages.common.AppAuthentication
import learning.rafaamo.languages.domain.entity.AppError
import learning.rafaamo.languages.domain.datasource.remote.util.Resource
import learning.rafaamo.languages.domain.datasource.remote.util.Resource.*
import learning.rafaamo.languages.domain.entity.user.IUser
import learning.rafaamo.languages.domain.entity.user.User
import learning.rafaamo.languages.domain.repository.ConversationRepository
import learning.rafaamo.languages.domain.repository.UserRepository
import learning.rafaamo.languages.presentation.ui.main.profile.ProfileItem.*
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
  val savedStateHandle: SavedStateHandle,
  private val appUserAuthentication: AppAuthentication,
  private val userRepository: UserRepository,
  private val conversationRepository: ConversationRepository
) : ViewModel() {

  /** Null if the user show in the UI is the app's user */
  val id: Long? = savedStateHandle["id"]

  private val _uiState = MutableStateFlow(UiState())
  val uiState = _uiState.asStateFlow()

  private val _userToRedirect = MutableSharedFlow<IUser>()
  val userToRedirect = _userToRedirect.asSharedFlow()

  private val _fetchConversationsError = MutableSharedFlow<Unit>()
  val fetchConversationsError = _fetchConversationsError.asSharedFlow()

  init {
    val isUserApp = id == null
    if (isUserApp) {
      loadUserApp()
    } else {
      loadUser()
    }
  }

  private fun loadUserApp() {
    viewModelScope.launch {
      userRepository.me().collect { userResponse ->
        processUserResponse(userResponse)
      }
    }

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
                    conversation, onUserClicked = {
                      viewModelScope.launch {
                        _userToRedirect.emit(conversation.IUser)
                      }
                    }, onEditClicked = null
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

  private fun loadUser() {
    viewModelScope.launch {
      userRepository.get(id!!).collect { userResponse ->
       processUserResponse(userResponse)
      }
    }
  }

  private fun processUserResponse(userResource: Resource<IUser, AppError>) {
    viewModelScope.launch {
      when (userResource) {
        is Load -> {
          _uiState.emit(_uiState.value.copy(userLoading = true))
        }
        is Success -> {
          val user = userResource.data
          val onLikeClickedCallBack: (() -> Unit)? = if (user is User) {
            {
              viewModelScope.launch {
                userRepository.like(user)
              }
            }
          } else {
            null
          }
          val headerItem = HeaderItem(user, onLikeClickedCallBack)

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
          val userErrorRes = if (userResource.error?.message == null) {
            R.string.error_unknown
          } else {
            null
          }
          _uiState.emit(_uiState.value.copy(userLoading = false, userError = UiStateError(userError = userResource.error?.message, userErrorRes = userErrorRes)))
        }
      }
    }
  }

  fun logOut() {
    appUserAuthentication.storeUserToken(null)
  }

}