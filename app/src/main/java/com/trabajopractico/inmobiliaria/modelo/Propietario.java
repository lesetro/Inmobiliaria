package com.trabajopractico.inmobiliaria.modelo;

import java.io.Serializable;

// Modelo Propietario.
// Los nombres de los campos coinciden EXACTAMENTE con el JSON que devuelve el back:
//   { idPropietario, nombre, apellido, dni, telefono, email, clave }
// Implementa Serializable para poder pasarse entre fragments via Bundle si hace falta.
public class Propietario implements Serializable {

    private int idPropietario;
    private String nombre;
    private String apellido;
    private String dni;
    private String telefono;
    private String email;
    // El back devuelve la clave hasheada.

    private String clave;

    public Propietario() { }

    public Propietario(int idPropietario, String nombre, String apellido, String dni,
                       String telefono, String email) {
        this.idPropietario = idPropietario;
        this.nombre = nombre;
        this.apellido = apellido;
        this.dni = dni;
        this.telefono = telefono;
        this.email = email;
    }

    public int getIdPropietario() { return idPropietario; }
    public void setIdPropietario(int idPropietario) { this.idPropietario = idPropietario; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getClave() { return clave; }
    public void setClave(String clave) { this.clave = clave; }

    @Override
    public String toString() {
        return apellido + ", " + nombre;
    }
}
