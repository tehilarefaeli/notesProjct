package com.example.notesapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.notesapp.R
import com.example.notesapp.activities.ui.main.LoginFragment
import com.example.notesapp.databinding.ActivityMainBinding

class LoginActivity : AppCompatActivity() {

    private val navController by lazy {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_login) as NavHostFragment
        navHostFragment.navController
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val appBarConfiguartion = AppBarConfiguration(setOf(
            R.id.login, R.id.create_profile)
        )
        setupActionBarWithNavController(navController, appBarConfiguartion)


    }
}