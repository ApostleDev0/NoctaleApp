package com.example.noctaleapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.noctaleapp.R
import com.example.noctaleapp.model.User

// Make sure you have the User data class defined in User.kt or elsewhere in your package.

class ProfileAdapter(private var users: List<User>) :
    RecyclerView.Adapter<ProfileAdapter.ProfileViewHolder>() {

    /**
     * Updates the list of users in the adapter and notifies the RecyclerView to refresh.
     * @param newUsers The new list of User objects to display.
     */
    fun updateUsers(newUsers: List<User>) {
        this.users = newUsers
        notifyDataSetChanged() // A simpler way to update for now; consider DiffUtil for performance in larger lists.
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileViewHolder {
        // Inflate the layout for a single profile item.
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_profile, parent, false)
        return ProfileViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProfileViewHolder, position: Int) {
        val user = users[position]
        holder.bind(user)
    }

    override fun getItemCount(): Int = users.size // Returns the total number of items in the list.

    /**
     * ViewHolder for a single profile item. It holds the views and binds data to them.
     */
    inner class ProfileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val profileImage: ImageView = itemView.findViewById(R.id.profileImage)
        private val profileName: TextView = itemView.findViewById(R.id.profileName)
        private val profileDescription: TextView = itemView.findViewById(R.id.profileDescription)


        /**
         * Binds a User object's data to the views in the ViewHolder.
         * @param user The User object to bind.
         */
        fun bind(user: User) {
            profileName.text = user.displayName
            // userName.text = user.uid // Uncomment if you want to display the user ID
            profileDescription.text = user.description

            // Load the avatar image using Glide.
            Glide.with(itemView.context)
                .load(user.avatarUrl)
                .placeholder(R.drawable.ic_user) // Optional: Add a placeholder drawable
//                .error(R.drawable.error_avatar)       // Optional: Add an error drawable
                .into(profileImage)
        }
    }
}