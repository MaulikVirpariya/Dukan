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


class Customer_name_adapter (private  val CustomerNameList : List<String>) :
    RecyclerView.Adapter<Customer_name_adapter.C_ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): C_ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.c_item_view,parent,false)
        return C_ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: C_ViewHolder, position: Int) {
        val currentItem = CustomerNameList[position]
        holder.tvName.setText(currentItem)
    }

    override fun getItemCount(): Int {
        return CustomerNameList.size
    }

    class C_ViewHolder( itemView: View) : RecyclerView.ViewHolder(itemView)  {
        val tvName: TextView = itemView.findViewById(R.id.tvName)
    }


}
