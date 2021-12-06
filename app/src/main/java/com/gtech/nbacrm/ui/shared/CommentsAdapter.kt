package com.gtech.nbacrm.ui.shared

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gtech.nbacrm.R

class CommentsAdapter(val mContext: Context, val comments: ArrayList<CommentModel>?) : RecyclerView.Adapter<CommentsAdapter.CommentsHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentsAdapter.CommentsHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_comments, parent, false)
        return CommentsHolder(view)
    }

    class CommentsHolder(view: View) : RecyclerView.ViewHolder(view) {
        val comment = view.findViewById<TextView>(R.id.i_comment_comment)
        val commentedby = view.findViewById<TextView>(R.id.i_comments_commented_by)
        val date = view.findViewById<TextView>(R.id.i_comment_date)


    }

    override fun onBindViewHolder(holder: CommentsAdapter.CommentsHolder, position: Int) {
        val model = comments?.get(holder.adapterPosition)
        holder.comment.text = model?.comment
        holder.commentedby.text = model?.commentedby
        holder.date.text = model?.timestamp?.let { model.getTimeDate(it) }

    }

    override fun getItemCount(): Int {
        return comments!!.size
    }

}
