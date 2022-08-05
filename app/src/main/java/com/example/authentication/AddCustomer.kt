package com.example.authentication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_add_customer.*
import kotlinx.android.synthetic.main.activity_add_expensis.*
import kotlinx.android.synthetic.main.activity_main.*

class AddCustomer : AppCompatActivity() {
    private lateinit var email :String

//    private lateinit var customerNameList: ArrayList<String>
    lateinit var customerName : List<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_customer)
        email = intent.getStringExtra("email").toString()
        Log.e("Mail is ",email)
//        customerNameList=arrayListOf<String>()
        customerName = listOf()
//        getcustomerNameList()


        val db= FirebaseFirestore.getInstance()


        var CustomerRecycleView = findViewById<RecyclerView>(R.id.sc_view)
        CustomerRecycleView.layoutManager =LinearLayoutManager(this)
        CustomerRecycleView.setHasFixedSize(true)

        var adapter = Customer_name_adapter(customerName)
        CustomerRecycleView.adapter=adapter
        Log.e("adapter","${customerName.size}\t\t${adapter.toString()}")
        adapter.notifyDataSetChanged()

        val useRef = db.collection("Users").document("${email}").get()
            .addOnSuccessListener {
                customerName= it.data!!.get("customer") as List<String>
                Log.e("Data :",customerName.toString()+"\n Size is ${customerName.size}")
            }.addOnCompleteListener {
                CustomerRecycleView.adapter=Customer_name_adapter(customerName)
            }


        btnSave.setOnClickListener {
            val db= FirebaseFirestore.getInstance()
            val useRef = db.collection("Users").document("${email}").get()
                .addOnSuccessListener {
                    Log.e("Database","Data is collected ${it.data!!.get("customer").toString().length}")

                    if(it.data!!.get("customer").toString().length <3 ){
                        var temp= ArrayList<String>()
                        temp.add(et_cust.text.toString())
                        var cust_list="1.\t\t${et_cust.text.toString()}"

                        val docref = db.collection("Users").document("${email}")
                        docref.update(mapOf("customer" to temp)).addOnSuccessListener {
                            Log.e("Update","Update Success")
                            Toast.makeText(this, "Array Updated", Toast.LENGTH_SHORT).show()
                        }.addOnFailureListener{
                            Log.e("Update Fail","${it.stackTrace.toString()}")
                        }

                        CustomerRecycleView.adapter = Customer_name_adapter(temp as List<String>)
                    }else {
                        var customer = it.data!!.get("customer") as List<*>
                        var temp = ArrayList<String>()
                        temp.add(et_cust.text.toString())
                        customerName = temp as List<String>
                        var cust_list = "1.\t\t${et_cust.text.toString()}"

                        for (i in 0..(customer.size - 1)) {
                            cust_list += "\n${i+2}.\t\t ${customer[i]}"
                            Log.e("Iteration", "${i} \t\t ${customer[i]}")
                            temp.add(customer[i].toString())
                        }
                        Log.e("Database", "Data Creation Done")
                        val docref = db.collection("Users").document("${email}")
                        docref.update(mapOf("customer" to temp)).addOnSuccessListener {
                            Log.e("Update","Update Success")
                            Toast.makeText(this, "Array Updated", Toast.LENGTH_SHORT).show()
                        }.addOnFailureListener{
                            Log.e("Update Fail","${it.stackTrace.toString()}")
                        }

                        CustomerRecycleView.adapter = Customer_name_adapter(customerName)
                    }
                    //----------------------------------------------------------------------------//
//                    val docref = db.collection("Users").document("${email}")
//                    docref.update(mapOf("customer" to temp)).addOnSuccessListener {
//                        Log.e("Update","Update Success")
//                        Toast.makeText(this, "Array Updated", Toast.LENGTH_SHORT).show()
//                    }.addOnFailureListener{
//                        Log.e("Update Fail","${it.stackTrace.toString()}")
//                    }
//                    tv_display.text= temp.toString()+"\t Size:: "+ customer.size

                }
        }

    }




}