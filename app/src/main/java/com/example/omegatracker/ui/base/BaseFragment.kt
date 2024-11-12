package com.example.omegatracker.ui.base

import android.graphics.drawable.Drawable
import android.os.Handler
import android.widget.ImageView
import android.widget.Toast
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.target.ImageViewTarget
import com.omegar.mvp.MvpAppCompatFragment

open class BaseFragment : MvpAppCompatFragment(), BaseFragmentView {

    private lateinit var toast: Toast

    override fun showMessage(msg: Int) {
        toast = Toast.makeText(requireActivity(), msg, Toast.LENGTH_SHORT)
        toast.show()
        val handler = Handler()
        handler.postDelayed(
            Runnable {
                toast.cancel()
            }, 1000
        )
    }

    override fun showMessageWithLongDuration(msg: Int) {
        toast = Toast.makeText(requireActivity(),msg,Toast.LENGTH_LONG)
        toast.show()
    }

    override fun hideMessage() {
        if (::toast.isInitialized) {
            toast.cancel()
        }
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

    override fun navigateUp() {
        requireActivity().findNavController(this.id).navigateUp()
    }

    override fun popBackStack() {
        requireActivity().findNavController(this.id).popBackStack()
    }


}