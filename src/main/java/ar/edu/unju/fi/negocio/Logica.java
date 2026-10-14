package ar.edu.unju.fi.negocio;

import ar.edu.unju.fi.datos.AccesoDatos;
import ar.edu.unju.fi.datos.excepciones.PersistenciaException;
import ar.edu.unju.fi.negocio.modelo.Socio;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Logica {

    // Mapa de socios indexados por DNI
    private Map<String, Socio> socios;
    private AccesoDatos accesoDatos;

    public Logica(AccesoDatos accesoDatos) {
        this.accesoDatos = accesoDatos;
        socios = new HashMap<>();
    }

    // Registra un socio y controla que no se repita el DNI
    public void registrarSocio(Socio socio) {
        if (socio == null || socio.getDni() == null
                || socio.getDni().isBlank()) {
            throw new IllegalArgumentException(
                    "El socio y su DNI son obligatorios.");
        }

        String dni = socio.getDni();

        if (socios.containsKey(dni)) {
            throw new IllegalArgumentException(
                    "Ya existe un socio con ese DNI.");
        }

        socios.put(dni, socio);
    }

    // Busca un socio mediante su DNI
    public Socio buscarSocio(String dni) {
        return socios.get(dni);
    }

    // Verifica si existe un socio con ese DNI
    public boolean existeSocio(String dni) {
        return socios.containsKey(dni);
    }

    // Carga los socios guardados en el archivo
    public void cargarSocios() throws PersistenciaException {
        List<Socio> lista =
                accesoDatos.leer(AccesoDatos.ARCHIVO_SOCIOS);

        socios.clear();

        for (Socio socio : lista) {
            socios.put(socio.getDni(), socio);
        }
    }
}
