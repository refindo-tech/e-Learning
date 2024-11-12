package com.example.kessekolah.ui.core

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.example.kessekolah.R
import com.example.kessekolah.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val navHostController = supportFragmentManager.findFragmentById(R.id.mainContainer) as NavHostFragment
        navController = navHostController.navController

        NavigationUI.setupWithNavController(binding.bottomNavigation, navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.homeFragment2,
                R.id.bookMarkFragment,
                R.id.profileFragment -> binding.bottomNavigation.visibility = View.VISIBLE

                else -> {
                    binding.bottomNavigation.visibility = View.GONE
                }
            }
        }


    }
}