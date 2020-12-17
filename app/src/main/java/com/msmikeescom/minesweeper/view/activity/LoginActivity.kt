package com.msmikeescom.minesweeper.view.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.msmikeescom.minesweeper.R
import com.msmikeescom.minesweeper.utilities.Constants.RC_SIGN_IN
import com.msmikeescom.minesweeper.utilities.Constants.RC_SIGN_OUT
import com.msmikeescom.minesweeper.utilities.SharePreferencesHelper
import com.msmikeescom.minesweeper.viewmodel.LoginViewModel


class LoginActivity : AppCompatActivity() {

    private lateinit var mGoogleSignInClient : GoogleSignInClient
    private lateinit var mSignInButton : SignInButton
    private lateinit var mSignInInstruction : TextView
    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)

        mSignInButton = findViewById(R.id.sign_in_button)
        mSignInButton.setSize(SignInButton.SIZE_WIDE);
        mSignInButton.setOnClickListener {
            when (it.id) {
                R.id.sign_in_button -> signIn()
            }
        }
        mSignInInstruction = findViewById(R.id.sign_in_instruction)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    override fun onStart() {
        super.onStart()
        val account = GoogleSignIn.getLastSignedInAccount(this)
        updateUI(account)
    }

    private fun updateUI(googleSignInAccount: GoogleSignInAccount?) {
        if (googleSignInAccount == null) {
            mSignInInstruction.visibility = View.VISIBLE
            mSignInButton.visibility = View.VISIBLE
        } else {
            SharePreferencesHelper.getInstance().putCurrentUserId(googleSignInAccount.id)
            viewModel.saveUserData(googleSignInAccount.displayName, googleSignInAccount.email, googleSignInAccount.photoUrl.toString(), googleSignInAccount.id)
            val intent = Intent(this, TabbedActivity::class.java)
            startActivityForResult(intent, RC_SIGN_OUT)
        }
    }

    private fun signIn() {
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this) {
                    Toast.makeText(this, "Successfully log out!", Toast.LENGTH_LONG).show()
                }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            RC_SIGN_IN -> { handleSignInResult(GoogleSignIn.getSignedInAccountFromIntent(data)) }
            RC_SIGN_OUT -> signOut()
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            updateUI(account)
        } catch (e: ApiException) {
            updateUI(null)
        }
    }
}