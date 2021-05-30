package com.example.queue.ui.queue

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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

val database = Firebase.database
val myRef = database.getReference("UserListing")

class QueueFragment : Fragment() {

    private lateinit var queueViewModel: QueueViewModel

    // private var _binding: FragmentQueueBinding? = null
    private lateinit var listView: ListView

    // This property is only valid between onCreateView and
    // onDestroyView.
    //  private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        queueViewModel =
            ViewModelProvider(this).get(QueueViewModel::class.java)

        //_binding = FragmentQueueBinding.inflate(inflater, container, false)
        val root: View = inflater.inflate(R.layout.fragment_queue, container, false)
        val userId = FirebaseAuth.getInstance().currentUser.uid

        listView = root.findViewById(R.id.list_queue)
        val waitLine = arrayListOf<String>()

        val mainHandler = Handler(Looper.getMainLooper())

        mainHandler.post(object : Runnable {
            override fun run() {
                myRef.child("users").child(userId).child("numberOfSessions").get()
                    .addOnSuccessListener {
                        var sessionNumber = it.value.toString().toInt()
                        sessionNumber = sessionNumber - 1
                        var sessionNumberString = sessionNumber.toString()
                        Log.d("KA", sessionNumberString.toString())
                        myRef.child("users").child(userId).child(sessionNumberString).child("queue")
                            .child("sizeOfQueue").get().addOnSuccessListener {
                                var queueLength = it.value.toString().toInt()
                                queueLength = queueLength - 1
                                Log.d("K", queueLength.toString())
                                myRef.child("users").child(userId).child(sessionNumberString)
                                    .child("queue")
                                    .child("frontValue").get().addOnSuccessListener {
                                        var frontValue = it.value.toString().toInt()
                                        for (i in frontValue..queueLength) {
                                            var v = 1
                                            myRef.child("users").child(userId)
                                                .child(sessionNumberString)
                                                .child("queue")
                                                .child(i.toString()).get().addOnSuccessListener {
                                                    myRef.child("users").child(userId)
                                                        .child(sessionNumberString)
                                                        .child("queue").child(i.toString() + "d")
                                                        .get()
                                                        .addOnSuccessListener {
                                                            if (it.value != null) {
                                                                v = 0
                                                            }
                                                        }
                                                    if (v == 1) {
                                                        waitLine.add(it.value.toString())
                                                        val arrayAdapter =
                                                            adapter(root.context, waitLine)
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

        //listView.setOn

        //val textView: TextView = root.findViewById(R.id.text_queue)
        //queueViewModel.text.observe(viewLifecycleOwner, Observer {
        //   textView.text = it
        //})
        return root
    }


}