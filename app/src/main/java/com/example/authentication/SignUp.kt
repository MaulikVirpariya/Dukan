package com.example.authentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.authentication.databinding.ActivityMainBinding
import com.example.authentication.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.Format
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class SignUp : AppCompatActivity() {

    private lateinit var binding:ActivitySignUpBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.textView.setOnClickListener{
            val INtent= Intent(this, SignIn::class.java)
            startActivity(INtent)
        }

        binding.button.setOnClickListener{
            val email = binding.emailEt.text.toString()
            val pass = binding.passET.text.toString()
            val confirmPass  = binding.confirmPassEt.text.toString()

            if(email.isNotEmpty() && pass.isNotEmpty() && confirmPass.isNotEmpty()){
                if(pass== confirmPass){
                    firebaseAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener {
                        if(it.isSuccessful){
                            val db = FirebaseFirestore.getInstance()
                            val formatter= SimpleDateFormat("yyyy_MM_dd", Locale.getDefault())
                            val now= Date()
                            val FileName= formatter.format(now)

                            val items= HashMap<String,Any>()
                            items.put("email",email)
                            val customer = arrayListOf<Any>("")
                            items.put("customer",customer)
                            db.collection("Users").document("$email").set(items)
                            val INtent= Intent(this, SignIn::class.java)
                            startActivity(INtent)
                        }else{
                            Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }
                }else{
                    Toast.makeText(this, "Password is not matching", Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this, "Empty Fields Are Not Allowed", Toast.LENGTH_SHORT).show()
            }
        }
    }
}