package ec.edu.uisek.githubclient

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ec.edu.uisek.githubclient.databinding.ActivityMainBinding
import ec.edu.uisek.githubclient.models.Repo
import ec.edu.uisek.githubclient.services.GithubApiService
import ec.edu.uisek.githubclient.services.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var reposAdapter: ReposAdapter
    private val apiService: GithubApiService by lazy {
        RetrofitClient.getApiService()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        binding.newRepoFab.setOnClickListener {
            displayNewRepoForm()
        }
    }

    override fun onResume() {
        super.onResume()
        fetchRepositories()
    }

    private fun setupRecyclerView() {
        // Configura Adapter pasando lambdas para manejar clics de editar y borrar
        reposAdapter = ReposAdapter(onEditClick = {
            displayEditRepoForm(it)
        }, onDeleteClick = {
            deleteRepository(it)
        })
        binding.reposRecyclerView.adapter = reposAdapter
    }

    private fun fetchRepositories() {
        val call = apiService.getRepos()

        call.enqueue(object : Callback<List<Repo>> {
            override fun onResponse(call: Call<List<Repo>>, response: Response<List<Repo>>) {
                if (response.isSuccessful) {
                    val repos = response.body()
                    if (repos != null && repos.isNotEmpty()) {
                        reposAdapter.updateRepositories(repos)
                    } else {
                        showMessage("No se encontraron repositorios")
                    }
                } else {
                    handleApiError(response.code())
                }
            }

            override fun onFailure(call: Call<List<Repo>>, t: Throwable) {
                showMessage("No se pudieron cargar repositorios")
            }
        })
    }

    private fun deleteRepository(repo: Repo) {
        // API DELETE: Borra el repositorio remoto usando owner y nombre
        val call = apiService.deleteRepo(repo.owner.login, repo.name)
        call.enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if (response.isSuccessful) {
                    showMessage("Repositorio eliminado exitosamente")
                    fetchRepositories() // Recarga la lista para reflejar cambios
                } else {
                    handleApiError(response.code())
                }
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                showMessage("Error al eliminar el repositorio: ${t.message}")
            }
        })
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun displayNewRepoForm() {
        Intent(this, RepoForm::class.java).apply {
            startActivity(this)
        }
    }

    private fun displayEditRepoForm(repo: Repo) {
        // Abre el formulario enviando datos existentes (Modo Edición)
        Intent(this, RepoForm::class.java).apply {
            putExtra("repo_name", repo.name)
            putExtra("repo_description", repo.description)
            startActivity(this)
        }
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