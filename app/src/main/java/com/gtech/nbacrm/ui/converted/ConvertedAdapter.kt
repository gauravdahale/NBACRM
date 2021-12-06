package com.gtech.nbacrm.ui.converted

import android.app.Dialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageButton
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gtech.nbacrm.ItemDecoration
import com.gtech.nbacrm.R
import com.gtech.nbacrm.listeners.ItemClickListener
import com.gtech.nbacrm.ui.converted.GetConvertedModel
import com.gtech.nbacrm.ui.followup.FollowUpAdapter
import com.gtech.nbacrm.ui.followup.GetFollowUpModel
import com.gtech.nbacrm.ui.shared.CommentModel
import com.gtech.nbacrm.ui.shared.CommentsAdapter
import java.util.*
import kotlin.collections.ArrayList


class ConvertedAdapter(val mContext: ConvertedFragment, val mList: ArrayList<GetConvertedModel>)
    : RecyclerView.Adapter<ConvertedAdapter.ViewHolder>(),Filterable {
    internal var filter: CustomFilter? = null
    internal var itemList: ArrayList<GetConvertedModel> = mList
    internal var arrayList: ArrayList<GetConvertedModel> = mList
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var layoutbg = itemView.findViewById<CardView>(R.id.leadcard)
        val name = itemView.findViewById<TextView>(R.id.i_lead_name)
        val number = itemView.findViewById<TextView>(R.id.i_lead_number)
        val city = itemView.findViewById<TextView>(R.id.i_lead_city)
        val carpetarea = itemView.findViewById<TextView>(R.id.i_lead_carpet_area)
        val date = itemView.findViewById<TextView>(R.id.i_lead_date)
        var itemClickListener: ItemClickListener? = null
var commentButton  =itemView.findViewById<ImageButton>(R.id.lead_comments_icon)
        init {
            itemView.setOnClickListener(this)
        }

        fun bind(model: GetConvertedModel) {
            if (model.clientname != null) name.text = model.clientname
            if (model.contact != null) number.text = model.contact
            if (model.location != null) city.text = model.location
            if (model.carpetArea != null) carpetarea.text = model.carpetArea.toString()
            if (model.timestamp != null) date.text = model.getTimeDate(model.timestamp!!)
            commentButton.setOnClickListener {
                commentsDialog(model.comments)
            }
        }
        private fun commentsDialog(comments: HashMap<String, CommentModel>?) {
            val clist =ArrayList<CommentModel>()
            val dialog = Dialog(mContext.requireContext())
            dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.dialog_comments_recyclerview)
            val recyclerView = dialog.findViewById<RecyclerView>(R.id.comments_recyclerview)
            recyclerView.layoutManager = LinearLayoutManager(mContext.requireContext())

            comments?.forEach {
                val model = it.value
                if(model !=null)clist.add(model)
            }
            val commentsAdapter = CommentsAdapter(mContext.requireContext(), clist)
            recyclerView.addItemDecoration(ItemDecoration(mContext.requireContext()))
            recyclerView.adapter = commentsAdapter
            dialog.show()
        }
        override fun onClick(p0: View?) {
            itemClickListener?.onItemClick(layoutPosition)
        }

        fun setItemOnclickListener(itemClickListener: ItemClickListener) {
            this.itemClickListener = itemClickListener
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConvertedAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_leads, parent, false)

        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ConvertedAdapter.ViewHolder, position: Int) {
        val model = itemList[holder.adapterPosition]
        holder.bind(model)
        when (model.priority) {
            "Hot" -> {
                holder.layoutbg.setBackgroundColor(mContext.resources.getColor(R.color.hotlead))
            }
            "Warm" -> {
                holder.layoutbg.setBackgroundColor(mContext.resources.getColor(R.color.warmlead))
            }
            "Cold" -> {
                holder.layoutbg.setBackgroundColor(mContext.resources.getColor(R.color.coldlead))
            }
            null -> {
                holder.layoutbg.setBackgroundColor(mContext.resources.getColor(android.R.color.background_light))
            }
            else -> {
                holder.layoutbg.setBackgroundColor(mContext.resources.getColor(android.R.color.background_light))
            }
        }

        holder.setItemOnclickListener(object : ItemClickListener {
            override fun onItemClick(i: Int) {
                val bundle = bundleOf("parcel" to model)
                mContext.mNavController.navigate(R.id.action_navigation_converted_to_convertedDetailFragment, bundle)
            }
        })
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun getFilter(): Filter {
        if (filter == null) {
            filter = CustomFilter()
        }
        return filter as CustomFilter
    }

    internal inner class CustomFilter : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            var constraint = constraint

            val results = FilterResults()
            if (constraint != null && constraint.isNotEmpty()) {
                constraint = constraint.toString().toUpperCase(Locale.ROOT)
                val filteredlist = ArrayList<GetConvertedModel>()
                for (i in arrayList.indices) {
                    Log.d("performFiltering","arraylist Item : ${arrayList[i].clientname}")
                    if (arrayList[i].clientname?.toUpperCase(Locale.ROOT)?.contains(constraint) ==true ||
                        arrayList[i].carpetArea?.toUpperCase(Locale.ROOT)?.contains(constraint) == true ||
                        arrayList[i].location?.toUpperCase(Locale.ROOT)?.contains(constraint) == true||
                        arrayList[i].priority?.toUpperCase(Locale.ROOT)?.contains(constraint) == true||
                        arrayList[i].contact?.toUpperCase(Locale.ROOT)?.contains(constraint) == true
                    ) {
                        val l = arrayList[i]
                        filteredlist.add(l)
                        Log.d("performFiltering", "msg: ${l.clientname}")
                    }
                }

                results.count = filteredlist.size
                results.values = filteredlist

            }
            else {
                results.count = arrayList.size
                results.values = arrayList
                Log.d("performFiltering", "arraylistsize ${arrayList.size}")
                //Toast.makeText(mContext, "Performing results", Toast.LENGTH_SHORT).show()
            }
            return results
        }

        override fun publishResults(constraint: CharSequence, results: FilterResults) {
//            itemList.clear()
            itemList = (results.values as? ArrayList<GetConvertedModel>)!!

            //   itemList.addAll(results.values as ArrayList<GetFollowUpModel>)
            Log.d("Publishedresults", "publishResults: ${itemList.size} : ")
            notifyDataSetChanged()
            Log.d("publishResults", "msg")
        }
    }
}