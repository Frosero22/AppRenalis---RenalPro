package com.eykcorp.renalapp.dto;

import com.eykcorp.renalapp.util.BaseObject;

import java.util.List;

/**
 * Created by galo.penaherrera on 26/09/2017.
 */
public class ContratanteDTO extends BaseObject{

    private String codigoContratante;
    private String nombreComercial;
    private List<SucursalDTO>  sucursales;

    public String getCodigoContratante() {
        return codigoContratante;
    }

    public void setCodigoContratante(String codigoContratante) {
        this.codigoContratante = codigoContratante;
    }

    public String getNombreComercial() {
        return nombreComercial;
    }

    public void setNombreComercial(String nombreComercial) {
        this.nombreComercial = nombreComercial;
    }

    public List<SucursalDTO> getSucursales() {
        return sucursales;
    }

    public void setSucursales(List<SucursalDTO> sucursales) {
        this.sucursales = sucursales;
    }

    @Override
    public String toString() {
        return this.getNombreComercial();
    }
}
