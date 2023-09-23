package com.example.vpa

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class MyAdapter(private val context: Context, private var dataList: List<DataClass>) : RecyclerView.Adapter<MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.recycler_item, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.recModel.text = dataList[position].vehicle
        holder.recName.text = dataList[position].name
        holder.recVehicleNo.text = dataList[position].vehNo
        holder.recCard.setOnClickListener {
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra("name", dataList[holder.adapterPosition].name)
            intent.putExtra("vehicle", dataList[holder.adapterPosition].vehicle)
            intent.putExtra("Key", dataList[holder.adapterPosition].key)
            intent.putExtra("vehNo", dataList[holder.adapterPosition].vehNo)
            intent.putExtra("token", dataList[holder.adapterPosition].token)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    fun searchDataList(searchList: List<DataClass>) {
        dataList = searchList
        notifyDataSetChanged()
    }
}

class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val recModel: TextView = itemView.findViewById(R.id.recModel)
    val recName: TextView = itemView.findViewById(R.id.recName)
    val recVehicleNo: TextView = itemView.findViewById(R.id.recVehicleNo)
    val recCard: CardView = itemView.findViewById(R.id.recCard)
}
