package ar.edu.unju.fi.negocio.servicios;

import ar.edu.unju.fi.datos.AccesoDatos;
import ar.edu.unju.fi.datos.excepciones.PersistenciaException;
import ar.edu.unju.fi.negocio.excepciones.DatoInvalidoException;
import ar.edu.unju.fi.negocio.excepciones.ReservaDuplicadaException;
import ar.edu.unju.fi.negocio.modelo.Reserva;
import ar.edu.unju.fi.negocio.modelo.Socio;

import java.time.LocalDateTime;
import java.util.*;

public class ReservaService {

    private Map<String, Set<Reserva>> reservasPorActividad;
    private final AccesoDatos accesoDatos;

    public ReservaService(AccesoDatos accesoDatos) {
        if (accesoDatos == null) {
            throw new IllegalArgumentException("AccesoDatos no puede ser nulo.");
        }
        this.accesoDatos = accesoDatos;
        this.reservasPorActividad = new HashMap<>();
    }

    public Reserva registrarReserva(String idReserva, String codigoActividad, Socio socio, LocalDateTime fechaHora)
            throws DatoInvalidoException, ReservaDuplicadaException {

        if (idReserva == null || idReserva.isBlank()) {
            throw new DatoInvalidoException("El ID de la reserva es obligatorio.");
        }
        if (codigoActividad == null || codigoActividad.isBlank()) {
            throw new DatoInvalidoException("El código de actividad es obligatorio.");
        }
        if (socio == null) {
            throw new DatoInvalidoException("El socio es obligatorio.");
        }
        if (!socio.isActivo()) {
            throw new DatoInvalidoException("El socio se encuentra inactivo y no puede realizar reservas.");
        }
        if (fechaHora == null) {
            throw new DatoInvalidoException("La fecha y hora son obligatorias.");
        }

        String claveActividad = codigoActividad.trim().toUpperCase();
        reservasPorActividad.putIfAbsent(claveActividad, new HashSet<>());
        Set<Reserva> conjuntoReservas = reservasPorActividad.get(claveActividad);

        Reserva nuevaReserva = new Reserva(idReserva, claveActividad, socio, fechaHora);

        boolean agregada = conjuntoReservas.add(nuevaReserva);
        if (!agregada) {
            throw new ReservaDuplicadaException("El socio " + socio.getApellido() +
                    " ya cuenta con una reserva activa para la actividad " + claveActividad +
                    " en el horario: " + fechaHora);
        }

        return nuevaReserva;
    }

    public Set<Reserva> obtenerReservasPorActividad(String codigoActividad) {
        if (codigoActividad == null || codigoActividad.isBlank()) {
            return Collections.emptySet();
        }
        Set<Reserva> reservas = reservasPorActividad.get(codigoActividad.trim().toUpperCase());
        if (reservas == null) {
            return Collections.emptySet();
        }
        return Collections.unmodifiableSet(reservas);
    }


    /**
     * Retorna una copia del mapa completo de reservas agrupadas por actividad.
     * Se copian también los conjuntos para no exponer las colecciones internas.
     */
    public Map<String, Set<Reserva>> obtenerTodasLasReservasPorActividad() {
        Map<String, Set<Reserva>> copia = new HashMap<>();

        for (Map.Entry<String, Set<Reserva>> entrada : reservasPorActividad.entrySet()) {
            copia.put(entrada.getKey(), new HashSet<>(entrada.getValue()));
        }

        return copia;
    }

    public void guardarReservas() throws PersistenciaException {
        accesoDatos.guardarMapaReservas(AccesoDatos.ARCHIVO_RESERVAS, this.reservasPorActividad);
    }

    public void cargarReservas() throws PersistenciaException {
        this.reservasPorActividad = accesoDatos.leerMapaReservas(AccesoDatos.ARCHIVO_RESERVAS);
        if (this.reservasPorActividad == null) {
            this.reservasPorActividad = new HashMap<>();
        }
    }

}
