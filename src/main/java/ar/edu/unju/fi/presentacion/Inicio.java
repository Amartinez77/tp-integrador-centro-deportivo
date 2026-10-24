package ar.edu.unju.fi.presentacion;

import ar.edu.unju.fi.datos.AccesoDatos;
import ar.edu.unju.fi.datos.excepciones.PersistenciaException;
import ar.edu.unju.fi.negocio.Logica;
import ar.edu.unju.fi.negocio.ControlAcceso;
import ar.edu.unju.fi.negocio.excepciones.AccesoDenegadoException;

public class Inicio {

    public static void main(String[] args) {

        AccesoDatos accesoDatos = new AccesoDatos();
        Logica logica = new Logica(accesoDatos);
        ControlAcceso molinete = new ControlAcceso(logica);

        try {
            logica.cargarSocios();

            System.out.println("Sistema iniciado correctamente.");

            // Ejemplo de prueba del molinete
            try {
                molinete.validarAcceso("40111222", true, false);
                System.out.println("Acceso permitido.");
            } catch (AccesoDenegadoException e) {
                System.out.println(e.getMessage());
            }

        } catch (PersistenciaException e) {
            System.out.println(
                    "Error al cargar los socios: " + e.getMessage());
        }
    }
}