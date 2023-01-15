package learning.rafaamo.languages.presentation.ui.main

import learning.rafaamo.languages.domain.entity.Conversation

interface IConversationItem {

  val conversation: Conversation
  val onUserClicked: () -> Unit
  val onEditClicked: (() -> Unit)?

}