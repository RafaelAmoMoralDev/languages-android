package learning.rafaamo.languages.presentation.ui.main.conversation.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import learning.rafaamo.languages.databinding.ViewHolderConversationBinding
import learning.rafaamo.languages.presentation.ui.main.ConversationViewHolder
class ConversationAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

  var list: List<ConversationListViewModel.ConversationItem>? = null

  override fun getItemCount(): Int = list?.size?: 0

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
    return ConversationViewHolder(ViewHolderConversationBinding.inflate(LayoutInflater.from(parent.context), parent, false))
  }

  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    if (list != null) {
      (holder as ConversationViewHolder).bind(list!![position])
    }
  }

  fun updateList(updatedList: List<ConversationListViewModel.ConversationItem>) {
    val diffResult = DiffUtil.calculateDiff(ConversationDiffUtil(list?: emptyList(), updatedList))
    list = updatedList
    diffResult.dispatchUpdatesTo(this)
  }

}