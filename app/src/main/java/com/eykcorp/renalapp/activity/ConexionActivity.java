package com.eykcorp.renalapp.activity;

import android.content.DialogInterface;
import android.os.Bundle;
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
import com.eykcorp.renalapp.util.WsUtil;
import com.google.android.material.textfield.TextInputLayout;
import com.google.common.net.MediaType;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by galo.penaherrera on 11/04/2018.
 */

public class ConexionActivity extends GenericActivity {

    private String TAG = "ConexionActivity";
    private ProgressBar loading;
    private RequestQueue requestQueue;

    private TextView txvTituloPaciente;
    private EditText txtObservacion;
    private TextInputLayout txtInpObservacion;
    private String idAtencionPaciente;
    private String idTurno;

    private AppCompatButton btnConectarEquipo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Bundle parametros = new Bundle();
        parametros.putInt("layoutActivity", R.layout.activity_conexion);
        parametros.putString("mostrarSalir", "S");
        super.onCreate(parametros);
        txvTituloPaciente = (TextView) findViewById(R.id.txvTituloPaciente);
        txtObservacion = (EditText) findViewById(R.id.txtObservacion);
        txtInpObservacion = (TextInputLayout) findViewById(R.id.txtInpObservacion);

        btnConectarEquipo = (AppCompatButton) findViewById(R.id.btnConectarEquipo);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            txvTituloPaciente.setText(bundle.get("nombrePaciente").toString());
            idTurno = bundle.getString("idTurno");
            idAtencionPaciente = bundle.getString("idAtencionPaciente");
        }

        loading = (ProgressBar) findViewById(R.id.loading);
        loading.setVisibility(View.GONE);

        btnConectarEquipo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if(validarCampos()){
                    conectarEquipo();
                //}
            }
        });
    }

    private boolean validarCampos(){
        if(txtObservacion.getText().toString().equals("")){
            txtInpObservacion.setErrorEnabled(true);
            txtInpObservacion.setError("Ingresa Observación");
            return false;
        }
        return true;
    }

    private void conectarEquipo(){
        try{
            final String token = sesion.obtenerDatosSession().get("tokenWs").toString();
            String url = this.getString(R.string.base_path) + "/v1/mobile/pacientes/"+idAtencionPaciente+"/maquina";
            Log.d(TAG, "URL: " + url);

            JSONObject objJson = new JSONObject();
            objJson.put("orden", "CON");
            objJson.put("codigoUsuario", sesion.obtenerDatosSession().get("codigoUsuario").toString());
            objJson.put("aclaramiento", null);
            objJson.put("naPlasmatico", null);
            objJson.put("kt", Double.parseDouble("0.0"));
            objJson.put("vst", Double.parseDouble("0.0"));
            objJson.put("tiempo", Integer.parseInt("0"));
            objJson.put("perdida", Double.parseDouble("0.0"));
            objJson.put("observacion", txtObservacion.getText());

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
                                lanzarMensaje("Conexión de máquina exitosa");
                                //((PacientesActivity)getContext()).consultarPacientesDia();
                                //dismiss();
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
                                        //solicitarToken();
                                    }
                                }
                                WsUtil.lanzarMensajeError(ConexionActivity.this, error.networkResponse);
                            }catch(Exception e){
                                e.printStackTrace();
                            }
                        }
                    }

            ) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    return WsUtil.generarHeader(ConexionActivity.this,null,null,token, MediaType.JSON_UTF_8);
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
        AlertDialog.Builder dialogo1 = new AlertDialog.Builder(ConexionActivity.this);
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
}
