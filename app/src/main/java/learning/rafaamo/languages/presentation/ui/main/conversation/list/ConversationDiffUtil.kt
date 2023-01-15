package learning.rafaamo.languages.presentation.ui.main.conversation.list

import androidx.recyclerview.widget.DiffUtil

class ConversationDiffUtil(
  private val oldConversations: List<ConversationListViewModel.ConversationItem>,
  private val newConversations: List<ConversationListViewModel.ConversationItem>
): DiffUtil.Callback() {

  override fun getOldListSize(): Int = oldConversations.size
  override fun getNewListSize(): Int = newConversations.size

  override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
    return oldConversations[oldItemPosition].conversation.id == newConversations[newItemPosition].conversation.id
  }

  override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
    return oldConversations[oldItemPosition] == newConversations[newItemPosition]
  }

}