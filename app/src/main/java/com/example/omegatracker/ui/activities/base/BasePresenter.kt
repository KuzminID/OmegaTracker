package com.example.omegatracker.ui.activities.base

import com.example.omegatracker.R
import com.omegar.mvp.MvpPresenter
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import retrofit2.HttpException
import java.net.UnknownHostException
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext

@Singleton
open class BasePresenter<V : BaseView> : MvpPresenter<V>(), CoroutineScope {

    private val supervisorJob = SupervisorJob()
    private val coroutineExceptionHandler =
        CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()

            when (throwable) {
                is HttpException -> {
                    viewState.showMessage(R.string.http_exception_msg)
                }

                is IllegalArgumentException -> {
                    viewState.showMessage(R.string.illegal_arg_msg)
                }

                is UnknownHostException -> {
                    viewState.showMessage(R.string.unknown_host_msg)
                }

                else -> {
                    viewState.showMessage(R.string.default_exception_msg)
                }
            }
        }
    override val coroutineContext: CoroutineContext =
        supervisorJob + Dispatchers.Main + coroutineExceptionHandler
}