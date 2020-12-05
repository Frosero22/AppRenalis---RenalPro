package com.eykcorp.renalapp.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.eykcorp.renalapp.R;
import com.eykcorp.renalapp.adaptador.TurnosAdapter;
import com.eykcorp.renalapp.com.eykcorp.renalapp.util.GenericActivity;
import com.eykcorp.renalapp.dto.TurnosDTO;
import com.android.volley.Response;
import com.eykcorp.renalapp.util.SessionManager;
import com.eykcorp.renalapp.util.WsUtil;
import com.google.common.net.MediaType;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by galo.penaherrera on 23/11/2017.
 */

public class TurnosActivity extends GenericActivity {

    private String TAG = "TurnosActivity";
    private ProgressBar loading;

    private RecyclerView.Adapter adapter;
    private List<TurnosDTO> lsTurnos;
    private RecyclerView recTurnos;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Bundle parametros = new Bundle();
        parametros.putInt("layoutActivity", R.layout.activity_turnos);
        parametros.putString("mostrarSalir", "S");
        super.onCreate(parametros);

        Log.d(TAG, "Se inicializa la vista");

        loading = (ProgressBar) findViewById(R.id.loadingLogin);
        loading.setVisibility(View.GONE);
        recTurnos = (RecyclerView) findViewById(R.id.recTurnos);
        recTurnos.setHasFixedSize(true);
        recTurnos.setLayoutManager(new LinearLayoutManager(this));

        lsTurnos = new ArrayList<>();

        adapter = new TurnosAdapter(lsTurnos, this);
        recTurnos.setAdapter(adapter);

        consultarTurnos();
    }

    private void consultarTurnos(){
        try{
            HashMap<String, Object> datosLogin = sesion.obtenerDatosSession();
            final String token = datosLogin.get("tokenWs").toString();
            String url = this.getString(R.string.base_path) + "/v1/mobile/contratantes/"+datosLogin.get("codigoEmpresa")+"-"+datosLogin.get("codigoSucursal")+"/turnos";
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
                                JSONArray jsonTurnos = response.getJSONArray("turnos");
                                lsTurnos.clear();

                                TurnosDTO objTur;
                                for (int i = 0; i < jsonTurnos.length(); i++){
                                    objTur = new TurnosDTO();
                                    objTur.cargarEntidadconJson(jsonTurnos.optJSONObject(i));
                                    lsTurnos.add(objTur);
                                }
                                adapter.notifyDataSetChanged();

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

                                WsUtil.lanzarMensajeError(TurnosActivity.this, error.networkResponse);
                            }catch(Exception e){
                                e.printStackTrace();
                            }
                        }
                    }

            ) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    return WsUtil.generarHeader(TurnosActivity.this,null,null,token, null);
                }

            };

            jsonRequest.setRetryPolicy(WsUtil.setTimeOut());

            requestQueue.add(jsonRequest);
            loading.setVisibility(View.VISIBLE);

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void seleccionarTurno(TurnosDTO objTurnoSeleccionado){
        Intent i;
        i = new Intent(TurnosActivity.this, PacientesActivity.class);
        i.putExtra("idTurno", objTurnoSeleccionado.getIdTurno());
        i.putExtra("descripcionTurno", objTurnoSeleccionado.getDescripcionHorario());
        startActivity(i);
        //startActivityForResult(i, 1000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sesion.actualizarVistaPacientes("T");
        consultarTurnos();
        //Toast.makeText(this,"ONRESUME",Toast.LENGTH_LONG).show();
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
                            Log.d("LoginActivity", "Se genero nuevo token");
                            consultarTurnos();
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
                            WsUtil.lanzarMensajeError(TurnosActivity.this, error.networkResponse);
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                }

        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return WsUtil.generarHeader(TurnosActivity.this,usuario,clave,null, MediaType.JSON_UTF_8);
            }

        };

        jsonRequest.setRetryPolicy(WsUtil.setTimeOut());
        requestQueue.add(jsonRequest);
        loading.setVisibility(View.VISIBLE);
    }
}
