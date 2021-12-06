package com.gtech.nbacrm.ui.followup

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
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.gtech.nbacrm.R
import com.gtech.nbacrm.ui.tasks.ConvertedTasksModel
import kotlinx.android.synthetic.main.fragment_follow_up_detail.*
import kotlinx.android.synthetic.main.fragment_update_lead.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class FollowUpDetailFragment : Fragment() {
    private var param1: String? = null
    lateinit var mNavController: NavController
    private var param2: String? = null
    lateinit var mName: TextInputEditText
    lateinit var mNumber: TextInputEditText
    lateinit var mCity: TextInputEditText
    lateinit var mCarpetArea: TextInputEditText
    lateinit var mLastCall: TextInputEditText
    lateinit var mAddress: TextInputEditText
    lateinit var mLastComment: TextView
    lateinit var mNextCall: TextInputEditText
    lateinit var mAddComment: TextInputEditText
    var getmodel: GetFollowUpModel? = null
    var lastcalldateselected: String? = null
    private val sharedprefFile = "shop_selected"
    lateinit var mLeadPriority: AutoCompleteTextView
    lateinit var mType: AutoCompleteTextView
    var selectedpriority: String? = null
    var selectedtype: String = "Before"
    lateinit var mCurrentUserName: String
    var nextcalldateselected: String? = null
    lateinit var mSharedPreferences: SharedPreferences
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
        return inflater.inflate(R.layout.fragment_follow_up_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mNavController = Navigation.findNavController(view)

        getmodel = arguments?.getSerializable("parcel") as GetFollowUpModel?
        mName = view.findViewById(R.id.create_followup_name)
        mNumber = view.findViewById(R.id.create_followup_number)
        mCity = view.findViewById(R.id.create_followup_city)
        mCarpetArea = view.findViewById(R.id.create_followup_carpetarea)
        mLastCall = view.findViewById(R.id.create_last_call_date)
        mNextCall = view.findViewById(R.id.create_next_call_date)
        mLastComment = view.findViewById(R.id.last_follow_comment)
        mAddComment = view.findViewById(R.id.add_followup_comment)
        mLeadPriority = view.findViewById(R.id.update_followup_priority)
        mType = view.findViewById(R.id.update_followup_type)
        mAddress = view.findViewById(R.id.create_followup_address)

        mSharedPreferences =
            requireActivity().getSharedPreferences(sharedprefFile, Context.MODE_PRIVATE)
        mCurrentUserName = mSharedPreferences.getString("USER_NAME", "user") as String
        Log.d("CurrentUser", "Current User In Follow Up : $mCurrentUserName")
        setValues(getmodel)

        val prioritylist = listOf<String>("Hot", "Warm", "Cold")
        val type = listOf<String>("Preconsultation", "Postconsultation")

        val priorityadapter = ArrayAdapter<String>(requireContext(), R.layout.support_simple_spinner_dropdown_item, prioritylist)
        val typeadapter = ArrayAdapter<String>(requireContext(), R.layout.support_simple_spinner_dropdown_item, type)


        mLeadPriority.setAdapter(priorityadapter)
        mLeadPriority.setOnItemClickListener { adapterView, view, i, l ->
            selectedpriority = adapterView.getItemAtPosition(i) as String
        }


        mType.setAdapter(typeadapter)
        mType.setOnItemClickListener { adapterView, view, i, l ->
            selectedtype = adapterView.getItemAtPosition(i) as String
        }

        delete_followup.setOnClickListener {
            Firebase.database.reference.child("followup").child(getmodel?.key!!).removeValue()
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "followup Removed", Toast.LENGTH_SHORT).show()
                    requireActivity().onBackPressed()
                }
        }
        mNextCall.setOnClickListener {
            val cal = Calendar.getInstance()

            val date = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                val dateFormat = "dd-MM-yyyy"
                val sdf = SimpleDateFormat(dateFormat, Locale.US)
                mNextCall.setText(sdf.format(cal.time))
                nextcalldateselected = sdf.format(cal.time)

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
        mLastCall.setOnClickListener {
            val cal = Calendar.getInstance()

            val date = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                val dateFormat = "dd-MM-yyyy"
                val sdf = SimpleDateFormat(dateFormat, Locale.US)
                mLastCall.setText(sdf.format(cal.time))
                lastcalldateselected = sdf.format(cal.time)


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
        submit_followup.setOnClickListener {
            val mHashMap = HashMap<String, Any>()

            when {
                mName.text.toString().isBlank() -> {
                    mName.error = "Please enter name"
                    mName.requestFocus()
                }
            mType.text.toString().isEmpty()->{
                mType.error ="Please select type "
                mType.requestFocus()
            }
                mNumber.text.toString().isBlank() -> {
                    mNumber.error = "Please enter number"
                    mNumber.requestFocus()
                }
                mCity.text.toString().isBlank() -> {
                    mCity.error = "Please enter a city"
                    mCity.requestFocus()
                }
                mCarpetArea.text.toString().isBlank() -> {
                    mCarpetArea.error = "Enter Carpet Area"
                    mCarpetArea.requestFocus()
                }
                mName.text.toString().isNotBlank() &&
                        mNumber.text.toString().isNotBlank() &&
                        mCity.text.toString().isNotBlank() &&
                        mCarpetArea.text.toString().isNotBlank() -> {
                    mHashMap["key"] = getmodel?.key.toString()
                    mHashMap["carpetArea"] = mCarpetArea.text.toString()
                    mHashMap["clientname"] = mName.text.toString()
                    mHashMap["contact"] = mNumber.text.toString()
                    selectedpriority?.let { mHashMap["priority"] = it }
                    mHashMap["location"] = mCity.text.toString()
                    mHashMap["type"] = mType.text.toString()
                    mHashMap["timestamp"] = ServerValue.TIMESTAMP
                    mHashMap["updatedat"] = ServerValue.TIMESTAMP

                    mAddress.text.toString()?.let { mHashMap["address"] = mAddress.text.toString() }
                    if (!mLastCall.text.toString().isEmpty()) mHashMap["lastcalldate"] =
                        mLastCall.text.toString()
                    if (!mNextCall.text.toString().isEmpty()) mHashMap["nextcalldate"] =
                        mNextCall.text.toString()
                    if (!mLastComment.text.toString().isEmpty()) mHashMap["lastcomment"] =
                        mLastComment.text.toString()
                    if (!mAddComment.text.toString().isEmpty()) {
                        val commentmap = HashMap<String, Any>()
                        commentmap["comment"] = mAddComment.text.toString()
                        commentmap["commentedby"] = mCurrentUserName
                        commentmap["timestamp"] = ServerValue.TIMESTAMP
                        val key =
                            Firebase.database.reference.child("followup/${getmodel?.key!!}").push().key
                        mHashMap["comments/$key"] = commentmap
                        mHashMap["lastcomment"] = mAddComment.text.toString()
                    }
        //                model.name =mName.text.toString()
                    FirebaseDatabase.getInstance().reference.child("followup").child(getmodel?.key!!)
                        .updateChildren(mHashMap)
                        .addOnSuccessListener {
                            this@FollowUpDetailFragment.requireActivity().onBackPressed()
                            Toast.makeText(
                                requireContext(),
                                "Submitted Successfully",
                                Toast.LENGTH_SHORT
                            ).show()

                        }
                        .addOnFailureListener {
                            Toast.makeText(
                                requireContext(),
                                "Error occured pleas try again Error : ${it.message.toString()}",
                                Toast.LENGTH_SHORT
                            ).show()
                            Log.e("Save followup", "onViewCreated:${it.message.toString()} ")
                        }
                }
            }
        }
        followup_convert.setOnClickListener {

FirebaseDatabase.getInstance().reference.child("CustomHeaders").addListenerForSingleValueEvent(object :ValueEventListener{
    override fun onDataChange(snapshot: DataSnapshot) {
 val list = ArrayList<ConvertedTasksModel>()
 if(snapshot.exists())       snapshot.children.forEach {
            list.add(it.getValue(ConvertedTasksModel::class.java)!!)
        }
        val model = FollowUpModel()
        val mHashMap = HashMap<String, Any>()
        mHashMap["key"] = getmodel?.key.toString()
        mHashMap["carpetArea"] = mCarpetArea.text.toString()
        mHashMap["clientname"] = mName.text.toString()
        mHashMap["contact"] = mNumber.text.toString()
        mHashMap["location"] = mCity.text.toString()
        mHashMap["timestamp"] = ServerValue.TIMESTAMP
        mHashMap["lastcomment"] = mLastComment.text.toString()
        mHashMap["priority"] = mLeadPriority.text.toString()
        mHashMap["type"] = mType.text.toString()
        if (getmodel?.comments != null) mHashMap["comments"] = getmodel?.comments!!

        mHashMap["tasks"] = list

        if (!mLastCall.text.toString().isEmpty()) mHashMap["lastcall"] =
            mLastCall.text.toString()

        model.apply {
            key = getmodel?.key
            carpetArea = mCarpetArea.text.toString()
            clientname = mName.text.toString()
            contact = mNumber.text.toString()
            location = mCity.text.toString()

            if (!mLastCall.text.toString().isEmpty()) lastcalldate =
                mLastCall.text.toString()
        }


        FirebaseDatabase.getInstance().reference.child("converted").child(
            getmodel?.key.toString()
        ).updateChildren(mHashMap)
            .addOnSuccessListener {
                Firebase.database.reference.child("followup").child(getmodel?.key!!)
                    .removeValue().addOnSuccessListener {
                        Snackbar.make(
                            followupupdatelayout,
                            "Moved to Converted",
                            Snackbar.LENGTH_LONG,
                        )
                            .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE).show()
mNavController.popBackStack()                    }
            }
    }

    override fun onCancelled(error: DatabaseError) {
    }

})

        }
//FirebaseDatabase.getInstance().reference.child("CustomHeaders").setValue(createTasks())
    }


    private fun setValues(model: GetFollowUpModel?) {
        if (model?.clientname != null) mName.setText(model.clientname)
        if (model?.contact != null) mNumber.setText(model.contact)
        if (model?.location != null) mCity.setText(model.location)
        if (model?.type != null) mType.setText(model.type)
        if (model?.carpetArea != null) mCarpetArea.setText(model.carpetArea.toString())
//       model?.carpetArea.let {  mCarpetArea.setText(it)}
        mLastCall.setText(model?.lastcalldate)
        mNextCall.setText(model?.nextcalldate)
        mLastComment.setText(model?.lastcomment)
        model?.address?.let { mAddress.setText(model?.address) }
        if (model?.priority != null) model.priority.toString().let { mLeadPriority.setText(it) }
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FollowUpDetailFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun createTasks(): ArrayList<ConvertedTasksModel> {
        val mlist = ArrayList<ConvertedTasksModel>()
        mlist.add(ConvertedTasksModel("SURVEY", false))
        mlist.add(ConvertedTasksModel("Fees Quoted", false))
        mlist.add(ConvertedTasksModel("Quotation sent from us", false))
        mlist.add(ConvertedTasksModel("Quotation Approved", false))
        mlist.add(ConvertedTasksModel("Advance payment Received", false))
        mlist.add(ConvertedTasksModel("LICENSES LIST SENT TO CLIENT", false))
        mlist.add(ConvertedTasksModel("TASK LIST SENT TO CLIENT", false))
        mlist.add(ConvertedTasksModel("Asked Client for the layout", false))
        mlist.add(ConvertedTasksModel("Asked Client for the layout", false))
        mlist.add(ConvertedTasksModel("Layout sent to Architect for Furniture Designing", false))
        mlist.add(ConvertedTasksModel("Layout correction required", false))
        mlist.add(ConvertedTasksModel("Layout Checked by Gulshan Sir", false))
        mlist.add(ConvertedTasksModel("Layout Sent to Client", false))
        mlist.add(ConvertedTasksModel("Layout Sent to Furniture Vendor", false))
        mlist.add(ConvertedTasksModel("Quotation Received from Furniture vendor", false))
        mlist.add(ConvertedTasksModel("Furniture Quotation sent to Client", false))
        mlist.add(
            ConvertedTasksModel(
                "A] Client Approved the furniture Quotation\n" +
                        "B] Client buying furniture from Somewhere else", false
            )
        )
        mlist.add(
            ConvertedTasksModel(
                "Expected Date of Furniture to reach at client location",
                false
            )
        )
        mlist.add(ConvertedTasksModel("Furniture reached", false))
        mlist.add(ConvertedTasksModel("List of furniture Accessories sent to Client", false))
        mlist.add(ConvertedTasksModel("Accessories list for packaging section Prepared", false))
        mlist.add(ConvertedTasksModel("Accessories list for packaging section sent", false))
        mlist.add(ConvertedTasksModel("Hardware list Prepared", false))
        mlist.add(ConvertedTasksModel("Hardware list sent to client", false))
        mlist.add(
            ConvertedTasksModel(
                "\"A] We are arranging the electronics\n" +
                        "B] Client is arranging the electronics.", false
            )
        )
        mlist.add(ConvertedTasksModel("Which Software recommended to client", false))
        mlist.add(
            ConvertedTasksModel(
                "\"Software Status \n" +
                        "A] Client is going with our suggested Software\n" +
                        "B] Client is buying software from somewhere else\"\n", false
            )
        )
        mlist.add(ConvertedTasksModel("Grocery List prepared", false))
        mlist.add(ConvertedTasksModel("Grocery list sent to client", false))
        mlist.add(ConvertedTasksModel("FMCG List prepared", false))
        mlist.add(ConvertedTasksModel("FMCG List Sent to client", false))
        mlist.add(ConvertedTasksModel("FMCG Purchase done", false))
        mlist.add(ConvertedTasksModel("Grocery Purchase done", false))
        mlist.add(ConvertedTasksModel("Data entry structure prepared", false))
        mlist.add(ConvertedTasksModel("Data Entry Structure provided to client", false))
        mlist.add(ConvertedTasksModel("Data Entry Done", false))
        mlist.add(ConvertedTasksModel("Display Management Plan PREPARED", false))
        mlist.add(ConvertedTasksModel("Display Management Plan SENT TO CLIENT", false))
        mlist.add(ConvertedTasksModel("DISPLAY MANAGER REQUIRED ON SITE", false))
        mlist.add(ConvertedTasksModel("All contact list provided", false))
        mlist.add(ConvertedTasksModel("MIS Reporting List prepared", false))
        mlist.add(ConvertedTasksModel("MIS reporting provided", false))
        mlist.add(ConvertedTasksModel("MIS Reporting training provided via video", false))
        mlist.add(
            ConvertedTasksModel(
                "\"In-store Branding, Banners, Staff Uniforms status\n" +
                        "Category Indicators, Every day low price banners", false
            )
        )
        mlist.add(ConvertedTasksModel("Staff List", false))
        mlist.add(ConvertedTasksModel("SOPs provided to client", false))
        mlist.add(ConvertedTasksModel("Opening day Preparation", false))
        mlist.add(ConvertedTasksModel("Remaining fees Paid", false))
        mlist.add(ConvertedTasksModel("Store Opened\n", false))
        return mlist
    }


}