package ar.edu.unju.fi.negocio.servicios;


import ar.edu.unju.fi.datos.AccesoDatos;
import ar.edu.unju.fi.datos.excepciones.PersistenciaException;
import ar.edu.unju.fi.negocio.excepciones.DatoInvalidoException;
import ar.edu.unju.fi.negocio.excepciones.SocioDuplicadoException;
import ar.edu.unju.fi.negocio.modelo.Socio;

import java.util.*;

public class SocioService {

    private final Map<String, Socio> socios;
    private final AccesoDatos accesoDatos;


    public SocioService(AccesoDatos accesoDatos) {

        if  (accesoDatos == null) {
            throw new IllegalArgumentException("accesoDatos no puede ser nulo");
        }
        this.accesoDatos = accesoDatos;
        this.socios = new HashMap<>();

    }

    public void registrarSocio(Socio socio) throws SocioDuplicadoException, DatoInvalidoException {
        if (socio == null || socio.getDni() == null || socio.getDni().isBlank()) {
            throw new DatoInvalidoException("El socio y su nro  de DNI son datos obligatorios");
        }

        String dni = socio.getDni().trim();
        if (socios.containsKey(dni)) {
            throw new SocioDuplicadoException("Ya existe un socio con ese DNI");
        }
        socios.put(dni, socio);

    }

    public Socio buscarSocio(String dni) {
        if (dni == null || dni.isBlank()) {
            return null;
        }
        return socios.get(dni.trim());
    }

    public boolean existeSocio(String dni) {
        if (dni == null || dni.isBlank()) {
            return false;
        }
        return socios.containsKey(dni.trim());
    }

    public List<Socio> obtenerTodosLosSocios() {
        return Collections.unmodifiableList(new ArrayList<>(socios.values()));
    }

    public void cargarSocios() throws PersistenciaException {
        List<Socio> lista = accesoDatos.leer(AccesoDatos.ARCHIVO_SOCIOS);
        socios.clear();
        for (Socio socio : lista) {
            socios.put(socio.getDni(), socio);
        }
    }

    public void guardarSocios() throws PersistenciaException {
        List<Socio> lista = new ArrayList<>(socios.values());
        accesoDatos.guardar(AccesoDatos.ARCHIVO_SOCIOS, lista);
    }

    public Map<String, Socio> getMapaSocios() {
        return Collections.unmodifiableMap(socios);
    }

}
