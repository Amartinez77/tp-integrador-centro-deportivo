package ar.edu.unju.fi.negocio;

import ar.edu.unju.fi.datos.AccesoDatos;
import ar.edu.unju.fi.datos.excepciones.PersistenciaException;
import ar.edu.unju.fi.negocio.excepciones.DatoInvalidoException;
import ar.edu.unju.fi.negocio.excepciones.NegocioException;
import ar.edu.unju.fi.negocio.excepciones.ReservaDuplicadaException;
import ar.edu.unju.fi.negocio.modelo.Reserva;
import ar.edu.unju.fi.negocio.modelo.Socio;
import ar.edu.unju.fi.negocio.modelo.SocioEstandar;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ReservasTest {

    private Logica logica;
    private AccesoDatos accesoDatos;

    @BeforeEach
    void setUp() throws Exception {
        accesoDatos = new AccesoDatos();
        logica = new Logica(accesoDatos);

        limpiarArchivos();

        // Socio activo base
        Socio socioActivo = new SocioEstandar("30123456", "Lucas", "Gomez", 12000.0);
        logica.registrarSocio(socioActivo);

        // Segundo socio activo
        Socio otroSocio = new SocioEstandar("35987654", "Ana", "Martinez", 12000.0);
        logica.registrarSocio(otroSocio);

        // Socio inactivo para pruebas de restricción
        Socio socioInactivo = new SocioEstandar("28111222", "Carlos", "Perez", 10000.0);
        socioInactivo.setActivo(false);
        logica.registrarSocio(socioInactivo);
    }

    @AfterEach
    void tearDown() {
        limpiarArchivos();
    }

    private void limpiarArchivos() {
        File file = new File(AccesoDatos.ARCHIVO_RESERVAS);
        if (file.exists()) {
            file.delete();
        }
    }

    @Test
    void registrarReserva_datosValidos_debeInscribirCorrectamente() throws NegocioException {
        LocalDateTime horario = LocalDateTime.of(2026, 11, 10, 18, 0);

        Reserva reserva = logica.registrarReserva("RES-01", "NATACION", "30123456", horario);

        assertNotNull(reserva);
        Set<Reserva> reservasNatacion = logica.obtenerReservasPorActividad("NATACION");
        assertEquals(1, reservasNatacion.size());
        assertTrue(reservasNatacion.contains(reserva));
    }

    @Test
    void registrarReserva_mismoSocioMismaClaseMismoHorario_debeLanzarReservaDuplicadaException() throws NegocioException {
        LocalDateTime horario = LocalDateTime.of(2026, 11, 10, 18, 0);

        logica.registrarReserva("RES-01", "SPINNING", "30123456", horario);

        assertThrows(ReservaDuplicadaException.class, () -> {
            logica.registrarReserva("RES-02", "SPINNING", "30123456", horario);
        });
    }

    @Test
    void registrarReserva_mismoSocioDiferenteHorario_debePermitirAmbasReservas() throws NegocioException {
        LocalDateTime horario1 = LocalDateTime.of(2026, 11, 10, 18, 0);
        LocalDateTime horario2 = LocalDateTime.of(2026, 11, 11, 18, 0);

        Reserva res1 = logica.registrarReserva("RES-01", "SPINNING", "30123456", horario1);
        Reserva res2 = logica.registrarReserva("RES-02", "SPINNING", "30123456", horario2);

        Set<Reserva> reservas = logica.obtenerReservasPorActividad("SPINNING");
        assertEquals(2, reservas.size());
        assertTrue(reservas.contains(res1));
        assertTrue(reservas.contains(res2));
    }

    @Test
    void registrarReserva_dosSociosMismaClaseMismoHorario_debePermitirAmbos() throws NegocioException {
        LocalDateTime horario = LocalDateTime.of(2026, 11, 10, 18, 0);

        Reserva res1 = logica.registrarReserva("RES-01", "BOXEO", "30123456", horario);
        Reserva res2 = logica.registrarReserva("RES-02", "BOXEO", "35987654", horario);

        Set<Reserva> reservas = logica.obtenerReservasPorActividad("BOXEO");
        assertEquals(2, reservas.size());
        assertTrue(reservas.contains(res1));
        assertTrue(reservas.contains(res2));
    }

    @Test
    void registrarReserva_socioInactivo_debeLanzarDatoInvalidoException() {
        LocalDateTime horario = LocalDateTime.of(2026, 11, 10, 18, 0);

        assertThrows(DatoInvalidoException.class, () -> {
            logica.registrarReserva("RES-99", "NATACION", "28111222", horario);
        });
    }

    @Test
    void registrarReserva_dniInexistente_debeLanzarDatoInvalidoException() {
        LocalDateTime horario = LocalDateTime.of(2026, 11, 10, 18, 0);

        assertThrows(DatoInvalidoException.class, () -> {
            logica.registrarReserva("RES-99", "NATACION", "99999999", horario);
        });
    }

    @Test
    void guardarYCargarReservas_debePreservarMapaAnidado() throws NegocioException, PersistenciaException {
        LocalDateTime horario = LocalDateTime.of(2026, 11, 12, 10, 0);
        logica.registrarReserva("RES-01", "TENIS", "30123456", horario);

        logica.guardarReservas();

        Logica nuevaLogica = new Logica(accesoDatos);
        nuevaLogica.cargarReservas();

        Set<Reserva> reservasTenis = nuevaLogica.obtenerReservasPorActividad("TENIS");
        assertEquals(1, reservasTenis.size());
    }
}