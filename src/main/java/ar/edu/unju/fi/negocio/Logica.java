package ar.edu.unju.fi.negocio;

import ar.edu.unju.fi.datos.AccesoDatos;
import ar.edu.unju.fi.datos.excepciones.PersistenciaException;
import ar.edu.unju.fi.negocio.excepciones.DatoInvalidoException;
import ar.edu.unju.fi.negocio.excepciones.ReservaDuplicadaException;
import ar.edu.unju.fi.negocio.excepciones.SocioDuplicadoException;
import ar.edu.unju.fi.negocio.modelo.*;
import ar.edu.unju.fi.negocio.servicios.EquipamientoService;
import ar.edu.unju.fi.negocio.servicios.FacturaService;
import ar.edu.unju.fi.negocio.servicios.ReservaService;
import ar.edu.unju.fi.negocio.servicios.SocioService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Fachada (Facade) principal de la capa de Negocio.
 * Proporciona un punto de entrada unificado para la capa de Presentación,
 * delegando las operaciones en servicios especializados de dominio (SRP).
 */
public class Logica {

    private final FacturaService facturaService;
    private final SocioService socioService;
    private final EquipamientoService equipamientoService;
    private final ReservaService reservaService;

    public Logica(AccesoDatos accesoDatos) {
        if (accesoDatos == null) {
            throw new IllegalArgumentException("AccesoDatos no puede ser nulo en la composición del sistema");
        }
        this.socioService = new SocioService(accesoDatos);
        this.equipamientoService = new EquipamientoService(accesoDatos);
        this.reservaService = new ReservaService(accesoDatos);
        this.facturaService = new FacturaService(accesoDatos);
        ;
    }

    // ==========================================
    // DELEGACIÓN: MÓDULO DE SOCIOS
    // ==========================================

    public void registrarSocio(Socio socio) throws SocioDuplicadoException, DatoInvalidoException {
        socioService.registrarSocio(socio);
    }

    public Socio buscarSocio(String dni) {
        return socioService.buscarSocio(dni);
    }

    public boolean existeSocio(String dni) {
        return socioService.existeSocio(dni);
    }

    public List<Socio> obtenerTodosLosSocios() {
        return socioService.obtenerTodosLosSocios();
    }

    public void cargarSocios() throws PersistenciaException {
        socioService.cargarSocios();
    }

    public void guardarSocios() throws PersistenciaException {
        socioService.guardarSocios();
    }

    // ==========================================
    // DELEGACIÓN: MÓDULO DE FACTURACIÓN
    // ==========================================

    /**
     * Consulta si el socio tiene sus cuotas al día.
     */
    public boolean tieneCuotasAlDia(String dni) {
        return facturaService.tieneCuotasAlDia(dni);
    }

    /**
     * Registra el estado de cuotas del socio.
     */
    public void registrarEstadoCuotas(String dni, boolean alDia) {
        facturaService.registrarEstadoCuotas(dni, alDia);
    }

    /**
     * Emite la Factura mensual utilizando el calculo polimorfico de la cuota
     */
    public Factura emitirFacturaCuota(String numeroFactura, String dniSocio, String periodoFiscal) throws DatoInvalidoException {
        Socio socio = socioService.buscarSocio(dniSocio);
        if (socio == null) {
            throw new DatoInvalidoException("No existe Socio registrado con el DNI");
        }
        return facturaService.emitirFacturaCuota(numeroFactura, socio, periodoFiscal);
    }

    /**
     * Consulta el historial de facturas del socio para un periodo fiscal especifico
     */
    public List<Factura> obtenerFacturasDeSocioPorPeriodo(String dniSocio, String periodoFiscal){
        Socio socio = socioService.buscarSocio(dniSocio);
        if(socio==null) return new ArrayList<>();
        return socio.getFacturaPorPeriodo(periodoFiscal);
    }
    public List<Factura> obtenerTodasLasFacturas(){
        return facturaService.obtenerTodasLasFacturas();
    }
    public void cargarFacturas() throws PersistenciaException{
        facturaService.cargarFacturas(socioService.getMapaSocios());
    }
    public void guardarFacturas() throws PersistenciaException{
        facturaService.guardarFacturas();
    }


    // ==========================================
    // DELEGACIÓN: EQUIPAMIENTO Y ALQUILERES
    // ==========================================

    public void registrarEquipamiento(Equipamiento equipamiento) throws DatoInvalidoException {
        equipamientoService.registrarEquipamiento(equipamiento);
    }

    public Alquiler alquilarEquipamiento(String idAlquiler, String dniSocio, String codigoEquipamiento,
                                         int diasPrestamo)
            throws DatoInvalidoException {
        Socio socio = socioService.buscarSocio(dniSocio);
        if (socio == null) {
            throw new DatoInvalidoException("No existe un socio registrado con el DNI especificado");
        }
        return equipamientoService.alquilarEquipamiento(idAlquiler, socio, codigoEquipamiento, diasPrestamo);
    }

    public double devolverEquipamiento(String idAlquiler, LocalDate fechaDevolucion, double tarifaMoraDiaria)
            throws DatoInvalidoException {
        return equipamientoService.devolverEquipamiento(idAlquiler, fechaDevolucion, tarifaMoraDiaria);
    }

    public Equipamiento buscarEquipamiento(String codigo) {
        return equipamientoService.buscarEquipamiento(codigo);
    }

    public Alquiler buscarAlquiler(String idAlquiler) {
        return equipamientoService.buscarAlquiler(idAlquiler);
    }

    public List<Equipamiento> obtenerTodosLosEquipamientos() {
        return equipamientoService.obtenerTodosLosEquipamientos();
    }

    public List<Alquiler> obtenerTodosLosAlquileres() {
        return equipamientoService.obtenerTodosLosAlquileres();
    }

    public void cargarEquipamientos() throws PersistenciaException {
        equipamientoService.cargarEquipamientosYAlquileres();
    }

    public void guardarEquipamientos() throws PersistenciaException {
        equipamientoService.guardarEquipamientosYAlquileres();
    }

    public void cargarAlquileres() throws PersistenciaException {
        // Mantiene compatibilidad delegando al mismo archivo unificado
        equipamientoService.cargarEquipamientosYAlquileres();
    }

    public void guardarAlquileres() throws PersistenciaException {
        // Mantiene compatibilidad delegando al mismo archivo unificado
        equipamientoService.guardarEquipamientosYAlquileres();
    }

    // ==========================================
    // DELEGACIÓN: RESERVAS Y ACTIVIDADES
    // ==========================================

    public Reserva registrarReserva(String idReserva, String codigoActividad, String dniSocio, LocalDateTime fechaHora)
            throws DatoInvalidoException, ReservaDuplicadaException {

        if (dniSocio == null || dniSocio.isBlank()) {
            throw new DatoInvalidoException("El DNI del socio es obligatorio.");
        }
        Socio socio = socioService.buscarSocio(dniSocio);
        if (socio == null) {
            throw new DatoInvalidoException("No existe un socio registrado con el DNI: " + dniSocio);
        }

        return reservaService.registrarReserva(idReserva, codigoActividad, socio, fechaHora);
    }

    public Set<Reserva> obtenerReservasPorActividad(String codigoActividad) {
        return reservaService.obtenerReservasPorActividad(codigoActividad);
    }

    public void cargarReservas() throws PersistenciaException {
        reservaService.cargarReservas();
    }

    public void guardarReservas() throws PersistenciaException {
        reservaService.guardarReservas();
    }

}
