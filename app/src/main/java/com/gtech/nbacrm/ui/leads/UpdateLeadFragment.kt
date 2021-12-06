package com.gtech.nbacrm.ui.leads

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.gtech.nbacrm.R
import com.gtech.nbacrm.databinding.FragmentUpdateLeadBinding
import com.gtech.nbacrm.ui.followup.FollowUpModel
import com.gtech.nbacrm.ui.shared.CommentModel
import kotlinx.android.synthetic.main.fragment_create_lead.delete_lead
import kotlinx.android.synthetic.main.fragment_create_lead.submit_lead
import kotlinx.android.synthetic.main.fragment_update_lead.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class UpdateLeadFragment : Fragment() {
    var _binding: FragmentUpdateLeadBinding? = null
    val binding get() = _binding!!
    private var param1: String? = null
    lateinit var mNavController: NavController
    private var param2: String? = null
    lateinit var mName: TextInputEditText
    lateinit var mNumber: TextInputEditText
    lateinit var mCity: TextInputEditText
    lateinit var mCarpetArea: TextInputEditText
    lateinit var mAddress: TextInputEditText
    lateinit var mLastComment: TextView
    lateinit var mAddComment: TextInputEditText
    lateinit var mLastCall: AutoCompleteTextView
    lateinit var mNextCall: AutoCompleteTextView
    lateinit var mLeadPriority: AutoCompleteTextView
    lateinit var db: FirebaseDatabase
    lateinit var getmodel: ReadLeadModel
    var selectedpriority: String = "Warm"
    var lastcalldateselected: String? = null
    var nextcalldateselected: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentUpdateLeadBinding.inflate(layoutInflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mNavController = Navigation.findNavController(view)
        getmodel = arguments?.getSerializable("parcel") as ReadLeadModel
        Toast.makeText(this.requireContext(), "${getmodel?.key}", Toast.LENGTH_SHORT).show()
        db = FirebaseDatabase.getInstance()
        mName = view.findViewById(R.id.create_lead_name)
        mNumber = view.findViewById(R.id.create_lead_number)
        mCity = view.findViewById(R.id.create_lead_city)
        mCarpetArea = view.findViewById(R.id.create_lead_carpetarea)
        mLastCall = view.findViewById(R.id.select_last_calldate_lead)
        mNextCall = view.findViewById(R.id.select_next_calldate_lead)
        mLastComment = view.findViewById(R.id.last_lead_comment)
        mAddComment = view.findViewById(R.id.add_lead_comment)
        mLeadPriority = view.findViewById(R.id.create_lead_priority)
        mAddress = view.findViewById(R.id.create_lead_address)
        setValues(getmodel)
        mLastCall.setOnClickListener {
            val cal = Calendar.getInstance()

            val date = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                val dateFormat = "dd-MM-yyyy"
                val sdf = SimpleDateFormat(dateFormat, Locale.US)
                select_last_calldate_lead.setText(sdf.format(cal.time))
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
        mNextCall.setOnClickListener {
            val cal = Calendar.getInstance()

            val date = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                val dateFormat = "dd-MM-yyyy"
                val sdf = SimpleDateFormat(dateFormat, Locale.US)
                select_next_calldate_lead.setText(sdf.format(cal.time))
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
        binding.deleteLead.setOnClickListener {
            Firebase.database.reference.child("leads").child(getmodel?.key!!).removeValue()
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Lead Removed", Toast.LENGTH_SHORT).show()
                    requireActivity().onBackPressed()
                }
        }

        binding.submitLead.setOnClickListener {
            updateData()
        }
        binding.leadConvert.setOnClickListener {
            val model = FollowUpModel()
            model.apply {
                key = getmodel?.key
                carpetArea = mCarpetArea.text.toString()
                clientname = mName.text.toString()
                contact = mNumber.text.toString()
                location = mCity.text.toString()
                if (!mLastCall.text.toString().isEmpty()) lastcalldate =
                    mLastCall.text.toString()
                if (!mNextCall.text.toString().isEmpty()) nextcalldate =
                    mNextCall.text.toString()
                getmodel?.comments?.let { model.comments = it }
                if (!mLeadPriority.text.toString().isBlank()) priority =
                    mLeadPriority.text.toString()
            }
            FirebaseDatabase.getInstance().reference.child("followup").child(
                getmodel?.key.toString()
            ).setValue(model)
                .addOnSuccessListener {
                    Firebase.database.reference.child("leads").child(getmodel?.key!!).removeValue()
                        .addOnSuccessListener {
                            Snackbar.make(
                                leadupdatelayout,
                                "Lead moved to followup",
                                Snackbar.LENGTH_LONG
                            )
                                .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE).show()
                            requireActivity().onBackPressed()
                        }
                }
        }
        val prioritylist = listOf<String>("Hot", "Warm", "Cold")
        val priorityadapter = ArrayAdapter<String>(
            requireContext(),
            R.layout.support_simple_spinner_dropdown_item,
            prioritylist
        )
        mLeadPriority.setAdapter(priorityadapter)
        mLeadPriority.setOnItemClickListener { adapterView, view, i, l ->
            selectedpriority = adapterView.getItemAtPosition(i) as String
        }
binding.leadClose.setOnClickListener {
    FirebaseDatabase.getInstance().reference.child("closedleads").child(getmodel?.key!!).setValue(getmodel).addOnCompleteListener { it ->
        if(it.isSuccessful)FirebaseDatabase.getInstance().reference.child("leads").child(getmodel?.key!!).removeValue().addOnCompleteListener {
            if(it.isSuccessful) Toast.makeText(requireContext(), "Lead Moved to Closed Leads", Toast.LENGTH_SHORT).show()
            requireActivity().onBackPressed()
        }
    }
}
    }

    private fun updateData() {
        if (mName.text.toString().isNullOrBlank()) {
            mName.error = "Please enter name"
            mName.requestFocus()
        } else if (mNumber.text.toString().isNullOrBlank()) {
            mNumber.error = "Please enter number"
            mNumber.requestFocus()
        } else if (mCity.text.toString().isNullOrBlank()) {
            mCity.error = "Please enter a city"
            mCity.requestFocus()
        } else if (
            mName.text.toString().isNotBlank() &&
            mNumber.text.toString().isNotBlank() &&
            mCity.text.toString().isNotBlank() &&
            mCarpetArea.text.toString().isNotBlank()
        ) {
            val mHashmap = HashMap<String, Any>()
            mHashmap["priority"] = selectedpriority
            mHashmap["clientname"] = mName.text.toString()
            mHashmap["key"] = getmodel?.key.toString()
            mHashmap["contact"] = mNumber.text.toString()
            mHashmap["carpetArea"] = mCarpetArea.text.toString()
            mHashmap["location"] = mCity.text.toString()
            if (!mLeadPriority.text.toString().isNullOrEmpty()) mHashmap["priority"] =
                mLeadPriority.text.toString()
            mAddress.text.toString()?.let { mHashmap["address"] = it }
            if (!mLastCall.text.toString().isNullOrEmpty()) mHashmap["lastcalldate"] =
                mLastCall.text.toString()
            if (!mNextCall.text.toString().isNullOrEmpty()) mHashmap["nextcalldate"] =
                mNextCall.text.toString()

            if (!mAddComment.text.toString().isNullOrEmpty()) {
                val commentmap = HashMap<String, Any>()
                commentmap["comment"] = mAddComment.text.toString()
                commentmap["commentedby"] = "Current User"
                commentmap["timestamp"] = ServerValue.TIMESTAMP
                val key = Firebase.database.reference.child("leads/${getmodel?.key!!}").push().key
                mHashmap["comments/$key"] = commentmap
                mHashmap["lastcomment"] = mAddComment.text.toString()
                mHashmap["updatedat"] = ServerValue.TIMESTAMP
            }
            //                model.name =mName.text.toString()
            db.reference.child("leads").child(getmodel?.key!!)
                .updateChildren(mHashmap)
                .addOnSuccessListener {
                    requireActivity().onBackPressed()
                    Toast.makeText(requireContext(), "Submitted Successfully", Toast.LENGTH_SHORT)
                        .show()

                }
                .addOnFailureListener {
                    Toast.makeText(
                        requireContext(),
                        "Error occured pleas try again Error : ${it.message.toString()}",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e("Save Lead", "onViewCreated:${it.message.toString()} ")
                }
        }
    }

    private fun setValues(model: ReadLeadModel?) {
        if (model?.clientname != null) mName.setText(model.clientname)
        if (model?.contact != null) mNumber.setText(model.contact)
        if (model?.location != null) mCity.setText(model.location)
        if (model?.carpetArea != null) mCarpetArea.setText(model.carpetArea.toString())
        if (model?.lastcalldate != null) mLastCall.setText(model.lastcalldate)
        if (model?.nextcalldate != null) mNextCall.setText(model.nextcalldate)
        if (model?.lastcomment != null) mLastComment.setText(model.lastcomment)
        if (model?.priority != null) mLeadPriority.setText(model.priority)

        model?.address?.let { mAddress.setText(it) }
        //   if (model?.lastcomment != null) mAddComment.setText(model?.lastcomment)

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}