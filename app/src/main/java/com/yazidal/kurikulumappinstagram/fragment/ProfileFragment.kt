package com.yazidal.kurikulumappinstagram.fragment

import User
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.ImageView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.yazidal.kurikulumappinstagram.EditProfile
import com.yazidal.kurikulumappinstagram.R
import com.yazidal.kurikulumappinstagram.adapter.ImageAdapter
import com.yazidal.kurikulumappinstagram.model.Post
import kotlinx.android.synthetic.main.fragment_profile.view.*

class ProfileFragment : Fragment() {
    private lateinit var profileId: String
    private lateinit var firebaseUser: FirebaseUser

    private var postListGrid: MutableList<Post>? = null
    private var mImagesAdapter: ImageAdapter? = null
    private var mRecyclerViewImg: RecyclerView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val viewProfile = inflater.inflate(R.layout.fragment_profile, container, false)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!

        mRecyclerViewImg = viewProfile.findViewById(R.id.rv_profile)
        mRecyclerViewImg!!.setHasFixedSize(true)

        val linearLayoutManager = GridLayoutManager(context,3)
        mRecyclerViewImg!!.layoutManager = linearLayoutManager

        postListGrid = ArrayList()
        mImagesAdapter = context?.let { ImageAdapter(it, postListGrid as ArrayList<Post> ) }
        mRecyclerViewImg!!.adapter = mImagesAdapter

        val pref = context?.getSharedPreferences("PREF", Context.MODE_PRIVATE)

        if (pref != null) {

            this.profileId = pref.getString("profileId", "none")!!

        }
        if (profileId == firebaseUser.uid) {

            view?.btnEditProfile?.text = "Edit Profile"

        } else if (profileId != firebaseUser.uid) {

            cekFllwFllwingStat()

        }

        viewProfile.btnEditProfile.setOnClickListener {
            val getBtnText = view?.btnEditProfile?.text.toString()
            when{
                getBtnText == "Edit Profile" -> startActivity(Intent(context, EditProfile::class.java))

                getBtnText == "Follow" -> {
                    firebaseUser.uid.let {it1 ->
                        FirebaseDatabase.getInstance().reference
                            .child("Follow").child(it1.toString())
                            .child("Following").child(profileId).setValue(true)
                    }
                    firebaseUser.uid.let {it1 ->
                        FirebaseDatabase.getInstance().reference
                            .child("Follow").child(profileId)
                            .child("Followers").child(it1.toString()).setValue(true)
                    }
                }

                getBtnText == "Following" ->{
                    firebaseUser.uid.let {it1 ->
                        FirebaseDatabase.getInstance().reference
                            .child("Follow").child(it1.toString())
                            .child("Following").child(profileId).removeValue()
                    }
                    firebaseUser.uid.let {it1 ->
                        FirebaseDatabase.getInstance().reference
                            .child("Follow").child(profileId)
                            .child("Followers").child(it1.toString()).setValue(true)
                    }

                }
            }
            startActivity(Intent(context, EditProfile::class.java))

        }

        getFollowers()
        getFollowing()
        userInfo()
        myPost()
        return viewProfile
    }

    private fun myPost() {
        val postRef = FirebaseDatabase.getInstance().reference
            .child("Posts")

        postRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    (postListGrid as ArrayList<Post>).clear()

                    for (s in snapshot.children){
                        val post = s.getValue(Post::class.java)
                        if (post!!.getPublisher() == profileId){
                            (postListGrid as ArrayList<Post>).add(post)
                        }

                        postListGrid!!.reverse()
                        mImagesAdapter!!.notifyDataSetChanged()
                    }
                }
            }
        })
    }

    private fun userInfo() {
        val userRef = FirebaseDatabase.getInstance().reference
            .child("Users").child(profileId)

        userRef.addValueEventListener(object : ValueEventListener {


            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user = snapshot.getValue<User>(User::class.java)

                    Picasso.get().load(user?.getImage()).into(view?.profileImg)
                    view?.txtUsernameProf?.text = user?.getUsername()
                    view?.txtFullnameProf?.text = user?.getFullname()
                    view?.txtBioProf?.text = user?.getBio()

                }
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun getFollowing() {
        val following = FirebaseDatabase.getInstance().reference
            .child("Follow").child(profileId)
            .child("Following")
        following.addValueEventListener(object : ValueEventListener {


            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    view?.totalFllwing?.text = snapshot.childrenCount.toString()
                }
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun cekFllwFllwingStat() {
        firebaseUser.uid.let { it ->
            FirebaseDatabase.getInstance().reference
                .child("Follow").child(it.toString())
                .child("Following")
        }.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    view?.btnEditProfile?.text = "Following"
                } else {
                    view?.btnEditProfile?.text = "Follow"
                }
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun getFollowers() {
        val followers = FirebaseDatabase.getInstance().reference
            .child("Follow").child(profileId)
            .child("Followers")
        followers.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    view?.totalFllwrs?.text = snapshot.childrenCount.toString()
                }
            }
            override fun onCancelled(error: DatabaseError) {

            }


        })
    }

    override fun onStop() {
        super.onStop()
        val pref = context?.getSharedPreferences("PREF", Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId", firebaseUser.uid)
        pref?.apply()
    }

    override fun onPause() {
        super.onPause()
        val pref = context?.getSharedPreferences("PREF", Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId", firebaseUser.uid)
        pref?.apply()
    }

    override fun onDestroy() {
        super.onDestroy()
        val pref = context?.getSharedPreferences("PREF", Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId", firebaseUser.uid)
        pref?.apply()
    }

}