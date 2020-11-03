package com.easycodingg.pickfiles

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import kotlinx.android.synthetic.main.image_item.view.*

class MyAdapter(
    var list: List<Uri>
): RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    inner class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.image_item, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val curImageUri = list[position]

        holder.itemView.apply {
            Glide.with(this)
                .load(curImageUri)
                .centerCrop()
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(ivImageItem)
        }
    }

    override fun getItemCount() = list.size
}