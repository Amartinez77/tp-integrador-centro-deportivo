package ar.edu.unju.fi.negocio.servicios;

import ar.edu.unju.fi.negocio.modelo.ActividadReporte;
import ar.edu.unju.fi.negocio.modelo.Reserva;
import ar.edu.unju.fi.negocio.modelo.Socio;
import ar.edu.unju.fi.negocio.modelo.SocioEstandar;

import java.util.*;

/**
 * Servicio de dominio especializado en la generación de reportes estadísticos,
 * ordenamientos avanzados y algoritmos de búsqueda binaria (SRP).
 */
public class ReporteService {

    public static final Comparator<Socio> POR_DNI = Comparator.comparing(Socio::getDni);

    public static final Comparator<Socio> POR_CUOTA = Comparator
            .comparingDouble(Socio::calcularCuotaMensual)
            .reversed();

    public static final Comparator<ActividadReporte> POR_CANTIDAD_RESERVAS = Comparator
            .comparingInt(ActividadReporte::getCantidadReservas)
            .reversed()
            .thenComparing(ActividadReporte::getCodigoActividad);

    public List<Socio> ordenarSociosPorApellido(List<Socio> socios) {
        if (socios ==  null || socios.isEmpty()) {
            return Collections.emptyList();
        }
        List<Socio> resultado = new ArrayList<>(socios);
        Collections.sort(resultado);
        return Collections.unmodifiableList(resultado);
    }

    public List<ActividadReporte> ordenarActividades(Map<String, Set<Reserva>> reservasPorActividad) {
        if (reservasPorActividad == null || reservasPorActividad.isEmpty()) {
            return Collections.emptyList();
        }

        List<ActividadReporte> resultado = new ArrayList<>();
        for (Map.Entry<String, Set<Reserva>> entrada : reservasPorActividad.entrySet()) {
            resultado.add(new ActividadReporte(entrada.getKey(), entrada.getValue().size()));
        }

        resultado.sort(POR_CANTIDAD_RESERVAS);
        return Collections.unmodifiableList(resultado);
    }

    public Socio buscarSocioPorDni(List<Socio> socios, String dni) {
        if (socios == null || socios.isEmpty() || dni == null || dni.isBlank()) {
            return null;
        }

        String documentoBuscado = dni.trim();

        List<Socio> sociosOrdenados = new ArrayList<>(socios);
        sociosOrdenados.sort(POR_DNI);

        Socio claveBusqueda = new SocioEstandar(documentoBuscado, "", "", 0.0);
        int posicion = Collections.binarySearch(sociosOrdenados, claveBusqueda, POR_DNI);

        return (posicion >= 0) ? sociosOrdenados.get(posicion) : null;
    }

    /**
     * Reporte de socios ordenados por cuota mensual de mayor a menor (Comparator).
     */
    public List<Socio> ordenarSociosPorCuota(List<Socio> socios) {
        if (socios == null || socios.isEmpty()) {
            return Collections.emptyList();
        }
        List<Socio> resultado = new ArrayList<>(socios);
        resultado.sort(POR_CUOTA);
        return Collections.unmodifiableList(resultado);
    }

}
