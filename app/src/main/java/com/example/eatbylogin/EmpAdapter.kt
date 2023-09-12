package com.example.eatbylogin

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class EmpAdapter(private val empList: ArrayList<EmployeeModel>, private val mListener: onItemClickListener) :
    RecyclerView.Adapter<EmpAdapter.ViewHolder>() {


    interface onItemClickListener {
        fun onItemClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.emp_list_item, parent, false)
        return ViewHolder(itemView, mListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentEmp = empList[position]
        holder.tvEmpName.text = currentEmp.empProductName

        if (currentEmp.isExpired) {
            holder.expirationIndicator.visibility = View.VISIBLE
            Log.d("MyApp", "O item está vencido: ${currentEmp.empProductName}")
        } else {
            holder.expirationIndicator.visibility = View.GONE
            Log.d("MyApp", "O item não está vencido: ${currentEmp.empProductName}")
        }

    }


    override fun getItemCount(): Int {
        return empList.size
    }

    class ViewHolder(itemView: View, clickListener: onItemClickListener) : RecyclerView.ViewHolder(itemView) {
        val tvEmpName: TextView = itemView.findViewById(R.id.tvEmpName)
        val expirationIndicator: ImageView = itemView.findViewById(R.id.expirationIndicator)

        init {
            itemView.setOnClickListener {
                clickListener.onItemClick(adapterPosition)
            }
        }
    }

}
