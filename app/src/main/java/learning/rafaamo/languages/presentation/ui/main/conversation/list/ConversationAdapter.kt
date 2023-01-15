package learning.rafaamo.languages.presentation.ui.main.conversation.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import learning.rafaamo.languages.domain.entity.Conversation
import learning.rafaamo.languages.domain.entity.user.IUser
import learning.rafaamo.languages.databinding.ViewHolderConversationBinding
import learning.rafaamo.languages.presentation.util.Util

class ConversationAdapter(
  private val onUserClicked: (IUser: IUser) -> Unit,
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
    private val onUserClicked: (IUser: IUser) -> Unit,
    private val onEditClicked: ((conversation: Conversation) -> Unit)?
  ): RecyclerView.ViewHolder(conversationView.root) {

    fun bind(conversation: Conversation) {
      conversationView.apply {
        tvUsername.apply {
          text = conversation.IUser.name
          setOnClickListener {
            onUserClicked(conversation.IUser)
          }
        }
        tvLikesNumber.text = conversation.IUser.likes.toString()
        imageView.apply {
          isVisible = conversation.IUser.image != null
          load(conversation.IUser.image)
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