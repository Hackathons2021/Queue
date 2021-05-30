package com.example.queue

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_enter_code.*

val databaseb = Firebase.database
val myRefb = databaseb.getReference("UserListing")

class EnterCode : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enter_code)

        submit.setOnClickListener {
            when {
                (codeEntry.text.toString().trim { it <= ' ' }.length != 6) -> {
                    Toast.makeText(
                        this@EnterCode,
                        "Code entered is not 6 characters long.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                else -> {
                    val code = codeEntry.text.toString().trim { it <= ' ' }
                    myRefb.child("codeList").child(code).get().addOnSuccessListener {
                        val businessId = it.value.toString()
                        Log.d("1", businessId)
                        val userId = FirebaseAuth.getInstance().currentUser.uid
                        Log.d("2", businessId)
                        myRefb.child("userCustomers").child(userId).child("username").get()
                            .addOnSuccessListener {
                                val name = it.value.toString()
                                Log.d("3", name)
                                myRefb.child("users").child(businessId).child("numberOfSessions")
                                    .get().addOnSuccessListener {
                                        var sessionNumber = it.value.toString().toInt()
                                        sessionNumber = sessionNumber - 1
                                        var sessionNumberString = sessionNumber.toString()
                                        myRefb.child("users").child(businessId)
                                            .child(sessionNumberString).child("requests")
                                            .child("sizeOfRequests").get().addOnSuccessListener {
                                                var sizeOfRequests = it.value.toString().toInt()
                                                myRefb.child("users").child(businessId)
                                                    .child(sessionNumberString).child("requests")
                                                    .child(sizeOfRequests.toString()).setValue(name)
                                                var x = sizeOfRequests.toString() + "a"
//                                    Log.d("x value",x)
                                                var y = sizeOfRequests.toString() + "b"
//                                    Log.d("y value",y)
                                                myRefb.child("users").child(businessId)
                                                    .child(sessionNumberString).child("requests")
                                                    .child(x)
                                                    .setValue(userId)
                                                myRefb.child("users").child(businessId)
                                                    .child(sessionNumberString).child("requests")
                                                    .child(y)
                                                    .setValue(sizeOfRequests.toString())
                                                myRefb.child("users").child(businessId)
                                                    .child(sessionNumberString).child("requests")
                                                    .child("sizeOfRequests")
                                                    .setValue((sizeOfRequests + 1).toString())

                                                val intent =
                                                    Intent(
                                                        this@EnterCode,
                                                        CustomerLanding::class.java
                                                    )
                                                intent.flags =
                                                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                                startActivity(intent)
                                                finish()

                                            }
                                    }
                            }
                    }.addOnFailureListener {
                        Toast.makeText(
                            this@EnterCode,
                            "Entered shop ID doesn't exist.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }
}