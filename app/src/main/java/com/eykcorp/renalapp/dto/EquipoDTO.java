package com.eykcorp.renalapp.dto;

/**
 * Created by Galo on 02/12/2017.
 */

public class EquipoDTO {

    private String idEquipo;
    private String descripcionEquipo;

    public EquipoDTO(String idEquipo, String descripcionEquipo){
        this.idEquipo = idEquipo;
        this.descripcionEquipo = descripcionEquipo;
    }

    public String getIdEquipo() {
        return idEquipo;
    }

    public void setIdEquipo(String idEquipo) {
        this.idEquipo = idEquipo;
    }

    public String getDescripcionEquipo() {
        return descripcionEquipo;
    }

    public void setDescripcionEquipo(String descripcionEquipo) {
        this.descripcionEquipo = descripcionEquipo;
    }

    @Override
    public String toString() {
        return this.descripcionEquipo;
    }
}
