package com.eykcorp.renalapp.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.eykcorp.renalapp.dto.OpcionMenuDTO;

import java.util.HashMap;
import java.util.List;

/**
 * Created by galo.penaherrera on 26/09/2017.
 */
public  class SessionManager {

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;

    int PRIVATE_MODE = Context.MODE_PRIVATE;
    private static final String PREF_NAME = "preferenciasRenalMobile";

    private static final String KEY_IS_LOGIN = "isLoggedIn";
    private static final String KEY_NOMBRE_USUARIO = "nombreUsuario";
    private static final String KEY_TOKEN_WS = "tokenWs";
    private static final String KEY_SECUENCIA_USUARIO = "secuenciaUsuario";
    private static final String KEY_CODIGO_USUARIO = "codigoUsuario";
    private static final String KEY_CLAVE_USUARIO = "claveUsuario";
    private static final String KEY_CODIGO_EMPRESA = "codigoEmpresa";
    private static final String KEY_CODIGO_SUCURSAL = "codigoSucursal";
    private static final String KEY_NOMBRE_SUCURSAL = "nombreSucursal";
    private static final String KEY_OPCION_ASIGNAR = "opcionAsignar";
    private static final String KEY_OPCION_CONECTAR = "opcionConectar";
    private static final String KEY_OPCION_TOMA_SIGNOS = "opcionTomaSignos";
    private static final String KEY_OPCION_DESCONECTAR = "opcionDesconectar";
    private static final String KEY_OPCION_PARAM_INI = "opcionParamIni";
    private static final String KEY_VISTA_PACIENTES = "vistaPacientes";

    public SessionManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
        editor.commit();
    }

    public  void crearSessionLogin(String secuenciaUsuario, String nombreUsuario, String tokenWs, String codigoUsuario,
                                  String claveUsuario, String codigoEmpresa, String codigoSucursal,
                                  String asignar, String conectar, String tomaSignos, String desconectar,
                                  String nombreSucursal, String paramIni, String vistaPacientes){
        editor.putString(KEY_NOMBRE_USUARIO,nombreUsuario);
        editor.putBoolean(KEY_IS_LOGIN,true);
        editor.putString(KEY_TOKEN_WS,tokenWs);
        editor.putString(KEY_SECUENCIA_USUARIO,secuenciaUsuario);
        editor.putString(KEY_CODIGO_USUARIO,codigoUsuario);
        editor.putString(KEY_CLAVE_USUARIO, claveUsuario);
        editor.putString(KEY_CODIGO_EMPRESA, codigoEmpresa);
        editor.putString(KEY_CODIGO_SUCURSAL, codigoSucursal);
        editor.putString(KEY_NOMBRE_SUCURSAL, nombreSucursal);
        editor.putBoolean(KEY_OPCION_ASIGNAR, asignar.equals("S")?true:false);
        editor.putBoolean(KEY_OPCION_CONECTAR, conectar.equals("S")?true:false);
        editor.putBoolean(KEY_OPCION_TOMA_SIGNOS, tomaSignos.equals("S")?true:false);
        editor.putBoolean(KEY_OPCION_DESCONECTAR, desconectar.equals("S")?true:false);
        editor.putBoolean(KEY_OPCION_PARAM_INI, paramIni.equals("S")?true:false);
        editor.putString(KEY_VISTA_PACIENTES, vistaPacientes);
        editor.commit();
    }

    public HashMap<String, Object> obtenerDatosSession(){
        HashMap<String, Object> user = new HashMap<String, Object>();
        user.put(KEY_CODIGO_USUARIO, String.valueOf(pref.getString(KEY_CODIGO_USUARIO, null)));
        user.put(KEY_TOKEN_WS, pref.getString(KEY_TOKEN_WS, null));
        user.put(KEY_SECUENCIA_USUARIO, pref.getString(KEY_SECUENCIA_USUARIO, null));
        user.put(KEY_NOMBRE_USUARIO, pref.getString(KEY_NOMBRE_USUARIO, null));
        user.put(KEY_CLAVE_USUARIO, pref.getString(KEY_CLAVE_USUARIO, null));
        user.put(KEY_CODIGO_EMPRESA, String.valueOf(pref.getString(KEY_CODIGO_EMPRESA, null)));
        user.put(KEY_CODIGO_SUCURSAL, String.valueOf(pref.getString(KEY_CODIGO_SUCURSAL, null)));
        user.put(KEY_NOMBRE_SUCURSAL, pref.getString(KEY_NOMBRE_SUCURSAL, null));
        user.put(KEY_OPCION_ASIGNAR, pref.getBoolean(KEY_OPCION_ASIGNAR,false));
        user.put(KEY_OPCION_CONECTAR, pref.getBoolean(KEY_OPCION_CONECTAR,false));
        user.put(KEY_OPCION_TOMA_SIGNOS, pref.getBoolean(KEY_OPCION_TOMA_SIGNOS,false));
        user.put(KEY_OPCION_DESCONECTAR, pref.getBoolean(KEY_OPCION_DESCONECTAR,false));
        user.put(KEY_OPCION_PARAM_INI, pref.getBoolean(KEY_OPCION_PARAM_INI,false));
        user.put(KEY_VISTA_PACIENTES, pref.getString(KEY_VISTA_PACIENTES, null));
        return user;
    }

    public boolean isLogin(){
        return pref.getBoolean(KEY_IS_LOGIN, false);
    }

    public void logout(){
        editor.remove(KEY_CLAVE_USUARIO);
        editor.remove(KEY_CODIGO_EMPRESA);
        editor.remove(KEY_CODIGO_SUCURSAL);
        editor.remove(KEY_CLAVE_USUARIO);
        editor.remove(KEY_IS_LOGIN);
        editor.remove(KEY_NOMBRE_SUCURSAL);
        editor.remove(KEY_OPCION_ASIGNAR);
        editor.remove(KEY_NOMBRE_USUARIO);
        editor.remove(KEY_OPCION_CONECTAR);
        editor.remove(KEY_TOKEN_WS);
        editor.remove(KEY_OPCION_TOMA_SIGNOS);
        editor.remove(KEY_OPCION_DESCONECTAR);
        editor.remove(KEY_OPCION_PARAM_INI);
        editor.remove(KEY_VISTA_PACIENTES);
        editor.commit();
    }

    public void actualizarVistaPacientes(String vistaPacientes){
        editor.putString(KEY_VISTA_PACIENTES, vistaPacientes);
        editor.commit();
    }

    public void actualizarToken(String newToken){
        editor.putString(KEY_TOKEN_WS, newToken);
        editor.commit();
    }
}
