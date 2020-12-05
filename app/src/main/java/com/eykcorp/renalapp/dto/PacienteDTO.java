package com.eykcorp.renalapp.dto;


import java.math.BigDecimal;

/**
 * Created by galo.penaherrera on 24/11/2017.
 */

public class PacienteDTO {

    /*private String secuenciaAtencion;
    private String secuenciaTurnoPaciente;*/
    private String idAtencionPaciente;
    private String nombre;
    private String descripcionMaquina;
    private String codigoEstado;
    private String descripcionEstado;
    private String edad;
    private String peso;
    private String talla;
    private String pathFoto;
    private String valorUF;
    private String qsPrescrito;
    private String tiempoDialisis;
    private String nombreFiltro;
    private String planMedicacion;

    public PacienteDTO(String idAtencionPaciente, String nombre, String descripcionMaquina, String edad,
                       String peso, String talla, String descripcionEstado, String pathFoto, String codigoEstado,
                       String valorUF, String qsPrescrito, String tiempoDialisis, String nombreFiltro,
                       String planMedicacion) {
        /*this.secuenciaAtencion = secuenciaAtencion;
        this.secuenciaTurnoPaciente = secuenciaTurnoPaciente;*/
        this.codigoEstado = codigoEstado;
        this.idAtencionPaciente = idAtencionPaciente;
        this.descripcionEstado = descripcionEstado;
        this.nombre = nombre;
        this.descripcionMaquina = descripcionMaquina;
        this.edad = edad;
        this.peso = peso;
        this.talla = talla;
        this.pathFoto = pathFoto;
        this.valorUF = valorUF;
        this.qsPrescrito = qsPrescrito;
        this.tiempoDialisis = tiempoDialisis;
        this.nombreFiltro = nombreFiltro;
        this.planMedicacion = planMedicacion;
    }

    public String getEdad() {
        return edad;
    }

    public void setEdad(String edad) {
        this.edad = edad;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcionMaquina() {
        return descripcionMaquina;
    }

    public void setDescripcionMaquina(String descripcionMaquina) {
        this.descripcionMaquina = descripcionMaquina;
    }

    public String getPeso() {
        return peso;
    }

    public void setPeso(String peso) {
        this.peso = peso;
    }

    public String getTalla() {
        return talla;
    }

    public void setTalla(String talla) {
        this.talla = talla;
    }

    public String getIdAtencionPaciente() {
        return idAtencionPaciente;
    }

    public void setIdAtencionPaciente(String idAtencionPaciente) {
        this.idAtencionPaciente = idAtencionPaciente;
    }

    public String getDescripcionEstado() {
        return descripcionEstado;
    }

    public void setDescripcionEstado(String descripcionEstado) {
        this.descripcionEstado = descripcionEstado;
    }

    public String getPathFoto() {
        return pathFoto;
    }

    public void setPathFoto(String pathFoto) {
        this.pathFoto = pathFoto;
    }

    public String getCodigoEstado() {
        return codigoEstado;
    }

    public void setCodigoEstado(String codigoEstado) {
        this.codigoEstado = codigoEstado;
    }

    @Override
    public String toString() {
        return this.nombre;
    }

    public String getValorUF() {
        return valorUF;
    }

    public void setValorUF(String valorUF) {
        this.valorUF = valorUF;
    }

    public String getQsPrescrito() {
        return qsPrescrito;
    }

    public void setQsPrescrito(String qsPrescrito) {
        this.qsPrescrito = qsPrescrito;
    }

    public String getTiempoDialisis() {
        return tiempoDialisis;
    }

    public void setTiempoDialisis(String tiempoDialisis) {
        this.tiempoDialisis = tiempoDialisis;
    }

    public String getNombreFiltro() {
        return nombreFiltro;
    }

    public void setNombreFiltro(String nombreFiltro) {
        this.nombreFiltro = nombreFiltro;
    }

    public String getPlanMedicacion() {
        return planMedicacion;
    }

    public void setPlanMedicacion(String planMedicacion) {
        this.planMedicacion = planMedicacion;
    }
}
