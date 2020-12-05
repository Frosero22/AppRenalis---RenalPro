package com.eykcorp.renalapp.dto;

import android.os.Parcel;
import android.os.Parcelable;

import com.eykcorp.renalapp.util.BaseObject;

/**
 * Created by galo.penaherrera on 11/12/2017.
 */

public class SignosVitalesDTO extends BaseObject  {

    private String idSignoVital;
    private String codigoUsuarioToma;
    private String descripcionFechaToma;
    private String nombreCompletoUsuarioToma;
    private Double tensionArterialSistolica;
    private Double tensionArterialDiastolica;
    private Double pulso;
    private Double temperatura;
    private Double valorQs;
    private Double valorQsEfectivo;
    private Double valorQd;
    private Double valorPMas;
    private Double valorPMenos;
    private Double valorPtm;
    private String medicacion;
    private String observacion;
    private String fecha;
    private String horaToma;

    public SignosVitalesDTO(){

    }

    public SignosVitalesDTO(String idSignoVital, String codigoUsuarioToma, String descripcionFechaToma, String nombreCompletoUsuarioToma, Double tensionArterialSistolica, Double tensionArterialDiastolica, Double pulso, Double temperatura, Double valorQs, Double valorQd, Double valorPMas, Double valorPMenos, Double valorPtm, String medicacion, String observacion,
                            Double valorQsEfectivo) {
        this.idSignoVital = idSignoVital;
        this.codigoUsuarioToma = codigoUsuarioToma;
        this.descripcionFechaToma = descripcionFechaToma;
        this.nombreCompletoUsuarioToma = nombreCompletoUsuarioToma;
        this.tensionArterialSistolica = tensionArterialSistolica;
        this.tensionArterialDiastolica = tensionArterialDiastolica;
        this.pulso = pulso;
        this.temperatura = temperatura;
        this.valorQs = valorQs;
        this.valorQsEfectivo = valorQsEfectivo;
        this.valorQd = valorQd;
        this.valorPMas = valorPMas;
        this.valorPMenos = valorPMenos;
        this.valorPtm = valorPtm;
        this.medicacion = medicacion;
        this.observacion = observacion;
    }

    public String getIdSignoVital() {
        return idSignoVital;
    }

    public void setIdSignoVital(String idSignoVital) {
        this.idSignoVital = idSignoVital;
    }

    public String getCodigoUsuarioToma() {
        return codigoUsuarioToma;
    }

    public void setCodigoUsuarioToma(String codigoUsuarioToma) {
        this.codigoUsuarioToma = codigoUsuarioToma;
    }

    public String getDescripcionFechaToma() {
        return descripcionFechaToma;
    }

    public void setDescripcionFechaToma(String descripcionFechaToma) {
        this.descripcionFechaToma = descripcionFechaToma;
    }

    public String getNombreCompletoUsuarioToma() {
        return nombreCompletoUsuarioToma;
    }

    public void setNombreCompletoUsuarioToma(String nombreCompletoUsuarioToma) {
        this.nombreCompletoUsuarioToma = nombreCompletoUsuarioToma;
    }

    public Double getTensionArterialSistolica() {
        return tensionArterialSistolica;
    }

    public void setTensionArterialSistolica(Double tensionArterialSistolica) {
        this.tensionArterialSistolica = tensionArterialSistolica;
    }

    public Double getTensionArterialDiastolica() {
        return tensionArterialDiastolica;
    }

    public void setTensionArterialDiastolica(Double tensionArterialDiastolica) {
        this.tensionArterialDiastolica = tensionArterialDiastolica;
    }

    public Double getPulso() {
        return pulso;
    }

    public void setPulso(Double pulso) {
        this.pulso = pulso;
    }

    public Double getTemperatura() {
        return temperatura;
    }

    public void setTemperatura(Double temperatura) {
        this.temperatura = temperatura;
    }

    public Double getValorQs() {
        return valorQs;
    }

    public void setValorQs(Double valorQs) {
        this.valorQs = valorQs;
    }

    public Double getValorQd() {
        return valorQd;
    }

    public void setValorQd(Double valorQd) {
        this.valorQd = valorQd;
    }

    public Double getValorPMas() {
        return valorPMas;
    }

    public void setValorPMas(Double valorPMas) {
        this.valorPMas = valorPMas;
    }

    public Double getValorPMenos() {
        return valorPMenos;
    }

    public void setValorPMenos(Double valorPMenos) {
        this.valorPMenos = valorPMenos;
    }

    public Double getValorPtm() {
        return valorPtm;
    }

    public void setValorPtm(Double valorPtm) {
        this.valorPtm = valorPtm;
    }

    public String getMedicacion() {
        return medicacion;
    }

    public void setMedicacion(String medicacion) {
        this.medicacion = medicacion;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public Double getValorQsEfectivo() {
        return valorQsEfectivo;
    }

    public void setValorQsEfectivo(Double valorQsEfectivo) {
        this.valorQsEfectivo = valorQsEfectivo;
    }

    public SignosVitalesDTO(Parcel in){
        this.tensionArterialSistolica = in.readDouble();
        this.tensionArterialDiastolica = in.readDouble();
        this.pulso = in.readDouble();
        this.valorPMas = in.readDouble();
        this.valorPMenos = in.readDouble();
        this.valorQs = in.readDouble();
        this.valorQd = in.readDouble();
        this.temperatura = in.readDouble();
        this.valorPtm = in.readDouble();
        this.observacion = in.readString();
        this.medicacion = in.readString();
        this.nombreCompletoUsuarioToma = in.readString();
        this.codigoUsuarioToma = in.readString();
        this.descripcionFechaToma = in.readString();
        this.idSignoVital = in.readString();
    }

    /*@Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.tensionArterialSistolica);
        dest.writeDouble(this.tensionArterialDiastolica);
        dest.writeDouble(this.pulso);
        dest.writeDouble(this.valorPMas);
        dest.writeDouble(this.valorPMenos);
        dest.writeDouble(this.valorQs);
        dest.writeDouble(this.valorQd);
        dest.writeDouble(this.temperatura!=null?this.temperatura:null);
        dest.writeDouble(this.valorPtm);
        dest.writeString(this.observacion);
        dest.writeString(this.medicacion);
        dest.writeString(this.nombreCompletoUsuarioToma);
        dest.writeString(this.codigoUsuarioToma);
        dest.writeString(this.descripcionFechaToma);
        dest.writeString(this.idSignoVital);
    }*/

    /*@Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }*/

    /*public static final Parcelable.Creator<SignosVitalesDTO> CREATOR = new Parcelable.Creator<SignosVitalesDTO>() {
        public SignosVitalesDTO createFromParcel(Parcel in) {
            return new SignosVitalesDTO(in);
        }

        public SignosVitalesDTO[] newArray(int size) {
            return new SignosVitalesDTO[size];
        }
    };*/

    public String getHoraToma() {
        return horaToma;
    }

    public void setHoraToma(String horaToma) {
        this.horaToma = horaToma;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
}
