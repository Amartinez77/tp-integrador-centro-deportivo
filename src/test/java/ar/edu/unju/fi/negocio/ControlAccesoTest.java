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
    void socioHabilitadoPuedeIngresar() throws Exception {
        assertTrue(
                molinete.validarAcceso(
                        "30123456", true, false));
    }

    @Test
    void dniInexistenteRechazaIngreso() {
        assertThrows(
                AccesoDenegadoException.class,
                () -> molinete.validarAcceso(
                        "99999999", true, false));
    }

    @Test
    void socioInactivoRechazaIngreso() throws Exception {
        Socio socio = logica.buscarSocio("30123456");
        socio.setActivo(false);

        assertThrows(
                AccesoDenegadoException.class,
                () -> molinete.validarAcceso(
                        "30123456", true, false));
    }

    @Test
    void socioConCuotasVencidasRechazaIngreso() {
        assertThrows(
                AccesoDenegadoException.class,
                () -> molinete.validarAcceso(
                        "30123456", false, false));
    }

    @Test
    void socioConSancionRechazaIngreso() {
        assertThrows(
                AccesoDenegadoException.class,
                () -> molinete.validarAcceso(
                        "30123456", true, true));
    }

    @Test
    void dniVacioRechazaIngreso() {
        assertThrows(
                AccesoDenegadoException.class,
                () -> molinete.validarAcceso(
                        " ", true, false));
    }
}