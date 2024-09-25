package com.example.omegatracker.ui.activities.baseService

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.lifecycle.lifecycleScope
import com.example.omegatracker.service.IssuesService
import com.example.omegatracker.service.IssuesServiceBinder
import com.example.omegatracker.ui.activities.base.BaseActivity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

open class BaseServiceActivity : BaseActivity(), BaseServiceView {
    private lateinit var serviceIntent: Intent
    private val _serviceControllerState = MutableStateFlow<IssuesServiceBinder?>(null)
    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            service as IssuesServiceBinder
            _serviceControllerState.value = service
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            _serviceControllerState.value = null
        }
    }

    private val presenter: BaseServicePresenter<BaseServiceView> by providePresenter {
        BaseServicePresenter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        println("111111")
        super.onCreate(savedInstanceState)
        println("22222222")
        serviceIntent = Intent(this, IssuesService::class.java)
        startService(serviceIntent)
        bindService()
    }

    override fun initialization() {

        serviceIntent = Intent(this, IssuesService::class.java)
        startService(serviceIntent)
        bindService()
    }

    override fun bindService() {
        println("here")
        bindService(
            serviceIntent,
            connection,
            BIND_AUTO_CREATE
        )
        lifecycleScope.launch {
            _serviceControllerState.collect { controller ->
                if (controller != null) {
                    println("Controller setting")
                    presenter.controller
                    println(presenter.controller)
                }
            }
        }
    }
}