package ar.edu.unju.fi.negocio.excepciones;

/**
 * Excepción de dominio lanzada cuando un socio intenta registrar
 * una reserva duplicada en la misma actividad y horario.
 */
public class ReservaDuplicadaException extends NegocioException{

    public ReservaDuplicadaException(String mensaje){
        super(mensaje);
    }

}
