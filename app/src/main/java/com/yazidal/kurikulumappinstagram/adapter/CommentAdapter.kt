package com.yazidal.kurikulumappinstagram.adapter

import User
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.yazidal.kurikulumappinstagram.R
import com.yazidal.kurikulumappinstagram.model.Comments
import de.hdodenhof.circleimageview.CircleImageView

class CommentAdapter(private val context: Context, private val mComment : MutableList<Comments>):
    RecyclerView.Adapter<CommentViewHolder>() {

    private var firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.list_comment, parent, false)
        return CommentViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mComment.size
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        val comment = mComment[position]

        holder.comment_nan.text = comment.comment

        getUserInfo(holder.ImageProfileComment, holder.usernameComment, comment.publisher)


    }

    private fun getUserInfo(imageProfileComment: CircleImageView, usernameComment: TextView, publisher: String) {
        val userRef = FirebaseDatabase.getInstance().reference
            .child("Users").child(publisher)

        userRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val user = snapshot.getValue(User::class.java)

//                   Picasso.get()
//                       .load(user!!.getImage())
//                       .into(imageProfileComment)

                    Glide.with(context).load(user!!.getImage()).into(imageProfileComment)
                    usernameComment.text = user.getUsername()
                }
            }
        })


    }
}

class CommentViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    var ImageProfileComment : CircleImageView = itemView.findViewById(R.id.imgSearchProfile)
    var usernameComment : TextView = itemView.findViewById(R.id.tvUserSearch)
    var comment_nan : TextView = itemView.findViewById(R.id.tvFullnameSearch)

}
