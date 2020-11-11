package com.yazidal.kurikulumappinstagram.adapter

import User
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.yazidal.kurikulumappinstagram.R
import com.yazidal.kurikulumappinstagram.fragment.ProfileFragment

class SearchAdapter (private val context: Context, private val mUser:List<User>, private var isFragment: Boolean = false) :
    RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {

    private var firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_search,parent, false)
        return SearchViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mUser.size
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val user = mUser[position]
        holder.userNameTxtView.text = user.getUsername()
        holder.fullNameTxtView.text = user.getFullname()
        Picasso.get().load(user.getImage()).into(holder.userProfileImage)

        cekFllwngStat(user.getUID(), holder.followButton)

        holder.itemView.setOnClickListener {
            val gotoProfile = context.getSharedPreferences("PREF", Context.MODE_PRIVATE).edit()
            gotoProfile.putString("profileId", user.getUID())
            gotoProfile.apply()

            (context as FragmentActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.frameLayout, ProfileFragment()).commit()

        }

        holder.followButton.setOnClickListener {
            if(holder.followButton.text.toString() == "Follow"){

                firebaseUser?.uid.let { it ->
                    FirebaseDatabase.getInstance().reference
                        .child("Follow").child(it.toString())
                        .child("Following").child(user.getUID())
                        .setValue(true).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                firebaseUser?.uid.let { it ->
                                    FirebaseDatabase.getInstance().reference
                                        .child("Follow").child(user.getUID())
                                        .child("Followers").child(it.toString())
                                        .setValue(true).addOnCompleteListener {
                                            if (task.isSuccessful){

                                            }
                                        }
                                }
                            }
                        }
                }
            }else{
                firebaseUser?.uid.let {it->
                    FirebaseDatabase.getInstance().reference
                        .child("Follow").child(it.toString())
                        .child("Following").child(user.getUID())
                        .removeValue().addOnCompleteListener {task ->
                            if (task.isSuccessful){
                                firebaseUser?.uid.let {
                                    FirebaseDatabase.getInstance().reference
                                        .child("Follow").child(user.getUID())
                                        .child("Followers").child(it.toString())
                                        .removeValue().addOnCompleteListener {task ->
                                            if (task.isSuccessful){

                                            }
                                        }
                                }
                            }
                        }
                }
            }
        }

    }

    private fun cekFllwngStat(uid: String, followButton: Button) {
        val following = firebaseUser?.uid.let {
            FirebaseDatabase.getInstance().reference
                .child("Follow").child(it.toString())
                .child("Following")
        }

        following.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.child(uid).exists()){
                    followButton.text = "Following"
                }else{
                    followButton.text = "Follow"
                }
            }
        })
    }

    inner class SearchViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val userNameTxtView : TextView = itemView.findViewById(R.id.tvUserSearch)
        val fullNameTxtView : TextView = itemView.findViewById(R.id.tvFullnameSearch)
        val userProfileImage : ImageView = itemView.findViewById(R.id.imgSearchProfile)
        val followButton : Button = itemView.findViewById(R.id.btnSearchUser)
    }
}