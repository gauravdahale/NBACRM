package com.gtech.nbacrm.ui.followup

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.MediaController
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.gtech.nbacrm.R
import com.gtech.nbacrm.databinding.FragmentFollowupBinding
import com.gtech.nbacrm.ui.SwipeToDeleteCallback
import com.gtech.nbacrm.ui.followup.FollowUpAdapter
import com.gtech.nbacrm.ui.leads.LeadsAdapter
import kotlinx.android.synthetic.main.fragment_followup.*
import kotlinx.android.synthetic.main.fragment_leads.*
import kotlinx.android.synthetic.main.fragment_leads.searchView

class FollowUpFragment : Fragment() {
    lateinit var mAdapter: FollowUpAdapter
    lateinit var mRecyclerView: RecyclerView
    var _binding:FragmentFollowupBinding?=null
    val binding get() = _binding!!
    var mList = ArrayList<GetFollowUpModel>()
    val mKeys = ArrayList<String>();
    lateinit var mNavController: NavController
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
      _binding  = FragmentFollowupBinding.inflate(layoutInflater,container,false)
        val view =binding.root
        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mNavController = Navigation.findNavController(view)
        mRecyclerView = view.findViewById<RecyclerView>(R.id.followup_recyclerview)
        val linearLayoutManager = LinearLayoutManager(requireContext())
        linearLayoutManager.stackFromEnd = true
        linearLayoutManager.reverseLayout = true
        mRecyclerView.layoutManager = linearLayoutManager
        mAdapter = FollowUpAdapter(this, mList)
        mRecyclerView.adapter = mAdapter
        getfollowups()
        mAdapter.notifyDataSetChanged()
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                val inputMethod =
                    requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethod.hideSoftInputFromWindow(searchView?.windowToken, 0)
//                listadapter =
//                    mRecyclerview.adapter as ItemListAdapter?
                //  listadapter?.filter?.filter(p0)
//                listadapter?.notifyDataSetChanged()


                Toast.makeText(
                    activity!!.applicationContext, "Search Completed...",
                    Toast.LENGTH_SHORT
                ).show()
                return true

            }

            override fun onQueryTextChange(p0: String?): Boolean {
                //Start filtering the list as user start entering the characters
                mAdapter = followup_recyclerview.adapter as FollowUpAdapter
                Log.d("onQueryTextChange", "msg $p0")
                mAdapter?.getFilter()?.filter(p0)
                //listadapter?.notifyDataSetChanged()
                Toast.makeText(
                    requireActivity(),
                    "On Text Change ${mAdapter?.mList?.size}",
                    Toast.LENGTH_SHORT
                ).show()
                return true
            }
        })

        val swipeHandler = object : SwipeToDeleteCallback(requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                FirebaseDatabase.getInstance().reference.child("deletedfollowup")
                    .child(mList[viewHolder.adapterPosition].key!!)
                    .setValue(mList[viewHolder.adapterPosition]).addOnCompleteListener {
                        if (it.isSuccessful) FirebaseDatabase.getInstance().reference.child("followup")
                            .child(mList[viewHolder.adapterPosition].key!!).removeValue()
                            .addOnCompleteListener {
                                if (it.isSuccessful) mAdapter.notifyItemRemoved(viewHolder.adapterPosition)

                            }


                    }

            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(binding.followupRecyclerview)


    }

    override fun onDestroyView() {
        super.onDestroyView()
    _binding =null
    }

    fun getfollowups() {
        mKeys.clear()
        mList.clear()
        FirebaseDatabase.getInstance().reference.child("followup").orderByChild("updatedat")
            .addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val model = snapshot.getValue(GetFollowUpModel::class.java)
                    mKeys.add(snapshot.key.toString())
                    model?.key = snapshot.key
                    mList.add(model!!)
                    Log.d("onChildAdded", "msg")
                    Log.d("onChildAdded", "mlist : ${mList.size}")
                    Log.d("onChildAdded", "mkeys : ${mKeys.size}")
                    mAdapter.notifyItemInserted(mKeys.indexOf(snapshot.key.toString()))
                mRecyclerView.scheduleLayoutAnimation()
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

                    val model = snapshot.getValue(GetFollowUpModel::class.java)
                    val index = mKeys.indexOf(snapshot.key)
                    if (index > -1) {
                        mList[index] = model!!
                        mAdapter.notifyItemChanged(index)

                    } else Log.w("childchanged", "Unknown Child")


                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    val key = snapshot.key
                    val index = mKeys.indexOf(key)

                    if (index > -1) {
                        mKeys.removeAt(index)
                        mList.removeAt(index)

                    }
                    mAdapter.notifyItemRemoved(index)
                    mAdapter.notifyDataSetChanged()

                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
    }
}