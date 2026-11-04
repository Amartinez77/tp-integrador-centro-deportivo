package ar.edu.unju.fi.negocio.modelo;

public class ActividadReporte {

    private final String codigoActividad;
    private final int cantidadReservas;

    public ActividadReporte(String codigoActividad, int cantidadReservas) {
        this.codigoActividad = codigoActividad;
        this.cantidadReservas = cantidadReservas;
    }

    public String getCodigoActividad() {
        return codigoActividad;
    }

    public int getCantidadReservas() {
        return cantidadReservas;
    }

    @Override
    public String toString() {
        return codigoActividad + " - Reservas: " + cantidadReservas;
    }
}