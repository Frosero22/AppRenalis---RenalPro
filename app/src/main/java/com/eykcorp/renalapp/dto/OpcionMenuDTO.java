package com.eykcorp.renalapp.dto;

import com.eykcorp.renalapp.util.BaseObject;

/**
 * Created by galo.penaherrera on 16/11/2017.
 */

public class OpcionMenuDTO extends BaseObject{

    private Integer codigoOpcion;
    private String acceso;

    public  OpcionMenuDTO(){

    }

    public OpcionMenuDTO(Integer codigoOpcion, String acceso) {
        this.codigoOpcion = codigoOpcion;
        this.acceso = acceso;
    }

    public Integer getCodigoOpcion() {
        return codigoOpcion;
    }

    public void setCodigoOpcion(Integer codigoOpcion) {
        this.codigoOpcion = codigoOpcion;
    }

    public String getAcceso() {
        return acceso;
    }

    public void setAcceso(String acceso) {
        this.acceso = acceso;
    }
}
