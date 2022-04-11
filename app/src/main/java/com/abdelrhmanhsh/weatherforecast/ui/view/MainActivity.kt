package com.abdelrhmanhsh.weatherforecast.ui.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.*
import com.abdelrhmanhsh.weatherforecast.databinding.ActivityMainBinding
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import androidx.lifecycle.lifecycleScope
import com.abdelrhmanhsh.weatherforecast.R
import com.abdelrhmanhsh.weatherforecast.util.UserPreferences
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    private lateinit var userPreferences: UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        userPreferences = UserPreferences(this)
        var language: String?

        lifecycleScope.launch {

            language = userPreferences.readLanguage()
            println("PREF LANGUAGE $language")

            if (language != getString(R.string.arabic) || language.isNullOrEmpty()){
                language = "en"
            } else {
                language = "ar"
            }

            val config = resources.configuration
            val locale = Locale(language!!)
            Locale.setDefault(locale)
            config.setLocale(locale)

            createConfigurationContext(config)
            resources.updateConfiguration(config, resources.displayMetrics)
            println("main language: $language")

            withContext(Main){
                binding = ActivityMainBinding.inflate(layoutInflater)

                setContentView(binding.root)

                setSupportActionBar(binding.toolbar)
                initNavDrawer()
                initNavGraph()

                firebaseAnalytics = Firebase.analytics
            }
        }
    }

    private fun initNavDrawer(){
        toggle = ActionBarDrawerToggle(this, binding.drawerLayout,
            R.string.nav_open,
            R.string.nav_close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }

    private fun initNavGraph(){
        navController = findNavController(R.id.nav_host_fragment)
        NavigationUI.setupWithNavController(binding.navView, navController)
        NavigationUI.setupActionBarWithNavController(this, navController, binding.drawerLayout)
        appBarConfiguration = AppBarConfiguration(navController.graph, binding.drawerLayout)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

}