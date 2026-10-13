package ar.edu.unju.fi.datos;

import ar.edu.unju.fi.datos.excepciones.PersistenciaException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AccesoDatosTest {

    private static final String ARCHIVO_TEST = "datos/test_dummy.txt";
    private AccesoDatos accesoDatos;

    // Objeto ficticio para comprobar serialización
    static class DummySocio implements Serializable {
        private static final long serialVersionUID = 1L;
        private final String dni;
        private final String nombre;

        public DummySocio(String dni, String nombre) {
            this.dni = dni;
            this.nombre = nombre;
        }

        public String getDni() { return dni; }
    }

    @BeforeEach
    void setUp() {
        accesoDatos = new AccesoDatos();
        limpiarArchivoTest();
    }

    @AfterEach
    void tearDown() {
        limpiarArchivoTest();
    }

    private void limpiarArchivoTest() {
        File file = new File(ARCHIVO_TEST);
        if (file.exists()) {
            file.delete();
        }
    }

    @Test
    void leer_archivoInexistente_debeRetornarListaVacia() throws PersistenciaException {
        List<DummySocio> resultado = accesoDatos.leer(ARCHIVO_TEST);
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void guardarYLeer_elementosValidos_debeRecuperarCorrectamente() throws PersistenciaException {
        List<DummySocio> listaGuardar = new ArrayList<>();
        listaGuardar.add(new DummySocio("40111222", "Carlos Tevez"));
        listaGuardar.add(new DummySocio("38555666", "Lionel Messi"));

        // Guardamos
        accesoDatos.guardar(ARCHIVO_TEST, listaGuardar);

        // Leemos
        List<DummySocio> listaLeida = accesoDatos.leer(ARCHIVO_TEST);

        assertEquals(2, listaLeida.size());
        assertEquals("40111222", listaLeida.get(0).getDni());
        assertEquals("38555666", listaLeida.get(1).getDni());
    }
}
