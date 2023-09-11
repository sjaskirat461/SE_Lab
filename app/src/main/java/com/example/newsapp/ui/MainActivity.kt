package com.example.newsapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavAction
import androidx.navigation.NavOptions
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.newsapp.R
import com.example.newsapp.databinding.ActivityMainBinding
import com.example.newsapp.viewmodels.NewsViewModel

class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    private val viewModel: NewsViewModel by lazy {
        val activity = requireNotNull(this){
            "You can only access the viewModel after this activity is created"
        }
        ViewModelProvider(
            this,
            NewsViewModel.Factory(
                activity.application
            )
        )[NewsViewModel::class.java]
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = DataBindingUtil.setContentView(this@MainActivity, R.layout.activity_main)
        binding.bottomNavigationView.background = null
        binding.bottomNavigationView.menu.getItem(1).isEnabled = false
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.main_host_fr) as NavHostFragment
        val navController = navHostFragment.navController

        binding.bottomNavigationView.setupWithNavController(navController)

        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                (R.id.breakingNewsFragment) -> {
                    navController.popBackStack()
                    navController.navigate(R.id.breakingNewsFragment)
                    true
                }
                (R.id.favouriteNewsFragment) -> {
                    navController.popBackStack()
                    navController.navigate(R.id.favouriteNewsFragment)
                    true
                }
                else -> false
            }
        }

        binding.searchFab?.setOnClickListener {
            navController.popBackStack()
            navController.navigate(R.id.searchFragment)
//            val fragment = SearchFragment()
//            supportFragmentManager.commit {
//                replace(R.id.main_host_fr, fragment)
//                addToBackStack("search")
//            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}