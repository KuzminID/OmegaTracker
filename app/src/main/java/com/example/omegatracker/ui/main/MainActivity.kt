package com.example.omegatracker.ui.main

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.omegatracker.R
import com.example.omegatracker.databinding.ActivityMainBinding
import com.example.omegatracker.service.IssuesService
import com.example.omegatracker.service.IssuesServiceBinder
import com.example.omegatracker.ui.issues.FragmentCallback
import com.example.omegatracker.ui.issues.IssuesFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var bottomNavMenu: BottomNavigationView
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController
    private var callback = MutableStateFlow<FragmentCallback?>(null)

    private lateinit var serviceIntent: Intent
    val serviceControllerState = MutableStateFlow<IssuesServiceBinder?>(null)
    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            service as IssuesServiceBinder
            serviceControllerState.value = service

            //val fragment = supportFragmentManager.findFragmentById(R.id.issuesFragment) as IssuesFragment
            //fragment.setController(service)

            //Setting controller to fragment
            lifecycleScope.launch {
                callback.collect{
                    delay(3000)
                    it?.setController(service)
                }
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            serviceControllerState.value = null
            lifecycleScope.launch {
                callback.collect {
                    it?.detachController()
                }
            }

        }
    }

    fun setFragmentCallback(callback: FragmentCallback) {
        this.callback.value = callback
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bottomNavMenu = binding.bottomNavigationView
        navHostFragment = binding.fragmentContainerView.getFragment()
        navController = navHostFragment.navController
        bottomNavMenu.setupWithNavController(navController)

        initialization()
    }

    private fun initialization() {
        initializeService()
    }

    private fun initializeService() {
        serviceIntent = Intent(this, IssuesService::class.java)
        startService(serviceIntent)
        bindService()

        lifecycleScope.launch {
            serviceControllerState.collect {
                if (it != null) {
                    navController.navigate(R.id.action_loadingFragment_to_issuesFragment)
                    navController.graph.setStartDestination(R.id.issuesFragment)
                }
            }
        }
    }

    private fun bindService() {
        bindService(
            serviceIntent,
            connection,
            BIND_AUTO_CREATE
        )
    }
}