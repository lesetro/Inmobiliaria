package com.trabajopractico.inmobiliaria.modelo;

import java.io.Serializable;

// Modelo Inmueble.
//   { idInmueble, direccion, uso, tipo, ambientes, superficie, latitud, valor,
//     imagen, disponible, longitud, idPropietario, duenio, tieneContratoVigente }
//

public class Inmueble implements Serializable {

    private int idInmueble;
    private String direccion;
    private String uso;
    private String tipo;
    private int ambientes;
    private int superficie;
    private double latitud;
    private double longitud;
    private double valor;
    private String imagen;
    private boolean disponible;
    private int idPropietario;
    private Propietario duenio;
    private boolean tieneContratoVigente;

    public Inmueble() { }

    public int getIdInmueble() { return idInmueble; }
    public void setIdInmueble(int idInmueble) { this.idInmueble = idInmueble; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getUso() { return uso; }
    public void setUso(String uso) { this.uso = uso; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public int getAmbientes() { return ambientes; }
    public void setAmbientes(int ambientes) { this.ambientes = ambientes; }

    public int getSuperficie() { return superficie; }
    public void setSuperficie(int superficie) { this.superficie = superficie; }

    public double getLatitud() { return latitud; }
    public void setLatitud(double latitud) { this.latitud = latitud; }

    public double getLongitud() { return longitud; }
    public void setLongitud(double longitud) { this.longitud = longitud; }

    public double getValor() { return valor; }
    public void setValor(double valor) { this.valor = valor; }

    public String getImagen() { return imagen; }
    public void setImagen(String imagen) { this.imagen = imagen; }

    public boolean isDisponible() { return disponible; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }

    public int getIdPropietario() { return idPropietario; }
    public void setIdPropietario(int idPropietario) { this.idPropietario = idPropietario; }

    public Propietario getDuenio() { return duenio; }
    public void setDuenio(Propietario duenio) { this.duenio = duenio; }

    public boolean isTieneContratoVigente() { return tieneContratoVigente; }
    public void setTieneContratoVigente(boolean tieneContratoVigente) {
        this.tieneContratoVigente = tieneContratoVigente;
    }
}
