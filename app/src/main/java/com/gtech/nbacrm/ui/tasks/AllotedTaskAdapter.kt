package com.gtech.nbacrm.ui.tasks

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.gtech.nbacrm.R
import com.gtech.nbacrm.listeners.ItemClickListener
import kotlinx.android.synthetic.main.item_alloted_tasks.view.*

class AllotedTaskAdapter(val mContext: Context, val mlist: ArrayList<GetAllotedTaskModel>) :
    RecyclerView.Adapter<AllotedTaskAdapter.AllotedTaskHolder>() {
    val currentuser = FirebaseAuth.getInstance().currentUser?.uid.toString()
  inner  class AllotedTaskHolder(itemview: View) : RecyclerView.ViewHolder(itemview),
        View.OnClickListener {
        var itemClickListener: ItemClickListener? = null
        var taskName = itemview.findViewById<TextView>(R.id.i_alloted_task_name)
        var taskRemark = itemview.findViewById<TextView>(R.id.i_task_remark)
        var clientName = itemview.findViewById<TextView>(R.id.i_alloted_task_client_name)
        var allotedBy = itemview.findViewById<TextView>(R.id.i_task_alloted_by)
        var allotedTo = itemview.findViewById<TextView>(R.id.i_task_alloted_to)
        var duedate = itemview.findViewById<TextView>(R.id.i_alloted_task_date)
        var status = itemview.findViewById<Button>(R.id.i_task_alloted_status)
        var delete = itemview.findViewById<ImageButton>(R.id.delete_task)

        init {
            //     itemview.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            itemClickListener?.onItemClick(layoutPosition)
        }

        fun setItemOnClickListener(itemClickListener: ItemClickListener) {
            this.itemClickListener = itemClickListener
        }

        fun bind(model: GetAllotedTaskModel) {
            taskName.text = model.taskname
            clientName.text = model.clientname
            allotedBy.text = model.allotedby
        model.taskremark?.let { taskRemark.text = it }
            duedate.text = model.taskdate
            allotedTo.text = model.allotedto
            when (model.status) {
                "PENDING" -> {
                    status.text = "PENDING"
                    status.setBackgroundColor(mContext.resources.getColor(R.color.brown_500))

                }
                "IN PROGRESS" -> {
                    status.text = "IN PROGRESS"
                    status.setBackgroundColor(mContext.resources.getColor(R.color.deep_orange_900))
                }
                "COMPLETED" -> {
                    status.text = "COMPLETED"
                    status.setBackgroundColor(Color.BLUE)
                }
                "FAILED" -> {
                    status.text = "FAILED"
                    status.setBackgroundColor(Color.RED)
                }
            }
        status.setOnClickListener {
            setStatus(itemView,model)
        }
            if(currentuser == model.allotedbyid){
                itemView.delete_task.visibility =View.VISIBLE

            }
            else if(currentuser != model.allotedbyid){
                itemView.delete_task.visibility =View.GONE

            }
        itemView.delete_task.setOnClickListener {
            openDeleteDialog(mContext,model)
        }
        }

      private fun openDeleteDialog(mContext: Context, model: GetAllotedTaskModel) {
              val builder = androidx.appcompat.app.AlertDialog.Builder(mContext)
              builder.setCancelable(true)
              with(builder) {
                  setMessage("Are you sure you want to delete this task?")
                  setTitle("Delete Task")
                  setPositiveButton("Yes", DialogInterface.OnClickListener { _, _ ->
                      //     Toast.makeText(context, "Yes Clicked", Toast.LENGTH_SHORT).show()
                      FirebaseDatabase.getInstance().reference.child("AllotedTasks").child(model.allotedtoid.toString()).child(model?.key.toString()).removeValue().addOnCompleteListener {
                          FirebaseDatabase.getInstance().reference.child("AllotedTasks").child(model.allotedbyid.toString()).child(model?.key.toString()).removeValue().addOnSuccessListener {
                              Toast.makeText(context, "Task Deleted Successfully", Toast.LENGTH_SHORT).show()
                          }
                      }
                  })
                  setNegativeButton("No", DialogInterface.OnClickListener { _, _ ->
                      Toast.makeText(context, "No Clicked", Toast.LENGTH_SHORT).show()
                  })
              }
              builder.show()
      }

      private fun setStatus(view: View, item:GetAllotedTaskModel) {

            val items = arrayOf("PENDING", "IN PROGRESS", "COMPLETED", "FAILED")
            val builder = AlertDialog.Builder(itemView.context)
            with(builder)
            {
                setTitle("List of Items")
                setItems(items) { dialog, which ->
                    Toast.makeText(itemView.context, items[which] + " is clicked", Toast.LENGTH_SHORT).show()
                    item.status = items[which]
                val mhashMap =HashMap<String,Any>()
                    mhashMap["AllotedTasks/${item.allotedtoid}/${item.key}/status"]=items[which]
                    mhashMap["AllotedTasks/${item.allotedbyid}/${item.key}/status"]=items[which]
                  FirebaseDatabase.getInstance().reference.updateChildren(mhashMap)
                }

                //     setPositiveButton("OK", positiveButtonClick)
                show()
            }
        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AllotedTaskAdapter.AllotedTaskHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_alloted_tasks, parent, false)
        return AllotedTaskHolder(view)
    }

    override fun onBindViewHolder(holder: AllotedTaskAdapter.AllotedTaskHolder, position: Int) {
        val model = mlist[holder.adapterPosition]
        holder.bind(model)
    }

    override fun getItemCount() = mlist.size

    }