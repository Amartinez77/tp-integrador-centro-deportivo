package ar.edu.unju.fi.negocio.modelo;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class Reserva implements Serializable {

    private static final long serialVersionUID = 1L;

    private String idReserva;
    private String codigoActividad;
    private Socio socio;
    private LocalDateTime fechaHora;

    public Reserva(String idReserva, String codigoActividad, Socio socio, LocalDateTime fechaHora) {

        if (idReserva == null || idReserva.isBlank()) {
            throw new IllegalArgumentException("El ID de la reserva es obligatorio");
        }

        if (codigoActividad == null || codigoActividad.isEmpty()) {
            throw new IllegalArgumentException("El codigo de actividad es obligatorio");
        }

        if (socio == null) {
            throw new IllegalArgumentException("El socio es obligatorio");
        }

        if (fechaHora == null) {
            throw new IllegalArgumentException("La fecha y hora son obligatorias");
        }

        this.idReserva = idReserva.trim();
        this.codigoActividad = codigoActividad.trim().toUpperCase();
        this.socio = socio;
        this.fechaHora = fechaHora;


    }

    public String getIdReserva() {
        return idReserva;
    }

    public void setIdReserva(String idReserva) {
        this.idReserva = idReserva;
    }

    public String getCodigoActividad() {
        return codigoActividad;
    }

    public void setCodigoActividad(String codigoActividad) {
        this.codigoActividad = codigoActividad;
    }

    public Socio getSocio() {
        return socio;
    }

    public void setSocio(Socio socio) {
        this.socio = socio;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    /**
     * Sobrescritura obligatoria de equals():
     * Dos reservas se consideran idénticas si pertenecen al mismo socio,
     * en la misma actividad y en el mismo horario exacto.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Reserva reserva)) return false;
        return Objects.equals(codigoActividad, reserva.codigoActividad) &&
                Objects.equals(socio.getDni(), reserva.socio.getDni()) &&
                Objects.equals(fechaHora, reserva.fechaHora);
    }

    /**
     * Sobrescritura obligatoria de hashCode():
     * Basado estrictamente en los mismos atributos de identidad que equals().
     */
    @Override
    public int hashCode() {
        return Objects.hash(codigoActividad, socio.getDni(), fechaHora);
    }

    @Override
    public String toString() {
        return "Reserva{" +
                "id='" + idReserva + '\'' +
                ", actividad='" + codigoActividad + '\'' +
                ", socio=" + socio.getApellido() + " (" + socio.getDni() + ")" +
                ", fechaHora=" + fechaHora +
                '}';
    }

}
