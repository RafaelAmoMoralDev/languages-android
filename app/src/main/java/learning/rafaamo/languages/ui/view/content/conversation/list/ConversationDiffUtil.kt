package learning.rafaamo.languages.ui.view.content.conversation.list

import androidx.recyclerview.widget.DiffUtil
import learning.rafaamo.languages.data.datasource.entity.Conversation

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