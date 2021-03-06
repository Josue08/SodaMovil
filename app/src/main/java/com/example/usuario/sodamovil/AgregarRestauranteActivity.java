package com.example.usuario.sodamovil;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.usuario.sodamovil.BaseDeDatos.DataBase;
import com.example.usuario.sodamovil.Entidades.Horario;
import com.example.usuario.sodamovil.Entidades.Restaurante;
import com.example.usuario.sodamovil.Entidades.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class AgregarRestauranteActivity extends AppCompatActivity {

    EditText nombre_restaurante;
    EditText descripcion_restaurante;
    static int  HORARIO_REQUEST = 1;
    static int  UBICACION_REQUEST = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_restaurante);

        // alambramos el boton

        Button MiBoton = (Button) findViewById(R.id.irAMapaRestaurante);

        Button MiBoton2 = (Button) findViewById(R.id.irAhorarioAgregar);
        Button AgregarRestaurante = (Button) findViewById(R.id.btnAgregarRestaurante);


        nombre_restaurante = (EditText) findViewById(R.id.nombreReId);
        descripcion_restaurante = (EditText) findViewById(R.id.descripReId);
        //Programamos el evento onclick

        MiBoton.setOnClickListener(new View.OnClickListener(){

            @Override

            public void onClick(View arg0) {
                Intent intento = new Intent(getApplicationContext(), UbicacionRestauranteActivity.class);
                startActivityForResult(intento,UBICACION_REQUEST );
            }

        });

        MiBoton2.setOnClickListener(new View.OnClickListener(){

            @Override

            public void onClick(View arg0) {
                Intent intento = new Intent(getApplicationContext(), AgregarHorarioRestauranteActivity.class);
                startActivityForResult(intento,HORARIO_REQUEST );
            }

        });



        AgregarRestaurante.setOnClickListener(new View.OnClickListener(){

            @Override

            public void onClick(View arg0) {
                AgregarRestaurante();
            }

        });
        getSupportActionBar().setTitle("Agregar Restaurante");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }




    private void AgregarRestaurante(){
        FirebaseAuth firebaseAuth;
        firebaseAuth= FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        Horario horario;
        final String nombre=nombre_restaurante.getText().toString();
        String descripcion= descripcion_restaurante.getText().toString();

        double latitud= VariablesGlobales.getInstance().posicionAgregarRestaurante.latitude;
        double longitud= VariablesGlobales.getInstance().posicionAgregarRestaurante.longitude;
        if(VariablesGlobales.getInstance().getHorario()!=null){
            horario = VariablesGlobales.getInstance().getHorario();
        }
        else{
            horario= new Horario();
        }

        final Restaurante restaurante = new Restaurante();
        restaurante.setNombre(nombre);
        restaurante.setDescripcion(descripcion);
        restaurante.setHorario(horario);
        restaurante.setLatitudesH(latitud);
        restaurante.setLatitudesV(longitud);

        final DataBase db= DataBase.getInstance();
        Query query= db.getmDatabaseReference().child("Usuario").orderByChild("correo").equalTo(user.getEmail());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChildren()) {
                    for(DataSnapshot postSnapshot:dataSnapshot.getChildren()){
                        Usuario user = postSnapshot.getValue(Usuario.class);
                        Mensaje("Restaurante agregado exitosamente");
                        db.agregarRestaurante(restaurante,user.getIdFirebase());
                        db.actualizarRestaurantesUsuario(restaurante,user);
                        limpiaForm();
                        Intent intento = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intento);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Mensaje("No habia nada!");
            }
        });
    }

    public void Mensaje(String msg){
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();};

    public void limpiaForm(){
        nombre_restaurante.setText("");
        descripcion_restaurante.setText("");
        VariablesGlobales.getInstance().posicionAgregarRestaurante=null;
    }



}
