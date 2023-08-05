package com.example.eatbylogin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.eatbylogin.databinding.ActivityInsertBinding
import com.example.eatbylogin.databinding.ActivityMain2Binding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class InsertActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInsertBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var dbRef: DatabaseReference
    private val empList: MutableList<EmployeeModel> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInsertBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        auth = Firebase.auth
        dbRef = FirebaseDatabase.getInstance().getReference("Employees")

        binding.btnSave.setOnClickListener{
            saveEmployeeData()
        }
    }

    private fun saveEmployeeData() {
        val empProductName = binding.etEmpName.text.toString()
        val empED = binding.etEmpAge.text.toString()
        val empSalary = binding.etEmpSalary.text.toString()

        if (empProductName.isEmpty()) {
            binding.etEmpName.error = "Please enter product name"
        }
        if (empED.isEmpty()) {
            binding.etEmpAge.error = "Please enter expiration date"
        }
        if (empSalary.isEmpty()) {
            binding.etEmpSalary.error = "Please enter description"
        }

        // Verificar se já existem 10 itens no RecyclerView
        if (empList.size >= 10) {
            Toast.makeText(this, "You can't add more than 10 items", Toast.LENGTH_LONG).show()
            return
        }

        // Se não houver 10 itens, continuar com a adição do novo item
        val empId = dbRef.push().key!!
        val employee = EmployeeModel(empId, empProductName, empED, empSalary)

        dbRef.child(empId).setValue(employee)
            .addOnCompleteListener {
                Toast.makeText(this, "Data inserted successfully", Toast.LENGTH_LONG).show()

                binding.etEmpName.text.clear()
                binding.etEmpAge.text.clear()
                binding.etEmpSalary.text.clear()

                // Atualizar a lista de itens no RecyclerView
                empList.add(employee)
            }
            .addOnFailureListener { err ->
                Toast.makeText(this, "Error ${err.message}", Toast.LENGTH_LONG).show()

            }
    }
}
