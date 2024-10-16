package com.example.omegatracker.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.omegatracker.databinding.ActivityAuthentificationBinding
import com.example.omegatracker.entity.HelperContent
import com.example.omegatracker.entity.Screens
import com.example.omegatracker.ui.base.BaseActivity
import com.example.omegatracker.ui.main.MainActivity
import com.example.omegatracker.ui.start.StartScreenActivity


class AuthActivity : BaseActivity(), AuthView {
    private lateinit var binding: ActivityAuthentificationBinding
    private val authPresenter: AuthPresenter by providePresenter {
        AuthPresenter()
    }
    private lateinit var authButton: Button
    private lateinit var authTextField: EditText
    private lateinit var urlTextField: EditText
    private val manager = LinearLayoutManager(this)
    private val adapter = HelperAdapter()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthentificationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initialization()
    }

    override fun initAuthButtonListener() {
        authButton.setOnClickListener {
            authPresenter.onAuthButtonClicked(
                authTextField.text.toString(),
                urlTextField.text.toString()
            )
        }
    }

    override fun initEditTextAction() {
        authTextField.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {

                authPresenter.onAuthButtonClicked(
                    authTextField.text.toString(),
                    urlTextField.text.toString()
                )
            }
            return@setOnEditorActionListener false
        }
    }

    override fun showScreen(screen: Screens) {
        val intent = when (screen) {
            Screens.AuthenticationScreens ->
                Intent(this, AuthActivity::class.java)

            Screens.IssuesScreens ->
                Intent(this, MainActivity::class.java)

            Screens.StartScreens ->
                Intent(this, StartScreenActivity::class.java)
        }
        startActivity(intent)
        finish()
    }


    override fun initialization() {
        authButton = binding.authorizationButton
        authTextField = binding.authorizationTokenEditText
        urlTextField = binding.serverEditText

        binding.bottomSheetToolbar.rvHelperContent.layoutManager = manager
        binding.bottomSheetToolbar.rvHelperContent.adapter = adapter

        initAuthButtonListener()
        initEditTextAction()

    }


    override fun setHelperData(data: List<HelperContent>) {
        adapter.data = data
    }

    override fun setSavedUrl(url: String) {
        urlTextField.setText(url)
    }
}