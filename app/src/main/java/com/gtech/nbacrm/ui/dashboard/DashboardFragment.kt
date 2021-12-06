package com.gtech.nbacrm.ui.dashboard

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gtech.nbacrm.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DashboardFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DashboardFragment : Fragment() {
    private var param1: String? = null
    lateinit var mNavController: NavController
    private var param2: String? = null
    lateinit var mRecyclerView: RecyclerView
    lateinit var mAdapter: DashBoardAdapter
    var mList = ArrayList<DashBoardModel>()
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
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mNavController = Navigation.findNavController(view)
        mRecyclerView = view.findViewById(R.id.dashboard_recyclerview)
        mList.clear()
        mList.add(DashBoardModel("Leads", R.drawable.leads))
        mList.add(DashBoardModel("FollowUp", R.drawable.followup))
        mList.add(DashBoardModel("Converted", R.drawable.confirmed))
        mList.add(DashBoardModel("Tasks", R.drawable.tasks))
        mList.add(DashBoardModel("Cancelled Leads", R.drawable.ic_baseline_cancel_24))
        mList.add(DashBoardModel("Completed Leads", R.drawable.ic_baseline_check_24))
        mAdapter = DashBoardAdapter(this, mList)
        mRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        mRecyclerView.adapter = mAdapter
    }

}