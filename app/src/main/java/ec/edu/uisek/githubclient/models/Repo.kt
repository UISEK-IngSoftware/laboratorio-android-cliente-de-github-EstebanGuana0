package ec.edu.uisek.githubclient.models

data class Repo (
    val id:Long,
    val name:String,
    val description:String,
    val language:String?,
    val owner:RepoOwner
)

data class RepoRequest (
    val name:String,
    val description:String,
)