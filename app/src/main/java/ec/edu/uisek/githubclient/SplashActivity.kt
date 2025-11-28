package ec.edu.uisek.githubclient

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import ec.edu.uisek.githubclient.services.RetrofitClient
import ec.edu.uisek.githubclient.services.SessionManager

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sessionManager = SessionManager(this)
        val credentials = sessionManager.getCredentials()

        if (credentials!= null) {
            RetrofitClient.createAuthenticadedService(credentials.username, credentials.password)
            startActivity(Intent(this, MainActivity::class.java))
        } else {
            startActivity(Intent(this, LoginActivity::class.java))

        }
        finish()
    }
}