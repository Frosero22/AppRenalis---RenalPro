package com.eykcorp.renalapp.dto;

import android.os.Parcel;
import android.os.Parcelable;

import com.eykcorp.renalapp.fragment.MenuBottomSheetDialogFragment;
import com.eykcorp.renalapp.util.BaseObject;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by galo.penaherrera on 27/02/2018.
 */

public class MedicamentoDTO extends BaseObject implements Parcelable{

    private String idReceta;
    private String secuenciaPrincipioActivo;
    private String descripcionMedicamento;
    private String cantidad;
    private String frecuencia;
    private String duracion;
    private String comentario;
    private String isChecked;

    public String getIdReceta() {
        return idReceta;
    }

    public void setIdReceta(String idReceta) {
        this.idReceta = idReceta;
    }

    public String getSecuenciaPrincipioActivo() {
        return secuenciaPrincipioActivo;
    }

    public void setSecuenciaPrincipioActivo(String secuenciaPrincipioActivo) {
        this.secuenciaPrincipioActivo = secuenciaPrincipioActivo;
    }

    public String getDescripcionMedicamento() {
        return descripcionMedicamento;
    }

    public void setDescripcionMedicamento(String descripcionMedicamento) {
        this.descripcionMedicamento = descripcionMedicamento;
    }

    public String getCantidad() {
        return cantidad;
    }

    public void setCantidad(String cantidad) {
        this.cantidad = cantidad;
    }

    public String getFrecuencia() {
        return frecuencia;
    }

    public void setFrecuencia(String frecuencia) {
        this.frecuencia = frecuencia;
    }

    public String getDuracion() {
        return duracion;
    }

    public void setDuracion(String duracion) {
        this.duracion = duracion;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public String getIsChecked() {
        return isChecked;
    }

    public void setIsChecked(String isChecked) {
        this.isChecked = isChecked;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.idReceta);
        dest.writeString(this.secuenciaPrincipioActivo);
        dest.writeString(this.descripcionMedicamento);
        dest.writeString(this.cantidad);
        dest.writeString(this.frecuencia);
        dest.writeString(this.duracion);
        dest.writeString(this.comentario);
        dest.writeString(this.isChecked);
    }

    public MedicamentoDTO(){

    }

    public MedicamentoDTO(Parcel in) {
        this.idReceta = in.readString();
        this.secuenciaPrincipioActivo = in.readString();
        this.descripcionMedicamento = in.readString();
        this.cantidad = in.readString();
        this.frecuencia = in.readString();
        this.duracion = in.readString();
        this.comentario = in.readString();
        this.isChecked = in.readString();
    }

    public static final Parcelable.Creator<MedicamentoDTO> CREATOR = new Parcelable.Creator<MedicamentoDTO>() {
        public MedicamentoDTO createFromParcel(Parcel in) {
            return new MedicamentoDTO(in);
        }

        public MedicamentoDTO[] newArray(int size) {
            return new MedicamentoDTO[size];
        }
    };
}
