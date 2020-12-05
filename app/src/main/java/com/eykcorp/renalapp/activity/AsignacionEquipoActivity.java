package com.eykcorp.renalapp.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
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
import com.eykcorp.renalapp.dto.EquipoDTO;
import com.eykcorp.renalapp.dto.PacienteDTO;
import com.eykcorp.renalapp.util.WsUtil;
import com.google.common.net.MediaType;

import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Galo on 02/12/2017.
 */

public class AsignacionEquipoActivity extends GenericActivity {

    private String TAG = "AsignacionEquiActivity";
    private AlertDialog.Builder dialogEquipo;
    private ArrayAdapter<EquipoDTO> adapterEquipos;
    private AppCompatButton btnEquipos;
    private AppCompatButton btnAsignarEquipo;
    private EquipoDTO objEquipo;
    private List<EquipoDTO> maquinas;
    private TextView txvTituloPaciente;
    private String idTurno;
    private String idAtencionPaciente;
    private ProgressBar loading;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Bundle parametros = new Bundle();
        parametros.putInt("layoutActivity", R.layout.activity_asignacion_equipo);
        parametros.putString("mostrarSalir", "S");
        super.onCreate(parametros);

        Bundle bundle = getIntent().getExtras();

        loading = (ProgressBar) findViewById(R.id.loadingLogin);
        loading.setVisibility(View.GONE);

        maquinas = new ArrayList<>();
        txvTituloPaciente = (TextView) findViewById(R.id.txvTituloPaciente);

        if(bundle != null){
            txvTituloPaciente.setText(bundle.get("nombrePaciente").toString());
            idTurno = bundle.getString("idTurno");
            idAtencionPaciente = bundle.getString("idAtencionPaciente");
        }

        dialogEquipo = new AlertDialog.Builder(AsignacionEquipoActivity.this);
        dialogEquipo.setIcon(R.mipmap.ic_dialogo);
        dialogEquipo.setTitle("Maquinas disponibles");

        adapterEquipos = new ArrayAdapter<EquipoDTO>(this,android.R.layout.simple_list_item_1);

        btnEquipos = (AppCompatButton) findViewById(R.id.btnEquipos);
        btnAsignarEquipo = (AppCompatButton) findViewById(R.id.btnAsignarEquipo);

        dialogEquipo.setAdapter(adapterEquipos, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                objEquipo = (EquipoDTO) adapterEquipos.getItem(i);
                btnEquipos.setText(objEquipo.getDescripcionEquipo());
            }
        });

        dialogEquipo.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        btnEquipos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                consultarEquipos();
            }
        });

        btnAsignarEquipo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(objEquipo == null){
                    btnEquipos.setError("Debe seleccionar un equipo");
                }else{
                    asignarEquipo();
                }
            }
        });
    }

    private void asignarEquipo(){
        try{
            final String token = sesion.obtenerDatosSession().get("tokenWs").toString();
            String url = this.getString(R.string.base_path) + "/v1/mobile/pacientes/maquina";
            Log.d(TAG, "URL: " + url);


            HashMap<String, String> params = new HashMap<String, String>();
            params.put("idMaquina", objEquipo.getIdEquipo());
            params.put("idPaciente", idAtencionPaciente);
            params.put("codigoUsuario", sesion.obtenerDatosSession().get("codigoUsuario").toString());

            //Log.d(TAG, params.toString());
            requestQueue= Volley.newRequestQueue(this);
            JsonObjectRequest jsonRequest = new JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    new JSONObject(params),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                loading.setVisibility(View.GONE);
                                lanzarMensaje("Asignación de máquina exitosa");
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
                                        solicitarToken(false,true);
                                    }
                                }
                                WsUtil.lanzarMensajeError(AsignacionEquipoActivity.this, error.networkResponse);
                            }catch(Exception e){
                                e.printStackTrace();
                            }
                        }
                    }

            ) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    return WsUtil.generarHeader(AsignacionEquipoActivity.this,null,null,token, MediaType.JSON_UTF_8);
                }

            };
            jsonRequest.setRetryPolicy(WsUtil.setTimeOut());

            requestQueue.add(jsonRequest);
            loading.setVisibility(View.VISIBLE);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void consultarEquipos(){
        try{
            final String token = sesion.obtenerDatosSession().get("tokenWs").toString();
            String url = this.getString(R.string.base_path) + "/v1/mobile/turnos/"+idTurno+"/maquinas/disponibles";
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
                                //Se decodifica el json de datos de turnos
                                JSONArray jsonEquipos = response.getJSONArray("maquinasDisponibles");
                                adapterEquipos.clear();

                                for (int i = 0; i < jsonEquipos.length(); i++){
                                    adapterEquipos.add(new EquipoDTO(jsonEquipos.optJSONObject(i).getString("idMaquina"),
                                            jsonEquipos.optJSONObject(i).getString("nombreMaquina")));
                                }

                                adapterEquipos.notifyDataSetChanged();
                                dialogEquipo.show();
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
                                        solicitarToken(true,false);
                                    }
                                }
                                WsUtil.lanzarMensajeError(AsignacionEquipoActivity.this, error.networkResponse);
                            }catch(Exception e){
                                e.printStackTrace();
                            }
                        }
                    }

            ) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    return WsUtil.generarHeader(AsignacionEquipoActivity.this,null,null,token, null);
                }

            };

            jsonRequest.setRetryPolicy(WsUtil.setTimeOut());

            requestQueue.add(jsonRequest);
            loading.setVisibility(View.VISIBLE);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void lanzarMensaje(String mensaje){
        AlertDialog.Builder dialogo1 = new AlertDialog.Builder(AsignacionEquipoActivity.this);
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

    private void solicitarToken(final boolean consultarEquipo,final boolean asignarEquipo){
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

                            if(consultarEquipo){
                                consultarEquipos();
                            }

                            if(asignarEquipo){
                                asignarEquipo();
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
                            WsUtil.lanzarMensajeError(AsignacionEquipoActivity.this, error.networkResponse);
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                }

        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return WsUtil.generarHeader(AsignacionEquipoActivity.this,usuario,clave,null, MediaType.JSON_UTF_8);
            }

        };

        jsonRequest.setRetryPolicy(WsUtil.setTimeOut());
        requestQueue.add(jsonRequest);
        loading.setVisibility(View.VISIBLE);
    }


}
