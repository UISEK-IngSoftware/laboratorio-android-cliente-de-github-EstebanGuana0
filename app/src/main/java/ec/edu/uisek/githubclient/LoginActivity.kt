package ec.edu.uisek.githubclient

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import ec.edu.uisek.githubclient.databinding.ActivityLoginBinding
import ec.edu.uisek.githubclient.services.RetrofitClient
import ec.edu.uisek.githubclient.services.SessionManager

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        binding.loginButton.setOnClickListener { loginUser() }
        setContentView(binding.root)

    }
    private fun loginUser() {
        val username = binding.userInput.text.toString()
        val password = binding.passwordInput.text.toString()

        if(username.isNotEmpty() && password.isNotEmpty()){
            RetrofitClient.createAuthenticadedService(username, password)
            val sessionManager = SessionManager(this)
            sessionManager.saveCredentials(username, password)
            navigateToMain()

        }

    }

    private fun navigateToMain(){
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

}