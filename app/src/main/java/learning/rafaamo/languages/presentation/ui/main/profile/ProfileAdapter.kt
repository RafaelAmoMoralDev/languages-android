package learning.rafaamo.languages.presentation.ui.main.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import learning.rafaamo.languages.R
import learning.rafaamo.languages.databinding.UserBinding
import learning.rafaamo.languages.databinding.ViewHolderConversationBinding
import learning.rafaamo.languages.databinding.ViewHolderLoaderBinding
import learning.rafaamo.languages.domain.entity.user.AppUser
import learning.rafaamo.languages.domain.entity.user.User
import learning.rafaamo.languages.presentation.ui.main.ConversationViewHolder
import learning.rafaamo.languages.presentation.ui.main.IConversationItem
import learning.rafaamo.languages.presentation.ui.main.profile.ProfileItem.*

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

    fun bind(headerItem: HeaderItem) {
      userView.apply {
        lifecycleOwner = viewLifeCycleOwner
        this.user = headerItem.user
        executePendingBindings()

        btnLike.apply {
          if (headerItem.user is AppUser) {
            isVisible = false
          } else {
            text = context.resources.getQuantityString(R.plurals._user_likes, headerItem.user.likes, headerItem.user.likes)
            isChecked = (headerItem.user as User).liked
            setOnClickListener {
              headerItem.onLikeClicked!!.invoke()
            }
            isVisible = true
          }
        }
      }
    }

    fun updateLikeButton(payload: Payload.Like) {
      val headerItem = payload.headerItem
      userView.apply {
        btnLike.apply {
          text = context.resources.getQuantityString(R.plurals._user_likes, headerItem.user.likes, headerItem.user.likes)
          isChecked = (headerItem.user as User).liked
          setOnClickListener {
            headerItem.onLikeClicked!!.invoke()
          }
        }
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

}