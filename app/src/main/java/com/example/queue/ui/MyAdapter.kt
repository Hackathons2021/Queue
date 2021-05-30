package com.example.queue.ui

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView
import com.example.queue.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

val database = Firebase.database
val myRef = database.getReference("UserListing")

class myAdapter(
    private val context: Context,
    private val data: ArrayList<String>,
    private val dataID: ArrayList<String>,
    private val dataIndex: ArrayList<String>
) : BaseAdapter() {

    private val inflater: LayoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {

        return data.size
    }

    override fun getItem(position: Int): Any {
        return data[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val rowView = inflater.inflate(R.layout.list_item, parent, false)
        val textView: TextView = rowView.findViewById(R.id.list_item_text)
        val acceptbtn: Button = rowView.findViewById(R.id.accept_btn)
        val rejectBtn: Button = rowView.findViewById(R.id.reject_btn)
        textView.text = data[position]

        var userId = FirebaseAuth.getInstance().currentUser.uid
        rejectBtn.setOnClickListener {
//            Log.d("Item Rejected", data[position])
            var l = dataID[position]
            myRef.child("users").child(userId).child("numberOfSessions").get()
                .addOnSuccessListener {
                    var numberOfSessions = it.value.toString()
                    var intNumberOfSessions = numberOfSessions.toInt()
                    intNumberOfSessions = intNumberOfSessions - 1
                    myRef.child("users").child(userId).child(intNumberOfSessions.toString())
                        .child("requests").child(l).setValue("X")
                    myRef.child("users").child(userId).child(intNumberOfSessions.toString())
                        .child("requests").child(l + "a").setValue("X")
                    myRef.child("users").child(userId).child(intNumberOfSessions.toString())
                        .child("requests").child(l + "b").setValue("X")
                }
            data.removeAt(position)
            notifyDataSetChanged()
        }
        acceptbtn.setOnClickListener {
            val userId = FirebaseAuth.getInstance().currentUser.uid
            Log.d("alpha", data[position])
            var l = dataIndex[position]
            myRef.child("users").child(userId).child("numberOfSessions").get()
                .addOnSuccessListener {
                    var sessionNumber = it.value.toString().toInt()
                    sessionNumber = sessionNumber - 1
                    var sessionNumberString = sessionNumber.toString()
                    myRef.child("userCustomers").child(dataID[position]).child("currentBusiness")
                        .setValue(userId)
                    myRef.child("users").child(userId).child(sessionNumberString).child("requests")
                        .child(l).setValue("X")
                    myRef.child("users").child(userId).child(sessionNumberString).child("requests")
                        .child(l + "a").setValue("X")
                    myRef.child("users").child(userId).child(sessionNumberString).child("requests")
                        .child(l + "b").setValue("X")
                    myRef.child("users").child(userId).child(sessionNumberString).child("queue")
                        .child("sizeOfQueue").get().addOnSuccessListener {
                            var queueIndex = it.value.toString().toInt()
                            myRef.child("users").child(userId).child(sessionNumberString)
                                .child("queue")
                                .child(queueIndex.toString()).setValue(data[position])
                            myRef.child("userCustomers").child(dataID[position])
                                .child("currentIndex")
                                .setValue(queueIndex.toString())
                            myRef.child("users").child(userId).child(sessionNumberString)
                                .child("queue")
                                .child(queueIndex.toString() + "a").setValue(dataID[position])
                            queueIndex = queueIndex + 1
                            myRef.child("users").child(userId).child(sessionNumberString)
                                .child("queue")
                                .child("sizeOfQueue").setValue(queueIndex.toString())
                            data.removeAt(position)
                            notifyDataSetChanged()
                        }
                }
        }
        return rowView

    }

}