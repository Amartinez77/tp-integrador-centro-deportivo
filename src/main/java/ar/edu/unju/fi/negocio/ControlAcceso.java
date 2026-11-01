
package ar.edu.unju.fi.negocio;

import ar.edu.unju.fi.negocio.excepciones.AccesoDenegadoException;
import ar.edu.unju.fi.negocio.modelo.Socio;

/**
 * Controla el ingreso al centro deportivo.
 * Consulta el estado del socio mediante la fachada Logica.
 */
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
     * Valida el acceso de un socio.
     *
     * @param dni DNI del socio que intenta ingresar.
     * @return true si el acceso está permitido.
     * @throws AccesoDenegadoException si el socio no está habilitado.
     */
    public boolean validarAcceso(String dni)
            throws AccesoDenegadoException {

        if (dni == null || dni.isBlank()) {
            throw new AccesoDenegadoException(
                    "Debe ingresar un DNI válido.");
        }

        String documento = dni.trim();

        // Consultar si el socio existe.
        Socio socio = logica.buscarSocio(documento);

        if (socio == null) {
            throw new AccesoDenegadoException(
                    "Acceso rechazado: no existe un socio con ese DNI.");
        }

        // Consultar si el socio está activo.
        if (!socio.isActivo()) {
            throw new AccesoDenegadoException(
                    "Acceso rechazado: el socio está inactivo.");
        }

        // Consultar el estado de las cuotas mediante la fachada.
        if (!logica.tieneCuotasAlDia(documento)) {
            throw new AccesoDenegadoException(
                    "Acceso rechazado: el socio tiene cuotas vencidas o no hay información de pagos.");
        }

        return true;
    }
}
