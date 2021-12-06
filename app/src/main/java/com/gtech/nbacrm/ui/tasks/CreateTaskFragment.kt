package com.gtech.nbacrm.ui.tasks

import android.app.DatePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Toast
import androidx.core.content.ContentProviderCompat
import androidx.core.content.ContentProviderCompat.requireContext
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.gtech.nbacrm.R
import com.gtech.nbacrm.databinding.FragmentCreateTaskBinding
import com.gtech.nbacrm.ui.auth.GetUserModel
import com.gtech.nbacrm.ui.auth.UserModel
import com.gtech.nbacrm.ui.leads.ReadLeadModel
import kotlinx.android.synthetic.main.dialog_add_comment.view.*
import kotlinx.android.synthetic.main.fragment_create_lead.*
import kotlinx.android.synthetic.main.fragment_create_task.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class CreateTaskFragment : Fragment() {
    var clientListener: ValueEventListener? = null
    var userListener: ValueEventListener? = null
    private var param1: String? = null
    private var param2: String? = null
    private var userRef: DatabaseReference? = null
    private var clientRef: DatabaseReference? = null
    var TOKENID = ""
    val list = ArrayList<String>()
    val currentuserid = FirebaseAuth.getInstance().currentUser?.uid
    var selectedtaskduedate: String? = null
    lateinit var mClientName: AutoCompleteTextView
    lateinit var mTaskName: AutoCompleteTextView
    lateinit var mTaskAllotedTo: AutoCompleteTextView
    lateinit var mTaskDueDate: TextInputEditText
    lateinit var mTaskSubmitButton: Button
    lateinit var mSharedPreferences: SharedPreferences
    private val sharedprefFile = "shop_selected"
    lateinit var mCurrentUserName: String
    lateinit var allotedtoId: String
    val ulist = ArrayList<String>()
    private  val TAG = "CreateTaskFragment"
    var _binding: FragmentCreateTaskBinding? = null
    val binding get() = _binding!!
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
        _binding = FragmentCreateTaskBinding.inflate(layoutInflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mSharedPreferences =
            requireActivity().getSharedPreferences(sharedprefFile, Context.MODE_PRIVATE)
        mCurrentUserName = mSharedPreferences.getString("USER_NAME", "user") as String
        Toast.makeText(requireContext(), "$mCurrentUserName", Toast.LENGTH_SHORT).show()
        mClientName = binding.createTaskClientName
        mTaskName = binding.createTaskTaskComment
        mTaskDueDate = binding.createTaskDate
        mTaskAllotedTo = binding.createTaskTaskAllotto
        mTaskSubmitButton = binding.submitTaskButton
        mTaskDueDate.setOnClickListener {
            val cal = Calendar.getInstance()
            val date = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                val dateFormat = "dd-MM-yyyy "
                val sdf = SimpleDateFormat(dateFormat, Locale.US)
                mTaskDueDate.setText(sdf.format(cal.time))
                selectedtaskduedate = sdf.format(cal.time)


            }
            DatePickerDialog(
                requireContext(),
                date,
                // set DatePickerDialog to point to today's date when it loads up
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
//Comments Sections
        val commentlist = arrayOf("CALL", "EMAIL", "MEETING", "PAYMENT REMINDER")
        val commentlistAdapter = ArrayAdapter<String>(
            requireContext(),
            R.layout.support_simple_spinner_dropdown_item,
            commentlist
        )
        setListeners()
        mTaskName.setAdapter(commentlistAdapter)
        getClients()
        getUsers()
        mTaskSubmitButton.setOnClickListener {
            if (
                mTaskName.text.toString().isNotEmpty() &&
                mTaskAllotedTo.text.toString().isNotEmpty() &&
                mClientName.text.toString().isNotEmpty() &&
                mTaskDueDate.text.toString().isNotEmpty()
            ) {
                val taskModel = AllotedTaskModel().apply {
                    allotedby = mCurrentUserName
                    taskdate = mTaskDueDate.text.toString()
                    allotedbyid = currentuserid
                    allotedtoid = allotedtoId
                    allotedtotoken = TOKENID
                    clientname = mClientName.text.toString()
                    allotedto = mTaskAllotedTo.text.toString()
                    taskname = mTaskName.text.toString()
                    taskremark = binding.taskremark.text.toString()
                }
                val postkey = FirebaseDatabase.getInstance().reference.child("AllotedTasks")
                    .child(currentuserid!!).push().key
                val mHashmap = HashMap<String, Any>()
                mHashmap["AllotedTasks/$currentuserid/$postkey"] = taskModel
                mHashmap["AllotedTasks/${allotedtoId}/$postkey"] = taskModel
                FirebaseDatabase.getInstance().reference.updateChildren(mHashmap)
                    .addOnSuccessListener {
                        Toast.makeText(
                            requireContext(),
                            "Task alloted successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                        requireActivity().onBackPressed()
                    }
            }

        }

    }


    private fun setListeners() {
        clientListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                list.clear()
                snapshot.children.forEach {
                    val model = it.getValue(ReadLeadModel::class.java)
                    list.add(model?.clientname!!)

                }
                val clientlistAdapter = ArrayAdapter<String>(
                    requireContext(),
                    R.layout.support_simple_spinner_dropdown_item,
                    list
                )
                mClientName.setAdapter(clientlistAdapter)
//                mLeadPriority.setOnItemClickListener { adapterView, view, i, l ->
//                    selectedpriority = adapterView.getItemAtPosition(i) as String
//
//                }

            }

            override fun onCancelled(error: DatabaseError) {
            }
        }

        userListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                ulist.clear()
                val userlist = ArrayList<GetUserModel>()
                snapshot.children.forEach {
                    val model = it.getValue(GetUserModel::class.java)
                    model?.key = it.key
                    ulist.add(model?.name!!)
                    userlist.add(model)
                }
                val userlistadapter = ArrayAdapter<String>(
                    requireContext(),
                    R.layout.support_simple_spinner_dropdown_item,
                    ulist
                )
                mTaskAllotedTo.setAdapter(userlistadapter)
                Log.d(TAG, "onDataChange: User List ${ulist.size} ")
                mTaskAllotedTo.setOnItemClickListener { adapterView, view, i, l ->
                    allotedtoId = userlist[i].key!!
                    TOKENID = userlist[i].usertoken.toString()
                    Log.d("TaskUserList", "$allotedtoId")
                }

            }

            override fun onCancelled(error: DatabaseError) {
            }


        }
    }

    private fun getClients() {
        clientRef = FirebaseDatabase.getInstance().reference.child("allclients")
        clientRef?.addValueEventListener(clientListener!!)

    }

    private fun getUsers() {
       userRef  =  FirebaseDatabase.getInstance().reference.child("users")

        userRef?.addValueEventListener(userListener!!)


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        userRef?.removeEventListener(userListener!!)
        clientRef?.removeEventListener(clientListener!!)
    }
}