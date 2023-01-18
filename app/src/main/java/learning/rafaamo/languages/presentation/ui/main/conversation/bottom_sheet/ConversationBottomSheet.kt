package learning.rafaamo.languages.presentation.ui.main.conversation.bottom_sheet

import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.CalendarConstraints.DateValidator
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import learning.rafaamo.languages.databinding.BottomSheetConversationBinding
import learning.rafaamo.languages.domain.datasource.remote.util.*
import learning.rafaamo.languages.presentation.util.Util
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes


@AndroidEntryPoint
class ConversationBottomSheet: BottomSheetDialogFragment() {

  companion object {
    const val TAG = "conversation_bottom_sheet"
    const val CONVERSATION = "conversation"

    fun newInstance(conversation: Conversation?): ConversationBottomSheet = ConversationBottomSheet().apply {
      arguments = Bundle().apply {
        putParcelable(CONVERSATION, conversation)
      }
    }
  }

  private val viewModel: ConversationBottomSheetViewModel by viewModels()
  private lateinit var binding: BottomSheetConversationBinding

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
    binding = BottomSheetConversationBinding.inflate(inflater).apply {
      form = viewModel.form
      loading = viewModel.loading
      lifecycleOwner = viewLifecycleOwner
    }
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    binding.apply {
      textInputLayout.setEndIconOnClickListener {

        MaterialDatePicker.Builder.datePicker().apply {
          val dateMs = viewModel.form.getDateMs()
          if (dateMs != null) {
            setSelection(Util.getMSWithOffset(dateMs))
          }

          val dateValidator: DateValidator = DateValidatorPointForward.from(System.currentTimeMillis())
          val constraintsBuilder = CalendarConstraints.Builder().setValidator(dateValidator)
          setCalendarConstraints(constraintsBuilder.build())

          build().apply {
            show(this@ConversationBottomSheet.childFragmentManager, null)

            addOnPositiveButtonClickListener { dateMs ->
              MaterialTimePicker.Builder().apply {
                if (dateMs != null) {
                  setHour(Util.getHourFromMS(dateMs))
                  setMinute(Util.getMinuteFromMS(dateMs))
                }

                build().apply {
                  show(this@ConversationBottomSheet.childFragmentManager, null)

                  addOnPositiveButtonClickListener {
                    val timeMs = hour.hours.inWholeMilliseconds + minute.minutes.inWholeMilliseconds
                    viewModel.form.setDateMs(dateMs + timeMs)
                  }
                }
              }
            }
          }
        }
      }

      button.setOnClickListener {
        viewModel.save()
      }
    }

    viewLifecycleOwner.lifecycleScope.launchWhenStarted {
      viewModel.response.collect {
        if (it != null && it is Resource.Success) {
          dismiss()
        }
      }
    }
  }

}