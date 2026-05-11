package com.trabajopractico.inmobiliaria.modelo;

import java.io.Serializable;

// Modelo Contrato.
// JSON
//   { idContrato, fechaInicio, fechaFinalizacion, montoAlquiler, estado,
//     idInquilino, idInmueble, inquilino, inmueble }
//

public class Contrato implements Serializable {

    private int idContrato;
    private String fechaInicio;
    private String fechaFinalizacion;
    private double montoAlquiler;
    private boolean estado;
    private int idInquilino;
    private int idInmueble;
    private Inquilino inquilino;
    private Inmueble inmueble;

    public Contrato() { }

    public int getIdContrato() { return idContrato; }
    public void setIdContrato(int idContrato) { this.idContrato = idContrato; }

    public String getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(String fechaInicio) { this.fechaInicio = fechaInicio; }

    public String getFechaFinalizacion() { return fechaFinalizacion; }
    public void setFechaFinalizacion(String fechaFinalizacion) { this.fechaFinalizacion = fechaFinalizacion; }

    public double getMontoAlquiler() { return montoAlquiler; }
    public void setMontoAlquiler(double montoAlquiler) { this.montoAlquiler = montoAlquiler; }

    public boolean isEstado() { return estado; }
    public void setEstado(boolean estado) { this.estado = estado; }

    public int getIdInquilino() { return idInquilino; }
    public void setIdInquilino(int idInquilino) { this.idInquilino = idInquilino; }

    public int getIdInmueble() { return idInmueble; }
    public void setIdInmueble(int idInmueble) { this.idInmueble = idInmueble; }

    public Inquilino getInquilino() { return inquilino; }
    public void setInquilino(Inquilino inquilino) { this.inquilino = inquilino; }

    public Inmueble getInmueble() { return inmueble; }
    public void setInmueble(Inmueble inmueble) { this.inmueble = inmueble; }
}
