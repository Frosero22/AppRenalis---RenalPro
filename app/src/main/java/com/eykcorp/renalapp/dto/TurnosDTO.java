package com.eykcorp.renalapp.dto;

import android.icu.math.BigDecimal;
import android.os.Parcelable;

import com.eykcorp.renalapp.activity.TurnosActivity;
import com.eykcorp.renalapp.util.BaseObject;

/**
 * Created by galo.penaherrera on 23/11/2017.
 */

public class TurnosDTO extends BaseObject{

    private String idTurno;
    private String descripcionHorario;

    public TurnosDTO(){

    }

    public TurnosDTO(String idTurno, String descripcionTurno) {
        this.idTurno = idTurno;
        this.descripcionHorario = descripcionTurno;
    }

    public String getDescripcionHorario() {
        return descripcionHorario;
    }

    public void setDescripcionHorario(String descripcionHorario) {
        this.descripcionHorario = descripcionHorario;
    }

    @Override
    public String toString() {
        return this.descripcionHorario;
    }

    public String getIdTurno() {
        return idTurno;
    }

    public void setIdTurno(String idTurno) {
        this.idTurno = idTurno;
    }
}
