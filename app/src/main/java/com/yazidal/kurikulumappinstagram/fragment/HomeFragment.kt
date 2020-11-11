package com.yazidal.kurikulumappinstagram.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.yazidal.kurikulumappinstagram.R
import com.yazidal.kurikulumappinstagram.adapter.PostAdapter
import com.yazidal.kurikulumappinstagram.model.Post

class HomeFragment : Fragment() {

    private var postAdapter: PostAdapter? = null
    private var postList: MutableList<Post>? = null
    private var follownigList: MutableList<Post>? = null
    var recyclerViewHome: RecyclerView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        recyclerViewHome = view.findViewById(R.id.recycler_home)
        val linearManager = LinearLayoutManager(context)
        linearManager.reverseLayout = true
        linearManager.stackFromEnd = true
        recyclerViewHome?.layoutManager = linearManager

        postList = ArrayList()
        postAdapter = context.let {
            it?.let { it1 ->
                PostAdapter(it1, postList as ArrayList<Post>)
            }
        }
        recyclerViewHome?.adapter = postAdapter

        cekFollowing()

        return view
    }

    private fun cekFollowing() {
        follownigList = ArrayList()

        val followingRef = FirebaseDatabase.getInstance().reference.child("Follow").child(
            FirebaseAuth.getInstance().currentUser!!.uid
        )
            .child("Following")

        followingRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    (follownigList as ArrayList<String>).clear()

                    for (s in snapshot.children) {
                        s.key.let { it?.let { it1 -> (follownigList as ArrayList<String>).add(it) } }
                    }
                    getPost()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

    }

    private fun getPost() {
        val posts = FirebaseDatabase.getInstance().reference.child("Posts")

        posts.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                postList?.clear()

                for (s in snapshot.children) {
                    val post = s.getValue(Post::class.java)
                    for (id in (follownigList as ArrayList<String>)) {
                        if (post!!.getPublisher() == id) {
                            postList!!.add(post)
                        }
                    }
                    postAdapter!!.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}
