package com.eykcorp.renalapp.activity;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.eykcorp.renalapp.R;
import com.eykcorp.renalapp.dto.ContratanteDTO;
import com.eykcorp.renalapp.dto.OpcionMenuDTO;
import com.eykcorp.renalapp.dto.SucursalDTO;
import com.eykcorp.renalapp.util.SessionManager;
import com.eykcorp.renalapp.util.WsUtil;
import com.google.android.material.textfield.TextInputLayout;
import com.google.common.net.MediaType;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private ProgressBar loadingLogin;

    private String TAG = "LoginActivity";
    private EditText txtUsuario;
    private EditText txtClave;
    private TextInputLayout txtInpUsuario;
    private TextInputLayout txtInpClave;
    private AppCompatButton btnIngresar;
    private RequestQueue requestQueue;
    private LinearLayout lyPaso1;
    private LinearLayout lyPaso2;

    private String secuenciaUsuario;
    private String nombreUsuario;
    private String claveUsuario;
    private String codigoUsuario;
    private String token;

    private AlertDialog.Builder dialogEmpresa;
    private AlertDialog.Builder dialogSucursal;

    private AppCompatButton btnEmpresa;
    private AppCompatButton btnSucursal;
    private AppCompatButton btnAceptarSucursal;

    private ContratanteDTO objEmpresaSel;
    private SucursalDTO objSucursalSel;

    private List<ContratanteDTO> lsContratantes;
    private ArrayAdapter<ContratanteDTO> adapterEmpresa;
    private ArrayAdapter<SucursalDTO> adapterSucursal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Bundle parametros = new Bundle();
        setContentView(R.layout.activity_login);
        //parametros.putInt("layoutActivity", R.layout.activity_login);
        //parametros.putString("mostrarSalir", "N");
        super.onCreate(savedInstanceState);

        loadingLogin = (ProgressBar) findViewById(R.id.loadingLogin);
        loadingLogin.setVisibility(View.GONE);

        txtUsuario = (EditText) findViewById(R.id.txtUsuario);
        txtClave = (EditText) findViewById(R.id.txtClave);
        txtInpUsuario = (TextInputLayout) findViewById(R.id.txtInpUsuario);
        txtInpClave = (TextInputLayout) findViewById(R.id.txtInpClave);
        btnIngresar = (AppCompatButton) findViewById(R.id.btnIngresar);
        btnEmpresa = (AppCompatButton) findViewById(R.id.btnEmpresa);
        btnSucursal = (AppCompatButton) findViewById(R.id.btnSucursal);
        btnAceptarSucursal = (AppCompatButton) findViewById(R.id.btnAceptarSucursal);
        lyPaso1 = (LinearLayout) findViewById(R.id.lyPaso1);
        lyPaso2 = (LinearLayout) findViewById(R.id.lyPaso2);

        //txtUsuario.setText("erik.nevarez@eykcorp.com");

        btnIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validarCampos()){
                    //Log.i("LoginActivity", "Datos correctos");
                    autenticar(txtUsuario.getText().toString(),txtClave.getText().toString());
                }
            }
        });

        btnAceptarSucursal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(objSucursalSel == null){
                    btnSucursal.setError("Seleccione una sucursal");
                }else{
                    String asignar = "N";
                    String conectar = "N";
                    String tomaSignos = "N";
                    String desconectar = "N";
                    String parametrosIniciales = "N";

                    for (OpcionMenuDTO objOpcion: objSucursalSel.getOpcionesDisponibles()) {
                        if(objOpcion.getCodigoOpcion().equals(17)){
                            asignar = objOpcion.getAcceso();
                        }
                        if(objOpcion.getCodigoOpcion().equals(18)){
                            conectar = objOpcion.getAcceso();
                        }
                        if(objOpcion.getCodigoOpcion().equals(19)){
                            tomaSignos = objOpcion.getAcceso();
                        }
                        if(objOpcion.getCodigoOpcion().equals(20)){
                            desconectar = objOpcion.getAcceso();
                        }
                        if(objOpcion.getCodigoOpcion().equals(27)){
                            parametrosIniciales = objOpcion.getAcceso();
                        }
                    }
                    SessionManager objSes = new SessionManager(LoginActivity.this);
                    objSes.crearSessionLogin(secuenciaUsuario, nombreUsuario, token, codigoUsuario, claveUsuario,
                            objEmpresaSel.getCodigoContratante(), objSucursalSel.getSecuenciaSucursal(),
                            asignar,conectar,tomaSignos,desconectar, objSucursalSel.getNombreSucursal(),
                            parametrosIniciales,"T");

                    finish();
                }
            }
        });

        lyPaso1.setVisibility(View.VISIBLE);
        lyPaso2.setVisibility(View.GONE);

        dialogEmpresa = new AlertDialog.Builder(this);
        dialogEmpresa.setIcon(R.mipmap.ic_dialogo);
        dialogEmpresa.setTitle("Seleccione una empresa");

        dialogSucursal = new AlertDialog.Builder(this);
        dialogSucursal.setIcon(R.mipmap.ic_dialogo);
        dialogSucursal.setTitle("Seleccione una sucursal");

        adapterEmpresa = new ArrayAdapter<ContratanteDTO>(this,android.R.layout.simple_list_item_1);
        adapterSucursal = new ArrayAdapter<SucursalDTO>(this,android.R.layout.simple_list_item_1);

        dialogEmpresa.setAdapter(adapterEmpresa, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                objEmpresaSel = adapterEmpresa.getItem(i);
                btnEmpresa.setText(objEmpresaSel.getNombreComercial());
                cargarSucursales(objEmpresaSel.getSucursales());
            }
        });

        dialogEmpresa.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        dialogSucursal.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });


        btnEmpresa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogEmpresa.show();
            }
        });


        btnSucursal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogSucursal.show();
            }
        });


        txtInpUsuario.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length() < 1){
                    txtInpUsuario.setErrorEnabled(true);
                    txtInpUsuario.setError("Ingresa tu usuario");
                }

                if(charSequence.length() >  0){
                    txtInpUsuario.setErrorEnabled(false);
                    txtInpUsuario.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        txtInpClave.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length() < 1){
                    txtInpClave.setErrorEnabled(true);
                    txtInpClave.setError("Ingresa tu clave");
                }

                if(charSequence.length() > 0){
                    txtInpClave.setErrorEnabled(false);
                    txtInpClave.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        //txtUsuario.setText("erik.nevarez@eykcorp.com");
        //txtClave.setText("Chamacoo1980");
    }

    private void cargarSucursales(List<SucursalDTO> sucursales){
        objSucursalSel = null;
        btnSucursal.setText("Seleccione sucursal");
        adapterSucursal.clear();
        adapterSucursal.addAll(sucursales);
        adapterSucursal.notifyDataSetChanged();
        dialogSucursal.setAdapter(adapterSucursal, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                btnSucursal.setError(null);
                objSucursalSel = adapterSucursal.getItem(i);
                btnSucursal.setText(objSucursalSel.getNombreSucursal());
            }
        });
    }

    private boolean validarCampos(){
        try{
            if(txtUsuario.getText().toString().equals("")){
                //Toast.makeText(this, "Codigo de usuario es requerido", Toast.LENGTH_LONG).show();
                txtInpUsuario.setErrorEnabled(true);
                txtInpUsuario.setError("Ingresa tu clave");
                return false;
            }
            if(txtClave.getText().toString().equals("")){
                //Toast.makeText(this, "La clave es requerida", Toast.LENGTH_LONG).show();
                txtInpClave.setError("Ingresa tu clave");
                txtInpClave.setErrorEnabled(true);
                return false;
            }
            return true;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    private void consultarContratantes(){
        try{
            String url = this.getString(R.string.base_path) + "/v1/mobile/"+secuenciaUsuario+"/contratantes?arg0="+codigoUsuario;
            Log.d("LoginActivity", "URL: " + url);

            requestQueue= Volley.newRequestQueue(this);
            JsonObjectRequest jsonRequest = new JsonObjectRequest(
                    Request.Method.GET,
                    url,
                    null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                //progressDialog.dismiss();
                                loadingLogin.setVisibility(View.GONE);

                                //Se decodifica el json de datos de usuario
                                JSONArray jsonContratantes = response.getJSONArray("contratantes");

                                List<SucursalDTO> lsSucursales;
                                lsContratantes = new ArrayList<>();
                                for (int i = 0; i < jsonContratantes.length(); i++){
                                    ContratanteDTO objContratante = new ContratanteDTO();
                                    objContratante.cargarEntidadconJson(jsonContratantes.optJSONObject(i));

                                    lsSucursales = new ArrayList<>();
                                    JSONArray jsonSucursales = jsonContratantes.getJSONObject(i).getJSONArray("sucursales");

                                    for (int j = 0; j < jsonSucursales.length(); j++){
                                        SucursalDTO objSucursal = new SucursalDTO();
                                        objSucursal.cargarEntidadconJson(jsonSucursales.getJSONObject(j));
                                        lsSucursales.add(objSucursal);


                                        List<OpcionMenuDTO> lsOpciones = new ArrayList<>();
                                        JSONArray jsonOpciones = jsonSucursales.getJSONObject(j).getJSONArray("opcionesAcceso");

                                        for(int k = 0; k < jsonOpciones.length(); k++){
                                            OpcionMenuDTO objOpcion = new OpcionMenuDTO();
                                            objOpcion.setCodigoOpcion(jsonOpciones.getJSONObject(k).getInt("codigoOpcion"));
                                            objOpcion.setAcceso(jsonOpciones.getJSONObject(k).getString("acceso"));
                                            //objOpcion.cargarEntidadconJson(jsonOpciones.getJSONObject(k));
                                            lsOpciones.add(objOpcion);
                                        }

                                        objSucursal.setOpcionesDisponibles(lsOpciones);
                                    }

                                    objContratante.setSucursales(lsSucursales);

                                    lyPaso2.setVisibility(View.VISIBLE);
                                    lyPaso1.setVisibility(View.GONE);

                                    lsContratantes.add(objContratante);
                                }

                                adapterEmpresa.clear();
                                adapterEmpresa.addAll(lsContratantes);
                                adapterEmpresa.notifyDataSetChanged();

                                if(lsContratantes.size() ==1 ){
                                    objEmpresaSel = lsContratantes.get(0);
                                    btnEmpresa.setText(objEmpresaSel.getNombreComercial());
                                    cargarSucursales(objEmpresaSel.getSucursales());
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            try{
                                loadingLogin.setVisibility(View.GONE);
                                WsUtil.lanzarMensajeError(LoginActivity.this, error.networkResponse);
                                //Log.d(TAG, jsonError);
                            }catch(Exception e){
                                e.printStackTrace();
                            }
                        }
                    }

            ) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    return WsUtil.generarHeader(LoginActivity.this,null,null,token, null);
                }

            };

            jsonRequest.setRetryPolicy(WsUtil.setTimeOut());

            requestQueue.add(jsonRequest);
            loadingLogin.setVisibility(View.VISIBLE);

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void autenticar(final String usuario, final String clave){
        try{
            String url = this.getString(R.string.base_path) + "/v1/mobile/login";
            Log.d("LoginActivity", "URL: " + url);

            JSONObject objJson = new JSONObject();

            requestQueue= Volley.newRequestQueue(this);
            JsonObjectRequest jsonRequest = new JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    objJson,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                //progressDialog.dismiss();
                                loadingLogin.setVisibility(View.GONE);
                                //Se decodifica el json de datos de usuario
                                JSONObject jsonUsuario= response.getJSONObject("usuario");
                                token = response.getString("token");
                                secuenciaUsuario = jsonUsuario.getString("secuenciaUsuario");
                                nombreUsuario = jsonUsuario.getString("nombreMostrarLogin");
                                codigoUsuario = usuario;
                                claveUsuario = clave;
                                consultarContratantes();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            try{
                                loadingLogin.setVisibility(View.GONE);
                                WsUtil.lanzarMensajeError(LoginActivity.this, error.networkResponse);
                            }catch(Exception e){
                                e.printStackTrace();
                            }
                        }
                    }
            ) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    return WsUtil.generarHeader(LoginActivity.this,usuario,clave,null, MediaType.JSON_UTF_8);
                }
            };

            jsonRequest.setRetryPolicy(WsUtil.setTimeOut());

            requestQueue.add(jsonRequest);
            loadingLogin.setVisibility(View.VISIBLE);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

}
