package learning.rafaamo.languages.presentation.ui.main.conversation.list

import androidx.recyclerview.widget.DiffUtil
import learning.rafaamo.languages.domain.entity.Conversation

class ConversationDiffUtil(private val oldConversations: List<Conversation>, private val newConversations: List<Conversation>): DiffUtil.Callback() {

  override fun getOldListSize(): Int = oldConversations.size
  override fun getNewListSize(): Int = newConversations.size

  override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
    return oldConversations[oldItemPosition].id == newConversations[newItemPosition].id
  }

  override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
    return oldConversations[oldItemPosition] == newConversations[newItemPosition]
  }

}