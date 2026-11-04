package ar.edu.unju.fi.negocio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ar.edu.unju.fi.negocio.modelo.ActividadReporte;
import ar.edu.unju.fi.negocio.modelo.Reserva;
import ar.edu.unju.fi.negocio.modelo.Socio;

public class Reportes {

    // Ordenar socios por cuota mensual, de mayor a menor.
    public static final Comparator<Socio> POR_CUOTA = Comparator.comparingDouble(Socio::calcularCuotaMensual)
            .reversed();

    // Ordenar actividades por cantidad de reservas, de mayor a menor.
    public static final Comparator<ActividadReporte> POR_CANTIDAD_RESERVAS = Comparator
            .comparingInt(ActividadReporte::getCantidadReservas)
            .reversed()
            .thenComparing(ActividadReporte::getCodigoActividad);

    // Reporte de socios ordenados por apellido y nombre.
    public List<Socio> ordenarSociosPorApellido(List<Socio> socios) {

        List<Socio> resultado = new ArrayList<>(socios);

        Collections.sort(resultado);

        return resultado;
    }

    // Reporte de socios ordenados por cuota mensual.
    public List<Socio> ordenarSociosPorCuota(List<Socio> socios) {

        List<Socio> resultado = new ArrayList<>(socios);

        Collections.sort(resultado, POR_CUOTA);

        return resultado;
    }

    // Reporte de actividades ordenadas por cantidad de reservas.
    public List<ActividadReporte> ordenarActividades(
            Map<String, Set<Reserva>> reservasPorActividad) {

        List<ActividadReporte> resultado = new ArrayList<>();

        for (Map.Entry<String, Set<Reserva>> entrada : reservasPorActividad.entrySet()) {

            ActividadReporte actividad = new ActividadReporte(
                    entrada.getKey(),
                    entrada.getValue().size());

            resultado.add(actividad);
        }

        Collections.sort(resultado, POR_CANTIDAD_RESERVAS);

        return resultado;
    }

    // Búsqueda binaria de un socio por DNI.
    public Socio buscarSocioPorDni(List<Socio> socios, String dni) {

        if (socios == null || dni == null || dni.isBlank()) {
            return null;
        }

        // Primero ordenamos los socios por DNI.
        List<Socio> sociosOrdenados = new ArrayList<>(socios);

        sociosOrdenados.sort(Comparator.comparing(Socio::getDni));

        // Creamos una lista de DNI con el mismo orden.
        List<String> documentos = new ArrayList<>();

        for (Socio socio : sociosOrdenados) {
            documentos.add(socio.getDni());
        }

        // Búsqueda binaria.
        int posicion = Collections.binarySearch(documentos, dni.trim());

        if (posicion >= 0) {
            return sociosOrdenados.get(posicion);
        }

        return null;
    }
}