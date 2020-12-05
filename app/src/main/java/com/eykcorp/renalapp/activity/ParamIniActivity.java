package com.eykcorp.renalapp.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.eykcorp.renalapp.R;
import com.eykcorp.renalapp.com.eykcorp.renalapp.util.GenericActivity;
import com.eykcorp.renalapp.dto.MedicamentoDTO;
import com.eykcorp.renalapp.util.WsUtil;
import com.google.android.material.textfield.TextInputLayout;
import com.google.common.net.MediaType;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by galo.penaherrera on 08/12/2017.
 */

public class ParamIniActivity extends GenericActivity {

    private String TAG = "ConexionEquiActivity";
    private ProgressBar loading;
    private RequestQueue requestQueue;
    private AppCompatButton btnConectarEquipo;
    private TextView txvTituloPaciente;
    private String idTurno;
    private String idAtencionPaciente;
    private EditText txtAclaramiento;
    private EditText txtNaPlasmotico;
    private TextInputLayout txtInpAclaramiento;
    private TextInputLayout txtInpNaPlasmotico;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Bundle parametros = new Bundle();
        parametros.putInt("layoutActivity", R.layout.activity_param_ini);
        parametros.putString("mostrarSalir", "S");
        super.onCreate(parametros);

        Bundle bundle = getIntent().getExtras();

        loading = (ProgressBar) findViewById(R.id.loading);
        loading.setVisibility(View.GONE);

        txtAclaramiento = (EditText) findViewById(R.id.txtAclaramiento);
        txtNaPlasmotico = (EditText) findViewById(R.id.txtNaPlasmotico);
        txtInpAclaramiento = (TextInputLayout) findViewById(R.id.txtInpAclaramiento);
        txtInpNaPlasmotico = (TextInputLayout) findViewById(R.id.txtInpNaPlasmotico);
        txvTituloPaciente = (TextView) findViewById(R.id.txvTituloPaciente);
        btnConectarEquipo = (AppCompatButton) findViewById(R.id.btnConectarEquipo);

        if(bundle != null){
            txvTituloPaciente.setText(bundle.get("nombrePaciente").toString());
            idTurno = bundle.getString("idTurno");
            idAtencionPaciente = bundle.getString("idAtencionPaciente");
        }

        btnConectarEquipo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validarCampos()){
                    conectarEquipo();
                }
            }
        });

        txtInpNaPlasmotico.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length() < 1){
                    txtInpNaPlasmotico.setError("Ingresa Na Plasmático");
                    txtInpNaPlasmotico.setErrorEnabled(true);

                }

                if(charSequence.length() >  0){
                    txtInpNaPlasmotico.setError(null);
                    txtInpNaPlasmotico.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        txtInpAclaramiento.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length() < 1){
                    txtInpAclaramiento.setError("Ingresa Aclaramiento");
                    txtInpAclaramiento.setErrorEnabled(true);
                }

                if(charSequence.length() >  0){
                    txtInpAclaramiento.setError(null);
                    txtInpAclaramiento.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        obtenerParametrosIniciales();
    }

    private boolean validarCampos(){
        try{
            if(txtAclaramiento.getText().toString().equals("")){
                //Toast.makeText(this, "Codigo de usuario es requerido", Toast.LENGTH_LONG).show();
                txtInpAclaramiento.setErrorEnabled(true);
                txtInpAclaramiento.setError("Ingresa Aclaramiento");
                return false;
            }
            if(txtNaPlasmotico.getText().toString().equals("")){
                //Toast.makeText(this, "La clave es requerida", Toast.LENGTH_LONG).show();
                txtInpNaPlasmotico.setError("Ingresa Na Plasmático");
                txtInpNaPlasmotico.setErrorEnabled(true);
                return false;
            }
            return true;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    private void obtenerParametrosIniciales(){
        try{
            final String token = sesion.obtenerDatosSession().get("tokenWs").toString();
            ///pacientes/{idPaciente}/medicamentos/nuevos
            String url = this.getString(R.string.base_path) + "/v1/mobile/pacientes/"+idAtencionPaciente+"/datosconexion";
            Log.d(TAG, "URL: " + url);

            requestQueue= Volley.newRequestQueue(this);

            JsonObjectRequest jsonRequest = new JsonObjectRequest(
                    Request.Method.GET,
                    url,
                    null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                loading.setVisibility(View.GONE);
                                JSONObject jsonDat = response.getJSONObject("datosConexion");
                                txtAclaramiento.setText(jsonDat.getString("aclaramiento"));
                                txtNaPlasmotico.setText(jsonDat.getString("naPlasmatico"));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            try{
                                loading.setVisibility(View.GONE);
                                if(error.networkResponse.statusCode == 401){
                                    //Debe hacer login nuevamente y obtener un nuevo token
                                    String jsonError = new String(error.networkResponse.data);
                                    JSONObject jsonResponse = new JSONObject(jsonError);
                                    Integer codigoInterno = jsonResponse.getInt("codigo");

                                    if(codigoInterno.equals(30)){
                                        Log.d(TAG, "Bearer token expirado se solicita uno nuevo");
                                        solicitarToken();
                                    }
                                }
                                WsUtil.lanzarMensajeError(ParamIniActivity.this, error.networkResponse);
                            }catch(Exception e){
                                e.printStackTrace();
                            }
                        }
                    }

            ) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    return WsUtil.generarHeader(ParamIniActivity.this,null,null,token, MediaType.JSON_UTF_8);
                }

            };
            jsonRequest.setRetryPolicy(WsUtil.setTimeOut());

            requestQueue.add(jsonRequest);
            loading.setVisibility(View.VISIBLE);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void conectarEquipo(){
        try{
            final String token = sesion.obtenerDatosSession().get("tokenWs").toString();
            String url = this.getString(R.string.base_path) + "/v1/mobile/pacientes/"+idAtencionPaciente+"/maquina";
            Log.d(TAG, "URL: " + url);

            JSONObject objJson = new JSONObject();
            objJson.put("orden", "CON");
            objJson.put("codigoUsuario", sesion.obtenerDatosSession().get("codigoUsuario").toString());
            objJson.put("aclaramiento", Double.parseDouble(txtAclaramiento.getText().toString()));
            objJson.put("naPlasmatico", Double.parseDouble(txtNaPlasmotico.getText().toString()));
            objJson.put("kt", Double.parseDouble("0.0"));
            objJson.put("vst", Double.parseDouble("0.0"));
            objJson.put("tiempo", Integer.parseInt("0"));
            objJson.put("perdida", Double.parseDouble("0.0"));

            Log.d(TAG, objJson.toString());
            requestQueue= Volley.newRequestQueue(this);

            JsonObjectRequest jsonRequest = new JsonObjectRequest(
                    Request.Method.PUT,
                    url,
                    objJson,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                loading.setVisibility(View.GONE);
                                lanzarMensaje("Parámetros iniciales actualizados exitosamente");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            try{
                                loading.setVisibility(View.GONE);
                                if(error.networkResponse.statusCode == 401){
                                    //Debe hacer login nuevamente y obtener un nuevo token
                                    String jsonError = new String(error.networkResponse.data);
                                    JSONObject jsonResponse = new JSONObject(jsonError);
                                    Integer codigoInterno = jsonResponse.getInt("codigo");

                                    if(codigoInterno.equals(30)){
                                        Log.d(TAG, "Bearer token expirado se solicita uno nuevo");
                                        solicitarToken();
                                    }
                                }
                                WsUtil.lanzarMensajeError(ParamIniActivity.this, error.networkResponse);
                            }catch(Exception e){
                                e.printStackTrace();
                            }
                        }
                    }

            ) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    return WsUtil.generarHeader(ParamIniActivity.this,null,null,token, MediaType.JSON_UTF_8);
                }

            };
            jsonRequest.setRetryPolicy(WsUtil.setTimeOut());

            requestQueue.add(jsonRequest);
            loading.setVisibility(View.VISIBLE);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void lanzarMensaje(String mensaje){
        AlertDialog.Builder dialogo1 = new AlertDialog.Builder(ParamIniActivity.this);
        dialogo1.setTitle("Corprenal");
        dialogo1.setIcon(R.mipmap.ic_information);
        dialogo1.setMessage(mensaje);

        dialogo1.setCancelable(false);
        dialogo1.setNeutralButton("Aceptar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {
                dialogo1.dismiss();
                finish();
            }
        });
        dialogo1.show();
    }

    private void solicitarToken(){
        String url = this.getString(R.string.base_path) + "/v1/mobile/login";
        final String usuario = sesion.obtenerDatosSession().get("codigoUsuario").toString();
        final String clave = sesion.obtenerDatosSession().get("claveUsuario").toString();

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
                            loading.setVisibility(View.GONE);
                            String newToken = response.getString("token");
                            sesion.actualizarToken(newToken);
                            Log.d(TAG, "Se genero nuevo token");

                            conectarEquipo();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        try{
                            loading.setVisibility(View.GONE);
                            WsUtil.lanzarMensajeError(ParamIniActivity.this, error.networkResponse);
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                }

        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return WsUtil.generarHeader(ParamIniActivity.this,usuario,clave,null, MediaType.JSON_UTF_8);
            }

        };

        jsonRequest.setRetryPolicy(WsUtil.setTimeOut());
        requestQueue.add(jsonRequest);
        loading.setVisibility(View.VISIBLE);
    }
}

