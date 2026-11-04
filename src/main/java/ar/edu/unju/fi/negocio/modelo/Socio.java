package ar.edu.unju.fi.negocio.modelo;

import java.io.Serializable;
import java.util.*;

public abstract class Socio implements Comparable<Socio>, Serializable {
    private static final long serialVersionUID = 1L;
    private String dni;
    private String nombre;
    private String apellido;
    private boolean activo;
    private double cuotaBase;

    private Map<String, List<Factura>> historialFacturas = new HashMap<>();

    public Socio(String dni, String nombre, String apellido, double cuotaBase) {
        this.dni = dni;
        this.nombre = nombre;
        this.apellido = apellido;
        this.activo = true;
        this.cuotaBase = cuotaBase;
    }

    public abstract double calcularCuotaMensual();

    /**
     * Agrega una factura al periodo fiscal correspondiente al mapa anidado
     */
    public void agregarFactura(Factura factura) {
        String periodo = factura.getPeriodoFiscal();
        historialFacturas.putIfAbsent(periodo, new ArrayList<>());
        historialFacturas.get(periodo).add(factura);
    }

    /**
     * Retorna las facturas emitidas para un periodo fiscal especifico
     */

    public List<Factura> getFacturaPorPeriodo(String periodoFiscal) {
        return historialFacturas.getOrDefault(periodoFiscal, new ArrayList<>());
    }

    /**
     * Retorna la coleccion completa de facturas agrupadas por periodo
     */
    public Map<String, List<Factura>> getHistorialFacturas() {
        return historialFacturas;
    }

    // getters y setters

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

    // equals() y hashCode()
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Socio socio = (Socio) o;
        return Objects.equals(dni, socio.dni);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dni);
    }

    @Override
    public int compareTo(Socio otro) {
        int resultado = this.getApellido()
                .compareToIgnoreCase(otro.getApellido());

        if (resultado == 0) {
            resultado = this.getNombre()
                    .compareToIgnoreCase(otro.getNombre());
        }

        return resultado;
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
