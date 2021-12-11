package com.gtech.nbacrm.ui.completed

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.SearchView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.gtech.nbacrm.R
import com.gtech.nbacrm.databinding.FragmentCompletedBinding
import com.gtech.nbacrm.databinding.FragmentConvertedBinding
import com.gtech.nbacrm.ui.SwipeToDeleteCallback
import com.gtech.nbacrm.ui.converted.GetConvertedModel
import kotlinx.android.synthetic.main.fragment_converted.*
import kotlinx.android.synthetic.main.fragment_converted.searchView
import kotlinx.android.synthetic.main.fragment_leads.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class CompletedFragment : Fragment() {
    var _binding: FragmentCompletedBinding? = null
    val binding get() = _binding!!
    lateinit var mNavController: NavController
    val mList = ArrayList<GetConvertedModel>();
    val mKeys = ArrayList<String>();
    lateinit var mAdapter: CompletedAdapter
    lateinit var listener: ValueEventListener
    lateinit var mRef: Query
    var sort: String? = "Preconsultation"
        override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
            _binding = FragmentCompletedBinding.inflate(layoutInflater, container, false)
            val view = binding.root
            return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mNavController = Navigation.findNavController(view)
        mRef = FirebaseDatabase.getInstance().reference.child("completed")
//        mRef = FirebaseDatabase.getInstance().reference.child("converted").orderByChild("updatedat")
        binding.before.setTextColor(ContextCompat.getColor(requireContext(), R.color.amber_400))
        Toast.makeText(requireContext(), "Completed Fragment", Toast.LENGTH_SHORT).show()
        Log.d("NBA", "onViewCreated: in completed fragment ")
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

        mAdapter = CompletedAdapter(this, mList)
        getConverted(sort!!)
        val linearLayoutManager = LinearLayoutManager(requireContext())
        linearLayoutManager.stackFromEnd = true
        linearLayoutManager.reverseLayout = true

        binding.completedRecyclerview.apply {
            binding.completedRecyclerview.layoutManager = linearLayoutManager
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



                return true

            }

            override fun onQueryTextChange(p0: String?): Boolean {
                //Start filtering the list as user start entering the characters
                mAdapter = binding.completedRecyclerview.adapter as CompletedAdapter
                Log.d("onQueryTextChange", "msg $p0")
                mAdapter?.getFilter()?.filter(p0)
                //listadapter?.notifyDataSetChanged()
                return true
            }
        })



    }


    private fun getConverted(sort: String) {
        mKeys.clear()
        mList.clear()
        mRef = FirebaseDatabase.getInstance().reference.child("completed")


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