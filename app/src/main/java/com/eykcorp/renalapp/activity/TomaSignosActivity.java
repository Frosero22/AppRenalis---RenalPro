package com.eykcorp.renalapp.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.eykcorp.renalapp.R;
import com.eykcorp.renalapp.adaptador.PacientesAdapter;
import com.eykcorp.renalapp.adaptador.SignosAdapter;
import com.eykcorp.renalapp.com.eykcorp.renalapp.util.GenericActivity;
import com.eykcorp.renalapp.dto.PacienteDTO;
import com.eykcorp.renalapp.dto.SignosVitalesDTO;
import com.eykcorp.renalapp.util.WsUtil;
import com.google.common.net.MediaType;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by galo.penaherrera on 11/12/2017.
 */

public class TomaSignosActivity extends GenericActivity {

    private String TAG = "TomaSignosActivity";
    private TextView txvTituloPaciente;
    private List<SignosVitalesDTO> historico;
    private ProgressBar loading;
    private RequestQueue requestQueue;
    private RecyclerView.Adapter adapter;
    private RecyclerView recHistorico;
    //private FloatingActionButton fabNuevaToma;
    private Button btnNueva;
    private String idTurno;
    private String idAtencionPaciente;
    private String nombrePaciente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Bundle parametros = new Bundle();
        parametros.putInt("layoutActivity", R.layout.activity_toma_signos_v2);
        parametros.putString("mostrarSalir", "S");
        super.onCreate(parametros);

        historico = new ArrayList<>();
        loading = (ProgressBar) findViewById(R.id.loading);
        loading.setVisibility(View.GONE);

        recHistorico = (RecyclerView) findViewById(R.id.recHistorico);
        txvTituloPaciente = (TextView) findViewById(R.id.txvTituloPaciente);
        //fabNuevaToma = (FloatingActionButton) findViewById(R.id.fabNuevaToma);
        btnNueva = (Button) findViewById(R.id.btnNueva);

        recHistorico.setHasFixedSize(true);
        adapter = new SignosAdapter(historico,this);
        recHistorico.setAdapter(adapter);

        LinearLayoutManager verticalLayoutmanager
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        recHistorico.setLayoutManager(verticalLayoutmanager);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            nombrePaciente = bundle.get("nombrePaciente").toString();
            //txvTituloPaciente.setText(bundle.get("nombrePaciente").toString());
            idTurno = bundle.getString("idTurno");
            idAtencionPaciente = bundle.getString("idAtencionPaciente");
        }

        btnNueva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                levantarEdicionSignos(null);
            }
        });

        /*fabNuevaToma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                levantarEdicionSignos(null);
            }
        });*/
        consultarHistorico();
    }

    private void consultarHistorico(){
        try{
            final String token = sesion.obtenerDatosSession().get("tokenWs").toString();
            String url = this.getString(R.string.base_path) + "/v1/mobile/pacientes/"+idAtencionPaciente+"/historico"+
                    "?arg0="+sesion.obtenerDatosSession().get("codigoUsuario").toString();
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
                                //pacientesTurno = new ArrayList<>();
                                loading.setVisibility(View.GONE);
                                //Se decodifica el json de datos de turnos
                                JSONArray jsonHistorico = response.getJSONArray("historico");
                                historico.clear();

                                SignosVitalesDTO objToma;
                                for (int i = 0; i < jsonHistorico.length(); i++){
                                    objToma = new SignosVitalesDTO();
                                    objToma.cargarEntidadconJson(jsonHistorico.optJSONObject(i));
                                    historico.add(objToma);
                                    //Log.d(TAG, "Fecha: "+objToma.getFecha());
                                    //Log.d(TAG, "QS "+objToma.getValorQs().toString());
                                }
                                txvTituloPaciente.setText(nombrePaciente+" ("+historico.size()+" registros de toma)");
                                adapter = new SignosAdapter(historico, TomaSignosActivity.this);
                                recHistorico.setAdapter(adapter);
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

                                WsUtil.lanzarMensajeError(TomaSignosActivity.this, error.networkResponse);
                            }catch(Exception e){
                                e.printStackTrace();
                            }
                        }
                    }

            ) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    return WsUtil.generarHeader(TomaSignosActivity.this,null,null,token, null);
                }

            };

            jsonRequest.setRetryPolicy(WsUtil.setTimeOut());

            requestQueue.add(jsonRequest);
            loading.setVisibility(View.VISIBLE);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        consultarHistorico();
        //Toast.makeText(this,"ONRESUME",Toast.LENGTH_LONG).show();
    }

    public void levantarEdicionSignos(SignosVitalesDTO objEditar){
        Intent i;
        i = new Intent(this, EdicionTomaSignosActivity.class);
        i.putExtra("nombrePaciente",nombrePaciente);
        i.putExtra("idAtencionPaciente", idAtencionPaciente);
        i.putExtra("idTurno", idTurno);
        i.putExtra("editar",objEditar!=null?true:false);

        if(objEditar != null){
            //i.putExtra("objHistorico", objEditar);

            if(objEditar.getIdSignoVital()!=null){
                i.putExtra("idSignoVital", objEditar.getIdSignoVital());
            }

            if (objEditar.getTemperatura() != null){
                i.putExtra("temperatura", objEditar.getTemperatura());
            }

            if (objEditar.getTensionArterialDiastolica() != null){
                i.putExtra("taDiastolica", objEditar.getTensionArterialDiastolica());
            }

            if (objEditar.getTensionArterialSistolica() != null){
                i.putExtra("taSistolica", objEditar.getTensionArterialSistolica());
            }

            if (objEditar.getPulso() != null){
                i.putExtra("pulso", objEditar.getPulso());
            }

            if (objEditar.getValorQs() != null){
                i.putExtra("qs", objEditar.getValorQs());
            }

            if (objEditar.getValorQd() != null){
                i.putExtra("qd", objEditar.getValorQd());
            }

            if (objEditar.getValorPMas() != null){
                i.putExtra("pmas", objEditar.getValorPMas());
            }

            if (objEditar.getValorPMenos() != null){
                i.putExtra("pmenos", objEditar.getValorPMenos());
            }

            if (objEditar.getValorPtm() != null){
                i.putExtra("ptm", objEditar.getValorPtm());
            }

            if (objEditar.getMedicacion() != null){
                i.putExtra("medicacion", objEditar.getMedicacion());
            }

            if (objEditar.getObservacion() != null){
                i.putExtra("observacion", objEditar.getObservacion());
            }

            if(objEditar.getDescripcionFechaToma() != null){
                i.putExtra("hora", objEditar.getHoraToma());
            }

            if(objEditar.getValorQsEfectivo() != null){
                i.putExtra("qsEfectivo", objEditar.getValorQsEfectivo());
            }


            if(objEditar.getDescripcionFechaToma() != null){
                i.putExtra("fecha", objEditar.getFecha());
            }

            if(!sesion.obtenerDatosSession().get("codigoUsuario").equals(objEditar.getCodigoUsuarioToma())){
                lanzarMensaje("No está autorizado a modificar este registro");
                return;
            }
        }

        startActivity(i);
    }

    public void eliminarRegistro(final SignosVitalesDTO objElm){
        AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
        dialogo1.setTitle("Corprenal");
        dialogo1.setIcon(R.mipmap.ic_information);
        dialogo1.setMessage("Vas a eliminar este registro?");

        dialogo1.setCancelable(true);
        dialogo1.setNeutralButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {
                eliminar(objElm.getIdSignoVital(), sesion.obtenerDatosSession().get("codigoUsuario").toString());
            }
        });
        dialogo1.show();
    }

    private void eliminar(String idSecuenciaSigno, String usuario){
        try{
            final String token = sesion.obtenerDatosSession().get("tokenWs").toString();
            String url = this.getString(R.string.base_path) + "/v1/mobile/pacientes/historico/"+idSecuenciaSigno+"-"+usuario;
            Log.d(TAG, "URL: " + url);

            //Log.d(TAG, objJson.toString());
            requestQueue= Volley.newRequestQueue(this);

            JsonObjectRequest jsonRequest = new JsonObjectRequest(
                    Request.Method.DELETE,
                    url,
                    null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                loading.setVisibility(View.GONE);
                                lanzarMensaje("Transaccion exitosa");
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
                                WsUtil.lanzarMensajeError(TomaSignosActivity.this, error.networkResponse);
                            }catch(Exception e){
                                e.printStackTrace();
                            }
                        }
                    }

            ) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    return WsUtil.generarHeader(TomaSignosActivity.this,null,null,token, MediaType.JSON_UTF_8);
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
        AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
        dialogo1.setTitle("Corprenal");
        dialogo1.setIcon(R.mipmap.ic_information);
        dialogo1.setMessage(mensaje);

        dialogo1.setCancelable(false);
        dialogo1.setNeutralButton("Aceptar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {
                dialogo1.dismiss();
                consultarHistorico();
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
                            consultarHistorico();
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
                            WsUtil.lanzarMensajeError(TomaSignosActivity.this, error.networkResponse);
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                }

        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return WsUtil.generarHeader(TomaSignosActivity.this,usuario,clave,null, MediaType.JSON_UTF_8);
            }

        };

        jsonRequest.setRetryPolicy(WsUtil.setTimeOut());
        requestQueue.add(jsonRequest);
        loading.setVisibility(View.VISIBLE);
    }

}
