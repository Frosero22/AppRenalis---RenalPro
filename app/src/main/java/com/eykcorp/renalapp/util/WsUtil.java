package com.eykcorp.renalapp.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AlertDialog;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.eykcorp.renalapp.R;
import com.eykcorp.renalapp.activity.LoginActivity;
import com.google.common.net.MediaType;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by galo.penaherrera on 26/09/2017.
 */
public class WsUtil {

    private static int TIMEOUT = 60000;
    private static String TAG = "WsUtil";
    private String newToken = null;

    public static Map<String, String> generarHeader(Context context, String usuario, String clave, String token, MediaType tipo) {
        HashMap<String, String> params = new HashMap<String, String>();
        String auth = "";
        if(usuario != null && clave != null){
            String credentials = usuario + ":" + clave;
            auth =  "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.URL_SAFE|Base64.NO_WRAP);
            //auth = new String(Base64.encode((usuario+":"+clave).getBytes(), Base64.URL_SAFE|Base64.NO_WRAP));
            Log.d(TAG, auth);
        }

        if(token != null){
            auth = "Bearer "+token;
        }

        params.put("Authorization", auth);

        if(tipo != null){
            //params.put("Content-Type", "application/json");
            params.put("Content-Type", tipo.toString());
        }
        return params;
    }

    public static boolean lanzarMensajeError(Context context, NetworkResponse response){

        String mensaje = null;
        boolean newToken = false;
        try{
            AlertDialog.Builder dialogo1 = new AlertDialog.Builder(context);
            dialogo1.setTitle("Advertencia");
            dialogo1.setIcon(R.mipmap.ic_warning);

            if(response == null){
                mensaje = "Ha sucecido un problema de comunicación con el servidor, intentelo mas tarde";
            }else if(response.statusCode == 404 || response.statusCode == 500) {
                mensaje = "Ha sucedido un problema de interno con el servidor, intentelo mas tarde";
            }

            if(response.statusCode == 400 ){
                dialogo1.setTitle("Corprenal");
                dialogo1.setIcon(R.mipmap.ic_information);
                String jsonError = new String(response.data);
                JSONObject jsonResponse = new JSONObject(jsonError);
                mensaje = jsonResponse.getString("causa");
            }

            if(mensaje != null){
                dialogo1.setMessage(mensaje);
                dialogo1.setCancelable(false);
                dialogo1.setNeutralButton("Aceptar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {
                        dialogo1.dismiss();
                    }
                });
                dialogo1.show();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return newToken;
    }

    private void autenticar(final Context context){
        String url = context.getString(R.string.base_path) + "/v1/mobile/login";
        SessionManager ses = new SessionManager(context);
        final String usuario = ses.obtenerDatosSession().get("codigoUsuario").toString();
        final String clave = ses.obtenerDatosSession().get("claveUsuario").toString();

        Log.d("LoginActivity", "URL: " + url);

        JSONObject objJson = new JSONObject();

        RequestQueue requestQueue= Volley.newRequestQueue(context);

        JsonObjectRequest jsonRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                objJson,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            //Se decodifica el json de datos de usuario
                            //JSONObject jsonUsuario= response.getJSONObject("usuario");
                            newToken = response.getString("token");

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        try{
                            WsUtil.lanzarMensajeError(context, error.networkResponse);
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                }

        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return WsUtil.generarHeader(context,usuario,clave,null, MediaType.JSON_UTF_8);
            }

        };

        jsonRequest.setRetryPolicy(WsUtil.setTimeOut());

        requestQueue.add(jsonRequest);

    }

    public static RetryPolicy setTimeOut(){
        RetryPolicy policy = new DefaultRetryPolicy(TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        return policy;
    }


}

