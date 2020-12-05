package com.eykcorp.renalapp.dto;

import com.eykcorp.renalapp.util.BaseObject;

import java.util.List;

/**
 * Created by galo.penaherrera on 26/09/2017.
 */
public class SucursalDTO extends BaseObject{

    private String secuenciaSucursal;
    private String nombreSucursal;
    private List<OpcionMenuDTO> opcionesDisponibles;

    public String getSecuenciaSucursal() {
        return secuenciaSucursal;
    }

    public void setSecuenciaSucursal(String secuenciaSucursal) {
        this.secuenciaSucursal = secuenciaSucursal;
    }

    public String getNombreSucursal() {
        return nombreSucursal;
    }

    public void setNombreSucursal(String nombreSucursal) {
        this.nombreSucursal = nombreSucursal;
    }

    public List<OpcionMenuDTO> getOpcionesDisponibles() {
        return opcionesDisponibles;
    }

    public void setOpcionesDisponibles(List<OpcionMenuDTO> opcionesDisponibles) {
        this.opcionesDisponibles = opcionesDisponibles;
    }

    @Override
    public String toString() {
        return this.getNombreSucursal();
    }
}
