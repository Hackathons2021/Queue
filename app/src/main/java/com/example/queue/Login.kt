package com.example.queue

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.sawolabs.androidsdk.LOGIN_SUCCESS_MESSAGE
import kotlinx.android.synthetic.main.activity_login4.*
import org.json.JSONObject

val database = Firebase.database
val myRef = database.getReference("UserListing")

data class User(val username: String? = null, val email: String? = null) {
    // Null default values create a no-argument default constructor, which is needed
    // for deserialization from a DataSnapshot.
}

fun writeNewUser(userId: String, name: String, email: String) {
    val user = User(name, email)
    myRef.child("users").child("numberOfUsers").get().addOnSuccessListener {
        Log.d("AAAA", it.value.toString())
        var numberOfUsers = it.value.toString()
        var intNumberOfUsers = numberOfUsers.toInt()
        intNumberOfUsers = intNumberOfUsers + 1
        while (numberOfUsers.length < 6) {
            numberOfUsers = "0" + numberOfUsers
        }
        myRef.child("users").child(userId).setValue(user)
        myRef.child("users").child(userId).child("numberOfSessions").setValue("0")
        myRef.child("users").child(userId).child("userNumber").setValue(numberOfUsers)
        myRef.child("users").child("numberOfUsers").setValue(intNumberOfUsers.toString())
        myRef.child("codeList").child(numberOfUsers).setValue(userId)
        myRef.child("users").child(userId).child("username").setValue("Your Name")
        myRef.child("users").child(userId).child("userAddress").setValue("Your Address")
        myRef.child("users").child(userId).child("userBusiness").setValue("Your Business")
    }
}

class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login4)
        val intent = intent
        val message = intent.getStringExtra(LOGIN_SUCCESS_MESSAGE)
        var userid = ""
        var email = ""
        try {
            val obj = JSONObject(message)
            Log.d("My App", obj.toString())
            userid = obj.getString("user_id")
            email = obj.getString("identifier")
        } catch (tx: Throwable) {
            Log.e("My App", "Could not parse malformed JSON: \"" + "\"")
        }
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, "password")
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    Toast.makeText(
                        this@Login,
                        "You were logged in successfully",
                        Toast.LENGTH_SHORT
                    ).show()

                    val intent =
                        Intent(this@Login, MainActivity::class.java)
                    intent.flags =
                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    intent.putExtra("user_id", FirebaseAuth.getInstance().currentUser!!.uid)
                    intent.putExtra("email_id", email)
                    //intent.putExtra("name",FirebaseAuth.getInstance().currentUser!!.name)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(
                        this@Login,
                        task.exception!!.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, "password")
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val firebaseUser: FirebaseUser = task.result!!.user!!

                                writeNewUser(firebaseUser.uid, "name", email)

                                Toast.makeText(
                                    this@Login,
                                    "You were registered successfully",
                                    Toast.LENGTH_SHORT
                                ).show()

                                val intent =
                                    Intent(this@Login, MainActivity::class.java)
                                intent.flags =
                                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                intent.putExtra("user_id", firebaseUser.uid)
                                intent.putExtra("email_id", email)
                                intent.putExtra("name", "name")
                                startActivity(intent)
                                finish()
                            } else {
                                Toast.makeText(
                                    this@Login,
                                    task.exception!!.message.toString(),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                }
            }
    }
}