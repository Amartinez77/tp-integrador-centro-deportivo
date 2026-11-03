package ar.edu.unju.fi.presentacion;

import ar.edu.unju.fi.datos.AccesoDatos;
import ar.edu.unju.fi.datos.excepciones.PersistenciaException;
import ar.edu.unju.fi.negocio.Logica;

public class Inicio {

    public static void main(String[] args) {

        AccesoDatos accesoDatos = new AccesoDatos();
        Logica logica = new Logica(accesoDatos);

        try {
            logica.cargarSocios();

            System.out.println("Sistema iniciado correctamente.");

         // Instanciar el Menú Principal (Capa de Presentación) 
         
            MenuPrincipal menuPrincipal = new MenuPrincipal(logica);
            menuPrincipal.mostrarMenu();

        } catch (PersistenciaException e) {

            System.out.println(
                    "Error al cargar los socios: " + e.getMessage());
        }
    }
}