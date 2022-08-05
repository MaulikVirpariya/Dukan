package com.example.authentication

import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import android.os.Bundle
import android.util.Log
import android.widget.Adapter
import android.widget.Toast
import androidx.core.net.toUri
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_recycle_view.*
import kotlinx.android.synthetic.main.activity_view_all_entry.*
import java.io.File
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.ItemTouchHelper
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatter.ofPattern
import java.util.*
import kotlin.collections.ArrayList


class RecycleView : AppCompatActivity() {

    private lateinit var newRecycleView: RecyclerView
    private lateinit var CustomerList : ArrayList<Customer>
    var email = ""
    private var mSelected_Position =0
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recycle_view)
        CustomerList= arrayListOf<Customer>()
        getCustomerList()

        newRecycleView =findViewById<RecyclerView>(R.id.recyclerView1)
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
        Log.e("Completion","Task Successful ${CustomerList.size}")


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

    }
        // ---------------- Sort The Array List ------------------//




    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode==100 && resultCode== RESULT_OK){
            CustomerList.remove(CustomerList[mSelected_Position])
            Log.e("Data","DataBase Changed \n Size :: ${CustomerList.size}")
            newRecycleView.adapter?.notifyDataSetChanged()
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun getCustomerList() {
        email= intent.getStringExtra("email").toString()
        val db = FirebaseFirestore.getInstance()

        var resultToDisplay=""
        db.collection("Users").document("${email}").get()
            .addOnSuccessListener {
                Log.e("Datbase","Getting name of SubClass Listener")
                Log.e("Received Data","${it.data?.toString()}")
                val array = it.data?.get("customer") as List<*>
                var baki :Int=0
                var bitmap = BitmapFactory.decodeFile(getResources().getDrawable(R.drawable.ic_launcher_background)
                    .toString())
                var bitmappath=""
                for (j in array){

                    db.collection("Users").document("${email}").collection("$j").get()
                        .addOnSuccessListener {
                            Log.e("Datbase","In Success of $j Listener ${it.toString()}")

                            for (document in it.documents!!){
                                val temp= Customer(j.toString(),
                                    document.getString("Money").toString(),
                                    document.getString("Date").toString()+"\t"+document.getString("Time").toString(),
                                    document.getString("image").toString(),
                                    document.id.toString(),
                                    email)
                                CustomerList.add(temp)


                                Log.e("Data of $j","${temp.toString()} \n size:: ${CustomerList.size}")
                                newRecycleView.adapter?.notifyDataSetChanged()

                                baki+= document.getString("Money")!!.toInt()
//                                Log.e("Data",resultToDisplay)
                            }
                            resultToDisplay+="\n\n$j To Collect $baki\n\n"
                            baki=0
                        }
                        .addOnFailureListener{
                            Log.e("Datbase","In Failure of $j Listener")
                            Log.e("Error",it.stackTrace.toString())
                        }
                }

            }
            .addOnFailureListener{
                Log.e("Datbase","In Failure Listener")
                Log.e("Error",it.stackTrace.toString())
            }
    }

}