package learning.rafaamo.languages.presentation.ui.main

import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import coil.load
import learning.rafaamo.languages.databinding.ViewHolderConversationBinding
import learning.rafaamo.languages.presentation.util.Util

class ConversationViewHolder(private val conversationView: ViewHolderConversationBinding): RecyclerView.ViewHolder(conversationView.root) {

  fun bind(conversationItem: IConversationItem) {
    val conversation = conversationItem.conversation

    conversationView.apply {
      tvUsername.apply {
        text = conversation.IUser.name
        setOnClickListener {
          conversationItem.onUserClicked()
        }
      }
      tvLikesNumber.text = conversation.IUser.likes.toString()
      imageView.apply {
        isVisible = conversation.IUser.image != null
        load(conversation.IUser.image)
      }
      tvLocation.text = conversation.location
      tvDate.text = Util.parseDate(conversation.datetime)

      if (conversationItem.onEditClicked == null) {
        btEdit.isVisible = false
      } else {
        btEdit.apply {
          isVisible = true
          setOnClickListener {
            conversationItem.onEditClicked?.invoke()
          }
        }
      }
    }
  }

}