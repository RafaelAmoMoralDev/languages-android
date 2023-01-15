package learning.rafaamo.languages.presentation.ui.authentication.signin

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle

private enum class SavedStateKeys { EMAIL, PASSWORD }

class SignInForm (savedStateHandle: SavedStateHandle){

  val email: MutableLiveData<String?> = savedStateHandle.getLiveData(SavedStateKeys.EMAIL.name)
  val password: MutableLiveData<String?> = savedStateHandle.getLiveData(SavedStateKeys.PASSWORD.name)
  val valid = MediatorLiveData<Boolean>().apply {
    addSource(email) { value = isValid() }
    addSource(password) { value = isValid() }
  }

  private fun isValid() : Boolean {
    return email.value?.isNotBlank() == true && password.value?.isNotBlank() == true
  }

}