
package ar.edu.unju.fi.negocio;

import java.util.HashMap;
import java.util.Map;

/**
 * Módulo de facturación.
 * Mantiene el estado de las cuotas de cada socio.
 */
public class Facturacion {

    private final Map<String, Boolean> estadoCuotas;

    public Facturacion() {
        this.estadoCuotas = new HashMap<>();
    }

    /**
     * Registra el estado de las cuotas de un socio.
     */
    public void registrarEstadoCuotas(String dni, boolean alDia) {

        if (dni == null || dni.isBlank()) {
            throw new IllegalArgumentException(
                    "El DNI no puede ser nulo ni vacío.");
        }

        estadoCuotas.put(dni.trim(), alDia);
    }

    /**
     * Consulta si el socio tiene sus cuotas al día.
     */
    public boolean tieneCuotasAlDia(String dni) {

        if (dni == null || dni.isBlank()) {
            return false;
        }

        return Boolean.TRUE.equals(
                estadoCuotas.get(dni.trim()));
    }
}
