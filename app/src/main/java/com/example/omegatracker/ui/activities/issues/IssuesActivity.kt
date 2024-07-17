package com.example.omegatracker.ui.activities.issues

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.lifecycle.lifecycleScope
import com.example.omegatracker.OmegaTrackerApplication.Companion.appComponent
import com.example.omegatracker.databinding.ActivityIssuesBinding
import com.example.omegatracker.entity.Issue
import com.example.omegatracker.entity.Screens
import com.example.omegatracker.service.IssuesService
import com.example.omegatracker.service.IssuesServiceBinder
import com.example.omegatracker.ui.activities.auth.AuthActivity
import com.example.omegatracker.ui.activities.base.BaseActivity
import com.example.omegatracker.ui.activities.profile.ProfileActivity
import com.example.omegatracker.ui.activities.start.StartScreenActivity
import com.example.omegatracker.ui.activities.timer.IssueTimerActivity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

interface IssuesCallback {
    fun startIssue(issueEntities: List<Issue>, position: Int)
    fun showIssueInfoActivity(issueEntity: Issue)
}

class IssuesActivity : BaseActivity(), IssuesView, IssuesCallback {
    private lateinit var binding: ActivityIssuesBinding
    private val issuesPresenter: IssuesPresenter by providePresenter {
        IssuesPresenter()
    }
    private lateinit var serviceIntent: Intent
    private val userManager = appComponent.getUserManager()
    private val adapter = IssuesAdapter()

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIssuesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initialization()
    }

    override fun initialization() {
        serviceIntent = Intent(this, IssuesService::class.java)
        showProfileScreen()
        setAvatar(userManager.getUser()?.avatarUrl, binding.userAvatar)
        binding.rvIssuesList.adapter = adapter
        issuesPresenter.getIssuesList()
        bindService()
    }

    override fun bindService() {
        bindService(
            serviceIntent,
            connection,
            BIND_AUTO_CREATE
        )

        lifecycleScope.launch {
            _serviceControllerState.collect { controller ->
                if (controller != null) {
                    issuesPresenter.setController(controller)
                }
            }
        }
    }

    override fun updateIssueTimer(issueEntity: Issue) {
        adapter.onIssueTimerUpdated(issueEntity)
    }

//    override fun setAvatar(url: String?) {
//        Glide.with(this)
//            .asDrawable()
//            .load(url)
//            .transition(DrawableTransitionOptions.withCrossFade())
//            .into(object : ImageViewTarget<Drawable>(binding.userAvatar) {
//                override fun setResource(resource: Drawable?) {
//                    binding.userAvatar.setImageDrawable(resource)
//                }
//            })
//        val uri = url?.toUri()
//        Glide
//            .with(this)
//            .load(uri)
//            .placeholder(R.drawable.ic_launcher_omega_tracker)
//            .listener(object : RequestListener<Drawable> {
//                override fun onLoadFailed(
//                    e: GlideException?,
//                    model: Any?,
//                    target: Target<Drawable>,
//                    isFirstResource: Boolean
//                ): Boolean {
//                    try {
//                        GlideToVectorYou
//                            .init()
//                            .with(this@IssuesActivity)
//                            .load(uri, binding.userAvatar)
//                    } catch (e: Exception) {
//                        e.printStackTrace()
//                    }
//                    return true
//                }
//
//                override fun onResourceReady(
//                    resource: Drawable,
//                    model: Any,
//                    target: Target<Drawable>?,
//                    dataSource: DataSource,
//                    isFirstResource: Boolean
//                ): Boolean {
//                    return false
//                }
//
//            })
//            .into(binding.userAvatar)
//    }

    override fun setIssuesToRV(issueEntities: List<Issue>) {
        adapter.issuesList = issueEntities
        adapter.setCallback(this)
    }

    override fun showProfileScreen() {
        binding.userAvatar.setOnClickListener {
            val intent = Intent(this,ProfileActivity::class.java)
            startActivity(intent)
        }
    }

    override fun showScreen(screens: Screens) {
        val intent = when (screens) {
            Screens.AuthenticationScreens ->
                Intent(this, AuthActivity::class.java)

            Screens.IssuesScreens ->
                Intent(this, IssuesActivity::class.java)

            Screens.StartScreens ->
                Intent(this, StartScreenActivity::class.java)
        }
        startActivity(intent)
        finish()
    }

//    override fun showPopUpMenu(view: View) {
//        val popup = PopupMenu(this, view)
//        popup.inflate(R.menu.menu_issues)
//
//        popup.setOnMenuItemClickListener {
//            when (it!!.itemId) {
//                R.id.profileBtn -> {
//                    val intent = Intent(this,ProfileActivity::class.java)
//                    startActivity(intent)
//                }
//
//                R.id.logoutBtn -> {
//                    userManager.deleteUser()
//                    showScreen(Screens.AuthenticationScreens)
//                }
//            }
//            true
//        }
//        popup.show()
//    }

    override fun startIssue(issues: List<Issue>, position: Int) {
        issuesPresenter.sortIssues(issues, position)
        issuesPresenter.startIssue(issues[position])
    }

    override fun showIssueInfoActivity(issue: Issue) {
        val intent = Intent(this, IssueTimerActivity::class.java)
        intent.putExtra("issue_id",issue.id)
        startActivity(intent)
    }
}