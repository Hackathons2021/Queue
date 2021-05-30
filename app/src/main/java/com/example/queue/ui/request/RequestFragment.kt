package com.example.queue.ui.request

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.queue.R
import com.example.queue.ui.myAdapter
import com.example.queue.ui.myRef
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

val databaseb = Firebase.database
val myRefb = databaseb.getReference("UserListing")

class RequestFragment : Fragment() {

    private lateinit var requestViewModel: RequestViewModel

    //private var _binding: FragmentRequestBinding? = null
    private lateinit var listView: ListView

    // This property is only valid between onCreateView and
    // onDestroyView.
    //private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        requestViewModel =
            ViewModelProvider(this).get(RequestViewModel::class.java)

        //  _binding = FragmentRequestBinding.inflate(inflater, container, false)
        val root: View = inflater.inflate(R.layout.fragment_request, container, false)
        listView = root.findViewById(R.id.list_request)
        val waitLine = arrayListOf<String>()
        val waitLineID = arrayListOf<String>()
        val waitLineNumber = arrayListOf<String>()

        val userId = FirebaseAuth.getInstance().currentUser.uid
        Log.d("userId", userId)

        val mainHandler = Handler(Looper.getMainLooper())

        mainHandler.post(object : Runnable {
            override fun run() {
                myRefb.child("users").child(userId).child("numberOfSessions").get()
                    .addOnSuccessListener {
                        Log.d("AAA", userId)
                        val numberOfSessions = it.value.toString()
                        Log.d("AAAAA", numberOfSessions)
                        var intNumberOfSessions = numberOfSessions.toInt()
                        intNumberOfSessions -= 1
                        val sessionNumber = intNumberOfSessions.toString()
                        myRefb.child("users").child(userId).child(sessionNumber).child("requests")
                            .child("sizeOfRequests").get().addOnSuccessListener {
                                var numberOfRequests = it.value.toString().toInt()
                                numberOfRequests -= 1
                                for (i in 0..numberOfRequests) {
                                    myRefb.child("users").child(userId).child(sessionNumber)
                                        .child("requests").child(i.toString()).get()
                                        .addOnSuccessListener {
                                            val customerName = it.value.toString()
                                            myRefb.child("users").child(userId).child(sessionNumber)
                                                .child("requests").child((i.toString()) + "a").get()
                                                .addOnSuccessListener {
                                                    val customerId = it.value.toString()
                                                    if (customerId != "X") {
                                                        waitLine.add(customerName)
                                                        waitLineID.add(customerId)
                                                        waitLineNumber.add(i.toString())
                                                        val arrayAdapter = myAdapter(
                                                            root.context,
                                                            waitLine,
                                                            waitLineID,
                                                            waitLineNumber
                                                        )
                                                        listView.adapter = arrayAdapter
                                                        arrayAdapter.notifyDataSetChanged()
                                                    }
                                                }
                                        }
                                }
                            }
                    }
                mainHandler.postDelayed(this, 3000)
            }
        })
        //val textView: TextView = root.findViewById(R.id.text_request)
        //requestViewModel.text.observe(viewLifecycleOwner, Observer {
        //   textView.text = it
        // })
        return root
    }


}