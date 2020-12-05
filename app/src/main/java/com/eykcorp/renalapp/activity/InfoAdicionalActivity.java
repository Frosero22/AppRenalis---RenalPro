package com.eykcorp.renalapp.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.eykcorp.renalapp.R;
import com.eykcorp.renalapp.com.eykcorp.renalapp.util.GenericActivity;
import com.eykcorp.renalapp.dto.InfoAdicionalDTO;
import com.eykcorp.renalapp.util.WsUtil;
import com.google.common.net.MediaType;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by galo.penaherrera on 21/12/2017.
 */

public class InfoAdicionalActivity extends GenericActivity {

    private String TAG = "InfoAdicionalActivity";
    private String idAtencionPaciente;
    private TextView txvTituloPaciente;
    private ProgressBar loading;
    private RequestQueue requestQueue;
    private TextView txvFiltro;
    private TextView txvLinea;
    private TextView txvAcceso;
    private TextView txvTiempoP;
    private TextView txvMaquina;
    private TextView txvUf;
    private TextView txvQs;
    private TextView txvQd;
    private TextView txvAguja;
    private TextView txvMedicamentos;
    private TextView txvTalla;
    private ListView lsvMedicamentos ;
    private List<String> lsMedicamentos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Bundle parametros = new Bundle();
        parametros.putInt("layoutActivity", R.layout.activity_info_adicional);
        parametros.putString("mostrarSalir", "S");
        super.onCreate(parametros);

        loading = (ProgressBar) findViewById(R.id.loading);
        loading.setVisibility(View.GONE);

        lsMedicamentos = new ArrayList<>();
        txvTituloPaciente = (TextView) findViewById(R.id.txvTituloPaciente);
        txvFiltro = (TextView) findViewById(R.id.txvFiltro);
        txvLinea = (TextView) findViewById(R.id.txvLinea);
        txvAcceso = (TextView) findViewById(R.id.txvAcceso);
        txvTiempoP = (TextView) findViewById(R.id.txvTiempoP);
        txvMaquina = (TextView) findViewById(R.id.txvMaquina);
        txvTalla = (TextView) findViewById(R.id.txvTalla);
        txvUf = (TextView) findViewById(R.id.txvUf);
        txvQs = (TextView) findViewById(R.id.txvQs);
        txvQd = (TextView) findViewById(R.id.txvQd);
        txvAguja = (TextView) findViewById(R.id.txvAguja);
        txvMedicamentos = (TextView) findViewById(R.id.txvMedicamentos);
        lsvMedicamentos = (ListView) findViewById(R.id.lsvMedicamentos);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            txvTituloPaciente.setText(bundle.get("nombrePaciente").toString());
            idAtencionPaciente = bundle.get("idAtencionPaciente").toString();
        }


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, lsMedicamentos);

        lsvMedicamentos.setAdapter(adapter);

        consultarInfo();

    }


    private void consultarInfo(){
        try{
            final String token = sesion.obtenerDatosSession().get("tokenWs").toString();
            String url = this.getString(R.string.base_path) + "/v1/mobile/pacientes/"+idAtencionPaciente+"/informacionadicional";
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

                                InfoAdicionalDTO objInfo = new InfoAdicionalDTO();
                                objInfo.cargarEntidadconJson(response.optJSONObject("infoAdicional"));
                                JSONArray jsonMedicamentos = response.optJSONObject("infoAdicional").getJSONArray("medicamentosPrescripcion");

                                String medicamentos = "";
                                for(int k = 0; k < jsonMedicamentos.length(); k++){
                                    lsMedicamentos.add(jsonMedicamentos.getString(k));
                                    medicamentos = medicamentos + jsonMedicamentos.getString(k)+",";
                                    //Log.d(TAG, jsonMedicamentos.getString(k));
                                }

                                txvMedicamentos.setText(medicamentos);
                                txvAcceso.setText(objInfo.getAcceso());
                                txvFiltro.setText(objInfo.getFiltro());
                                txvLinea.setText(objInfo.getLinea());
                                txvTiempoP.setText(objInfo.getTiempoP());
                                txvAguja.setText(objInfo.getTipoAguja());
                                txvMaquina.setText(objInfo.getTipoMaquina());
                                txvQs.setText(objInfo.getValorQs());
                                txvUf.setText(objInfo.getValorUf());
                                txvQd.setText(objInfo.getValorQd());
                                txvTalla.setText(objInfo.getTalla());
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
                                WsUtil.lanzarMensajeError(InfoAdicionalActivity.this, error.networkResponse);
                            }catch(Exception e){
                                e.printStackTrace();
                            }
                        }
                    }

            ) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    return WsUtil.generarHeader(InfoAdicionalActivity.this,null,null,token, null);
                }

            };

            jsonRequest.setRetryPolicy(WsUtil.setTimeOut());

            requestQueue.add(jsonRequest);
            loading.setVisibility(View.VISIBLE);
        }catch(Exception e){
            e.printStackTrace();
        }
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

                            consultarInfo();
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
                            WsUtil.lanzarMensajeError(InfoAdicionalActivity.this, error.networkResponse);
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                }

        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return WsUtil.generarHeader(InfoAdicionalActivity.this,usuario,clave,null, MediaType.JSON_UTF_8);
            }

        };

        jsonRequest.setRetryPolicy(WsUtil.setTimeOut());
        requestQueue.add(jsonRequest);
        loading.setVisibility(View.VISIBLE);
    }

}
