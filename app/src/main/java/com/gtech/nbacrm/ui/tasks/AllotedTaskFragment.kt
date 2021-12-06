package com.gtech.nbacrm.ui.tasks

import android.os.Bundle
import android.renderscript.Sampler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.gtech.nbacrm.ItemDecoration
import com.gtech.nbacrm.R
import kotlinx.android.synthetic.main.fragment_alloted_task.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class AllotedTaskFragment : Fragment() {

    lateinit var mNavController: NavController
    private var param1: String? = null
    private var param2: String? = null
    lateinit var mAdapter: AllotedTaskAdapter
    lateinit var listener: ValueEventListener
    lateinit var mRecyclerView: RecyclerView
    val currentuser = FirebaseAuth.getInstance().currentUser?.uid
    var mlist = ArrayList<GetAllotedTaskModel>()
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
        return inflater.inflate(R.layout.fragment_alloted_task, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mNavController = Navigation.findNavController(view)
        mRecyclerView = view.findViewById(R.id.alloted_tasks_recyclerview)
        mAdapter = AllotedTaskAdapter(requireContext(), mlist)
        mRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        mRecyclerView.addItemDecoration(ItemDecoration(requireContext()))
        mRecyclerView.adapter = mAdapter
        getAlllotedTasks()
        fab_alloted_tasks.setOnClickListener {
            mNavController.navigate(R.id.action_navigation_tasks_to_createTaskFragment)
        }

    }

    private fun getAlllotedTasks() {
        val mReference = Firebase.database.reference.child("AllotedTasks").child(currentuser!!)
        listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                mlist.clear()
                snapshot.children.forEach {
                    val model = it.getValue(GetAllotedTaskModel::class.java)
                    model?.key = it.key
                    mlist.add(model!!)
                }
                mAdapter.notifyDataSetChanged()
          mRecyclerView.scheduleLayoutAnimation()
            }


            override fun onCancelled(error: DatabaseError) {
            }

        }
        mReference.addValueEventListener(listener)
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AllotedTaskFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}