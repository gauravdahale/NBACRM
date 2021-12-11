package com.gtech.nbacrm.ui.converted

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.*
import com.gtech.nbacrm.R
import com.gtech.nbacrm.ItemDecoration
import com.gtech.nbacrm.databinding.FragmentConvertedDetailBinding
import com.gtech.nbacrm.ui.tasks.GetConvertedTasksModel
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.fragment_converted_detail.*


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ConvertedDetailFragment : Fragment() {
    private var param1: String? = null
    private  val TAG = "ConvertedDetailFragment"
    private var param2: String? = null
    lateinit var mlistener:ValueEventListener
    var model: GetConvertedModel? = null
    var mQuery :Query?=null
    val booleanlist = arrayListOf<Boolean>()
    lateinit var mAdapter: ConvertedTasksAdapter
    lateinit var mRecyclerView: RecyclerView
var _binding: FragmentConvertedDetailBinding?=null
    val binding get() = _binding!!
    var mList = ArrayList<GetConvertedTasksModel>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
      _binding = FragmentConvertedDetailBinding.inflate(layoutInflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val name = binding.convertedName
        val number = binding.convertedNumber
        val city = binding.convertedCity
        val carpetarea = binding.convertedCarpetArea
        val date = binding.convertedDate
        //   model = GetConvertedModel()
        val addressvoew = binding.textView10
        model = arguments?.getSerializable("parcel") as GetConvertedModel?
        val tasks = model?.tasks
        mQuery = FirebaseDatabase.getInstance().reference.child("converted/${model?.key}/tasks")
            .orderByChild("confirm")
        Log.d("TASKS", "Task ${tasks?.size}")
        Log.d("Tasks List", "Tasks : ${model?.tasks?.size}")
        if (model?.clientname != null) name.text = model?.clientname
        if (model?.contact != null) number.text = model?.contact
        if (model?.location != null) city.text = model?.location
        if (model?.carpetArea != null) carpetarea.text = model?.carpetArea.toString()
        if (model?.address != null) converted_last_call_address.text = model?.address.toString()
        if (model?.timestamp != null) date.text = model?.getTimeDate(model?.timestamp!!)
        setvalue(model?.type, converted_detail_type)
        mRecyclerView = view.findViewById(R.id.tasks_recyclerview)
//        mAdapter = ConvertedTasksAdapter(model?.tasks!!, requireContext(), model!!)
        mAdapter = ConvertedTasksAdapter(mList, requireContext(), model!!)

        mRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        mRecyclerView.addItemDecoration(ItemDecoration(requireContext()))
        mRecyclerView.adapter = mAdapter
        binding.convertedDetailType.setOnClickListener { changetypedialog(model!!) }
        getTasks()
        addressvoew.setOnClickListener { addComment(view, requireContext()) }
        val tasktype = arrayListOf<String>("ALL", "Layout", "Inital Listings", "Product Listings","Final")
        val tasktypeadapter = ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            tasktype
        )

        tasktypefilter.setOnItemClickListener { adapterView, view, i, l ->
            if (adapterView.getItemAtPosition(i).toString() != "ALL") mAdapter.getFilter()
                .filter(adapterView.getItemAtPosition(i).toString())
            else mAdapter.getFilter().filter("")
        }
        tasktypefilter.setAdapter(tasktypeadapter)


        binding.closeLead.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setCancelable(true)
            with(builder) {
                setMessage("Are you sure you want to close this lead.")
                setTitle("Close this lead?")
                setPositiveButton("Yes", DialogInterface.OnClickListener { _, _ ->
               //     Toast.makeText(context, "Yes Clicked", Toast.LENGTH_SHORT).show()
FirebaseDatabase.getInstance().reference.child("completed").child(model?.key.toString()).setValue(model).addOnCompleteListener {
    FirebaseDatabase.getInstance().reference.child("converted").child(model?.key.toString()).removeValue().addOnSuccessListener {
Navigation.findNavController(view).popBackStack()
   }
}
                })
                setNegativeButton("No", DialogInterface.OnClickListener { _, _ ->
                })
            }
builder.show()
        }
    }

    private fun changetypedialog(model: GetConvertedModel) {
        val builder = AlertDialog.Builder(requireContext())
val types =arrayOf("Preconsultation","Postconsultation")
builder.setTitle("Set Type")
        builder.setItems(types){ dialog, which ->
      //    FirebaseDatabase.getInstance().reference.child("converted/${model.key}/type").setValue(types[which])
            converted_detail_type.text=types[which]
        }
builder.show()
    }

    private fun setvalue(value: String?, view: TextView?) {
        if(value!=null && view!=null) view.text =value.toString()
    }

    private fun getTasks() {
        mlistener  =  object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                mList.clear()
                snapshot.children.forEach {
                    val model = it.getValue(GetConvertedTasksModel::class.java)
                    model?.key = it.key
                    mList.add(model!!)
                }
                mList.forEach {

                    booleanlist.add(it.done!!)

                }

                when {
                    booleanlist.any { !it } -> binding.allDone.visibility =View.GONE
                    booleanlist.any { it } -> binding.allDone.visibility =View.VISIBLE

                }
                mAdapter.notifyDataSetChanged()
                Log.d(TAG, "onDataChange:${mList.size} ")
            }

            override fun onCancelled(error: DatabaseError) {

            }
        }
        mQuery?.addValueEventListener(mlistener)
    }

    fun addComment(view: View, context: Context) {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_add_comment)
        dialog.textView2.text = "Address"
        val edittext = dialog.findViewById<TextInputEditText>(R.id.converted_comment_edit)
        val submit = dialog.findViewById<FloatingActionButton>(R.id.btn_submit_comment)
        submit.setOnClickListener {
            if (!edittext.text.toString().isEmpty()) {
                val firebaseDatabase =
                    FirebaseDatabase.getInstance().reference.child("converted/${model?.key}/address")
                        .setValue(edittext.text.toString()).addOnSuccessListener {
                            converted_last_call_address.text = edittext.text.toString()
                        dialog.dismiss()
                        }
            } else {
                Toast.makeText(this.requireContext(), "Please enter address!!!", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        dialog.show()

    }

    override fun onDestroyView() {
        super.onDestroyView()
    _binding =null
        mQuery?.removeEventListener(mlistener)
    }
}