package com.example.omegatracker.ui.activities.base

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Handler
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.LifecycleObserver
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.target.ImageViewTarget
import com.example.omegatracker.service.IssuesService
import com.omegar.mvp.MvpAppCompatActivity
import javax.inject.Singleton

@Singleton
open class BaseActivity : MvpAppCompatActivity(), BaseView, LifecycleObserver {
    private lateinit var toast: Toast

    override fun showMessage(msg: Int) {
        toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT)
        toast.show()
        val handler = Handler()
        handler.postDelayed(
            Runnable {
                toast.cancel()
            }, 1000
        )
    }

    override fun setAvatar(url: String?, iv: ImageView) {
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

    override fun initialization() {
        val serviceIntent = Intent(this, IssuesService::class.java)
        startService(serviceIntent)
    }
}