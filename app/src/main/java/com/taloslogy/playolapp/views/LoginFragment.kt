package com.taloslogy.playolapp.views

import android.accounts.Account
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.GoogleAuthUtil
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.taloslogy.playolapp.R
import com.taloslogy.playolapp.models.LoginPayload
import com.taloslogy.playolapp.view_models.UserViewModel
import kotlinx.android.synthetic.main.fragment_login.*
import kotlin.concurrent.thread


/** @author Rangana Perera. @copyrights: Taloslogy PVT Ltd. */
class LoginFragment : Fragment() {

    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 9001
    private val userViewModel: UserViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val builder = AlertDialog.Builder(context)
        builder.setCancelable(false)
        builder.setView(R.layout.progress)
        val dialog = builder.create()

        userViewModel.loginCycle.observe(viewLifecycleOwner, Observer {
            when(it) {
                LoginPayload.LoginLoading -> dialog.show()
                LoginPayload.LoginError -> {
                    dialog.dismiss()
                    Toast.makeText(activity, getString(R.string.login_error), Toast.LENGTH_SHORT).show()
                }
                LoginPayload.LoginSuccess -> {
                    dialog.dismiss()
                    findNavController().navigate(R.id.action_userDetails)
                }
                LoginPayload.LoginWaiting -> {}
            }
        })

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.google_client_id))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        google_sign_in.setOnClickListener {
            signIn()
        }
    }

    private fun signIn() {
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(
            signInIntent, RC_SIGN_IN
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(
                ApiException::class.java
            )

            val googleFirstName = account?.givenName ?: ""
            val googleLastName = account?.familyName ?: ""
            val googleEmail = account?.email ?: ""

            val acnt = Account(googleEmail, "com.google")
            val scopes = "oauth2:profile email"

            thread {
                val token = GoogleAuthUtil.getToken(context, acnt, scopes)
                userViewModel.googleSignIn(googleFirstName, googleLastName, googleEmail, token)
            }

        } catch (e: ApiException) {
            // Sign in was unsuccessful
            Log.e(
                "failed code=", e.statusCode.toString()
            )
        }
    }

}