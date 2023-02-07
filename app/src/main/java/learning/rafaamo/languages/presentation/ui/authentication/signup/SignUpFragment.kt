package learning.rafaamo.languages.presentation.ui.authentication.signup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import learning.rafaamo.languages.R
import learning.rafaamo.languages.domain.datasource.remote.util.*
import learning.rafaamo.languages.databinding.FragmentSignupBinding
import learning.rafaamo.languages.presentation.util.Util.cleanFormErrors
import learning.rafaamo.languages.presentation.util.Util.showFormErrors
import android.app.Activity
import android.view.inputmethod.InputMethodManager


@AndroidEntryPoint
class SignUpFragment : Fragment() {

  private val viewModel: SignUpViewModel by viewModels()
  private lateinit var binding: FragmentSignupBinding
  private val inputMethodManager: InputMethodManager by lazy { requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager }


  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
    binding = FragmentSignupBinding.inflate(layoutInflater).apply {
      form = viewModel.form
      loading = viewModel.loading
      lifecycleOwner = viewLifecycleOwner
    }
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    binding.btnSignup.setOnClickListener {
      viewModel.signUp()
    }

    viewLifecycleOwner.lifecycleScope.launchWhenStarted {
      viewModel.signUpResponse.collect { response ->
        when (response) {
          is Resource.Load -> {
            cleanFormErrors(binding.root)
          }
          is Resource.Success -> {}
          is Resource.Error -> {
            if (response.error?.code != null) {
              showFormErrors(binding.root, response.error.errors)
            } else {
              Snackbar.make(binding.root, getString(R.string.error_unknown), Snackbar.LENGTH_SHORT).show()
              inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
            }
          }
        }
      }
    }
  }

}