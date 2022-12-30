package learning.rafaamo.languages.ui.view.authentication.signup

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import learning.rafaamo.languages.data.datasource.entity.AppError
import learning.rafaamo.languages.data.datasource.remote.util.Resource
import learning.rafaamo.languages.data.repository.UserRepository
import javax.inject.Inject


@HiltViewModel
class SignUpViewModel @Inject constructor(
  savedStateHandle: SavedStateHandle,
  private val userRepository: UserRepository
) : ViewModel() {

  val form = SignUpForm(savedStateHandle)

  //TODO: Estudiar después del proyeto - Diferencia entre shared flow y state flow
  private var _signUpResponse: MutableSharedFlow<Resource<Unit, AppError>> = MutableSharedFlow()
  val signUpResponse: SharedFlow<Resource<Unit, AppError>> = _signUpResponse

  private var _loading: MutableStateFlow<Boolean> = MutableStateFlow(false)
  var loading: StateFlow<Boolean> = _loading

  init {
    viewModelScope.launch {
      signUpResponse.collect {
        _loading.emit(it is Resource.Load)
      }
    }
  }


  fun signUp() {
    viewModelScope.launch {
      _signUpResponse.emit(Resource.Load(Unit))
      _signUpResponse.emit(
        userRepository.signUp(form.email.value!!, form.name.value!!, form.password.value!!)
      )
    }
  }

}