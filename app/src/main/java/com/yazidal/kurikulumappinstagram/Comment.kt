package com.yazidal.kurikulumappinstagram

import User
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.yazidal.kurikulumappinstagram.adapter.CommentAdapter
import com.yazidal.kurikulumappinstagram.model.Comments
import kotlinx.android.synthetic.main.activity_comment.*

class Comment : AppCompatActivity() {

    private var postId = ""
    private var publisherId = ""
    private var firebaseUser: FirebaseUser? = null
    private var commentAdapter: CommentAdapter? = null
    private var commentListData: MutableList<Comments>? = null
    private var recyclerView: RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comment)

        firebaseUser = FirebaseAuth.getInstance().currentUser
        recyclerView = findViewById(R.id.recyclerComment)
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.reverseLayout = true
        recyclerView?.layoutManager = linearLayoutManager

        commentListData = ArrayList()
        commentAdapter = CommentAdapter(this, commentListData as ArrayList<Comments>)
        recyclerView?.adapter = commentAdapter


        val intent = intent
        postId = intent.getStringExtra("postId")
        publisherId = intent.getStringExtra("publisherId")

        userInfo()
        readComment()
        getPostImageComment()
        textPostComment.setOnClickListener {
            if (etAddComment.text.toString() == ""){
                Toast.makeText(this, "Di isi syg :)", Toast.LENGTH_SHORT).show()
            }else{
                addComment()
            }
        }
    }

    private fun getPostImageComment() {
        val postCommentRef = FirebaseDatabase.getInstance().reference
            .child("Posts").child(postId).child("postimage")

        postCommentRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val image = snapshot.value.toString()
                    Picasso.get().load(image).into(imageComment)

                }
            }
        })
    }

    private fun readComment() {
        val commmentRef = FirebaseDatabase.getInstance().reference
            .child("Comment").child(postId)

        commmentRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    commentListData!!.clear()

                    for (s in snapshot.children){
                        val comment = s.getValue(Comments::class.java)
                        commentListData!!.add(comment!!)
                    }
                    commentAdapter!!.notifyDataSetChanged()
                }
            }
        })
    }

    private fun addComment() {
        val comment = FirebaseDatabase.getInstance().reference
            .child("Comment").child(postId)

        val commentMap = HashMap<String, Any>()
        commentMap["comment"] = etAddComment.text.toString()
        commentMap["publisher"] = firebaseUser!!.uid

        comment.push().setValue(commentMap)
        etAddComment.text.clear()
    }

    private fun userInfo() {
        val user = FirebaseDatabase.getInstance().reference
            .child("Users").child(firebaseUser!!.uid)

        user.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val user = snapshot.getValue(User::class.java)

                    Picasso.get()
                        .load(user?.getImage())
                        .into(imageComment)
                }
            }
        })
    }
}