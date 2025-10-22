package ec.edu.uisek.githubclient

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ec.edu.uisek.githubclient.databinding.FragmentRepoItemBinding

class ReposViewHolder(private val binding: FragmentRepoItemBinding) :
    RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int){

            binding.repoName.text = "Repositorio No.${position}"
            binding.repoDescription.text = "Esta es la Descripcion para el elemento No. ${position}"

        }

    }

class ReposAdapter: RecyclerView.Adapter<ReposViewHolder>(){

    override fun getItemCount(): Int = 3

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReposViewHolder {
        var binding = FragmentRepoItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
                )
        return ReposViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReposViewHolder, position: Int) {
        holder.bind(position)
    }
}