package com.example.omegatracker.ui.activities.base

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.IBinder
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.target.ImageViewTarget
import com.example.omegatracker.service.IssuesService
import com.example.omegatracker.service.IssuesServiceBinder
import com.omegar.mvp.MvpAppCompatActivity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Singleton

@Singleton
open class BaseActivity : MvpAppCompatActivity(), BaseView, LifecycleObserver {
    private lateinit var toast: Toast

//    private val connection = object : ServiceConnection {
////        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
////            service as IssuesServiceBinder
////            _serviceControllerState.value = service
////        }
////
////        override fun onServiceDisconnected(name: ComponentName?) {
////            _serviceControllerState.value = null
////        }
////    }

    override fun showMessage(msg: Int) {
        toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT)
        toast.show()
        val handler = Handler()
        handler.postDelayed(
            Runnable
            {
                toast.cancel()
            }, 1000
        )
    }

//    override fun bindService() {
//        val intent = Intent(this, this::class.java)
//        bindService(
//            intent,
//            connection,
//            BIND_AUTO_CREATE
//        )
//    }

    override fun setAvatar(url: String?, iv : ImageView) {
        println(url)
        Glide.with(this)
            .asDrawable()
            .load(url)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(object : ImageViewTarget<Drawable>(iv) {
                override fun setResource(resource: Drawable?) {
                    iv.setImageDrawable(resource)
                }
            })
    }

//    override fun createIntentForChildClass(context: Context) : Intent {
//        return Intent(this ,this::class.java)
//    }

    override fun initialization() {}
}