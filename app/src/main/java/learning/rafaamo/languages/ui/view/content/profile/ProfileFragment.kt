package learning.rafaamo.languages.ui.view.content.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import learning.rafaamo.languages.R
import learning.rafaamo.languages.databinding.FragmentProfileBinding
import learning.rafaamo.languages.ui.util.ItemDecorator

@AndroidEntryPoint
class ProfileFragment: Fragment() {

  lateinit var viewModel: ProfileViewModel
  private lateinit var binding: FragmentProfileBinding
  private var adapter: ProfileAdapter? = null

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
    val isShownInNavigation = findNavController().currentDestination!!.id == R.id.frag_profile
    viewModel = if (isShownInNavigation) {
      navGraphViewModels<ProfileViewModel>(R.id.main_graph) { defaultViewModelProviderFactory }.value
    } else {
      viewModels<ProfileViewModel>({ this }).value
    }
    binding = FragmentProfileBinding.inflate(layoutInflater)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    viewLifecycleOwner.lifecycleScope.launchWhenStarted {
      viewModel.uiState.collect { uiState ->
        when {
          uiState.userLoading -> {
            binding.loader.show()
          }
          uiState.list != null -> {
            setList(uiState.list!!)
            binding.apply {
              loader.hide()
              list.isVisible = true
            }
          }
          uiState.userError != null -> {
            binding.apply {
              loader.hide()
              tvError.apply {
                isVisible = true
                text = uiState.userError
              }
            }
          }
        }
      }
    }

    viewLifecycleOwner.lifecycleScope.launchWhenStarted {
      viewModel.userToRedirect.collect { user ->
        binding.root.findNavController().navigate(
          ProfileFragmentDirections.actionProfileToUserDetail(user.id, user.name)
        )
      }
    }

    viewLifecycleOwner.lifecycleScope.launchWhenStarted {
      viewModel.fetchConversationsError.collect {
        Snackbar.make(binding.root, getString(R.string.fragment_profile_error_conversations), Snackbar.LENGTH_SHORT).show()
      }
    }
  }

  private fun setList(list: List<ProfileItem>) {
    if (adapter == null) {
      adapter = ProfileAdapter(list, viewLifecycleOwner)

      binding.list.apply {
        addItemDecoration(ItemDecorator(resources.getDimensionPixelSize(R.dimen.space_6)))
        adapter = this@ProfileFragment.adapter
      }
    } else {
      adapter!!.updateList(list)
    }
  }

  override fun onDestroyView() {
    super.onDestroyView()
    adapter = null
  }

}