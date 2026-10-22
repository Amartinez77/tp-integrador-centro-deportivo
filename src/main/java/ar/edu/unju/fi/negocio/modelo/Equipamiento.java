package ar.edu.unju.fi.negocio.modelo;

import java.io.Serializable;

public class Equipamiento implements Serializable {
    private static final long serialVersionUID = 1L;

    private String codigo;
    private String nombre;
    private double costoAlquiler;
    private boolean disponible;

    public Equipamiento(String codigo, String nombre, double costoAlquiler) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.costoAlquiler = costoAlquiler;
        this.disponible = true;
    }

    //Getters y Setters
    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getCostoAlquiler() {
        return costoAlquiler;
    }

    public void setCostoAlquiler(double costoAlquiler) {
        this.costoAlquiler = costoAlquiler;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

    //toString

    @Override
    public String toString() {
        return "Equipamiento{" +
                "codigo='" + codigo + '\'' +
                ", nombre='" + nombre + '\'' +
                ", costoAlquiler=" + costoAlquiler +
                ", disponible=" + disponible +
                '}';
    }
}
