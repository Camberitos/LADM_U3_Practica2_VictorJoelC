package mx.edu.ittepic.ladm_u3_practica2_victorjoelc

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main2.*

class Main2Activity : AppCompatActivity() {
    var id = ""
    var baseDatos = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        var extras = intent.extras
        id = extras!!.getString("id")!!
        nombre2.setText(extras.getString("nombre"))
        domicilio2.setText(extras.getString("domicilio"))
        telefono2.setText(extras.getString("telefono"))
        descripcion2.setText(extras.getString("descripcion"))
        precio2.setText(extras.getDouble("precio").toString())
        cantida2.setText(extras.getLong("cantidad").toString())
        if(extras.getBoolean("entregado") == false){
            entregado2.isChecked = false
        }

        actualizar.setOnClickListener(){
            baseDatos.collection("restaurante")
                .document(id)
                .update("nombre",nombre2.text.toString(),
                    "domicilio",domicilio2.text.toString(),
                    "telefono",telefono2.text.toString(),
                    "pedido.descripcion",descripcion2.text.toString(),
                    "pedido.precio",precio2.text.toString().toFloat(),
                    "pedido.cantidad",cantida2.text.toString().toInt(),
                    "pedido.entregado",entregado2.isChecked)
                .addOnSuccessListener {
                    Toast.makeText(this,"UPDATED", Toast.LENGTH_LONG)
                        .show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this,"FAILED CONNECTION TO INTERNET", Toast.LENGTH_LONG)
                        .show()
                }
        }

        regresar.setOnClickListener(){
            finish()
        }
    }
}
