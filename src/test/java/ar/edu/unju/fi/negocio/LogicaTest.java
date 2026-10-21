package ar.edu.unju.fi.negocio;

import ar.edu.unju.fi.datos.AccesoDatos;
import ar.edu.unju.fi.datos.excepciones.PersistenciaException;
import ar.edu.unju.fi.negocio.excepciones.DatoInvalidoException;
import ar.edu.unju.fi.negocio.excepciones.NegocioException;
import ar.edu.unju.fi.negocio.excepciones.SocioDuplicadoException;
import ar.edu.unju.fi.negocio.modelo.Socio;
import ar.edu.unju.fi.negocio.modelo.SocioEstandar;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

public class LogicaTest {
    private Logica logica;
    private AccesoDatos accesoDatos;

    @BeforeEach
    void setUp() {
        accesoDatos = new AccesoDatos();
        logica = new Logica(accesoDatos);

        // Limpiamos el archivo de socios por si quedó de pruebas previas
        File archivo = new File(AccesoDatos.ARCHIVO_SOCIOS);
        if (archivo.exists()) {
            archivo.delete();
        }
    }

    @Test
    void registrarSocio_socioValido_debeRegistrarloCorrectamente() throws NegocioException {
        Socio socio = new SocioEstandar("12345678", "Juan", "Perez", 15000.0);

        logica.registrarSocio(socio);

        assertTrue(logica.existeSocio("12345678"));
        assertEquals(socio, logica.buscarSocio("12345678"));
        assertEquals(1, logica.obtenerTodosLosSocios().size());
    }

    @Test
    void registrarSocio_dniDuplicado_debeLanzarSocioDuplicadoException() throws NegocioException {
        Socio socio1 = new SocioEstandar("12345678", "Juan", "Perez", 15000.0);
        Socio socio2 = new SocioEstandar("12345678", "Carlos", "Gomez", 15000.0);

        logica.registrarSocio(socio1);

        // Verificamos que al reinsertar el mismo DNI se dispare la excepción de negocio esperada
        assertThrows(SocioDuplicadoException.class, () -> {
            logica.registrarSocio(socio2);
        });
    }

    @Test
    void registrarSocio_datosNulosOVacios_debeLanzarDatoInvalidoException() {
        // Socio con DNI nulo o vacío
        Socio socioInvalido = new SocioEstandar("", "Sin", "Dni", 10000.0);

        assertThrows(DatoInvalidoException.class, () -> {
            logica.registrarSocio(socioInvalido);
        });

        assertThrows(DatoInvalidoException.class, () -> {
            logica.registrarSocio(null);
        });
    }

    @Test
    void guardarYCargarSocios_cicloCompleto_debePreservarDatos() throws NegocioException, PersistenciaException {
        Socio socio = new SocioEstandar("99999999", "Maria", "Lopez", 20000.0);
        logica.registrarSocio(socio);

        // Persistimos en disco
        logica.guardarSocios();

        // Creamos una nueva instancia de Logica para simular un reinicio del sistema
        Logica nuevaInstanciaLogica = new Logica(accesoDatos);
        nuevaInstanciaLogica.cargarSocios();

        assertTrue(nuevaInstanciaLogica.existeSocio("99999999"));
        assertEquals("Maria", nuevaInstanciaLogica.buscarSocio("99999999").getNombre());
    }
}
