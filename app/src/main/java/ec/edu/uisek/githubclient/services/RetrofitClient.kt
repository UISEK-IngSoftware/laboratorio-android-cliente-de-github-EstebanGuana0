package ec.edu.uisek.githubclient.services

import android.util.Log
import ec.edu.uisek.githubclient.BuildConfig
import ec.edu.uisek.githubclient.interceptors.BasicAuthInterceptor
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Objeto singleton que configura y proporciona la instancia de Retrofit
 * para conectarse a la API de GitHub
 */
object RetrofitClient {

    private const val BASE_URL = "https://api.github.com/"
    private var apiService: GithubApiService? = null

    fun createAuthenticadedService(username: String, password: String): GithubApiService {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY

        }
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(BasicAuthInterceptor(username, password))
            .addInterceptor(loggingInterceptor)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(GithubApiService::class.java)
        return apiService!!


    }

    fun getApiService(): GithubApiService{
        return apiService ?: throw IllegalStateException("El cliente retrofit no pudo inicializarse")

    }
    
    fun clearService() {
        apiService = null
    }

}