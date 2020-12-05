package com.eykcorp.renalapp.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import com.eykcorp.renalapp.adaptador.SignosAdapter;
import com.eykcorp.renalapp.com.eykcorp.renalapp.util.GenericActivity;
import com.eykcorp.renalapp.dto.SignosVitalesDTO;
import com.eykcorp.renalapp.util.WsUtil;
import com.google.android.material.textfield.TextInputLayout;
import com.google.common.net.MediaType;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by Galo on 09/12/2017.
 */

public class DesconexionActivity extends GenericActivity{

    private String TAG = "DesconexionActivity";
    private ProgressBar loading;
    private RequestQueue requestQueue;

    private TextView txvTituloPaciente;
    private EditText txtKt;
    private EditText txtKtv;
    private EditText txtVst;
    private EditText txtPerdida;
    private EditText txtTiempo;
    private EditText txtNaPlasmotico;
    private TextInputLayout txtInpKt;
    private TextInputLayout txtInpKtv;
    private TextInputLayout txtInpVst;
    private TextInputLayout txtInpPerdida;
    private TextInputLayout txtInpTiempo;
    private TextInputLayout txtInpNaPlasmotico;

    private String idTurno;
    private String idAtencionPaciente;

    private String horas;
    private String minutos;

    private AppCompatButton btnDesconectarEquipo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Bundle parametros = new Bundle();
        parametros.putInt("layoutActivity", R.layout.activity_desconexion);
        parametros.putString("mostrarSalir", "S");
        super.onCreate(parametros);
        txvTituloPaciente = (TextView) findViewById(R.id.txvTituloPaciente);
        txtTiempo = (EditText) findViewById(R.id.txtTiempo);
        txtKt = (EditText) findViewById(R.id.txtKt);
        txtKtv = (EditText) findViewById(R.id.txtKtv);
        txtVst = (EditText) findViewById(R.id.txtVst);
        //txtPerdida = (EditText) findViewById(R.id.txtPerdida);
        txtNaPlasmotico = (EditText) findViewById(R.id.txtNaPlasmotico);

        txtTiempo.setKeyListener(null);
        txtInpTiempo = (TextInputLayout) findViewById(R.id.txtInpTiempo);
        txtInpKt = (TextInputLayout) findViewById(R.id.txtInpKt);
        txtInpKtv = (TextInputLayout) findViewById(R.id.txtInpKtv);
        txtInpVst = (TextInputLayout) findViewById(R.id.txtInpVst);
        //txtInpPerdida = (TextInputLayout) findViewById(R.id.txtInpPerdida);
        txtInpNaPlasmotico = (TextInputLayout) findViewById(R.id.txtInpNaPlasmotico);

        btnDesconectarEquipo = (AppCompatButton) findViewById(R.id.btnDesconectarEquipo);
        Bundle bundle = getIntent().getExtras();

        if(bundle != null){
            txvTituloPaciente.setText(bundle.get("nombrePaciente").toString());
            idTurno = bundle.getString("idTurno");
            idAtencionPaciente = bundle.getString("idAtencionPaciente");
        }

        loading = (ProgressBar) findViewById(R.id.loading);
        loading.setVisibility(View.GONE);


        txtTiempo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                levantarDuracion();
            }
        });

        btnDesconectarEquipo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validarCampos()){
                    validarAlerta();
                    //desconectar();
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
                    txtInpNaPlasmotico.setError("Ingresa Na Plasmótico");
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

        txtInpKt.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length() < 1){
                    txtInpKt.setError("Ingresa KT");
                    txtInpKt.setErrorEnabled(true);

                }

                if(charSequence.length() >  0){
                    txtInpKt.setError(null);
                    txtInpKt.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        txtInpVst.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length() < 1){
                    txtInpVst.setError("Ingresa VST");
                    txtInpVst.setErrorEnabled(true);
                }

                if(charSequence.length() >  0){
                    txtInpVst.setError(null);
                    txtInpVst.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        horas = "0";
        minutos = "0";

        txtTiempo.setText(horas+" horas : "+minutos+" minutos");

        /*txtInpPerdida.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length() < 1){
                    txtInpPerdida.setError("Ingresa Perdida");
                    txtInpPerdida.setErrorEnabled(true);
                }

                if(charSequence.length() >  0){
                    txtInpPerdida.setError(null);
                    txtInpPerdida.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });*/

    }

    private void levantarDuracion(){

        LayoutInflater inflater = this.getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.layout_tiempo, null);
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Tiempo (Horas:Minutos)");

        final NumberPicker npInicio = (NumberPicker) alertLayout.findViewById(R.id.npHora);
        npInicio.setMaxValue(10);
        npInicio.setMinValue(0);
        npInicio.setWrapSelectorWheel(false);

        EditText numberPickerChild = (EditText) npInicio.getChildAt(0);
        numberPickerChild.setFocusable(false);
        numberPickerChild.setInputType(InputType.TYPE_NULL);

        if (horas != null){
            npInicio.setValue(Integer.parseInt(horas));
        }

        final NumberPicker npFin = (NumberPicker) alertLayout.findViewById(R.id.npMinutos);
        npFin.setMaxValue(59);
        npFin.setMinValue(0);
        npFin.setWrapSelectorWheel(false);

        EditText numberPickerChild2 = (EditText) npFin.getChildAt(0);
        numberPickerChild2.setFocusable(false);
        numberPickerChild2.setInputType(InputType.TYPE_NULL);

        if (minutos != null){
            npFin.setValue(Integer.parseInt(minutos));
        }
        alert.setView(alertLayout);

        alert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                horas = String.valueOf(npInicio.getValue());
                minutos = String.valueOf(npFin.getValue());
                txtTiempo.setText(horas+" horas : "+minutos+" minutos ");
                dialogInterface.dismiss();
            }
        });


        AlertDialog dialog = alert.create();
        dialog.show();
    }

    public void eliminarRegistro(String alerta){
        AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
        dialogo1.setTitle("Corprenal");
        dialogo1.setIcon(R.mipmap.ic_information);
        dialogo1.setMessage(alerta);

        dialogo1.setCancelable(true);
        dialogo1.setPositiveButton("Continuar de todas formas", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                desconectar();
            }
        });

        dialogo1.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        dialogo1.show();
    }

    private void validarAlerta(){
        try{
            final String token = sesion.obtenerDatosSession().get("tokenWs").toString();
            String url = this.getString(R.string.base_path) + "/v1/mobile/pacientes/"+idAtencionPaciente+"/alertadesconexion";
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
                                String msj = response.getString("alerta");

                                if(!msj.equals("null")){
                                    eliminarRegistro(msj);
                                }else{
                                    desconectar();
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
                                loading.setVisibility(View.GONE);

                                //Si el token expiro, se solicita uno nuevamente
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

                                WsUtil.lanzarMensajeError(DesconexionActivity.this, error.networkResponse);
                            }catch(Exception e){
                                e.printStackTrace();
                            }
                        }
                    }

            ) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    return WsUtil.generarHeader(DesconexionActivity.this,null,null,token, null);
                }

            };

            jsonRequest.setRetryPolicy(WsUtil.setTimeOut());

            requestQueue.add(jsonRequest);
            loading.setVisibility(View.VISIBLE);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void desconectar(){
        try{
            final String token = sesion.obtenerDatosSession().get("tokenWs").toString();
            String url = this.getString(R.string.base_path) + "/v2/mobile/pacientes/"+idAtencionPaciente+"/maquina";
            Log.d(TAG, "URL: " + url);

            JSONObject objJson = new JSONObject();
            objJson.put("orden", "DES");
            objJson.put("codigoUsuario", sesion.obtenerDatosSession().get("codigoUsuario").toString());
            objJson.put("aclaramiento", Double.parseDouble("0"));
            objJson.put("naPlasmatico", Double.parseDouble(txtNaPlasmotico.getText().toString()));
            objJson.put("kt", Double.parseDouble(txtKt.getText().toString()));
            objJson.put("vst", Double.parseDouble(txtVst.getText().toString()));
            Integer tiempoFinal = (Integer.parseInt(horas) * 60) + Integer.parseInt(minutos);
            objJson.put("tiempo", tiempoFinal);
            objJson.put("perdida", JSONObject.NULL);
            objJson.put("ktv", Double.parseDouble(txtKtv.getText().toString()));

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
                                lanzarMensaje("Desconexion de máquina exitosa");
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
                                WsUtil.lanzarMensajeError(DesconexionActivity.this, error.networkResponse);
                            }catch(Exception e){
                                e.printStackTrace();
                            }
                        }
                    }

            ) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    return WsUtil.generarHeader(DesconexionActivity.this,null,null,token, MediaType.JSON_UTF_8);
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
        AlertDialog.Builder dialogo1 = new AlertDialog.Builder(DesconexionActivity.this);
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

    private boolean validarCampos(){
        if(txtKt.getText().toString().equals("")){
            txtInpKt.setErrorEnabled(true);
            txtInpKt.setError("Ingresa KT");
            txtKt.requestFocus();
            return false;
        }
        if(txtVst.getText().toString().equals("")){
            txtInpVst.setErrorEnabled(true);
            txtInpVst.setError("Ingresa VST");
            txtVst.requestFocus();
            return false;
        }
        if(txtNaPlasmotico.getText().toString().equals("")){
            txtInpNaPlasmotico.setErrorEnabled(true);
            txtInpNaPlasmotico.setError("Ingresa Na Plasmático");
            txtNaPlasmotico.requestFocus();
            return false;
        }

        /*if(txtPerdida.getText().toString().equals("")){
            txtInpPerdida.setErrorEnabled(true);
            txtInpPerdida.setError("Ingresa Perdida");
            txtPerdida.requestFocus();
            return false;
        }*/

        if(horas.equals("0") && minutos.equals("0")){
            txtInpTiempo.setErrorEnabled(true);
            txtInpTiempo.setError("Ingresa Tiempo");
            txtTiempo.requestFocus();
            return false;
        }
        return true;
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

                            desconectar();
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
                            WsUtil.lanzarMensajeError(DesconexionActivity.this, error.networkResponse);
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                }

        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return WsUtil.generarHeader(DesconexionActivity.this,usuario,clave,null, MediaType.JSON_UTF_8);
            }

        };

        jsonRequest.setRetryPolicy(WsUtil.setTimeOut());
        requestQueue.add(jsonRequest);
        loading.setVisibility(View.VISIBLE);
    }
}
