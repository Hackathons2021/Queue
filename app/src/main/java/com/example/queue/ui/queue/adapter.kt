package com.example.queue.ui.queue

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

val databasea = Firebase.database
val myRefa = databasea.getReference("UserListing")

class adapter(private val context: Context, private val data: ArrayList<String>) : BaseAdapter() {

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
        val rowView = inflater.inflate(R.layout.list_item_new, parent, false)
        val textView: TextView = rowView.findViewById(R.id.list_item_text_1)
        textView.text = data[position]
        val removeBtn: Button = rowView.findViewById(R.id.remove_btn)

        if (position == 0) {
            removeBtn.visibility = View.VISIBLE
            removeBtn.setOnClickListener {
                Log.d("Item Rejected", data[position])
                val userId = FirebaseAuth.getInstance().currentUser.uid

                myRefa.child("users").child(userId).child("numberOfSessions").get()
                    .addOnSuccessListener {
                        var sessionNumber = it.value.toString().toInt()
                        sessionNumber = sessionNumber - 1
                        var sessionNumberString = sessionNumber.toString()
                        myRef.child("users").child(userId).child(sessionNumberString).child("queue")
                            .child("frontValue").get().addOnSuccessListener {
                                var frontValue = it.value.toString().toInt()
                                frontValue = frontValue + 1
                                myRef.child("users").child(userId).child(sessionNumberString)
                                    .child("queue").child("frontValue")
                                    .setValue(frontValue.toString())
                            }
                    }

                data.removeAt(position)
                notifyDataSetChanged()
            }
        }
        return rowView
    }
}