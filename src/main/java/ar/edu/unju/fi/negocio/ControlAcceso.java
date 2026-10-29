package ar.edu.unju.fi.negocio;

import ar.edu.unju.fi.negocio.excepciones.AccesoDenegadoException;
import ar.edu.unju.fi.negocio.modelo.Socio;

public class ControlAcceso {

    private final Logica logica;

    public ControlAcceso(Logica logica) {
        if (logica == null) {
            throw new IllegalArgumentException(
                    "La lógica del sistema no puede ser nula.");
        }

        this.logica = logica;
    }

    /**
     * Valida si un socio puede ingresar al centro deportivo.
     *
     * @param dni            DNI del socio que intenta ingresar.
     * @param cuotasAlDia    indica si tiene las cuotas al día.
     * @param sancionVigente indica si tiene una sanción vigente.
     * @return true si el acceso está permitido.
     * @throws AccesoDenegadoException si no cumple alguna condición.
     */
    public boolean validarAcceso(
            String dni,
            boolean cuotasAlDia,
            boolean sancionVigente)
            throws AccesoDenegadoException {

        if (dni == null || dni.isBlank()) {
            throw new AccesoDenegadoException(
                    "Debe ingresar un DNI válido.");
        }

        Socio socio = logica.buscarSocio(dni.trim());

        if (socio == null) {
            throw new AccesoDenegadoException(
                    "Acceso rechazado: no existe un socio con ese DNI.");
        }

        if (!socio.isActivo()) {
            throw new AccesoDenegadoException(
                    "Acceso rechazado: el socio está inactivo.");
        }

        if (!cuotasAlDia) {
            throw new AccesoDenegadoException(
                    "Acceso rechazado: el socio tiene cuotas vencidas.");
        }

        if (sancionVigente) {
            throw new AccesoDenegadoException(
                    "Acceso rechazado: el socio tiene una sanción vigente.");
        }

        return true;
    }
}