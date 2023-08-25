package com.example.eatbylogin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import com.example.eatbylogin.databinding.ActivityInsertBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.Calendar
import java.text.SimpleDateFormat
import java.util.Locale

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
        dbRef = FirebaseDatabase.getInstance().reference.child("Employees")

        binding.btnSave.setOnClickListener {
            saveEmployeeData()
        }

        val dateMaskWatcher = object : TextWatcher {
            private val inputDateFormat = SimpleDateFormat("ddMMyyyy", Locale.getDefault())
            private val outputDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            private val cal = Calendar.getInstance()

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (s != null && s.toString().isNotEmpty()) {
                    try {
                        val inputText = s.toString()

                        if (inputText.length == 8) {
                            // Check if the year has 4 digits, if not, set it to the current year
                            val year = inputText.substring(4)
                            val currentYear = Calendar.getInstance().get(Calendar.YEAR).toString()
                            val formattedYear = if (year.length < 4) currentYear else year

                            val inputDate = inputText.substring(0, 4) + formattedYear
                            val date = inputDateFormat.parse(inputDate)
                            val formattedDate = outputDateFormat.format(date!!)
                            binding.etEmpAge.removeTextChangedListener(this)
                            binding.etEmpAge.setText(formattedDate)
                            binding.etEmpAge.setSelection(formattedDate.length)
                            binding.etEmpAge.addTextChangedListener(this)
                        }
                    } catch (e: Exception) {
                        // Handle the exception, e.g., invalid date input
                    }
                }
            }
        }

        binding.etEmpAge.addTextChangedListener(dateMaskWatcher)

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
    //


    }


}
