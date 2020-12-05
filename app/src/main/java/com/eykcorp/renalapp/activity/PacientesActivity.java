package com.eykcorp.renalapp.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.OrientationHelper;
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
import com.eykcorp.renalapp.com.eykcorp.renalapp.util.GenericActivity;
import com.eykcorp.renalapp.dto.PacienteDTO;
import com.eykcorp.renalapp.dto.TurnosDTO;
import com.eykcorp.renalapp.fragment.MenuBottomSheetDialogFragment;
import com.eykcorp.renalapp.util.VarColumnGridLayoutManager;
import com.eykcorp.renalapp.util.WsUtil;
import com.google.android.material.tabs.TabLayout;
import com.google.common.net.MediaType;

import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by galo.penaherrera on 24/11/2017.
 */

public class PacientesActivity extends GenericActivity {

    private String TAG = "PacientesActivity";
    private TextView txtTituloTurno;
    private String idTurno;
    private RecyclerView recPacientes;
    private List<PacienteDTO> pacientesTurno;
    private RecyclerView.Adapter adapter;
    private ProgressBar loading;
    private RequestQueue requestQueue;
    private AppCompatButton mostrarTodos;
    private AppCompatButton mostrarGrupo;
    private TabLayout tabs;
    private MenuBottomSheetDialogFragment menuInferior;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Bundle parametros = new Bundle();
        parametros.putInt("layoutActivity", R.layout.activity_pacientes);
        parametros.putString("mostrarSalir", "S");
        super.onCreate(parametros);

        pacientesTurno = new ArrayList<>();
        loading = (ProgressBar) findViewById(R.id.loadingLogin);
        loading.setVisibility(View.GONE);

        tabs = (TabLayout) findViewById(R.id.tabs);
        recPacientes = (RecyclerView) findViewById(R.id.recPacientes);
        txtTituloTurno = (TextView) findViewById(R.id.txvTituloTurno);
        recPacientes.setHasFixedSize(true);
        mostrarTodos = (AppCompatButton) findViewById(R.id.mostrarTodos);
        mostrarGrupo = (AppCompatButton) findViewById(R.id.mostrarGrupo);

        final VarColumnGridLayoutManager layoutManager
                = new VarColumnGridLayoutManager(this, OrientationHelper.VERTICAL, false);
        VarColumnGridLayoutManager.ColumnCountProvider columnProvider
                = new VarColumnGridLayoutManager.DefaultColumnCountProvider(this);
        layoutManager.setColumnCountProvider(columnProvider);

        recPacientes.setLayoutManager(layoutManager);

        /*GridLayoutManager lLayout = new GridLayoutManager(this, 2);
        recPacientes.setLayoutManager(lLayout);*/
        adapter = new PacientesAdapter(pacientesTurno, this, false, true);
        recPacientes.setAdapter(adapter);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            if(bundle.getString("idTurno") != null && bundle.getString("descripcionTurno") != null) {
                idTurno = bundle.getString("idTurno");
                txtTituloTurno.setText(bundle.getString("descripcionTurno"));
            }
        }

        mostrarTodos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sesion.actualizarVistaPacientes("T");
                consultarPacientesDia();
            }
        });

        mostrarGrupo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sesion.actualizarVistaPacientes("G");
                consultarPacientesMiGrupo();
            }
        });

        if(sesion.obtenerDatosSession().get("vistaPacientes")!=null) {
            if(sesion.obtenerDatosSession().get("vistaPacientes").equals("T")){
                tabs.getTabAt(0).select();
                consultarPacientesDia();
            }
            if(sesion.obtenerDatosSession().get("vistaPacientes").equals("G")){
                tabs.getTabAt(1).select();
                consultarPacientesMiGrupo();
            }
        }else{
            consultarPacientesDia();
        }

        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getPosition()==0){
                    sesion.actualizarVistaPacientes("T");
                    consultarPacientesDia();
                }
                if(tab.getPosition()==1){
                    sesion.actualizarVistaPacientes("G");
                    consultarPacientesMiGrupo();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    public void consultarPacientesMiGrupo(){
        try{
            HashMap<String, Object> datosLogin = sesion.obtenerDatosSession();
            //if(sesion.isLogin()){
            final String token = datosLogin.get("tokenWs").toString();
            String url = this.getString(R.string.base_path) + "/v1/mobile/pacientes/"+datosLogin.get("codigoUsuario").toString()+"/favoritos?arg0="+idTurno;
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
                                JSONArray jsonPacientes = response.getJSONArray("pacientes");
                                pacientesTurno.clear();

                                PacienteDTO objPac;
                                for (int i = 0; i < jsonPacientes.length(); i++){
                                    objPac = new PacienteDTO(jsonPacientes.optJSONObject(i).getString("idAtencionPaciente"),
                                            jsonPacientes.optJSONObject(i).getString("nombreCompleto"),
                                            jsonPacientes.optJSONObject(i).getString("descripcionMaquina"),
                                            jsonPacientes.optJSONObject(i).getString("edad"),
                                            jsonPacientes.optJSONObject(i).getString("peso"),
                                            jsonPacientes.optJSONObject(i).getString("talla"),
                                            jsonPacientes.optJSONObject(i).getString("descripcionEstado"),
                                            jsonPacientes.optJSONObject(i).getString("pathFoto"),
                                            jsonPacientes.optJSONObject(i).getString("codigoEstado"),
                                            jsonPacientes.optJSONObject(i).getString("valorUF"),
                                            jsonPacientes.optJSONObject(i).getString("qsPrescrito"),
                                            jsonPacientes.optJSONObject(i).getString("tiempoDialisis"),
                                            jsonPacientes.optJSONObject(i).getString("nombreFiltro"),
                                            jsonPacientes.optJSONObject(i).getString("planMedicacion"));

                                    pacientesTurno.add(objPac);
                                    Log.d(TAG, objPac.toString());

                                }
                                adapter = new PacientesAdapter(pacientesTurno, PacientesActivity.this, true, false);
                                recPacientes.setAdapter(adapter);
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

                                WsUtil.lanzarMensajeError(PacientesActivity.this, error.networkResponse);
                            }catch(Exception e){
                                e.printStackTrace();
                            }
                        }
                    }

            ) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    return WsUtil.generarHeader(PacientesActivity.this,null,null,token, null);
                }

            };

            jsonRequest.setRetryPolicy(WsUtil.setTimeOut());

            requestQueue.add(jsonRequest);
            loading.setVisibility(View.VISIBLE);
            //}
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void agregarFavoritos(PacienteDTO objPac){
        try{
            final String token = sesion.obtenerDatosSession().get("tokenWs").toString();
            String url = this.getString(R.string.base_path) + "/v1/mobile/pacientes/"+objPac.getIdAtencionPaciente()+"/favoritos";
            Log.d(TAG, "URL: " + url);

            JSONObject objJson = new JSONObject();
            objJson.put("codigoUsuario", sesion.obtenerDatosSession().get("codigoUsuario").toString());


            Log.d(TAG, objJson.toString());
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
                                Toast.makeText(PacientesActivity.this,"Paciente agregado a tu grupo", Toast.LENGTH_LONG).show();
                                consultarPacientesDia();
                                //lanzarMensaje("Paciente agregado a tu grupo");
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
                                    }
                                }
                                WsUtil.lanzarMensajeError(PacientesActivity.this, error.networkResponse);
                            }catch(Exception e){
                                e.printStackTrace();
                            }
                        }
                    }

            ) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    return WsUtil.generarHeader(PacientesActivity.this,null,null,token, MediaType.JSON_UTF_8);
                }

            };
            jsonRequest.setRetryPolicy(WsUtil.setTimeOut());

            requestQueue.add(jsonRequest);
            loading.setVisibility(View.VISIBLE);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void eliminarFavoritos(PacienteDTO objPac){
        try{
            final String token = sesion.obtenerDatosSession().get("tokenWs").toString();
            String url = this.getString(R.string.base_path) + "/v1/mobile/pacientes/"+objPac.getIdAtencionPaciente()+"/"+sesion.obtenerDatosSession().get("codigoUsuario").toString()+"/favoritos";
            Log.d(TAG, "URL: " + url);

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
                                Toast.makeText(PacientesActivity.this,"Paciente eliminado de tu grupo", Toast.LENGTH_LONG).show();
                                consultarPacientesMiGrupo();
                                //lanzarMensaje("Paciente eliminado de tu grupo");
                                //((PacientesActivity)getContext()).consultarPacientesDia();
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
                                    }
                                }
                                WsUtil.lanzarMensajeError(PacientesActivity.this, error.networkResponse);
                            }catch(Exception e){
                                e.printStackTrace();
                            }
                        }
                    }

            ) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    return WsUtil.generarHeader(PacientesActivity.this,null,null,token, MediaType.JSON_UTF_8);
                }

            };
            jsonRequest.setRetryPolicy(WsUtil.setTimeOut());

            requestQueue.add(jsonRequest);
            loading.setVisibility(View.VISIBLE);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void consultarPacientesDia(){
        try{
            HashMap<String, Object> datosLogin = sesion.obtenerDatosSession();
            //if(sesion.isLogin()){
                final String token = datosLogin.get("tokenWs").toString();
                String url = this.getString(R.string.base_path) + "/v1/mobile/turnos/"+idTurno+"/pacientes";
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
                                    JSONArray jsonPacientes = response.getJSONArray("pacientes");
                                    pacientesTurno.clear();

                                    PacienteDTO objPac;
                                    for (int i = 0; i < jsonPacientes.length(); i++){
                                        objPac = new PacienteDTO(jsonPacientes.optJSONObject(i).getString("idAtencionPaciente"),
                                                jsonPacientes.optJSONObject(i).getString("nombreCompleto"),
                                                jsonPacientes.optJSONObject(i).getString("descripcionMaquina"),
                                                jsonPacientes.optJSONObject(i).getString("edad"),
                                                jsonPacientes.optJSONObject(i).getString("peso"),
                                                jsonPacientes.optJSONObject(i).getString("talla"),
                                                jsonPacientes.optJSONObject(i).getString("descripcionEstado"),
                                                jsonPacientes.optJSONObject(i).getString("pathFoto"),
                                                jsonPacientes.optJSONObject(i).getString("codigoEstado"),
                                                jsonPacientes.optJSONObject(i).getString("valorUF"),
                                                jsonPacientes.optJSONObject(i).getString("qsPrescrito"),
                                                jsonPacientes.optJSONObject(i).getString("tiempoDialisis"),
                                                jsonPacientes.optJSONObject(i).getString("nombreFiltro"),
                                                jsonPacientes.optJSONObject(i).getString("planMedicacion"));

                                        pacientesTurno.add(objPac);
                                        Log.d(TAG, objPac.toString());

                                    }
                                    adapter = new PacientesAdapter(pacientesTurno, PacientesActivity.this, false, true);
                                    recPacientes.setAdapter(adapter);
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

                                    WsUtil.lanzarMensajeError(PacientesActivity.this, error.networkResponse);
                                }catch(Exception e){
                                    e.printStackTrace();
                                }
                            }
                        }

                ) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        return WsUtil.generarHeader(PacientesActivity.this,null,null,token, null);
                    }

                };

                jsonRequest.setRetryPolicy(WsUtil.setTimeOut());

                requestQueue.add(jsonRequest);
                loading.setVisibility(View.VISIBLE);
            //}
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    public void mostrarMenuInferior(PacienteDTO objPaciente){
        menuInferior = new MenuBottomSheetDialogFragment();
        menuInferior.show(getSupportFragmentManager(), menuInferior.getTag());
        Bundle bundle = new Bundle();
        bundle.putString("nombrePaciente", objPaciente.getNombre());
        bundle.putString("idTurno", idTurno);
        bundle.putString("idAtencionPaciente", objPaciente.getIdAtencionPaciente());
        bundle.putString("idEstado", objPaciente.getCodigoEstado());

        menuInferior.setArguments(bundle);
    }


    /*public void lanzarMensaje(String mensaje){
        AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
        dialogo1.setTitle("Corprenal");
        dialogo1.setMessage(mensaje);

        dialogo1.setCancelable(false);
        dialogo1.setNeutralButton("Aceptar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {
                dialogo1.dismiss();
                //finish();
            }
        });
        dialogo1.show();
    }*/

    @Override
    protected void onRestart() {
        super.onRestart();

        if(sesion.obtenerDatosSession().get("vistaPacientes").equals("T")){
            consultarPacientesDia();
        }
        if(sesion.obtenerDatosSession().get("vistaPacientes").equals("G")){
            consultarPacientesMiGrupo();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if(sesion.obtenerDatosSession().get("vistaPacientes").equals("T")){
            consultarPacientesDia();
        }
        if(sesion.obtenerDatosSession().get("vistaPacientes").equals("G")){
            consultarPacientesMiGrupo();
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
                            consultarPacientesDia();
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
                            WsUtil.lanzarMensajeError(PacientesActivity.this, error.networkResponse);
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                }

        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return WsUtil.generarHeader(PacientesActivity.this,usuario,clave,null, MediaType.JSON_UTF_8);
            }

        };

        jsonRequest.setRetryPolicy(WsUtil.setTimeOut());
        requestQueue.add(jsonRequest);
        loading.setVisibility(View.VISIBLE);
    }

}
