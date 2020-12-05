package com.eykcorp.renalapp.com.eykcorp.renalapp.util;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.eykcorp.renalapp.R;
import com.eykcorp.renalapp.activity.LoginActivity;
import com.eykcorp.renalapp.activity.TurnosActivity;
import com.eykcorp.renalapp.util.SessionManager;

/**
 * Created by galo.penaherrera on 23/11/2017.
 */

public class GenericActivity extends AppCompatActivity {

    private String TAG = "GenericActivity";
    protected Toolbar toolbar;
    protected ImageButton imgButSalir;
    protected ImageButton imgButReporte;
    protected SessionManager sesion;
    protected TextView txvUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(savedInstanceState.getInt("layoutActivity"));
        sesion = new SessionManager(this);
        sesion.obtenerDatosSession();

        txvUser = (TextView) findViewById(R.id.txvUser);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        imgButSalir = (ImageButton) findViewById(R.id.imgButSalir);
        imgButReporte = (ImageButton) findViewById(R.id.imgButReporte);
        imgButReporte.setVisibility(View.GONE);

        if(savedInstanceState.getString("mostrarSalir") != null){
            if(savedInstanceState.getString("mostrarSalir").equals("S")){
                imgButSalir.setVisibility(View.VISIBLE);
            }else{
                imgButSalir.setVisibility(View.GONE);
            }
        }else{
            imgButSalir.setVisibility(View.GONE);
        }

        if(sesion.isLogin()){
            txvUser.setVisibility(View.VISIBLE);
            if(sesion.obtenerDatosSession().get("nombreSucursal") != null){
                txvUser.setText(sesion.obtenerDatosSession().get("nombreUsuario").toString()+" - "+sesion.obtenerDatosSession().get("nombreSucursal").toString());
            }else{
                txvUser.setText(sesion.obtenerDatosSession().get("nombreUsuario").toString());
            }
            txvUser.setVisibility(View.VISIBLE);
        }else{
            //Si no ha iniciado sesion, se debe llamar el login
            txvUser.setVisibility(View.GONE);
        }

        imgButSalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(GenericActivity.this,"Cerrar sesion",Toast.LENGTH_LONG).show();
                cerrarSesion();
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();

        if(!sesion.isLogin()){
            txvUser.setVisibility(View.GONE);
            Intent i;
            i = new Intent(this, LoginActivity.class);
            startActivity(i);
        }else{
            txvUser.setVisibility(View.VISIBLE);
            if(sesion.obtenerDatosSession().get("nombreSucursal") != null){
                txvUser.setText(sesion.obtenerDatosSession().get("nombreUsuario").toString()+" - "+sesion.obtenerDatosSession().get("nombreSucursal").toString());
            }else{
                txvUser.setText(sesion.obtenerDatosSession().get("nombreUsuario").toString());
            }
            txvUser.setVisibility(View.VISIBLE);
        }
    }

    private void cerrarSesion(){
        new AlertDialog.Builder(this)
                .setIcon(R.mipmap.ic_exit_to_app_black_36dp)
                .setTitle("CORPRENAL")
                .setMessage("Salir?")
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,int which) {
                                sesion.logout();
                                finish();
                                Intent i;
                                i = new Intent(GenericActivity.this, TurnosActivity.class);
                                //Se limpia el hostorial de activities
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(i);
                            }
                        }).show();

    }

}
