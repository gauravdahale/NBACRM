package com.gtech.nbacrm.ui.dashboard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.gtech.nbacrm.R
import com.gtech.nbacrm.listeners.ItemClickListener

class DashBoardAdapter(val mContext: DashboardFragment,val mlist: ArrayList<DashBoardModel>?):RecyclerView.Adapter<DashBoardAdapter.DashboardHolder>() {
var currentuserid =FirebaseAuth.getInstance().currentUser?.uid
    class DashboardHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        var itemClickListener: ItemClickListener? = null
        val counter = view.findViewById<TextView>(R.id.i_dashboard_count)
        val title = view.findViewById<TextView>(R.id.i_dashboard_title)
        val image = view.findViewById<ImageView>(R.id.i_dashboard_image)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            itemClickListener?.onItemClick(adapterPosition)
        }

        fun setItemOnclickListener(itemClickListener: ItemClickListener) {
            this.itemClickListener = itemClickListener
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashboardHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_dashboard, parent, false)
        return DashboardHolder(v)
    }

    override fun onBindViewHolder(holder: DashboardHolder, position: Int) {
        val model = mlist?.get(holder.adapterPosition) as DashBoardModel
        holder.title.text = model?.name
        holder.image.setImageResource(model.image)

        getcount(position, holder.counter)
        holder.setItemOnclickListener(object : ItemClickListener {
            override fun onItemClick(i: Int) {
                navigatetofragment(position)
            }
        })
    }

    override fun getItemCount(): Int {
        if (mlist != null) {
            return mlist.size
        } else return 0
    }

    private fun navigatetofragment(position: Int) {
        when (position) {

//            0 -> mContext.mNavController.navigate(R.id.action_nav_dashboard_to_usersFragment)
            0 -> mContext.mNavController.navigate(R.id.action_navigation_dashboard_to_navigation_leads)
            1 -> mContext.mNavController.navigate(R.id.action_navigation_dashboard_to_navigation_follow_up)
            2 -> mContext.mNavController.navigate(R.id.action_navigation_dashboard_to_navigation_converted)
            3 -> mContext.mNavController.navigate(R.id.action_navigation_dashboard_to_navigation_tasks)
            4 -> mContext.mNavController.navigate(R.id.action_navigation_dashboard_to_closedLeadsFragment)
            5 -> mContext.mNavController.navigate(R.id.action_navigation_dashboard_to_completedFragment)

        }
    }
    private fun getcount(position: Int, counter: TextView,) {
        when(position){
            10->{ FirebaseDatabase.getInstance().reference.child("customers")
                .addListenerForSingleValueEvent(object :ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        counter.text =snapshot.childrenCount.toString()
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }

                })}
            0->{ FirebaseDatabase.getInstance().reference.child("leads")
                .addListenerForSingleValueEvent(object :ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        counter.text =snapshot.childrenCount.toString()
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }

                })}
            1->{ FirebaseDatabase.getInstance().reference.child("followup")
                .addListenerForSingleValueEvent(object :ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        counter.text =snapshot.childrenCount.toString()
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }

                })}
            2->{ FirebaseDatabase.getInstance().reference.child("converted")
                .addListenerForSingleValueEvent(object :ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        counter.text =snapshot.childrenCount.toString()
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }

                })}
            3->{ FirebaseDatabase.getInstance().reference.child("AllotedTasks/$currentuserid")
                .addListenerForSingleValueEvent(object :ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        counter.text =snapshot.childrenCount.toString()
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }

                })}
            4->{ FirebaseDatabase.getInstance().reference.child("closedleads")
                .addListenerForSingleValueEvent(object :ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        counter.text =snapshot.childrenCount.toString()
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }

                })}
            5->{ FirebaseDatabase.getInstance().reference.child("completed")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        counter.text =snapshot.childrenCount.toString()
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }

                })}

        }


    }
}