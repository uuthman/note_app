package com.androiddevs.ktornoteapp.ui.auth

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.androiddevs.ktornoteapp.R
import com.androiddevs.ktornoteapp.data.remote.BasicAuthInterceptor
import com.androiddevs.ktornoteapp.other.Constants.KEY_LOGGED_IN_EMAIL
import com.androiddevs.ktornoteapp.other.Constants.KEY_PASSWORD
import com.androiddevs.ktornoteapp.other.Constants.NO_EMAIL
import com.androiddevs.ktornoteapp.other.Constants.NO_PASSWORD
import com.androiddevs.ktornoteapp.other.Status
import com.androiddevs.ktornoteapp.ui.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_auth.*
import javax.inject.Inject

@AndroidEntryPoint
class AuthFragment : BaseFragment(R.layout.fragment_auth) {

    private val viewModel: AuthViewModel by viewModels()

    @Inject
    lateinit var sharedPref: SharedPreferences

    @Inject
    lateinit var basicAuthInterceptor: BasicAuthInterceptor

    private var curEmail: String? = null
    private var curPassword: String? = null

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(isLoggedIn()){
            authenticateApi(curEmail ?: "",curPassword ?: "")
            redirectLogin()
        }
        requireActivity().requestedOrientation = SCREEN_ORIENTATION_PORTRAIT
        subscribeToObservers()
        btnRegister.setOnClickListener {
            val email = etRegisterEmail.text.toString()
            val password = etRegisterPassword.text.toString()
            val confirmedPassword = etRegisterPasswordConfirm.text.toString()
            viewModel.register(email,password,confirmedPassword)
        }

        btnLogin.setOnClickListener {
            val email = etLoginEmail.text.toString()
            val password = etLoginPassword.text.toString()
            curEmail = email
            curPassword = password
            viewModel.login(email,password)
        }

    }

    private fun isLoggedIn(): Boolean{
        curEmail = sharedPref.getString(KEY_LOGGED_IN_EMAIL, NO_EMAIL) ?: NO_EMAIL
        curPassword = sharedPref.getString(KEY_PASSWORD, NO_PASSWORD) ?: NO_PASSWORD
        return curEmail != NO_EMAIL && curPassword != NO_PASSWORD
    }

    private fun authenticateApi(email:String,password: String){
        basicAuthInterceptor.email = email
        basicAuthInterceptor.password = password
    }

    private fun redirectLogin(){
        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.authFragment,true)
            .build()

        findNavController().navigate(
            AuthFragmentDirections.actionAuthFragmentToNoteFragment(),
            navOptions
        )
    }

    private fun subscribeToObservers(){

        viewModel.loginStatus.observe(viewLifecycleOwner, Observer {
                result ->
            when(result.status){
                Status.LOADING -> {
                    registerProgressBar.visibility = View.VISIBLE
                }
                Status.ERROR -> {
                    registerProgressBar.visibility = View.GONE
                    showSnackbar(result.data ?: "An unknown error occurred")
                }
                Status.SUCCESS -> {
                    registerProgressBar.visibility = View.GONE
                    showSnackbar(result.data ?: "Successfully logged in")
                    sharedPref.edit().putString(KEY_LOGGED_IN_EMAIL,curEmail).apply()
                    sharedPref.edit().putString(KEY_PASSWORD,curPassword).apply()
                    authenticateApi(curEmail ?: "",curPassword ?: "")
                    redirectLogin()
                }
            }
        })

        viewModel.registerStatus.observe(viewLifecycleOwner, Observer {
            result ->
            when(result.status){
                Status.LOADING -> {
                    registerProgressBar.visibility = View.VISIBLE
                }
                Status.ERROR -> {
                    registerProgressBar.visibility = View.GONE
                    showSnackbar(result.data ?: "An unknown error occurred")
                }
                Status.SUCCESS -> {
                    registerProgressBar.visibility = View.GONE
                    showSnackbar(result.message ?: "Successfully registered an account")
                }
            }
        })
    }
}