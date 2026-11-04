package ar.edu.unju.fi.negocio;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ar.edu.unju.fi.negocio.modelo.ActividadReporte;
import ar.edu.unju.fi.negocio.modelo.Socio;
import ar.edu.unju.fi.negocio.modelo.SocioEstandar;

class ReportesTest {

        private Reportes reportes;
        private List<Socio> socios;

        @BeforeEach
        void setUp() {
                reportes = new Reportes();

                socios = new ArrayList<>();

                socios.add(new SocioEstandar(
                                "33333333", "Carlos", "Perez", 30000));

                socios.add(new SocioEstandar(
                                "22222222", "Ana", "Gomez", 20000));

                socios.add(new SocioEstandar(
                                "11111111", "Luis", "Alvarez", 25000));
        }

        @Test
        void ordenarSociosPorApellido() {

                List<Socio> resultado = reportes.ordenarSociosPorApellido(socios);

                assertEquals("Alvarez", resultado.get(0).getApellido());
                assertEquals("Gomez", resultado.get(1).getApellido());
                assertEquals("Perez", resultado.get(2).getApellido());
        }

        @Test
        void ordenarSociosPorApellidoYNombre() {

                socios.add(new SocioEstandar(
                                "44444444", "Beatriz", "Perez", 18000));

                List<Socio> resultado = reportes.ordenarSociosPorApellido(socios);

                assertEquals("Alvarez", resultado.get(0).getApellido());
                assertEquals("Gomez", resultado.get(1).getApellido());
                assertEquals("Beatriz", resultado.get(2).getNombre());
                assertEquals("Carlos", resultado.get(3).getNombre());
        }

        @Test
        void ordenarSociosNoModificaLaListaOriginal() {

                List<Socio> resultado = reportes.ordenarSociosPorApellido(socios);

                assertEquals("Perez", socios.get(0).getApellido());
                assertEquals("Alvarez", resultado.get(0).getApellido());
        }

        @Test
        void ordenarSociosPorCuota() {

                List<Socio> resultado = reportes.ordenarSociosPorCuota(socios);

                assertEquals(30000,
                                resultado.get(0).calcularCuotaMensual());

                assertEquals(25000,
                                resultado.get(1).calcularCuotaMensual());

                assertEquals(20000,
                                resultado.get(2).calcularCuotaMensual());
        }

        @Test
        void buscarSocioPorDni() {

                Socio encontrado = reportes.buscarSocioPorDni(socios, "22222222");

                assertNotNull(encontrado);
                assertEquals("Ana", encontrado.getNombre());
        }

        @Test
        void buscarSocioInexistente() {

                Socio encontrado = reportes.buscarSocioPorDni(socios, "99999999");

                assertNull(encontrado);
        }

        @Test
        void ordenarActividadesPorCantidadDeReservas() {

                List<ActividadReporte> actividades = new ArrayList<>();

                actividades.add(new ActividadReporte("TENIS", 5));
                actividades.add(new ActividadReporte("NATACION", 12));
                actividades.add(new ActividadReporte("YOGA", 8));

                actividades.sort(Reportes.POR_CANTIDAD_RESERVAS);

                assertEquals("NATACION",
                                actividades.get(0).getCodigoActividad());

                assertEquals("YOGA",
                                actividades.get(1).getCodigoActividad());

                assertEquals("TENIS",
                                actividades.get(2).getCodigoActividad());
        }
}