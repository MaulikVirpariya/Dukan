package com.example.authentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.authentication.databinding.ActivitySignInBinding
import com.example.authentication.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth


class SignIn : AppCompatActivity() {
    private lateinit var binding: ActivitySignInBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivitySignInBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        
        val sharedPref = getSharedPreferences("myPref", MODE_PRIVATE)
        val editor = sharedPref.edit()

        if(!sharedPref.getString("email",null).isNullOrBlank()){
            binding.emailEt.setText(sharedPref.getString("email",null).toString())
            binding.passET.setText(sharedPref.getString("Pass",null).toString())
        }
        firebaseAuth = FirebaseAuth.getInstance()
        binding.textView.setOnClickListener{
            val INtent= Intent(this, SignUp::class.java)
            startActivity(INtent)
        }

        binding.button.setOnClickListener{
            val email = binding.emailEt.text.toString()
            val pass = binding.passET.text.toString()

            if(email.isNotEmpty() && pass.isNotEmpty() ){
                    firebaseAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener {
                        if(it.isSuccessful){
                            editor.apply{
                                putString("email",email)
                                putString("Pass",pass)
                                editor.apply()
                            }
                            val INtent= Intent(this, MainActivity::class.java)
//                            val INtent= Intent(this, AddExpensis::class.java)
                            INtent.putExtra("email",email)
                            startActivity(INtent)

                        }else{
                            Log.e("Error",it.exception.toString())
                            Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }
            }else{
                Toast.makeText(this, "Empty Fields Are Not Allowed", Toast.LENGTH_SHORT).show()
            }
        }
    }
}