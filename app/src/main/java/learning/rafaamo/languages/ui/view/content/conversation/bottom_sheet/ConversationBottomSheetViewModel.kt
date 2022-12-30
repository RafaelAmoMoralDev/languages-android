package learning.rafaamo.languages.ui.view.content.conversation.bottom_sheet

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import learning.rafaamo.languages.data.datasource.remote.util.Resource
import learning.rafaamo.languages.data.repository.ConversationRepository
import learning.rafaamo.languages.ui.util.Util
import javax.inject.Inject

@HiltViewModel
class ConversationBottomSheetViewModel @Inject constructor(
  savedStateHandle: SavedStateHandle,
  private val conversationRepository: ConversationRepository
): ViewModel() {

  val conversation: Conversation = savedStateHandle[ConversationBottomSheet.CONVERSATION]!!
  val form = ConversationForm(savedStateHandle, conversation.location, conversation.datetime)

  private val _response: MutableStateFlow<Resource<Unit, Unit>?> = MutableStateFlow(null)
  val response: StateFlow<Resource<Unit, Unit>?> = _response

  private var _loading: MutableStateFlow<Boolean> = MutableStateFlow(false)
  var loading: StateFlow<Boolean> = _loading

  init {
    viewModelScope.launch {
      response.collect {
        _loading.emit(it is Resource.Load)
      }
    }
  }

  fun save() {
    viewModelScope.launch {
      _response.emit(Resource.Load(Unit))
      _response.emit(conversationRepository.update(Conversation(conversation.id, form.location.value!!, Util.parseDate(form.date.value!!))))
    }
  }

}