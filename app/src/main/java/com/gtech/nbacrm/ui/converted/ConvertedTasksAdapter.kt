package com.gtech.nbacrm.ui.converted

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.gtech.nbacrm.ItemDecoration
import com.gtech.nbacrm.R
import com.gtech.nbacrm.listeners.ItemClickListener
import com.gtech.nbacrm.ui.shared.CommentModel
import com.gtech.nbacrm.ui.shared.CommentsAdapter
import com.gtech.nbacrm.ui.tasks.ConvertedTasksModel
import com.gtech.nbacrm.ui.tasks.GetConvertedTasksModel
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class ConvertedTasksAdapter(private var mList: ArrayList<GetConvertedTasksModel>, private val mContext: Context, val parentmodel: GetConvertedModel) : RecyclerView.Adapter<ViewHolder>(),
        Filterable {
    private  val TAG = "ConvertedTasksAdapter"
    //    internal var currentuserid = FirebaseAuth.getInstance().currentUser!!.uid
    private val sharedprefFile = "shop_selected"
val mprefs = mContext.getSharedPreferences(sharedprefFile,Context.MODE_PRIVATE)
    val currentusername = mprefs.getString("USER_NAME","user")
    internal var filter: CustomFilter? = null

    internal var itemList: ArrayList<GetConvertedTasksModel> = mList

    internal var arrayList: ArrayList<GetConvertedTasksModel> = mList

    val firebaseDatabase = FirebaseDatabase.getInstance().reference.child("converted/${parentmodel.key}/tasks")

    private var counter :  Int = 0


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var viewHolder: ViewHolder? = null
//        when (viewType) {


                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_convert_task_switch, parent, false)
                return Type1Holder(view)
//            }
//
//            2 -> {
//                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_task_withtext, parent, false)
//                return Type2Holder(view)
//
//            }
//
//            3 -> {
//                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_task_switchandtext, parent, false)
//                return Type2Holder(view)
//
//            }
//
//            else -> {
//                val view = LayoutInflater.from(parent.context).inflate(
//                        R.layout.item_convert_task_switch, parent,
//                        false
//                )
//                return Type1Holder(
//                        view
//                )
//            }
//        }
    }

    inner class Type1Holder(v: View) : ViewHolder(v), View.OnClickListener {
        private var itemClickListener: ItemClickListener? = null
        var name = v.findViewById<TextView>(R.id.i_task_name)
        var date = v.findViewById<TextView>(R.id.i_task_date)
        var doneby = v.findViewById<TextView>(R.id.i_task_done_by)
        var comment = v.findViewById<TextView>(R.id.i_task_comment)
        var isdone = v.findViewById<Button>(R.id.i_task_switch)
        var isconfrim = v.findViewById<ImageButton>(R.id.i_task_confirm)

        init { itemView.setOnClickListener(this) }

        fun setItemOnClickListener(itemClickListener: ItemClickListener) {
            this.itemClickListener = itemClickListener
        }

        override fun onClick(v: View) {
            this.itemClickListener!!.onItemClick(adapterPosition)
        }

        fun bind1(item: GetConvertedTasksModel) {
            val p = adapterPosition
            name.text = item.name
//if (item.done==true) isdone.isChecked
            if (item.done == true) {
                try {
                    isdone.text = "Yes"
                    isdone.setBackgroundColor(itemView.resources.getColor(R.color.blue_900))
             //       notifyDataSetChanged()
                } catch(e:Exception){
                    Log.e(TAG, "bind1: ${e.message}", )
                }
            } else if (!item.done!!) {

try {
    isdone.text = "No"
    isdone.setBackgroundColor(itemView.resources.getColor(R.color.red_900))
} catch(e:Exception){
    Log.e(TAG, "bind1: ${e.message}", )
}
            }
            doneby.text = item.doneby
            isdone.setOnClickListener {
                Log.d(TAG, "bind1:${item.name} ")
                FirebaseDatabase.getInstance().reference.child("converted").child("${parentmodel.key}/tasks/${item.key}").child("done").setValue(!item.done!!)
                FirebaseDatabase.getInstance().reference.child("converted").child("${parentmodel.key}/tasks/${item.key}").child("doneby").setValue(currentusername)
                FirebaseDatabase.getInstance().reference.child("converted").child("${parentmodel.key}/updatedat").setValue(ServerValue.TIMESTAMP)
                item.done =!item.done!!
            }
            isconfrim.setOnClickListener {
                FirebaseDatabase.getInstance().reference.child("converted").child("${parentmodel.key}/tasks/${item.key}").child("confirm").setValue(!item.confirm)
                //FirebaseDatabase.getInstance().reference.child("converted").child("${parentmodel.key}/tasks/$p").child("doneby").setValue(currentusername)
                item.confirm =!item.confirm!!
            }

//            isdone.setChecked(item.done!!)
            itemView.setOnClickListener {
                addComment(itemView, itemView.context, item, adapterPosition)
            }

        }

    }

    inner class Type2Holder(v: View) : ViewHolder(v), View.OnClickListener {
        override fun onClick(p0: View?) {
        }

        fun bind2(items: GetConvertedTasksModel) {
        }
    }

    override fun getItemViewType(position: Int): Int {

        return when (itemList[position].type) {

            1 -> 1
            2 -> 2
            3 -> 3
            else -> 1
        }

    }

    override fun getItemCount(): Int {
//        return itemList.size
        return itemList.size


    }

    fun currentList(): ArrayList<GetConvertedTasksModel> {
        return itemList
    }

    override fun getFilter(): Filter {
        if (filter == null) {
            filter = CustomFilter()
        }
        return filter as CustomFilter
    }

    private fun commentsDialog(comments: java.util.HashMap<String, CommentModel>?) {
        val clist = ArrayList<CommentModel>()
        val dialog = Dialog(mContext)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_comments_recyclerview)
        val recyclerView = dialog.findViewById<RecyclerView>(R.id.comments_recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(mContext)
        Log.d("commentsDialog", "${comments?.size}")
        comments?.forEach {
            val model = it.value
            clist.add(model)
            Log.d("COMMENTSDIALOG", "commentsDialog:${model.comment} ")
        }
        val commentsAdapter = CommentsAdapter(mContext, clist)
        recyclerView.addItemDecoration(ItemDecoration(mContext))
        recyclerView.adapter = commentsAdapter
        dialog.show()
    }

    internal inner class CustomFilter : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            var constraint = constraint

            val results = FilterResults()
            if (constraint != null && constraint.isNotEmpty()) {
                constraint = constraint.toString().uppercase(Locale.getDefault())
                val filteredlist = ArrayList<GetConvertedTasksModel>()
                for (i in arrayList.indices) {
                    Log.d("performFiltering", "arraylist Item : ${arrayList[i].name}")
                    if (arrayList[i].tasktype!!.uppercase(Locale.getDefault()).contains(constraint)) {
                        val l = arrayList[i]
                        filteredlist.add(l)
                        Log.d("performFiltering", "msg: ${l.name}")
                    }
                }

                results.count = filteredlist.size
                results.values = filteredlist

            } else {
                results.count = arrayList.size
                results.values = arrayList
                Log.d("performFiltering", "arraylistsize ${arrayList.size}")
                Toast.makeText(mContext, "Performing results", Toast.LENGTH_SHORT).show()
            }
            return results
        }

        override fun publishResults(constraint: CharSequence, results: FilterResults) {
//            itemList.clear()
            itemList = results.values as ArrayList<GetConvertedTasksModel>

            //   itemList.addAll(results.values as ArrayList<GetConvertedTasksModel>)
            Log.d("Publishedresults", "publishResults: ${itemList.size} : ")
            notifyDataSetChanged()
            Log.d("publishResults", "msg")
        }
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        val items = itemList[viewHolder.adapterPosition]
        when (items.type) {
            1 -> {
                Log.d("onBindViewHolder", "msg")
                val holder = viewHolder as Type1Holder
                if (items.timestamp != null) holder.date.text = items.getTimeDate(items.timestamp!!)
                holder.comment.text = items.comment
                holder.itemView.setOnLongClickListener {
                    commentsDialog(comments = items.comments)
                    true
                }
//                holder.listener = (CompoundButton.OnCheckedChangeListener { p0, b ->
//
//                    FirebaseDatabase.getInstance().reference.child("converted").child("${parentmodel.key}/tasks/${holder.layoutPosition}").child("done").setValue(b)
////                            .addOnSuccessListener { FirebaseDatabase.getInstance().reference.child("converted").child("${parentmodel.key}/tasks/${holder.layoutPosition}").child("timestamp").setValue(ServerValue.TIMESTAMP) }
//
//                })


                viewHolder.bind1(items)


            }
            2 -> {
                val holder = viewHolder as Type2Holder

                viewHolder.bind2(items)
            }

        }

    }

    fun addComment(view: View, context: Context, items: GetConvertedTasksModel, position: Int) {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_add_comment)
        val edittext = dialog.findViewById<TextInputEditText>(R.id.converted_comment_edit)
        val submit = dialog.findViewById<FloatingActionButton>(R.id.btn_submit_comment)
        submit.setOnClickListener {
            if (edittext.text.toString().isNotEmpty()) {
                val mHashMap = HashMap<String, Any>()
                mHashMap["comment"] = edittext.text.toString()
                mHashMap["lastcomment"] = edittext.text.toString()
                mHashMap["timestamp"] = ServerValue.TIMESTAMP
                mHashMap["updatedat"] = ServerValue.TIMESTAMP

                mHashMap["commentedby"] = "${currentusername.toString()}"
                val commentmap = HashMap<String, Any>()
                commentmap["comment"] = edittext.text.toString()
                commentmap["commentedby"] = currentusername.toString()
                commentmap["timestamp"] = ServerValue.TIMESTAMP
                val key = firebaseDatabase.child("${items.key}/comments").push().key
                mHashMap["comments/${key}"] = commentmap
                firebaseDatabase.child(items.key.toString()).updateChildren(mHashMap)
                        .addOnSuccessListener {

                            firebaseDatabase.parent?.child("updatedat")?.setValue(ServerValue.TIMESTAMP)
                            mList[position].comment = edittext.text.toString()
                            notifyItemChanged(position)
                            dialog.dismiss()
                        }
            } else {
                Toast.makeText(mContext, "Please enter something!!!", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()

    }

}


