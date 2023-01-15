package learning.rafaamo.languages.presentation.ui.main.profile

import android.os.Bundle
import android.view.*
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import learning.rafaamo.languages.R
import learning.rafaamo.languages.databinding.FragmentProfileBinding
import learning.rafaamo.languages.presentation.util.ItemDecorator
import kotlin.properties.Delegates

@AndroidEntryPoint
class ProfileFragment: Fragment() {

  lateinit var viewModel: ProfileViewModel
  private lateinit var binding: FragmentProfileBinding
  var isAppUserProfile by Delegates.notNull<Boolean>()
  private var adapter: ProfileAdapter? = null

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
    isAppUserProfile = findNavController().currentDestination!!.id == R.id.frag_profile
    viewModel = if (isAppUserProfile) {
      navGraphViewModels<ProfileViewModel>(R.id.main_graph) { defaultViewModelProviderFactory }.value
    } else {
      viewModels<ProfileViewModel>({ this }).value
    }
    binding = FragmentProfileBinding.inflate(layoutInflater)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    setupMenu()

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
                text = uiState.userError!!.userError?: getString(uiState.userError!!.userErrorRes!!)
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

  private fun setupMenu() {
    if (isAppUserProfile) {
      (requireActivity() as MenuHost).addMenuProvider(object : MenuProvider {
        override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
          menuInflater.inflate(R.menu.main_menu, menu)
        }

        override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
          return when (menuItem.itemId) {
            R.id.log_out -> {
              viewModel.logOut()
              findNavController().setGraph(R.navigation.authentication_graph)
              true
            }
            else -> false
          }
        }
      }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }
  }

  override fun onDestroyView() {
    super.onDestroyView()
    adapter = null
  }

}