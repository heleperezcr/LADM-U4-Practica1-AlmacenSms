package com.example.ladm_u4_practica1_almacensms

import android.R
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import com.example.ladm_u4_practica1_almacensms.databinding.ActivityMainBinding
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    var listaIds = ArrayList<String>()
    var idActualizar =""

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mostrar()


        //Enviar mensaje
        binding.enviar.setOnClickListener {
            val dateTime = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("MMM dd yyyy, hh:mm:ss a"))

            var datos = hashMapOf(
                "fecha" to dateTime.toString(),
                "mensaje" to binding.mensaje.text.toString(),
                "telefono" to binding.telefono.text.toString(),
                "registrado" to Date()
            )

            //Crear la tabla personas                                         //agregar
            FirebaseFirestore.getInstance().collection("smsenviados").add(datos)
                .addOnSuccessListener {
                    //Si y solo si se pudo realizar la transaccion en la nube
                    toas("Mensaje enviado")
                    binding.telefono.setText("")
                    binding.mensaje.setText("")
                }
                .addOnFailureListener {
                    //Si y solo si NO se pudo realizar la transaccion en la nube
                    aler(it.message!!)
                }

        }


    }


    private fun mostrar() {
        FirebaseFirestore.getInstance().collection("smsenviados")
            .addSnapshotListener { value, error ->
                if(error!=null){
                    aler("NO SE PUDO REALIZAR LA CONSULTA")
                    return@addSnapshotListener
                }//if

                var lista = ArrayList<String>()
                listaIds.clear()
                for(documento in value!!){
                    val cadena = "Enviado a: "+documento.getString("telefono")+"\n"+
                            "Mensaje: "+documento.get("mensaje")+"\n"+
                            "Fecha envio: "+documento.getString("fecha");
                    lista.add(cadena)

                    //Recuperar los id
                    listaIds.add(documento.id)
                }//for

                binding.mostrar.adapter = ArrayAdapter<String>(this,
                    R.layout.simple_list_item_1, lista)

            }//firebase
    }//mostrar


    fun toas(m:String){
        Toast.makeText(this, m, Toast.LENGTH_LONG).show()
    }

    fun aler(m:String){
        AlertDialog.Builder(this).setTitle("ATENCION")
            .setMessage(m)
            .setPositiveButton("OK"){d, i->}
            .show()
    }

}