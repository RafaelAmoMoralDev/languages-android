package learning.rafaamo.languages.presentation.ui.authentication.signin

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import learning.rafaamo.languages.domain.datasource.remote.util.Resource
import learning.rafaamo.languages.domain.repository.UserRepository
import javax.inject.Inject


@HiltViewModel
class SignInViewModel @Inject constructor(
  savedStateHandle: SavedStateHandle,
  private val userRepository: UserRepository
) : ViewModel() {

  val form = SignInForm(savedStateHandle)

  private var _signInResponse: MutableSharedFlow<Resource<Unit, UserRepository.SignInError>> = MutableSharedFlow()
  val signInResponse: SharedFlow<Resource<Unit, UserRepository.SignInError>> = _signInResponse

  private var _loading: MutableStateFlow<Boolean> = MutableStateFlow(false)
  var loading: StateFlow<Boolean> = _loading

  init {
    viewModelScope.launch {
      signInResponse.collect {
        _loading.emit(it is Resource.Load)
      }
    }
  }

  fun signIn() {
    viewModelScope.launch {
      _signInResponse.emit(Resource.Load(null))
      _signInResponse.emit(
        userRepository.signIn(form.email.value!!, form.password.value!!)
      )
    }
  }

}