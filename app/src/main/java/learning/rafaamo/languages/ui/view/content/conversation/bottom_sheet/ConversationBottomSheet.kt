package learning.rafaamo.languages.ui.view.content.conversation.bottom_sheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.CalendarConstraints.DateValidator
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import dagger.hilt.android.AndroidEntryPoint
import learning.rafaamo.languages.data.datasource.remote.util.*
import learning.rafaamo.languages.databinding.BottomSheetConversationBinding
import learning.rafaamo.languages.ui.util.Util
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes


@AndroidEntryPoint
class ConversationBottomSheet: BottomSheetDialogFragment() {

  companion object {
    const val TAG = "conversation_bottom_sheet"
    const val CONVERSATION = "conversation"

    fun newInstance(conversation: Conversation): ConversationBottomSheet = ConversationBottomSheet().apply {
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
          setSelection(Util.getMSWithOffset(viewModel.form.getDateMs()))

          val dateValidator: DateValidator = DateValidatorPointForward.from(System.currentTimeMillis())
          val constraintsBuilder = CalendarConstraints.Builder().setValidator(dateValidator)
          setCalendarConstraints(constraintsBuilder.build())

          build().apply {
            show(this@ConversationBottomSheet.childFragmentManager, null)

            addOnPositiveButtonClickListener { dateMs ->
              MaterialTimePicker.Builder()
                .setHour(Util.getHourFromMS(viewModel.form.getDateMs()))
                .setMinute(Util.getMinuteFromMS(viewModel.form.getDateMs()))
                .build().apply {
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