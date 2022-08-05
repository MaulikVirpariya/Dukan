package com.example.authentication

import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_view_all_entry.*
import java.io.File

class user_entry : AppCompatActivity() {
    lateinit var email : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_all_entry)
        email= intent.getStringExtra("email").toString()
        val DocId = intent.getStringExtra("DocId")
        val name=intent.getStringExtra("name")
        val imageStr=intent.getStringExtra("image")
        Name.setText(name)
        tvDate.setText(intent.getStringExtra("Date"))
        Money.setText(intent.getStringExtra("Money"))

        // ----------------- download Image -----------------//
        val storageReferance = FirebaseStorage.getInstance().reference.child("images/${imageStr}")

        val localFile= File.createTempFile("tempImage",".jpg")
        storageReferance.getFile(localFile).addOnSuccessListener{
            imageView.setImageBitmap(BitmapFactory.decodeFile(localFile.absolutePath))
        }.addOnFailureListener{
        Log.e("Failure","${it.stackTrace.toString()}")
        }
        // ---------------- image download Complete ---------------//
//                                Log.e("Image Path $j","${localFile.absolutePath.toUri().toString()}")

        btn_delet.setOnClickListener {

            Log.e("Delet","${email}->${name}->${DocId}")
            val db = FirebaseFirestore.getInstance()
            if (name != null) {
                if (DocId != null) {
                    db.collection("Users").document("${email}").collection(name).document(DocId).delete()
                        .addOnSuccessListener {
                            storageReferance.delete()
                            setResult(RESULT_OK)
                            finish()
                        }
                        .addOnFailureListener{
                            Log.e("Failure","${it.stackTrace.toString()}")
                        }
                }
            }

        }


    }
}