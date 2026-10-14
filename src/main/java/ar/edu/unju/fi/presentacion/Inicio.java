package ar.edu.unju.fi.presentacion;

import ar.edu.unju.fi.datos.AccesoDatos;
import ar.edu.unju.fi.negocio.Logica;
import ar.edu.unju.fi.datos.excepciones.PersistenciaException;

public class Inicio {

    public static void main(String[] args) {
        
        // 1. Instanciamos la capa de datos
        AccesoDatos accesoDatos = new AccesoDatos();

        // 2. Inyectamos datos en la lógica por constructor
        Logica logica = new Logica(accesoDatos);

        try {
            // 3. Probamos cargar los socios desde el archivo
            logica.cargarSocios();
            System.out.println("Sistema iniciado correctamente.");

        } catch (PersistenciaException e) {
            System.out.println("Error al cargar los socios: " + e.getMessage());
        }
    }
}
