package com.gustavoforero.sharemycar.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import com.facebook.AccessToken
import com.google.firebase.auth.FirebaseAuth
import com.gustavoforero.sharemycar.R


class SplashScreen : AppCompatActivity() {
    private var mDelayHandler: Handler? = null
    private val SPLASH_DELAY: Long = 3000 //3 seconds

    internal val mRunnable: Runnable = Runnable {
        if (!isFinishing) {

            val currentUser = FirebaseAuth.getInstance().currentUser
            val accessToken = AccessToken.getCurrentAccessToken()
            val isLoggedIn = accessToken != null && !accessToken.isExpired
            val intent: Intent
            intent = if (currentUser != null) {
                Intent(applicationContext, MainActivity::class.java)

            } else
                Intent(applicationContext, LoginActivity::class.java)

            startActivity(intent)
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash)
        //Initialize the Handler
        mDelayHandler = Handler()
        //Navigate with delay
        mDelayHandler!!.postDelayed(mRunnable, SPLASH_DELAY)
        // Add code to print out the key hash
        /* try {
             val info = packageManager.getPackageInfo(
                     "com.gustavoforero.sharemycar",
                     PackageManager.GET_SIGNATURES)
             for (signature in info.signatures) {
                 val md = MessageDigest.getInstance("SHA")
                 md.update(signature.toByteArray())
                 Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT))
             }
         } catch (e: PackageManager.NameNotFoundException) {

         } catch (e: NoSuchAlgorithmException) {

         }*/

    }

    public override fun onDestroy() {

        if (mDelayHandler != null) {
            mDelayHandler!!.removeCallbacks(mRunnable)
        }

        super.onDestroy()
    }

}