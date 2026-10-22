package ar.edu.unju.fi.negocio.modelo;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Alquiler implements Serializable {
    private static final long serialVersionUID = 1L;

    private String idAlquiler;
    private Socio socio;
    private Equipamiento equipamiento;
    private LocalDate fechaAlquiler;
    private LocalDate fechaLimiteDevolucion;
    private LocalDate fechaDevolucionReal;
    private double recargoPorMora;
    private boolean devuelto;

    public Alquiler(String idAlquiler, Socio socio, Equipamiento equipamiento, LocalDate fechaAlquiler, int diasPrestamo) {
        this.idAlquiler = idAlquiler;
        this.socio = socio;
        this.equipamiento = equipamiento;
        this.fechaAlquiler = fechaAlquiler;
        this.fechaLimiteDevolucion = fechaAlquiler.plusDays(diasPrestamo);
        this.devuelto = false;
        this.recargoPorMora = 0.0;
        this.equipamiento.setDisponible(false);
    }

    public double registrarDevolucion(LocalDate fechaDevolucion, double tarifaMoraDiaria) {
        this.fechaDevolucionReal = fechaDevolucion;
        this.devuelto = true;
        this.equipamiento.setDisponible(true);

        if (fechaDevolucion.isAfter(fechaLimiteDevolucion)) {
            long diasRetraso = ChronoUnit.DAYS.between(fechaLimiteDevolucion, fechaDevolucion);
            this.recargoPorMora = diasRetraso * tarifaMoraDiaria;
        } else {
            this.recargoPorMora = 0.0;
        }
        return this.recargoPorMora;
    }

    public String getIdAlquiler() {
        return idAlquiler;
    }

    public void setIdAlquiler(String idAlquiler) {
        this.idAlquiler = idAlquiler;
    }

    public Socio getSocio() {
        return socio;
    }

    public void setSocio(Socio socio) {
        this.socio = socio;
    }

    public Equipamiento getEquipamiento() {
        return equipamiento;
    }

    public void setEquipamiento(Equipamiento equipamiento) {
        this.equipamiento = equipamiento;
    }

    public LocalDate getFechaAlquiler() {
        return fechaAlquiler;
    }

    public void setFechaAlquiler(LocalDate fechaAlquiler) {
        this.fechaAlquiler = fechaAlquiler;
    }

    public LocalDate getFechaLimiteDevolucion() {
        return fechaLimiteDevolucion;
    }

    public void setFechaLimiteDevolucion(LocalDate fechaLimiteDevolucion) {
        this.fechaLimiteDevolucion = fechaLimiteDevolucion;
    }

    public LocalDate getFechaDevolucionReal() {
        return fechaDevolucionReal;
    }

    public void setFechaDevolucionReal(LocalDate fechaDevolucionReal) {
        this.fechaDevolucionReal = fechaDevolucionReal;
    }

    public double getRecargoPorMora() {
        return recargoPorMora;
    }

    public void setRecargoPorMora(double recargoPorMora) {
        this.recargoPorMora = recargoPorMora;
    }

    public boolean isDevuelto() {
        return devuelto;
    }

    public void setDevuelto(boolean devuelto) {
        this.devuelto = devuelto;
    }

    @Override
    public String toString() {
        return "Alquiler{" +
                "idAlquiler='" + idAlquiler + '\'' +
                ", socio=" + socio +
                ", equipamiento=" + equipamiento +
                ", fechaLimiteDevolucion=" + fechaLimiteDevolucion +
                ", devuelto=" + (devuelto ? "SI" : "NO") +
                '}';
    }
}
