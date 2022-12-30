package learning.rafaamo.languages.ui.view.authentication.signin

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import learning.rafaamo.languages.R
import learning.rafaamo.languages.data.datasource.remote.util.*
import learning.rafaamo.languages.data.repository.UserRepository
import learning.rafaamo.languages.databinding.FragmentSigninBinding


@AndroidEntryPoint
class SignInFragment : Fragment() {

  private val viewModel: SignInViewModel by viewModels()
  private lateinit var binding: FragmentSigninBinding
  private val inputMethodManager: InputMethodManager by lazy { requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager }


  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
    binding = FragmentSigninBinding.inflate(layoutInflater).apply {
      form = viewModel.form
      loading = viewModel.loading
      lifecycleOwner = viewLifecycleOwner
    }
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    binding.apply {
      btnSignin.setOnClickListener {
        viewModel.signIn()
      }

      tvSignUp.setOnClickListener {
        findNavController().navigate(R.id.action_signin_to_signup)
      }
    }

    viewLifecycleOwner.lifecycleScope.launchWhenStarted {
      viewModel.signInResponse.collect { response ->
        when (response) {
          is Resource.Load -> {
            binding.apply {
              email.error = null
              password.error = null
            }
          }
          is Resource.Success -> {
            findNavController().setGraph(R.navigation.main_graph)
          }
          is Resource.Error -> {
            when (response.error?.code) {
              UserRepository.SignInError.Code.USER_NOT_FOUND.code -> {
                binding.email.error = getString(R.string.error_user_not_found)
              }
              UserRepository.SignInError.Code.INVALID_PASSWORD.code -> {
                binding.password.error = getString(R.string.error_invalid_password)
              }
              else -> {
                Snackbar.make(binding.root, getString(R.string.error_unknown), Snackbar.LENGTH_SHORT).show()
                inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
              }
            }
          }
        }
      }
    }
  }

}