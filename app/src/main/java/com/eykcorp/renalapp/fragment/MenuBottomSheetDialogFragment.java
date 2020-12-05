package com.eykcorp.renalapp.fragment;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.eykcorp.renalapp.R;
import com.eykcorp.renalapp.activity.AsignacionEquipoActivity;
import com.eykcorp.renalapp.activity.ConexionActivity;
import com.eykcorp.renalapp.activity.PacientesActivity;
import com.eykcorp.renalapp.activity.ParamIniActivity;
import com.eykcorp.renalapp.activity.DesconexionActivity;
import com.eykcorp.renalapp.activity.InfoAdicionalActivity;
import com.eykcorp.renalapp.activity.ReporteActivity;
import com.eykcorp.renalapp.activity.TomaSignosActivity;
import com.eykcorp.renalapp.util.SessionManager;
import com.eykcorp.renalapp.util.WsUtil;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.common.net.MediaType;

import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.Map;

/**
 * Created by galo.penaherrera on 30/11/2017.
 */

public class MenuBottomSheetDialogFragment extends BottomSheetDialogFragment {

    private TextView txvAsignar;
    private TextView txvConectar;
    private TextView txvTomaSignos;
    private TextView txvDesconectar;
    private TextView txvInfo;
    private TextView txvParamIni;
    private TextView txvReporte;
    private TextView txvAgregar;
    private TextView txvEliminar;
    private SessionManager sesion;
    private RequestQueue requestQueue;
    private String idAtencionPaciente;
    private String TAG = "MenuBottomSheetDialFrag";


    @Override
    public void setupDialog(Dialog dialog, int style) {
        //super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.menu_inferior_paciente, null);
        dialog.setContentView(contentView);
        sesion = new SessionManager(getContext());
        Bundle bundle = getArguments();
        final String nombrePaciente = bundle.get("nombrePaciente").toString();
        final String idTurno = bundle.get("idTurno").toString();
        idAtencionPaciente = bundle.get("idAtencionPaciente").toString();
        final String idEstadoPaciente = bundle.get("idEstado").toString();

        txvAsignar = (TextView) contentView.findViewById(R.id.txvAsignar);
        txvConectar = (TextView) contentView.findViewById(R.id.txvConectar);
        txvTomaSignos = (TextView) contentView.findViewById(R.id.txvTomaSignos);
        txvDesconectar = (TextView) contentView.findViewById(R.id.txvDesconectar);
        txvInfo = (TextView) contentView.findViewById(R.id.txvInfo);
        txvParamIni = (TextView) contentView.findViewById(R.id.txvParamIni);
        txvReporte = (TextView) contentView.findViewById(R.id.txvReporte);

        txvParamIni.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validarPermisos(5)){
                    if(!idEstadoPaciente.equals("CON")){
                        mostrarMensaje("El paciente debe estar conectado a una máquina");
                    }else{
                        dismiss();
                        Intent i;
                        i = new Intent(getContext(), ParamIniActivity.class);
                        i.putExtra("nombrePaciente",nombrePaciente);
                        i.putExtra("idTurno",idTurno);
                        i.putExtra("idAtencionPaciente", idAtencionPaciente);
                        startActivity(i);
                    }
                }else{
                    mostrarMensaje("No esta autorizado para utilizar esta función");
                }
            }
        });

        txvInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    dismiss();
                    Intent i;
                    i = new Intent(getContext(), InfoAdicionalActivity.class);
                    i.putExtra("nombrePaciente",nombrePaciente);
                    i.putExtra("idTurno",idTurno);
                    i.putExtra("idAtencionPaciente", idAtencionPaciente);
                    startActivity(i);
            }
        });

        txvAsignar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validarPermisos(1)){
                    if(idEstadoPaciente.equals("CON")) {
                        mostrarMensaje("El paciente ya está conectado a una máquina, debe estar desconectado para asignar otra.");
                    }else{
                        dismiss();
                        Intent i;
                        i = new Intent(getContext(), AsignacionEquipoActivity.class);
                        i.putExtra("nombrePaciente",nombrePaciente);
                        i.putExtra("idTurno",idTurno);
                        i.putExtra("idAtencionPaciente", idAtencionPaciente);
                        startActivity(i);
                        //startActivityForResult(i,100);
                    }
                }else{
                    mostrarMensaje("No esta autorizado para utilizar esta función");
                }
            }
        });

        txvConectar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validarPermisos(2)){
                    if(!idEstadoPaciente.equals("ASI")) {
                        mostrarMensaje("El paciente debe estar asignado a una máquina");
                    }else{
                        //Llamar ws para que conecte
                        //conectarEquipo();
                        dismiss();
                        Intent i;
                        i = new Intent(getContext(), ConexionActivity.class);
                        i.putExtra("nombrePaciente",nombrePaciente);
                        i.putExtra("idTurno",idTurno);
                        i.putExtra("idAtencionPaciente", idAtencionPaciente);
                        startActivity(i);
                    }
                }else{
                    mostrarMensaje("No esta autorizado para utilizar esta función");
                }

            }
        });

        txvTomaSignos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validarPermisos(3)){
                    if(idEstadoPaciente.equals("CON")){
                        dismiss();
                        Intent i;
                        i = new Intent(getContext(), TomaSignosActivity.class);
                        i.putExtra("nombrePaciente",nombrePaciente);
                        i.putExtra("idTurno",idTurno);
                        i.putExtra("idAtencionPaciente", idAtencionPaciente);
                        startActivity(i);
                        //startActivityForResult(i,300);
                    }else{
                        mostrarMensaje("El paciente debe estar conectado a una máquina");
                    }
                }else{
                    mostrarMensaje("No esta autorizado para utilizar esta función");
                }
                //Toast.makeText(getContext(),"Toma de signos",Toast.LENGTH_LONG).show();
            }
        });

        txvDesconectar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validarPermisos(4)){
                    if(idEstadoPaciente.equals("CON")){
                        dismiss();
                        Intent i;
                        i = new Intent(getContext(), DesconexionActivity.class);
                        i.putExtra("nombrePaciente",nombrePaciente);
                        i.putExtra("idTurno",idTurno);
                        i.putExtra("idAtencionPaciente", idAtencionPaciente);
                        startActivity(i);
                        //startActivityForResult(i,400);
                    }else{
                        mostrarMensaje("El paciente debe estar conectado a una máquina");
                    }
                }else{
                    mostrarMensaje("No esta autorizado para utilizar esta función");
                }
                //Toast.makeText(getContext(),"Desconectar",Toast.LENGTH_LONG).show();
            }
        });

        txvReporte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                generarReporte(nombrePaciente);
                /*if(!idEstadoPaciente.equals("ASI")){
                    mostrarMensaje("El paciente debe estar asignado a una máquina");
                }else{
                    generarReporte();
                }*/
            }
        });
    }


    private boolean validarPermisos(Integer codigoOpcion){
        SessionManager ses = new SessionManager(getContext());
        if(codigoOpcion.equals(1)){
            return (Boolean)ses.obtenerDatosSession().get("opcionAsignar");
        }
        if(codigoOpcion.equals(2)){
            return (Boolean)ses.obtenerDatosSession().get("opcionConectar");
        }
        if(codigoOpcion.equals(3)){
            return (Boolean)ses.obtenerDatosSession().get("opcionTomaSignos");
        }
        if(codigoOpcion.equals(4)){
            return (Boolean)ses.obtenerDatosSession().get("opcionDesconectar");
        }
        if(codigoOpcion.equals(5)){
            return (Boolean)ses.obtenerDatosSession().get("opcionParamIni");
        }
        return true;
    }

    private void mostrarMensaje(String mensaje){
        AlertDialog.Builder dialogo1 = new AlertDialog.Builder(getContext());
        dialogo1.setTitle("Corprenal");
        dialogo1.setMessage(mensaje);
        dialogo1.setIcon(R.mipmap.ic_information);
        dialogo1.setCancelable(false);
        dialogo1.setNeutralButton("Aceptar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {
                dialogo1.dismiss();
            }
        });
        dialogo1.show();
    }

    private void generarReporte(final String nombrePaciente){
        try{
            final String token = sesion.obtenerDatosSession().get("tokenWs").toString();
            String url = this.getString(R.string.base_path) + "/v1/mobile/pacientes/"+idAtencionPaciente+"/reporte/hemodialisis?arg0="+
                    sesion.obtenerDatosSession().get("codigoUsuario").toString();

            Log.d(TAG, "URL: " + url);
            requestQueue= Volley.newRequestQueue(getContext());

            JsonObjectRequest jsonRequest = new JsonObjectRequest(
                    Request.Method.GET,
                    url,
                    null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String rutaReporte = response.getString("rutaArchivo");
                                visualizarResultado(rutaReporte, nombrePaciente);
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
                                //loading.setVisibility(View.GONE);
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
                                WsUtil.lanzarMensajeError(getContext(), error.networkResponse);
                            }catch(Exception e){
                                e.printStackTrace();
                            }
                        }
                    }

            ) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    return WsUtil.generarHeader(getContext(),null,null,token, MediaType.JSON_UTF_8);
                }

            };
            jsonRequest.setRetryPolicy(WsUtil.setTimeOut());

            requestQueue.add(jsonRequest);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @JavascriptInterface
    public void visualizarResultado(String archivo, String nombrePaciente){
        String url = getResources().getString(R.string.base_path)+"/v1/mobile/reporte/hemodialisis?arg0="+ URLEncoder.encode(archivo);
        String urlFinal = "http://drive.google.com/viewerng/viewer?url="+url;
        //WebView webview = new WebView(this);
        //webview.getSettings().setJavaScriptEnabled(true);
        //String pdf = "http://www.adobe.com/devnet/acrobat/pdfs/pdf_open_parameters.pdf";
        //webview.loadUrl("http://200.31.28.222:8080/MiverisWsrest/servicio/doctor/archivo?arg0=%2Fcompartidos%2Fportal%2F1003879.pdf");
        //webview.loadUrl("http://drive.google.com/viewerng/viewer?url=" + url);
        try {
            /*Log.d(TAG,url);
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://drive.google.com/viewerng/viewer?url="+url));
            //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //intent.setPackage("com.android.chrome");
            startActivity(intent);*/

            Intent i;
            i = new Intent(getContext(), ReporteActivity.class);
            i.putExtra("urlReporte",urlFinal);
            i.putExtra("nombrePaciente",nombrePaciente);
            startActivity(i);

        }catch(ActivityNotFoundException e){
            Toast.makeText(getContext(), "Es preferible que instale Google Chrome.", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse("http://drive.google.com/viewerng/viewer?url="+url));
            startActivity(i);
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
            objJson.put("aclaramiento", null);
            objJson.put("naPlasmatico", null);
            objJson.put("kt", Double.parseDouble("0.0"));
            objJson.put("vst", Double.parseDouble("0.0"));
            objJson.put("tiempo", Integer.parseInt("0"));
            objJson.put("perdida", Double.parseDouble("0.0"));

            Log.d(TAG, objJson.toString());
            requestQueue= Volley.newRequestQueue(getContext());

            JsonObjectRequest jsonRequest = new JsonObjectRequest(
                    Request.Method.PUT,
                    url,
                    objJson,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                //loading.setVisibility(View.GONE);
                                lanzarMensaje("Conexión de máquina exitosa");
                                ((PacientesActivity)getContext()).consultarPacientesDia();
                                dismiss();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            try{
                                //loading.setVisibility(View.GONE);
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
                                WsUtil.lanzarMensajeError(getContext(), error.networkResponse);
                            }catch(Exception e){
                                e.printStackTrace();
                            }
                        }
                    }

            ) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    return WsUtil.generarHeader(getContext(),null,null,token, MediaType.JSON_UTF_8);
                }

            };
            jsonRequest.setRetryPolicy(WsUtil.setTimeOut());

            requestQueue.add(jsonRequest);
            //loading.setVisibility(View.VISIBLE);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void lanzarMensaje(String mensaje){
        AlertDialog.Builder dialogo1 = new AlertDialog.Builder(getContext());
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
    }
}
