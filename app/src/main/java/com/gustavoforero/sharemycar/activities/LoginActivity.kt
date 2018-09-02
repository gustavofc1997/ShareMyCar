package com.gustavoforero.sharemycar.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.gustavoforero.sharemycar.R
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.toast
import java.util.*


class LoginActivity : AppCompatActivity(), FacebookCallback<LoginResult>, OnCompleteListener<AuthResult> {
    override fun onComplete(task: Task<AuthResult>) {
        if (task.isSuccessful) {
            // Sign in success, update UI with the signed-in user's information
            Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            // If sign in fails, display a message to the user.
            toast("Ha ocurrido un error intenta nuevamente")
        }
    }

    lateinit var callbackManager: CallbackManager
    override fun onSuccess(result: LoginResult?) {
        if (result != null) {
            handleFacebookAccessToken(result.accessToken)
        }
    }

    override fun onCancel() {
        toast("Ha ocurrido un error intenta nuevamente")
    }

    override fun onError(error: FacebookException?) {
        toast("Ha ocurrido un error intenta nuevamente")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)

    }

    lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mAuth = FirebaseAuth.getInstance()
        callbackManager = CallbackManager.Factory.create()
        setContentView(R.layout.activity_login)
        LoginManager.getInstance().registerCallback(callbackManager,this)
        login_button.setOnClickListener {
            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "user_friends"))
        }
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        Log.d("Login", "handleFacebookAccessToken:$token")

        val credential = FacebookAuthProvider.getCredential(token.token)
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this)
    }
}