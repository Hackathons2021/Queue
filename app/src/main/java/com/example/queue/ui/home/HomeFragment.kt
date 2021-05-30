package com.example.queue.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.queue.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class HomeFragment : Fragment() {
    val database = Firebase.database
    private val myRef = database.getReference("UserListing")
    private lateinit var homeViewModel: HomeViewModel
    // private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    // private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)
        val userId = FirebaseAuth.getInstance().currentUser.uid

        //_binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = inflater.inflate(R.layout.fragment_home, container, false)
        val button: Button = root.findViewById(R.id.startQueueButton)
        val editButton: Button = root.findViewById(R.id.edit_btn)

        val nameText: EditText = root.findViewById(R.id.text_name)
        val addressText: EditText = root.findViewById(R.id.text_address)
        val businessText: EditText = root.findViewById(R.id.text_business)
        val businessIdText: TextView = root.findViewById(R.id.businessId)

        myRef.child("users").child(userId).child("username").get().addOnSuccessListener {
            Log.d("nnejeeffffffff", userId)
            nameText.setText(it.value.toString())
        }
        myRef.child("users").child(userId).child("userAddress").get().addOnSuccessListener {
            addressText.setText(it.value.toString())
        }
        myRef.child("users").child(userId).child("userBusiness").get().addOnSuccessListener {
            businessText.setText(it.value.toString())
        }

        myRef.child("users").child(userId).child("userNumber").get().addOnSuccessListener {
            businessIdText.text = it.value.toString()
        }

        editButton.setOnClickListener {
            myRef.child("users").child(userId).child("username").setValue(nameText.text.toString())
            myRef.child("users").child(userId).child("userAddress")
                .setValue(addressText.text.toString())
            myRef.child("users").child(userId).child("userBusiness")
                .setValue(businessText.text.toString())
        }

        button.setOnClickListener {
            myRef.child("users").child(userId).child("numberOfSessions").get()
                .addOnSuccessListener {
                    Log.d("AAAA", it.value.toString())
                    var numberOfSessions = it.value.toString()
                    var intNumberOfSessions = numberOfSessions.toInt()
                    intNumberOfSessions = intNumberOfSessions + 1
                    myRef.child("users").child(userId).child("numberOfSessions")
                        .setValue(intNumberOfSessions.toString())
                    myRef.child("users").child(userId).child(numberOfSessions).child("queue")
                        .child("sizeOfQueue").setValue("0")
                    myRef.child("users").child(userId).child(numberOfSessions).child("queue")
                        .child("frontValue").setValue("0")
                    myRef.child("users").child(userId).child(numberOfSessions).child("requests")
                        .child("sizeOfRequests").setValue("0")
                }
        }

        // val textView: TextView = binding.textHome
        //homeViewModel.text.observe(viewLifecycleOwner, Observer {
        //    textView.text = it
        //})
        return root
    }

}