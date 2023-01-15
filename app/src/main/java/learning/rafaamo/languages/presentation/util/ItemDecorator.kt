package learning.rafaamo.languages.presentation.util

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView


class ItemDecorator(private val size: Int, val shouldAddHorizontalSpace: Boolean = false) : RecyclerView.ItemDecoration() {

  override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
    val position = parent.getChildAdapterPosition(view)
    val isFirstPosition = position == 0

    if (!isFirstPosition) {
      val itemCount = state.itemCount
      val isLastPosition = position == (itemCount - 1)

      // Saved size
      val sizeBasedOnEdge = size
      val sizeBasedOnFirstPosition = if (isFirstPosition) sizeBasedOnEdge else size
      val sizeBasedOnLastPosition = if (isLastPosition) sizeBasedOnEdge else 0

      // Update properties
      with(outRect) {
        if (shouldAddHorizontalSpace) {
          left = sizeBasedOnEdge
          right = sizeBasedOnEdge
        }
        top = sizeBasedOnFirstPosition
        bottom = sizeBasedOnLastPosition
      }
    }
  }

}