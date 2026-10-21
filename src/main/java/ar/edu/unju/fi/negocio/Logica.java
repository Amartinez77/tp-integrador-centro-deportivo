package ar.edu.unju.fi.negocio;

import ar.edu.unju.fi.datos.AccesoDatos;
import ar.edu.unju.fi.datos.excepciones.PersistenciaException;
import ar.edu.unju.fi.negocio.excepciones.DatoInvalidoException;
import ar.edu.unju.fi.negocio.excepciones.SocioDuplicadoException;
import ar.edu.unju.fi.negocio.modelo.Socio;

import java.util.*;

public class Logica {

    // Mapa de socios indexados por DNI
    private Map<String, Socio> socios;
    private AccesoDatos accesoDatos;

    public Logica(AccesoDatos accesoDatos) {
        // fix s2-02
        if (accesoDatos == null) {
            throw new IllegalArgumentException("AccesoDatos no puede ser nulo en la composición del sistema");
        }
        this.accesoDatos = accesoDatos;
        socios = new HashMap<>();
    }

    // Registra un socio y controla que no se repita el DNI

    /**
     * Registra un nuevo socio verificando unicidad por DNI en 0
     * @param socio Instancia de socio a registrar
     * @throws DatoInvalidoException  Si el socio o su DNI vienen nulos o vacios
     * @throws SocioDuplicadoException  Si ya existe un socio registrado con ese DNI
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



    // Carga los socios guardados en el archivo

    /**
     * Carga los socios desde el archivo serializado Socios.txt hacia el Map en memoria
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

}
