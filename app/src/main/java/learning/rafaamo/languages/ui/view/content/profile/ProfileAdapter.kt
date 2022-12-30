package learning.rafaamo.languages.ui.view.content.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import learning.rafaamo.languages.databinding.UserBinding
import learning.rafaamo.languages.databinding.ViewHolderConversationBinding
import learning.rafaamo.languages.databinding.ViewHolderLoaderBinding
import learning.rafaamo.languages.ui.util.Util
import learning.rafaamo.languages.ui.view.content.IConversationItem
import learning.rafaamo.languages.ui.view.content.profile.ProfileItem.*

class ProfileAdapter(
  private var list: List<ProfileItem>,
  private val viewLifeCycleOwner: LifecycleOwner
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

  private enum class TYPE { HEAD, LOADER, CONVERSATION }

  override fun getItemCount(): Int = list.size

  override fun getItemViewType(position: Int): Int {
    return when (list[position]){
      is HeaderItem -> TYPE.HEAD.ordinal
      is LoaderItem -> TYPE.LOADER.ordinal
      is ConversationItem -> TYPE.CONVERSATION.ordinal
    }
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
    return when (viewType) {
      TYPE.HEAD.ordinal -> {
        ProfileViewHolder(UserBinding.inflate(LayoutInflater.from(parent.context), parent, false), viewLifeCycleOwner)
      }
      TYPE.LOADER.ordinal -> {
        LoaderViewHolder(ViewHolderLoaderBinding.inflate(LayoutInflater.from(parent.context), parent, false))
      }
      else -> {
        ConversationViewHolder(
          ViewHolderConversationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
      }
    }
  }

  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    when (holder) {
      is ProfileViewHolder -> holder.bind(list[position] as HeaderItem)
      is LoaderViewHolder -> holder.bind()
      is ConversationViewHolder -> holder.bind(list[position] as IConversationItem)
    }
  }

  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, payloads: MutableList<Any>) {
    if (payloads.isNotEmpty()) {
      payloads.forEach { payload ->
        if (payload is Payload.Like) {
          (holder as ProfileViewHolder).updateLikeButton(payload)
        }
      }
    } else {
      super.onBindViewHolder(holder, position, payloads)
    }
  }

  fun updateList(updatedList: List<ProfileItem>) {
    val diffResult = DiffUtil.calculateDiff(ProfileDiffUtil(list, updatedList))
    list = updatedList
    diffResult.dispatchUpdatesTo(this)
  }

  sealed class Payload {
    class Like(val headerItem: HeaderItem): Payload()
  }

  class ProfileViewHolder(
    private val userView: UserBinding,
    private val viewLifeCycleOwner: LifecycleOwner
  ): RecyclerView.ViewHolder(userView.root) {

    lateinit var headerItem: HeaderItem

    fun bind(headerItem: HeaderItem) {
      this.headerItem = headerItem

      userView.apply {
        lifecycleOwner = viewLifeCycleOwner
        this.user = headerItem.user
        executePendingBindings()

        btnLike.apply {
          isVisible = !headerItem.appUser
          setOnClickListener {
            this@ProfileViewHolder.headerItem.onLikeClicked()
          }
        }
      }
    }

    fun updateLikeButton(payload: Payload.Like) {
      userView.apply {
        this.user = payload.headerItem.user
        executePendingBindings()

        this@ProfileViewHolder.headerItem = payload.headerItem
      }
    }

  }

  class LoaderViewHolder(private val loaderView: ViewHolderLoaderBinding): RecyclerView.ViewHolder(loaderView.root) {

    fun bind() {
      loaderView.apply {
        loader.show()
      }
    }

  }

  // TODO: Unificar ambos ConversationViewHolder
  class ConversationViewHolder(private val conversationView: ViewHolderConversationBinding): RecyclerView.ViewHolder(conversationView.root) {

    fun bind(conversationItem: IConversationItem) {
      val conversation = conversationItem.conversation

      conversationView.apply {
        tvUsername.apply {
          text = conversation.user.name
          setOnClickListener {
            conversationItem.onUserClicked()
          }
        }
        tvLikesNumber.text = conversation.user.likes.toString()
        imageView.apply {
          isVisible = conversation.user.image != null
          load(conversation.user.image)
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

}