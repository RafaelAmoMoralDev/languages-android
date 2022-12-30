package learning.rafaamo.languages.ui.view.content.conversation.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import learning.rafaamo.languages.data.datasource.entity.Conversation
import learning.rafaamo.languages.data.datasource.entity.User
import learning.rafaamo.languages.databinding.ViewHolderConversationBinding
import learning.rafaamo.languages.ui.util.Util

class ConversationAdapter(
  private val onUserClicked: (user: User) -> Unit,
  private val onEditClicked: (conversation: Conversation) -> Unit,
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

  var list: List<Conversation>? = null

  override fun getItemCount(): Int = list?.size?: 0

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
    return ConversationViewHolder(ViewHolderConversationBinding.inflate(LayoutInflater.from(parent.context), parent, false), onUserClicked, onEditClicked)
  }

  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    if (list != null) {
      (holder as ConversationViewHolder).bind(list!![position])
    }
  }

  fun updateList(updatedList: List<Conversation>) {
    val diffResult = DiffUtil.calculateDiff(ConversationDiffUtil(list?: emptyList(), updatedList))
    list = updatedList
    diffResult.dispatchUpdatesTo(this)
  }

  class ConversationViewHolder(
    private val conversationView: ViewHolderConversationBinding,
    private val onUserClicked: (user: User) -> Unit,
    private val onEditClicked: ((conversation: Conversation) -> Unit)?
  ): RecyclerView.ViewHolder(conversationView.root) {

    fun bind(conversation: Conversation) {
      conversationView.apply {
        tvUsername.apply {
          text = conversation.user.name
          setOnClickListener {
            onUserClicked(conversation.user)
          }
        }
        tvLikesNumber.text = conversation.user.likes.toString()
        imageView.apply {
          isVisible = conversation.user.image != null
          load(conversation.user.image)
        }
        tvLocation.text = conversation.location
        tvDate.text = Util.parseDate(conversation.datetime)

        if (onEditClicked == null) {
          btEdit.isVisible = false
        } else {
          btEdit.apply {
            isVisible = true
            setOnClickListener {
              onEditClicked.invoke(conversation)
            }
          }
        }
      }
    }

  }

}