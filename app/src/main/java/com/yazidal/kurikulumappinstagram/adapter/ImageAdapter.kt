package com.yazidal.kurikulumappinstagram.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

import com.squareup.picasso.Picasso
import com.yazidal.kurikulumappinstagram.R
import com.yazidal.kurikulumappinstagram.fragment.DetailPostFragment
import com.yazidal.kurikulumappinstagram.fragment.ProfileFragment
import com.yazidal.kurikulumappinstagram.model.Post

class ImageAdapter(private val context: Context, private val mPost: List<Post>)
    : RecyclerView.Adapter<ImageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.image_list_item, parent, false)
        return ImageViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mPost.size
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val mPost = mPost[position]

//        Picasso.get().load(mPost.getPostImage()).into(holder.postImageGrid)
        Glide.with(context).load(mPost.getPostImage()).into(holder.postImageGrid)

        holder.itemView.setOnClickListener {
            val gotoDetail = context.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()
            gotoDetail.putString("postid", mPost.getPostId())
            gotoDetail.apply()

            (context as FragmentActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.frameLayout, DetailPostFragment()).commit()
        }

    }
}

class ImageViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    var postImageGrid : ImageView = itemView.findViewById(R.id.image_my_post)
}
