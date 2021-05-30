package com.example.queue


import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_register.*

val databasea = Firebase.database
val myRefa = databasea.getReference("UserListing")

data class Usera(val username: String? = null, val email: String? = null) {
    // Null default values create a no-argument default constructor, which is needed
    // for deserialization from a DataSnapshot.
}

fun writeNewUsera(userId: String, name: String, email: String) {
    val user = Usera(name, email)
    myRefa.child("userCustomers").child("numberOfUsers").get().addOnSuccessListener {
        Log.d("AAAA", it.value.toString())
        var numberOfUsers = it.value.toString()
        var intNumberOfUsers = numberOfUsers.toInt()
        intNumberOfUsers = intNumberOfUsers + 1
        while (numberOfUsers.length < 6) {
            numberOfUsers = "0" + numberOfUsers
        }
        myRefa.child("userCustomers").child(userId).child("currentBusiness").setValue("X")
        myRefa.child("userCustomers").child(userId).setValue(user)
        myRefa.child("userCustomers").child(userId).child("numberOfSessions").setValue("0")
        myRefa.child("userCustomers").child(userId).child("userNumber").setValue(numberOfUsers)
        myRefa.child("userCustomers").child("numberOfUsers").setValue(intNumberOfUsers.toString())
        myRefa.child("userCustomers").child(userId).child("currentBusiness").setValue("X")
    }
}

//fun addSongToPlaylist(userId: String, playListId: String, songId: String) {
//    val sizeOfPlaylist = myRefa.child("userCustomers").child(userId).child(playListId).child("playListSize").get().toString()
//    val intSizeOfPlaylist = sizeOfPlaylist.toInt()
//    myRefa.child("userCustomers").child(userId).child(playListId).child(sizeOfPlaylist.toString()).setValue(songId)
//    myRefa.child("userCustomers").child(userId).child(playListId).child("playListSize").setValue((intSizeOfPlaylist+1).toString())
//}
//
//fun createPlaylistEmpty(userId: String, playListNumber: Int, playListName: String) {
//    val numOfPlaylist = myRefa.child("userCustomers").child(userId).child("num_playlists").get().toString()
//    val intNumOfPlaylist = numOfPlaylist.toInt()
//    myRefa.child("userCustomers").child(userId).child(numOfPlaylist.toString()).child("name").setValue(playListName)
//    myRefa.child("userCustomers").child(userId).child(numOfPlaylist.toString()).child("playListSize").setValue("0")
//    myRefa.child("userCustomers").child(userId).child(numOfPlaylist).setValue((intNumOfPlaylist+1).toString())
//}

class CustomerRegistration : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_registration)

        tv_login.setOnClickListener {
            val intent =
                Intent(this@CustomerRegistration, LoginActivity::class.java)
            intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        btn_register.setOnClickListener {
            when {
                TextUtils.isEmpty(et_register_email.text.toString().trim { it <= ' ' }) -> {
                    Toast.makeText(
                        this@CustomerRegistration,
                        "Please enter email.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                TextUtils.isEmpty(et_register_password.text.toString().trim { it <= ' ' }) -> {
                    Toast.makeText(
                        this@CustomerRegistration,
                        "Please enter password.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                TextUtils.isEmpty(et_register_name.text.toString().trim { it <= ' ' }) -> {
                    Toast.makeText(
                        this@CustomerRegistration,
                        "Please enter name.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                else -> {
                    val email: String = et_register_email.text.toString().trim { it <= ' ' }
                    val password: String = et_register_password.text.toString().trim { it <= ' ' }
                    val name: String = et_register_name.text.toString().trim { it <= ' ' }

                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val firebaseUser: FirebaseUser = task.result!!.user!!

                                writeNewUsera(firebaseUser.uid, name, email)

                                Toast.makeText(
                                    this@CustomerRegistration,
                                    "You were registered successfully",
                                    Toast.LENGTH_SHORT
                                ).show()

                                val intent =
                                    Intent(this@CustomerRegistration, EnterCode::class.java)
                                intent.flags =
                                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                intent.putExtra("user_id", firebaseUser.uid)
                                intent.putExtra("email_id", email)
                                intent.putExtra("name", name)
                                startActivity(intent)
                                finish()
                            } else {
                                Toast.makeText(
                                    this@CustomerRegistration,
                                    task.exception!!.message.toString(),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                }
            }
        }
    }
}