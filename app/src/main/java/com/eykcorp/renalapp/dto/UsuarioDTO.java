package com.eykcorp.renalapp.dto;

/**
 * Created by galo.penaherrera on 25/09/2017.
 */
public class UsuarioDTO {

    private String nombreMostrarLogin;
    private String pathFoto;
    private String secuenciaUsuario;
    private String token;

    public String getNombreMostrarLogin() {
        return nombreMostrarLogin;
    }

    public void setNombreMostrarLogin(String nombreMostrarLogin) {
        this.nombreMostrarLogin = nombreMostrarLogin;
    }

    public String getPathFoto() {
        return pathFoto;
    }

    public void setPathFoto(String pathFoto) {
        this.pathFoto = pathFoto;
    }

    public String getSecuenciaUsuario() {
        return secuenciaUsuario;
    }

    public void setSecuenciaUsuario(String secuenciaUsuario) {
        this.secuenciaUsuario = secuenciaUsuario;
    }
}
