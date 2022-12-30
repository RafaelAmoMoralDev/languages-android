package learning.rafaamo.languages.ui.view.content.conversation.bottom_sheet

import androidx.lifecycle.*
import learning.rafaamo.languages.ui.util.Util

private enum class SavedStateKeys { LOCATION, DATE }

class ConversationForm(
  savedStateHandle: SavedStateHandle,
  _location: String,
  private var _dateMs: Long
): ViewModel() {

  var location: MutableLiveData<String> = savedStateHandle.getLiveData(SavedStateKeys.LOCATION.name, _location)
  private var _date: MutableLiveData<String> = savedStateHandle.getLiveData(SavedStateKeys.DATE.name, Util.parseDate(_dateMs))
  var date = _date as LiveData<String>

  val valid = MediatorLiveData<Boolean>().apply {
    addSource(location) { value = isValid() }
    addSource(date) { value = isValid() }
  }

  fun setDateMs(dateMs: Long) {
    _dateMs = dateMs
    _date.value = Util.parseDate(dateMs, false)
  }

  fun getDateMs(): Long {
    return _dateMs
  }

  private fun isValid() : Boolean {
    return location.value!!.isNotBlank() && date.value!!.isNotBlank()
  }

}