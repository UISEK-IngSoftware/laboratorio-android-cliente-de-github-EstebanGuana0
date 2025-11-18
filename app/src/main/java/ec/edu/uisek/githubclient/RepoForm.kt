package ec.edu.uisek.githubclient

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ec.edu.uisek.githubclient.databinding.ActivityRepoFormBinding
import ec.edu.uisek.githubclient.models.Repo
import ec.edu.uisek.githubclient.models.RepoRequest
import ec.edu.uisek.githubclient.services.GithubApiService
import ec.edu.uisek.githubclient.services.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RepoForm : AppCompatActivity() {

    private lateinit var binding: ActivityRepoFormBinding
    private val apiService: GithubApiService by lazy {
        RetrofitClient.gitHubApiService
    }
    private var repoName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRepoFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        repoName = intent.getStringExtra("repo_name")

        if (repoName != null) {
            binding.repoNameInput.setText(repoName)
            binding.repoNameInput.isEnabled = false // Cannot edit repo name
            binding.RepoDescriptionInput.setText(intent.getStringExtra("repo_description"))
        }

        binding.cancelButton.setOnClickListener {
            finish()
        }

        binding.saveButton.setOnClickListener {
            if (repoName != null) {
                updateRepo()
            } else {
                createRepo()
            }
        }
    }

    private fun validateForm(): Boolean {
        if (binding.repoNameInput.text.toString().isBlank()) {
            binding.repoNameInput.error = "El nombre del repositorio es requerido"
            return false
        }
        if (binding.repoNameInput.text.toString().contains(" ")) {
            binding.repoNameInput.error = "El nombre del repositorio no puede contener espacios"
            return false
        }
        binding.repoNameInput.error = null
        return true
    }

    private fun createRepo() {
        if (!validateForm()) {
            return
        }
        val newRepoName = binding.repoNameInput.text.toString().trim()
        val repoDescription = binding.RepoDescriptionInput.text.toString().trim()
        val repoRequest = RepoRequest(newRepoName, repoDescription)

        val call = apiService.addRepo(repoRequest)
        call.enqueue(object : Callback<Repo> {
            override fun onResponse(call: Call<Repo>, response: Response<Repo>) {
                if (response.isSuccessful) {
                    showMessage("Repositorio creado exitosamente")
                    finish()
                } else {
                    handleApiError(response.code())
                }
            }

            override fun onFailure(call: Call<Repo>, t: Throwable) {
                showError("Error al crear el repositorio", t)
            }
        })
    }

    private fun updateRepo() {
        if (!validateForm()) {
            return
        }
        val repoDescription = binding.RepoDescriptionInput.text.toString().trim()
        val repoRequest = RepoRequest(repoName!!, repoDescription) // Name is not updated
        // TODO: Get owner from a reliable source instead of hardcoding
        val call = apiService.updateRepo("EstebanGuana0", repoName!!, repoRequest)

        call.enqueue(object : Callback<Repo> {
            override fun onResponse(call: Call<Repo>, response: Response<Repo>) {
                if (response.isSuccessful) {
                    showMessage("Repositorio actualizado exitosamente")
                    finish()
                } else {
                    handleApiError(response.code())
                }
            }

            override fun onFailure(call: Call<Repo>, t: Throwable) {
                showError("Error al actualizar el repositorio", t)
            }
        })
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showError(message: String, t: Throwable) {
        val errorMsg = "$message: ${t.message}"
        Log.d("RepoForm", errorMsg, t)
        showMessage(errorMsg)
    }

    private fun handleApiError(code: Int) {
        val errorMessage = when (code) {
            401 -> "No autorizado"
            403 -> "Prohibido"
            404 -> "No encontrado"
            else -> "Error $code"
        }
        showMessage("Error: $errorMessage")
    }
}