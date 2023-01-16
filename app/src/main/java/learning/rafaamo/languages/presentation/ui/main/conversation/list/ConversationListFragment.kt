package learning.rafaamo.languages.presentation.ui.main.conversation.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import dagger.hilt.android.AndroidEntryPoint
import learning.rafaamo.languages.R
import learning.rafaamo.languages.databinding.FragmentConversationListBinding
import learning.rafaamo.languages.domain.datasource.remote.util.Resource
import learning.rafaamo.languages.presentation.ui.main.conversation.bottom_sheet.ConversationBottomSheet
import learning.rafaamo.languages.presentation.util.ItemDecorator

@AndroidEntryPoint
class ConversationListFragment: Fragment() {

  private val viewModel: ConversationListViewModel by viewModels()
  private lateinit var binding: FragmentConversationListBinding
  private var adapter: ConversationAdapter? = null

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
    binding = FragmentConversationListBinding.inflate(layoutInflater)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    viewLifecycleOwner.lifecycleScope.launchWhenStarted {
      viewModel.response.collect { response ->
        when (response) {
          is Resource.Load -> {
            binding.loader.show()
          }
          is Resource.Success -> {
            binding.tvMessage.isVisible = false
            onDataLoaded(response.data)
          }
          is Resource.Error -> {
            binding.loader.hide()
            binding.tvMessage.apply {
              text = getString(R.string.error_unknown)
              isVisible = true
            }
          }
        }
      }
    }

    viewLifecycleOwner.lifecycleScope.launchWhenStarted {
      viewModel.conversationToEdit.collect { conversation ->
        ConversationBottomSheet.newInstance(
          learning.rafaamo.languages.presentation.ui.main.conversation.bottom_sheet.Conversation(conversation.id, conversation.location, conversation.datetime)
        ).show(this@ConversationListFragment.childFragmentManager, ConversationBottomSheet.TAG)
      }
    }

    viewLifecycleOwner.lifecycleScope.launchWhenStarted {
      viewModel.userToRedirect.collect { user ->
        binding.root.findNavController().navigate(
          ConversationListFragmentDirections.actionProfileToUserDetail(user.id, user.name)
        )
      }
    }
  }

  private fun onDataLoaded(list: List<ConversationListViewModel.ConversationItem>) {
    binding.loader.hide()

    if (list.isEmpty()) {
      binding.tvMessage.apply {
        text = getString(R.string.fragment_conversation_list_empty_no_conversations)
        isVisible = true
      }
    } else {
      setList(list)
    }
  }

  private fun setList(list: List<ConversationListViewModel.ConversationItem>) {
    if (this@ConversationListFragment.adapter == null) {
      this@ConversationListFragment.adapter = ConversationAdapter()
      binding.list.apply {
        addItemDecoration(ItemDecorator(resources.getDimensionPixelSize(R.dimen.space_6)))
        adapter = this@ConversationListFragment.adapter
      }
    }

    binding.list.isVisible = true
    adapter!!.updateList(list)
  }

  override fun onDestroyView() {
    super.onDestroyView()
    adapter = null
  }

}