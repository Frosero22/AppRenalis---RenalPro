package com.eykcorp.renalapp.dto;

import com.eykcorp.renalapp.util.BaseObject;

/**
 * Created by galo.penaherrera on 22/12/2017.
 */

public class InfoAdicionalDTO extends BaseObject {

    private String filtro;
    private String linea;
    private String acceso;
    private String tiempoP;
    private String tipoMaquina;
    private String tipoAguja;
    private String valorUf;
    private String valorQs;
    private String valorQd;
    private String talla;

    public String getFiltro() {
        return filtro;
    }

    public void setFiltro(String filtro) {
        this.filtro = filtro;
    }

    public String getLinea() {
        return linea;
    }

    public void setLinea(String linea) {
        this.linea = linea;
    }

    public String getAcceso() {
        return acceso;
    }

    public void setAcceso(String acceso) {
        this.acceso = acceso;
    }

    public String getTiempoP() {
        return tiempoP;
    }

    public void setTiempoP(String tiempoP) {
        this.tiempoP = tiempoP;
    }

    public String getTipoMaquina() {
        return tipoMaquina;
    }

    public void setTipoMaquina(String tipoMaquina) {
        this.tipoMaquina = tipoMaquina;
    }

    public String getTipoAguja() {
        return tipoAguja;
    }

    public void setTipoAguja(String tipoAguja) {
        this.tipoAguja = tipoAguja;
    }

    public String getValorUf() {
        return valorUf;
    }

    public void setValorUf(String valorUf) {
        this.valorUf = valorUf;
    }

    public String getValorQs() {
        return valorQs;
    }

    public void setValorQs(String valorQs) {
        this.valorQs = valorQs;
    }

    public String getValorQd() {
        return valorQd;
    }

    public void setValorQd(String valorQd) {
        this.valorQd = valorQd;
    }

    public String getTalla() {
        return talla;
    }

    public void setTalla(String talla) {
        this.talla = talla;
    }
}
