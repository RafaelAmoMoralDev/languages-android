package learning.rafaamo.languages.ui.view.content

import learning.rafaamo.languages.data.datasource.entity.Conversation

interface IConversationItem {

  val conversation: Conversation
  val onUserClicked: () -> Unit
  val onEditClicked: (() -> Unit)?

}