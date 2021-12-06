package com.gtech.nbacrm.ui.closedleads

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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gtech.nbacrm.ItemDecoration
import com.gtech.nbacrm.R
import com.gtech.nbacrm.listeners.ItemClickListener
import com.gtech.nbacrm.ui.leads.LeadFragment
import com.gtech.nbacrm.ui.leads.ReadLeadModel
import com.gtech.nbacrm.ui.shared.CommentModel
import com.gtech.nbacrm.ui.shared.CommentsAdapter
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class ClosedLeadsAdapter(val mContext: ClosedLeadsFragment, val mList: ArrayList<ReadLeadModel>) :
    RecyclerView.Adapter<ClosedLeadsAdapter.ViewHolder>(), Filterable {

    internal var filter: CustomFilter? = null
    internal var itemList: ArrayList<ReadLeadModel> = mList
    internal var arrayList: ArrayList<ReadLeadModel> = mList

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        val name = itemView.findViewById<TextView>(R.id.i_lead_name)
        val number = itemView.findViewById<TextView>(R.id.i_lead_number)
        val city = itemView.findViewById<TextView>(R.id.i_lead_city)
        val carpetarea = itemView.findViewById<TextView>(R.id.i_lead_carpet_area)
        val lastcalldate = itemView.findViewById<TextView>(R.id.i_lead_last_call_date)
        val nextcalldate = itemView.findViewById<TextView>(R.id.i_lead_next_call_date)
        val lastcomment = itemView.findViewById<TextView>(R.id.i_lead_last_comment)
        val date = itemView.findViewById<TextView>(R.id.i_lead_date)
        var itemClickListener: ItemClickListener? = null
        var leadlayout = itemView.findViewById<CardView>(R.id.leadcard)
        var commentButton = itemView.findViewById<ImageButton>(R.id.lead_comments_icon)

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(model: ReadLeadModel) {
            if (model.clientname != null) name.text = model.clientname
            if (model.contact != null) number.text = model.contact
            if (model.location != null) city.text = model.location
            if (model.carpetArea != null) carpetarea.text = model.carpetArea.toString()
            if (model.lastcalldate != null) lastcalldate.text = model.lastcalldate.toString()
            if (model.nextcalldate != null) nextcalldate.text = model.nextcalldate.toString()
            if (model.lastcomment != null) lastcomment.text = model.lastcomment.toString()
            if (model.timestamp != null) date.text = model.getTimeDate(model.timestamp!!)
            commentButton.setOnClickListener {
                commentsDialog(model.comments)
            }
        }

        override fun onClick(p0: View?) {
            itemClickListener?.onItemClick(layoutPosition)
        }

        fun setItemOnclickListener(itemClickListener: ItemClickListener) {
            this.itemClickListener = itemClickListener
        }
    }

    private fun commentsDialog(comments: HashMap<String, CommentModel>?) {
        val clist = ArrayList<CommentModel>()
        val dialog = Dialog(mContext.requireContext())
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_comments_recyclerview)
        val recyclerView = dialog.findViewById<RecyclerView>(R.id.comments_recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(mContext.requireContext())

        comments?.forEach {
            val model = it.value
            if (model != null) clist.add(model)
        }
        val commentsAdapter = CommentsAdapter(mContext.requireContext(), clist)
        recyclerView.addItemDecoration(ItemDecoration(mContext.requireContext()))
        recyclerView.adapter = commentsAdapter
        dialog.show()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ClosedLeadsAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_leads, parent, false)

        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ClosedLeadsAdapter.ViewHolder, position: Int) {
        val model = itemList[holder.adapterPosition]
        holder.bind(model)
        when (model.priority) {
            "Hot" -> {
                holder.leadlayout.setBackgroundColor(mContext.resources.getColor(R.color.hotlead))
            }
            "Warm" -> {
                holder.leadlayout.setBackgroundColor(mContext.resources.getColor(R.color.warmlead))
            }
            "Cold" -> {
                holder.leadlayout.setBackgroundColor(mContext.resources.getColor(R.color.coldlead))
            }
            null -> {
                holder.leadlayout.setBackgroundColor(mContext.resources.getColor(android.R.color.background_light))
            }
            else -> {
                holder.leadlayout.setBackgroundColor(mContext.resources.getColor(android.R.color.background_light))
            }
        }
        holder.setItemOnclickListener(object : ItemClickListener {
            override fun onItemClick(i: Int) {
                val bundle = bundleOf("parcel" to model)

//                mContext.findNavController().navigate(R.id.action_closedLeadsFragment_to_updateLeadFragment, bundle)
            }

        })
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    ///For Search Option
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
                val filteredlist = ArrayList<ReadLeadModel>()
                for (i in arrayList.indices) {
                    Log.d("performFiltering", "arraylist Item : ${arrayList[i].clientname}")
                    if (arrayList[i].clientname?.toUpperCase(Locale.ROOT)
                            ?.contains(constraint) == true ||
                        arrayList[i].carpetArea?.toUpperCase(Locale.ROOT)
                            ?.contains(constraint) == true ||
                        arrayList[i].location?.toUpperCase(Locale.ROOT)
                            ?.contains(constraint) == true ||
                        arrayList[i].priority?.toUpperCase(Locale.ROOT)
                            ?.contains(constraint) == true ||
                        arrayList[i].contact?.toUpperCase(Locale.ROOT)?.contains(constraint) == true
                    ) {
                        val l = arrayList[i]
                        filteredlist.add(l)
                        Log.d("performFiltering", "msg: ${l.clientname}")
                    }
                }

                results.count = filteredlist.size
                results.values = filteredlist

            } else {
                results.count = arrayList.size
                results.values = arrayList
                Log.d("performFiltering", "arraylistsize ${arrayList.size}")
                //Toast.makeText(mContext, "Performing results", Toast.LENGTH_SHORT).show()
            }
            return results
        }

        override fun publishResults(constraint: CharSequence, results: FilterResults) {
//            itemList.clear()
            itemList = (results.values as? ArrayList<ReadLeadModel>)!!

            //   itemList.addAll(results.values as ArrayList<ReadLeadModel>)
            Log.d("Publishedresults", "publishResults: ${itemList.size} : ")
            notifyDataSetChanged()
            Log.d("publishResults", "msg")
        }
    }

}