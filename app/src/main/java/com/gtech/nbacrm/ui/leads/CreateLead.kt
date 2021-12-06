package com.gtech.nbacrm.ui.leads

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.gtech.nbacrm.R
import kotlinx.android.synthetic.main.fragment_create_lead.*
import kotlinx.android.synthetic.main.fragment_create_lead.create_last_call_date
import kotlinx.android.synthetic.main.fragment_create_lead.submit_lead
import kotlinx.android.synthetic.main.fragment_update_lead.*
import java.text.SimpleDateFormat
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class CreateLead : Fragment() {
    private var param1: String? = null
    lateinit var mNavController: NavController
    private var param2: String? = null
    lateinit var mName: TextInputEditText
    lateinit var mAddress: TextInputEditText
    lateinit var mNumber: TextInputEditText
    lateinit var mCity: TextInputEditText
    lateinit var mCarpetArea: TextInputEditText
    lateinit var mLastCall: AutoCompleteTextView
    lateinit var mLeadPriority: AutoCompleteTextView
    var selectedpriority: String? = null
    var lastcalldateselected: String? = null
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
        return inflater.inflate(R.layout.fragment_create_lead, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mNavController = Navigation.findNavController(view)
        mName = view.findViewById(R.id.create_lead_name)
        mNumber = view.findViewById(R.id.create_lead_number)
        mCity = view.findViewById(R.id.create_lead_city)
        mCarpetArea = view.findViewById(R.id.create_lead_carpetarea)
        mLastCall = view.findViewById(R.id.create_last_call_date)
        mLeadPriority = view.findViewById(R.id.create_lead_priority)
        mAddress = view.findViewById(R.id.create_lead_address)
        create_last_call_date.setOnClickListener {
            val cal = Calendar.getInstance()

            val date = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                val dateFormat = "dd-MM-yyyy"
                val sdf = SimpleDateFormat(dateFormat, Locale.US)
                create_last_call_date.setText(sdf.format(cal.time))
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
        val prioritylist = listOf<String>("Hot", "Warm", "Cold")
        val priorityadapter = ArrayAdapter<String>(
            requireContext(),
            R.layout.support_simple_spinner_dropdown_item,
            prioritylist
        )
        mLeadPriority.setAdapter(priorityadapter)
        mLeadPriority.setOnItemClickListener { adapterView, view, i, l ->
            selectedpriority = adapterView.getItemAtPosition(i).toString()

        }

        submit_lead.setOnClickListener {
            if (mName.text.toString().isBlank()) {
                mName.error = "Please enter name"
                mName.requestFocus()
            } else if (mNumber.text.toString().isBlank()) {
                mNumber.error = "Please enter number"
                mNumber.requestFocus()
            } else if (mCity.text.toString().isBlank()) {
                mCity.error = "Please enter a city"
                mCity.requestFocus()
            } else if (mCarpetArea.text.toString().isBlank()) {
                mCarpetArea.error = "Enter Carpet Area"
                mCarpetArea.requestFocus()
            } else if (
                mName.text.toString().isNotBlank() &&
                mNumber.text.toString().isNotBlank() &&
                mCity.text.toString().isNotBlank() &&
                mCarpetArea.text.toString().isNotBlank()
            ) {
                val model = LeadModel()
                model.apply {
                    clientname = mName.text.toString()
                    contact = mNumber.text.toString()
                    carpetArea = mCarpetArea.text.toString()
                    location = mCity.text.toString()
                    timestamp = ServerValue.TIMESTAMP
                    if(mAddress.text.toString().isNullOrEmpty()) address = mAddress.text.toString()
                    if (lastcalldateselected != null) lastcalldate = lastcalldateselected
                    if (selectedpriority != null) priority = selectedpriority

                }
                model.clientname = mName.text.toString()
                val key = FirebaseDatabase.getInstance().reference.child("leads").push().key
                FirebaseDatabase.getInstance().reference.child("allclients").child(key.toString())
                    .setValue(model)

                FirebaseDatabase.getInstance().reference.child("leads").child(key.toString())
                    .setValue(model)
                    .addOnSuccessListener {
                        this@CreateLead.requireActivity().onBackPressed()
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
                        Log.e("Save Lead", "onViewCreated:${it.message.toString()} ")
                    }
            }
        }
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CreateLead().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}