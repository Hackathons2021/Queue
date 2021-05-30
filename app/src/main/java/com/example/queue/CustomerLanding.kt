package com.example.queue

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_customer_landing.*

val databasec = Firebase.database
val myRefc = databasec.getReference("UserListing")

class CustomerLanding : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_landing)
        val mainHandler = Handler(Looper.getMainLooper())
        val text = findViewById<TextView>(R.id.number)

        var userId = FirebaseAuth.getInstance().currentUser.uid

        mainHandler.post(object : Runnable {
            override fun run() {
                myRefc.child("userCustomers").child(userId).child("currentBusiness").get()
                    .addOnSuccessListener {
                        var currentBusiness = it.value.toString()
                        if (currentBusiness == "X") {
                            leaveBtn.visibility = View.INVISIBLE
                            val text = findViewById<TextView>(R.id.number)
                            text.text = "You are not currently connected to any queue."
                        } else {
                            myRefc.child("userCustomers").child(userId).child("currentIndex").get()
                                .addOnSuccessListener {
                                    var currentIndex = it.value.toString().toInt()
                                    myRefc.child("users").child(currentBusiness)
                                        .child("numberOfSessions").get().addOnSuccessListener {
                                            var numberOfSessions = it.value.toString()
                                            var intNumberOfSessions = numberOfSessions.toInt()
                                            intNumberOfSessions = intNumberOfSessions - 1
                                            myRefc.child("users").child(currentBusiness)
                                                .child(intNumberOfSessions.toString())
                                                .child("queue")
                                                .child("frontValue").get().addOnSuccessListener {
                                                    val frontValue = it.value.toString().toInt()
                                                    if (frontValue > currentIndex) {
                                                        text.text = "Your turn has already come."
                                                        backButton.visibility = View.VISIBLE
                                                        leaveBtn.visibility = View.INVISIBLE
                                                    } else {
                                                        var rank = 0
                                                        for (i in frontValue..currentIndex) {
                                                            myRefc.child("users")
                                                                .child(currentBusiness)
                                                                .child(intNumberOfSessions.toString())
                                                                .child("queue")
                                                                .child(i.toString() + "d")
                                                                .get().addOnSuccessListener {
                                                                    if (it.value == null) {
                                                                        rank += 1
                                                                        text.text =
                                                                            "Your current position is " + rank.toString()
                                                                        leaveBtn.visibility =
                                                                            View.VISIBLE
                                                                    }
                                                                    Log.d(
                                                                        "fbyrfbyrw88888888888888888",
                                                                        i.toString()
                                                                    )
                                                                    Log.d(
                                                                        "fbyrfbyrw88888888888888888",
                                                                        it.value.toString()
                                                                    )
                                                                }
                                                        }
                                                    }
                                                }
                                        }
                                }
                        }
                    }
                mainHandler.postDelayed(this, 1000)
            }
        })

        leaveBtn.setOnClickListener {
            myRefc.child("userCustomers").child(userId).child("currentIndex").get()
                .addOnSuccessListener {
                    var currentIndex = it.value.toString().toInt()
                    myRefc.child("userCustomers").child(userId).child("currentBusiness").get()
                        .addOnSuccessListener {
                            var currentBusiness = it.value.toString()
                            myRefc.child("users").child(currentBusiness).child("numberOfSessions")
                                .get().addOnSuccessListener {
                                    var numSessions = it.value.toString().toInt()
                                    numSessions -= 1
                                    myRefc.child("users").child(currentBusiness)
                                        .child(numSessions.toString()).child("queue")
                                        .child(currentIndex.toString() + "d").setValue("X")
                                }
                        }
                }
            val intent =
                Intent(this@CustomerLanding, EnterCode::class.java)
            intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
        backButton.setOnClickListener {
            val intent =
                Intent(this@CustomerLanding, EnterCode::class.java)
            intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }


    }
}