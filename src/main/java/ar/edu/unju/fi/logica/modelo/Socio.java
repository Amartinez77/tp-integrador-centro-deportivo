package ar.edu.unju.fi.logica.modelo;

import java.io.Serializable;

public abstract class Socio implements Serializable {
    private static final long serialVersionUID = 1L;
    private String dni;
    private String nombre;
    private String apellido;
    private boolean activo;
    private double cuotaBase;

    public Socio(String dni, String nombre, String apellido, double cuotaBase) {
        this.dni = dni;
        this.nombre = nombre;
        this.apellido = apellido;
        this.activo=true;
        this.cuotaBase = cuotaBase;
    }

    public abstract double calcularCoutaMensual();

    //getters y setters

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public double getCuotaBase() {
        return cuotaBase;
    }

    public void setCuotaBase(double cuotaBase) {
        this.cuotaBase = cuotaBase;
    }

    // ToString


    @Override
    public String toString() {
        return "Socio{" +
                "apellido='" + apellido + '\'' +
                ", nombre='" + nombre + '\'' +
                ", dni='" + dni + '\'' +
                '}';
    }
}
