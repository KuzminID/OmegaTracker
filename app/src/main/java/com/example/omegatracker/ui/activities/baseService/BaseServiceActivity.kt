package com.example.omegatracker.ui.activities.baseService

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.example.omegatracker.service.IssuesService
import com.example.omegatracker.service.IssuesServiceBinder
import com.example.omegatracker.ui.activities.base.BaseActivity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

open class BaseServiceActivity : BaseActivity(), BaseServiceView {
    private lateinit var serviceIntent: Intent
    val serviceControllerState = MutableStateFlow<IssuesServiceBinder?>(null)
    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            service as IssuesServiceBinder
            serviceControllerState.value = service
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            serviceControllerState.value = null
        }
    }

    private var controller : IssuesServiceBinder? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        serviceIntent = Intent(this, IssuesService::class.java)
        startService(serviceIntent)
        bindService()
        Log.d("BaseServiceActivity",this.toString())
    }

    fun printThis() {
        Log.d("BaseServiceActivity",this.toString())
    }

    override fun initialization() {

        serviceIntent = Intent(this, IssuesService::class.java)
        startService(serviceIntent)
        bindService()
    }

    fun getController() : IssuesServiceBinder? {
        return controller
    }

    override fun bindService() {
        bindService(
            serviceIntent,
            connection,
            BIND_AUTO_CREATE
        )
    }
}