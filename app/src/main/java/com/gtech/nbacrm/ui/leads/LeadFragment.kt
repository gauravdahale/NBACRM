package com.gtech.nbacrm.ui.leads

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.SearchView
import android.widget.SimpleAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.gtech.nbacrm.R
import com.gtech.nbacrm.databinding.FragmentLeadsBinding
import com.gtech.nbacrm.ui.SwipeToDeleteCallback
import kotlinx.android.synthetic.main.fragment_leads.*
import java.time.temporal.ValueRange

class LeadFragment : Fragment() {
    lateinit var mNavController: NavController
    var mList = ArrayList<ReadLeadModel>();
    val mKeys = ArrayList<String>();
    lateinit var mAdapter: LeadsAdapter
    var childlistener: ChildEventListener? = null
    lateinit var mRefrence: Query
    var _binding: FragmentLeadsBinding? = null
    val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLeadsBinding.inflate(layoutInflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mNavController = Navigation.findNavController(view)
        mRefrence = FirebaseDatabase.getInstance().reference.child("leads").orderByChild("updatedat")
        mAdapter = LeadsAdapter(this, mList)
        getdata()
        val linearLayoutManager = LinearLayoutManager(requireContext())
        linearLayoutManager.stackFromEnd = true
        linearLayoutManager.reverseLayout = true

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
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
                mAdapter = lead_recyclerview.adapter as LeadsAdapter
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
FirebaseDatabase.getInstance().reference.child("closedleads").child(mList[viewHolder.adapterPosition].key!!).setValue(mList[viewHolder.adapterPosition]).addOnCompleteListener {
    if(it.isSuccessful)                 FirebaseDatabase.getInstance().reference.child("leads").child(mList[viewHolder.adapterPosition].key!!).removeValue().addOnCompleteListener {
        if(it.isSuccessful)                mAdapter.notifyItemRemoved(viewHolder.adapterPosition)

    }


}

            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(binding.leadRecyclerview)


        binding.leadRecyclerview.apply {
            lead_recyclerview.layoutManager = linearLayoutManager
            adapter = mAdapter
            itemAnimator = DefaultItemAnimator()
        }
        binding.fabLeads.setOnClickListener {
            mNavController.navigate(R.id.action_navigation_home_to_createLead)

        }
    }

    private fun getdata() {
        mKeys.clear()
        mList.clear()
        childlistener = object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val model = snapshot.getValue(ReadLeadModel::class.java)
                mKeys.add(snapshot.key.toString())
                model?.key = snapshot.key
                mList.add(model!!)
                Log.d("onChildAdded", "msg")
                Log.d("onChildAdded", "mlist : ${mList.size}")
                Log.d("onChildAdded", "mkeys : ${mKeys.size}")
                mAdapter.notifyItemInserted(mKeys.indexOf(snapshot.key.toString()))
                binding.leadRecyclerview.scheduleLayoutAnimation()

            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val model = snapshot.getValue(ReadLeadModel::class.java)
                val index = mKeys.indexOf(snapshot.key)
                if (index > -1) {
                    mList[index] = model!!
                    mAdapter.notifyItemChanged(index)
                    binding.leadRecyclerview?.let { it.scheduleLayoutAnimation() }

                } else Log.w("childchanged", "Unknown Child")


            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                val key = snapshot.key
                val index = mKeys.indexOf(key)

                if (index > -1) {
                    mKeys.removeAt(index)
                    mList.removeAt(index)
                    mAdapter.notifyItemRemoved(index)
mAdapter.notifyDataSetChanged()
                }
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onCancelled(error: DatabaseError) {
            }

        }
        mRefrence
            .addChildEventListener(
                childlistener!!
            )

    }

    override fun onDestroyView() {
        super.onDestroyView()
        mRefrence.removeEventListener(childlistener!!)
    }
}