package com.example.eatbylogin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.eatbylogin.databinding.ActivityFetchingBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase



class FetchingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFetchingBinding
    private lateinit var auth: FirebaseAuth

    private lateinit var empRecyclerView: RecyclerView
    private lateinit var tvLoadingData: TextView
    private lateinit var empList: ArrayList<EmployeeModel>
    private lateinit var dbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFetchingBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val firebase : DatabaseReference = FirebaseDatabase.getInstance().getReference()

        empRecyclerView = findViewById(R.id.empRecyclerView)
        empRecyclerView.layoutManager = LinearLayoutManager(this)
        empRecyclerView.setHasFixedSize(true)
        tvLoadingData = findViewById(R.id.tvLoadingData)

        empList = arrayListOf<EmployeeModel>()

        val mAdapter = EmpAdapter(empList, object : EmpAdapter.onItemClickListener {
            override fun onItemClick(position: Int) {
                // Implemente a ação do clique do item aqui
            }
        })

        empRecyclerView.adapter = mAdapter


        getEmployeesData()

    }

    private fun getEmployeesData() {

        empRecyclerView.visibility = View.GONE
        tvLoadingData.visibility = View.VISIBLE

        dbRef = FirebaseDatabase.getInstance().getReference("Employees")

        dbRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                empList.clear()
                if (snapshot.exists()){
                    for (empSnap in snapshot.children){
                        val empData = empSnap.getValue(EmployeeModel::class.java)
                        empList.add(empData!!)
                    }
                    val mAdapter = EmpAdapter(empList, object : EmpAdapter.onItemClickListener {
                        override fun onItemClick(position: Int) {
                            val intent = Intent(this@FetchingActivity, activity_employee_details::class.java)
                            intent.putExtra("empId", empList[position].empId)
                            intent.putExtra("empName", empList[position].empProductName)
                            intent.putExtra("empAge", empList[position].empED)
                            intent.putExtra("empSalary", empList[position].empDescription)
                            startActivity(intent)
                        }
                    })
                    empRecyclerView.adapter = mAdapter

                    empRecyclerView.visibility = View.VISIBLE
                    tvLoadingData.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        auth = Firebase.auth

    }


}