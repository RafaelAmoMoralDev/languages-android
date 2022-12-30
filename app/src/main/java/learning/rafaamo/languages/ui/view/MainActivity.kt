package learning.rafaamo.languages.ui.view

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.isVisible
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import dagger.hilt.android.AndroidEntryPoint
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

    val callback: OnBackPressedCallback = object : OnBackPressedCallback(true /* enabled by default */) {
      override fun handleOnBackPressed() {
        val backStackEntry: NavBackStackEntry? = navController.previousBackStackEntry
        if (backStackEntry != null) {
          println(backStackEntry.id)
          println(backStackEntry.destination)
        }
      }
    }
    onBackPressedDispatcher.addCallback(this, callback)

    navController = (supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment).navController
    if (appUserAuthentication.isUserLogged()) {
      navController.setGraph(R.navigation.main_graph)
    } else {
      navController.setGraph(R.navigation.authentication_graph)
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