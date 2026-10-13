package ar.edu.unju.fi.datos.excepciones;

/**
 * Excepción personalizada para aislar fallos de Entrada/Salida (I/O)
 * o de serialización de la capa de datos
 */
public class PersistenciaException extends Exception {

    public PersistenciaException(String mensaje){

        super(mensaje);
    }

    public PersistenciaException(String mensaje, Throwable causa){
        super(mensaje, causa);
    }

}
