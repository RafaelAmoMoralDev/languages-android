package learning.rafaamo.languages.presentation.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import learning.rafaamo.languages.R
import learning.rafaamo.languages.common.AppAuthentication
import learning.rafaamo.languages.databinding.ActivityMainBinding
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

  @Inject lateinit var appUserAuthentication: AppAuthentication
  private lateinit var binding: ActivityMainBinding
  private lateinit var navController: NavController
  private lateinit var appBarConfiguration: AppBarConfiguration

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    installSplashScreen()

    binding = ActivityMainBinding.inflate(layoutInflater)
    setSupportActionBar(binding.toolbar)
    setContentView(binding.root)

    navController = (supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment).navController

    lifecycleScope.launch {
      repeatOnLifecycle(Lifecycle.State.STARTED) {
        appUserAuthentication.logged.collect { logged ->
          if (logged == true) {
            navController.setGraph(R.navigation.main_graph)
          } else {
            navController.setGraph(R.navigation.authentication_graph)
          }
        }
      }
    }

    navController.addOnDestinationChangedListener { controller, _, _ ->
      if (controller.graph.id == R.id.main_graph) {
        binding.apply {
          toolbar.isVisible = true
          bottomNavigationView.apply {
            isVisible = true
            setupWithNavController(navController)
          }
        }
        appBarConfiguration = AppBarConfiguration(setOf(R.id.frag_list, R.id.frag_profile))
        setupActionBarWithNavController(navController, appBarConfiguration)
      } else {
        binding.apply {
          toolbar.isVisible = false
          bottomNavigationView.isVisible = false
        }
      }
    }
  }

  override fun onSupportNavigateUp(): Boolean {
    return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
  }

}