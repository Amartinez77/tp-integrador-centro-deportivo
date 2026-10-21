package ar.edu.unju.fi.negocio.excepciones;

public abstract class NegocioException extends Exception {

    public NegocioException(String mensaje) {
        super(mensaje);
    }

    public NegocioException(String mensaje, Throwable cause) {
        super(mensaje, cause);
    }

}
