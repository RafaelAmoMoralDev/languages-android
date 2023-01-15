package learning.rafaamo.languages.presentation.ui.main.profile

import androidx.annotation.StringRes
import learning.rafaamo.languages.domain.entity.Conversation
import learning.rafaamo.languages.domain.entity.user.IUser
import learning.rafaamo.languages.presentation.ui.main.IConversationItem

/**
 * Represents the user profile view state.
 * @param userLoading Indicates if the user fetch is in progress or not.
 * @param list Items that are displayed in the view. @see [ProfileItem] to see the different list types.
 * @param userError Error with the cause of the user fetch error. @see [UiStateError] to see the different error types.
 */
data class UiState(
  var userLoading: Boolean = false,
  var list: MutableList<ProfileItem>? = null,
  var userError: UiStateError? = null
)

/**
 * Represents the user fetch error.
 * @param userError Text with the error.
 * @param userErrorRes String Resource with the error.
 */
data class UiStateError(
  var userError: String? = null,
  @StringRes var userErrorRes: Int? = null
)

/**
 * Represents the list types of the view.
 */
sealed class ProfileItem {
  class HeaderItem(
    val user: IUser, val onLikeClicked: (() -> Unit)?
  ) : ProfileItem()

  object LoaderItem : ProfileItem()

  class ConversationItem(
    override val conversation: Conversation, override val onUserClicked: () -> Unit, override val onEditClicked: (() -> Unit)?
  ) : ProfileItem(), IConversationItem
}