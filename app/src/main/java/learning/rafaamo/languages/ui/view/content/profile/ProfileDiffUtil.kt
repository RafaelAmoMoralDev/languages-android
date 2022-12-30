package learning.rafaamo.languages.ui.view.content.profile

import androidx.recyclerview.widget.DiffUtil
import learning.rafaamo.languages.ui.view.content.profile.ProfileItem.*

class ProfileDiffUtil(private val oldListItems: List<ProfileItem>, private val newListItems: List<ProfileItem>): DiffUtil.Callback() {

  override fun getOldListSize(): Int = oldListItems.size
  override fun getNewListSize(): Int = newListItems.size

  private fun getOldItem(oldItemPosition: Int): ProfileItem = oldListItems[oldItemPosition]
  private fun getNewItem(newItemPosition: Int): ProfileItem = newListItems[newItemPosition]

  override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
    val oldItem = getOldItem(oldItemPosition)
    val newItem = getNewItem(newItemPosition)
    
    return when {
      oldItem is HeaderItem && newItem is HeaderItem -> {
        oldItem.user.id == newItem.user.id
      }
      oldItem is ConversationItem && newItem is ConversationItem -> {
        oldItem.conversation.id == newItem.conversation.id
      }
      oldItem is LoaderItem && newItem is LoaderItem -> {
        true
      } 
      else -> false
    }
  }

  override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
    val oldItem = getOldItem(oldItemPosition)
    val newItem = getNewItem(newItemPosition)

    return when {
      oldItem is HeaderItem && newItem is HeaderItem -> {
        oldItem.user == newItem.user
      }
      oldItem is ConversationItem && newItem is ConversationItem -> {
        oldItem.conversation == newItem.conversation
      }
      oldItem is LoaderItem && newItem is LoaderItem -> {
        true
      }
      else -> false
    }
  }

  override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
    val oldItem = getOldItem(oldItemPosition)
    val newItem = getNewItem(newItemPosition)

    var payload: Any? = null
    if (oldItem is HeaderItem && newItem is HeaderItem && oldItem.user.liked != newItem.user.liked) {
      payload = ProfileAdapter.Payload.Like(newItem)
    }

    return payload
  }
}