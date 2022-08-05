package com.example.authentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*
import com.example.authentication.Customer_report as Customer_report1

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val email = intent.getStringExtra("email")
        if (email != null) {
            Log.e("Mail is",email)
        }else{
            Log.e("Mail is","email is null")
        }

        btn_add_customer.setOnClickListener {
            val intent1 = Intent(this, AddCustomer::class.java)
            intent1.putExtra("email", email)
            startActivity(intent1)
        }

        btn_addExpensis.setOnClickListener {
            val intent1 = Intent(this, AddExpensis::class.java)
            intent1.putExtra("email", email)
            startActivity(intent1)
        }

        btn_ViewData.setOnClickListener {
            val intent1 = Intent(this, RecycleView::class.java)
            intent1.putExtra("email", email)
            startActivity(intent1)
        }

        btn_Customer_report.setOnClickListener {
            val intent1 = Intent(this, Customer_report1::class.java)
            intent1.putExtra("email", email)
            startActivity(intent1)
        }
    }
}