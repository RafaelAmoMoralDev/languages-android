package learning.rafaamo.languages.presentation.util

import android.content.res.ColorStateList
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.BindingAdapter
import coil.load
import com.google.android.material.textfield.TextInputLayout
import learning.rafaamo.languages.R
import learning.rafaamo.languages.domain.entity.ErrorResponse
import java.time.*
import java.time.format.DateTimeFormatter
import kotlin.time.Duration.Companion.days

object Util {

  fun parseDate(ms: Long, applyTimeZone: Boolean = true): String {
    val timeOffset = if (applyTimeZone) {
      OffsetDateTime.now().offset
    } else {
      ZoneOffset.UTC
    }
    val now: LocalDateTime = LocalDateTime.ofEpochSecond(ms / 1000, 0, timeOffset)
    val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    return now.format(formatter)
  }

  fun parseDate(dateTime: String, applyTimeZone: Boolean = true): Long {
    val localDateTime = LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
    val timeOffset = if (applyTimeZone) {
      OffsetDateTime.now().offset
    } else {
      ZoneOffset.UTC
    }
    return localDateTime.atZone(timeOffset).toInstant().toEpochMilli()
  }

  fun getMSWithOffset(ms: Long): Long {
    val local = LocalDateTime.ofEpochSecond(ms / 1000, 0, OffsetDateTime.now().offset)
    return local.atZone(ZoneOffset.UTC).toInstant().toEpochMilli()
  }

  fun getHourFromMS(ms: Long): Int {
    return LocalDateTime.ofEpochSecond(ms / 1000, 0, OffsetDateTime.now().offset).hour
  }

  fun getMinuteFromMS(ms: Long): Int {
    return LocalDateTime.ofEpochSecond(ms / 1000, 0, OffsetDateTime.now().offset).minute
  }

  fun getLastWeekMs(ms: Long = System.currentTimeMillis()): Long {
    return ms - 7.days.inWholeMilliseconds
  }

  fun getNextWeekMs(ms: Long = System.currentTimeMillis()): Long {
    return ms + 7.days.inWholeMilliseconds
  }

  fun showFormErrors(rootView: View, errors: List<ErrorResponse>) {
    errors.forEach {
      rootView.findViewById<TextInputLayout>(
        rootView.context.resources.getIdentifier(
          it.type, "id", rootView.context.packageName
        )
      ).error = it.causes.joinToString(", ")
    }
  }

  fun cleanFormErrors(rootView: View) {
    if (rootView is ViewGroup) {
      for (i in 0 until rootView.childCount) {
        val view = rootView.getChildAt(i)
        if (view is TextInputLayout) {
          view.error = null
        }
        cleanFormErrors(view)
      }
    }
  }

  @BindingAdapter("imageUrl")
  @JvmStatic
  fun ImageView.loadImage(url: String?) {
    if (url != null) {
      load(url)
    } else {
      setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.ic_baseline_account_circle_24, null))
      imageTintList = ColorStateList.valueOf(ResourcesCompat.getColor(resources, R.color.md_theme_light_onSurfaceVariant, null))
    }
  }

}