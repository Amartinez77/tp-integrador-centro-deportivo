package ar.edu.unju.fi.negocio;

import ar.edu.unju.fi.datos.AccesoDatos;
import ar.edu.unju.fi.datos.excepciones.PersistenciaException;
import ar.edu.unju.fi.negocio.excepciones.DatoInvalidoException;
import ar.edu.unju.fi.negocio.excepciones.SocioDuplicadoException;
import ar.edu.unju.fi.negocio.modelo.Alquiler;
import ar.edu.unju.fi.negocio.modelo.Equipamiento;
import ar.edu.unju.fi.negocio.modelo.Socio;

import java.time.LocalDate;
import java.util.*;

public class Logica {

    // Mapa de socios indexados por DNI
    private Map<String, Socio> socios;

    //Listas para Equipamientos y Alquileres
    private List<Equipamiento> equipamientos;
    private List<Alquiler> alquileres;

    private AccesoDatos accesoDatos;

    public Logica(AccesoDatos accesoDatos) {
        // fix s2-02
        if (accesoDatos == null) {
            throw new IllegalArgumentException("AccesoDatos no puede ser nulo en la composición del sistema");
        }
        this.accesoDatos = accesoDatos;
        socios = new HashMap<>();
        this.equipamientos = new ArrayList<>();
        this.alquileres = new ArrayList<>();

    }

    // Registra un socio y controla que no se repita el DNI

    /**
     * Registra un nuevo socio verificando unicidad por DNI en 0
     *
     * @param socio Instancia de socio a registrar
     * @throws DatoInvalidoException   Si el socio o su DNI vienen nulos o vacios
     * @throws SocioDuplicadoException Si ya existe un socio registrado con ese DNI
     */
    public void registrarSocio(Socio socio) throws SocioDuplicadoException, DatoInvalidoException {
        if (socio == null || socio.getDni() == null || socio.getDni().isBlank()) {
            throw new DatoInvalidoException("El socio y su número de DNI son datos obligatorios.");
        }

        String dni = socio.getDni();

        if (socios.containsKey(dni)) {
            throw new SocioDuplicadoException("Ya existe un socio con ese DNI.");
        }

        socios.put(dni, socio);
    }

    // Busca un socio mediante su DNI
    public Socio buscarSocio(String dni) {
        if (dni == null || dni.isBlank()) {
            return null;
        }
        return socios.get(dni.trim());
    }

    // Verifica si existe un socio con ese DNI
    public boolean existeSocio(String dni) {
        if (dni == null || dni.isBlank()) {
            return false;
        }
        return socios.containsKey(dni.trim());
    }

    /**
     * Retorna una vista inmodificable de los socios para proteger el encapsulamiento.
     *
     */
    public List<Socio> obtenerTodosLosSocios() {
        return Collections.unmodifiableList(new ArrayList<>(socios.values()));
    }


    //Modulo de Equipamientos y Alquileres TS2-01

    /**
     * Registra un nuevo elemento deportivo en el inventario
     */
    public void registrarEquipamiento(Equipamiento equipamiento) throws DatoInvalidoException {
        if (equipamiento == null || equipamiento.getCodigo() == null || equipamiento.getCodigo().isBlank() || equipamiento.getCodigo().isEmpty()) {
            throw new DatoInvalidoException("El equipamiento y su código son datos obligatorios.");
        }
        equipamientos.add(equipamiento);
    }

    /**
     * Registra un Alquiler validadando que el socio este activo y el equipo disponible
     */
    public Alquiler alquilarEquipamiento(String idAlquiler, String dniSocio, String codigoEquipamiento, int diasPrestamo) throws DatoInvalidoException {
        Socio socio = buscarSocio(dniSocio);
        if (socio == null) {
            throw new DatoInvalidoException("No existe un socio registrado con el DNI especificado");
        }
        if (!socio.isActivo()) {
            throw new IllegalStateException("El Socio se encuentra inactivo o bloqueado para realizar alquileres");
        }
        Equipamiento equipo = buscarEquipamiento(codigoEquipamiento);
        if (equipo == null) {
            throw new DatoInvalidoException("No existe el equipamiento solicitado");
        }
        if (!equipo.isDisponible()) {
            throw new IllegalStateException("El Equipamiento solicitado se encuentra actualmente en uso");
        }
        Alquiler alquiler = new Alquiler(idAlquiler, socio, equipo, LocalDate.now(), diasPrestamo);
        alquileres.add(alquiler);
        return alquiler;
    }

    public double devolverEquipamiento(String idAlquiler, LocalDate fechaDevolucion, double tarifaMoraDiaria) throws DatoInvalidoException {
        Alquiler alquiler = buscarAlquiler(idAlquiler);
        if (alquiler == null) {
            throw new DatoInvalidoException("No se encontro el registro de alquiler especificado");
        }
        if (alquiler.isDevuelto()) {
            throw new IllegalStateException("Este alquiler ya fue devuelto previamente");
        }
        return alquiler.registrarDevolucion(fechaDevolucion, tarifaMoraDiaria);

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

    public Alquiler buscarAlquiler(String idAlquiler) {
        if (idAlquiler == null || idAlquiler.isBlank()) return null;
        for (Alquiler alq : alquileres) {
            if (alq.getIdAlquiler().equalsIgnoreCase(idAlquiler.trim())) {
                return alq;
            }
        }
        return null;
    }

    public List<Equipamiento> obtenerTodosLosEquipamientos() {
        return Collections.unmodifiableList(new ArrayList<>(equipamientos));
    }

    public List<Alquiler> obtenerTodosLosAlquileres() {
        return Collections.unmodifiableList(new ArrayList<>(alquileres));
    }


    // Carga los socios guardados en el archivo

    /**
     * Carga los socios desde el archivo serializado Socios.txt hacia el Map en memoria
     *
     * @throws PersistenciaException
     */
    public void cargarSocios() throws PersistenciaException {
        List<Socio> lista =
                accesoDatos.leer(AccesoDatos.ARCHIVO_SOCIOS);

        socios.clear();

        for (Socio socio : lista) {
            socios.put(socio.getDni(), socio);
        }
    }

    /**
     * Persiste los socios del Map en memoria hacia el archivo serializado Socios.txt
     */
    public void guardarSocios() throws PersistenciaException {
        List<Socio> lista = new ArrayList<>(socios.values());
        accesoDatos.guardar(AccesoDatos.ARCHIVO_SOCIOS, lista);
    }

    public void cargarEquipamientos() throws PersistenciaException {
        equipamientos = accesoDatos.leer(AccesoDatos.ARCHIVO_EQUIPAMIENTO);
    }

    public void guardarEquipamientos() throws PersistenciaException {
        accesoDatos.guardar(AccesoDatos.ARCHIVO_EQUIPAMIENTO, equipamientos);
    }

    public void cargarAlquileres() throws PersistenciaException {
        // Asignamos a alquileres, no a equipamientos
        this.alquileres = accesoDatos.leer(AccesoDatos.ARCHIVO_EQUIPAMIENTO);
    }

    public void guardarAlquileres() throws PersistenciaException {
        // Persiste en Equipamientos.txt cumpliendo con lo de 4 archivos
        accesoDatos.guardar(AccesoDatos.ARCHIVO_EQUIPAMIENTO, this.alquileres);
    }




}
