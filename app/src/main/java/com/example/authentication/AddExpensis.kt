package com.example.authentication

import android.app.Activity.RESULT_OK
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.net.toUri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_add_expensis.*
import kotlinx.android.synthetic.main.activity_main.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class AddExpensis : AppCompatActivity() {

    lateinit var ImageUri : Uri
    lateinit var Name : String
    lateinit var email : String
    lateinit var FileName : String
    lateinit var customer : List<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_expensis)

        savebutton.isEnabled = false
        Et_Date.isEnabled= false


        //-------------------- Seting Auto Complete ---------------//
         email = intent.getStringExtra("email").toString()
        //  val email="testin1@gmail.com"
        val db= FirebaseFirestore.getInstance()
        val useRef = db.collection("Users").document("${email}").get()
            .addOnSuccessListener {
                Log.e("Database","Data is collected For Auto Complete Dialoage")
                customer = it.data!!.get("customer") as List<String>
                Log.e("Data","${customer.toString()}")
                var adapter =ArrayAdapter<Any>(this,android.R.layout.simple_spinner_dropdown_item,customer)
                Et_Customer_name.setAdapter(adapter)
            }.addOnFailureListener {
                Log.e("Fail","Failure In Data is collected For Auto Complete Dialoage \n${it.stackTrace.toString()}")
            }
        //------------------ done -------------------------------//


        // --------------  Seting  Date ------------------------//
        var calander_Instance = Calendar.getInstance()
//        Et_Date.setText("${calander_Instance.get(Calendar.DAY_OF_MONTH)}/${calander_Instance.get(Calendar.MONTH)}/${calander_Instance.get(Calendar.YEAR)}\t ${calander_Instance.get(Calendar.HOUR)}:${calander_Instance.get(Calendar.MINUTE)}")
        val formatter= SimpleDateFormat("dd/MM/yyyy",Locale.getDefault())
        val now= Date()
        Et_Date.setText(formatter.format(now))
        Et_Date.setOnClickListener{
//            DatePickerDialog(this, DatePickerDialog.OnDateSetListener{view, year, month, dayOfMonth ->
//                Et_Date.setText("${dayOfMonth}/${month}/${year}")
//            },
//                calander_Instance.get(Calendar.YEAR),calander_Instance.get(Calendar.MONTH), calander_Instance.get(Calendar.DAY_OF_MONTH)).show()

        }
        // --------------- Done -------------------------------//


        // ---------------- image Update -------------------//
        imagebtn.setOnClickListener {
            if (Build.VERSION.SDK_INT >= 30) {
                if (!Environment.isExternalStorageManager()) {
                    val getpermission = Intent()
                    getpermission.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                    startActivity(getpermission)
                }
            }
            if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,android.Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,android.Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED
            ){
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA,android.Manifest.permission.WRITE_EXTERNAL_STORAGE,android.Manifest.permission.READ_EXTERNAL_STORAGE,android.Manifest.permission.MANAGE_EXTERNAL_STORAGE),100)
            }else{
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(intent,101)
            }
        }
        // -------------------- Done -------------------------//


        // --------------------- Save Button -----------------//
        savebutton.setOnClickListener{
            val db = FirebaseFirestore.getInstance()
            val user: MutableMap<String,Any> = HashMap()
//            user["name"]=Et_Customer_name.text.toString()
            Et_Customer_name.onItemSelectedListener= object :
             AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    user["name"] = customer[position] as Any
                    Log.e("Selected Customer","${user["name"]}")
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    TODO("Not yet implemented")
                }

            }
            user["name"] = customer[Et_Customer_name.selectedItemPosition]
            Log.e("Selected Customer","${user["name"]}")
            user["mail"]=email
            user["Money"]=Et_Money.text.toString()
            val now= Date()
            val OnlyDate=SimpleDateFormat("dd/MM/yyyy",Locale.getDefault())
            user["Date"]=OnlyDate.format(now)
            Et_Date.setText(OnlyDate.format(now))
            val formatter= SimpleDateFormat("HH:mm:ss",Locale.getDefault())
            user["Time"]=formatter.format(now)
            user["image"]=FileName

            db.collection("Users").document("${email}").collection("${ user["name"]}").add(user)
                .addOnSuccessListener {
                    Toast.makeText(this, "Added Succcessfully", Toast.LENGTH_SHORT).show()
                            Et_Customer_name.setSelection(0)
                            Et_Money.setText("")
                            val formatter= SimpleDateFormat("dd/MM/yyyy\t\tHH:mm",Locale.getDefault())
                            val now= Date()
                            Et_Date.setText(formatter.format(now))
                            imageView1.setImageDrawable(getResources().getDrawable(R.drawable.ic_launcher_background))
                            savebutton.isEnabled = false
                }.addOnFailureListener{
                    Toast.makeText(this, "Record Failure", Toast.LENGTH_SHORT).show()
                    Log.e("Fail","Failure In upload Record  \n${it.stackTrace.toString()}")
                }

        }


        }



    //----------------- Getting Requests ----------------//
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode==100 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
        {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent,101)
        }
    }
    //----------------- done ----------------//



    // -------------------- Activity Result Handling --------------------//
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode==101 && resultCode== RESULT_OK){
            Log.e("Load","Intialize Camara")
            var pic: Bitmap? =data?.getParcelableExtra<Bitmap>("data")
            imageView1.setImageBitmap(pic)
            Log.e("Load","Captured Image")


            // -------------- saving to Internal Storage --------//

            val formatter= SimpleDateFormat("yyyy_MM_dd_HH_mm_ss",Locale.getDefault())
            val now= Date()
            FileName= formatter.format(now)
            var file1: File? = null

            file1 = File(Environment.getExternalStorageDirectory().toString() + File.separator + (FileName+".jpg"))
            file1.createNewFile()
            val bos = ByteArrayOutputStream()
            if (pic != null) {
                pic.compress(Bitmap.CompressFormat.JPEG, 55, bos)
            }
            val bitmapdata = bos.toByteArray()
            val fos = FileOutputStream(file1)
            fos.write(bitmapdata)
            fos.flush()
            fos.close()
//            imageView1.setImageURI(file1.absolutePath.toUri())
            ImageUri= File(file1.path).toUri()
            Log.e("Save","Saved Image to File Uri \t ${ImageUri}")






            // -------------------- uploading Code --------------//

            val progressDialog = ProgressDialog(this)
            progressDialog.setMessage("Uploading Files...")
            progressDialog.setCancelable(false)
            progressDialog.show()

            val storageReferance = FirebaseStorage.getInstance().getReference("images/$FileName")
            Log.e("Upload File Name","--> \t\t $FileName \t\t Size :: ${FileName.length}")
            storageReferance.putFile(ImageUri)
                .addOnSuccessListener {
                    if(progressDialog.isShowing) progressDialog.dismiss()
//                    ImageUploadSuccess(FileName)
                    Toast.makeText(this, "Successfully Uploaded", Toast.LENGTH_SHORT).show()
                    savebutton.isEnabled = true
     }
                .addOnFailureListener{
                    Toast.makeText(this, "Failed to Uploaded ${it.stackTrace.toString()}", Toast.LENGTH_SHORT).show()
                    Log.e("Fail","Failure In uploading Photo  \n${it.stackTrace.toString()}")
                }
        }
    }

}