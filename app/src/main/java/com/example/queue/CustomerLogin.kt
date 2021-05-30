package com.example.queue

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.sawolabs.androidsdk.LOGIN_SUCCESS_MESSAGE
import kotlinx.android.synthetic.main.activity_customer_login.*
import org.json.JSONObject

class CustomerLogin : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_login)
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
                        this@CustomerLogin,
                        "You were logged in successfully",
                        Toast.LENGTH_SHORT
                    ).show()

                    val intent =
                        Intent(this@CustomerLogin, EnterCode::class.java)
                    intent.flags =
                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    intent.putExtra("user_id", FirebaseAuth.getInstance().currentUser!!.uid)
                    intent.putExtra("email_id", email)
                    //intent.putExtra("name",FirebaseAuth.getInstance().currentUser!!.name)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(
                        this@CustomerLogin,
                        task.exception!!.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, "password")
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val firebaseUser: FirebaseUser = task.result!!.user!!

                                writeNewUsera(firebaseUser.uid, "name", email)

                                Toast.makeText(
                                    this@CustomerLogin,
                                    "You were registered successfully",
                                    Toast.LENGTH_SHORT
                                ).show()

                                val intent =
                                    Intent(this@CustomerLogin, EnterCode::class.java)
                                intent.flags =
                                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                intent.putExtra("user_id", firebaseUser.uid)
                                intent.putExtra("email_id", email)
                                intent.putExtra("name", "name")
                                startActivity(intent)
                                finish()
                            } else {
                                Toast.makeText(
                                    this@CustomerLogin,
                                    task.exception!!.message.toString(),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                }
            }
    }
}