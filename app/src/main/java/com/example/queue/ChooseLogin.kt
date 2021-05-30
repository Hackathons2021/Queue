package com.example.queue

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sawolabs.androidsdk.Sawo
import kotlinx.android.synthetic.main.activity_choose_login.*

class ChooseLogin : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_login)

        customer_btn.setOnClickListener {
            Sawo(
                this,
                "43870b26-d36e-478b-8e71-f8f9e1919d0b",  // your api key,
                "60a90060dcd4d3f63600e4f0rmkapWKyQ0f6e0DCjOKx5R76" // your api key secret
            ).login(
                "email",  // can be one of 'email' or 'phone_number_sms'
                CustomerLogin::class.java.name // Callback class name
            )
        }

        business_btn.setOnClickListener {
            Sawo(
                this,
                "43870b26-d36e-478b-8e71-f8f9e1919d0b",  // your api key,
                "60a90060dcd4d3f63600e4f0rmkapWKyQ0f6e0DCjOKx5R76" // your api key secret
            ).login(
                "email",  // can be one of 'email' or 'phone_number_sms'
                Login::class.java.name // Callback class name
            )
        }
    }
}