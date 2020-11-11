package com.yazidal.kurikulumappinstagram.adapter

import User
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.yazidal.kurikulumappinstagram.Comment
import com.yazidal.kurikulumappinstagram.R
import com.yazidal.kurikulumappinstagram.model.Post
import de.hdodenhof.circleimageview.CircleImageView

class PostAdapter(private val context: Context, private val mPost: List<Post>) :
    RecyclerView.Adapter<PostViewHolder>() {

    private var firebaseUser: FirebaseUser? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {

        val view = LayoutInflater.from(context).inflate(R.layout.list_post, parent, false)
        return PostViewHolder(view)

    }

    override fun getItemCount(): Int {
        return mPost.size
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        firebaseUser = FirebaseAuth.getInstance().currentUser

        val myPost = mPost[position]

        holder.comment_Button.setOnClickListener {
            val comentKu = Intent(context, Comment::class.java)
            comentKu.putExtra("postId", myPost.getPostId())
            comentKu.putExtra("publisherId", myPost.getPublisher())
            context.startActivity(comentKu)
        }

        Picasso.get()
            .load(myPost.getPostImage())
            .into(holder.post_Image)
        if (myPost.getDescription().equals("")) {
            holder.description.visibility = View.GONE
        } else {
            holder.description.visibility = View.VISIBLE
            holder.description.text = myPost.getDescription()
        }
        publisherInfo(
            holder.profile_Image,
            holder.username,
            holder.publisher,
            myPost.getPublisher()
        )

        likePost(myPost.getPostId(), holder.like_Button)

        totalLike(holder.likes, myPost.getPostId())
        getTotalPostComment(holder.comment, myPost.getPostId())

        holder.like_Button.setOnClickListener {
            if (holder.like_Button.tag == "Like") {

                FirebaseDatabase.getInstance().reference
                    .child("Likes").child(myPost.getPostId()).child(firebaseUser!!.uid)
                    .setValue(true)
            } else {
                FirebaseDatabase.getInstance().reference
                    .child("Likes").child(myPost.getPostId()).child(firebaseUser!!.uid)
                    .removeValue()
            }
        }
    }

    private fun getTotalPostComment(comment: TextView, postId: String) {
        val commentRef = FirebaseDatabase.getInstance().reference
            .child("Comment").child(postId)
        commentRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    comment.text = snapshot.childrenCount.toString() + "Comments"
                }
            }
        })
    }

    private fun totalLike(likes: TextView, postId: String) {
        val likesRef = FirebaseDatabase.getInstance().reference
            .child("Likes").child(postId)

        likesRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    likes.text = snapshot.childrenCount.toString() + "Likes"
                }
            }
        })
    }

    private fun likePost(postId: String, likeButton: ImageView) {
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val likesRef = FirebaseDatabase.getInstance().reference
            .child("Likes").child(postId)

        likesRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.child(firebaseUser!!.uid).exists()) {
                    likeButton.setImageResource(R.drawable.heart_clicked)
                    likeButton.tag = "Liked"
                } else {
                    likeButton.setImageResource(R.drawable.heart_not_clicked)
                    likeButton.tag = "Like"
                }
            }
        })

    }

    private fun publisherInfo(
        profileImage: CircleImageView,
        username: TextView,
        publisherName: TextView,
        publisher: String
    ) {
        val userRef = FirebaseDatabase.getInstance().reference.child("Users").child(publisher)

        userRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue<User>(User::class.java)

                Picasso.get()
                    .load(user?.getImage())
                    .into(profileImage)
                username.text = user?.getUsername()
                publisherName.text = user?.getUsername()
            }
        })

    }

}

class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var profile_Image: CircleImageView = itemView.findViewById(R.id.circleImagePost)
    var post_Image: ImageView = itemView.findViewById(R.id.image_postHome)
    var like_Button: ImageView = itemView.findViewById(R.id.btnLikePost)
    var share_Button: ImageView = itemView.findViewById(R.id.btnSharePost)
    var comment_Button: ImageView = itemView.findViewById(R.id.btnCommentPost)
    var save_Button: ImageView = itemView.findViewById(R.id.btnSavePost)
    var username: TextView = itemView.findViewById(R.id.txt_usernamePost)
    var likes: TextView = itemView.findViewById(R.id.tvPostLike)
    var publisher_Name: TextView = itemView.findViewById(R.id.txt_usernamePost)
    var publisher: TextView = itemView.findViewById(R.id.tvPublisherPost)
    var comment: TextView = itemView.findViewById(R.id.tvCommentPost)
    var description: TextView = itemView.findViewById(R.id.tvDescriptionPost)

}
