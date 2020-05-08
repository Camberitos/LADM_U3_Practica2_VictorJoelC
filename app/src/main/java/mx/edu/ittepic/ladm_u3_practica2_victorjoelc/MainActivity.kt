package mx.edu.ittepic.ladm_u3_practica2_victorjoelc

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    var baseRemota = FirebaseFirestore.getInstance()
    var dataLista = ArrayList<String>()
    var listaID = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        salir.setOnClickListener(){
            finish()
        }

        insertar.setOnClickListener() {
            insert()
        }

        baseRemota.collection("restaurante")
                .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    if(firebaseFirestoreException != null){
                        //SI ES DIFERENTE DE NULL ENTONCES SI HAY ERROR
                        Toast.makeText(this,"NOT CONSULTED",Toast.LENGTH_LONG)
                                .show()
                        return@addSnapshotListener //ASÍ EVITAMOS EL ELSE
                    }
                    dataLista.clear()
                    listaID.clear()
                    for(document in querySnapshot!!){
                        var cadena = document.getString("nombre")+"\n"+
                                document.getString("domicilio")+"\n"+
                                document.getString("telefono")+"\n"+
                                document.get("pedido.descripcion")+"\n"+
                                document.get("pedido.precio")+"\n"+
                                document.get("pedido.cantidad")+"\n"+
                                document.get("pedido.entregado")
                        dataLista.add(cadena)
                        listaID.add(document.id)
                    }
                    if(dataLista.size==0){
                        dataLista.add("NO DATA")
                    }

                    var adaptador = ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,dataLista)
                    lista.adapter=adaptador
                }

        lista.setOnItemClickListener { parent, view, position, id ->
            if(listaID.size==0){
                return@setOnItemClickListener //SE DEJA DE EJECUTAR EL setOnItemClickListener
            }
            AlertaEliminarActualizar(position)
        }

    }
    private fun insert(){
        var datosInsertar = hashMapOf(
                "nombre" to nombre.text.toString(),
                "domicilio" to domicilio.text.toString(),
                "telefono" to telefono.text.toString(),
                "pedido" to hashMapOf(
                        "descripcion" to descripcion.text.toString(),
                        "precio" to precio.text.toString().toFloat(),
                        "cantidad" to cantidad.text.toString().toInt(),
                        "entregado" to entregado.isChecked
                )

        )
        baseRemota.collection("restaurante")
                .add(datosInsertar as Any)
                .addOnSuccessListener {
                    Toast.makeText(this,"INSERTED", Toast.LENGTH_LONG)
                            .show()
                }
                .addOnFailureListener {
                    Toast.makeText(this,"NOT INSERTED", Toast.LENGTH_LONG)
                                .show()
                    }
            limpiarCampos()
        }
    private fun AlertaEliminarActualizar(position:Int){
        AlertDialog.Builder(this)
            .setTitle("ATENTION")
            .setMessage("What do you want to do with \n ${dataLista[position]}?")
            .setPositiveButton("DELETE"){d,w->
                eliminar(listaID[position])
            }
            .setNegativeButton("UPDATE"){d,w->
                llamarVentanaActualizar(listaID[position])
            }
            .setNeutralButton("CANCEL"){dialog,which->}
            .show()
    }
    private fun eliminar(idEliminar:String){
        baseRemota.collection("restaurante")
            .document(idEliminar)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this,"DELETED",Toast.LENGTH_LONG)
                    .show()
            }
            .addOnFailureListener {
                Toast.makeText(this,"NOT DELETED",Toast.LENGTH_LONG)
                    .show()
            }
    }
    private fun llamarVentanaActualizar(idActualzar:String){
        baseRemota.collection("restaurante")
            .document(idActualzar)
            .get()
            .addOnSuccessListener {
                var v = Intent(this,Main2Activity::class.java)

                v.putExtra("id",idActualzar)
                v.putExtra("nombre",it.getString("nombre"))
                v.putExtra("domicilio",it.getString("domicilio"))
                v.putExtra("telefono",it.getString("telefono"))
                v.putExtra("descripcion",it.getString("pedido.descripcion"))
                v.putExtra("precio",it.getDouble("pedido.precio"))
                v.putExtra("cantidad",it.getLong("pedido.cantidad"))
                v.putExtra("entregado",it.getBoolean("pedido.entregado"))

                startActivity(v)

            }
            .addOnFailureListener {
                Toast.makeText(this,"FAILED CONNECTION TO INTERNET",Toast.LENGTH_LONG)
                    .show()
            }
    }
    private fun limpiarCampos(){
        nombre.setText("")
        domicilio.setText("")
        telefono.setText("")
        descripcion.setText("")
        precio.setText("")
        cantidad.setText("")
    }
}
