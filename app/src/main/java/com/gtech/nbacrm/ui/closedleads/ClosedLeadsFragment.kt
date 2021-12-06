package com.gtech.nbacrm.ui.closedleads

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import com.gtech.nbacrm.R
import com.gtech.nbacrm.databinding.FragmentClosedLeadsBinding
import com.gtech.nbacrm.ui.leads.ReadLeadModel
import com.gtech.nbacrm.ui.leads.LeadsAdapter
import kotlinx.android.synthetic.main.fragment_leads.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ClosedLeadsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ClosedLeadsFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    var mList = ArrayList<ReadLeadModel>();
    val mKeys = ArrayList<String>();
    lateinit var mRefrence :DatabaseReference
    var childlistener: ChildEventListener? = null
    lateinit var mAdapter :ClosedLeadsAdapter
    private var _binding: FragmentClosedLeadsBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentClosedLeadsBinding.inflate(layoutInflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mRefrence =   FirebaseDatabase.getInstance().reference.child("closedleads")
getdata()
        mAdapter = ClosedLeadsAdapter(this,mList)
        binding.closedRecyclerview.apply {
        layoutManager =LinearLayoutManager(requireContext())
        adapter =mAdapter
    }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ClosedLeadsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ClosedLeadsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
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
                binding.closedRecyclerview.scheduleLayoutAnimation()

            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val model = snapshot.getValue(ReadLeadModel::class.java)
                val index = mKeys.indexOf(snapshot.key)
                if (index > -1) {
                    mList[index] = model!!
                    mAdapter.notifyItemChanged(index)
                    binding.closedRecyclerview?.let { it.scheduleLayoutAnimation() }

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
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        }
        mRefrence
            .addChildEventListener(
                childlistener!!
            )

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        mRefrence.removeEventListener(childlistener!!)

    }
}