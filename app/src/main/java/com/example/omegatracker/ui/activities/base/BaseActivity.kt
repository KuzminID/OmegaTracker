package com.example.omegatracker.ui.activities.base

import android.os.Handler
import android.widget.Toast
import androidx.lifecycle.LifecycleObserver
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
            Runnable
            {
                toast.cancel()
            }, 1000
        )
    }

    override fun initialization() {}
}