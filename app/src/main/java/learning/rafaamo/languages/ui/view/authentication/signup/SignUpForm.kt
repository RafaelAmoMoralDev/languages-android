package learning.rafaamo.languages.ui.view.authentication.signup

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle

private enum class SavedStateKeys { EMAIL, NAME, PASSWORD }

class SignUpForm(savedStateHandle: SavedStateHandle) {

  val email: MutableLiveData<String?> = savedStateHandle.getLiveData(SavedStateKeys.EMAIL.name)
  val name: MutableLiveData<String?> = savedStateHandle.getLiveData(SavedStateKeys.NAME.name)
  val password: MutableLiveData<String?> = savedStateHandle.getLiveData(SavedStateKeys.PASSWORD.name)
  val valid = MediatorLiveData<Boolean>().apply {
    addSource(email) { value = isValid() }
    addSource(name) { value = isValid() }
    addSource(password) { value = isValid() }
  }

  private fun isValid() : Boolean {
    return email.value?.isNotBlank() == true
      && name.value?.isNotBlank() == true
      && password.value?.isNotBlank() == true
  }

}