package learning.rafaamo.languages.ui.view.content.profile

import learning.rafaamo.languages.data.datasource.entity.Conversation
import learning.rafaamo.languages.data.datasource.entity.User
import learning.rafaamo.languages.ui.view.content.IConversationItem

/**
 * Represents the user profile view state.
 * @param userLoading Indicates if the user fetch is in progress or not.
 * @param list Items that are displayed in the view. @see [ProfileItem] to see the different list types.
 * @param userError Text with the cause of the user fetch error.
 */
data class UiState(
  var userLoading: Boolean = false,
  var list: MutableList<ProfileItem>? = null,
  var userError: String? = null
)

/**
 * Represents the list types of the view.
 */
sealed class ProfileItem {
  class HeaderItem(
    val user: User, val appUser: Boolean, val onLikeClicked: () -> Unit
  ) : ProfileItem()

  object LoaderItem : ProfileItem()

  class ConversationItem(
    override val conversation: Conversation, override val onUserClicked: () -> Unit, override val onEditClicked: (() -> Unit)?
  ) : ProfileItem(), IConversationItem
}