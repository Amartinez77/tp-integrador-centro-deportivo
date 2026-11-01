package ar.edu.unju.fi.negocio.servicios;

import ar.edu.unju.fi.datos.AccesoDatos;
import ar.edu.unju.fi.datos.excepciones.PersistenciaException;
import ar.edu.unju.fi.negocio.excepciones.DatoInvalidoException;
import ar.edu.unju.fi.negocio.modelo.Alquiler;
import ar.edu.unju.fi.negocio.modelo.Equipamiento;
import ar.edu.unju.fi.negocio.modelo.Socio;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EquipamientoService {

    private List<Equipamiento> equipamientos;
    private List<Alquiler> alquileres;
    private final AccesoDatos accesoDatos;


    public EquipamientoService(AccesoDatos accesoDatos) {
        if (accesoDatos == null) {
            throw new IllegalArgumentException("AccesoDatos no puede ser nulo");
        }
        this.accesoDatos = accesoDatos;
        equipamientos = new ArrayList<>();
        alquileres = new ArrayList<>();
    }

    // ==========================================
    // INVENTARIO DE EQUIPAMIENTO
    // ==========================================

    public void registrarEquipamiento(Equipamiento equipamiento) throws DatoInvalidoException {

        if (equipamiento == null || equipamiento.getCodigo() == null  || equipamiento.getCodigo().isBlank()) {
            throw new DatoInvalidoException("El equipamiento y su codigo son datos obligatorios");
        }
        equipamientos.add(equipamiento);

    }

    public Equipamiento buscarEquipamiento(String codigo) {
        if (codigo == null || codigo.isBlank()) return null;
        for (Equipamiento eq : equipamientos) {
            if (eq.getCodigo().equalsIgnoreCase(codigo.trim())) {
                return eq;
            }
        }
        return null;
    }

    public List<Equipamiento> obtenerTodosLosEquipamientos() {
        return Collections.unmodifiableList(new ArrayList<>(equipamientos));
    }

    // ==========================================
    // GESTIÓN DE ALQUILERES Y DEVOLUCIONES
    // ==========================================

    public Alquiler alquilarEquipamiento(String idAlquiler, Socio socio, String codigoEquipamiento, int diasPrestamo)
            throws DatoInvalidoException {

        if (idAlquiler == null || idAlquiler.isBlank()) {
            throw new DatoInvalidoException("El ID del alquiler es obligatorio.");
        }
        if (buscarAlquiler(idAlquiler) != null) {
            throw new DatoInvalidoException("Ya existe un alquiler con el ID: " + idAlquiler);
        }
        if (socio == null) {
            throw new DatoInvalidoException("No existe un socio registrado con el DNI especificado.");
        }
        if (!socio.isActivo()) {
            throw new DatoInvalidoException("El socio se encuentra inactivo y no puede realizar alquileres.");
        }
        if (diasPrestamo <= 0) {
            throw new DatoInvalidoException("La cantidad de días de préstamo debe ser mayor a cero.");
        }

        Equipamiento equipo = buscarEquipamiento(codigoEquipamiento);
        if (equipo == null) {
            throw new DatoInvalidoException("No existe el equipamiento solicitado con código: " + codigoEquipamiento);
        }
        if (!equipo.isDisponible()) {
            throw new DatoInvalidoException("El equipamiento '" + equipo.getNombre() + "' se encuentra actualmente en uso.");
        }

        Alquiler alquiler = new Alquiler(idAlquiler, socio, equipo, LocalDate.now(), diasPrestamo);
        alquileres.add(alquiler);
        return alquiler;
    }

    public double devolverEquipamiento(String idAlquiler, LocalDate fechaDevolucion, double tarifaMoraDiaria)
            throws DatoInvalidoException {

        Alquiler alquiler = buscarAlquiler(idAlquiler);
        if (alquiler == null) {
            throw new DatoInvalidoException("No se encontró el registro de alquiler con ID: " + idAlquiler);
        }
        if (alquiler.isDevuelto()) {
            throw new DatoInvalidoException("Este alquiler ya fue devuelto previamente.");
        }

        return alquiler.registrarDevolucion(fechaDevolucion, tarifaMoraDiaria);
    }

    public Alquiler buscarAlquiler(String idAlquiler) {
        if (idAlquiler == null || idAlquiler.isBlank()) return null;
        for (Alquiler alq : alquileres) {
            if (alq.getIdAlquiler().equalsIgnoreCase(idAlquiler.trim())) {
                return alq;
            }
        }
        return null;
    }

    public List<Alquiler> obtenerTodosLosAlquileres() {
        return Collections.unmodifiableList(new ArrayList<>(alquileres));
    }

    /**
     * Retorna los alquileres de un socio específico que posean mora acumulada pendiente de facturación.
     * Sprint3
     */
    public List<Alquiler> obtenerAlquileresConMoraPorSocio(String dniSocio) {
        if (dniSocio == null || dniSocio.isBlank()) return Collections.emptyList();
        List<Alquiler> conMora = new ArrayList<>();
        for (Alquiler alq : alquileres) {
            if (alq.getSocio().getDni().equalsIgnoreCase(dniSocio.trim()) && alq.getRecargoPorMora() > 0) {
                conMora.add(alq);
            }
        }
        return Collections.unmodifiableList(conMora);
    }

    // ==========================================
    // PERSISTENCIA EN Equipamiento.txt
    // ==========================================

    /**
     * Persiste tanto el inventario como el histórico de alquileres en Equipamiento.txt
     * respetando el límite de 4 archivos en disco fijado por la cátedra.
     */
    public void guardarEquipamientosYAlquileres() throws PersistenciaException {
        File archivo = new File(AccesoDatos.ARCHIVO_EQUIPAMIENTO);
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(archivo))) {
            oos.writeObject(this.equipamientos);
            oos.writeObject(this.alquileres);
        } catch (IOException e) {
            throw new PersistenciaException("Error al guardar equipamientos y alquileres en: " + AccesoDatos.ARCHIVO_EQUIPAMIENTO, e);
        }
    }

    @SuppressWarnings("unchecked")
    public void cargarEquipamientosYAlquileres() throws PersistenciaException {
        File archivo = new File(AccesoDatos.ARCHIVO_EQUIPAMIENTO);
        if (!archivo.exists() || archivo.length() == 0) {
            this.equipamientos = new ArrayList<>();
            this.alquileres = new ArrayList<>();
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(archivo))) {
            Object objEquipos = ois.readObject();
            if (objEquipos instanceof List<?>) {
                this.equipamientos = (List<Equipamiento>) objEquipos;
            }

            // Si el archivo contiene el segundo stream con la lista de alquileres
            try {
                Object objAlquileres = ois.readObject();
                if (objAlquileres instanceof List<?>) {
                    this.alquileres = (List<Alquiler>) objAlquileres;
                }
            } catch (IOException e) {
                // Caso compatible si el archivo solo contenía equipamientos
                this.alquileres = new ArrayList<>();
            }

        } catch (ClassNotFoundException e) {
            throw new PersistenciaException("Error de compatibilidad al deserializar " + AccesoDatos.ARCHIVO_EQUIPAMIENTO, e);
        } catch (IOException e) {
            throw new PersistenciaException("Error de E/S al leer " + AccesoDatos.ARCHIVO_EQUIPAMIENTO, e);
        }
    }

}
