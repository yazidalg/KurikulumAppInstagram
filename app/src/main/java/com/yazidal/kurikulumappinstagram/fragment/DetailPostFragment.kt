package com.yazidal.kurikulumappinstagram.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.yazidal.kurikulumappinstagram.R
import com.yazidal.kurikulumappinstagram.adapter.ImageAdapter
import com.yazidal.kurikulumappinstagram.adapter.PostAdapter
import com.yazidal.kurikulumappinstagram.model.Post


class DetailPostFragment : Fragment() {

    private lateinit var postId: String
    private var adapterDetail: PostAdapter? = null
    private var postiap: MutableList<Post>? = null
    private var recyclerViewDetail: RecyclerView? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_detail_post, container, false)

        recyclerViewDetail = view.findViewById(R.id.recyclerDetail)
        val layoutManager = LinearLayoutManager(context)
        recyclerViewDetail?.setHasFixedSize(true)
        recyclerViewDetail?.layoutManager = layoutManager
        postiap = ArrayList()

        adapterDetail = context.let {
            it?.let { it1 ->
                PostAdapter(it1, postiap as ArrayList<Post>)}}

        recyclerViewDetail?.adapter = adapterDetail

        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        if (pref != null){
            this.postId = pref.getString("postid", "none")!!
        }

        getPost()
        return view
    }

    private fun getPost() {
        var post = FirebaseDatabase.getInstance().reference
            .child("Posts").child(postId)
        post.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val myPost = snapshot.getValue(Post::class.java)
                postiap!!.add(myPost!!)

                adapterDetail!!.notifyDataSetChanged()
            }
        })
    }
}