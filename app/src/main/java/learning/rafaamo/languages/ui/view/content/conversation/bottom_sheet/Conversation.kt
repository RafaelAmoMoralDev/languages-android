package learning.rafaamo.languages.ui.view.content.conversation.bottom_sheet

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Conversation (
  val id: Long,
  val location: String,
  val datetime: Long
): Parcelable