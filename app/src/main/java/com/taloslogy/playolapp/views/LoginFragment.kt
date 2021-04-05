package com.taloslogy.playolapp.views

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.taloslogy.playolapp.R
import kotlinx.android.synthetic.main.fragment_login.*

/** @author Rangana Perera. @copyrights: Taloslogy PVT Ltd. */
class LoginFragment : Fragment() {

    lateinit var mGoogleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 9001

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("615255787291-uhh8vmdh76bc1qeurojl11bq8j7rs56f.apps.googleusercontent.com")
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        google_sign_in.setOnClickListener {
            findNavController().navigate(R.id.action_userDetails)
//            signIn()
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
            val task =
                GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(
                ApiException::class.java
            )
            // Signed in successfully
            val googleId = account?.id ?: ""
            Log.i("Google ID",googleId)

            val googleFirstName = account?.givenName ?: ""
            Log.i("Google First Name", googleFirstName)

            val googleLastName = account?.familyName ?: ""
            Log.i("Google Last Name", googleLastName)

            val googleEmail = account?.email ?: ""
            Log.i("Google Email", googleEmail)

            val googleProfilePicURL = account?.photoUrl.toString()
            Log.i("Google Profile Pic URL", googleProfilePicURL)

            val googleIdToken = account?.idToken ?: ""
            Log.i("Google ID Token", googleIdToken)

        } catch (e: ApiException) {
            // Sign in was unsuccessful
            Log.e(
                "failed code=", e.statusCode.toString()
            )
        }
    }

}