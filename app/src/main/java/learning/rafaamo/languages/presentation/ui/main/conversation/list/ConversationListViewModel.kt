package learning.rafaamo.languages.presentation.ui.main.conversation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import learning.rafaamo.languages.domain.entity.Conversation
import learning.rafaamo.languages.domain.datasource.remote.util.Resource
import learning.rafaamo.languages.domain.repository.ConversationRepository
import javax.inject.Inject

@HiltViewModel
class ConversationListViewModel @Inject constructor(private val conversationRepository: ConversationRepository): ViewModel() {

  private val _response: MutableStateFlow<Resource<List<Conversation>, Unit>> = MutableStateFlow(Resource.Load(emptyList()))
  val response: StateFlow<Resource<List<Conversation>, Unit>> = _response

  init {
    viewModelScope.launch {
      conversationRepository.getAll().collect {
          _response.emit(it)
        }
    }
  }

}