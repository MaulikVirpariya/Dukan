package com.example.authentication

import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_add_customer.*
import kotlinx.android.synthetic.main.activity_add_customer.btnSave
import kotlinx.android.synthetic.main.activity_add_expensis.*
import kotlinx.android.synthetic.main.activity_add_expensis.Et_Customer_name
import kotlinx.android.synthetic.main.activity_customer_report.*

class Customer_report : AppCompatActivity() {
    lateinit var email:String
    lateinit var customer : List<String>
    private lateinit var newRecycleView: RecyclerView
    private lateinit var CustomerList : ArrayList<Customer>
    private var mSelected_Position =0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_report)
        CustomerList= arrayListOf<Customer>()
        // -------------------- Adapter Setting -----------------//
        email = intent.getStringExtra("email").toString()
        //  val email="testin1@gmail.com"
        val db= FirebaseFirestore.getInstance()
        val useRef = db.collection("Users").document("${email}").get()
            .addOnSuccessListener {
                Log.e("Database","Data is collected For Auto Complete Dialoage")
                customer = it.data!!.get("customer") as List<String>
                Log.e("Data","${customer.toString()}")
                var adapter =
                    ArrayAdapter<Any>(this,android.R.layout.simple_spinner_dropdown_item,customer)
                Et_Customer_name.setAdapter(adapter)
            }.addOnFailureListener {
                Log.e("Fail","Failure In Data is collected For Auto Complete Dialoage \n${it.stackTrace.toString()}")
            }

        newRecycleView =findViewById<RecyclerView>(R.id.sc_view)
        newRecycleView.layoutManager= LinearLayoutManager(this)
        newRecycleView.setHasFixedSize(true)
        val inntent = Intent(this,user_entry::class.java)
        var adapter = MyAdapter(CustomerList)
        newRecycleView.adapter=adapter

        //--------------- to user entry View ------------------//
        adapter.setOnItemClickListener(object : MyAdapter.onItemClickListener{

            override fun onItemClick(position: Int) {
                mSelected_Position=position
//                Toast.makeText(this, "this ${CustomerList[position].toString()}", Toast.LENGTH_SHORT).show()
                inntent.putExtra("name",CustomerList[position].name)
                inntent.putExtra("Money",CustomerList[position].Money)
                inntent.putExtra("DocId",CustomerList[position].DocId)
                inntent.putExtra("Date",CustomerList[position].Date)
                inntent.putExtra("image",CustomerList[position].imageStr)
                inntent.putExtra("email",email)
//                startActivity(inntent)
                startActivityForResult(inntent,100)
                Log.e("Toast","this ${CustomerList[position].toString()}")
            }

        })

        // ---------------- Swip Gesture ---------------------//
        val swipeGesture = object : swipeGesture(this){
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                when(direction){
                    ItemTouchHelper.RIGHT -> {
                        adapter.deletItem(viewHolder.adapterPosition)

                    }
                }
            }
        }
        val touchHelper = ItemTouchHelper(swipeGesture)
        touchHelper.attachToRecyclerView(newRecycleView)
        newRecycleView.adapter?.notifyDataSetChanged()

        //----------------- Save btn -----------//
        btnSave1.setOnClickListener {

            CustomerList.clear()
            val db = FirebaseFirestore.getInstance()

            var resultToDisplay=""
            db.collection("Users").document("${email}").collection("${customer[Et_Customer_name.selectedItemPosition]}").get()
                     .addOnSuccessListener {
                                Log.e("Datbase","In Success of ${customer[Et_Customer_name.selectedItemPosition]} Listener ${it.toString()}")
                                var baki=0
                                for (document in it.documents!!){
                                    val temp= Customer(customer[Et_Customer_name.selectedItemPosition].toString(),
                                        document.getString("Money").toString(),
                                        document.getString("Date").toString()+"\t"+document.getString("Time").toString(),
                                        document.getString("image").toString(),
                                        document.id.toString(),
                                        email)

                                    CustomerList.add(temp)
                                    Log.e("Data of ${customer[Et_Customer_name.selectedItemPosition]}","${temp.toString()} \n size:: ${CustomerList.size}")
//                                    newRecycleView.adapter?.notifyDataSetChanged()

                                    baki+= document.getString("Money")!!.toInt()

//                                Log.e("Data",resultToDisplay)
                                }
                         tv_liner.visibility= View.VISIBLE
                         tvremaining.text="${baki}\tRupees"
                                newRecycleView.adapter?.notifyDataSetChanged()
                                resultToDisplay+="\n\n${customer[Et_Customer_name.selectedItemPosition]} To Collect $baki\n\n"
                                baki=0
                            }
                            .addOnFailureListener{
                                Log.e("Datbase","In Failure of ${customer[Et_Customer_name.selectedItemPosition]} Listener")
                                Log.e("Error",it.stackTrace.toString())
                            }
                    }

                }






        }