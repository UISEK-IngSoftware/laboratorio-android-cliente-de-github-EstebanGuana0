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
        reposAdapter = ReposAdapter()
        binding.reposRecyclerView.adapter = reposAdapter
    }

    private  fun fetchRepositories(){
        val apiService : GithubApiService = RetrofitClient.gitHubApiService
        val call = apiService.getRepos()

        call.enqueue(object : Callback<List<Repo>>{
            override fun onResponse(call: Call<List<Repo>?>, response: Response<List<Repo>?>) {
                if(response.isSuccessful){
                    val repos = response.body()
                    if(repos !=null && repos.isNotEmpty()){
                        reposAdapter.updateRepositories(repos)
                    } else{
                        showMessage("No se encontraron repositorios")
                    }

                }else{
                    val errorMessage = when(response.code()){
                        401 -> "No autorizado"
                        403 -> "Prohibido"
                        404 -> "No encontrado"
                        else -> "Error ${response.code()}"
                    }
                    showMessage("Error: $errorMessage")

                    }
                }



            override fun onFailure(call: Call<List<Repo>>, t: Throwable) {
                showMessage("No se pudieron cargar repositorios")
            }
        })
    }

    private fun showMessage(message: String) {
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show()
    }

    private fun displayNewRepoForm(){
        Intent(this,RepoForm::class.java).apply {
            startActivity(this)
        }
    }

}