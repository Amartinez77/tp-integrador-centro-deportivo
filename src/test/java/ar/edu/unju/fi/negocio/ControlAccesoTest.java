
package ar.edu.unju.fi.negocio;

import ar.edu.unju.fi.datos.AccesoDatos;
import ar.edu.unju.fi.negocio.excepciones.AccesoDenegadoException;
import ar.edu.unju.fi.negocio.modelo.Socio;
import ar.edu.unju.fi.negocio.modelo.SocioEstandar;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ControlAccesoTest {

    private Logica logica;
    private ControlAcceso molinete;

    @BeforeEach
    void setUp() throws Exception {

        logica = new Logica(new AccesoDatos());

        Socio socio = new SocioEstandar(
                "30123456",
                "Lucas",
                "Gomez",
                12000.0);

        logica.registrarSocio(socio);

        molinete = new ControlAcceso(logica);
    }

    @Test
    void socioHabilitadoPuedeIngresar() throws AccesoDenegadoException {

        logica.registrarEstadoCuotas("30123456", true);

        assertTrue(molinete.validarAcceso("30123456"));
    }

    @Test
    void dniInexistenteRechazaIngreso() {

        assertThrows(AccesoDenegadoException.class,
                () -> molinete.validarAcceso("99999999"));
    }

    @Test
    void socioInactivoRechazaIngreso() {

        Socio socio = logica.buscarSocio("30123456");

        socio.setActivo(false);

        logica.registrarEstadoCuotas("30123456", true);

        assertThrows(AccesoDenegadoException.class,
                () -> molinete.validarAcceso("30123456"));
    }

    @Test
    void socioConCuotasVencidasRechazaIngreso() {

        logica.registrarEstadoCuotas("30123456", false);

        assertThrows(AccesoDenegadoException.class,
                () -> molinete.validarAcceso("30123456"));
    }

    @Test
    void socioSinInformacionDeCuotasRechazaIngreso() {

        assertThrows(AccesoDenegadoException.class,
                () -> molinete.validarAcceso("30123456"));
    }

    @Test
    void dniVacioRechazaIngreso() {

        assertThrows(AccesoDenegadoException.class,
                () -> molinete.validarAcceso(" "));
    }

    @Test
    void constructorRechazaLogicaNula() {

        assertThrows(IllegalArgumentException.class,
                () -> new ControlAcceso(null));
    }
}
