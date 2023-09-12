package com.example.eatbylogin

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
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
import com.google.firebase.messaging.FirebaseMessaging
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import androidx.cardview.widget.CardView


class FetchingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFetchingBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var mAdapter: EmpAdapter

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

        mAdapter = EmpAdapter(empList, object : EmpAdapter.onItemClickListener {
            override fun onItemClick(position : Int) {
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

        val currentDate = LocalDate.now()

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                empList.clear()
                if (snapshot.exists()) {
                    for (empSnap in snapshot.children) {
                        val empData = empSnap.getValue(EmployeeModel::class.java)
                        if (empData != null) {
                            val dateString = empData.empED
                            Log.d("DateDebug", "Date String: $dateString")

                            if (!dateString.isNullOrBlank()) {
                                try {
                                    val expirationDate = LocalDate.parse(dateString, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                                    val currentDate = LocalDate.now()
                                    if (expirationDate.isBefore(currentDate) || expirationDate.isEqual(currentDate)) {
                                        empData.empProductName?.let { productName ->
                                            empData.empId?.let { itemId ->
                                                sendNotification(productName, itemId)
                                            }
                                        }
                                    }

                                    empList.add(empData)
                                } catch (e: DateTimeParseException) {
                                    // Tratar valores de data inválidos aqui
                                    e.printStackTrace()
                                }
                            }
                        }
                    }


                    // Atualize o RecyclerView aqui
                    mAdapter.notifyDataSetChanged()

                    empRecyclerView.visibility = View.VISIBLE
                    tvLoadingData.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Lidar com o cancelamento da leitura dos dados do Firebase, se necessário
            }
        })




        auth = Firebase.auth

    }

    private fun sendNotification(productName: String, itemId: String) {
        Log.d("expirationn", "Date String:oi")



        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationId = 1 // Pode ser qualquer valor exclusivo para identificar a notificação

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "emp_channel"
            val channelName = "Emp Channel"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val notificationChannel = NotificationChannel(channelId, channelName, importance)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationManager.createNotificationChannel(notificationChannel)
        }

        val notificationBuilder = NotificationCompat.Builder(this, "emp_channel")
            .setSmallIcon(R.drawable.logo)
            .setContentTitle("Atenção!")
            .setContentText("Seu produto $productName está vencido ou vai vencer em breve.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        notificationManager.notify(notificationId, notificationBuilder.build())

        // Encontrar o item correspondente na lista e atualizar a cor do card
        for (item in empList) {
            Log.d("ListDebug", "Item ID: ${item.empId}, Is Expired: ${item.isExpired}")
            if (item.empId == itemId) {
                item.isExpired = true // Supondo que você tenha um atributo "isExpired" no seu modelo
                break
            }
        }

        // Notificar o adapter para atualizar a exibição dos itens
        mAdapter.notifyDataSetChanged()

        empRecyclerView.adapter?.notifyDataSetChanged()

    }




    class EmpAdapter(
        private val empList: List<EmployeeModel>,
        private val listener: onItemClickListener
    ) : RecyclerView.Adapter<EmpAdapter.EmpViewHolder>() {

    private val currentDate: LocalDate = LocalDate.now()

        inner class EmpViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            val expirationIndicator = itemView.findViewById<View>(R.id.expirationIndicator)

            fun bind(item: EmployeeModel) {
                val expirationDate = LocalDate.parse(item.empED, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                val currentDate = LocalDate.now()

                if (expirationDate.isBefore(currentDate) || expirationDate.isEqual(currentDate)) {
                    Log.d("ExpirationDebug", "Item expirado: ${item.empProductName}")
                    expirationIndicator.visibility = View.VISIBLE
                } else {
                    Log.d("ExpirationDebug", "Item não expirado: ${item.empProductName}")
                    expirationIndicator.visibility = View.GONE
                }


                // Implemente a lógica do clique do item aqui
                itemView.setOnClickListener {
                    listener.onItemClick(adapterPosition)
                }
            }
        }





        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmpViewHolder {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.emp_list_item, parent, false)
            return EmpViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: EmpViewHolder, position: Int) {
            val currentItem = empList[position]
            holder.bind(currentItem) // Chame o método bind aqui para configurar o item
        }

        override fun getItemCount() = empList.size

        interface onItemClickListener {
            fun onItemClick(position: Int)
        }
    }




}