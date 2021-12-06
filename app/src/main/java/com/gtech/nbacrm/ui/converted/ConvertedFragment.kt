package com.gtech.nbacrm.ui.converted

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.gtech.nbacrm.R
import com.gtech.nbacrm.databinding.FragmentConvertedBinding
import com.gtech.nbacrm.ui.SwipeToDeleteCallback
import com.gtech.nbacrm.ui.followup.FollowUpAdapter
import kotlinx.android.synthetic.main.fragment_converted.*
import kotlinx.android.synthetic.main.fragment_followup.*
import kotlinx.android.synthetic.main.fragment_leads.*
import kotlinx.android.synthetic.main.fragment_leads.searchView

class ConvertedFragment : Fragment() {
    var _binding: FragmentConvertedBinding? = null
    val binding get() = _binding!!
    lateinit var mNavController: NavController
    val mList = ArrayList<GetConvertedModel>();
    val mKeys = ArrayList<String>();
    lateinit var mAdapter: ConvertedAdapter
    lateinit var listener: ValueEventListener
    var sort: String? = "Preconsultation"
    lateinit var mRef: Query
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentConvertedBinding.inflate(layoutInflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mNavController = Navigation.findNavController(view)
        mRef = FirebaseDatabase.getInstance().reference.child("converted").orderByChild("type").equalTo(sort)
//        mRef = FirebaseDatabase.getInstance().reference.child("converted").orderByChild("updatedat")
        binding.before.setTextColor(ContextCompat.getColor(requireContext(), R.color.amber_400))
        binding.before.setOnClickListener {
            before.setTextColor(ContextCompat.getColor(requireContext(), R.color.amber_400))
            after.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            sort = "Preconsultation"

            getConverted(sort!!)
        }
        binding.after.setOnClickListener {
            binding.after.setTextColor(ContextCompat.getColor(requireContext(), R.color.amber_400))
            binding.before.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            sort = "Postconsultation"
            getConverted(sort!!)
        }

        mAdapter = ConvertedAdapter(this, mList)
        getConverted(sort!!)
        val linearLayoutManager = LinearLayoutManager(requireContext())
        linearLayoutManager.stackFromEnd = true
        linearLayoutManager.reverseLayout = true

        binding.convertedRecyclerview.apply {
            binding.convertedRecyclerview.layoutManager = linearLayoutManager
            adapter = mAdapter
            itemAnimator = DefaultItemAnimator()
        }
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
                mAdapter = binding.convertedRecyclerview.adapter as ConvertedAdapter
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
                FirebaseDatabase.getInstance().reference.child("deletedconverted")
                    .child(mList[viewHolder.adapterPosition].key!!)
                    .setValue(mList[viewHolder.adapterPosition]).addOnCompleteListener {
                        if (it.isSuccessful) FirebaseDatabase.getInstance().reference.child("converted")
                            .child(mList[viewHolder.adapterPosition].key!!).removeValue()
                            .addOnCompleteListener {
                                if (it.isSuccessful) mAdapter.notifyItemRemoved(viewHolder.adapterPosition)

                            }


                    }

            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(binding.convertedRecyclerview)


    }

    private fun getdata(sort: String) {
        mKeys.clear()
        mList.clear()
        FirebaseDatabase.getInstance().reference.child("converted").orderByChild("type")
            .equalTo(sort)
            .addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val model = snapshot.getValue(GetConvertedModel::class.java)
                    mKeys.add(snapshot.key.toString())
                    model?.key = snapshot.key
                    mList.add(model!!)
//                    Log.d("onChildAdded", "msg")
//                    Log.d("onChildAdded", "mlist : ${mList.size}")
//                    Log.d("onChildAdded", "mkeys : ${mKeys.size}")
                    mAdapter.notifyItemInserted(mKeys.indexOf(snapshot.key.toString()))
                    binding.convertedRecyclerview.scheduleLayoutAnimation()
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    val model = snapshot.getValue(GetConvertedModel::class.java)
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

    private fun getConverted(sort: String) {
        mKeys.clear()
        mList.clear()
        mRef = FirebaseDatabase.getInstance().reference.child("converted")
            .orderByChild("type").equalTo(sort)

        listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                mList.clear()
                snapshot.children.forEach {
                    val model = it.getValue(GetConvertedModel::class.java)
//                    if(model?.type==null) {
//                        FirebaseDatabase.getInstance().reference.child("converted").child(it.key!!).child("type").setValue("Before")
//                    }

                    mList.add(model!!)

                }
                mList.sortByDescending{it.updatedat}

                mAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
            }


        }
        mRef.addValueEventListener(listener)
    }

    override fun onDestroyView() {
        mRef.removeEventListener(listener)

        super.onDestroyView()
        _binding = null
    }

}