package com.example.authentication

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class MyAdapter(private  val CustomerList : ArrayList<Customer>) :
    RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    private lateinit var mListener : onItemClickListener

    interface onItemClickListener {
        fun onItemClick(position: Int)
        
    }

    fun setOnItemClickListener(listener: onItemClickListener){
        mListener = listener
    }

    fun deletItem(iposition:Int){
        val db = FirebaseFirestore.getInstance()
        db.collection("Users").document("${CustomerList[iposition].mail}").collection(CustomerList[iposition].name).document(CustomerList[iposition].DocId).delete()
            .addOnFailureListener {
                Log.e("error delet Image","${it.stackTrace.toString()}")
            }
        val storageReferance = FirebaseStorage.getInstance().reference.child("images/${CustomerList[iposition].imageStr}")
        storageReferance.delete().addOnFailureListener {
            Log.e("error delet Image","${it.stackTrace.toString()}")
        }
        CustomerList.removeAt(iposition)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item,parent,false)

        return MyViewHolder(itemView,mListener)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = CustomerList[position]
//        holder.titleImage.setImageBitmap(currentItem.ImageBitmap)
//        holder.titleImage.setImageDrawable(currentItem.ImageBitmap)
        holder.tvName.setText(currentItem.name)
        holder.tvDate.setText(currentItem.Date)
        holder.tvMoney.setText(currentItem.Money)
    }

    override fun getItemCount(): Int {
        return CustomerList.size
    }

    class MyViewHolder( itemView: View,listener: onItemClickListener) : RecyclerView.ViewHolder(itemView) {

//        val titleImage : ShapeableImageView = itemView.findViewById(R.id.titleImage)
        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val tvMoney: TextView = itemView.findViewById(R.id.tvMoney)
        val tvDate: TextView = itemView.findViewById(R.id.tvDate)

        init {
            itemView.setOnClickListener{
                listener.onItemClick(adapterPosition)
            }
        }

    }

}
