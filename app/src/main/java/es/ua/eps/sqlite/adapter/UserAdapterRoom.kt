package es.ua.eps.sqlite.adapter

import es.ua.eps.sqlite.sql.UserEntity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import es.ua.eps.sqlite.databinding.ListItemUserBinding

class UserAdapterRoom(private val userList: List<UserEntity>) : RecyclerView.Adapter<UserAdapterRoom.UserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ListItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList[position]
        holder.bind(user)
    }

    override fun getItemCount(): Int = userList.size

    inner class UserViewHolder(private val binding: ListItemUserBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(user: UserEntity) {
            binding.usernameTextView.text = user.nombre_usuario
            binding.emailTextView.text = user.email
        }
    }
}